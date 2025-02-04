package edu.ucdenver.ccp.nlp.uima.annotators.converter;

/*
 * #%L
 * Colorado Computational Pharmacology's nlp module
 * %%
 * Copyright (C) 2012 - 2014 Regents of the University of Colorado
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Regents of the University of Colorado nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;

import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.mention.CCPClassMention;
import edu.ucdenver.ccp.nlp.uima.util.UIMA_Util;

/**
 * Converts the class mentions of the user supplied types to the target mention type.
 * 
 * This is simple utility Analysis Engine that enables the user to change the annotation type (class
 * mention name) of CCPTextAnnotations stored in the CAS. For each CCPTextAnnotation in the CAS, the
 * user-specified annotation type is assigned if the current annotation type is in the
 * mention-types-to-convert list.
 * 
 * This AE can be used in one of several different ways:
 * - User provides a list of class-name strings for the MentionTypesToConvert and a single 
     ToMentionType string. 
 *   EX: MentionTypesToConvert = [biological_process, molecular_function], 
 *       ToMentionType = GO_term 
 * - User provides a list of class-name regular expressions for the MentionTypesToConvert
 *   and a single ToMentionType string. 
 *   EX: MentionTypesToConvert = [GO:.*] 
 *       ToMentionType = GO_term 
 * - User provides a list of class-name regular expressions that includes grouping-paren-pair(s), 
 *   and a single ToMentionType string that includes the $1, $2, etc. syntax of calling 
 *   regular-expression-matched groups. 
 *   EX: MentionTypesToConvert = [GO:(.*)] 
 *       ToMentionType = GO_ID:$1
 * 
 * If the AE detects the $1 syntax in the ToMentionType, it checks to make sure the user has
 * provided sufficient grouping-paren-pairs in the MentionTypesToConvert param.
 * 
 * !! NOTE THAT IN THE INCREDIBLY UNLIKELY EVENT THAT YOU WANT A CLASSNAME CONVERTED TO A STRING
 * THAT INCLUDES AN ACTUAL "$" SYMBOL IN IT, THIS AE WILL BREAK. !!
 * 
 * @author William A Baumgartner Jr
 * @author Helen L Johnson
 * 
 */
public class ClassMentionConverter_AE extends JCasAnnotator_ImplBase {

	private static Logger logger = Logger.getLogger(ClassMentionConverter_AE.class);

	public static final String PARAM_TARGET_MENTION = "ToMentionType";
	public static final String PARAM_MENTION_TYPES_TO_CONVERT = "MentionTypesToConvertRegExes";

	public static final String PARAM_ANNOTATION_SET_IDS_TO_IGNORE = "annotationSetIdsToIgnore";
	@ConfigurationParameter()
	private int[] annotationSetIdsToIgnore;

	private String targetMentionType = null;
	private Set<String> mentionTypesToConvert;
	private Set<Pattern> mentionTypePatterns;

	private boolean handleGroupVars = false;

	@Override
	public void initialize(UimaContext uc) throws ResourceInitializationException {
		super.initialize(uc);
		int groupVarsCount = 0;
		int groupParensCount = 0;

		/* read in input parameters */
		targetMentionType = (String) uc.getConfigParameterValue(PARAM_TARGET_MENTION);

		mentionTypesToConvert = new HashSet<String>(Arrays.asList((String[]) uc
				.getConfigParameterValue(PARAM_MENTION_TYPES_TO_CONVERT)));

		// check syntax of user=provided parameters; if grouping syntax has been provided in
		// either the TargetMention or the MentionTypesToConvert, make sure there is
		// parallel syntax in the other var.
		if (!checkParameterSyntax(targetMentionType, mentionTypesToConvert)) {
			// ResourceInitializationException is all that can get thrown
			// out of this method. It has a somewhat clumsy constructor
			// that expects an exception. In this case there isn't one,
			// so I create it: squarePeg.
			Exception squarePeg = new Exception("The number of regular expression elements in the targetMentionType"
					+ " parameter (like $1) do NOT match the number of regular expression elements"
					+ " (like parens) in at least one of the MentionTypesToConvert param." + "\nTargetMentionType: "
					+ targetMentionType + "\nMentionTypesToConvert: " + mentionTypesToConvert.toString());
			throw new ResourceInitializationException(squarePeg);
		}

		/* convert all types to lowercase */
		// not sure if this is necessary or not with the addition of regex capability????
		for (String type : mentionTypesToConvert) {
			type = type.toLowerCase();
		}

		mentionTypePatterns = new HashSet<Pattern>();
		for (String mentionType : mentionTypesToConvert) {
			mentionTypePatterns.add(Pattern.compile(mentionType));
		}

		logger.info("Initialized ClassMentionConverter: " + mentionTypesToConvert + " --> " + targetMentionType);

	}

	/**
	 * cycle through all annotations and change the mention if appropriate
	 */
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		FSIterator<Annotation> annotIter = jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type)
				.iterator();
		while (annotIter.hasNext()) {
			CCPTextAnnotation ccpTA = (CCPTextAnnotation) annotIter.next();
			if (!ignoreAnnotation(ccpTA)) {
				for (Pattern p : mentionTypePatterns) {

					CCPClassMention cm = ccpTA.getClassMention();
					if (cm == null) {
						throw new AnalysisEngineProcessException(new RuntimeException(
								"no class mention on annotation for span:" + ccpTA.getCoveredText()));
					}
					String name = cm.getMentionName();
					if (name == null) {
						throw new AnalysisEngineProcessException(new RuntimeException(
								"null class mention name on annotation for span:" + ccpTA.getCoveredText()));
					}
					Matcher m = p.matcher(ccpTA.getClassMention().getMentionName());
					if (m.matches()) {
						int groupCount = m.groupCount();
						// logger.debug("groupCount: " + groupCount);
						if (groupCount == 0) {
							// no capturing groups, only the full pattern matched.
							// TODO: VERIFY there are no $1 dohickeys in the targetMentionType
							// logger.debug("GroupCount is 0. No capturing groups found.");
							ccpTA.getClassMention().setMentionName(targetMentionType);
						} else {
							// one or more capturing groups found (1 is most likely scenario)
							// logger.debug("GroupCount is greater than 0.");
							String newTargetMentionType = targetMentionType;
							for (int i = 1; i <= m.groupCount(); i++) {
								String s = "$" + i;
								newTargetMentionType = newTargetMentionType.replace(s, m.group(i));
								// logger.debug("NewTargetMentionType: <" + newTargetMentionType +
								// ">");
							}
							// logger.debug("Replacing old targetMentionType <" + targetMentionType
							// + "> with newTargetMentiontype <" + newTargetMentionType + ">");
							ccpTA.getClassMention().setMentionName(newTargetMentionType);
						}
					}
				}
			}
		}
	}

	/**
	 * @param ccpTA
	 * @return true if the annotation is a member of one of the annotation sets to ignore
	 */
	private boolean ignoreAnnotation(CCPTextAnnotation ccpTa) {
		if (annotationSetIdsToIgnore != null && annotationSetIdsToIgnore.length > 0) {
			for (int setIdToIgnore : annotationSetIdsToIgnore) {
				if (UIMA_Util.hasAnnotationSet(ccpTa, setIdToIgnore)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param String
	 *            string1
	 * @param Set
	 *            <String> strings2
	 * @return
	 */
	private Boolean checkParameterSyntax(String string1, Set<String> strings2) {
		boolean goodSyntax = true;

		// how many $1, $2, etc. does the string1 have?
		int dollarCount = 0;
		Pattern p = Pattern.compile("\\$[0-9]");
		Matcher m = p.matcher(string1);
		while (m.find()) {
			dollarCount += 1;
		}

		// how many paren pairs do each of the elements in the set have?
		Pattern p2 = Pattern.compile("\\(.*?\\)");
		for (String s : strings2) {
			Matcher m2 = p2.matcher(s);
			int parenPairCount = 0;
			while (m2.find()) {
				parenPairCount += 1;
			}
			if (dollarCount != parenPairCount) {
				goodSyntax = false;
				logger.debug("Found a bad param syntax. \nParam1 <" + string1 + "> has <" + dollarCount + "> elements."
						+ "\nParam2 <" + s + "> has <" + parenPairCount + "> elements.");
				break;
			}
		}

		// TODO Auto-generated method stub
		return goodSyntax;
	}

	public static AnalysisEngine createAnalysisEngine(TypeSystemDescription tsd, 
		String targetMention, String[] convertTypes) 
	throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitive(createAnalysisEngineDescription(tsd, targetMention, convertTypes));
	}

	public static AnalysisEngineDescription createAnalysisEngineDescription(TypeSystemDescription tsd,
		String targetMention, String[] convertTypes) 
    throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(ClassMentionConverter_AE.class, tsd,
				PARAM_TARGET_MENTION, targetMention, 
				PARAM_MENTION_TYPES_TO_CONVERT, convertTypes);
	}

	public static AnalysisEngineDescription createAnalysisEngineDescription(TypeSystemDescription tsd,
		String targetMention, String[] convertTypes, int[] annotationSetIdsToIgnore)
	throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(ClassMentionConverter_AE.class, tsd,
				PARAM_TARGET_MENTION, targetMention, 
				PARAM_MENTION_TYPES_TO_CONVERT, convertTypes,
				PARAM_ANNOTATION_SET_IDS_TO_IGNORE, annotationSetIdsToIgnore);
	}
}

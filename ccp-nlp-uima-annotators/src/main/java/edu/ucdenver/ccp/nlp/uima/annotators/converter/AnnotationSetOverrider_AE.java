/*
 * AnnotationSetOverrider_AE.java
 * Copyright (C) 2007 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 */

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

import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ConfigurationParameterFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;

import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPAnnotationSet;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;

/**
 * A simple utility Analysis Engine that enables the user to change the annotation set assigned to
 * <code>CCPTextAnnotations</code> stored in the CAS. For each <code>CCPTextAnnotation</code> in the
 * CAS, the user-specified annotation set is assigned, unless the annotation is associated with an
 * annotation set in the annotation-sets-to-ignore list, in which case nothing is changed. This
 * utility is particularly useful when using the Annotation Comparison machinery.
 * 
 * @author William A Baumgartner, Jr.
 * 
 */
public class AnnotationSetOverrider_AE extends JCasAnnotator_ImplBase {

	public static final String PARAM_SETID = "setID";
	@ConfigurationParameter(mandatory = true)
	private int setID = -1;

	public static final String PARAM_SETNAME = "setName";
	@ConfigurationParameter()
	private String setName = null;

	public static final String PARAM_SETDESCRIPTION = "setDescription";
	@ConfigurationParameter()
	private String setDescription = null;

	public static final String PARAM_ANNOTATION_SETS_TO_IGNORE = "annotationSetsToIgnore";
	@ConfigurationParameter()
	private Set<Integer> annotationSetsToIgnore;

	/**
	 * Constant that can be used as the identifier for the gold standard annotation set during
	 * annotation comparisons
	 */
	public static final int GOLD_ANNOTATION_SET_ID = 99099099;

	/**
	 * Constant that can be used as the identifier for the evaluation set (test set) annotation set
	 * during annotation comparisons
	 */
	public static final int EVAL_ANNOTATION_SET_ID = 11011011;

	/**
	 * cycle through all annotations and set the annotation set
	 */
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		/* create an annotation set list */
		CCPAnnotationSet ccpAnnotationSet = new CCPAnnotationSet(jcas);
		ccpAnnotationSet.setAnnotationSetID(setID);
		ccpAnnotationSet.setAnnotationSetName(setName);
		ccpAnnotationSet.setAnnotationSetDescription(setDescription);
		FSArray annotationSets = new FSArray(jcas, 1);
		annotationSets.set(0, ccpAnnotationSet);

		FSIterator annotIter = jcas.getJFSIndexRepository().getAnnotationIndex(CCPTextAnnotation.type).iterator();

		while (annotIter.hasNext()) {
			Object possibleAnnot = annotIter.next();
			if (possibleAnnot instanceof CCPTextAnnotation) {
				CCPTextAnnotation ccpTA = (CCPTextAnnotation) possibleAnnot;
				boolean ignore = checkForIgnoreBasedOnAnnotationSet(ccpTA);

				if (!ignore) {
					ccpTA.setAnnotationSets(annotationSets);
				}
			} else {
				System.err.println("WARNING -- AnnotationSetValidator_AE: CCPTextAnnotation expected but instead got "
						+ possibleAnnot.getClass().getName());
			}
		}

	}

	protected boolean checkForIgnoreBasedOnAnnotationSet(CCPTextAnnotation ccpTA) {
		boolean ignore = false;
		FSArray annotationSets = ccpTA.getAnnotationSets();
		if (annotationSets != null & annotationSetsToIgnore != null) {
			for (int i = 0; i < annotationSets.size(); i++) {
				CCPAnnotationSet aSet = (CCPAnnotationSet) annotationSets.get(i);
				if (annotationSetsToIgnore.contains(aSet.getAnnotationSetID())) {
					ignore = true;
					break;
				}
			}
		}
		return ignore;
	}

	public static AnalysisEngineDescription createAnalysisEngineDescription(TypeSystemDescription tsd, int setId,
			String setName, String setDescription, int[] setsToIgnore) throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(AnnotationSetOverrider_AE.class, tsd, PARAM_SETID,
				setId, PARAM_SETNAME, setName, PARAM_SETDESCRIPTION, setDescription, PARAM_ANNOTATION_SETS_TO_IGNORE,
				setsToIgnore);
	}

	/**
	 * Returns the description for an {@link AnalysisEngine} that will override the annotation set
	 * for all annotations in the CAS and assign each annotation to the gold standard annotation
	 * set. The GOLD annotation set is meant to define the gold standard set of annotations to use
	 * during an annotation comparison pipeline.
	 * 
	 * @param tsd
	 * @param setsToIgnore
	 *            annotations assigned to sets with IDs in this array will not be assigned to the
	 *            gold set
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static AnalysisEngineDescription createGoldSetOverriderDescription(TypeSystemDescription tsd,
			int[] setsToIgnore) throws ResourceInitializationException {
		String goldSetName = "gold set";
		String goldSetDescription = "This annotation set defines the annotations that are members of the gold standard set.";
		return AnalysisEngineFactory.createPrimitiveDescription(AnnotationSetOverrider_AE.class, tsd, PARAM_SETID,
				GOLD_ANNOTATION_SET_ID, PARAM_SETNAME, goldSetName, PARAM_SETDESCRIPTION, goldSetDescription,
				PARAM_ANNOTATION_SETS_TO_IGNORE, setsToIgnore);
	}

	public static AnalysisEngineDescription createGoldSetOverriderDescription(TypeSystemDescription tsd)
			throws ResourceInitializationException {
		return createGoldSetOverriderDescription(tsd, new int[0]);
	}

	/**
	 * Returns the description for an {@link AnalysisEngine} that will override the annotation set
	 * for all annotations in the CAS and add each annotation to the EVAL annotation set. The EVAL
	 * annotation set defines the set of annotations to be evaluated in an annotation comparison
	 * pipeline. Annotation sets identified by the GOLD_ANNOTATION_SET_ID are not overriden by this
	 * AE.
	 * 
	 * @param tsd
	 * @return
	 */
	public static AnalysisEngineDescription createEvalSetOverriderDescription(TypeSystemDescription tsd)
			throws ResourceInitializationException {
		String evalSetName = "eval set";
		String evalSetDescription = "This annotation set defines the annotations that are members of the evaluation set that will be tested against the gold standard.";
		return AnalysisEngineFactory.createPrimitiveDescription(AnnotationSetOverrider_AE.class, tsd, PARAM_SETID,
				EVAL_ANNOTATION_SET_ID, PARAM_SETNAME, evalSetName, PARAM_SETDESCRIPTION, evalSetDescription,
				PARAM_ANNOTATION_SETS_TO_IGNORE, new int[] { GOLD_ANNOTATION_SET_ID });
	}

}

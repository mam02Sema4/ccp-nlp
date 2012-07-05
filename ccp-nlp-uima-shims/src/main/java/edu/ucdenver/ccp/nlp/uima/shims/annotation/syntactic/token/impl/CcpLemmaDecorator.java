/**
 * 
 */
package edu.ucdenver.ccp.nlp.uima.shims.annotation.syntactic.token.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import edu.ucdenver.ccp.nlp.core.mention.ClassMentionType;
import edu.ucdenver.ccp.nlp.core.mention.SlotMentionType;
import edu.ucdenver.ccp.nlp.core.mention.StringSlotMention;
import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.annotation.impl.WrappedCCPTextAnnotation;
import edu.ucdenver.ccp.nlp.core.uima.util.UIMA_Annotation_Util;
import edu.ucdenver.ccp.uima.shims.annotation.AnnotationDataExtractor;
import edu.ucdenver.ccp.uima.shims.annotation.Span;
import edu.ucdenver.ccp.uima.shims.annotation.syntactic.token.Lemma;
import edu.ucdenver.ccp.uima.shims.annotation.syntactic.token.LemmaDecorator;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class CcpLemmaDecorator implements LemmaDecorator {

	/**
	 * @return an initialized {@link CCPTextAnnotation} with a mention name of "token"
	 * @see edu.ucdenver.ccp.uima.shims.annotation.AnnotationDecorator#newAnnotation(org.apache.uima.jcas.JCas,
	 *      java.lang.String, edu.ucdenver.ccp.uima.shims.annotation.Span)
	 */
	@Override
	public Annotation newAnnotation(JCas jcas, @SuppressWarnings("unused") String type, Span span) {
		return UIMA_Annotation_Util.createCCPTextAnnotation(ClassMentionType.TOKEN.typeName(), span.getSpanStart(),
				span.getSpanEnd(), jcas);
	}

	/**
	 * Inserts information representing the input {@link Lemma} into the input {@link Annotation}
	 * which is assumed to be of type {@link CCPTextAnnotation} in this instance.
	 * 
	 * @throws IllegalArgumentException
	 *             if the input {@link Annotation} is not a {@link CCPTextAnnotation}
	 * 
	 * @see edu.ucdenver.ccp.uima.shims.annotation.syntactic.token.LemmaDecorator#insertLemma(org.apache.uima.jcas.tcas.Annotation,
	 *      edu.ucdenver.ccp.uima.shims.annotation.syntactic.token.Lemma)
	 */
	@Override
	public void insertLemma(Annotation annotation, Lemma lemma) {
		checkAnnotationType(annotation);
		WrappedCCPTextAnnotation wrappedCcpTa = new WrappedCCPTextAnnotation((CCPTextAnnotation) annotation);
		StringSlotMention lemmaSlot = (StringSlotMention) wrappedCcpTa.getClassMention().getPrimitiveSlotMentionByName(
				SlotMentionType.TOKEN_LEMMA.typeName());
		lemmaSlot.addSlotValue(lemma.serializeToString());
	}

	/**
	 * @see edu.ucdenver.ccp.uima.shims.annotation.syntactic.token.LemmaDecorator#extractLemmas(org.apache
	 *      .uima.jcas.tcas.Annotation)
	 */
	@Override
	public List<Lemma> extractLemmas(Annotation annotation) {
		checkAnnotationType(annotation);
		List<Lemma> lemmasToReturn = new ArrayList<Lemma>();
		WrappedCCPTextAnnotation wrappedCcpTa = new WrappedCCPTextAnnotation((CCPTextAnnotation) annotation);
		StringSlotMention lemmaSlot = (StringSlotMention) wrappedCcpTa.getClassMention().getPrimitiveSlotMentionByName(
				SlotMentionType.TOKEN_LEMMA.typeName());
		for (String lemmaStr : lemmaSlot.getSlotValues()) {
			lemmasToReturn.add(Lemma.deserializeFromString(lemmaStr));
		}
		return lemmasToReturn;
	}

	/**
	 * In the case of the {@link CcpLemmaDecorator}, the annotation to decorate is simple the input
	 * token annotation. A slot for the lemma will be added to the token annotation.
	 * 
	 * @param tokenAnnotation
	 *            in this case, the input annotation represents the token annotation whose covered
	 *            text was lemmatized
	 * 
	 * @see edu.ucdenver.ccp.uima.shims.annotation.AnnotationDecorator#getAnnotationToDecorate(org.apache.uima.jcas.tcas.Annotation,
	 *      edu.ucdenver.ccp.uima.shims.annotation.AnnotationDataExtractor)
	 */
	@Override
	public Annotation getAnnotationToDecorate(Annotation tokenAnnotation,
			@SuppressWarnings("unused") AnnotationDataExtractor annotationDataExtractor) {
		return tokenAnnotation;
	}

	/**
	 * 
	 * @see edu.ucdenver.ccp.uima.shims.annotation.AnnotationDecorator#decorateAnnotation(org.apache
	 *      .uima.jcas.tcas.Annotation, java.lang.String, java.lang.Object)
	 */
	@Override
	public void decorateAnnotation(Annotation annotation, @SuppressWarnings("unused") String attributeType, Lemma lemma) {
		insertLemma(annotation, lemma);
	}

	/**
	 * Checks that the input {@link Annotation} is a {@link CCPTextAnnotation}
	 * 
	 * @param annotation
	 * @throws IllegalArgumentException
	 *             if the input {@link Annotation} is not a {@link CCPTextAnnotation}
	 * 
	 */
	private static void checkAnnotationType(Annotation annotation) {
		if (!(annotation instanceof CCPTextAnnotation)) {
			throw new IllegalArgumentException(
					"Expecting LemmaAnnotation class. Unable to assign lemma information to annotation of type: "
							+ annotation.getClass().getName());
		}
	}

	/**
	 * @see edu.ucdenver.ccp.uima.shims.annotation.AnnotationDecorator#extractAttribute(org.apache.uima
	 *      .jcas.tcas.Annotation, java.lang.String)
	 */
	@Override
	public List<Lemma> extractAttribute(Annotation annotation, @SuppressWarnings("unused") String attributeType) {
		return extractLemmas(annotation);
	}

}

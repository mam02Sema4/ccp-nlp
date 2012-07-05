/* Copyright (C) 2007-2010 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This file is part of the CCP NLP library.
 * The CCP NLP library is free software: you can redistribute it and/or
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
 */

package edu.ucdenver.ccp.nlp.core.annotation.comparison;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.ucdenver.ccp.nlp.core.annotation.AnnotationSet;
import edu.ucdenver.ccp.nlp.core.annotation.Annotator;
import edu.ucdenver.ccp.nlp.core.annotation.TextAnnotation;
import edu.ucdenver.ccp.nlp.core.annotation.impl.DefaultTextAnnotation;
import edu.ucdenver.ccp.nlp.core.mention.ClassMentionType;
import edu.ucdenver.ccp.nlp.core.mention.comparison.IdenticalMentionComparator;
import edu.ucdenver.ccp.nlp.core.mention.comparison.MentionComparator;
import edu.ucdenver.ccp.nlp.core.mention.impl.DefaultClassMention;

/**
 * TODO: Add tests on annotations at different depths. This is not crucial as the mention comparators have been tested
 * at different depths, but it would be good redundancy to have.
 * 
 * @author williamb
 * 
 */
public class AnnotationComparatorTest {
	List<TextAnnotation> goldAnnotations;

	TextAnnotation goldAnnotation;

	Annotator annotator;

	AnnotationSet annotationSet;

	int documentCollectionID;

	int documentSectionID;

	String testText1;

	String testText2;

	@Before
	public void setUp() throws Exception {

		annotator = new Annotator(new Integer(-1), "Test", "Annotator", "UCHSC");
		annotationSet = new AnnotationSet(new Integer(-1), "Test Annotation Set", "This is a test annotation set.");
		documentCollectionID = -1;
		documentSectionID = -1;

		testText1 = "Androgen action in prostate and prostate cancer cells is dependent upon the androgen receptor (AR) protein that transcriptionally regulates the expression of androgen-dependent genes in the presence of a steroid ligand. Whereas the overall schema of androgen action mediated by this receptor protein appears to be relatively simple, androgen signaling is now known to be influenced by several other cell signal transduction pathways and here we review the evidence that the canonical Wnt signaling pathway also modulates androgen signaling at multiple levels. Wnt is a complex signaling pathway whose endpoint involves activation of transcription from LEF-1/TCF transcription factors and it is known to be involved in the development and progression of numerous human epithelial tumors including prostate cancer. beta-catenin protein, a particularly critical molecular component of canonical Wnt signaling is now known to promote androgen signaling through its ability to bind to the AR protein in a ligand-dependent fashion and to enhance the ability of liganded AR to activate transcription of androgen-regulated genes. Under certain conditions, glycogen synthase kinase-3beta (GSK-3beta), a protein serine/threonine kinase that regulates beta-catenin degradation within the Wnt signaling pathway, can also phosphorylate AR and suppress its ability to activate transcription. Finally, it was recently found that the human AR gene itself is a target of LEF-1/TCF-mediated transcription and that AR mRNA is highly upregulated by activation of Wnt signaling in prostate cancer cells. Paradoxically, Wnt activation also appears to stimulate Akt activity promoting an MDM-2-mediated degradation process that reduces AR protein levels in Wnt-stimulated prostate cancer cells. Collectively, this information indicates that the multifaceted nature of the interaction between the Wnt and the androgen signaling pathways likely has numerous consequences for the development, growth, and progression of prostate cancer.";
		testText2 = "Obesity has been recognized as a risk factor for breast cancer. Adipocyte-derived leptin may play as a paracrine regulator on the growth of breast cancer cells. Expression of both leptin and its OB-Rb receptor was detected in human breast cancer ZR-75-1 cells and further induced by leptin, suggesting that both expression and message mediation of leptin were autoregulated by itself. With cell counting and MTT assay, we had observed leptin stimulated ZR-75-1 growth in dose- and time-dependent manners. To study what steps of cell cycle progression leptin may involve in, we analyzed cell-cycle profile with flow cytometric analysis, mRNA and protein expressions of four cell-cycle regulators with RT-PCR and Western blotting analysis. Under the treatment of leptin, the G1 arrest of cells was reduced accompanied with up-regulation of G1 phase-specific cyclin D1 and proto-oncogene c-Myc, but down-regulation of cyclin-dependent kinase inhibitor p21(WAF1/CIP1) and tumor suppressor p53. Furthermore, JAK2 inhibitor AG490, PI3K/Akt inhibitor Wortmannin, and MEK/ERK1/2 inhibitor PD98059 were efficiently prevented leptin-promoted cell growth. Effect of cooperation between leptin and estrogen on ZR-75-1 growth had been observed. Collectively, the results showed that the proliferative effect of leptin on ZR-75-1 was associated with the up-regulation of cyclin D1 and c-Myc and down-regulation of tumor suppressor p53 and p21(WAF1/CIP1) plausibly through a hypothesized JAK2-PI3K/Akt-MEK/ERK pathway. The leptin- and OB-Rb-expressing capability of ZR-75-1 created a possible autocrine control of leptin, in which signal could be effectively amplified by itself, on cell growth.";

		goldAnnotation = createProteinAnnotation(1, 0, 8, "1", testText1);

		createGoldSetAnnotations();

	}

	@After
	public void tearDown() throws Exception {
		goldAnnotations = null;
	}

	/**
	 * Test the default compare() method on individual annotations. The default method utilizes the StrictSpanComparator
	 * and the IdenticalMentionComparator classes.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDefaultCompare() throws Exception {
		AnnotationComparator ac = new AnnotationComparator();

		String documentID = "1";
		String documentText = testText1;
		int annotationID = 0;

		/* compare against an identical annotation */
		DefaultTextAnnotation identicalAnnotation = createProteinAnnotation(annotationID++, 0, 8, documentID, documentText);
		assertEquals(0, ac.compare(goldAnnotation, identicalAnnotation));

		/* compare against an overlapping annotation */
		DefaultTextAnnotation overlappingAnnotation = createProteinAnnotation(annotationID++, 0, 12, documentID, documentText);
		assertFalse( ac.compare(goldAnnotation, overlappingAnnotation)==0);

		/* compare against an annotation that matches the span end boundary */
		DefaultTextAnnotation matchRightAnnotation = createProteinAnnotation(annotationID++, 4, 8, documentID, documentText);
		assertFalse( ac.compare(goldAnnotation, matchRightAnnotation)==0);

		/* compare against an annotation that matches the span begin boundary */
		DefaultTextAnnotation matchLeftAnnotation = createProteinAnnotation(annotationID++, 0, 15, documentID, documentText);
		assertFalse( ac.compare(goldAnnotation, matchLeftAnnotation)==0);
	}

	/**
	 * Test the default compare() method on annotation lists. The default method utilizes the StrictSpanComparator and
	 * the IdenticalMentionComparator classes.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDefaultCompareLists() throws Exception {
		AnnotationComparator ac = new AnnotationComparator();

		/* load a test set that is identical to the gold standard set */
		List<TextAnnotation> testAnnotations = createPerfectTestSet();
		PRFResult prf = ac.compare(goldAnnotations, testAnnotations);
		assertEquals(1.0, prf.getPrecision(), 0.0);
		assertEquals(1.0, prf.getRecall(), 0.0);
		assertEquals(1.0, prf.getFmeasure(), 0.0);

		/* test for match with no test annotations */
		testAnnotations = new ArrayList<TextAnnotation>();
		prf = ac.compare(goldAnnotations, testAnnotations);
		assertTrue(Double.isNaN(prf.getPrecision()));
		assertEquals(0.0, prf.getRecall(), 0.0);
		assertTrue(Double.isNaN(prf.getFmeasure()));

		/* test when missing half of the annotations, the other half are strict matches */
		testAnnotations = createMissingHalfTestSet();
		prf = ac.compare(goldAnnotations, testAnnotations);
		assertEquals(1.0, prf.getPrecision(), 0.0);
		assertEquals(0.5, prf.getRecall(), 0.0);
		assertEquals(2.0 / 3.0, prf.getFmeasure(), 0.0);

		/* test when all annotations overlap gold by 1 character on right */
		testAnnotations = createOverlappingRightAnnotations();
		prf = ac.compare(goldAnnotations, testAnnotations);
		assertEquals(0.0, prf.getPrecision(), 0.0);
		assertEquals(0.0, prf.getRecall(), 0.0);
		assertTrue(Double.isNaN(prf.getFmeasure()));

		/* test on mixed results */
		testAnnotations = createMixedResultAnnotations();
		prf = ac.compare(goldAnnotations, testAnnotations);
		
//		assertEquals(-2, ac.compare(goldAnnotations.get(1), testAnnotations.get(0)));
//		assertEquals(-2, ac.compare(goldAnnotations.get(2), testAnnotations.get(1)));
//		assertEquals(-2, ac.compare(goldAnnotations.get(5), testAnnotations.get(2)));
//		assertEquals(-2, ac.compare(goldAnnotations.get(7), testAnnotations.get(3)));
//		assertEquals(-2, ac.compare(goldAnnotations.get(9), testAnnotations.get(4)));
//		assertEquals(0, ac.compare(goldAnnotations.get(10), testAnnotations.get(5)));
//		System.err.println("GOLD\n" + goldAnnotations.get(11).toString());
//		System.err.println("TEST\n" + testAnnotations.get(6).toString());
//		
//		assertEquals(-2, ac.compare(goldAnnotations.get(11), testAnnotations.get(6)));
//		assertEquals(-2, ac.compare(goldAnnotations.get(12), testAnnotations.get(7)));
//		assertEquals(-2, ac.compare(goldAnnotations.get(17), testAnnotations.get(8)));
//		assertEquals(0, ac.compare(goldAnnotations.get(22), testAnnotations.get(9)));
		
		assertEquals(2.0 / 10.0, prf.getPrecision(), 0.0);
		assertEquals(2.0 / (28.0 + 34.0), prf.getRecall(), 0.0);
		// assertTrue(Double.isNaN(stats.getFmeasure()));

		/* test on mixed class annotations (gene/protein) */
		testAnnotations = createPerfectMixedClassTestAnnotations();
		prf = ac.compare(goldAnnotations, testAnnotations);
		assertEquals(60.0 / 62.0, prf.getPrecision(), 0.0);
		assertEquals(60.0 / 62.0, prf.getRecall(), 0.0);
	}

	/**
	 * Test the compare() method using the StrictSpanComparator and the IdenticalMentionComparator classes
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCompareWithStrictSpanIdenticalMention() throws Exception {
		AnnotationComparator ac = new AnnotationComparator();
		SpanComparator sc = new StrictSpanComparator();
		MentionComparator cmc = new IdenticalMentionComparator();

		String documentID = "1";
		String documentText = testText1;
		int annotationID = 0;

		/* compare against an identical annotation */
		DefaultTextAnnotation identicalAnnotation = createProteinAnnotation(annotationID++, 0, 8, documentID, documentText);
		assertEquals(0, ac.compare(goldAnnotation, identicalAnnotation, sc, cmc));

		/* compare against an overlapping annotation */
		DefaultTextAnnotation overlappingAnnotation = createProteinAnnotation(annotationID++, 0, 12, documentID, documentText);
		assertFalse( ac.compare(goldAnnotation, overlappingAnnotation, sc, cmc)==0);

		/* compare against an annotation that matches the span end boundary */
		DefaultTextAnnotation matchRightAnnotation = createProteinAnnotation(annotationID++, 4, 8, documentID, documentText);
		assertFalse( ac.compare(goldAnnotation, matchRightAnnotation, sc, cmc)==0);

		/* compare against an annotation that matches the span begin boundary */
		DefaultTextAnnotation matchLeftAnnotation = createProteinAnnotation(annotationID++, 0, 15, documentID, documentText);
		assertFalse( ac.compare(goldAnnotation, matchLeftAnnotation, sc, cmc)==0);
	}

	/**
	 * Test the compare() method on lists using the StrictSpanComparator and the IdenticalMentionComparator classes
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCompareListsWithStrictSpanIdenticalMention() throws Exception {
		AnnotationComparator ac = new AnnotationComparator();
		SpanComparator sc = new StrictSpanComparator();
		MentionComparator cmc = new IdenticalMentionComparator();

		/* load a test set that is identical to the gold standard set */
		List<TextAnnotation> testAnnotations = createPerfectTestSet();
		PRFResult prf = ac.compare(goldAnnotations, testAnnotations, sc, cmc);
		assertEquals(1.0, prf.getPrecision(), 0.0);
		assertEquals(1.0, prf.getRecall(), 0.0);
		assertEquals(1.0, prf.getFmeasure(), 0.0);

		/* test for match with no test annotations */
		testAnnotations = new ArrayList<TextAnnotation>();
		prf = ac.compare(goldAnnotations, testAnnotations, sc, cmc);
		assertTrue(Double.isNaN(prf.getPrecision()));
		assertEquals(0.0, prf.getRecall(), 0.0);
		assertTrue(Double.isNaN(prf.getFmeasure()));

		/* test when missing half of the annotations, the other half are strict matches */
		testAnnotations = createMissingHalfTestSet();
		prf = ac.compare(goldAnnotations, testAnnotations, sc, cmc);
		assertEquals(1.0, prf.getPrecision(), 0.0);
		assertEquals(0.5, prf.getRecall(), 0.0);
		assertEquals(2.0 / 3.0, prf.getFmeasure(), 0.0);

		/* test when all annotations overlap gold by 1 character on right */
		testAnnotations = createOverlappingRightAnnotations();
		prf = ac.compare(goldAnnotations, testAnnotations, sc, cmc);
		assertEquals(0.0, prf.getPrecision(), 0.0);
		assertEquals(0.0, prf.getRecall(), 0.0);
		assertTrue(Double.isNaN(prf.getFmeasure()));

		/* test on mixed results */
		testAnnotations = createMixedResultAnnotations();
		prf = ac.compare(goldAnnotations, testAnnotations, sc, cmc);
		assertEquals(2.0 / 10.0, prf.getPrecision(), 0.0);
		assertEquals(2.0 / (28.0 + 34.0), prf.getRecall(), 0.0);
		// assertTrue(Double.isNaN(stats.getFmeasure()));

		/* test on mixed class annotations (gene/protein) */
		testAnnotations = createPerfectMixedClassTestAnnotations();
		prf = ac.compare(goldAnnotations, testAnnotations, sc, cmc);
		assertEquals(60.0 / 62.0, prf.getPrecision(), 0.0);
		assertEquals(60.0 / 62.0, prf.getRecall(), 0.0);
	}

	/**
	 * Test the compare() method using the SloppySpanComparator and the IdenticalMentionComparator classes
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCompareWithSloppySpanIdenticalMention() throws Exception {
		AnnotationComparator ac = new AnnotationComparator();
		SpanComparator sc = new SloppySpanComparator();
		MentionComparator cmc = new IdenticalMentionComparator();

		String documentID = "1";
		String documentText = testText1;
		int annotationID = 0;

		/* compare against an identical annotation */
		DefaultTextAnnotation identicalAnnotation = createProteinAnnotation(annotationID++, 0, 8, documentID, documentText);
		assertEquals(0, ac.compare(goldAnnotation, identicalAnnotation, sc, cmc));

		/* compare against an overlapping annotation */
		DefaultTextAnnotation overlappingAnnotation = createProteinAnnotation(annotationID++, 0, 12, documentID, documentText);
		assertEquals(0, ac.compare(goldAnnotation, overlappingAnnotation, sc, cmc));

		/* compare against an annotation that matches the span end boundary */
		DefaultTextAnnotation matchRightAnnotation = createProteinAnnotation(annotationID++, 4, 8, documentID, documentText);
		assertEquals(0, ac.compare(goldAnnotation, matchRightAnnotation, sc, cmc));

		/* compare against an annotation that matches the span begin boundary */
		DefaultTextAnnotation matchLeftAnnotation = createProteinAnnotation(annotationID++, 0, 15, documentID, documentText);
		assertEquals(0, ac.compare(goldAnnotation, matchLeftAnnotation, sc, cmc));
	}

	/**
	 * Test the compare() method on lists using the SloppySpanComparator and the IdenticalMentionComparator classes
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCompareListsWithSloppySpanIdenticalMention() throws Exception {
		AnnotationComparator ac = new AnnotationComparator();
		SpanComparator sc = new SloppySpanComparator();
		MentionComparator cmc = new IdenticalMentionComparator();

		/* test for sloppy match with identical annotation sets */
		List<TextAnnotation> testAnnotations = createPerfectTestSet();
		PRFResult prf = ac.compare(goldAnnotations, testAnnotations, sc, cmc);
		assertEquals(1.0, prf.getPrecision(), 0.0);
		assertEquals(1.0, prf.getRecall(), 0.0);
		assertEquals(1.0, prf.getFmeasure(), 0.0);

		/* test for sloppy match with no annotations */
		testAnnotations = new ArrayList<TextAnnotation>();
		prf = ac.compare(goldAnnotations, testAnnotations, sc, cmc);
		assertTrue(Double.isNaN(prf.getPrecision()));
		assertEquals(0.0, prf.getRecall(), 0.0);
		assertTrue(Double.isNaN(prf.getFmeasure()));

		/* test when missing half of the annotations, the other half are strict matches */
		testAnnotations = createMissingHalfTestSet();
		prf = ac.compare(goldAnnotations, testAnnotations, sc, cmc);
		assertEquals(1.0, prf.getPrecision(), 0.0);
		assertEquals(0.5, prf.getRecall(), 0.0);
		assertEquals(2.0 / 3.0, prf.getFmeasure(), 0.0);

		/* test when all annotations overlap gold by 1 character on right */
		testAnnotations = createOverlappingRightAnnotations();
		prf = ac.compare(goldAnnotations, testAnnotations, sc, cmc);
		assertEquals(1.0, prf.getPrecision(), 0.0);
		assertEquals(1.0, prf.getRecall(), 0.0);
		assertEquals(1.0, prf.getFmeasure(), 0.0);

		/* test on mixed results */
		testAnnotations = createMixedResultAnnotations();
		prf = ac.compare(goldAnnotations, testAnnotations, sc, cmc);
		assertEquals(8.0 / 10.0, prf.getPrecision(), 0.0);
		assertEquals(8.0 / (8.0 + 17.0 + 34.0), prf.getRecall(), 0.0);
		// assertEquals(1.0, stats.getFmeasure());

		/* test on mixed class annotations (gene/protein) */
		testAnnotations = createPerfectMixedClassTestAnnotations();
		prf = ac.compare(goldAnnotations, testAnnotations, sc, cmc);
		assertEquals(60.0 / 62.0, prf.getPrecision(), 0.0);
		assertEquals(60.0 / 62.0, prf.getRecall(), 0.0);
	}

	/**
	 * Test the compare() method using the SharedStartSpanComparator and the IdenticalMentionComparator classes
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCompareWithSharedStartSpanIdenticalMention() throws Exception {
		AnnotationComparator ac = new AnnotationComparator();
		SpanComparator sc = new SharedStartSpanComparator();
		MentionComparator cmc = new IdenticalMentionComparator();

		String documentID = "1";
		String documentText = testText1;
		int annotationID = 0;

		/* compare against an identical annotation */
		DefaultTextAnnotation identicalAnnotation = createProteinAnnotation(annotationID++, 0, 8, documentID, documentText);
		assertEquals(0, ac.compare(goldAnnotation, identicalAnnotation, sc, cmc));

		/* compare against an overlapping annotation */
		DefaultTextAnnotation overlappingAnnotation = createProteinAnnotation(annotationID++, 0, 12, documentID, documentText);
		assertEquals(0, ac.compare(goldAnnotation, overlappingAnnotation, sc, cmc));

		/* compare against an annotation that matches the span end boundary */
		DefaultTextAnnotation matchRightAnnotation = createProteinAnnotation(annotationID++, 4, 8, documentID, documentText);
		assertFalse( ac.compare(goldAnnotation, matchRightAnnotation, sc, cmc)==0);

		/* compare against an annotation that matches the span begin boundary */
		DefaultTextAnnotation matchLeftAnnotation = createProteinAnnotation(annotationID++, 0, 15, documentID, documentText);
		assertEquals(0, ac.compare(goldAnnotation, matchLeftAnnotation, sc, cmc));
	}

	/**
	 * Test the compare() method on lists using the SharedStartSpanComparator and the IdenticalMentionComparator classes
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCompareListsWithSharedStartSpanIdenticalMention() throws Exception {
		AnnotationComparator ac = new AnnotationComparator();
		SpanComparator sc = new SharedStartSpanComparator();
		MentionComparator cmc = new IdenticalMentionComparator();

		/* test for left boundary match with identical annotation sets */
		List<TextAnnotation> testAnnotations = createPerfectTestSet();
		PRFResult prf = ac.compare(goldAnnotations, testAnnotations, sc, cmc);
		assertEquals(1.0, prf.getPrecision(), 0.0);
		assertEquals(1.0, prf.getRecall(), 0.0);
		assertEquals(1.0, prf.getFmeasure(), 0.0);

		/* test for left boundary match with empty annotation set */
		testAnnotations = new ArrayList<TextAnnotation>();
		prf = ac.compare(goldAnnotations, testAnnotations, sc, cmc);
		assertTrue(Double.isNaN(prf.getPrecision()));
		assertEquals(0.0, prf.getRecall(), 0.0);
		assertTrue(Double.isNaN(prf.getFmeasure()));

		/* test when missing half of the annotations, the other half are strict matches */
		testAnnotations = createMissingHalfTestSet();
		prf = ac.compare(goldAnnotations, testAnnotations, sc, cmc);
		assertEquals(1.0, prf.getPrecision(), 0.0);
		assertEquals(0.5, prf.getRecall(), 0.0);
		assertEquals(2.0 / 3.0, prf.getFmeasure(), 0.0);

		/* test when all annotations overlap gold by 1 character on right */
		testAnnotations = createOverlappingRightAnnotations();
		prf = ac.compare(goldAnnotations, testAnnotations, sc, cmc);
		assertEquals(1.0, prf.getPrecision(), 0.0);
		assertEquals(1.0, prf.getRecall(), 0.0);
		assertEquals(1.0, prf.getFmeasure(), 0.0);

		/* test on mixed results */
		testAnnotations = createMixedResultAnnotations();
		prf = ac.compare(goldAnnotations, testAnnotations, sc, cmc);
		assertEquals(7.0 / 10.0, prf.getPrecision(), 0.0);
		assertEquals(7.0 / (28.0 + 34.0), prf.getRecall(), 0.0);
		// assertEquals(1.0, stats.getFmeasure());

		/* test on mixed class annotations (gene/protein) */
		testAnnotations = createPerfectMixedClassTestAnnotations();
		prf = ac.compare(goldAnnotations, testAnnotations, sc, cmc);
		assertEquals(60.0 / 62.0, prf.getPrecision(), 0.0);
		assertEquals(60.0 / 62.0, prf.getRecall(), 0.0);
	}

	/**
	 * Test the compare() method using the SharedEndSpanComparator and the IdenticalMentionComparator classes
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCompareWithSharedEndSpanIdenticalMention() throws Exception {
		AnnotationComparator ac = new AnnotationComparator();
		SpanComparator sc = new SharedEndSpanComparator();
		MentionComparator cmc = new IdenticalMentionComparator();

		String documentID = "1";
		String documentText = testText1;
		int annotationID = 0;

		/* compare against an identical annotation */
		DefaultTextAnnotation identicalAnnotation = createProteinAnnotation(annotationID++, 0, 8, documentID, documentText);
		assertEquals(0, ac.compare(goldAnnotation, identicalAnnotation, sc, cmc));

		/* compare against an overlapping annotation */
		DefaultTextAnnotation overlappingAnnotation = createProteinAnnotation(annotationID++, 0, 12, documentID, documentText);
		assertFalse( ac.compare(goldAnnotation, overlappingAnnotation, sc, cmc)==0);

		/* compare against an annotation that matches the span end boundary */
		DefaultTextAnnotation matchRightAnnotation = createProteinAnnotation(annotationID++, 4, 8, documentID, documentText);
		assertEquals(0, ac.compare(goldAnnotation, matchRightAnnotation, sc, cmc));

		/* compare against an annotation that matches the span begin boundary */
		DefaultTextAnnotation matchLeftAnnotation = createProteinAnnotation(annotationID++, 0, 15, documentID, documentText);
		assertFalse( ac.compare(goldAnnotation, matchLeftAnnotation, sc, cmc)==0);
	}

	/**
	 * Test the compare() method on lists using the SharedEndSpanComparator and the IdenticalMentionComparator classes
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCompareListsWithSharedEndSpanIdenticalMention() throws Exception {
		AnnotationComparator ac = new AnnotationComparator();
		SpanComparator sc = new SharedEndSpanComparator();
		MentionComparator cmc = new IdenticalMentionComparator();

		/* test for right boundary match with identical annotation sets */
		List<TextAnnotation> testAnnotations = createPerfectTestSet();
		PRFResult prf = ac.compare(goldAnnotations, testAnnotations, sc, cmc);
		assertEquals(1.0, prf.getPrecision(), 0.0);
		assertEquals(1.0, prf.getRecall(), 0.0);
		assertEquals(1.0, prf.getFmeasure(), 0.0);

		/* test for right boundary match with empty annotation set */
		testAnnotations = new ArrayList<TextAnnotation>();
		prf = ac.compare(goldAnnotations, testAnnotations, sc, cmc);
		assertTrue(Double.isNaN(prf.getPrecision()));
		assertEquals(0.0, prf.getRecall(), 0.0);
		assertTrue(Double.isNaN(prf.getFmeasure()));

		/* test when missing half of the annotations, the other half are strict matches */
		testAnnotations = createMissingHalfTestSet();
		prf = ac.compare(goldAnnotations, testAnnotations, sc, cmc);
		assertEquals(1.0, prf.getPrecision(), 0.0);
		assertEquals(0.5, prf.getRecall(), 0.0);
		assertEquals(2.0 / 3.0, prf.getFmeasure(), 0.0);

		/* test when all annotations overlap gold by 1 character on right */
		testAnnotations = createOverlappingRightAnnotations();
		prf = ac.compare(goldAnnotations, testAnnotations, sc, cmc);
		assertEquals(0.0, prf.getPrecision(), 0.0);
		assertEquals(0.0, prf.getRecall(), 0.0);
		assertTrue(Double.isNaN(prf.getFmeasure()));

		/* test on mixed results */
		testAnnotations = createMixedResultAnnotations();
		prf = ac.compare(goldAnnotations, testAnnotations, sc, cmc);
		assertEquals(5.0 / 10.0, prf.getPrecision(), 0.0);
		assertEquals(5.0 / (5.0 + 23.0 + 34.0), prf.getRecall(), 0.0);
		// assertTrue(Double.isNaN(stats.getFmeasure()));

		/* test on mixed class annotations (gene/protein) */
		testAnnotations = createPerfectMixedClassTestAnnotations();
		prf = ac.compare(goldAnnotations, testAnnotations, sc, cmc);
		assertEquals(60.0 / 62.0, prf.getPrecision(), 0.0);
		assertEquals(60.0 / 62.0, prf.getRecall(), 0.0);
	}

	/**
	 * Test the compare() method using the SharedStartOrEndSpanComparator and the IdenticalMentionComparator classes
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCompareWithSharedEitherSpanIdenticalMention() throws Exception {
		AnnotationComparator ac = new AnnotationComparator();
		SpanComparator sc = new SharedStartOrEndSpanComparator();
		MentionComparator cmc = new IdenticalMentionComparator();

		String documentID = "1";
		String documentText = testText1;
		int annotationID = 0;

		/* compare against an identical annotation */
		DefaultTextAnnotation identicalAnnotation = createProteinAnnotation(annotationID++, 0, 8, documentID, documentText);
		assertEquals(0, ac.compare(goldAnnotation, identicalAnnotation, sc, cmc));

		/* compare against an overlapping annotation */
		DefaultTextAnnotation overlappingAnnotation = createProteinAnnotation(annotationID++, 0, 12, documentID, documentText);
		assertEquals(0, ac.compare(goldAnnotation, overlappingAnnotation, sc, cmc));

		/* compare against an annotation that matches the span end boundary */
		DefaultTextAnnotation matchRightAnnotation = createProteinAnnotation(annotationID++, 4, 8, documentID, documentText);
		assertEquals(0, ac.compare(goldAnnotation, matchRightAnnotation, sc, cmc));

		/* compare against an annotation that matches neither span boundary */
		DefaultTextAnnotation matchNeitherSideAnnotation = createProteinAnnotation(annotationID++, 4, 12, documentID, documentText);
		assertFalse( ac.compare(goldAnnotation, matchNeitherSideAnnotation, sc, cmc)==0);

		/* compare against an annotation that matches the span begin boundary */
		DefaultTextAnnotation matchLeftAnnotation = createProteinAnnotation(annotationID++, 0, 15, documentID, documentText);
		assertEquals(0, ac.compare(goldAnnotation, matchLeftAnnotation, sc, cmc));
	}

	/**
	 * Test the compare() method on lists using the SharedStartOrEndSpanComparator and the IdenticalMentionComparator
	 * classes
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCompareListsWithSharedEitherSpanIdenticalMention() throws Exception {
		AnnotationComparator ac = new AnnotationComparator();
		SpanComparator sc = new SharedStartOrEndSpanComparator();
		MentionComparator cmc = new IdenticalMentionComparator();

		/* test for either boundary match with identical annotation sets */
		List<TextAnnotation> testAnnotations = createPerfectTestSet();
		PRFResult prf = ac.compare(goldAnnotations, testAnnotations, sc, cmc);
		assertEquals(1.0, prf.getPrecision(), 0.0);
		assertEquals(1.0, prf.getRecall(), 0.0);
		assertEquals(1.0, prf.getFmeasure(), 0.0);

		/* test for either boundary match with empty annotation set */
		testAnnotations = new ArrayList<TextAnnotation>();
		prf = ac.compare(goldAnnotations, testAnnotations, sc, cmc);
		assertTrue(Double.isNaN(prf.getPrecision()));
		assertEquals(0.0, prf.getRecall(), 0.0);
		assertTrue(Double.isNaN(prf.getFmeasure()));

		/* test when missing half of the annotations, the other half are strict matches */
		testAnnotations = createMissingHalfTestSet();
		prf = ac.compare(goldAnnotations, testAnnotations, sc, cmc);
		assertEquals(1.0, prf.getPrecision(), 0.0);
		assertEquals(0.5, prf.getRecall(), 0.0);
		assertEquals(2.0 / 3.0, prf.getFmeasure(), 0.0);

		/* test when all annotations overlap gold by 1 character on right */
		testAnnotations = createOverlappingRightAnnotations();
		prf = ac.compare(goldAnnotations, testAnnotations, sc, cmc);
		assertEquals(1.0, prf.getPrecision(), 0.0);
		assertEquals(1.0, prf.getRecall(), 0.0);
		assertEquals(1.0, prf.getFmeasure(), 0.0);

		/* test on mixed results */
		testAnnotations = createMixedResultAnnotations();
		prf = ac.compare(goldAnnotations, testAnnotations, sc, cmc);
		assertEquals(8.0 / 10.0, prf.getPrecision(), 0.0);
		assertEquals(8.0 / (8.0 + 18.0 + 34.0), prf.getRecall(), 0.0);
		// assertEquals(1.0, stats.getFmeasure());

		/* test on mixed class annotations (gene/protein) */
		testAnnotations = createPerfectMixedClassTestAnnotations();
		prf = ac.compare(goldAnnotations, testAnnotations, sc, cmc);
		assertEquals(60.0 / 62.0, prf.getPrecision(), 0.0);
		assertEquals(60.0 / 62.0, prf.getRecall(), 0.0);
	}

	/**
	 * Test the EntityPartsAnnotationComparator
	 * 
	 * @throws Exception
	 */
	@Test
	public void testEntityPartsAnnotationComparator() throws Exception {
		AnnotationComparator ac = new EntityPartsAnnotationComparator();

		/* test for pnp match with identical annotation sets */
		List<TextAnnotation> testAnnotations = createPerfectTestSet();
		PRFResult prf = ac.compare(goldAnnotations, testAnnotations);
		assertEquals(1.0, prf.getPrecision(), 0.0);
		assertEquals(1.0, prf.getRecall(), 0.0);
		assertEquals(1.0, prf.getFmeasure(), 0.0);

		/* test for pnp match with empty annotation set */
		testAnnotations = new ArrayList<TextAnnotation>();
		prf = ac.compare(goldAnnotations, testAnnotations);
		assertTrue(Double.isNaN(prf.getPrecision()));
		assertEquals(0.0, prf.getRecall(), 0.0);
		assertTrue(Double.isNaN(prf.getFmeasure()));

		/* test when missing half of the annotations, the other half are strict matches */
		testAnnotations = createMissingHalfTestSet();
		prf = ac.compare(goldAnnotations, testAnnotations);
		assertEquals(1.0, prf.getPrecision(), 0.0);
		assertEquals(32.0 / 69.0, prf.getRecall(), 0.0);
		assertEquals((2.0 * 32.0 / 69.0) / (1.0 + 32.0 / 69.0), prf.getFmeasure(), 0.0);

		/* test when all annotations overlap gold by 1 character on right */
		testAnnotations = createOverlappingRightAnnotations();
		prf = ac.compare(goldAnnotations, testAnnotations);
		assertEquals(48.0 / 70.0, prf.getPrecision(), 0.0);
		assertEquals(48.0 / 69.0, prf.getRecall(), 0.0);
		// assertEquals((2.0*48.0/70.0*48.0/69.0)/(48.0/70.0+48.0/69.0), stats.getFmeasure());

		/* test on mixed results */
		testAnnotations = createMixedResultAnnotations();
		prf = ac.compare(goldAnnotations, testAnnotations);
		assertEquals(8.0 / 17.0, prf.getPrecision(), 0.0);
		assertEquals(8.0 / (8.0 + 23.0 + 38.0), prf.getRecall(), 0.0);

		/*
		 * make sure calls to compare() with extraneous input parameters return what is expected (i.e. they ignore the
		 * extra input parameters)
		 */
		SpanComparator sc = new SharedStartOrEndSpanComparator();
		MentionComparator cmc = new IdenticalMentionComparator();
		testAnnotations = createMissingHalfTestSet();
		prf = ac.compare(goldAnnotations, testAnnotations, sc, cmc);
		assertEquals(1.0, prf.getPrecision(), 0.0);
		assertEquals(32.0 / 69.0, prf.getRecall(), 0.0);
		assertEquals((2.0 * 32.0 / 69.0) / (1.0 + 32.0 / 69.0), prf.getFmeasure(), 0.0);

		prf = ac.compare(goldAnnotations, testAnnotations, sc, cmc, -1);
		assertEquals(1.0, prf.getPrecision(), 0.0);
		assertEquals(32.0 / 69.0, prf.getRecall(), 0.0);
		assertEquals((2.0 * 32.0 / 69.0) / (1.0 + 32.0 / 69.0), prf.getFmeasure(), 0.0);

	}

	/**
	 * Create a protein annotation to be used for testing purposes
	 */
	private DefaultTextAnnotation createProteinAnnotation(int annotationID, int spanStart, int spanEnd, String documentID, String documentText) {

		String coveredText = documentText.substring(spanStart, spanEnd);

		DefaultClassMention cm = new DefaultClassMention(ClassMentionType.PROTEIN.typeName());

		DefaultTextAnnotation ta = new DefaultTextAnnotation(spanStart, spanEnd, coveredText, annotator, annotationSet, annotationID,
				documentCollectionID, documentID, documentSectionID, cm);

		return ta;
	}

	/**
	 * Create a gene annotation to be used for testing purposes
	 */
	private DefaultTextAnnotation createGeneAnnotation(int annotationID, int spanStart, int spanEnd, String documentID, String documentText) {

		String coveredText = documentText.substring(spanStart, spanEnd);

		DefaultClassMention cm = new DefaultClassMention(ClassMentionType.GENE.typeName());

		DefaultTextAnnotation ta = new DefaultTextAnnotation(spanStart, spanEnd, coveredText, annotator, annotationSet, annotationID,
				documentCollectionID, documentID, documentSectionID, cm);

		return ta;
	}

	/**
	 * Create a set of gold standard annotations to be used for testing purposes
	 */
	private void createGoldSetAnnotations() {
		goldAnnotations = new ArrayList<TextAnnotation>();
		String documentID = "1";
		String documentText = testText1;
		int annotationID = 0;
		
//		returnList.add(createProteinAnnotation(annotationID++, 76, 98, documentID, documentText));/* 1x */
//		returnList.add(createProteinAnnotation(annotationID++, 158, 182, documentID, documentText));/*2 x */
//		returnList.add(createProteinAnnotation(annotationID++, 652, 661, documentID, documentText));/* 5x */
//		returnList.add(createProteinAnnotation(annotationID++, 813, 833, documentID, documentText));/* 7x */
//		returnList.add(createProteinAnnotation(annotationID++, 984, 994, documentID, documentText));/* 9x */
//		returnList.add(createProteinAnnotation(annotationID++, 1064, 1066, documentID, documentText));/* 10o */
//		returnList.add(createProteinAnnotation(annotationID++, 1096, 1120, documentID, documentText));/* 11x */
//		returnList.add(createProteinAnnotation(annotationID++, 1157, 1178, documentID, documentText));/* 12x */
//		returnList.add(createProteinAnnotation(annotationID++, 1454, 1463, documentID, documentText));/* 17x */
//		returnList.add(createProteinAnnotation(annotationID++, 1639, 1642, documentID, documentText));/* 22o */
		
		goldAnnotations.add(createProteinAnnotation(annotationID++, 0, 8, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 76, 93, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 95, 97, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 484, 487, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 560, 563, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 652, 657, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 658, 661, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 813, 825, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 892, 895, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 984, 986, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1064, 1066, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1148, 1178, documentID, documentText));// 11
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1180, 1189, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1241, 1253, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1277, 1280, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1323, 1325, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1424, 1426, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1454, 1459, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1460, 1463, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1496, 1498, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1543, 1546, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1598, 1601, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1639, 1642, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1665, 1670, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1713, 1715, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1734, 1737, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1873, 1876, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1885, 1893, documentID, documentText));

		documentID = "2";
		documentText = testText2;
		annotationID = 0;
		goldAnnotations.add(createProteinAnnotation(annotationID++, 0, 8, documentID, documentText)); // this one is
																										// fake, but
																										// matches the
		// span of an annotation in document
		// #1
		goldAnnotations.add(createProteinAnnotation(annotationID++, 64, 88, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 180, 186, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 195, 209, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 283, 289, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 348, 354, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 435, 441, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 551, 557, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 856, 865, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 885, 890, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 949, 963, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 985, 988, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1003, 1007, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1018, 1023, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1025, 1029, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1030, 1033, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1044, 1054, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1060, 1063, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1064, 1068, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1081, 1088, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1175, 1181, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1186, 1194, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1298, 1304, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1357, 1366, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1371, 1376, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1417, 1420, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1425, 1439, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1473, 1477, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1478, 1482, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1483, 1486, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1487, 1490, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1491, 1494, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1508, 1514, documentID, documentText));
		goldAnnotations.add(createProteinAnnotation(annotationID++, 1520, 1525, documentID, documentText));
	}

	/**
	 * Return a perfect match to the set of gold standard annotations
	 * 
	 * @return
	 */
	private List<TextAnnotation> createPerfectTestSet() {
		return new ArrayList<TextAnnotation>(goldAnnotations);
	}

	/**
	 * Return half of the set of gold standard annotations
	 * 
	 * @return
	 */
	private List<TextAnnotation> createMissingHalfTestSet() {
		List<TextAnnotation> returnList = new ArrayList<TextAnnotation>();
		String documentID = "1";
		String documentText = testText1;
		int annotationID = 0;
		returnList.add(createProteinAnnotation(annotationID++, 0, 8, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 95, 97, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 560, 563, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 658, 661, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 892, 895, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1064, 1066, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1180, 1189, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1277, 1280, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1424, 1426, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1460, 1463, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1543, 1546, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1639, 1642, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1713, 1715, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1873, 1876, documentID, documentText));

		documentID = "2";
		documentText = testText2;
		annotationID = 0;
		returnList.add(createProteinAnnotation(annotationID++, 0, 8, documentID, documentText)); // this one is fake,
																									// but matches the
		// span of an annotation in document #1
		returnList.add(createProteinAnnotation(annotationID++, 180, 186, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 283, 289, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 435, 441, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 856, 865, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 949, 963, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1003, 1007, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1025, 1029, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1044, 1054, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1064, 1068, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1175, 1181, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1298, 1304, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1371, 1376, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1425, 1439, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1478, 1482, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1487, 1490, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1508, 1514, documentID, documentText));

		return returnList;

	}

	/**
	 * Return a set of annotations that overlap the gold annotations by 1 on the right
	 */
	private List<TextAnnotation> createOverlappingRightAnnotations() {
		List<TextAnnotation> returnList = new ArrayList<TextAnnotation>();
		String documentID = "1";
		String documentText = testText1;
		int annotationID = 0;
		returnList.add(createProteinAnnotation(annotationID++, 0, 8 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 76, 93 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 95, 97 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 484, 487 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 560, 563 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 652, 657 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 658, 661 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 813, 825 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 892, 895 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 984, 986 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1064, 1066 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1148, 1178 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1180, 1189 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1241, 1253 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1277, 1280 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1323, 1325 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1424, 1426 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1454, 1459 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1460, 1463 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1496, 1498 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1543, 1546 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1598, 1601 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1639, 1642 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1665, 1670 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1713, 1715 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1734, 1737 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1873, 1876 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1885, 1893 + 1, documentID, documentText));

		documentID = "2";
		documentText = testText2;
		annotationID = 0;
		returnList.add(createProteinAnnotation(annotationID++, 0, 8 + 1, documentID, documentText)); // this one is
																										// fake, but
																										// matches the
		// span of an annotation in document
		// #1
		returnList.add(createProteinAnnotation(annotationID++, 64, 88 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 180, 186 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 195, 209 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 283, 289 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 348, 354 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 435, 441 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 551, 557 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 856, 865 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 885, 890 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 949, 963 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 985, 988 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1003, 1007 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1018, 1023 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1025, 1029 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1030, 1033 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1044, 1054 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1060, 1063 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1064, 1068 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1081, 1088 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1175, 1181 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1186, 1194 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1298, 1304 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1357, 1366 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1371, 1376 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1417, 1420 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1425, 1439 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1473, 1477 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1478, 1482 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1483, 1486 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1487, 1490 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1491, 1494 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1508, 1514 + 1, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1520, 1525 + 1, documentID, documentText));
		return returnList;
	}

	/**
	 * Create a set of annotations that contains a mix of overlaps, tp's, and fp's
	 */
	private List<TextAnnotation> createMixedResultAnnotations() {
		List<TextAnnotation> returnList = new ArrayList<TextAnnotation>();
		String documentID = "1";
		String documentText = testText1;
		int annotationID = 0;
		returnList.add(createProteinAnnotation(annotationID++, 76, 98, documentID, documentText));/*  */
		returnList.add(createProteinAnnotation(annotationID++, 158, 182, documentID, documentText));/*  */
		returnList.add(createProteinAnnotation(annotationID++, 652, 661, documentID, documentText));/*  */
		returnList.add(createProteinAnnotation(annotationID++, 813, 833, documentID, documentText));/*  */
		returnList.add(createProteinAnnotation(annotationID++, 984, 994, documentID, documentText));/*  */
		returnList.add(createProteinAnnotation(annotationID++, 1064, 1066, documentID, documentText));/*  */
		returnList.add(createProteinAnnotation(annotationID++, 1096, 1120, documentID, documentText));/*  */
		returnList.add(createProteinAnnotation(annotationID++, 1157, 1178, documentID, documentText));/*  */
		returnList.add(createProteinAnnotation(annotationID++, 1454, 1463, documentID, documentText));/*  */
		returnList.add(createProteinAnnotation(annotationID++, 1639, 1642, documentID, documentText));/*  */

		// documentID = "2";
		// documentText = testText2;
		// annotationID = 0;
		// returnList.add(createProteinAnnotation(annotationID++, 195, 209, documentID, documentText));/* */
		// returnList.add(createProteinAnnotation(annotationID++, 773, 775, documentID, documentText));/* */
		// returnList.add(createProteinAnnotation(annotationID++, 885, 890, documentID, documentText));/* */
		// returnList.add(createProteinAnnotation(annotationID++, 949, 952, documentID, documentText));/* */
		// returnList.add(createProteinAnnotation(annotationID++, 968, 988, documentID, documentText));/* */
		// returnList.add(createProteinAnnotation(annotationID++, 1003, 1007, documentID, documentText));/* */
		// returnList.add(createProteinAnnotation(annotationID++, 1018, 1023, documentID, documentText));/* */
		// returnList.add(createProteinAnnotation(annotationID++, 1025, 1033, documentID, documentText));/* */
		// returnList.add(createProteinAnnotation(annotationID++, 1060, 1070, documentID, documentText));/* */
		// returnList.add(createProteinAnnotation(annotationID++, 1081, 1088, documentID, documentText));/* */
		// returnList.add(createProteinAnnotation(annotationID++, 1198, 1205, documentID, documentText));/* */
		// returnList.add(createProteinAnnotation(annotationID++, 1308, 1315, documentID, documentText));/* */
		// returnList.add(createProteinAnnotation(annotationID++, 1357, 1366, documentID, documentText));/* */
		// returnList.add(createProteinAnnotation(annotationID++, 1371, 1376, documentID, documentText));/* */
		// returnList.add(createProteinAnnotation(annotationID++, 1417, 1420, documentID, documentText));/* */
		// returnList.add(createProteinAnnotation(annotationID++, 1425, 1428, documentID, documentText));/* */
		// returnList.add(createProteinAnnotation(annotationID++, 1473, 1494, documentID, documentText));/* */
		// returnList.add(createProteinAnnotation(annotationID++, 1520, 1536, documentID, documentText));/* */
		// returnList.add(createProteinAnnotation(annotationID++, 1551, 1558, documentID, documentText));/* */

		return returnList;
	}

	/**
	 * Create a set of annotations that has identical spans, but a few annotations with "gene" annotations instead of
	 * "protein" annotations
	 * 
	 * @return
	 */
	private List<TextAnnotation> createPerfectMixedClassTestAnnotations() {
		List<TextAnnotation> returnList = new ArrayList<TextAnnotation>();
		String documentID = "1";
		String documentText = testText1;
		int annotationID = 0;
		returnList.add(createGeneAnnotation(annotationID++, 0, 8, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 76, 93, documentID, documentText));
		returnList.add(createGeneAnnotation(annotationID++, 95, 97, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 484, 487, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 560, 563, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 652, 657, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 658, 661, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 813, 825, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 892, 895, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 984, 986, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1064, 1066, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1148, 1178, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1180, 1189, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1241, 1253, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1277, 1280, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1323, 1325, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1424, 1426, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1454, 1459, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1460, 1463, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1496, 1498, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1543, 1546, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1598, 1601, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1639, 1642, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1665, 1670, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1713, 1715, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1734, 1737, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1873, 1876, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1885, 1893, documentID, documentText));

		documentID = "2";
		documentText = testText2;
		annotationID = 0;
		returnList.add(createProteinAnnotation(annotationID++, 0, 8, documentID, documentText)); // this one is fake,
																									// but matches the
		// span of an annotation in document
		// #1
		returnList.add(createProteinAnnotation(annotationID++, 64, 88, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 180, 186, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 195, 209, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 283, 289, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 348, 354, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 435, 441, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 551, 557, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 856, 865, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 885, 890, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 949, 963, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 985, 988, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1003, 1007, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1018, 1023, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1025, 1029, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1030, 1033, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1044, 1054, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1060, 1063, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1064, 1068, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1081, 1088, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1175, 1181, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1186, 1194, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1298, 1304, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1357, 1366, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1371, 1376, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1417, 1420, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1425, 1439, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1473, 1477, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1478, 1482, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1483, 1486, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1487, 1490, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1491, 1494, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1508, 1514, documentID, documentText));
		returnList.add(createProteinAnnotation(annotationID++, 1520, 1525, documentID, documentText));

		return returnList;
	}
}

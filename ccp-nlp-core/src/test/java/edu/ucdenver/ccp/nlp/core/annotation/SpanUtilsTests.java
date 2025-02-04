package edu.ucdenver.ccp.nlp.core.annotation;

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

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;

/**
 * @author Colorado Computational Pharmacology, UC Denver;
 *         ccpsupport@ucdenver.edu
 * 
 */
public class SpanUtilsTests {

	@Test
	public void testGetCoveredText() {
		               // 012345678901234567890123456789012345678901234567890123456789
		String docText = "The quick brown fox jumped over the lazy frog.";
		List<Span> quickBrownFox = CollectionsUtil.createList(new Span(4, 19));

		assertEquals("quick brown fox", SpanUtils.getCoveredText(quickBrownFox, docText));

		List<Span> quickJumpedFrog = CollectionsUtil.createList(new Span(4, 9), new Span(20, 26), new Span(41, 45));
		assertEquals("quick .. jumped .. frog", SpanUtils.getCoveredText(quickJumpedFrog, docText));

	}

	@Test
	public void testSpan0() throws Exception {
		Span span = new Span(3, 5);
		Span span2 = new Span(3, 5);
		assertEquals(span, span2);
		assertEquals(span.getSpanStart(), 3);
		assertEquals(span.getSpanEnd(), 5);
	}

	@Test
	public void testSpan1_1() throws Exception {
		Span span = new Span(3, 6);
		Span span2 = new Span(5, 10);
		List<Span> norm = SpanUtils.reduceSpans(span, span2);
		assertEquals(1, norm.size());
		Span mergeSpan = norm.get(0);
		assertEquals(3, mergeSpan.getSpanStart());
		assertEquals(10, mergeSpan.getSpanEnd());
	}

	@Test
	public void testSpan1_2() throws Exception {
		Span span = new Span(3, 5);
		Span span2 = new Span(7, 10);
		List<Span> norm = SpanUtils.reduceSpans(span, span2);
		assertEquals(2, norm.size());
		Span mergeSpan = norm.get(0);
		assertEquals(3, mergeSpan.getSpanStart());
		assertEquals(5, mergeSpan.getSpanEnd());
		mergeSpan = norm.get(1);
		assertEquals(7, mergeSpan.getSpanStart());
		assertEquals(10, mergeSpan.getSpanEnd());
	}

	@Test
	public void testSpan2_1() throws Exception {
		Span span = new Span(3, 6);
		Span span2 = new Span(5, 10);
		List<Span> spanList = new LinkedList<Span>();
		spanList.add(span2);
		List<Span> norm = SpanUtils.reduceSpans(span, spanList);

		assertEquals(1, norm.size());
		Span mergeSpan = norm.get(0);
		assertEquals(3, mergeSpan.getSpanStart());
		assertEquals(10, mergeSpan.getSpanEnd());
	}

	@Test
	public void testSpan2_2() throws Exception {
		Span span = new Span(3, 5);
		Span span2 = new Span(7, 10);
		List<Span> spanList = new LinkedList<Span>();
		spanList.add(span2);
		List<Span> norm = SpanUtils.reduceSpans(span, spanList);
		assertEquals(2, norm.size());
		Span mergeSpan = norm.get(0);
		assertEquals(3, mergeSpan.getSpanStart());
		assertEquals(5, mergeSpan.getSpanEnd());
		mergeSpan = norm.get(1);
		assertEquals(7, mergeSpan.getSpanStart());
		assertEquals(10, mergeSpan.getSpanEnd());
	}

	@Test
	public void testSpan3_1() throws Exception {
		Span span = new Span(3, 6);
		Span span2 = new Span(5, 10);
		List<Span> spans = new LinkedList<Span>();
		spans.add(span);
		spans.add(span2);
		List<Span> norm = SpanUtils.normalizeSpans(spans);
		assertEquals(1, norm.size());
		Span normSpan = norm.get(0);
		assertEquals(3, normSpan.getSpanStart());
		assertEquals(10, normSpan.getSpanEnd());
	}

	@Test
	public void testSpan3_2() throws Exception {
		Span span2 = new Span(3, 6);
		Span span = new Span(5, 10);
		List<Span> spans = new LinkedList<Span>();
		spans.add(span);
		spans.add(span2);
		List<Span> norm = SpanUtils.normalizeSpans(spans);
		assertEquals(1, norm.size());
		Span normSpan = norm.get(0);
		assertEquals(3, normSpan.getSpanStart());
		assertEquals(10, normSpan.getSpanEnd());
	}

	@Test
	public void testSpan4_1() throws Exception {
		Span span = new Span(3, 6);
		Span span2 = new Span(5, 10);
		List<Span> spans = new LinkedList<Span>();
		spans.add(span);
		spans.add(span2);
		List<Span> norm = SpanUtils.normalizeSpans(spans);
		assertEquals(1, norm.size());
		Span mergeSpan = norm.get(0);
		assertEquals(3, mergeSpan.getSpanStart());
		assertEquals(10, mergeSpan.getSpanEnd());
	}

	@Test
	public void testSpan4_2() throws Exception {
		Span span = new Span(3, 5);
		Span span2 = new Span(7, 10);
		List<Span> spans = new LinkedList<Span>();
		spans.add(span);
		spans.add(span2);
		List<Span> norm = SpanUtils.normalizeSpans(spans);
		assertEquals(2, norm.size());
		Span mergeSpan = norm.get(0);
		assertEquals(3, mergeSpan.getSpanStart());
		assertEquals(5, mergeSpan.getSpanEnd());
		mergeSpan = norm.get(1);
		assertEquals(7, mergeSpan.getSpanStart());
		assertEquals(10, mergeSpan.getSpanEnd());
	}

	@Test
	public void testSpan4_3() throws Exception {
		Span span2 = new Span(3, 6);
		Span span = new Span(5, 10);
		List<Span> spans = new LinkedList<Span>();
		spans.add(span);
		spans.add(span2);
		List<Span> norm = SpanUtils.normalizeSpans(spans);
		assertEquals(1, norm.size());
		Span mergeSpan = norm.get(0);
		assertEquals(3, mergeSpan.getSpanStart());
		assertEquals(10, mergeSpan.getSpanEnd());
	}

	@Test
	public void testSpan4_4() throws Exception {
		Span span2 = new Span(3, 5);
		Span span = new Span(7, 10);
		List<Span> spans = new LinkedList<Span>();
		spans.add(span);
		spans.add(span2);
		List<Span> norm = SpanUtils.normalizeSpans(spans);
		assertEquals(2, norm.size());
		Span mergeSpan = norm.get(0);
		assertEquals(3, mergeSpan.getSpanStart());
		assertEquals(5, mergeSpan.getSpanEnd());
		mergeSpan = norm.get(1);
		assertEquals(7, mergeSpan.getSpanStart());
		assertEquals(10, mergeSpan.getSpanEnd());
	}

	@Test
	public void testSpan4_5() throws Exception {
		Span span = new Span(3, 6);
		Span span2 = new Span(5, 10);
		List<Span> spans = new LinkedList<Span>();
		spans.add(span);
		spans.add(span2);
		List<Span> norm = SpanUtils.normalizeSpans(spans);
		assertEquals(1, norm.size());
		Span mergeSpan = norm.get(0);
		assertEquals(3, mergeSpan.getSpanStart());
		assertEquals(10, mergeSpan.getSpanEnd());
	}

	@Test
	public void testSpan4_6() throws Exception {
		Span span = new Span(3, 5);
		Span span2 = new Span(7, 10);
		List<Span> spans = new LinkedList<Span>();
		spans.add(span);
		spans.add(span2);
		List<Span> norm = SpanUtils.normalizeSpans(spans);
		assertEquals(2, norm.size());
		Span mergeSpan = norm.get(0);
		assertEquals(3, mergeSpan.getSpanStart());
		assertEquals(5, mergeSpan.getSpanEnd());
		mergeSpan = norm.get(1);
		assertEquals(7, mergeSpan.getSpanStart());
		assertEquals(10, mergeSpan.getSpanEnd());
	}

	@Test
	public void testSpan5_1() throws Exception {
		Span span = new Span(3, 5);
		Span span2 = new Span(7, 10);
		Span span3 = new Span(4, 8);
		List<Span> spans = new LinkedList<Span>();
		spans.add(span);
		spans.add(span2);
		spans.add(span3);
		List<Span> norm = SpanUtils.normalizeSpans(spans);
		assertEquals(1, norm.size());
		Span mergeSpan = norm.get(0);
		assertEquals(3, mergeSpan.getSpanStart());
		assertEquals(10, mergeSpan.getSpanEnd());
	}

	@Test
	public void testSpan5_2() throws Exception {
		Span span = new Span(3, 5);
		Span span2 = new Span(2, 10);
		Span span3 = new Span(4, 8);
		List<Span> spans = new LinkedList<Span>();
		spans.add(span);
		spans.add(span2);
		spans.add(span3);
		List<Span> norm = SpanUtils.normalizeSpans(spans);
		assertEquals(1, norm.size());
		Span mergeSpan = norm.get(0);
		assertEquals(2, mergeSpan.getSpanStart());
		assertEquals(10, mergeSpan.getSpanEnd());
	}

	@Test
	public void testSpan6_1() throws Exception {
		Span span = new Span(13, 15);
		Span span2 = new Span(3, 5);
		Span span3 = new Span(7, 10);
		List<Span> spans = new LinkedList<Span>();
		spans.add(span);
		spans.add(span2);
		spans.add(span3);
		List<Span> norm = SpanUtils.normalizeSpans(spans);
		assertEquals(3, norm.size());
		Span mergeSpan = norm.get(0);
		assertEquals(3, mergeSpan.getSpanStart());
		assertEquals(5, mergeSpan.getSpanEnd());
		mergeSpan = norm.get(1);
		assertEquals(7, mergeSpan.getSpanStart());
		assertEquals(10, mergeSpan.getSpanEnd());
		mergeSpan = norm.get(2);
		assertEquals(13, mergeSpan.getSpanStart());
		assertEquals(15, mergeSpan.getSpanEnd());
	}

}

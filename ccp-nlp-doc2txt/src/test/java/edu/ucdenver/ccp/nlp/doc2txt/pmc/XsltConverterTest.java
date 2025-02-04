package edu.ucdenver.ccp.nlp.doc2txt.pmc;

/*
 * #%L
 * Colorado Computational Pharmacology's nlp module
 * %%
 * Copyright (C) 2012 - 2017 Regents of the University of Colorado
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

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileComparisonUtil;
import edu.ucdenver.ccp.common.file.FileComparisonUtil.ColumnOrder;
import edu.ucdenver.ccp.common.file.FileComparisonUtil.LineOrder;
import edu.ucdenver.ccp.common.file.FileComparisonUtil.LineTrim;
import edu.ucdenver.ccp.common.file.FileComparisonUtil.ShowWhiteSpace;
import edu.ucdenver.ccp.common.io.ClassPathUtil;
import edu.ucdenver.ccp.nlp.doc2txt.XslUtil;

public class XsltConverterTest {

	@Ignore("There are some whitespace issues that arise with the XML parsing when using Java > 8. This test fails due to the whitespace changes.")
	@Test
	public void testPmcXslt() throws SAXException, IOException, ParserConfigurationException, TransformerException {
		InputStream xmlStream = this.getClass().getResourceAsStream("/sample_pmc.xml");
		InputStream styleSheetStream = PmcXslLocator.getPmcXslStream();
		String elementName = "article";
		List<String> ccpXmlLines = Arrays
				.asList(XslUtil.applyXslt(styleSheetStream, xmlStream, elementName).split("\\n"));
		List<String> expectedCcpXmlLines = Arrays.asList(ClassPathUtil
				.getContentsFromClasspathResource(getClass(), "/sample_ccp.xml", CharacterEncoding.UTF_8).split("\\n"));
		assertTrue(FileComparisonUtil.hasExpectedLines(ccpXmlLines, expectedCcpXmlLines, null, LineOrder.AS_IN_FILE,
				ColumnOrder.AS_IN_FILE, LineTrim.OFF, ShowWhiteSpace.ON));
	}
}

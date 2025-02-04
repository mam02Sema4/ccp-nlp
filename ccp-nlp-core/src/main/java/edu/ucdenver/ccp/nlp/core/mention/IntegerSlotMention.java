package edu.ucdenver.ccp.nlp.core.mention;

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

import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public abstract class IntegerSlotMention extends PrimitiveSlotMention<Integer> {
	private static Logger logger = LogManager.getLogger(IntegerSlotMention.class);

	public IntegerSlotMention(Object... wrappedObjectPlusGlobalVars) {
		super(wrappedObjectPlusGlobalVars);
	}

	@Override
	public String getStringRepresentation(int indentLevel, boolean showReferencingAnnotationInfo) {
		List<Integer> slotValuesList = Collections.list(Collections.enumeration(this.getSlotValues()));
		try {
			Collections.sort(slotValuesList);
		} catch (ClassCastException cce) {
			System.err.println("CAUGHT CCE SLOT VALUES: " + slotValuesList.toString());
		}
		return getStringRepresentation(indentLevel, slotValuesList);
	}

	@Override
	public void addSlotValueAsString(String slotValue) {
		try {
			Integer integerSlotValue = Integer.parseInt(slotValue.trim());
			addSlotValue(integerSlotValue);
		} catch (Exception e) {
			logger.error("Attempting to add slot value that is not an Integer (" + slotValue
					+ ") to an IntegerSlotValue. This value has not been added to the mention hierarchy.");
		}
	}

}

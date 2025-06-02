/*
 * Copyright 2019 B2i Healthcare, https://b2ihealthcare.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.rf2.sctid;

/**
 * Implementation of the Verhoeff algorithm.
 *
 * @since 0.1
 */
public class RF2VerhoeffCheck {
	
	/**
	 * Represents the multiplication table.
	 */
	private static final char[][] D_TABLE = new char[][] {
			{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 },
			{ 1, 2, 3, 4, 0, 6, 7, 8, 9, 5 },
			{ 2, 3, 4, 0, 1, 7, 8, 9, 5, 6 },
			{ 3, 4, 0, 1, 2, 8, 9, 5, 6, 7 },
			{ 4, 0, 1, 2, 3, 9, 5, 6, 7, 8 },
			{ 5, 9, 8, 7, 6, 0, 4, 3, 2, 1 },
			{ 6, 5, 9, 8, 7, 1, 0, 4, 3, 2 },
			{ 7, 6, 5, 9, 8, 2, 1, 0, 4, 3 },
			{ 8, 7, 6, 5, 9, 3, 2, 1, 0, 4 },
			{ 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 } };
	
	/**
	 * Represents the permutation table.
	 */
	private static final char[][] P_TABLE = new char[][] {
			{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 },
			{ 1, 5, 7, 6, 2, 8, 3, 0, 9, 4 },
			{ 5, 8, 0, 3, 7, 9, 6, 1, 4, 2 },
			{ 8, 9, 1, 6, 0, 4, 3, 5, 2, 7 },
			{ 9, 4, 5, 3, 1, 2, 6, 8, 7, 0 },
			{ 4, 2, 8, 6, 5, 7, 3, 9, 0, 1 },
			{ 2, 7, 9, 3, 8, 0, 6, 4, 1, 5 },
			{ 7, 0, 4, 6, 9, 1, 3, 2, 5, 8 } };
	
	/**
	 * Represents the inverse table.
	 */
	private static final char[] INV_TABLE = new char[] { 0, 4, 3, 2, 1, 5, 6, 7, 8, 9 };

	/**
	 * Calculates the checksum. Calls <tt>calculateChecksum(code, 0, code.length(), includeCheckDigit)</tt>.
	 * @param code the readable sequence of characters to be checked.
	 * @param includeCheckDigit true if the check digit should be validated.
	 * @return the proper value to use as a check digit.
	 */
	public static char calculateChecksum(final CharSequence code, final boolean includeCheckDigit) {
		return calculateChecksum(code, 0, code.length(), includeCheckDigit);
	}

	/**
	 * Calculates the checksum. Calls <tt>calculateChecksum(code, start, code.length(), includeCheckDigit)</tt>.
	 * @param code the readable sequence of characters to be checked.
	 * @param start offset.
	 * @param includeCheckDigit true if the check digit should be validated.
	 * @return the proper value to use as a check digit.
	 */
	public static char calculateChecksum(final CharSequence code, final int start, final boolean includeCheckDigit) {
		return calculateChecksum(code, start, code.length(), includeCheckDigit);
	}

	/**
	 * Calculates the checksum.
	 * @param code the readable sequence of characters to be checked.
	 * @param start offset.
	 * @param end the position of the last character to be checked.
	 * @param includeCheckDigit true if the check digit should be validated.
	 * @return the proper value to use as a check digit.
	 */
	public static char calculateChecksum(final CharSequence code, final int start, final int end, final boolean includeCheckDigit) {
		
		//initialize the checksum to zero.
		char checksum = 0;
		
		for (int i = start; i < end; i++) {
			
			int pos = end - (i + 1);
			final int num = Character.getNumericValue(code.charAt(pos));
			
			if (num < 0 || num > 9) {
				throw new IllegalArgumentException(String.format("Invalid Character '%s' at position %d in %s", code.charAt(pos), pos, code));
			}
			
			pos = includeCheckDigit ? i : i + 1;
			checksum = D_TABLE[checksum][P_TABLE[pos % 8][num]];
		}
		return Character.forDigit(INV_TABLE[checksum], 10);
	}
}
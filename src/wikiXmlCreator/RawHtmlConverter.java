/*
    Wiki XML Creator
    Copyright (c) 2009 Kim Hauritz <kim.hauritz@gmail.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package wikiXmlCreator;

import java.io.InputStream;

public class RawHtmlConverter extends Converter {

	public void load(InputStream is) {
		try {
			// TODO Load HTML document
		} catch (Exception e) {
			System.out.println("PdfToHtmlConverter.load() exception");
			e.printStackTrace();
		}
	}

	public void convert() {
		if (splitBy == PdfToHtmlXMLConverter.SPLIT_BY_CHAPTER
				&& (ChapterRegX == null || testRegX(false) == false)) {
			if (ChapterRegX == null)
				System.out
						.println("Error, You are trying to split by chapters: Without defining a chapter.\nPlease define a chapter using the -chapter argument");
			else
				System.out
						.println("Error, You are trying to split by chapters: No chapters found in the document.\nUse -testregx to debug.");

			return;
		}

	}

	public boolean testRegX(boolean print) {
		if (print) {
			System.out.println("Test of regular expression:");
			System.out
					.println("-------------------------------------------------------------");

			if (ChapterRegX == null) {
				System.out.println("Error: No regular expression defined.");
			}
		}

		int matches = 0;

		if (ChapterRegX != null) {
			// TODO Add code to check the regular expression
		}
		if (print) {
			System.out
					.println("-------------------------------------------------------------");
			System.out.println(matches + " matches.");
		}
		if (matches > 0)
			return true;

		return false;
	}

}

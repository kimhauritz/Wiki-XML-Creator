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

import java.io.*;

public class Main {

	private static PdfToHtmlXMLConverter pthconv  = new PdfToHtmlXMLConverter();

	public static void main(String[] args) throws java.io.IOException,
			java.io.FileNotFoundException {
		InputStream is;

		if (args.length == 1)
			is = new FileInputStream(args[0]);
		else
			is = System.in;

		System.out.println("Args len: " + args.length);

		if (is != null) {
			try {
				System.out.println("starting PdfToHtmlXMLConverter");
				pthconv.load(is);
				pthconv.setSkipLinesTop(1);
				pthconv.setSkipLinesBottom(0);
				pthconv.setChapterRegX("(Chapter\\xa0.*)|Epilogue");
				pthconv.setDropLinks(true);
				pthconv.setSplitBy(PdfToHtmlXMLConverter.SPLIT_BY_CHAPTER);
				pthconv.setSplitAfterNumPages(5);

				pthconv.convert();
				//pthconv.testRegX();
				
				pthconv.PrintAllPages();
			} catch (Exception e) {
				System.out.println("Main exception");
				e.printStackTrace();
			}
		}

	}

}

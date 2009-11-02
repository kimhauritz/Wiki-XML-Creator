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
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.util.regex.*;
import java.util.*;

public class PdfToHtmlXMLConverter {

	public static int SPLIT_BY_PAGE = 0;
	public static int SPLIT_BY_CHAPTER = 1;

	private Document XmlDoc;
	private int SkipLinesTop = 0;
	private int SkipLinesBottom = 0;
	private Pattern ChapterRegX = null;
	private boolean dropLinks = false;
	private int splitBy = PdfToHtmlXMLConverter.SPLIT_BY_PAGE;
	private int splitAfterNumPages = 1;

	private ArrayList<WikiPage> wikiPageList;

	public PdfToHtmlXMLConverter() {
		wikiPageList = new ArrayList<WikiPage>();
	}

	public void setSkipLinesTop(int skip) {
		SkipLinesTop = skip;
	}

	public void setSkipLinesBottom(int skip) {
		SkipLinesBottom = skip;
	}

	public void setChapterRegX(String reg) {
		ChapterRegX = Pattern.compile(reg);
	}

	public void setDropLinks(boolean d) {
		dropLinks = d;
	}

	public void setSplitBy(int s) {
		splitBy = s;
	}

	public void setSplitAfterNumPages(int num) {
		// minimum one page
		if (num > 0)
			splitAfterNumPages = num;
	}

	public void convert() {
		if (splitBy == PdfToHtmlXMLConverter.SPLIT_BY_CHAPTER
				&& (ChapterRegX == null || testRegX(false) == false)) {
			if (ChapterRegX == null)
				System.out
						.println("Error: You are trying to split by chapters, Without defining a chapter.\nPlease define a chapter using the -chapter argument");
			else
				System.out
						.println("Error: You are trying to split by chapters. No chapters found in the document.\nUse -testregx to debug.");

			return;
		}
		// get the root element
		Element docEle = XmlDoc.getDocumentElement();

		WikiPage currentWikiPage = new WikiPage();
		int LastPageNum = 0;
		int ChapterNum = 0;
		int i = 0;
		// get a nodelist of elements
		NodeList nlPage = docEle.getElementsByTagName("page");
		if (nlPage != null && nlPage.getLength() > 0) {
			for (i = 0; i < nlPage.getLength(); i++) {

				// Check if it is time to start a new wiki page
				if (splitBy == PdfToHtmlXMLConverter.SPLIT_BY_PAGE
						&& (i + 1) % splitAfterNumPages == 0) {
					if (splitAfterNumPages > 1) {
						LastPageNum = ((i + 2) - splitAfterNumPages);
						// Create title in the form of TITLE_Page_X_to_Y
						currentWikiPage.setTitle("_Page_" + LastPageNum
								+ "_to_" + (i + 1));
					} else {
						LastPageNum = (i + 1);
						// Create title in the form of TITLE_Page_X
						currentWikiPage.setTitle("_Page_" + (i + 1));
					}

					wikiPageList.add(currentWikiPage);
					currentWikiPage = new WikiPage();
				}

				Element elPage = (Element) nlPage.item(i);
				// DEBUG
				// System.out.println("Node: page ----------------------");
				// System.out.println(el.getTextContent());

				// Get the text elements in the page
				NodeList nlText = elPage.getElementsByTagName("text");
				int lastLeftIndent = 0;
				int curLeftIndent = 0;

				int lastTopPos = 0;
				int curTopPos = 0;
				int lastTopDif = 0;

				String currentTextLine = "";

				// Traverse text elements in a page
				if (nlText != null && nlText.getLength() > 0) {
					for (int j = SkipLinesTop; j < nlText.getLength()
							- SkipLinesBottom; j++) {

						Element elText = (Element) nlText.item(j);

						if (dropLinks) {
							NodeList nl = elText.getElementsByTagName("a");
							if (nl.getLength() > 0)
								continue;
						}

						curLeftIndent = Integer.parseInt(elText
								.getAttribute("left"));
						curTopPos = Integer
								.parseInt(elText.getAttribute("top"));

						// Check if a new chapter is encountered and then start
						// a new
						if (ChapterRegX != null
								&& ChapterRegX.matcher(elText.getTextContent())
										.matches()) {
							// DEBUG
							//System.out.print(elText.getTextContent() + "\n\n");
							if (splitBy == PdfToHtmlXMLConverter.SPLIT_BY_CHAPTER
									&& ChapterNum > 0) {
								// Create title in the form of TITLE_Page_X
								currentWikiPage.setTitle("_Chapter_"
										+ (ChapterNum++));

								wikiPageList.add(currentWikiPage);
								currentWikiPage = new WikiPage();

							} else if (splitBy == PdfToHtmlXMLConverter.SPLIT_BY_CHAPTER
									&& ChapterNum == 0) {
								// Add preface and other stuff to chapter 1
								ChapterNum++;
							}
							currentTextLine = elText.getTextContent() + "\n\n";
						} else {
							// DEBUG
							// System.out.print(elText.getTextContent() + "" );

							currentTextLine = elText.getTextContent();
						}

						// System.out.print(curLeftIndent);
						// By comparing the indentation we find if a new
						// paragraph is started
						if ((lastLeftIndent < curLeftIndent && lastLeftIndent != 0)) {
							// DEBUG
							// System.out.print("\n\n");
							currentTextLine += "\n\n";
							// System.out.print("\n  Indent\n");
						}
						// Compare the distance between lines and add a newline
						// when it changes, this only happens when there is no
						// indentation
						else if ((lastTopDif < (curTopPos - lastTopPos) && lastTopDif != 0)) {
							// DEBUG
							// System.out.print("\n");
							currentTextLine += "\n";
							// System.out.print("\n  Top\n");

							/**/
							// 1
							/*
							 * / //2 /*
							 */
						}

						// System.out.print(curTopPos - lastTopPos);
						// System.out.print(" " + curTopPos);
						// System.out.print( " " + elText.getTextContent() +
						// "\n");

						currentWikiPage.addText(currentTextLine);

						lastLeftIndent = curLeftIndent;
						lastTopDif = (curTopPos - lastTopPos);
						lastTopPos = curTopPos;
						// DEBUG
						// System.out.println("Node: text ----------------------");
						// System.out.print(elText.getTextContent());
					}
				}

			}
		}

		if (splitBy == PdfToHtmlXMLConverter.SPLIT_BY_PAGE
				&& splitAfterNumPages > 1) {
			// Create title in the form of TITLE_Page_X_to_Y
			currentWikiPage.setTitle("_Page_" + LastPageNum + "_to_" + i);
		} else {
			// Create title in the form of TITLE_Page_X
			currentWikiPage.setTitle("_Page_" + i);
		}

		if (splitBy == PdfToHtmlXMLConverter.SPLIT_BY_CHAPTER) {
			// Create title in the form of TITLE_Page_X
			currentWikiPage.setTitle("_Chapter_" + ChapterNum);

		}

		// Add the last element to the list
		wikiPageList.add(currentWikiPage);

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
			// get the root element
			Element docEle = XmlDoc.getDocumentElement();

			// get a nodelist of elements
			NodeList nlPage = docEle.getElementsByTagName("page");
			if (nlPage != null && nlPage.getLength() > 0) {
				for (int i = 0; i < nlPage.getLength(); i++) {

					Element elPage = (Element) nlPage.item(i);
					// Get the text elements in the page
					NodeList nlText = elPage.getElementsByTagName("text");

					// Traverse text elements in a page
					if (nlText != null && nlText.getLength() > 0) {
						for (int j = 0; j < nlText.getLength(); j++) {

							Element elText = (Element) nlText.item(j);

							// Check if a new chapter is encountered and then
							// start a new
							if (ChapterRegX.matcher(elText.getTextContent())
									.matches()) {
								// DEBUG
								if (print) {
									System.out.println(elText.getTextContent());
								}
								matches++;
							}

						}
					}

				}
			}
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

	public void PrintAllPages() {
		ListIterator listIterator = wikiPageList.listIterator();

		while (listIterator.hasNext()) {
			WikiPage p = (WikiPage) listIterator.next();

			System.out.println(p.toXML());
		}

	}

	public void load(InputStream is) {
		try {
			XmlDoc = getDocument(is);
		} catch (Exception e) {
			System.out.println("PdfToHtmlXMLConverter.load() exception");
			e.printStackTrace();
		}
	}

	private Document getDocument(InputStream is) throws Exception {

		// Step 1: create a DocumentBuilderFactory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		// Step 2: create a DocumentBuilder
		DocumentBuilder db = dbf.newDocumentBuilder();

		// Step 3: parse the input file to get a Document object
		Document doc = db.parse(is);
		return doc;
	}

}

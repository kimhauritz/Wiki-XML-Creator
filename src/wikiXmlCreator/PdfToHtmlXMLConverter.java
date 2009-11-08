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

public class PdfToHtmlXMLConverter extends Converter {

	public PdfToHtmlXMLConverter() {
		super();
	}

	protected Document XmlDoc;

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
						currentWikiPage.setTitle(DocumentTitle + " Page "
								+ LastPageNum + " to " + (i + 1));
					} else {
						LastPageNum = (i + 1);
						// Create title in the form of TITLE_Page_X
						currentWikiPage.setTitle(DocumentTitle + " Page "
								+ (i + 1));
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

						// Skip empty tags
						if (elText.getTextContent().trim() == "")
							continue;

						if (dropLinks || abbyyFix) {
							NodeList nlA = elText.getElementsByTagName("a");

							// abbyyFix drop all nodes containing an anchor and
							// a bold tag, and keeps the text from anchors
							// without a bold tag
							if (nlA.getLength() > 0) {
								if (abbyyFix) {
									Element elA = (Element) nlA.item(0);
									NodeList nlAB = elA
											.getElementsByTagName("b");

									if (nlAB.getLength() > 0)
										continue;
								} else
									continue;
							}
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

							if (splitBy == PdfToHtmlXMLConverter.SPLIT_BY_CHAPTER
									&& ChapterNum > 0) {
								// Create title in the form of TITLE_Page_X
								currentWikiPage.setTitle(DocumentTitle
										+ " Chapter " + (ChapterNum++));

								wikiPageList.add(currentWikiPage);
								currentWikiPage = new WikiPage();

							} else if (splitBy == PdfToHtmlXMLConverter.SPLIT_BY_CHAPTER
									&& ChapterNum == 0) {
								// Add preface and other stuff to chapter 1
								ChapterNum++;
							}
							currentTextLine = " \n==" + elText.getTextContent()
									+ "==\n\n";

							// Only add chapter text when we split by chapter
							// and when we split by 1 page
							if (!(splitBy == PdfToHtmlXMLConverter.SPLIT_BY_PAGE && splitAfterNumPages > 1))
								currentWikiPage.setChapterText(elText
										.getTextContent());
						} else {

							// Check for bold tag
							NodeList nlB = elText.getElementsByTagName("b");
							// Check for italic tag
							NodeList nlI = elText.getElementsByTagName("i");

							// Bold and italic
							if (nlB.getLength() > 0 && nlI.getLength() > 0) {
								currentTextLine += "'''''"
										+ elText.getTextContent() + "''''' ";
								// Bold
							} else if (nlB.getLength() > 0)
								currentTextLine += "'''"
										+ elText.getTextContent() + "''' ";
							// Italic
							else if (nlI.getLength() > 0)
								currentTextLine += "''"
										+ elText.getTextContent() + "'' ";
							// PLAIN
							else
								currentTextLine += elText.getTextContent()
										+ " ";

						}

						// By comparing the indentation we find if a new
						// paragraph is started
						if ((lastLeftIndent < curLeftIndent && lastLeftIndent != 0)) {
							currentTextLine += "\n\n";
						}
						// Compare the distance between lines and add a newline
						// when it changes, this only happens when there is no
						// indentation
						else if ((lastTopDif < (curTopPos - lastTopPos) && lastTopDif != 0)) {
							currentTextLine += "\n";
						}

						currentWikiPage.addText(currentTextLine);
						currentTextLine = "";
						lastLeftIndent = curLeftIndent;
						lastTopDif = (curTopPos - lastTopPos);
						lastTopPos = curTopPos;
					}
				}

			}
		}

		if (splitBy == PdfToHtmlXMLConverter.SPLIT_BY_PAGE
				&& splitAfterNumPages > 1) {
			// Create title in the form of TITLE_Page_X_to_Y
			currentWikiPage.setTitle(DocumentTitle + " Page " + LastPageNum
					+ "_to_" + i);
		} else {
			// Create title in the form of TITLE_Page_X
			currentWikiPage.setTitle(DocumentTitle + " Page " + i);
		}

		if (splitBy == PdfToHtmlXMLConverter.SPLIT_BY_CHAPTER) {
			// Create title in the form of TITLE_Page_X
			currentWikiPage.setTitle(DocumentTitle + " Chapter " + ChapterNum);

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

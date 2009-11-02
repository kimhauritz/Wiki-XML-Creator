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

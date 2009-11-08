package wikiXmlCreator;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;
import java.util.regex.Pattern;

public class Converter {
	
	public static int SPLIT_BY_PAGE = 0;
	public static int SPLIT_BY_CHAPTER = 1;

	protected int SkipLinesTop = 0;
	protected int SkipLinesBottom = 0;
	protected Pattern ChapterRegX = null;
	protected boolean dropLinks = false;
	protected boolean noMainPage = false;
	protected int splitBy = PdfToHtmlXMLConverter.SPLIT_BY_PAGE;
	protected int splitAfterNumPages = 1;
	protected String DocumentTitle = "";
	protected boolean abbyyFix = false;
	
	protected ArrayList<WikiPage> wikiPageList;
	
	

	public Converter() {
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

	public void setNoMainPage(boolean nmp) {
		noMainPage = nmp;
	}
	
	public void setSplitBy(int s) {
		splitBy = s;
	}

	public void setSplitAfterNumPages(int num) {
		// minimum one page
		if (num > 0)
			splitAfterNumPages = num;
	}
	
	public void setAbbyyFix(boolean d) {
		abbyyFix = d;
	}
	
	public void setDocumentTitle(String t) {
		DocumentTitle = t;
	}
	
	public void PrintAllPages() {
		
		//Drop main page.
		if(!noMainPage)
			createMainPage();
		
		Random generator = new Random();
		
		ListIterator listIterator = wikiPageList.listIterator();

		int pgId, revId;
		int Lower_Bound = 265795;
		int Upper_Bound = 310689540;
		while (listIterator.hasNext()) {
			WikiPage p = (WikiPage) listIterator.next();
			
			pgId = (int) (Lower_Bound + generator.nextDouble() * ( Upper_Bound - Lower_Bound) );
			revId = (int) (Lower_Bound + generator.nextDouble() * ( Upper_Bound - Lower_Bound) );
			
			p.setIds(pgId, revId);
			
			System.out.println(p.toXML());
		}

	}
	
	protected void createMainPage()
	{
		WikiPage mainPage = new WikiPage(DocumentTitle);
		mainPage.addText("\n\n'''Index'''\n\n");
		mainPage.setCommentText("MainPg");
		
		for(int i = 0; i < wikiPageList.size() ; i++)
		{
			if(i == 0)
			{
				wikiPageList.get(i).setNextPageRef(wikiPageList.get(i+1).getTitle());
				mainPage.setNextPageRef(wikiPageList.get(i).getTitle());
				mainPage.setTopPageRef(DocumentTitle);
			}
			else if( i == wikiPageList.size()-1)
			{
				wikiPageList.get(i).setPrevPageRef(wikiPageList.get(i-1).getTitle());
			}
			else
			{
				wikiPageList.get(i).setPrevPageRef(wikiPageList.get(i-1).getTitle());
				wikiPageList.get(i).setNextPageRef(wikiPageList.get(i+1).getTitle());
			}
			if(wikiPageList.get(i).getChapterText() != "")
				mainPage.addText("* [[" + wikiPageList.get(i).getTitle() + "|" + wikiPageList.get(i).getChapterText() + "]]\n");
			else
				mainPage.addText("* [[" + wikiPageList.get(i).getTitle() + "]]\n");
				
			wikiPageList.get(i).setTopPageRef(DocumentTitle);
		}
		
		wikiPageList.add(mainPage);
	}




}

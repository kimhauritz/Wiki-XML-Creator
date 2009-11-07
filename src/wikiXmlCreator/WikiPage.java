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

import java.util.*;
/**
 * 
 * @author Kim Hauritz
 * This class describes a MediaWiki page in the XML format used when exporting wiki's
 * see http://meta.wikimedia.org/wiki/Help:Export for more information.
 * 
 * Example:
 *     <page>
 *     <title>Page title</title>
 *     <restrictions>edit=sysop:move=sysop</restrictions>
 *     <revision>
 *       <timestamp>2001-01-15T13:15:00Z</timestamp>
 *       <contributor><username>Foobar</username></contributor>
 *       <comment>I have just one thing to say!</comment>
 *       <text>A bunch of [[text]] here.</text>
 *       <minor />
 *     </revision>
 *     <revision>
 *       <timestamp>2001-01-15T13:10:27Z</timestamp>
 *       <contributor><ip>10.0.0.2</ip></contributor>
 *       <comment>new!</comment>
 *       <text>An earlier [[revision]].</text>
 *     </revision>
 *   </page>
 *
 *
 */
public class WikiPage {

	protected String title;
	protected String chapterText="";
	protected ArrayList<String> textList;
	
	// Reference to the wiki page with a list of all pages
	protected String topPageRef = "";
	protected String nextPageRef = "";
	protected String prevPageRef = "";
	protected int pgId = 0;
	protected int revId = 0;
	
	public WikiPage()
	{
		title = "";
		textList = new ArrayList<String>();
	}
	
	public WikiPage(String t)
	{
		title = t;	
		textList = new ArrayList<String>();
	}
	
	public void setIds(int p, int r)
	{
		pgId = p;
		revId = r;	
	}
	
	public void addText(String t)
	{
		textList.add(t);
	}
	
	public void setTitle(String t)
	{
		title = t;
	}
	
	public void setChapterText(String t)
	{
		chapterText = t;
	}
	
	public String getChapterText()
	{
		return chapterText;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public void setTopPageRef(String t)
	{
		topPageRef = t;
	}
	
	public void setPrevPageRef(String t)
	{
		prevPageRef = t;
	}
	public void setNextPageRef(String t)
	{
		nextPageRef = t;
	}
	public String toXML()
	{
		
		String textStr  = "";
		
		ListIterator listIterator = textList.listIterator();
		
		while(listIterator.hasNext())
			textStr += listIterator.next();
		
		if(topPageRef != "" && prevPageRef != "" && nextPageRef != "")
		{
			textStr += "\n\n'''Navigation'''\n\n" + 
			"[[" + prevPageRef + "|Prev]]  -  [[" + topPageRef + "|Main]]  -  [[" + nextPageRef + "|Next]]\n\n"; 
		}
		else if(topPageRef != "" && prevPageRef != "")
		{
			textStr += "\n\n'''Navigation'''\n\n" + 
			"[[" + prevPageRef + "|Prev]]  -  [[" + topPageRef + "|Main]]\n\n"; 
		}
		else if(topPageRef != "" && nextPageRef != "")
		{
			textStr += "\n\n'''Navigation'''\n\n" + 
			"[[" + topPageRef + "|Main]]  -  [[" + nextPageRef + "|Next]]\n\n"; 
		}

		
		String res = "<page>\n<title>" + title + "</title>\n" + 
	    "<id>" + pgId + "</id>" +
	    "<revision><id>" + revId + "</id>" +
	      "<timestamp>2009-08-29T09:34:16Z</timestamp>" +
	      "<contributor>" +
	       "<username>Chreod</username>" +
	        "<id>7335409</id>" +
	      "</contributor>" +
	      "<minor/>" +
	      "<comment>mainly grammatical changes</comment>" +
	      
//		"<revision><timestamp>2001-01-15T13:15:00Z</timestamp><contributor><username>WikiXMLCreator</username></contributor>" +
//		"<comment></comment>\n" +
		"<text xml:space=\"preserve\">\n" + textStr + "\n</text></revision>\n</page>";
		
		return res;

	}
}

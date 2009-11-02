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
	protected ArrayList<String> textList;
	
	// Reference to the wiki page with a list of all pages
	protected String topPageRef;
	protected String nextPageRef;
	protected String prevPageRef;
	
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
	
	public void addText(String t)
	{
		textList.add(t);
	}
	
	public void setTitle(String t)
	{
		title = t;
	}
	
	public String toXML()
	{
		
		String textStr  = "";
		
		ListIterator listIterator = textList.listIterator();
		
		while(listIterator.hasNext())
			textStr += listIterator.next();
		
		String res = "<page>\n<title>" + title + "</title>\n" + 
		"<revision><timestamp>2001-01-15T13:15:00Z</timestamp><contributor><username>WikiXMLCreator</username></contributor>" +
		"<comment></comment>\n"
		+"<text>\n" + textStr + "\n</text></revision>\n</page>";
		
		return res;

	}
}

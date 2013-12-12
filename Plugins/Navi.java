/*
 * RITZAL Roman
 * STABRAWA Victor
 * 
 * SWE1
 * 
 * Version 1.1
 * 
 * Das Programm ist ein Webserver welcher MultiuserfÃ¤hig ist
 * und Ã¼ber ein dynamisches Pluginsystem verfÃ¼gt
 */

import java.io.*;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Navi implements Plugin 
{
	/**
	 * readMap():
	 * reads an XML-File and parses it into the Hashtable Street_City
	 * which is located in Myserver.java
	 */
	public void readMap() throws Exception, ParserConfigurationException,
    SAXException, IOException
	{
		File osmXmlFile = new File("austria.osm");
	    if (! osmXmlFile.canRead()) {
	      System.out.println("Osm file austria.osm doesn't exist.");
	    }
	    SAXParserFactory parserFactory = SAXParserFactory.newInstance();
	    //parserFactory.setNamespaceAware(true);
	    SAXParser parser = parserFactory.newSAXParser();

	    OsmNamesHandler handler = new OsmNamesHandler();
	    Myserver.naviReading = 1;
	    parser.parse(osmXmlFile, handler);
	    Myserver.naviReading = 0;
	}
	
	private static class OsmNamesHandler extends DefaultHandler 
	{
		
		private final Stack<String> eleStack = new Stack<String>();
		private String[] curNodeValues = new String[2];
		
		OsmNamesHandler()
	    {
	    	curNodeValues[0] = "";
	    	curNodeValues[1] = "";
	    }
		
		private ArrayList<String> cityNames;
		
		@Override
		public void startElement(String uri, String localName, String qName,
		    Attributes attrs) throws SAXException 
		{
			int found = 0;
		    //a tag with parent being node
		    if("tag".equals(qName) && ("node".equals(eleStack.peek()) || "way".equals(eleStack.peek())))
		    {
		    	String key = attrs.getValue("k");
		    	if ("addr:city".equals(key)) 
		        {
		    		curNodeValues[0] = attrs.getValue("v");
		        }
		    	else if ("addr:street".equals(key)) 
		        {
		    		curNodeValues[1] = attrs.getValue("v");
		        }
		    	
		    	//array is complete => add to id_name hashtable
			    if(!curNodeValues[0].equals("") && !curNodeValues[1].equals(""))
			    {
			    	//getting entries from hashtable for this street = curNodeValues[2]
			    	if((cityNames = Myserver.Street_City.get(curNodeValues[1])) == null)
			    	{
			    		cityNames = new ArrayList<String>();
			    		//adding current city to list
				    	cityNames.add(curNodeValues[0]);
			    	}
			    	else
			    	{
			    		for(int i = 0; i < cityNames.size(); i++)
			    		{
			    			if(cityNames.get(i).equals(curNodeValues[0]))
			    			{
			    				//adding current city to list
						    	found = 1;
						    	break;
			    			}
			    		}
			    		if(found==0)
			    		{
			    			cityNames.add(curNodeValues[0]);
			    		}
			    	}
		    		Myserver.Street_City.put(curNodeValues[1], cityNames);
			    }
		    }
		    
		    eleStack.push(qName);
		}

	    @Override
	    public void endElement(String uri, String localName, String qName)
	        throws SAXException 
	    {
	    	eleStack.pop();
	    	
	    	if("node".equals(qName) || "way".equals(qName))
	    	{
	    		//clear nodeValues for next node
		        curNodeValues[0] = ""; //addr:city
		        curNodeValues[1] = ""; //addr:street
	    	}
	    }
	}
	
	
    public String run(String option)
    {
    	ArrayList<String> cityNames = new ArrayList<String>();
    	String content;
    	
    	content = "<a href='?readmap'>Karte neu aufbereiten</a></br>";
		content += "<form method='GET'><input id='streetin' onkeydown=\"if (event.keyCode == 13) { window.location.href='?'+streetin.value; return false; }\"></input></form>";
    	
    	if(Myserver.naviReading == 1)
		{
			content="<p>WARNING: Map is being updated. Please try again later.</p>";
			return content;
		}
    	
    	
    	//no option, just call to Navi page
    	if(option != null)
    	{
    		//read the map into some kind of struct
    		if(option.equals("readmap"))
    		{
    			try {
					readMap();
				} catch (Exception e) {
					e.printStackTrace();
				}
    			content += "Map has been read successfully.";
    		}
    		//option is a street, look for it in map struct
    		else
    		{
    			//handle special characters
    			option = option.replaceAll("%20", " ");
        		option = option.replaceAll("%DF", "ß");
        		option = option.replaceAll("%DC", "Ü");
        		option = option.replaceAll("%FC", "ü");
        		option = option.replaceAll("%C4", "Ä");
        		option = option.replaceAll("%E4", "ä");
        		option = option.replaceAll("%D6", "Ö");
        		option = option.replaceAll("%F6", "ö");
    			
    			if((cityNames = Myserver.Street_City.get(option)) != null)
    			{
    				
    				int size = cityNames.size();
    				
    				System.out.println("Cities found "+size+"cities");
    				content += option + ":<br>";
    				for(int i = 0; i < size; i++)
    				{
    					
    					content += "=> " + cityNames.get(i) + "<br>";
    				}
    			}
    			else
    			{
    				content += "The street "+option+" could not be found.";
    			}
    		}
    	}
    	
        return content;
    }
    
}

/*
 * RITZAL Roman
 * STABRAWA Victor
 * 
 * SWE1
 * 
 * Version 1.1
 * 
 * Das Programm ist ein Webserver welcher Multiuserfähig ist
 * und über ein dynamisches Pluginsystem verfügt
 */

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.FileNameMap;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class StatischeDateien implements Plugin {
    //Konstruktor
    public String run(String option){
    	FileReader fr = null;
        String content = "";
    	File theDir = new File("Files");
    	String filename;
    	String mime;
    	int i;
    	
    	if(option == null){
    		return null;
    	}

    	/*if the directory does not exist, create it*/
    	if (!theDir.exists()) {
    		System.out.println("creating directory: Files");
    	    boolean result = theDir.mkdir();  

    	    if(result) {    
    	    	System.out.println("Plugin Directory Created.");  
    	    }
    	}
    	else
    	{
    		/*some specific data is requested*/
    		if(option!=null)
    		{
    			try {
    				File file = new File(option);
    				mime = checkMimeType(theDir, file);
    				switch(mime)
    				{
    				case "":
    					content = "Fehler MIME nicht gefunden";
    					break;
	    			case "text/plain":
	    			case "text/html":
	    	    		content = convertStrToHTML(mime,file);
	    	    		break;
	    	    	case "image/jpeg":
	    	    		content = "Files/dwight.jpg";
	    	    		break;
	    	    	    		
	    	    	default:
	    	    		content = "Keine Verarbeitung des speziellen MimeTyps moeglich";
	    	    	}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
    		}
    	}
    	if(content == null || content.isEmpty())
    		content = "<p>There are no Files in the Files-Directory yet.</p>";
            
        return content;
    }
    
    public String checkMimeType(File curDir, File file){
    	
    	try {
    		Path path = Paths.get(file.getName());
			String mimeType = Files.probeContentType(path);
    		
	    	if(mimeType == null){
	    		System.out.println("Cant get mime type of image");
	    	}
	    	else{
		    	System.out.println("Mime: " + mimeType);
	    	}
	    	return mimeType;
	    	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
    }
    
    /*converts filestring to an HTML-string that will display the file in the browser*/
    public String convertStrToHTML(String mimeType, File file) throws IOException
    {
    	File curFile = new File("Files/" + file.getName());
    	File curDir = new File(".");
    	String filepath = "";
    	String content = "";
    	
		FileReader fr = new FileReader("Files/" + file.getName());
		BufferedReader br = new BufferedReader(fr);
		
		String line = "";

		while((line = br.readLine()) != null){
			content += line;
		}
    	return content;
    }
}

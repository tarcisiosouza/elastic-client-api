package de.l3s.elasticquery;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AppTest 
    extends TestCase
{
	public static boolean ASC = true;
    public static boolean DESC = false;

    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }
    public void testApp() throws IOException
    {
    	
    	 String query = "In weiten Teilen Norditaliens liefen die Menschen aus ihren Häusern ins Freie. Schulen";
    	 int limit = 1000;
    	 HashMap<String,Article> documents = new HashMap<String,Article>();
    	 new ElasticMain (query, limit,"text");
    	 ElasticMain.setKeywords(query);
		 ElasticMain.run();
		
		 documents = ElasticMain.getResult();
	//	 for(Entry<String, Article> s : documents.entrySet())
//		    {
				
//		    	System.out.print(s.getKey()+" "+s.getValue().getTimestamp()+" "+s.getValue().getUrl()+" ");
//		    	String str = s.getKey()+" "+s.getValue().getText()+"\n--------\n";
//		    	System.out.println (str);
		    	
//		    }
		
		 
//		 ElasticMain.setKeywords("bundestagswahl 2013");
//		 ElasticMain.run();
		 documents = ElasticMain.getResult();
		 System.out.println ("Total documents: "+documents.size());
		 int i = 0;
		 for(Entry<String, Article> s : documents.entrySet())
		    {
			 i++;
			 if (i>50)
				 break;
			/* if (!s.getValue().getText().toString().contains("angela merkel"))
				 continue;
			 */
				 StringBuilder sb = new StringBuilder();
			//	 sb.append("Timestamp: "+s.getValue().getTimestamp()+"\n");
		//		 sb.append("Title: "+ s.getValue().getTitle()+"\n");
				 sb.append(s.getValue().getText());
				 BufferedWriter out = new BufferedWriter
		    		    (new OutputStreamWriter(new FileOutputStream(s.getValue().getTimestamp()+".txt"),"UTF-8"));
		    	//System.out.print(s.getValue().getTimestamp()+" "+s.getValue().getUrl()+" \n");
				 out.write(sb.toString());
				 out.close();
					    	//String str = s.getKey()+" "+s.getValue().getText()+"\n--------\n";
		    //	System.out.println (str);
		    	
		    }
		 
    }
}

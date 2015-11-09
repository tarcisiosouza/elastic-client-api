package de.l3s.elasticquery;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
    	/*
    	 String query = "Angela Merkel";
    	 int limit = 5;
    	 HashMap<String,Article> documents = new HashMap<String,Article>();
    	 new ElasticMain (query, limit);
    	 ElasticMain.setKeywords(query);
		 ElasticMain.run();
		
		 documents = ElasticMain.getResult();
		 for(Entry<String, Article> s : documents.entrySet())
		    {
				
		    	System.out.print(s.getKey()+" "+s.getValue().getTimestamp()+" "+s.getValue().getUrl()+" "+s.getValue().getText());
		    	String str = s.getKey()+" "+s.getValue().getText()+"\n--------\n";
		    	System.out.println (str);
		    	
		    }
		    */
    }
}

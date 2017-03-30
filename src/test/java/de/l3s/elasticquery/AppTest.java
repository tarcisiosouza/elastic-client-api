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
    public AppTest( String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }
    public void testApp() throws IOException
    {
    	
    	 String query = "bundestagswahl 2002";
    	 int limit = 10;
    	 HashMap<String,Article> documents = new HashMap<String,Article>();
    	 new ElasticMain (query, limit,"url");
    	 ElasticMain.setKeywords(query);
		 ElasticMain.run();
		
		 documents = ElasticMain.getResult();

		 System.out.println ("Total documents: "+documents.size());
		 int i = 0;
		 for(Entry<String, Article> s : documents.entrySet())
				System.out.print(s.getValue().getTimestamp()+" "+s.getValue().getUrl()+" \n");
		 
    }
}

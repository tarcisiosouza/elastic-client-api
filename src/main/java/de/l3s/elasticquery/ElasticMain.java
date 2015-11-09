package de.l3s.elasticquery;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import de.l3s.elasticquery.Article;

public class ElasticMain {

	public static boolean ASC = true;
    public static boolean DESC = false;
    private static String keywords;
    private static int limit;
    private static long count;
    private static HashMap<String, Article> result;
    private static String propFileName;
    private static UrlElasticQuery query;
    
    public ElasticMain (String q, int lim) throws IOException
    {
    	result = new HashMap <String,Article>();
    	keywords = q;
    	limit = lim;
    	count = 0;
    	query = new UrlElasticQuery ();	
    }
	public static void main(String[] args) throws IOException 
	{
		propFileName = args[0];
		
		PropertyUtil property = new PropertyUtil (propFileName); 
		String dateFrom = property.getStringProperty("dateFrom");
		String dateTo = property.getStringProperty("dateTo");
		String keywords = property.getStringProperty("keywords");
		int limit = Integer.parseInt(property.getStringProperty("limit"));
		
		
   	 	HashMap<String,Article> documents = new HashMap<String,Article>();
   	 	HashMap<String, Integer> domains = new HashMap<String,Integer>();
   	 	
   	 	new ElasticMain(keywords,limit);
   	 	documents = query.getArticlesFilterDates(dateFrom, dateTo, keywords, limit);
		
	    Map<String, Integer> sortedMapDesc = sortByComparator(domains, DESC);
	    
	    System.out.println ("\nQuery terms:"+keywords);
	    System.out.println ("Index name: "+query.getIndexName()+"\n");
	    System.out.println ("###########<Query Result>###########");
	    System.out.println ("Total articles: "+query.getTotalDocuments());
	    System.out.println ("Total domains: "+query.getDomainCount());
	    	
	    for (Entry<String, Integer> entry : sortedMapDesc.entrySet())
	 	{
	       System.out.println("Domain: " + entry.getKey() + " freq: "+ entry.getValue());
	 	}  
		
		 for(Entry<String, Article> s : documents.entrySet())
		 {
				
		    	System.out.print(s.getKey()+" "+s.getValue().getTimestamp()+" "+s.getValue().getUrl()+" "+s.getValue().getText());
		    	String str = s.getKey()+" "+s.getValue().getText()+"\n--------\n";
		    	System.out.println (str);
		    	
		 }
	
    }
	
	public static String getKeywords() {
		return keywords;
	}
	public static void setKeywords(String key) {
		keywords = key;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int lim) {
		limit = lim;
	}
	
	public static HashMap<String, Article> getResult() {
		return result;
	}

	public static void run ()
	{
		result = query.getDocuments(keywords, limit);
	}

	public static long getCount ()
	{
		count = query.countResponse(keywords);
		return count;
	}
	 private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap, final boolean order)
	 {

	            List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());

	            // Sorting the list based on values
	            Collections.sort(list, new Comparator<Entry<String, Integer>>()
	            {
	                public int compare(Entry<String, Integer> o1,
	                        Entry<String, Integer> o2)
	                {
	                    if (order)
	                    {
	                        return o1.getValue().compareTo(o2.getValue());
	                    }
	                    else
	                    {
	                        return o2.getValue().compareTo(o1.getValue());

	                    }
	                }
	            });

	            // Maintaining insertion order with the help of LinkedList
	            Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
	            for (Entry<String, Integer> entry : list)
	            {
	                sortedMap.put(entry.getKey(), entry.getValue());
	            }

	            return sortedMap;
	}
	
	}

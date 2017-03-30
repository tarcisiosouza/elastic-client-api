package de.l3s.elasticquery;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java.util.Map.Entry;


public class ElasticMain {

	public static boolean ASC = true;
    public static boolean DESC = false;
    private static String keywords;
    private static String field;
    private static int limit;
    private static long count;
    private static Map<Article, Double> result;
    private static String propFileName;
    private static UrlElasticQuery query;
    
    public ElasticMain (String q, int lim, String f) throws IOException
    {
    	result = new HashMap<Article, Double>();
    	keywords = q;
    	limit = lim;
    	field = f;
    	count = 0;
    	query = new UrlElasticQuery ();	
    }
  
	public static String getKeywords() {
		return keywords;
	}
	public static void setKeywords(String key) {
		keywords = key;
	}
	
	public static String getField() {
		return field;
	}
	public static void setField(String field) {
		ElasticMain.field = field;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int lim) {
		limit = lim;
	}
	
	public static Map<Article, Double> getResult() {
		return result;
	}

	public HashMap <String, Integer> getDomains (){
		return query.getDomains();
	}
	
	public ArrayList<String> getArticleText () {
		return query.getArticleText();
	}
	public ArrayList<String> getTotalDocuments (){
		return query.getArrayTotalDoc();
	}
	public static void run () throws IOException
	{
		result = query.getRankedDocuments(keywords, limit, field);
	}

	public HashMap<String, Article> getAllDocuments () throws IOException
	{
		return query.getDocuments(limit);
	}
/*
	public static long getCount (String f)
	{
	//	return (query.countResponse(keywords, f));
		
	}
	*/
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

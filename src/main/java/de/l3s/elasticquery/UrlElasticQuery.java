package de.l3s.elasticquery;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.apache.commons.collections.map.MultiValueMap;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import com.google.common.collect.Multimap;

import de.l3s.elasticquery.Article;
public class UrlElasticQuery  
{
	private Url url;
	private HashMap <String, Url> document;
	private HashMap <String, Integer> domains;
	private ArrayList <String> totalDocuments = new ArrayList <String>();
	private ArrayList <String> articleText = new ArrayList<String> ();	
	private String dateFrom;
	private String dateTo;
	private int capturesCount;
	private int domainCount;
	private int docs = 0;
	private String indexName;
	private static String[] allMatches = new String[1];
	private static String date;
	private static String str;
	private static URL Url;
	public UrlElasticQuery() throws IOException {
		
		document = new HashMap <String,Url>();
		ElasticServer.loadProperties();
	}

/*	
	public List<Map<String, Object>> getAllDocs(){
        int scrollSize = 1000;
        List<Map<String,Object>> esData = new ArrayList<Map<String,Object>>();
        SearchResponse response = null;
        int i = 0;
        while( response == null || response.getHits().hits().length != 0){
            response = client.prepareSearch(indexName)
                    .setTypes(typeName)
                       .setQuery(QueryBuilders.matchAllQuery())
                       .setSize(scrollSize)
                       .setFrom(i * scrollSize)
                    .execute()
                    .actionGet();
            for(SearchHit hit : response.getHits()){
                esData.add(hit.getSource());
            }
            i++;
        }
        return esData;
}
*/
	//Perform a query and get Documents as News Articles
	public HashMap<String, Article> getDocuments (String keywords, int limit, String field) throws MalformedURLException {
		
		domainCount = 0;
		int index = 0;
		capturesCount = 0;
		domains = new HashMap <String,Integer>();
		HashMap<String, Article> articles = new HashMap<String, Article>();
		totalDocuments.clear();
		articleText.clear();
		indexName = ElasticServer.getIndex();
	
		SearchResponse response = ElasticServer.getClient().prepareSearch(ElasticServer.getIndex())
		        .setTypes(ElasticServer.getType())
		        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)		    
		       // .setScroll(new TimeValue(60000))
		         .setQuery(QueryBuilders.matchQuery(field,keywords)).setSize(limit).execute().actionGet();
		      //  .setQuery(QueryBuilders.matchPhraseQuery(field, keywords).minimumShouldMatch("0.4f"))             // Query
		      //  .setSize(limit).execute().actionGet();
		
		//while (limit > 0) {
		
		    for (SearchHit hit : response.getHits().getHits()) {
		    	
		    //	if (limit == 0)
		    	//	break;
		    	Map<String,Object> source=hit.getSource();
		    	Article article = new Article ();
		    	article.setTimestamp("no_ts");
		    	article.setDomain("no_domain");
		    try {	
		    	article.setText(source.get("text").toString());
		    	article.setUrl(source.get("url").toString()); 	
		    	article.setScore(hit.getScore());
		    	article.setDomain(source.get("domain").toString());	 
		    	article.setTimestamp(source.get("ts").toString());
		    } catch (Exception e)
		    {
		    	if (article.getTimestamp().contentEquals("no_ts"))
		    		article.setTimestamp(source.get("timestamp").toString());
		    	if (article.getDomain().contentEquals("no_domain"))
		    		article.setDomain(getDomain(article.getUrl()));
		    }
		    	capturesCount++;
		    	
		    	articles.put(article.getUrl(), article);
		    	
		    	if (domains.containsKey(article.getDomain()))
		    	{
		    		int value = domains.get(article.getDomain());
		    		value += 1;
		    		domains.put(article.getDomain(), value);
		    	}
		    	else
		    	{
		    		domains.put(article.getDomain(), 1);
		    		domainCount += 1;
		    	}
		    	
		    	totalDocuments.add(article.getUrl() + " " + article.getTimestamp() + " " + article.getScore() + " " + index);
		    	articleText.add(article.getText());
		    	index ++;
		    	// limit--;
		    	//response = ElasticServer.getClient().prepareSearchScroll(response.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
		/*    	if (response.getHits().getHits().length == 0) {
					        break;
		    	}*/
		    }
		    
		   // response = ElasticServer.getClient().prepareSearchScroll(response.getScrollId()).execute().actionGet();
		    
		    //Break condition: No hits are returned
		     
	    //	  if (response.getHits().getHits().length == 0) {
			//        break;
			  //  } 
		//}

		return articles;
		
	}
	

	public ArrayList<String> getArticleText ()
	{
		return articleText;
	}
	public void setTotalDocuments(ArrayList<String> totalDocuments) {
		this.totalDocuments = totalDocuments;
	}

	public ArrayList<String> getArrayTotalDoc () {
		return totalDocuments;
	}
	//Perform a query and get Documents as News Articles with filter in dates
	public HashMap<String,Article> getArticlesFilterDates (String dateF, String dateT, String keywords, int limit) {
		
		domainCount = 0;
		domains = new HashMap <String,Integer>();
		dateFrom = DateUtils.parseDate(dateF);
		dateTo = DateUtils.parseDate(dateT);
		HashMap<String,Article> articles = new HashMap<String,Article>();
		
		indexName = ElasticServer.getIndex();
		
		SearchResponse response = ElasticServer.getClient().prepareSearch(ElasticServer.getIndex())
		        .setTypes(ElasticServer.getType())
		        .setSearchType(SearchType.SCAN)		    
		        .setScroll(new TimeValue(0))
		        .setQuery(QueryBuilders.queryStringQuery(keywords))             // Query
		        .setPostFilter(FilterBuilders.rangeFilter("ts").from(dateFrom).to(dateTo))
		        .setSize(limit).execute().actionGet();
		
		while (limit > 0) {
		    for (SearchHit hit : response.getHits().getHits()) {
		    	
		    	if (limit == 0)
		    		break;
		    	Map<String,Object> source=hit.getSource();
		    	Article article = new Article ();
		    	article.setUrl(source.get("url").toString());
		    	article.setDomain(source.get("domain").toString());
		    	article.setText(source.get("text").toString());
		    	article.setTimestamp(source.get("ts").toString());
		    	capturesCount++;
		    	
		    	articles.put(article.getUrl(), article);
		    	
		    	if (domains.containsKey(article.getDomain()))
		    	{
		    		int value = domains.get(article.getDomain());
		    		value += 1;
		    		domains.put(article.getDomain(), value);
		    	}
		    	else
		    	{
		    		domains.put(article.getDomain(), 1);
		    		domainCount += 1;
		    	}
		    	
		    	limit--;
		    }
		    response = ElasticServer.getClient().prepareSearchScroll(response.getScrollId()).setScroll(new TimeValue(0)).execute().actionGet();
		    
		   // response = ElasticServer.getClient().prepareSearchScroll(response.getScrollId()).execute().actionGet();
		    
		    //Break condition: No hits are returned
		    if (response.getHits().getHits().length == 0) {
		        break;
		    }
		}

		return articles;
		
	}
	
	public long countResponse (String keywords,String field)
	{
		long result = 0;
		SearchResponse response = ElasticServer.getClient().prepareSearch(ElasticServer.getIndex())
		        .setTypes(ElasticServer.getType())
		        .setSearchType(SearchType.COUNT)		    
		       // .setScroll(new TimeValue(60000))
		         .setQuery(QueryBuilders.matchQuery(field,keywords)).execute().actionGet();
		
		result = response.getHits().totalHits();
		return result;
	}
	
	public HashMap<String, Url> getDocuments (String dateF, String dateT, String keywords, int limit) {
	
		dateFrom = DateUtils.parseDate(dateF);
		dateTo = DateUtils.parseDate(dateT);
		domainCount = 0;
		domains = new HashMap <String,Integer>();
		document = new HashMap <String, Url>();
		indexName = ElasticServer.getIndex();
		
		SearchResponse response = ElasticServer.getClient().prepareSearch(ElasticServer.getIndex())
		        .setTypes(ElasticServer.getType())
		        .setSearchType(SearchType.SCAN)		    
		        .setScroll(new TimeValue(60000))
		        .setQuery(QueryBuilders.queryString(keywords))             // Query
		        .setPostFilter(FilterBuilders.rangeFilter("ts").from(dateFrom).to(dateTo))   // Filter
		        .setSize(100).execute().actionGet();
		
		while (limit > 0) {
		    for (SearchHit hit : response.getHits().getHits()) {
		    	
		    	if (limit == 0)
		    		break;
		    	Map<String,Object> source=hit.getSource();
		    	url = new Url ();
		    	url.setOrigUrl(source.get("orig").toString());
		    	url.setCompressedsize(source.get("compressedsize").toString());
		    	url.setFilename(source.get("filename").toString());
		    	url.setOffset(source.get("offset").toString());
		    	url.setRedirectUrl(source.get("redirectUrl").toString());
		    	url.setTimestamp(source.get("ts").toString());
		    	url.setDomain(source.get("orig").toString());
		    	capturesCount++;
		    	
		    	document.put(url.getOrigUrl(), url);
		    	
		    	if (domains.containsKey(url.getDomain()))
		    	{
		    		int value = domains.get(url.getDomain());
		    		value += 1;
		    		domains.put(url.getDomain(), value);
		    	}
		    	else
		    	{
		    		domains.put(url.getDomain(), 1);
		    		domainCount += 1;
		    	}
		    
		    	
		    	limit--;
		    }
		    response = ElasticServer.getClient().prepareSearchScroll(response.getScrollId()).setScroll(new TimeValue(600000)).execute().actionGet();
		    //Break condition: No hits are returned
		    if (response.getHits().getHits().length == 0) {
		        break;
		    }
		}

		return document;
		
	}
	
	public String getIndexName() {
		return indexName;
	}

	public static String getDomain (String url) throws MalformedURLException
	{
		Matcher m = Pattern.compile("(http).*").matcher(url);
		while (m.find()) 
		{
			
			allMatches[0] = m.group(); 
			str = allMatches[0];
			Url = new URL(str);
		}
      
		String Domain = Url.getHost();
		if (Domain.contains("www")) {
			int index = Domain.indexOf(".");
			Domain = Domain.substring(index + 1, Domain.length());
		}
		return Domain;
	}	
	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public HashMap<String, Integer> getDomains ()
	{
		return domains;
	}

	public int getTotalDocuments() {
		return capturesCount;
	}
	public int getDomainCount() {
		return domainCount;
	}
	
}

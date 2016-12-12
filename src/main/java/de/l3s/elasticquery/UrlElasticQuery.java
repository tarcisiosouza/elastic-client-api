package de.l3s.elasticquery;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

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
	private ElasticServer esearch;
	private HashMap<String, Article> articles;
	private HashMap <String, List<Date>> uniqueSnapshots;
	private List<Date> newdates;
	
	public UrlElasticQuery() throws IOException {
		
		document = new HashMap <String,Url>();
		
		articles = new HashMap<String, Article>();
		//ElasticServer.loadProperties();
		uniqueSnapshots = new HashMap<String,List<Date>>();
	}

	public void setArticleText(ArrayList<String> articleText) {
		this.articleText = articleText;
	}

	//Perform a query and get Documents as News Articles
	public HashMap<String, Article> getDocuments (String keywords, int limit, String field) throws IOException {
		
		domainCount = 0;
		int index = 0;
		capturesCount = 0;
		articles.clear();
		esearch = new ElasticServer();
		esearch.loadProperties();
		indexName = esearch.getIndex();
	
		SearchResponse response = esearch.getClient().prepareSearch(esearch.getIndex())
		        .setTypes(esearch.getType())
		        .setSearchType(SearchType.QUERY_THEN_FETCH)		    
		        //.setScroll(new TimeValue(1000000))
		        
		     //  .setQuery(QueryBuilders.queryStringQuery(keywords).minimumShouldMatch("1")).setSize(limit).execute().actionGet();
		   //     .setQuery(QueryBuilders.matchQuery(field, keywords)).setSize(limit).execute().actionGet();
		       // .setScroll(new TimeValue(60000))
    		 //   .setQuery(QueryBuilders.matchAllQuery()) // Match all query
    		//    .setSize(limit).execute().actionGet();
		   .setQuery(QueryBuilders.matchPhraseQuery(field, keywords)).setSize(limit).execute().actionGet();
			
		    for (SearchHit hit : response.getHits().getHits()) {
		  
		    	 
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
		    	article.setTitle(source.get("title").toString());
		    	article.setHtml(source.get("html").toString());
		    } catch (Exception e)
		    {
		    	if (article.getTimestamp().contentEquals("no_ts"))
		    		article.setTimestamp(source.get("timestamp").toString());
		    	if (article.getDomain().contentEquals("no_domain"))
		    		article.setDomain(getDomain(article.getUrl()));
		    }
		    	capturesCount++;
		    	
		    	articles.put(article.getUrl(), article);
		    	
		    }
		    esearch.closeConection();
		    
		    
		return articles;
		
	}
	
	//Match All query
	public HashMap<String, Article> getDocuments (int limit) throws IOException {
		
		domainCount = 0;
		int index = 0;
		capturesCount = 0;
		articles.clear();
		esearch = new ElasticServer();
		esearch.loadProperties();
		indexName = esearch.getIndex();
	
		SearchResponse response = esearch.getClient().prepareSearch(esearch.getIndex())
		        .setTypes(esearch.getType())
		        .setSearchType(SearchType.QUERY_THEN_FETCH)		    
		    
		   .setQuery(QueryBuilders.matchAllQuery()).setSize(limit).execute().actionGet();
			
		    for (SearchHit hit : response.getHits().getHits()) {
		  
		    	 
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
		    	article.setTitle(source.get("title").toString());
		    	article.setHtml(source.get("html").toString());
		    } catch (Exception e)
		    {
		    	if (article.getTimestamp().contentEquals("no_ts"))
		    		article.setTimestamp(source.get("timestamp").toString());
		    	if (article.getDomain().contentEquals("no_domain"))
		    		article.setDomain(getDomain(article.getUrl()));
		    }
		    	capturesCount++;
		    	
		    	articles.put(article.getUrl(), article);
		    	
		    }
		    esearch.closeConection();
		    
		    
		return articles;
		
	}
/*	
	public HashMap<String, Url> getDocuments (String dateF, String dateT, String keywords, String field,int limit) {
		
		dateFrom = DateUtils.parseDate(dateF);
		dateTo = DateUtils.parseDate(dateT);
		domainCount = 0;
		domains = new HashMap <String,Integer>();
		document = new HashMap <String, Url>();
		
		SearchResponse response = esearch.getClient().prepareSearch(esearch.getIndex())
		        .setTypes(esearch.getType())
		        .setSearchType(SearchType.QUERY_THEN_FETCH)		    
		     
		        .setQuery(QueryBuilders.matchQuery(field, keywords))             // Query
		        .setPostFilter(QueryBuilders.rangeQuery("ts").from(dateFrom).to(dateTo))   // Filter
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
		    response = esearch.getClient().prepareSearchScroll(response.getScrollId()).setScroll(new TimeValue(600000)).execute().actionGet();
		    //Break condition: No hits are returned
		    if (response.getHits().getHits().length == 0) {
		        break;
		    }
		}

		return document;
		
	}
*/

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

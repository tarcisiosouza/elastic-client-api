package de.l3s.elasticquery;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import de.l3s.elasticquery.Article;

public class UrlElasticQuery {

	private HashMap<String, Integer> domains;
	private ArrayList<String> totalDocuments = new ArrayList<String>();
	private ArrayList<String> articleText = new ArrayList<String>();

	private int capturesCount;
	private int domainCount;
	private int docs = 0;
	private String indexName;
	private static String[] allMatches = new String[1];

	private static String str;
	private static URL Url;
	private ElasticServer esearch;
	private HashMap<String, Article> articles;
	private HashMap<Article, Double> rankedArticles;

	public UrlElasticQuery() throws IOException {

		articles = new HashMap<String, Article>();
		// ElasticServer.loadProperties();
		rankedArticles = new HashMap<Article, Double>();
	}

	public void setArticleText(ArrayList<String> articleText) {
		this.articleText = articleText;
	}

	// Perform a query and get Documents as News Articles
	public HashMap<String, Article> getDocuments(String keywords, int limit, String field) throws IOException {

		domainCount = 0;
		int index = 0;
		capturesCount = 0;
		articles.clear();
		esearch = new ElasticServer();
		esearch.loadProperties();
		indexName = esearch.getIndex();

		SearchResponse response = esearch.getClient().prepareSearch(esearch.getIndex()).setTypes(esearch.getType())
				.setSearchType(SearchType.QUERY_THEN_FETCH)

				.setQuery(QueryBuilders.matchQuery(field, keywords)).setSize(limit).execute().actionGet();

		for (SearchHit hit : response.getHits().getHits()) {

			Map<String, Object> source = hit.getSource();
			Article article = new Article();
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
			} catch (Exception e) {
				if (article.getTimestamp().contentEquals("no_ts"))
					article.setTimestamp(source.get("timestamp").toString());
				if (article.getDomain().contentEquals("no_domain"))
					article.setDomain(getDomain(article.getUrl()));
			}
			capturesCount++;

			articles.put(article.getTimestamp() + article.getUrl(), article);

		}
		esearch.closeConection();

		return articles;

	}

	// Perform a query and get ranked Documents
	public Map<Article, Double> getRankedDocuments(String keywords, int limit, String field) throws IOException {

		domainCount = 0;
		int index = 0;
		capturesCount = 0;
		articles.clear();
		esearch = new ElasticServer();
		esearch.loadProperties();
		indexName = esearch.getIndex();

		SearchResponse response = esearch.getClient().prepareSearch(esearch.getIndex()).setTypes(esearch.getType())
				.setSearchType(SearchType.QUERY_THEN_FETCH)

				.setQuery(QueryBuilders.matchQuery(field, keywords)).setSize(limit).execute().actionGet();

		for (SearchHit hit : response.getHits().getHits()) {

			Map<String, Object> source = hit.getSource();
			Article article = new Article();
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
			} catch (Exception e) {
				if (article.getTimestamp().contentEquals("no_ts"))
					article.setTimestamp(source.get("timestamp").toString());
				if (article.getDomain().contentEquals("no_domain"))
					article.setDomain(getDomain(article.getUrl()));
			}
			capturesCount++;

			rankedArticles.put(article, article.getScore());

		}
		esearch.closeConection();

		return sortByComparator(rankedArticles, false);

	}

	// Match All query
	public HashMap<String, Article> getDocuments(int limit) throws IOException {

		domainCount = 0;
		int index = 0;
		capturesCount = 0;
		articles.clear();
		esearch = new ElasticServer();
		esearch.loadProperties();
		indexName = esearch.getIndex();

		SearchResponse response = esearch.getClient().prepareSearch(esearch.getIndex()).setTypes(esearch.getType())
				.setSearchType(SearchType.QUERY_THEN_FETCH)

				.setQuery(QueryBuilders.matchAllQuery()).setSize(limit).execute().actionGet();

		for (SearchHit hit : response.getHits().getHits()) {

			Map<String, Object> source = hit.getSource();
			Article article = new Article();
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
			} catch (Exception e) {
				if (article.getTimestamp().contentEquals("no_ts"))
					article.setTimestamp(source.get("timestamp").toString());
				if (article.getDomain().contentEquals("no_domain"))
					article.setDomain(getDomain(article.getUrl()));
			}
			capturesCount++;

			articles.put(article.getTimestamp() + article.getUrl(), article);

		}
		esearch.closeConection();

		return articles;

	}

	public ArrayList<String> getArticleText() {
		return articleText;
	}

	public void setTotalDocuments(ArrayList<String> totalDocuments) {
		this.totalDocuments = totalDocuments;
	}

	public ArrayList<String> getArrayTotalDoc() {
		return totalDocuments;
	}

	public String getIndexName() {
		return indexName;
	}

	public static String getDomain(String url) throws MalformedURLException {
		Matcher m = Pattern.compile("(http).*").matcher(url);
		while (m.find()) {

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

	public HashMap<String, Integer> getDomains() {
		return domains;
	}

	public int getTotalDocuments() {
		return capturesCount;
	}

	public int getDomainCount() {
		return domainCount;
	}

	private static Map<Article, Double> sortByComparator(Map<Article, Double> unsortMap, final boolean order) {

		List<Entry<Article, Double>> list = new LinkedList<Entry<Article, Double>>(unsortMap.entrySet());

		// Sorting the list based on values
		Collections.sort(list, new Comparator<Entry<Article, Double>>() {
			public int compare(Entry<Article, Double> o1, Entry<Article, Double> o2) {
				if (order) {
					return o1.getValue().compareTo(o2.getValue());
				} else {
					return o2.getValue().compareTo(o1.getValue());

				}
			}
		});

		// Maintaining insertion order with the help of LinkedList
		Map<Article, Double> sortedMap = new LinkedHashMap<Article, Double>();
		for (Entry<Article, Double> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}
}

package de.l3s.elasticquery;

import java.io.IOException;

import java.util.HashMap;

import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AppTest extends TestCase {
	public AppTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	public void testApp() throws IOException {

		String query = "angela merkel";
		int limit = 700;
		Map<Article, Double> documents = new HashMap<Article, Double>();
		new ElasticMain(query, limit, "url");
		ElasticMain.setKeywords(query);
		ElasticMain.run();

		documents = ElasticMain.getResult();

		System.out.println("Total documents: " + documents.size());
		int i = 0;
		for (Entry<Article, Double> s : documents.entrySet())
			System.out
					.print(s.getKey().getTimestamp() + " " + s.getKey().getUrl() + " " + s.getKey().getScore() + " \n");

	}
}

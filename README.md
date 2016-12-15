ElasticSearchClient@L3S
=================

Elastic Search client at L3S for Java applications

1. If you want to add it as a dependency in your maven project:
-----------------------------------

		<dependency>
			<groupId>de.l3s</groupId>
			<artifactId>elasticquery</artifactId>
			<version>0.0.1-SNAPSHOT</version>
			<scope>compile</scope>
		</dependency>
    
1.1. In the client project
-----------------------------------
```
mvn clean install
```

1.2. In your application folder
-----------------------------------
```
mvn eclipse:eclipse
```

2. If you want to use the jar with all the dependencies:
-----------------------------------
```
mvn clean package
```
2.1. Now you should have a .jar called `elasticquery-jar-with-dependencies.jar`
in the directory `target` that contains all the classes.

3. Example of using the client in your java code:
-----------------------------------
```
String query = "Angela Merkel";
int limit = 1000; //total number of documents to retrieve
HashMap<String,Article> documents = new HashMap<String,Article>();
new ElasticMain (query, limit,"text");
ElasticMain.setKeywords(query);
ElasticMain.run();
documents = ElasticMain.getResult();
for(Entry<String, Article> s : documents.entrySet())
	System.out.print(s.getValue().getTimestamp()+" "+s.getValue().getUrl()+" \n");
```
4. Depending on the index you want to search on the cluster you should edit the file config.properties
-----------------------------------
For the german news
```
index=souza_warc_news
type=capture
cluster=nextsearch
hostname=master02.ib
port=9350

```
For the UK News
```
index=souza_warc_news_annotated
type=capture
cluster=nextsearch
hostname=master02.ib
port=9350

```

package com.example.dubaipolica.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;

public class ElkService {

	private static final String ES_HOST = "172.24.120.123";
	private static final int ES_PORT = 9200;
	private static final String ES_USERNAME = "elastic";
	private static final String ES_PASSWORD = "123456";
	private static final String INDEX_NAME = "filtered_filterdevicename_index1";

	private static final String ORACLE_URL = "jdbc:oracle:thin:@10.10.14.222:1521:oracle";
	private static final String ORACLE_USER = "system";
	private static final String ORACLE_PASSWORD = "123456";
	
	private static final String SQL_URL = "jdbc:sqlserver://10.10.13.11:1435;databaseName=polica";
	private static final String SQL_USER = "lowcodeadmin";
	private static final String SQL_PASSWORD = "lcnc@123";

	public void main(String[] args, Connection oracleConnection) throws IOException, SQLException {
		RestHighLevelClient esClient = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = oracleConnection.createStatement();
//			resultSet = statement.executeQuery(
//					"SELECT count(*) FROM user_tables WHERE table_name = '" + INDEX_NAME.toUpperCase() + "'");
//			resultSet.next();
			resultSet = statement.executeQuery("SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = '" + INDEX_NAME + "'");
			resultSet.next();
			int tableCount = resultSet.getInt(1);

			if (tableCount == 0) {
				// Table does not exist, so create it
//				String sql = "CREATE TABLE " + INDEX_NAME + " (Name VARCHAR2(50), Age NUMBER, City VARCHAR2(50))";
//				statement.executeUpdate(sql);
//				System.out.println("Table created successfully in Oracle Database");
				
				String sql = "CREATE TABLE " + INDEX_NAME + " (Name VARCHAR(50), Age INT, City VARCHAR(50))";
			    statement.executeUpdate(sql);
			    System.out.println("Table created successfully in SQL Server Database");
			} else {
				System.out.println("Table already exists in Oracle Database");
			}

			esClient = createClient();

			createIndex(esClient);
			createDocument(esClient);
			oracleCreateOperation(oracleConnection);
//	        oracleUpdateOperation(oracleConnection);

		}

		catch (ElasticsearchStatusException e) {
			if (e.getMessage().contains("resource_already_exists_exception")) {
				System.out.println("Index already exists: " + INDEX_NAME);
				// Delete the existing index
				deleteIndex(esClient, INDEX_NAME);
				// Recreate the index
				createIndex(esClient);
				createDocument(esClient);
				updateDocument(esClient);
				oracleCreateOperation(oracleConnection);
			} else {
				// Handle other Elasticsearch exceptions
				e.printStackTrace();
			}
		}

		finally {
			if (esClient != null) {
				try {
					esClient.close();
				} catch (IOException e) {
					e.printStackTrace();
					// Handle the exception appropriately
				}
			}
		}
	}

	public static Connection createOracleConnection() throws SQLException {
		Connection connection = null;
		try {
			// Register the Oracle JDBC driver
			Class.forName("oracle.jdbc.driver.OracleDriver");
			// Create the connection
			connection = DriverManager.getConnection(ORACLE_URL, ORACLE_USER, ORACLE_PASSWORD);
			System.out.println("Connected to Oracle database.");
		} catch (ClassNotFoundException | SQLException e) {
			System.err.println("Error connecting to Oracle database: " + e.getMessage());
			throw new SQLException("Error connecting to Oracle database: " + e.getMessage());
		}
		return connection;
	}

	public static Connection createSqlConnection() throws SQLException {
		Connection connection = null;
		try {
			// Register the Oracle JDBC driver
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			// Create the connection
			connection = DriverManager.getConnection(SQL_URL, SQL_USER, SQL_PASSWORD);
			System.out.println("Connected to SQL database.");
		} catch (ClassNotFoundException | SQLException e) {
			System.err.println("Error connecting to SQL database: " + e.getMessage());
			throw new SQLException("Error connecting to SQL database: " + e.getMessage());
		}
		return connection;
	}
	
	private static void deleteIndex(RestHighLevelClient esClient, String indexName) throws IOException {
		DeleteIndexRequest request = new DeleteIndexRequest(indexName);
		esClient.indices().delete(request, RequestOptions.DEFAULT);
		System.out.println("Index deleted: " + indexName);
	}

	public static RestHighLevelClient createClient() {
		BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(ES_USERNAME, ES_PASSWORD));

		RestClientBuilder builder = RestClient.builder(new HttpHost(ES_HOST, ES_PORT, "http"))
				.setHttpClientConfigCallback(
						httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
				.setDefaultHeaders(compatibilityHeaders());

		return new RestHighLevelClient(builder);
	}

	private static Header[] compatibilityHeaders() {
		return new Header[] {
				new BasicHeader(HttpHeaders.ACCEPT, "application/vnd.elasticsearch+json;compatible-with=7"),
				new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/vnd.elasticsearch+json;compatible-with=7") };
	}

	public static void createIndex(RestHighLevelClient esClient) throws IOException {
		CreateIndexRequest request = new CreateIndexRequest(INDEX_NAME);
		request.settings(Settings.builder().put("index.number_of_shards", 1).put("index.number_of_replicas", 0));
		esClient.indices().create(request, RequestOptions.DEFAULT);
		System.out.println("Index created successfully: " + INDEX_NAME);
	}

	public static void createDocument(RestHighLevelClient esClient) throws IOException {
		IndexRequest request = new IndexRequest(INDEX_NAME);
		request.id("1");
		String jsonString = "{" + "\"name\":\"John\"," + "\"age\":30," + "\"city\":\"New York\"" + "}";
		request.source(jsonString, XContentType.JSON);
		IndexResponse indexResponse = esClient.index(request, RequestOptions.DEFAULT);
		System.out.println("Document created in Elasticsearch: " + indexResponse.getResult().name());
	}

	public static void updateDocument(RestHighLevelClient esClient) throws IOException {
		UpdateRequest request = new UpdateRequest(INDEX_NAME, "1");
		request.doc("city", "Los Angeles");
		UpdateResponse updateResponse = esClient.update(request, RequestOptions.DEFAULT);
		System.out.println("Document updated in Elasticsearch: " + updateResponse.getResult().name());
	}

	public static void oracleCreateOperation(Connection oracleConnection) throws SQLException {
		Statement statement = null;
		try {
			statement = oracleConnection.createStatement();
			String sql = "INSERT INTO " + INDEX_NAME + " (Name, Age, City) VALUES ('John', '30', 'New York')";
			statement.executeUpdate(sql);
			System.out.println("Record created in Oracle Database");
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}

}

package edu.cumt.semantic.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Properties specific to Semantic CUMT Demo.
 * <p>
 * Properties are configured in the application.yml file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
	private final Neo4j neo4j=new Neo4j();
	
	public Neo4j getNeo4j() {
		return neo4j;
	}

	public static class Neo4j{
		private String uri="";
		private String username="";
		private String password="";
		public String getUri() {
			return uri;
		}
		public void setUri(String uri) {
			this.uri = uri;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
	}
}

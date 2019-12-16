package edu.cumt.semantic.config;

import org.neo4j.ogm.session.SessionFactory;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableNeo4jRepositories(basePackages="edu.cumt.semantic.neo4j.repository")
//@EntityScan(basePackages = "edu.cumt.semantic.neo4j.domain")
@EnableTransactionManagement
public class Neo4jConfig {
	@Bean
    public SessionFactory sessionFactory() {
        // with domain entity base package(s)
        return new SessionFactory(configuration(), "edu.cumt.semantic.neo4j.domain");
    }
	
	@Bean
	public org.neo4j.ogm.config.Configuration configuration() {
	    org.neo4j.ogm.config.Configuration configuration = new org.neo4j.ogm.config.Configuration.Builder()
	            .uri("bolt://localhost")
	            .credentials("neo4j", "123456")
	            .build();
	    return configuration;
	}
	@Bean
    public Neo4jTransactionManager transactionManager() {
        return new Neo4jTransactionManager(sessionFactory());
    }
}

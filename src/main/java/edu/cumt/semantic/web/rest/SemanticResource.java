package edu.cumt.semantic.web.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import edu.cumt.semantic.config.ApplicationProperties;
import edu.cumt.semantic.neo4j.domain.Person;
import edu.cumt.semantic.neo4j.repository.PersonRepository;
import edu.cumt.semantic.service.dto.SemanticResourceDTO;

@RestController
@RequestMapping("/semantic")
public class SemanticResource {
	private final Logger log = LoggerFactory.getLogger(SemanticResource.class);
	private static final String CHEESE_SCHEMA = "http://data.kasabi.com/dataset/cheese/schema/";
	private static final String DATA_DIR = "./src/main/resources/data/";
	private static final String CHEESE_DATA_FILE = DATA_DIR + "cheeses-0.1.ttl";
	
	@Autowired
	PersonRepository personRepository;
	@Autowired
	ApplicationProperties applicationProperties;
	
	
	
//	测试neo4j
	@RequestMapping(value = "/neo4j/delete", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional
	public ResponseEntity<?> testNeo4j(HttpServletRequest request, HttpServletResponse response){
		personRepository.deleteAll();
		return new ResponseEntity<>("delete", HttpStatus.OK);
	}
	
	@RequestMapping(value = "/neo4j/add", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional
	public ResponseEntity<?> testNeo4j2(HttpServletRequest request, HttpServletResponse response){
		Person greg = new Person("Greg");
		Person roy = new Person("Roy");
		Person craig = new Person("Craig");

		List<Person> team = Arrays.asList(greg, roy, craig);

		log.info("Before linking up with Neo4j...");

		team.stream().forEach(person -> log.info("\t" + person.toString()));

		personRepository.save(greg);
		personRepository.save(roy);
		personRepository.save(craig);

		greg = personRepository.findByName(greg.getName());
		greg.worksWith(roy);
		greg.worksWith(craig);
		personRepository.save(greg);

		roy = personRepository.findByName(roy.getName());
		roy.worksWith(craig);
		// We already know that roy works with greg
		personRepository.save(roy);
		
		return new ResponseEntity<>("add", HttpStatus.OK);
	}
	
	@RequestMapping(value = "/getclass/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional
	public ResponseEntity<?> getDefaultAlarmPushStatus(@PathVariable("name") String name,HttpServletRequest request, HttpServletResponse response){
		Model m = ModelFactory.createDefaultModel();
		FileManager.get().readModel( m, CHEESE_DATA_FILE );
		Resource cheeseClass = m.getResource( CHEESE_SCHEMA + name );
		StmtIterator i = m.listStatements( null, RDF.type, cheeseClass );
		List<SemanticResourceDTO> semanticResourceDTOLi=new ArrayList<SemanticResourceDTO>();
		while (i.hasNext()) {
            Resource cheese = i.next().getSubject();
            String label = getLabel( cheese );
//            System.out.println( String.format( "Cheese %s has name: %s", cheese.getURI(), label ) );
            SemanticResourceDTO semanticResourceDTO=new SemanticResourceDTO();
            semanticResourceDTO.setURI(cheese.getURI());
            semanticResourceDTO.setLabel(label);
            semanticResourceDTOLi.add(semanticResourceDTO);
        }
		return new ResponseEntity<>(semanticResourceDTOLi, HttpStatus.OK);
	}
	
	private String getLabel( Resource cheese ) {
        StmtIterator i = cheese.listProperties( RDFS.label );
        while (i.hasNext()) {
            Literal l = i.next().getLiteral();
            return l.getLexicalForm();            
        }

        return "no Label!";
    }

}

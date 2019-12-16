package edu.cumt.semantic.neo4j.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.cumt.semantic.neo4j.domain.Person;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {

	Person findByName(String name);
}

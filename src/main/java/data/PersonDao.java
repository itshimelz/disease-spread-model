package data;

import domain.person.Person;

import java.util.List;

public interface PersonDao {
    void addPerson(Person person);
    List<Person> getAllPersons();
    Person getPersonByName(String name);
    void updatePerson(Person person);
    void deletePerson(String name);
    List<Person> getInfectedPersons();
}

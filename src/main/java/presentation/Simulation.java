package presentation;

import data.JsonPersonDatabase;
import data.PersonDao;
import domain.person.Person;

import java.util.List;
import java.util.ResourceBundle;

public class Main {
    public class SimulationService {
        private PersonDao personDao;

        public SimulationService(PersonDao personDao) {
            this.personDao = personDao;
        }
        public static void main(String[] args) {
            ResourceBundle peoples = ResourceBundle.getBundle("strings");
            PersonDao personDao = new JsonPersonDatabase(peoples.getString("peoplesPath"));

            Person person1 = new Person("Alice", 30, "New York");
            Person person2 = new Person("Bob", 25, "Los Angeles");

            personDao.addPerson(person1);
            personDao.addPerson(person2);

            List<Person> allPersons = personDao.getAllPersons();
            System.out.println(allPersons);

            Person retrievedPerson = personDao.getPersonByName("Alice");
            System.out.println(retrievedPerson);

            retrievedPerson.setAge(31);
            personDao.updatePerson(retrievedPerson);
            System.out.println(personDao.getPersonByName("Alice"));

            personDao.deletePerson("Bob");
            System.out.println(personDao.getAllPersons());
        }
    }

}

package data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import domain.person.HealthStatus;
import domain.person.Person;
import domain.utils.MyLogger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class JsonPersonDatabase implements PersonDao {
    // WARNING: This implementation loads and saves the entire person list for every operation.
    // TODO: Add concurrency controls and transactional safety for multi-threaded or multi-process use.
    // TODO: Implement error bubbling (currently, errors are only printed, not thrown to callers).
    // NOTE: There are no automated tests for this component yet.
    private final String filePath;
    private final ObjectMapper objectMapper;
    private static final Logger logger = MyLogger.getLogger();

    public JsonPersonDatabase(String filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper();
    }

    private List<Person> loadPersons() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return new ArrayList<>(); // Return empty list if file doesn't exist
            }
            CollectionType type = objectMapper.getTypeFactory().constructCollectionType(List.class, Person.class);
            return objectMapper.readValue(file, type);
        } catch (IOException e) {
            System.err.println("Error loading persons from JSON: " + e.getMessage());
            return new ArrayList<>(); // Return empty list in case of error
        }
    }

    private void savePersons(List<Person> persons) {
        try {
            objectMapper.writeValue(new File(filePath), persons);
        } catch (IOException e) {
            System.err.println("Error saving persons to JSON: " + e.getMessage());
        }
    }

    @Override
    public void addPerson(Person person) {
        List<Person> persons = loadPersons();
        persons.add(person);
        savePersons(persons);
        MyLogger.logInfo("Person added");
    }

    @Override
    public List<Person> getAllPersons() {
        return loadPersons();
    }

    @Override
    public Person getPersonByName(String name) {
        List<Person> persons = loadPersons();
        return persons.stream().filter(person -> person.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public void updatePerson(Person person) {
        List<Person> persons = loadPersons();
        persons.removeIf(c -> c.getName().equals(person.getName())); // Remove the old person
        persons.add(person);
        savePersons(persons);
    }

    @Override
    public void deletePerson(String name) {
        List<Person> persons = loadPersons();
        persons.removeIf(person -> person.getName().equals(name));
        savePersons(persons);
    }

    @Override
    public List<Person> getInfectedPersons() {
        List<Person> persons = loadPersons();
        return persons.stream()
                .filter(person -> person.getHealthStatus().equals(HealthStatus.INFECTED))
                .collect(Collectors.toList());
    }
}

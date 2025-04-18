package service;

import data.CityDao;
import data.PersonDao;
import domain.city.City;
import domain.person.HealthStatus;
import domain.person.Person;
import domain.utils.MergeSort;
import domain.utils.MyLogger;

import java.util.*;

public class PopulationManager {
    private final CityDao cityDao;
    private final PersonDao personDao;
    private final CityService cityService; // Add CityService field
    private final Graph cityGraph;
    private final Random random = new Random();

    public PopulationManager(CityDao cityDao, PersonDao personDao, CityService cityService) { // Add CityService parameter
        this.cityDao = cityDao;
        this.personDao = personDao;
        this.cityService = cityService; // Initialize the field
        this.cityGraph = new Graph();
    }

    public void initializeCities(List<String> cities, CityService cityService){
        for (String cityName : cities) {
            if (!cityGraph.hasVertex(cityName)) {
                cityGraph.addVertex(cityName);
                cityService.addCity(cityName, generateRandomPopulationDensity());
            }
        }
    }

    public void initializePopulation(String cityName, int minPopulation, int maxPopulation) {
        City city = cityDao.getCityByName(cityName);
        if (city != null) {
            int populationSize = generateRandomPopulationSize(minPopulation, maxPopulation);
            List<Person> people = new ArrayList<>();
            for (int i = 0; i < populationSize; i++) {
                Person person = Person.createRandomPerson(80, cityName);
                people.add(person);
                personDao.addPerson(person);
            }
            city.setResidents(people); // Always set residents
            cityDao.updateCity(city);
            MyLogger.logInfo("Finished initializing " + populationSize + " people in " + cityName + " population size: " + populationSize);
        } else {
            MyLogger.logSevere("City not found: " + cityName);
        }
    }

    public List<Person> getSortedPopulation(String cityName) {
        City city = cityDao.getCityByName(cityName);
        if (city != null && city.getResidents() != null) {
            List<Person> populationCopy = new ArrayList<>(city.getResidents());
            MergeSort.mergeSort(populationCopy);
            return populationCopy;
        }
        return Collections.emptyList();
    }


    public void addConnectionBetweenCity(String city1Name, String city2Name, double weight){
        if (!cityGraph.hasVertex(city1Name)) {
            cityGraph.addVertex(city1Name);
        }
        if (!cityGraph.hasVertex(city2Name)) {
            cityGraph.addVertex(city2Name);
        }

        City city1 = cityDao.getCityByName(city1Name);
        City city2 = cityDao.getCityByName(city2Name);

        if (city1 != null && city2 != null) {
            cityGraph.addEdge(city1Name, city2Name, weight);
            cityService.addConnectionBetweenCity(city1Name, city2Name, weight);
        }
    }

    public Graph getCityGraph() {
        return cityGraph;
    }

    private double generateRandomPopulationDensity(){
        return 5 + (20 - 5) * Math.random();
    }

    private int generateRandomPopulationSize(int minPopulation, int maxPopulation) {
        if (minPopulation > maxPopulation) {
            throw new IllegalArgumentException("Minimum population cannot be greater than maximum population");
        }
        return minPopulation + (int) (Math.random() * (maxPopulation - minPopulation + 1));
    }

    public void spreadInfection(String cityName, double infectionProbability) {
        City city = cityDao.getCityByName(cityName);
        if (city != null) {
            List<Person> cityPopulation = city.getResidents();
            if(cityPopulation != null){
                for (Person person : cityPopulation) {
                    if (person.getHealthStatus() == HealthStatus.SUSCEPTIBLE) {
                        if (random.nextDouble() < infectionProbability) {
                            person.infect();
                            personDao.updatePerson(person);
                        }
                    }
                }
                cityDao.updateCity(city);
            }
        } else {
            System.out.println("City not found");
        }
    }

    public void updatePopulationHealth(String cityName) {
        City city = cityDao.getCityByName(cityName);
        if (city != null) {
            List<Person> allPersons = city.getResidents();
            if (allPersons != null) {
                for (Person person : allPersons) {
                    person.updateHealthStatus();
                    personDao.updatePerson(person);
                }
            }
            cityDao.updateCity(city);
        } else {
            System.out.println("City not found");
        }
    }

    public int countInfected(String cityName) {
        City city = cityDao.getCityByName(cityName);
        if (city != null) {
            List<Person> allPersons = city.getResidents();
            if (allPersons != null) {
                return (int) allPersons.stream()
                        .filter(person -> person.getHealthStatus() == HealthStatus.INFECTED)
                        .count();
            }
            return 0;
        } else {
            MyLogger.logSevere("City not found: " + cityName);
            return 0;
        }
    }

    public List<String> shortestPath(String startCity, String endCity) {
        return cityGraph.shortestPath(startCity, endCity);
    }

    public Set<String> getConnectedCities(String cityName) {
        return cityGraph.getAdjacentVertices(cityName);
    }

    public Set<String> getAllVertices(){
        return cityGraph.getVertices();
    }

    public List<Person> getAllPerson() {
        return personDao.getAllPersons();
    }

    public Person getPersonByName(String name) {
        return personDao.getPersonByName(name);
    }

    public void addPerson(Person person) {
        personDao.addPerson(person);
    }

    public void updatePerson(Person person) {
        personDao.updatePerson(person);
    }

    public void deletePerson(String name) {
        personDao.deletePerson(name);
    }
}
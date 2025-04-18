package presentation;

import data.CityDao;
import data.PersonDao;
import service.CityService;
import service.PopulationManager;

import java.util.List;

public class Simulation {
    private final PopulationManager populationManager;
    private final CityService cityService;
    private static final List<String> CITIES = List.of(
            "Dhaka", "Chittagong", "Khulna", "Rajshahi", "Barishal"
    );

    public Simulation(CityDao cityDao, PersonDao personDao) {
        this.cityService = new CityService(cityDao);
        this.populationManager = new PopulationManager(cityDao, personDao, cityService);
    }

    public CityService getCityService() {
        return cityService;
    }

    public PopulationManager getPopulationManager() {
        return populationManager;
    }

    public void initialize(){
        populationManager.initializeCities(CITIES, cityService);
    }

    public void initializePopulations() {
        for (String city : CITIES) {
            populationManager.initializePopulation(city, 25, 100);
        }
    }

    public void initializeConnections(){
        populationManager.addConnectionBetweenCity("Dhaka", "Chittagong", 0.5);
        populationManager.addConnectionBetweenCity("Dhaka", "Sylhet", 0.3);
        populationManager.addConnectionBetweenCity("Chittagong", "Khulna", 0.2);
        populationManager.addConnectionBetweenCity("Rajshahi", "Khulna", 0.7);
        populationManager.addConnectionBetweenCity("Dhaka", "Khulna", 0.1);
        populationManager.addConnectionBetweenCity("Sylhet", "Barishal", 0.6);
        populationManager.addConnectionBetweenCity("Rangpur", "Rajshahi", 0.4);
        populationManager.addConnectionBetweenCity("Comilla", "Chittagong", 0.8);
        populationManager.addConnectionBetweenCity("Mymensingh", "Dhaka", 0.9);
        populationManager.addConnectionBetweenCity("Gazipur", "Mymensingh", 0.2);
        populationManager.addConnectionBetweenCity("Gazipur", "Dhaka", 0.6);
    }


    public void runSimulation(int days) {
        for (int day = 1; day <= days; day++) {
            System.out.println("Day " + day + ":");
            for(String city: CITIES){
                populationManager.spreadInfection(city, 0.1);
                populationManager.updatePopulationHealth(city);
                System.out.println("Infected "+city+": " + populationManager.countInfected(city));
            }
        }
    }
}
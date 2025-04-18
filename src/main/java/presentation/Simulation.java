package presentation;

import data.CityDao;
import data.PersonDao;
import service.CityService;
import service.PopulationManager;
import domain.utils.MyLogger;
import domain.city.RiskLevel;

import java.util.List;

public class Simulation {
    private final PopulationManager populationManager;
    private final CityService cityService;
    // Unified city list: includes all cities referenced in connections
    private static final List<String> CITIES = List.of(
            "Dhaka", "Chittagong", "Khulna", "Rajshahi", "Barishal",
            "Sylhet", "Rangpur", "Comilla", "Mymensingh", "Gazipur"
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
    } // Now always tries to initialize population for every city

    public void initializeConnections(){
        // If you want bidirectional connections, add both directions
        addBidirectionalConnection("Dhaka", "Chittagong", 0.5);
        addBidirectionalConnection("Dhaka", "Sylhet", 0.3);
        addBidirectionalConnection("Chittagong", "Khulna", 0.2);
        addBidirectionalConnection("Rajshahi", "Khulna", 0.7);
        addBidirectionalConnection("Dhaka", "Khulna", 0.1);
        addBidirectionalConnection("Sylhet", "Barishal", 0.6);
        addBidirectionalConnection("Rangpur", "Rajshahi", 0.4);
        addBidirectionalConnection("Comilla", "Chittagong", 0.8);
        addBidirectionalConnection("Mymensingh", "Dhaka", 0.9);
        addBidirectionalConnection("Gazipur", "Mymensingh", 0.2);
        addBidirectionalConnection("Gazipur", "Dhaka", 0.6);
    }

    private void addBidirectionalConnection(String cityA, String cityB, double weight) {
        populationManager.addConnectionBetweenCity(cityA, cityB, weight);
        populationManager.addConnectionBetweenCity(cityB, cityA, weight);
    }

    public void runSimulation(int days) {
        for (int day = 1; day <= days; day++) {
            MyLogger.logInfo("Day " + day + ":");
            for(String city: CITIES){
                populationManager.spreadInfection(city, 0.1);
                populationManager.updatePopulationHealth(city);
                CityService cs = getCityService();
                var c = cs.getCityByName(city);
                if (c != null) {
                    int infected = populationManager.countInfected(city);
                    int total = c.getResidents() != null ? c.getResidents().size() : 0;
                    double infectionRate = (total > 0) ? ((double) infected / total) : 0.0;
                    c.setInfectionRate(infectionRate);
                    // Determine risk level
                    if (infectionRate == 0.0) {
                        c.setRiskLevel(RiskLevel.OUT_OF_DANGER);
                    } else if (infectionRate <= 0.05) {
                        c.setRiskLevel(RiskLevel.LOW);
                    } else if (infectionRate <= 0.15) {
                        c.setRiskLevel(RiskLevel.MEDIUM);
                    } else {
                        c.setRiskLevel(RiskLevel.HIGH);
                    }
                    cs.updateCity(c);
                }
                MyLogger.logInfo("Infected "+city+": " + populationManager.countInfected(city));
            }
        }
    }

    // NOTE: Make sure you have the required resource files (e.g., strings.properties and personNamesPath JSON) in your classpath/resources.
    // Otherwise, Person name generation will fail.
}
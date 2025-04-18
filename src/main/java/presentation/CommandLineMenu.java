package presentation;

import data.CityDao;
import data.JsonCityDatabase;
import data.JsonPersonDatabase;
import data.PersonDao;
import domain.city.City;
import domain.person.Person;
import domain.utils.MyLogger;
import service.CityService;
import service.Graph;

import java.util.*;
import java.util.logging.Logger;

public class CommandLineMenu {

    private static final Logger logger = MyLogger.getLogger();
    private final Simulation simulation;
    private final Scanner scanner;
    private boolean isInitialized = false;

    public CommandLineMenu(Simulation simulation) {
        this.simulation = simulation;
        this.scanner = new Scanner(System.in);
    }

    public void runMenu() throws CityService.CityAlreadyExistsException {
        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getChoice();

            switch (choice) {
                case 1:
                    initializeSimulation();
                    break;
                case 2:
                    runSimulation();
                    break;
                case 3:
                    displayGraph();
                    break;
                case 4:
                    findShortestPath();
                    break;
                case 5:
                    displaySortedPopulation();
                    break;
                case 8:
                    running = false;
                    System.out.println("Exiting simulation.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    private void displayMenu() {
        System.out.println("\nSimulation Menu:");
        if (!isInitialized) {
            System.out.println("1. Initialize Simulation (Cities, Population, Graph)");
        } else {
            System.out.println("2. Run Simulation");
            System.out.println("3. Display City Graph (Breadth-First Traversal)");
            System.out.println("4. Find Shortest Path Between Cities");
            System.out.println("5. Display Sorted Population");
        }
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    }

    private int getChoice() {
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next();
            return -1;
        }
    }

    private void initializeSimulation() throws CityService.CityAlreadyExistsException {
            simulation.initialize();
            simulation.initializePopulations();
            simulation.initializeConnections();
            isInitialized = true;
            System.out.println("Simulation initialized successfully.");
            logger.info("Simulation initialized successfully.");
    }

    private void runSimulation() {
        if (!isInitialized) {
            System.out.println("Please initialize the simulation first (Option 1).");
            return;
        }
        System.out.print("Enter the number of days to simulate: ");
        int days = getChoice();
        if (days > 0) {
            simulation.runSimulation(days);
        } else {
            System.out.println("Please enter a valid number of days (greater than 0).");
        }
    }

    private void displayGraph() {
        if (!isInitialized) {
            System.out.println("Please initialize the simulation first (Option 1).");
            return;
        }
        Graph graph = simulation.getPopulationManager().getCityGraph();
        System.out.print("Enter the starting city for traversal: ");
        String startCity = scanner.next();
        if(graph.hasVertex(startCity)){
            List<String> traversal = graph.breadthFirstTraversal(startCity);
            System.out.println("Breadth-First Traversal from " + startCity + ":");
            System.out.println(traversal);
        }
        else{
            System.out.println("City not found in the graph.");
        }
    }

    private void findShortestPath() {
        if (!isInitialized) {
            System.out.println("Please initialize the simulation first (Option 1).");
            return;
        }
        System.out.print("Enter the starting city: ");
        String startCity = scanner.next();
        System.out.print("Enter the destination city: ");
        String endCity = scanner.next();

        List<String> path = simulation.getPopulationManager().shortestPath(startCity, endCity);

        if (path != null) {
            System.out.println("Shortest path from " + startCity + " to " + endCity + ":");
            System.out.println(path);
        } else {
            System.out.println("No path found between " + startCity + " and " + endCity + ".");
        }
    }

    private void displayAllCities() {
        List<City> cities = simulation.getCityService().getAllCities();
        if (cities.isEmpty()) {
            System.out.println("No cities found.");
        } else {
            System.out.println("All Cities:");
            for (City city : cities) {
                System.out.println(city);
            }
        }
    }

    private void displaySortedPopulation() {
        if (!isInitialized) {
            System.out.println("Please initialize the simulation first (Option 1).");
            return;
        }
        System.out.print("Enter the city to display sorted population: ");
        String cityName = scanner.next();
        if (simulation.getCityService().getCityByName(cityName) == null){
            System.out.println("City not found");
            return;
        }
        List<Person> sortedPopulation = simulation.getPopulationManager().getSortedPopulation(cityName);

        if (sortedPopulation.isEmpty()) {
            System.out.println("No population found for " + cityName);
        } else {
            System.out.println("Sorted Population of " + cityName + " (by age):");
            for (Person person : sortedPopulation) {
                System.out.println(person);
            }
        }
    }

    public static void main(String[] args) throws CityService.CityAlreadyExistsException {
        ResourceBundle path = ResourceBundle.getBundle("strings");
        PersonDao personDao = new JsonPersonDatabase(path.getString("peoplesPath"));
        CityDao cityDao = new JsonCityDatabase(path.getString("citiesPath"));

        Simulation simulation = new Simulation(cityDao, personDao);
        CommandLineMenu menu = new CommandLineMenu(simulation);
        menu.runMenu();
    }
}

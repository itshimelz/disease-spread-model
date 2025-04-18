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

    // Toggles the logging state using MyLogger
    private void toggleLogging() {
        boolean current = MyLogger.isEnabled();
        MyLogger.setEnabled(!current);
        System.out.println("Logging is now " + (MyLogger.isEnabled() ? "ON" : "OFF"));
    }

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
            int optionCount = displayMenu();
            int choice = getChoice(optionCount);
            int idx = 1;
            if (!isInitialized) {
                if (choice == idx++) {
                    initializeSimulation();
                } else if (choice == idx++) {
                    toggleLogging();
                } else if (choice == idx++) {
                    showHelp();
                } else if (choice == idx++) {
                    running = false;
                    System.out.println("Exiting simulation.");
                } else {
                    System.out.println("Invalid choice. Please try again.");
                }
            } else {
                if (choice == idx++) {
                    runSimulation();
                } else if (choice == idx++) {
                    displayGraph();
                } else if (choice == idx++) {
                    findShortestPath();
                } else if (choice == idx++) {
                    displaySortedPopulation();
                } else if (choice == idx++) {
                    displayAllCities();
                } else if (choice == idx++) {
                    toggleLogging();
                } else if (choice == idx++) {
                    showHelp();
                } else if (choice == idx++) {
                    running = false;
                    System.out.println("Exiting simulation.");
                } else {
                    System.out.println("Invalid choice. Please try again.");
                }
            }
        }
        scanner.close();
    }

    /**
     * Displays help information for the menu options.
     */
    private void showHelp() {
        System.out.println("\n========= Help Menu =========");
        System.out.println("1. Initialize Simulation: Sets up cities, population, and graph.");
        System.out.println("2. Toggle Logging: Turns logging on or off.");
        System.out.println("3. Run Simulation: Starts the disease spread simulation.");
        System.out.println("4. Display Graph: Shows the current city graph.");
        System.out.println("5. Find Shortest Path: Finds the shortest path between two cities.");
        System.out.println("6. Display Sorted Population: Shows cities sorted by population.");
        System.out.println("7. Display All Cities: Lists all cities in the database.");
        System.out.println("8. Show Help: Displays this help menu.");
        System.out.println("9. Exit: Exits the simulation.");
    }

    /**
     * Displays the menu and returns the number of available options.
     */
    private int displayMenu() {
        System.out.println("\n========= Disease Spread Simulation Menu =========");
        int idx = 1;
        if (!isInitialized) {
            System.out.printf("%d. Initialize Simulation (Cities, Population, Graph)\n", idx++);
            System.out.printf("%d. Toggle Logging (Currently: %s)\n", idx++, MyLogger.isEnabled() ? "ON" : "OFF");
            System.out.printf("%d. Help/About\n", idx++);
            System.out.printf("%d. Exit\n", idx++);
        } else {
            System.out.printf("%d. Run Simulation\n", idx++);
            System.out.printf("%d. Display City Graph (Breadth-First Traversal)\n", idx++);
            System.out.printf("%d. Find Shortest Path Between Cities\n", idx++);
            System.out.printf("%d. Display Sorted Population\n", idx++);
            System.out.printf("%d. Show All Cities\n", idx++);
            System.out.printf("%d. Toggle Logging (Currently: %s)\n", idx++, MyLogger.isEnabled() ? "ON" : "OFF");
            System.out.printf("%d. Help/About\n", idx++);
            System.out.printf("%d. Exit\n", idx++);
        }
        System.out.print("Enter your choice: ");
        return idx - 1;
    }

    private int getChoice(int maxOption) {
        while (true) {
            String input = scanner.nextLine();
            try {
                int choice = Integer.parseInt(input.trim());
                if (choice >= 1 && choice <= maxOption) {
                    return choice;
                } else {
                    System.out.printf("Please enter a number between 1 and %d.\n", maxOption);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
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
        int days = -1;
        while (days <= 0) {
            String input = scanner.nextLine();
            try {
                days = Integer.parseInt(input.trim());
                if (days <= 0) {
                    System.out.println("Please enter a valid number of days (greater than 0).");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        simulation.runSimulation(days);
        System.out.printf("Simulation completed for %d day(s).\n", days);
    }

    private void displayGraph() {
        if (!isInitialized) {
            System.out.println("Please initialize the simulation first (Option 1).");
            return;
        }
        Graph graph = simulation.getPopulationManager().getCityGraph();
        System.out.print("Enter the starting city for traversal: ");
        String startCity = scanner.nextLine().trim();
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
        String startCity = scanner.nextLine().trim();
        System.out.print("Enter the destination city: ");
        String endCity = scanner.nextLine().trim();

        List<String> path = simulation.getPopulationManager().shortestPath(startCity, endCity);

        if (path != null && !path.isEmpty()) {
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
                System.out.printf("- %s (Population: %d, Risk: %s, Infection Rate: %.2f%%)\n",
                        city.getName(),
                        city.getResidents() != null ? city.getResidents().size() : 0,
                        city.getRiskLevel(),
                        city.getInfectionRate() * 100);
            }
        }
    }

    private void displaySortedPopulation() {
        if (!isInitialized) {
            System.out.println("Please initialize the simulation first (Option 1).");
            return;
        }
        System.out.print("Enter the city to display sorted population: ");
        String cityName = scanner.nextLine().trim();
        if (simulation.getCityService().getCityByName(cityName) == null){
            System.out.println("City not found");
            return;
        }
        List<Person> sortedPopulation = simulation.getPopulationManager().getSortedPopulation(cityName);

        if (sortedPopulation.isEmpty()) {
            System.out.println("No population found for " + cityName);
        } else {
            System.out.println("Sorted Population of " + cityName + " (by age):");
            String format = "| %-10s | %-3s | %-15s | %-18s | %-10s |%n";
            String line = String.format("+------------+-----+-----------------+--------------------+------------+");
            System.out.println(line);
            System.out.printf(format, "Name", "Age", "Health Status", "Infection Duration", "City");
            System.out.println(line);
            for (Person person : sortedPopulation) {
                System.out.printf(format,
                        person.getName(),
                        person.getAge(),
                        person.getHealthStatus(),
                        person.getInfectionDuration(),
                        person.getCityName());
            }
            System.out.println(line);
        }
    }

    public static void main(String[] args) throws CityService.CityAlreadyExistsException {
        ResourceBundle path = ResourceBundle.getBundle("strings");
        PersonDao personDao = new JsonPersonDatabase(path.getString("peoplesPath"));
        CityDao cityDao = new JsonCityDatabase(path.getString("citiesPath"));

        Simulation simulation = new Simulation(cityDao, personDao);
        CommandLineMenu menu = new CommandLineMenu(simulation);
        System.out.println("Welcome to the Disease Spread Simulation!");
        menu.runMenu();
        System.out.println("Thank you for using the simulation. Goodbye!");
    }
}

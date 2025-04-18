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
                    runSimulationStepByStep();
                } else if (choice == idx++) {
                    displayGraph();
                } else if (choice == idx++) {
                    findShortestPath();
                } else if (choice == idx++) {
                    findAllShortestPathsAndRisks();
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
        System.out.println("\n\033[1;36m========= Help Menu =========\033[0m");
        System.out.println("\033[1;35m1. Initialize Simulation:\033[0m Sets up cities, population, and graph.");
        System.out.println("\033[1;35m2. Toggle Logging:\033[0m Turns logging on or off.");
        System.out.println("\033[1;35m3. Run Simulation (all days):\033[0m Runs the simulation for a specified number of days with custom infection rate, recovery time, and initial infected.");
        System.out.println("\033[1;35m4. Run Simulation Step-by-Step:\033[0m Advance the simulation one day at a time, viewing summary stats after each day.");
        System.out.println("\033[1;35m5. Display Graph:\033[0m Shows the current city graph.");
        System.out.println("\033[1;35m6. Find Shortest Path:\033[0m Finds the shortest path between two cities.");
        System.out.println("\033[1;35m7. Display Sorted Population:\033[0m Shows cities sorted by population.");
        System.out.println("\033[1;35m8. Display All Cities:\033[0m Lists all cities in the database.");
        System.out.println("\033[1;35m9. Show Help:\033[0m Displays this help menu.");
        System.out.println("\033[1;35m10. Exit:\033[0m Exits the simulation.");
    }

    /**
     * Displays the menu and returns the number of available options.
     */
    private int displayMenu() {
        System.out.println("\n\033[1;34m========= Disease Spread Simulation Menu =========\033[0m");
        int idx = 1;
        if (!isInitialized) {
            System.out.printf("%d. Initialize Simulation (Cities, Population, Graph)\n", idx++);
            System.out.printf("%d. Toggle Logging (Currently: %s)\n", idx++, MyLogger.isEnabled() ? "ON" : "OFF");
            System.out.printf("%d. Help/About\n", idx++);
            System.out.printf("%d. Exit\n", idx++);
        } else {
            System.out.printf("%d. Run Simulation (all days)\n", idx++);
            System.out.printf("%d. Run Simulation Step-by-Step\n", idx++);
            System.out.printf("%d. Display City Graph (Breadth-First Traversal)\n", idx++);
            System.out.printf("%d. Find Shortest Path Between Cities\n", idx++);
            System.out.printf("%d. Find All Shortest Paths & Path Risks\n", idx++);
            System.out.printf("%d. Display Sorted Population\n", idx++);
            System.out.printf("%d. Show All Cities\n", idx++);
            System.out.printf("%d. Toggle Logging (Currently: %s)\n", idx++, MyLogger.isEnabled() ? "ON" : "OFF");
            System.out.printf("%d. Help/About\n", idx++);
            System.out.printf("%d. Exit\n", idx++);
        }
        System.out.print("\033[1;33mEnter your choice:\033[0m ");
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
            System.out.println("\033[1;32m[✓] Simulation initialized successfully!\033[0m");
            logger.info("Simulation initialized successfully.");
    }

    private void runSimulation() {
        if (!isInitialized) {
            System.out.println("Please initialize the simulation first (Option 1).");
            return;
        }
        System.out.print("\033[1;33mEnter the number of days to simulate:\033[0m ");
        int days = -1;
        while (days <= 0) {
            String input = scanner.nextLine();
            try {
                days = Integer.parseInt(input.trim());
                if (days <= 0) {
                    System.out.println("\033[1;31m[!] Please enter a valid number of days (greater than 0).\033[0m");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        System.out.print("Enter infection rate (e.g., 0.1 for 10%): ");
        double infectionRate = -1;
        while (infectionRate < 0 || infectionRate > 1) {
            String input = scanner.nextLine();
            try {
                infectionRate = Double.parseDouble(input.trim());
                if (infectionRate < 0 || infectionRate > 1) {
                    System.out.println("Please enter a value between 0 and 1.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a decimal value.");
            }
        }
        System.out.print("Enter recovery time (days): ");
        int recoveryTime = -1;
        while (recoveryTime <= 0) {
            String input = scanner.nextLine();
            try {
                recoveryTime = Integer.parseInt(input.trim());
                if (recoveryTime <= 0) {
                    System.out.println("Please enter a positive number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        System.out.print("Enter initial number of infected people per city: ");
        int initialInfected = -1;
        while (initialInfected < 0) {
            String input = scanner.nextLine();
            try {
                initialInfected = Integer.parseInt(input.trim());
                if (initialInfected < 0) {
                    System.out.println("Please enter a non-negative number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        simulation.setSimulationParameters(infectionRate, recoveryTime, initialInfected);
        simulation.runSimulation(days);
        simulation.printSummaryStatistics();
        System.out.printf("\033[1;32m[✓] Simulation completed for %d day(s).\033[0m\n", days);
    }

    private void runSimulationStepByStep() {
        if (!isInitialized) {
            System.out.println("Please initialize the simulation first (Option 1).");
            return;
        }
        System.out.print("Enter infection rate (e.g., 0.1 for 10%): ");
        double infectionRate = -1;
        while (infectionRate < 0 || infectionRate > 1) {
            String input = scanner.nextLine();
            try {
                infectionRate = Double.parseDouble(input.trim());
                if (infectionRate < 0 || infectionRate > 1) {
                    System.out.println("Please enter a value between 0 and 1.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a decimal value.");
            }
        }
        System.out.print("Enter recovery time (days): ");
        int recoveryTime = -1;
        while (recoveryTime <= 0) {
            String input = scanner.nextLine();
            try {
                recoveryTime = Integer.parseInt(input.trim());
                if (recoveryTime <= 0) {
                    System.out.println("Please enter a positive number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        System.out.print("Enter initial number of infected people per city: ");
        int initialInfected = -1;
        while (initialInfected < 0) {
            String input = scanner.nextLine();
            try {
                initialInfected = Integer.parseInt(input.trim());
                if (initialInfected < 0) {
                    System.out.println("Please enter a non-negative number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        simulation.setSimulationParameters(infectionRate, recoveryTime, initialInfected);
        int day = 1;
        while (true) {
            System.out.println("\n--- Day " + day + " ---");
            simulation.runSimulation(1);
            simulation.printSummaryStatistics();
            System.out.print("Press Enter to advance to the next day or type 'exit' to stop: ");
            String input = scanner.nextLine();
            if (input.trim().equalsIgnoreCase("exit")) {
                break;
            }
            day++;
        }
        System.out.println("Step-by-step simulation ended.");
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
            System.out.println("\033[1;34mBreadth-First Traversal from " + startCity + ":\033[0m");
            System.out.println("    [\033[1;32m" + String.join("\033[0m, \033[1;32m", traversal) + "\033[0m]");
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

    // New feature: Find all shortest paths and their risk
    private void findAllShortestPathsAndRisks() {
        if (!isInitialized) {
            System.out.println("Please initialize the simulation first (Option 1).");
            return;
        }
        System.out.print("Enter the starting city: ");
        String startCity = scanner.nextLine().trim();
        System.out.print("Enter the destination city: ");
        String endCity = scanner.nextLine().trim();

        List<List<String>> paths = simulation.allShortestPaths(startCity, endCity);
        if (paths == null || paths.isEmpty()) {
            System.out.println("No path found between " + startCity + " and " + endCity + ".");
            return;
        }
        System.out.println("All shortest paths from " + startCity + " to " + endCity + ":");
        int idx = 1;
        for (List<String> path : paths) {
            double totalWeight = simulation.totalPathWeight(path);
            double riskProduct = simulation.pathRiskProduct(path);
            System.out.printf("%d. Path: %s\n", idx++, path);
            System.out.printf("   Total Path Weight: %.3f\n", totalWeight);
            System.out.printf("   Path Risk (product of edge weights): %.5f\n", riskProduct);
        }
    }

    private void displayAllCities() {
        List<City> cities = simulation.getCityService().getAllCities();
        if (cities.isEmpty()) {
            System.out.println("No cities found.");
        } else {
            String format = "| %-12s | %-10s | %-15s | %-15s |%n";
            String line = "+--------------+------------+-----------------+-----------------+";
            System.out.println("\033[1;34mAll Cities:\033[0m");
            System.out.println(line);
            System.out.printf(format, "City", "Population", "Risk Level", "Infection Rate");
            System.out.println(line);
            for (City city : cities) {
            System.out.print("    ");
                System.out.printf(format,
                        city.getName(),
                        city.getResidents() != null ? city.getResidents().size() : 0,
                        city.getRiskLevel(),
                        String.format("%.2f%%", city.getInfectionRate() * 100));
            }
            System.out.println(line);
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

# Disease Spread Model Simulation

A Java console application that simulates the spread of a disease across a network of interconnected cities. It allows users to initialize cities and populations, configure connections, run simulations over days, and analyze paths and risks between cities.

---

## Features

- **City Management**: Add cities with random or custom population densities.
- **Population Initialization**: Generate random populations per city (configurable range).
- **Connection Graph**: Create bidirectional weighted edges between cities.
- **Simulation**:
  - Run full or step-by-step simulations with custom infection rate, recovery time, and initial infected count.
  - Spread infection probabilistically and update recovery.
  - Assign risk levels to cities based on current infection rate.
- **Statistics & Reporting**:
  - Console-based summary table of Susceptible, Infected, Recovered counts.
  - Display sorted populations by age or health status.
  - Export and view city list with details.
- **Graph Analysis**:
  - Breadth-First Traversal to display city connectivity.
  - Find the single shortest path (Dijkstra) between cities.
  - **Find all shortest paths** and calculate **path risks** (product of edge weights).

---

## Project Structure

```
DiseaseSpreadModel/
├── src/main/java/
│   ├── data/             # DAO interfaces & JSON implementations
│   ├── domain/
│   │   ├── city/         # City entity & risk levels
│   │   └── person/       # Person entity & health status
│   ├── service/          # Business logic and graph utilities
│   │   ├── Graph.java
│   │   ├── CityService.java
│   │   └── PopulationManager.java
│   └── presentation/     # Console UI and Simulation orchestrator
│       ├── Simulation.java
│       └── CommandLineMenu.java
├── src/main/resources/   # Configuration (e.g., strings.properties)
└── README.md             # Project documentation
```

---

## Prerequisites

- Java 11 or newer
- [Jackson Databind](https://github.com/FasterXML/jackson) on classpath for JSON serialization
- (Optional) Maven or Gradle for build automation

---

## Setup & Running

1. **Clone the repository**:
   ```bash
   git clone https://github.com/itshimelz/disease-spread-model.git
   cd DiseaseSpreadModel
   ```

2. **Configure JSON file paths** in `src/main/resources/strings.properties`:
   ```properties
   peoplesPath=path/to/people.json
   citiesPath=path/to/cities.json
   ```

3. **Build**:
   - **With Maven**:
     ```bash
     mvn clean compile
     ```
   - **Without Maven**:
     ```bash
     javac -d out src/main/java/**/*.java
     ```

4. **Run**:
   ```bash
   java -cp out presentation.CommandLineMenu
   ```

---

## Usage

Upon launch, the menu will present options:

1. **Initialize Simulation**: Setup cities, populate residents, and build the connection graph.
2. **Toggle Logging**: Enable or disable informational logs.
3. **Run Simulation (all days)**: Execute the simulation for N days.
4. **Run Simulation Step-by-Step**: Advance one day at a time and view summary stats after each day.
5. **Display City Graph**: Show BFS traversal order from a chosen start city.
6. **Find Shortest Path Between Cities**: Compute the minimal-weight path.
7. **Find All Shortest Paths & Path Risks**: List all minimal paths and their risk scores.
8. **Display Sorted Population**: View residents sorted by name, age, or health metrics.
9. **Display All Cities**: List city details in a formatted table.
10. **Help/About**: Show this help menu.
11. **Exit**: Quit the application.

---

## Architecture Details

- **Graph.java**: Implements a directed weighted graph with algorithms for BFS, Dijkstra, multi-path enumeration, and risk computation.
- **PopulationManager.java**: Manages city graph, population seeding, infection spread, and health updates.
- **CityService.java**: CRUD operations for cities via DAO.
- **Simulation.java**: Orchestrates simulation parameters, runs daily updates, and prints statistics.
- **CommandLineMenu.java**: Provides an interactive menu-driven CLI.
- **Domain Models**:
  - `City`: Holds details like name, density, connections, risk level, infection rate.
  - `Person`: Tracks health status, recovery time, infection duration, and city assignment.
- **Data Layer**: Interfaces `CityDao` and `PersonDao` with JSON-based storage (`JsonCityDatabase`, `JsonPersonDatabase`).

---

## Contributing

Contributions are welcome! Please fork the repo, create a feature branch, and submit a pull request.

---

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.

package domain.city;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import domain.person.Person;

import java.util.*;

public class City {
    @JsonProperty("name")
    private String name;
    @JsonProperty("residents")
    private List<Person> residents = new ArrayList<>(); // Initialize to avoid nulls
    @JsonProperty("connections")
    private Map<String, Double> connections = new HashMap<>(); // Key is String (city name)
    @JsonProperty("riskLevel")
    private RiskLevel riskLevel = RiskLevel.OUT_OF_DANGER; // Initialize
    @JsonProperty("infectionRate")
    private double infectionRate = 0.0;
    @JsonProperty("populationDensity")
    private double populationDensity;

    @JsonCreator // Important for deserialization when using constructor with parameters
    public City(@JsonProperty("name") String name, @JsonProperty("populationDensity") double populationDensity) {
        this.name = name;
        this.populationDensity = populationDensity;
    }

    // Getters (Important for serialization)
    public String getName() { return name; }
    public List<Person> getResidents() { return residents; }
    public Map<String, Double> getConnections() { return connections; }
    public RiskLevel getRiskLevel() { return riskLevel; }
    public double getInfectionRate() { return infectionRate; }
    public double getPopulationDensity() { return populationDensity; }

    // Setters (Important for deserialization)
    public void setResidents(List<Person> residents) { this.residents = residents; }
    public void setConnections(Map<String, Double> connections) { this.connections = connections; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }
    public void setInfectionRate(double infectionRate) { this.infectionRate = infectionRate; }
    public void setPopulationDensity(double populationDensity) { this.populationDensity = populationDensity; }

    public void addResident(Person resident) { this.residents.add(resident); }
    public void addConnection(String cityName, Double weight) { this.connections.put(cityName, weight); }

    @Override
    public String toString() {
        return "City{" +
                "name='" + name + '\'' +
                ", populationDensity=" + populationDensity +
                ", riskLevel=" + riskLevel +
                ", infectionRate=" + infectionRate +
                ", residents size=" + residents.size() +
                ", connections=" + connections +
                '}';
    }

}

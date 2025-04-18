package domain;

import java.util.List;
import java.util.Map;

public class City {
    private String name;
    private List<Person> residents;
    private Map<City, Double> connections;
    private RiskLevel riskLevel;

    private double infectionRate; // Percentage
    private double populationDensity; // people/km²

    public City(String name, List<Person> residents, Map<City, Double> connections, RiskLevel riskLevel) {
        this.name = name;
        this.residents = residents;
        this.connections = connections;
        this.riskLevel = riskLevel;
    }

    // Getter and Setter methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Person> getResidents() {
        return residents;
    }

    public void setResidents(List<Person> residents) {
        this.residents = residents;
    }

    public Map<City, Double> getConnections() {
        return connections;
    }

    public void setConnections(Map<City, Double> connections) {
        this.connections = connections;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public double getInfectionRate() {
        return infectionRate;
    }

    public void setInfectionRate(double infectionRate) {
        this.infectionRate = infectionRate;
    }

    public double getPopulationDensity() {
        return populationDensity;
    }

    public void setPopulationDensity(double populationDensity) {
        this.populationDensity = populationDensity;
    }

    // Method for updating the Risk Level
    public void updateRiskLevel() {
        if (infectionRate < 5 && populationDensity < 100) {
            this.riskLevel = RiskLevel.OUT_OF_DANGER;
        } else if (infectionRate >= 5 && infectionRate <= 20 && populationDensity >= 100 && populationDensity <= 500) {
            this.riskLevel = RiskLevel.AT_RISK;
        } else if (infectionRate > 20 && populationDensity > 500) {
            this.riskLevel = RiskLevel.HIGH_RISK;
        } else if(infectionRate > 20 && populationDensity <= 500){
            this.riskLevel = RiskLevel.HIGH_RISK;
        }
        else if(infectionRate <= 20 && populationDensity > 500){
            this.riskLevel = RiskLevel.HIGH_RISK;
        }
    }
}

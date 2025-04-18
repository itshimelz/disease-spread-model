package service;

import data.CityDao;
import domain.city.City;

import java.util.List;

public class CityService {
    private final CityDao cityDao;

    public CityService(CityDao cityDao) {
        this.cityDao = cityDao;
    }

    public void addCity(String name, double populationDensity) {
        if (cityDao.getCityByName(name) == null) {
            City city = new City(name, populationDensity);
            cityDao.addCity(city);
        } else {
            System.out.println("City with name " + name + " already exists.");
        }
    }

    public void addConnectionBetweenCity(String city1Name, String city2Name, double weight){
        City city1 = cityDao.getCityByName(city1Name);
        City city2 = cityDao.getCityByName(city2Name);

        if (city1 != null && city2 != null) {
            city1.addConnection(city2Name, weight); // Use city name directly in addConnection
            cityDao.updateCity(city1); // Update the city object in the DAO
        }
    }

    public List<City> getAllCities() {
        return cityDao.getAllCities();
    }

    public City getCityByName(String name) {
        return cityDao.getCityByName(name);
    }

    public void updateCity(City city) {
        if (cityDao.getCityByName(city.getName()) != null) {
            cityDao.updateCity(city);
        } else {
            System.out.println("City with name " + city.getName() + " not found.");
        }
    }

    public void deleteCity(String name) {
        if (cityDao.getCityByName(name) != null) {
            cityDao.deleteCity(name);
        } else {
            System.out.println("City with name " + name + " not found.");
        }
    }


    public static class CityAlreadyExistsException extends Exception {
        public CityAlreadyExistsException(String message) {
            super(message);
        }
    }
}

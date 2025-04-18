package data;

import domain.city.City;

import java.util.List;

public interface  CityDao {
    void addCity(City city);
    List<City> getAllCities();
    City getCityByName(String name);
    void updateCity(City city);
    void deleteCity(String name);
}

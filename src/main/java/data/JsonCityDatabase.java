package data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.type.CollectionType;
import domain.city.City;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsonCityDatabase implements CityDao {

    private final String filePath;
    private final ObjectMapper objectMapper;

    public JsonCityDatabase(String filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper();
    }

    private List<City> loadCities() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                Files.createDirectories(Paths.get(file.getParent()));
                Files.writeString(Paths.get(filePath), "[]");
                return new ArrayList<>();
            }
            return objectMapper.readValue(file, new TypeReference<>() {
            });
        } catch (IOException e) {
            System.err.println("Error loading cities from JSON: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void saveCities(List<City> cities) {
        try {
            objectMapper.writeValue(new File(filePath), cities);
        } catch (IOException e) {
            System.err.println("Error saving cities to JSON: " + e.getMessage());
        }
    }

    @Override
    public void addCity(City city) {
        List<City> cities = loadCities();
        cities.add(city);
        saveCities(cities);
    }

    @Override
    public void updateCity(City city) {
        List<City> cities = loadCities();
        cities.removeIf(c -> c.getName().equals(city.getName())); // Remove the old entry
        cities.add(city);
        saveCities(cities);
    }

    @Override
    public List<City> getAllCities() {
        return loadCities();
    }

    @Override
    public City getCityByName(String name) {
        List<City> cities = loadCities();
        return cities.stream().filter(city -> city.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public void deleteCity(String name) {
        List<City> cities = loadCities();
        cities.removeIf(city -> city.getName().equals(name));
        saveCities(cities);
    }
}

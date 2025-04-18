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

import java.util.concurrent.locks.ReentrantLock;

public class JsonCityDatabase implements CityDao {
    // In-memory cache for cities
    private final List<City> cityCache = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final String filePath;
    private final ObjectMapper objectMapper;
    private boolean cacheLoaded = false;

    public JsonCityDatabase(String filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper();
    }

    // Load cache from disk if not loaded
    private void ensureCacheLoaded() {
        lock.lock();
        try {
            if (!cacheLoaded) {
                File file = new File(filePath);
                if (!file.exists()) {
                    Files.createDirectories(Paths.get(file.getParent()));
                    Files.writeString(Paths.get(filePath), "[]");
                    cityCache.clear();
                } else {
                    List<City> loaded = objectMapper.readValue(file, new TypeReference<>() {});
                    cityCache.clear();
                    cityCache.addAll(loaded);
                }
                cacheLoaded = true;
            }
        } catch (IOException e) {
            System.err.println("Error loading cities from JSON: " + e.getMessage());
            cityCache.clear();
        } finally {
            lock.unlock();
        }
    }

    // Save cache to disk
    private void saveCacheToDisk() {
        lock.lock();
        try {
            objectMapper.writeValue(new File(filePath), cityCache);
        } catch (IOException e) {
            System.err.println("Error saving cities to JSON: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void addCity(City city) {
        ensureCacheLoaded();
        lock.lock();
        try {
            cityCache.add(city);
            saveCacheToDisk();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void updateCity(City city) {
        ensureCacheLoaded();
        lock.lock();
        try {
            cityCache.removeIf(c -> c.getName().equals(city.getName()));
            cityCache.add(city);
            saveCacheToDisk();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<City> getAllCities() {
        ensureCacheLoaded();
        lock.lock();
        try {
            return new ArrayList<>(cityCache);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public City getCityByName(String name) {
        ensureCacheLoaded();
        lock.lock();
        try {
            return cityCache.stream().filter(city -> city.getName().equals(name)).findFirst().orElse(null);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void deleteCity(String name) {
        ensureCacheLoaded();
        lock.lock();
        try {
            cityCache.removeIf(city -> city.getName().equals(name));
            saveCacheToDisk();
        } finally {
            lock.unlock();
        }
    }
}

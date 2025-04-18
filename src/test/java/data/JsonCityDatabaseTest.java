package data;

import domain.city.City;
import org.junit.jupiter.api.*;
import java.io.File;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class JsonCityDatabaseTest {
    private static final String TEST_FILE = "test_cities.json";
    private JsonCityDatabase db;

    @BeforeEach
    void setUp() {
        File f = new File(TEST_FILE);
        if (f.exists()) f.delete();
        db = new JsonCityDatabase(TEST_FILE);
    }

    @AfterEach
    void tearDown() {
        File f = new File(TEST_FILE);
        if (f.exists()) f.delete();
    }

    @Test
    void testAddAndGetCity() {
        City city = new City("TestCity", 10.0);
        db.addCity(city);
        City fetched = db.getCityByName("TestCity");
        assertNotNull(fetched);
        assertEquals("TestCity", fetched.getName());
    }

    @Test
    void testUpdateCity() {
        City city = new City("TestCity", 10.0);
        db.addCity(city);
        city.setPopulationDensity(20.0);
        db.updateCity(city);
        City fetched = db.getCityByName("TestCity");
        assertEquals(20.0, fetched.getPopulationDensity());
    }

    @Test
    void testDeleteCity() {
        City city = new City("TestCity", 10.0);
        db.addCity(city);
        db.deleteCity("TestCity");
        assertNull(db.getCityByName("TestCity"));
    }

    @Test
    void testGetAllCities() {
        db.addCity(new City("A", 1.0));
        db.addCity(new City("B", 2.0));
        List<City> all = db.getAllCities();
        assertTrue(all.size() >= 2);
    }
}

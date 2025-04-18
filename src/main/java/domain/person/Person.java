package domain.person;

import domain.utils.JSONLoader;

import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class Person {
    private String name;
    private int age;
    private HealthStatus healthStatus;
    private int infectionDuration;
    private String cityName; // Add city name


    private static List<String> personNames;
    private int recoveryTime;

    static {
        ResourceBundle bundle = ResourceBundle.getBundle("strings");
        String personNamesPath = bundle.getString("personNamesPath");
        personNames = JSONLoader.loadListFromJSON(personNamesPath, "Default Name");
    }

    public Person() {
    }

    public Person(String name, int age, String cityName) { // Add cityName to constructor
        this.name = name;
        this.age = age;
        this.healthStatus = HealthStatus.SUSCEPTIBLE;
        this.infectionDuration = 0;
        this.cityName = cityName;
        this.recoveryTime = getRecoveryTime(age);

    }

    public String getName() {
        return name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public static List<String> getPersonNames() {
        return personNames;
    }

    public static void setPersonNames(List<String> personNames) {
        Person.personNames = personNames;
    }

    public int getRecoveryTime() {
        return recoveryTime;
    }

    public void setHealthStatus(HealthStatus healthStatus) {
        this.healthStatus = healthStatus;
    }

    public void setInfectionDuration(int infectionDuration) {
        this.infectionDuration = infectionDuration;
    }

    public HealthStatus getHealthStatus() {
        return healthStatus;
    }

    public int getInfectionDuration() {
        return infectionDuration;
    }

    public String getCityName() {
        return cityName;
    }

    private int getRecoveryTime(int age) {
        if (age <= 14) {
            return 14;
        } else if (age <= 24) {
            return 7;
        } else if (age <= 50) {
            return 14;
        } else {
            return 18;
        }
    }

    public void infect() {
        if (this.healthStatus == HealthStatus.SUSCEPTIBLE) {
            this.healthStatus = HealthStatus.INFECTED;
            this.infectionDuration = 1;
        }
    }

    public void updateHealthStatus() {
        if (this.healthStatus == HealthStatus.INFECTED) {
            this.infectionDuration++;
            if (this.infectionDuration > recoveryTime) {
                this.healthStatus = HealthStatus.RECOVERED;
                this.infectionDuration = 0;
            }
        }
    }

    public static Person createRandomPerson(int maxAge, String cityName) {
        Random random = new Random();
        int age = random.nextInt(maxAge + 1);
        String randomName;
        if (personNames != null && !personNames.isEmpty()) {
            randomName = personNames.get(random.nextInt(personNames.size()));
        } else {
            randomName = "Default Name";
        }
        return new Person(randomName, age, cityName);
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", healthStatus=" + healthStatus +
                ", infectionDuration=" + infectionDuration +
                ", cityName='" + cityName + '\'' + // Include cityName in toString
                '}';
    }
}
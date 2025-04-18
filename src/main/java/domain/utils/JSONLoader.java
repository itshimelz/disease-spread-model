package domain.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JSONLoader {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static List<String> loadListFromJSON(String filePath, String defaultValue) {
        List<String> list = new ArrayList<>();
        try {
            File file = new File(filePath);
            if (file.exists()) {
                JsonNode rootNode = objectMapper.readTree(file);
                if (rootNode.isArray()) {
                    ArrayNode arrayNode = (ArrayNode) rootNode;
                    for (JsonNode node : arrayNode) {
                        list.add(node.asText());
                    }
                } else {
                    System.err.println(filePath + " is not a valid JSON array.");
                    list.add(defaultValue);
                }
            } else {
                System.err.println(filePath + " file not found.");
                list.add(defaultValue);
            }
        } catch (IOException e) {
            System.err.println("Error loading list from JSON: " + e.getMessage());
            list.add(defaultValue);
        }
        return list;
    }
}


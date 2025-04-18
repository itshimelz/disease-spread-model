package service;

import java.util.*;

public class Graph {
    private Map<String, List<Edge>> adjacencyList;

    public Graph() {
        this.adjacencyList = new HashMap<>();
    }

    public void addVertex(String vertex) {
        adjacencyList.putIfAbsent(vertex, new ArrayList<>());
    }

    public void addEdge(String source, String destination, double weight) {
        if (!adjacencyList.containsKey(source) || !adjacencyList.containsKey(destination)) {
            throw new IllegalArgumentException("Vertices must exist in the graph.");
        }
        adjacencyList.get(source).add(new Edge(destination, weight));
    }

    public List<Edge> getEdges(String vertex) {
        return adjacencyList.getOrDefault(vertex, new ArrayList<>());
    }

    public Set<String> getVertices() {
        return adjacencyList.keySet();
    }

    public boolean hasVertex(String vertex){
        return adjacencyList.containsKey(vertex);
    }

    public boolean hasEdge(String source, String destination){
        if(adjacencyList.containsKey(source)){
            for(Edge edge: adjacencyList.get(source)){
                if(edge.destination.equals(destination)){
                    return true;
                }
            }
        }
        return false;
    }

    public double getEdgeWeight(String source, String destination){
        if(adjacencyList.containsKey(source)){
            for(Edge edge: adjacencyList.get(source)){
                if(edge.destination.equals(destination)){
                    return edge.weight;
                }
            }
        }
        return -1; // Or throw an exception if you prefer
    }

    public List<String> breadthFirstTraversal(String startVertex) {
        List<String> traversalOrder = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        if (!adjacencyList.containsKey(startVertex)) {
            return traversalOrder; // Or throw an exception
        }

        queue.add(startVertex);
        visited.add(startVertex);

        while (!queue.isEmpty()) {
            String currentVertex = queue.poll();
            traversalOrder.add(currentVertex);

            for (Edge edge : getEdges(currentVertex)) {
                if (!visited.contains(edge.destination)) {
                    queue.add(edge.destination);
                    visited.add(edge.destination);
                }
            }
        }

        return traversalOrder;
    }

    public List<String> shortestPath(String startCity, String endCity) {
        if (!adjacencyList.containsKey(startCity) || !adjacencyList.containsKey(endCity)) {
            return null;
        }

        Map<String, String> previous = new HashMap<>();
        Map<String, Double> distances = new HashMap<>();
        PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparingDouble(distances::get));

        for (String vertex : adjacencyList.keySet()) {
            distances.put(vertex, Double.POSITIVE_INFINITY);
        }

        distances.put(startCity, 0.0);
        queue.add(startCity);

        while (!queue.isEmpty()) {
            String current = queue.poll();

            if (current.equals(endCity)) {
                break; // Found the shortest path
            }

            for (Edge edge : getEdges(current)) {
                double newDist = distances.get(current) + edge.weight;
                if (newDist < distances.get(edge.destination)) {
                    distances.put(edge.destination, newDist);
                    previous.put(edge.destination, current);
                    queue.remove(edge.destination); // Important for PriorityQueue update
                    queue.add(edge.destination);
                }
            }
        }

        return reconstructPath(previous, startCity, endCity);
    }

    private List<String> reconstructPath(Map<String, String> previous, String startCity, String endCity) {
        List<String> path = new ArrayList<>();
        String current = endCity;

        while (current != null) {
            path.add(0, current);
            current = previous.get(current);
        }

        if (path.isEmpty() || !path.get(0).equals(startCity)) {
            return null; // No path found
        }

        return path;
    }

    public Set<String> getAdjacentVertices(String vertex){
        Set<String> adjacentVertices = new HashSet<>();
        if(adjacencyList.containsKey(vertex)){
            for(Edge edge: adjacencyList.get(vertex)){
                adjacentVertices.add(edge.destination);
            }
        }
        return adjacentVertices;
    }


    public static class Edge {
        public String destination;
        public double weight;

        public Edge(String destination, double weight) {
            this.destination = destination;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return "Edge{" +
                    "destination='" + destination + '\'' +
                    ", weight=" + weight +
                    '}';
        }
    }
}
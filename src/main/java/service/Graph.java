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

    // Returns all shortest paths between startCity and endCity (BFS-based, for weighted graphs finds minimal total weight paths)
    public List<List<String>> allShortestPaths(String startCity, String endCity) {
        List<List<String>> result = new ArrayList<>();
        if (!adjacencyList.containsKey(startCity) || !adjacencyList.containsKey(endCity)) {
            return result;
        }
        // Dijkstra's algorithm to get the minimum distance
        Map<String, Double> minDist = new HashMap<>();
        for (String v : adjacencyList.keySet()) minDist.put(v, Double.POSITIVE_INFINITY);
        minDist.put(startCity, 0.0);
        Queue<List<String>> queue = new LinkedList<>();
        queue.add(List.of(startCity));
        double shortest = Double.POSITIVE_INFINITY;
        while (!queue.isEmpty()) {
            List<String> path = queue.poll();
            String last = path.get(path.size() - 1);
            double pathWeight = totalPathWeight(path);
            if (pathWeight > shortest) continue; // Prune longer paths
            if (last.equals(endCity)) {
                if (result.isEmpty() || Math.abs(pathWeight - shortest) < 1e-9) {
                    result.add(new ArrayList<>(path));
                    shortest = pathWeight;
                } else if (pathWeight < shortest) {
                    result.clear();
                    result.add(new ArrayList<>(path));
                    shortest = pathWeight;
                }
                continue;
            }
            for (Edge edge : getEdges(last)) {
                if (!path.contains(edge.destination)) { // avoid cycles
                    double nextWeight = pathWeight + edge.weight;
                    if (nextWeight - shortest > 1e-9) continue; // prune
                    List<String> nextPath = new ArrayList<>(path);
                    nextPath.add(edge.destination);
                    queue.add(nextPath);
                }
            }
        }
        return result;
    }

    // Helper: returns total weight of a path
    public double totalPathWeight(List<String> path) {
        double sum = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            sum += getEdgeWeight(path.get(i), path.get(i + 1));
        }
        return sum;
    }

    // Helper: returns product of edge weights (risk probability) along a path
    public double pathRiskProduct(List<String> path) {
        double prod = 1.0;
        for (int i = 0; i < path.size() - 1; i++) {
            prod *= getEdgeWeight(path.get(i), path.get(i + 1));
        }
        return prod;
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
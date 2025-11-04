package graph;

import java.util.*;

public class Graph {
    private int n;
    private boolean directed;
    private List<List<Edge>> adj;

    public Graph(int n, boolean directed) {
        this.n = n;
        this.directed = directed;
        this.adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
    }

    public void addEdge(int u, int v, int weight) {
        adj.get(u).add(new Edge(v, weight));
        if (!directed) {
            adj.get(v).add(new Edge(u, weight));
        }
    }

    public List<Edge> getNeighbors(int u) {
        return adj.get(u);
    }

    public int size() {
        return n;
    }

    public boolean isDirected() {
        return directed;
    }

    public static class Edge {
        public int to, weight;

        public Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }
}
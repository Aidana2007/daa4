import graph.Graph;
import graph.Tarjan;
import graph.TopologicalSort;
import graph.DAGShortestPath;
import metrics.Metrics;
import utils.JsonParser;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("=== Smart City Scheduling Analysis ===\n");

            Graph graph = JsonParser.parseGraph("tasks.json");
            int source = JsonParser.getSource("tasks.json");

            System.out.println("Graph: " + graph.size() + " vertices");
            System.out.println("Source node: " + source);

            System.out.println("\n1. STRONGLY CONNECTED COMPONENTS");
            Metrics sccMetrics = new Metrics();
            Tarjan tarjan = new Tarjan(graph, sccMetrics);
            List<List<Integer>> components = tarjan.findSCCs();

            System.out.println("Found " + components.size() + " SCCs:");
            for (int i = 0; i < components.size(); i++) {
                System.out.println("  SCC " + i + ": " + components.get(i));
            }
            System.out.println("Operations: " + sccMetrics.getOperationCount());

            System.out.println("\n2. CONDENSATION GRAPH");
            Metrics condMetrics = new Metrics();
            Graph condensation = TopologicalSort.buildCondensationGraph(graph, components, condMetrics);
            System.out.println("Condensation graph: " + condensation.size() + " vertices");
            System.out.println("Operations: " + condMetrics.getOperationCount());

            System.out.println("\n3. TOPOLOGICAL SORT");
            Metrics topoMetrics = new Metrics();
            List<Integer> topoOrder = TopologicalSort.kahnTopologicalSort(condensation, topoMetrics);
            System.out.println("Topological order: " + topoOrder);
            System.out.println("Operations: " + topoMetrics.getOperationCount());

            System.out.println("\n4. SHORTEST PATHS from source " + source);
            Metrics spMetrics = new Metrics();
            DAGShortestPath.PathResult shortest = DAGShortestPath.findShortestPaths(graph, source, spMetrics);

            for (int i = 0; i < graph.size(); i++) {
                String dist = shortest.distances[i] == Integer.MAX_VALUE ? "∞" : String.valueOf(shortest.distances[i]);
                System.out.println("  to " + i + ": distance = " + dist + ", path = " + shortest.reconstructPath(i));
            }
            System.out.println("Operations: " + spMetrics.getOperationCount());

            System.out.println("\n5. LONGEST PATHS from source " + source);
            Metrics lpMetrics = new Metrics();
            DAGShortestPath.PathResult longest = DAGShortestPath.findLongestPaths(graph, source, lpMetrics);

            for (int i = 0; i < graph.size(); i++) {
                String dist = longest.distances[i] == Integer.MAX_VALUE ? "∞" : String.valueOf(longest.distances[i]);
                System.out.println("  to " + i + ": distance = " + dist + ", path = " + longest.reconstructPath(i));
            }
            System.out.println("Operations: " + lpMetrics.getOperationCount());

            System.out.println("\n6. CRITICAL PATH");
            Metrics cpMetrics = new Metrics();
            DAGShortestPath.CriticalPathResult critical = DAGShortestPath.findCriticalPath(graph, cpMetrics);
            System.out.println("Critical path length: " + critical.length);
            System.out.println("Critical path: " + critical.path);
            System.out.println("Operations: " + cpMetrics.getOperationCount());

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
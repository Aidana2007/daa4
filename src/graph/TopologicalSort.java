package graph;
import metrics.Metrics;
import java.util.*;

public class TopologicalSort {

    public static Graph buildCondensationGraph(Graph original, List<List<Integer>> components, Metrics metrics) {
        metrics.startTimer();

        Graph condensation = new Graph(components.size(), true);
        int[] compId = new int[original.size()];

        for (int i = 0; i < components.size(); i++) {
            for (int vertex : components.get(i)) {
                compId[vertex] = i;
                metrics.incrementOperation();
            }
        }

        Set<String> addedEdges = new HashSet<>();
        for (int u = 0; u < original.size(); u++) {
            for (Graph.Edge edge : original.getNeighbors(u)) {
                metrics.incrementOperation();
                int compU = compId[u];
                int compV = compId[edge.to];

                if (compU != compV) {
                    String edgeKey = compU + "->" + compV;
                    if (!addedEdges.contains(edgeKey)) {
                        condensation.addEdge(compU, compV, edge.weight);
                        addedEdges.add(edgeKey);
                    }
                }
            }
        }

        metrics.stopTimer();
        return condensation;
    }

    public static List<Integer> kahnTopologicalSort(Graph dag, Metrics metrics) {
        metrics.startTimer();

        int n = dag.size();
        int[] inDegree = new int[n];

        for (int u = 0; u < n; u++) {
            for (Graph.Edge edge : dag.getNeighbors(u)) {
                inDegree[edge.to]++;
                metrics.incrementOperation();
            }
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
            }
            metrics.incrementOperation();
        }

        List<Integer> topoOrder = new ArrayList<>();
        while (!queue.isEmpty()) {
            int u = queue.poll();
            topoOrder.add(u);
            metrics.incrementOperation();

            for (Graph.Edge edge : dag.getNeighbors(u)) {
                inDegree[edge.to]--;
                metrics.incrementOperation();
                if (inDegree[edge.to] == 0) {
                    queue.offer(edge.to);
                }
            }
        }

        metrics.stopTimer();
        return topoOrder;
    }
}

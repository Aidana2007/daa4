package graph;
import metrics.Metrics;
import java.util.*;

public class DAGShortestPath {

    public static class PathResult {
        public int[] distances;
        public int[] predecessors;

        public PathResult(int[] distances, int[] predecessors) {
            this.distances = distances;
            this.predecessors = predecessors;
        }

        public List<Integer> reconstructPath(int target) {
            if (distances[target] == Integer.MAX_VALUE) {
                return new ArrayList<>();
            }

            List<Integer> path = new ArrayList<>();
            for (int at = target; at != -1; at = predecessors[at]) {
                path.add(at);
            }
            Collections.reverse(path);
            return path;
        }
    }

    private static List<Integer> topologicalSort(Graph dag, Metrics metrics) {
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

        List<Integer> order = new ArrayList<>();
        while (!queue.isEmpty()) {
            int u = queue.poll();
            order.add(u);
            metrics.incrementOperation();

            for (Graph.Edge edge : dag.getNeighbors(u)) {
                inDegree[edge.to]--;
                metrics.incrementOperation();
                if (inDegree[edge.to] == 0) {
                    queue.offer(edge.to);
                }
            }
        }

        return order;
    }

    public static PathResult findShortestPaths(Graph dag, int source, Metrics metrics) {
        metrics.startTimer();

        int n = dag.size();
        int[] dist = new int[n];
        int[] pred = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(pred, -1);
        dist[source] = 0;

        List<Integer> topoOrder = topologicalSort(dag, metrics);

        for (int u : topoOrder) {
            metrics.incrementOperation();
            if (dist[u] != Integer.MAX_VALUE) {
                for (Graph.Edge edge : dag.getNeighbors(u)) {
                    metrics.incrementOperation();
                    int newDist = dist[u] + edge.weight;
                    if (newDist < dist[edge.to]) {
                        dist[edge.to] = newDist;
                        pred[edge.to] = u;
                    }
                }
            }
        }

        metrics.stopTimer();
        return new PathResult(dist, pred);
    }

    public static PathResult findLongestPaths(Graph dag, int source, Metrics metrics) {
        metrics.startTimer();

        Graph negatedGraph = new Graph(dag.size(), true);
        for (int u = 0; u < dag.size(); u++) {
            for (Graph.Edge edge : dag.getNeighbors(u)) {
                negatedGraph.addEdge(u, edge.to, -edge.weight);
                metrics.incrementOperation();
            }
        }

        PathResult result = findShortestPaths(negatedGraph, source, metrics);

        for (int i = 0; i < result.distances.length; i++) {
            if (result.distances[i] != Integer.MAX_VALUE) {
                result.distances[i] = -result.distances[i];
            }
        }

        metrics.stopTimer();
        return result;
    }

    public static CriticalPathResult findCriticalPath(Graph dag, Metrics metrics) {
        metrics.startTimer();

        int maxLength = Integer.MIN_VALUE;
        List<Integer> criticalPath = new ArrayList<>();

        for (int source = 0; source < dag.size(); source++) {
            Metrics spMetrics = new Metrics();
            PathResult result = findLongestPaths(dag, source, spMetrics);
            metrics.incrementOperation(spMetrics.getOperationCount());

            for (int target = 0; target < dag.size(); target++) {
                if (result.distances[target] != Integer.MAX_VALUE &&
                        result.distances[target] > maxLength) {
                    maxLength = result.distances[target];
                    criticalPath = result.reconstructPath(target);
                }
                metrics.incrementOperation();
            }
        }

        metrics.stopTimer();
        return new CriticalPathResult(criticalPath, maxLength);
    }

    public static class CriticalPathResult {
        public List<Integer> path;
        public int length;

        public CriticalPathResult(List<Integer> path, int length) {
            this.path = path;
            this.length = length;
        }
    }
}
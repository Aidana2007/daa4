package graph;

import metrics.Metrics;
import java.util.*;

public class Tarjan {
    private Graph graph;
    private Metrics metrics;
    private int index;
    private int[] indices, lowLinks;
    private boolean[] onStack;
    private Stack<Integer> stack;
    private List<List<Integer>> components;

    public Tarjan(Graph graph, Metrics metrics) {
        this.graph = graph;
        this.metrics = metrics;
        this.indices = new int[graph.size()];
        this.lowLinks = new int[graph.size()];
        this.onStack = new boolean[graph.size()];
        this.stack = new Stack<>();
        this.components = new ArrayList<>();
        Arrays.fill(indices, -1);
    }

    public List<List<Integer>> findSCCs() {
        metrics.startTimer();

        for (int v = 0; v < graph.size(); v++) {
            if (indices[v] == -1) {
                strongConnect(v);
            }
        }

        metrics.stopTimer();
        return components;
    }

    private void strongConnect(int v) {
        metrics.incrementOperation();

        indices[v] = index;
        lowLinks[v] = index;
        index++;
        stack.push(v);
        onStack[v] = true;

        for (Graph.Edge edge : graph.getNeighbors(v)) {
            metrics.incrementOperation();
            int w = edge.to;

            if (indices[w] == -1) {
                strongConnect(w);
                lowLinks[v] = Math.min(lowLinks[v], lowLinks[w]);
            } else if (onStack[w]) {
                lowLinks[v] = Math.min(lowLinks[v], indices[w]);
            }
        }

        if (lowLinks[v] == indices[v]) {
            List<Integer> component = new ArrayList<>();
            int w;
            do {
                w = stack.pop();
                onStack[w] = false;
                component.add(w);
                metrics.incrementOperation();
            } while (w != v);
            components.add(component);
        }
    }
}

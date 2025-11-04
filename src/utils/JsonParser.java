package utils;

import graph.Graph;
import java.io.*;
import java.nio.file.*;

public class JsonParser {
    public static Graph parseGraph(String filename) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get("data", filename)));

        boolean directed = content.contains("\"directed\": true");

        int nStart = content.indexOf("\"n\":") + 4;
        int nEnd = content.indexOf(",", nStart);
        int n = Integer.parseInt(content.substring(nStart, nEnd).trim());

        Graph graph = new Graph(n, directed);

        int edgesStart = content.indexOf("\"edges\":") + 8;
        int edgesEnd = content.indexOf("]", edgesStart) + 1;
        String edgesStr = content.substring(edgesStart, edgesEnd);

        String[] edges = edgesStr.split("\\},\\s*\\{");
        for (String edge : edges) {
            edge = edge.replace("{", "").replace("}", "").trim();
            String[] parts = edge.split(",");

            int u = Integer.parseInt(parts[0].split(":")[1].trim());
            int v = Integer.parseInt(parts[1].split(":")[1].trim());
            int w = Integer.parseInt(parts[2].split(":")[1].trim());

            graph.addEdge(u, v, w);
        }

        return graph;
    }

    public static int getSource(String filename) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get("data", filename)));
        int sourceStart = content.indexOf("\"source\":") + 9;
        int sourceEnd = content.indexOf(",", sourceStart);
        if (sourceEnd == -1) sourceEnd = content.indexOf("}", sourceStart);
        return Integer.parseInt(content.substring(sourceStart, sourceEnd).trim());
    }
}
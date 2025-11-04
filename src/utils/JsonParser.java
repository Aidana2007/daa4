package utils;

import graph.Graph;
import java.io.*;
import java.nio.file.*;

public class JsonParser {
    public static Graph parseGraph(String filename) throws IOException {
        String content = Files.readString(Paths.get("data", filename));

        content = content.replaceAll("\\s+", " ").trim();

        boolean directed = content.contains("\"directed\": true");

        int nIndex = content.indexOf("\"n\":") + 4;
        int nEnd = findNextDelimiter(content, nIndex);
        int n = Integer.parseInt(content.substring(nIndex, nEnd).trim());

        Graph graph = new Graph(n, directed);

        int edgesStart = content.indexOf("\"edges\":") + 8;
        int edgesEnd = content.indexOf("]", edgesStart) + 1;
        String edgesSection = content.substring(edgesStart, edgesEnd);

        int pos = 0;
        while ((pos = edgesSection.indexOf("{", pos)) != -1) {
            int edgeEnd = edgesSection.indexOf("}", pos);
            if (edgeEnd == -1) break;

            String edgeStr = edgesSection.substring(pos + 1, edgeEnd);

            int uStart = edgeStr.indexOf("\"u\":") + 4;
            int uEnd = findNextDelimiter(edgeStr, uStart);
            int u = Integer.parseInt(edgeStr.substring(uStart, uEnd).trim());

            int vStart = edgeStr.indexOf("\"v\":") + 4;
            int vEnd = findNextDelimiter(edgeStr, vStart);
            int v = Integer.parseInt(edgeStr.substring(vStart, vEnd).trim());

            int wStart = edgeStr.indexOf("\"w\":") + 4;
            int wEnd = findNextDelimiter(edgeStr, wStart);
            int w = Integer.parseInt(edgeStr.substring(wStart, wEnd).trim());

            graph.addEdge(u, v, w);
            pos = edgeEnd + 1;
        }

        return graph;
    }

    public static int getSource(String filename) throws IOException {
        String content = Files.readString(Paths.get("data", filename));
        content = content.replaceAll("\\s+", " ").trim();

        int sourceStart = content.indexOf("\"source\":") + 9;
        int sourceEnd = findNextDelimiter(content, sourceStart);
        return Integer.parseInt(content.substring(sourceStart, sourceEnd).trim());
    }

    private static int findNextDelimiter(String content, int start) {
        for (int i = start; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c == ',' || c == '}' || c == ']') {
                return i;
            }
        }
        return content.length();
    }
}
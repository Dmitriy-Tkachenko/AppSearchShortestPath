package com.university.project;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;

import java.util.ArrayList;
import java.util.List;

public class Logic {

    Graph<String, StringBuilder> getGraph (List<String[]> rows) {
        Graph<String, StringBuilder> graph = new SparseMultigraph<>();

        for (int i = 1; i < rows.size(); i++) {
            for (int j = 1; j < rows.get(i).length; j++) {
                if (rows.get(i)[j].equals("@") || rows.get(i)[j].equals("+") || rows.get(i)[j].equals("=") || rows.get(i)[j].equals("%")) {
                    if (!rows.get(i)[0].equals(rows.get(0)[j]) && !graph.isSuccessor(rows.get(i)[0], rows.get(0)[j])) {
                        graph.addVertex(rows.get(i)[0]);
                        graph.addEdge(new StringBuilder(""), rows.get(i)[0], rows.get(0)[j]);
                    }
                } else if (rows.get(i)[j].matches("\\d+(\\.\\d+)?")){
                    if (!rows.get(i)[0].equals(rows.get(0)[j]) && !graph.isSuccessor(rows.get(i)[0], rows.get(0)[j])) {
                        graph.addVertex(rows.get(i)[0]);
                        graph.addEdge(new StringBuilder(rows.get(i)[j]), rows.get(i)[0], rows.get(0)[j]);
                    }
                } else {
                    graph.addVertex(rows.get(i)[0]);
                }
            }
        }
        return graph;
    }

    ArrayList<String> distDijkstra(List<String[]> rows) {
        String[][] vertexGraph = fillVertexGraph(rows);
        double[][] vertexLessGraph = fillVertexLessGraph(rows);

        GraphAlgorithms graphAlgorithms = new GraphAlgorithms(vertexGraph, vertexLessGraph);
        GraphAlgorithms.Dijkstra dijkstra = graphAlgorithms.new Dijkstra();

        return dijkstra.findTheShortestPath();
    }

    ArrayList<String> distFloyd(List<String[]> rows) {
        String[][] vertexGraph = fillVertexGraph(rows);
        double[][] vertexLessGraph = fillVertexLessGraph(rows);

        GraphAlgorithms graphAlgorithms = new GraphAlgorithms(vertexGraph, vertexLessGraph);
        GraphAlgorithms.Floyd floyd = graphAlgorithms.new Floyd();

        return floyd.findTheShortestPath();
    }

    ArrayList<String> distBellmanFord(List<String[]> rows) {
        String[][] vertexGraph = fillVertexGraph(rows);
        double[][] vertexLessGraph = fillVertexLessGraph(rows);

        GraphAlgorithms graphAlgorithms = new GraphAlgorithms(vertexGraph, vertexLessGraph);
        GraphAlgorithms.BellmanFord bellmanFord = graphAlgorithms.new BellmanFord();

        return bellmanFord.findTheShortestPath();
    }

    private String[][] fillVertexGraph(List<String[]> rows) {
        final int SIZE = rows.size();
        String[][] vertexGraph = new String[SIZE][SIZE];

        for (int i = 0; i < rows.size(); i++) {
            System.arraycopy(rows.get(i), 0, vertexGraph[i], 0, rows.get(i).length);
        }
        return vertexGraph;
    }

    private double[][] fillVertexLessGraph(List<String[]> rows) {
        final int SIZE = rows.size() - 1;
        double[][] vertexLessGraph = new double[SIZE][SIZE];

        for (int i = 1; i < rows.size(); i++) {
            for (int j = 1; j < rows.get(i).length; j++) {
                if (!rows.get(i)[j].equals("")) {
                    vertexLessGraph[i - 1][j - 1] = Double.parseDouble(rows.get(i)[j]);
                } else {
                    vertexLessGraph[i - 1][j - 1] = 0;
                }
            }
        }

        return vertexLessGraph;
    }
}

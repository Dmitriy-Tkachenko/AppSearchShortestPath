package com.university.project;

import java.util.ArrayList;

public class GraphAlgorithms {
    private String[][] vertexGraph;
    private double[][] vertexLessGraph;
    private int SIZE;

    GraphAlgorithms(String[][] vertexGraph, double[][] vertexLessGraph) {
        this.vertexGraph = vertexGraph;
        this.vertexLessGraph = vertexLessGraph;
        SIZE = vertexLessGraph.length;
    }

    private ArrayList<String> pathRestoration(double[] dist) {
        ArrayList<String> minPathVertices = new ArrayList<>(); // массив посещенных вершин
        int indexEndVertex = SIZE - 1; // индекс конечной вершины = 7 - 1
        minPathVertices.add(0, vertexGraph[0][indexEndVertex + 1]); // начальный элемент - конечная вершина
        int indexPrevVertex = 1; // индекс предыдущей вершины
        double weightEndVertex = dist[indexEndVertex]; // вес конечной вершины
        int weightPrimaryVertex = 0;


        // пока не дошли до начальной вершины
        while (indexEndVertex != weightPrimaryVertex) {
            // просматриваем все вершины
            for (int i = 0; i < SIZE; i++) {
                // если связь есть
                if (vertexGraph[i + 1][indexEndVertex + 1].matches("\\d+(\\.\\d+)?")) {
                    // определяем вес пути из предыдущей вершины
                    //double tmp = Double.parseDouble(vertexGraph[i + 1][indexEndVertex + 1]);
                    double roundWeightEndVertex = Math.round(weightEndVertex * 100.0) / 100.0;
                    double roundDistValue = Math.round(Double.parseDouble(vertexGraph[i + 1][indexEndVertex + 1]) * 100.0) / 100.0;
                    double roundValue = Math.round((roundWeightEndVertex - roundDistValue) * 100.0) / 100.0;

                    double roundDist = Math.round(dist[i] * 100.0) / 100.0;
                    // если вес совпал с рассчитанным, значит из этой вершины и был переход
                    if (roundValue == roundDist) {
                        weightEndVertex = roundValue; // сохраняем новый вес
                        indexEndVertex = i;     // сохраняем предыдущую вершину
                        minPathVertices.add(indexPrevVertex, vertexGraph[0][i + 1]); // и записываем ее в массив
                        indexPrevVertex++;
                    }
                }
            }
        }

        return minPathVertices;
    }

    public class Dijkstra {
        public ArrayList<String> findTheShortestPath() {
            double[] minDistToEachVertex = fillingDist();
            boolean[] checkedVertices = fillingCheckedVertices();
            calcTheMinDistToEachVertex(minDistToEachVertex, checkedVertices);
            return pathRestoration(minDistToEachVertex);
        }

        private double[] fillingDist() {
            double[] dist = new double[SIZE];
            for (int i = 1; i < SIZE; i++) {
                dist[i] = Double.MAX_VALUE;
            }
            dist[0] = 0;
            return dist;
        }

        private boolean[] fillingCheckedVertices() {
            boolean[] checkedVertices = new boolean[SIZE];
            for (int i = 0; i < SIZE; i++)
                checkedVertices[i] = false;
            return checkedVertices;
        }

        private void calcTheMinDistToEachVertex(double[] dist, boolean[] checkedVertices) {
            for (int i = 0; i < SIZE - 1; i++) {
                int indexMinDist = indexMinDist(dist, checkedVertices);

                checkedVertices[indexMinDist] = true;

                for (int j = 0; j < vertexLessGraph.length; j++) {
                    if (!checkedVertices[j] && vertexLessGraph[indexMinDist][j] != 0 && dist[indexMinDist] != Double.MAX_VALUE && dist[indexMinDist] + vertexLessGraph[indexMinDist][j] < dist[j]) {
                        dist[j] = dist[indexMinDist] + vertexLessGraph[indexMinDist][j];
                    }
                }
            }
        }

        private int indexMinDist(double[] dist, boolean[] checkedVertices) {
            double min = Double.MAX_VALUE;
            int indexMinDist = -1;

            for (int i = 0; i < SIZE; i++) {
                if (!checkedVertices[i] && dist[i] <= min) {
                    min = dist[i];
                    indexMinDist = i;
                }
            }
            return indexMinDist;
        }
    }

    public class Floyd {
        public ArrayList<String> findTheShortestPath() {
            int maxSum = 0;
            double[] dist = new double[SIZE];
            for (int i = 0 ; i < SIZE ; i++ ) {
                for (int j = 0; j < SIZE; j++) {
                    if (vertexLessGraph[i][j] != 0) maxSum += vertexLessGraph[i][j];
                }
            }

            for (int i = 0 ; i < SIZE ; i++ ) {
                for (int j = 0; j < SIZE; j++) {
                    if (vertexLessGraph[i][j] == 0 && i != j) vertexLessGraph[i][j] = maxSum;
                }
            }

            for (int k = 0 ; k < SIZE; k++ ) {
                for (int i = 0; i < SIZE; i++) {
                    for (int j = 0; j < SIZE; j++) {
                        if ((vertexLessGraph[i][k] + vertexLessGraph[k][j]) < vertexLessGraph[i][j]) vertexLessGraph[i][j] = vertexLessGraph[i][k] + vertexLessGraph[k][j];
                    }
                }
            }

            System.arraycopy(vertexLessGraph[0], 0, dist, 0, SIZE);

            return pathRestoration(dist);
        }
    }

    public class BellmanFord {
        public ArrayList<String> findTheShortestPath() {
            double[] distances = new double[SIZE + 1];
            for (int node = 0; node < SIZE; node++) {
                distances[node] = Integer.MAX_VALUE;
            }

            distances[0] = 0;
            for (int node = 0; node < SIZE - 1; node++) {
                for (int sourcenode = 0; sourcenode < SIZE; sourcenode++) {
                    for (int destinationnode = 0; destinationnode < SIZE; destinationnode++) {
                        if (vertexLessGraph[sourcenode][destinationnode] != 0) {
                            if (distances[destinationnode] > distances[sourcenode]
                                    + vertexLessGraph[sourcenode][destinationnode])
                                distances[destinationnode] = distances[sourcenode]
                                        + vertexLessGraph[sourcenode][destinationnode];
                        }
                    }
                }
            }
            return pathRestoration(distances);
        }
    }
}

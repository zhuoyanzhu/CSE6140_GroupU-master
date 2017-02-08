import java.util.*;


public class GraphAlgorithms {

    public static <T> Set<Edge<T>> kruskals(Graph<T> graph) {

        if (graph == null) {
            throw new IllegalArgumentException("Enter non null graph");
        }

        Map<Vertex<T>, DisjointSet> djsMap = new HashMap<>();
        Set<Vertex<T>> keySet = graph.getAdjacencyList().keySet();
        for (Vertex<T> v : keySet){
            djsMap.put(v, new DisjointSet());

        }

        Queue<Edge<T>> pq = new PriorityQueue<>(graph.getEdgeList());
        Set<Edge<T>> retSet = new HashSet<>();

        while (retSet.size() < keySet.size() - 1) {


            Edge<T> minEdge = pq.poll();

            if (minEdge != null) {
                DisjointSet udjs = djsMap.get(minEdge.getU());
                DisjointSet vdjs = djsMap.get(minEdge.getV());
                if (!udjs.find().equals(vdjs.find())) {
                    retSet.add(minEdge);
                    udjs.union(vdjs);
                    djsMap.put(minEdge.getU(), udjs);
                    djsMap.put(minEdge.getV(), udjs);

                }
            } else {
                return null;
            }


        }
        return retSet;









    }
}
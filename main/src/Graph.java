import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;


public class Graph<T> {

    private Set<Edge<T>> edges;
    private Map<Vertex<T>, List<VertexDistancePair<T>>> adjacencyList;
    private boolean directed;
    private ArrayList<Integer> preOrderList = new ArrayList<>();
    private Set<Edge> preOrderEdgeSet = new HashSet<>();

    /**
     * Constructor to build a Graph from an edge list.
     *
     * @param edges the edge list to build the graph from.
     */
    public Graph(Set<Edge<T>> edges) {
        this.adjacencyList = new HashMap<>();
        this.edges = edges;
        for (Edge<T> e : edges) {
            adjacencyList.putIfAbsent(e.getU(),
                    new ArrayList<VertexDistancePair<T>>());
            adjacencyList.putIfAbsent(e.getV(),
                    new ArrayList<VertexDistancePair<T>>());
            adjacencyList.get(e.getU()).add(
                    new VertexDistancePair<T>(e.getV(), e.getWeight()));
            if (!e.isDirected()) {
                adjacencyList.get(e.getV()).add(
                        new VertexDistancePair<T>(e.getU(), e.getWeight()));
            } else {
                this.directed = true;
            }
        }
    }

    /**
     * Gets the edge list of this graph.
     *
     * @return the edge list of this graph
     */
    public Set<Edge<T>> getEdgeList() {
        return edges;
    }
    
    public Set<Edge> getPreOrderEdgeSet() {
      return preOrderEdgeSet;
    }

    /**
     * Gets the adjacency list of this graph.
     *
     * @return the adjacency list of this graph
     */
    public Map<Vertex<T>, List<VertexDistancePair<T>>> getAdjacencyList() {
        return adjacencyList;
    }

    /**
     * Gets whether or not the edges of this graph are directed.
     *
     * @return true if this graph is directed, false otherwise
     */
    public boolean isDirected() {
        return directed;
    }
    
    public void preOrder() {
      preOrder(1);

    }
    
    private void preOrder(int root) {
    
        Set<Edge> preOrderEdgeSetTemp = new HashSet<>();
      
      
        for (Edge e : edges) {
           
           if ((int)e.getU().getData() == root || (int)e.getV().getData() == root) {
               if (!e.checkMark()) {
                   preOrderEdgeSet.add(e);
                   preOrderEdgeSetTemp.add(e);
                   e.markEdge();
                   //preOrderTourLength += e.weight();
               }
           }
           
        }
        preOrderList.add(root);
        for (Edge e : preOrderEdgeSetTemp) {
            if ((int)e.getU().getData() == root) {
                preOrder((int)e.getV().getData());
            }
            if ((int)e.getV().getData() == root) {
                preOrder((int)e.getU().getData());
            }
        }
      
    }
    public void printPreOrder() {
      for (Integer i : preOrderList) {
         System.out.println(i);
      }
      
    }
    
    public ArrayList<Integer> getPreOrderList() {
      return preOrderList;
    }
    
    public static Graph parseEdges2(int[][] matrix) {
      
      
   
       
       Set<Edge<Integer>> edges = new HashSet<Edge<Integer>>();
       for (int i = 0; i < matrix.length; i++) {
         for (int j = 0; j < matrix[0].length; j++) {
            Vertex u = new Vertex(i);
            Vertex v = new Vertex(j);
            Edge e = new Edge(u, v, matrix[i][j], false);
            edges.add(e);
         }
       }
       Graph g = new Graph<>(edges);
       return g;
       
   
   
   }
}
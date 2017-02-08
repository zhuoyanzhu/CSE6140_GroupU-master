/**
 *  The {@code EdgeWeightedGraph} class represents an edge-weighted
 *  graph of vertices named 0 through <em>V</em> - 1, where each
 *  undirected edge is of type {@link Edge} and has a real-valued weight.
 *  It supports the following two primary operations: add an edge to the graph,
 *  iterate over all of the edges incident to a vertex. It also provides
 *  methods for returning the number of vertices <em>V</em> and the number
 *  of edges <em>E</em>. Parallel edges and self-loops are permitted.
 *  <p>
 *  This implementation uses an adjacency-lists representation, which 
 *  is a vertex-indexed array of @link{Bag} objects.
 *  All operations take constant time (in the worst case) except
 *  iterating over the edges incident to a given vertex, which takes
 *  time proportional to the number of such edges.
 *  <p>
 *  For additional documentation,
 *  see <a href="http://algs4.cs.princeton.edu/43mst">Section 4.3</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
 import java.util.*;
public class EdgeWeightedGraph {
    private static final String NEWLINE = System.getProperty("line.separator");

    private int V;
    private int E;
    private ArrayList<Edge> edgeList;
    public ArrayList<Integer> preOrderList = new ArrayList<>();
    public Set<Edge> edgeSet;
    


    /**  
     * Initializes an edge-weighted graph from an input stream.
     * The format is the number of vertices <em>V</em>,
     * followed by the number of edges <em>E</em>,
     * followed by <em>E</em> pairs of vertices and edge weights,
     * with each entry separated by whitespace.
     *
     * @param  in the input stream
     * @throws IndexOutOfBoundsException if the endpoints of any edge are not in prescribed range
     * @throws IllegalArgumentException if the number of vertices or edges is negative
     */
    public EdgeWeightedGraph(In in) {

        edgeList = new ArrayList<>();
        V = in.readInt(); 
        E = in.readInt();
        
      if (E < 0) throw new IllegalArgumentException("Number of edges must be nonnegative");
        in.readLine();
        for (int i = 0; i < E; i++) {
            int v = in.readInt();
            int w = in.readInt();
  
            double weight = in.readDouble();
            Edge e = new Edge(v, w, weight);
            
            edgeList.add(e);

            in.readLine();
        }
        
    }
    
    public EdgeWeightedGraph() {
    
      edgeList = new ArrayList<>();
      edgeSet = new HashSet<>();
      V = 0;
      E = 0;
    }


    /**
     * Returns the number of vertices in this edge-weighted graph.
     *
     * @return the number of vertices in this edge-weighted graph
     */
    public int V() {
        return V;
    }
    
    public void setV(int newV) {
      V = newV;
    }

    /**
     * Returns the number of edges in this edge-weighted graph.
     *
     * @return the number of edges in this edge-weighted graph
     */
    public int E() {
        return E;
    }


    /**
     * Returns all edges in this edge-weighted graph.
     * To iterate over the edges in this edge-weighted graph, use foreach notation:
     * {@code for (Edge e : G.edges())}.
     *
     * @return all edges in this edge-weighted graph, as an iterable
     */
    public ArrayList<Edge> edges() {

        Collections.sort(edgeList);
        return edgeList;
    }
    
    public void setNewEdges(Edge[] mstEdges) {
        ArrayList<Edge> newEdgeList = new ArrayList<>();
        Set<Edge> newEdgeSet = new HashSet<>();
        for (int i = 0; i < mstEdges.length; i++) {
            newEdgeList.add(mstEdges[i]);
            newEdgeSet.add(mstEdges[i]);
            
        }
        newEdgeList.remove(newEdgeList.size() - 1);
        edgeList = newEdgeList;
        
        edgeSet = newEdgeSet;
    }
    
    public void addEdge(Edge e) {
        edgeList.add(e);
        edgeSet.add(e);
        E++;
    
    }
    
    
    public void preOrder() {
      preOrder(1);
      //preOrderList.add(1);
    }
    
    private void preOrder(int root) {
    
        Set<Edge> preOrderEdgeSet = new HashSet<>();
      
      
        for (Edge e : edgeSet) {
           
           if (e.other(e.either()) == root || e.either() == root) {
               if (!e.checkMark()) {
                   preOrderEdgeSet.add(e);
                   e.markEdge();
               }
           }
           
        }
        preOrderList.add(root);
        for (Edge e : preOrderEdgeSet) {
            if (e.w == root) {
                preOrder(e.v);
            }
            if (e.v == root) {
                preOrder(e.w);
            }
        }
      
    }
    public void printPreOrder() {
      for (Integer i : preOrderList) {
         System.out.println(i);
      }
      
    }
    
    public void clearMarks() {
      for (Edge e : edgeSet) {
         
      }
    }

    /**
     * Returns a string representation of the edge-weighted graph.
     * This method takes time proportional to <em>E</em> + <em>V</em>.
     *
     * @return the number of vertices <em>V</em>, followed by the number of edges <em>E</em>,
     *         followed by the <em>V</em> adjacency lists of edges
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(V + " " + E + NEWLINE);
        edgeSet.remove(null);
       //// for (int v = 0; v < V; v++) {
            //s.append(v + ": ");
            for (Edge e : edgeSet) {
                s.append(e + "  ");
            }
            s.append(NEWLINE);
       // }
        return s.toString();
    }

    /**
     * Unit tests the {@code EdgeWeightedGraph} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        
    }

}



import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.TreeSet;
import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;
import java.util.*;


public class MSTApprox {

   private int quality;
   private static double time;

   
// 	public static void main(String[] args) throws FileNotFoundException {
// 
// 
// 		String graph_file = "C:\\Users\\dshekhar\\Desktop\\CSE6140_GroupU\\data/rmat0608.gr";
// 		String change_file = "C:\\Users\\dshekhar\\Desktop\\CSE6140_GroupU\\data/rmat0608.extra";
// 		String output_file = "C:\\Users\\dshekhar\\Desktop\\CX4140\\MST\\results/myrmat0608.out";
// 
// 		PrintWriter output = null;
//       
//       try {
// 		output = new PrintWriter(output_file, "UTF-8");
// 
// 		      } catch (IOException ex) {
//       System.out.println("File not found");
//       }
//       EdgeWeightedGraph g = parseEdges(graph_file);
// 
// 
// 		long startMST = System.nanoTime();
// 		int MSTweight = computeMST(g);
// 		long finishMST = System.nanoTime();
// 
// 		//Subtract the start time from the finish time to get the actual algorithm running time
// 		double MSTtotal = (finishMST - startMST)/1000000;
// 
// 		//Write to output file the initial MST weight and time
// 		output.println(Integer.toString(MSTweight) + " " + Double.toString(MSTtotal));
// 
// 		//Iterate through changes file
//       BufferedReader br = null;;
//       try {
// 		     br = new BufferedReader(new FileReader(change_file));
// 		     String line = br.readLine();
// 		     String[] split = line.split(" ");
// 	        int num_changes = Integer.parseInt(split[0]);
//            System.out.println(num_changes);
// 		     int u, v, weight;
//       
//       
//       
// 		    while ((line = br.readLine()) != null) {
// 			     split = line.split(" ");
// 			     u = Integer.parseInt(split[0]);
// 			     v = Integer.parseInt(split[1]);
// 		        weight = Integer.parseInt(split[2]);
// 
// 			     //Run your recomputeMST function to recalculate the new weight of the MST given the addition of this new edge
// 			     //Note: you are responsible for maintaining the MST in order to update the cost without recalculating the entire MST
// 			     long start_newMST = System.nanoTime();
// 			     int newMST_weight = recomputeMST(u,v,weight,g);
// 			     long finish_newMST = System.nanoTime();
// 
// 			     double newMST_total = (finish_newMST - start_newMST)/1000000;
// 
// 			     //Write new MST weight and time to output file
// 			     output.println(Integer.toString(newMST_weight) + " " + Double.toString(newMST_total));
// 
//               
// 		     }
//       } catch (IOException ex) {
//       System.out.println("File not found");
//       }
//      // br.close();
// 		output.close();
// 		
// 
// 
// 
// 
// 	}
   
   
   public static double getTime() {
      return time;
   }
   
   public static EdgeWeightedGraph parseEdges(String fileName) {
      
      In file = new In(fileName);
   
       EdgeWeightedGraph g = new EdgeWeightedGraph(file);
       return g;
       
   
   
   }
   
   public static EdgeWeightedGraph parseEdges(int[][] matrix) {
      
      
   
       EdgeWeightedGraph g = new EdgeWeightedGraph();
       for (int i = 0; i < matrix.length; i++) {
         for (int j = 0; j < matrix[0].length; j++) {
            Edge e = new Edge(i, j, matrix[i][j]);
            g.addEdge(e);
         }
       }
       g.setV(matrix.length);
       return g;
       
   
   
   }
   
   static class subtree {
       int parent;
       int rank;
   };
   
   //getSet utilizes path compression
   static int getSet(subtree[] trees, int i) {
       int parent = trees[i].parent;
       if (parent == i) {
           return i;
       }
       trees[i].parent = getSet(trees, trees[i].parent);
       return trees[i].parent;    
       
   }
   
   static void CombineTrees(subtree[] trees, int i, int j) {
       
       int iRoot = getSet(trees, i);
       int jRoot = getSet(trees, j);
       
       if (trees[iRoot].rank < trees[jRoot].rank) {
           trees[iRoot].parent = jRoot;
       } else if (trees[iRoot].rank > trees[jRoot].rank) {
           trees[jRoot].parent = iRoot;
       } else {
           trees[jRoot].parent = iRoot;
           trees[iRoot].rank++;
       }
   }
   public static int computeMST(EdgeWeightedGraph g){
   
   long start = System.nanoTime();
   long end;
   Edge[] mstEdges = new Edge[g.V()];
   int mstIndex = 0;
   //Iterable<Edge> edgeIter = g.edges();
   ArrayList<Edge> sortedEdges = g.edges();
   Edge[] sortedEdgeArray = sortedEdges.toArray(new Edge[0]);
   subtree[] trees = new subtree[g.V()];
   for (int i = 0; i < g.V(); i++) {
       trees[i] = new subtree();
   }
   for (int i = 0; i < g.V(); ++i) {
       trees[i].parent = i;
       trees[i].rank = 0;
   }
   int indexOfEdges = 0;
   while (mstIndex < g.V() - 1) {
   
       Edge next = sortedEdgeArray[indexOfEdges++];
       
       int i = getSet(trees, next.either());
       int j = getSet(trees, next.other(next.either()));
       
       if (i != j) {
           mstEdges[mstIndex++] = next;
           CombineTrees(trees, i, j);
       }
   }
   
   int totalWeight = 0;
   for (int i = 0; i < mstEdges.length - 1; i++) {
       totalWeight += mstEdges[i].weight();
   }

      g.setNewEdges(mstEdges);

   ArrayList<Edge> checkMST = g.edges();
   end = System.nanoTime();
   time = end - start;
   return totalWeight;
   
   }
   public static int recomputeMST(int x, int y, int z, EdgeWeightedGraph g){
   
       Edge e = new Edge(x,y,z);
       g.addEdge(e);
       return computeMST(g);
   }
   
}
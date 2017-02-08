

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class BranchAndBound {
	int[][] cost;
	private double[][] intermediteCost;
	Solution bestSolution = new Solution();
	private int n;
	private int quality;
	private ArrayList<int[]> qualityList = new ArrayList<int[]>();
	private ArrayList<double[]> timeList = new ArrayList<double[]>();
	private int cutoff;
	private long start;
	private long interval;
	public BranchAndBound(int[][] matrix, int cutoff) {
		this.cost = matrix;
		n = matrix.length;
		this.cutoff = cutoff;
	}
	public int getQuality() {
		return quality;
	}
	public ArrayList<int[]> getQualityList() {
		return qualityList;
	}
	public ArrayList<double[]> getTimeList() {
		return timeList;
	}
	public void solve() {
		start = System.nanoTime();
		long end;
		interval = cutoff * 1000000000L;
		bestSolution.lowerBound = Integer.MAX_VALUE;
		Solution currentSolution = new Solution();
		currentSolution.exclusion = new boolean[n][n];
		intermediteCost = new double[n][n];
		HeldKarp(currentSolution);
		PriorityQueue<Solution> qu = new PriorityQueue<Solution>(n, new SolutionComparator());
		do {
			do {
				int i = -1;
		        for (int j = 0; j < n; j++) {
		        //find if each point in current Solution meet the requirement of TSP
		          if (currentSolution.degree[j] > 2 && (i < 0 || currentSolution.degree[j] < currentSolution.degree[i])) i = j;
		        }
		        if (i < 0) {
		        //update the bestSolution if it is a tsp 
		        	if (currentSolution.lowerBound < bestSolution.lowerBound) {
		        		bestSolution = currentSolution;
		        		quality = (int) bestSolution.lowerBound;
		        		end = System.nanoTime();
		        		timeList.add(new double[] {(end - start) / 1000000000.0, quality});
		        	}
		        	break;
		        }
		        //choosing whether to choose one edge to create multi branches
		        PriorityQueue<Solution> children = new PriorityQueue<Solution>(n, new SolutionComparator());
		        children.add(exclude(currentSolution, i, currentSolution.beforePoint[i]));
		        for (int j = 0; j < n; j++) {
		        	if (currentSolution.beforePoint[j] == i) children.add(exclude(currentSolution, i, j));
		        }
		        //get the Solution with lowest bound
		        currentSolution = children.poll();
		        qu.addAll(children);
			    long ends = System.nanoTime();
			    //if time pass jump out of the loop
			    if(ends - start > interval) break;
			} while (currentSolution.lowerBound < bestSolution.lowerBound);
		    currentSolution = qu.poll();
		    long ends = System.nanoTime();
		    //if time pass jump out of the loop
		    if(ends - start > interval) break;
		} while (currentSolution != null && currentSolution.lowerBound < bestSolution.lowerBound);
		int j = 0;
		if(bestSolution.beforePoint == null) return;
		do {
		     int i = bestSolution.beforePoint[j];
		     qualityList.add(new int[] {j, i , cost[i][j]});
		     j = i;
		} while (j != 0);
	}

	private Solution exclude(Solution Solution, int i, int j) {
		Solution child = new Solution();
		//exclud edge i and j and then compute TSP using left edges
		child.exclusion = Solution.exclusion.clone();
		child.exclusion[i] = Solution.exclusion[i].clone();
		child.exclusion[j] = Solution.exclusion[j].clone();
		child.exclusion[i][j] = true;
		child.exclusion[j][i] = true;
		HeldKarp(child);
		return child;
	}

	private void HeldKarp(Solution Solution) {
		Solution.pi = new double[n];
		Solution.lowerBound = Integer.MIN_VALUE;
		Solution.degree = new int[n];
		Solution.beforePoint = new int[n];
		double threshold = 0.1;
		while (threshold > 1e-06) {
			double previousLowerBound = Solution.lowerBound;
		    computeOneTree(Solution);
		    if (!(Solution.lowerBound < bestSolution.lowerBound)) return;
		    if (!(Solution.lowerBound < previousLowerBound)) threshold *= 0.9;
		    int abundantDegree = 0;
		    for (int i = 1; i < n; i++) {
		    	int a = Solution.degree[i] - 2;
		    	abundantDegree += a * a;
		    }
		    //if it is a tsp, return
		    if (abundantDegree == 0) return;
		    double t = threshold * Solution.lowerBound / abundantDegree;
		    // update each pi
		    for (int i = 1; i < n; i++) Solution.pi[i] += t * (Solution.degree[i] - 2);
		}
	}

	private void computeOneTree(Solution Solution) {
		// compute adjusted costs
		Solution.lowerBound = 0;
		Arrays.fill(Solution.degree, 0);
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) 
				intermediteCost[i][j] = Solution.exclusion[i][j] ? Double.MAX_VALUE : cost[i][j] + Solution.pi[i] + Solution.pi[j];
		}
		int firstNear;
		int secondNear;
		// find the two nearnest edges from 0
		if (intermediteCost[0][2] < intermediteCost[0][1]) {
		    firstNear = 2;
		    secondNear = 1;
		} else {
			firstNear = 1;
		    secondNear = 2;
		}
		    for (int j = 3; j < n; j++) {
		    	if (intermediteCost[0][j] < intermediteCost[0][secondNear]) {
		    		if (intermediteCost[0][j] < intermediteCost[0][firstNear]) {
		    			secondNear = firstNear;
		    			firstNear = j;
		    		} else {
		    			secondNear = j;
		    		}
		    	}
		    }
		    addEdge(Solution, 0, firstNear);
		    Arrays.fill(Solution.beforePoint, firstNear);
		    Solution.beforePoint[firstNear] = 0;
		    // keep computing the spanning tree
		    double[] minCost = intermediteCost[firstNear].clone();
		    for (int k = 2; k < n; k++) {
		    	int i;
		    //find disconnected Solutions
		    	for (i = 1; i < n; i++) {
		    		if (Solution.degree[i] == 0) break;
		    	}
		    //find disconeected Solutions with lowest cost
		    	for (int j = i + 1; j < n; j++) {
		    		if (Solution.degree[j] == 0 && minCost[j] < minCost[i]) i = j;
		    	}
		    //add this edge and update min cost in edges
		    	addEdge(Solution, Solution.beforePoint[i], i);
		    	for (int j = 1; j < n; j++) {
		    		if (Solution.degree[j] == 0 && intermediteCost[i][j] < minCost[j]) {
		    			minCost[j] = intermediteCost[i][j];
		    			Solution.beforePoint[j] = i;
		    		}
		    	}
		    }
		    addEdge(Solution, 0, secondNear);
		    Solution.beforePoint[0] = secondNear;
		    //make lowerbound to be integer
		    Solution.lowerBound = (int) Math.rint(Solution.lowerBound);
	}
	private void addEdge(Solution Solution, int i, int j) {
		//add edge and update cost between i and j
		Solution.lowerBound += intermediteCost[i][j];
		Solution.degree[i]++;
		Solution.degree[j]++;
	}
}

class Solution {
	public boolean[][] exclusion;
	public double[] pi;
	public double lowerBound;
	public int[] degree;
	public int[] beforePoint;
}

class SolutionComparator implements Comparator<Solution> {
	public int compare(Solution a, Solution b) {
	/*use comparator to compare the lower bound of two Solutions*/
		return Double.compare(a.lowerBound, b.lowerBound);
	}
}

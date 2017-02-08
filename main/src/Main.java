

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Set;
import java.lang.Math;
public class Main
{

	private static int quality;
	private static String[] methods = {"BnB", "MSTApprox", "Heur", "LS1", "LS2"};
	private static String[] files = {"SanFrancisco", "NYC", "Roanoke", "Atlanta", "Champaign", "Cincinnati"
			,"Philadelphia", "UKansasState", "Toronto", "UMissouri", "Boston", "Denver"};
	private static String[] commands = {"-inst", "-time", "-alg", "-seed"};
	
	@SuppressWarnings("resource")
	private static boolean find(String[] array, String element) {
		for(String x : array)
			if(x.equals(element)) return true;
		return false;
	}
	public static void main(String args[]) throws IOException
	{
		int time = 600;
		String method = "BnB";
		long seed = System.currentTimeMillis();
		String file = "Atlanta";
		if(args.length % 2 == 1)
		{
			System.out.println("The useage must be exec -inst <filename> -alg BnB|MSTApprox|Heur|LS1|LS2 -time <cutoff_in_seconds> -seed <random_seed>");
			System.exit(-1);
		}
		else {
			for(int i = 0; i < args.length - 1; i += 2)
			{
				if(!find(commands, args[i])) {
					System.out.println("The useage must be exec -inst <filename> -alg BnB|MSTApprox|Heur|LS1|LS2 -time <cutoff_in_seconds> -seed <random_seed>");
					System.exit(-1);
				}
				else {
					if(args[i].equals("-alg")) {
						if(!find(methods, args[i + 1])) {
							System.out.println("The useage must be exec -inst <filename> -alg BnB|MSTApprox|Heur|LS1|LS2 -time <cutoff_in_seconds> -seed <random_seed>");
							System.exit(-1);
						}
						method = args[i + 1];
					}
					else if(args[i].equals("-inst")) {
						if(!find(files, args[i + 1])) {
							System.out.println("The useage must be exec -inst <filename> -alg BnB|MSTApprox|Heur|LS1|LS2 -time <cutoff_in_seconds> -seed <random_seed>");
							System.exit(-1);
						}
						file = args[i + 1];
					}
					else if(args[i].equals("-seed"))
					{
						seed = Long.parseLong(args[i + 1]);
					}
					else if(args[i].equals("-time"))
					{
						time = Integer.parseInt(args[i + 1]);
					}
				}
			}
		}
			System.out.println(file);
			String inputFile = "./DATA/DATA/" + file + ".tsp";
			String outputFile1 = file + "_" + method + "_" + time + "_" + seed + ".sol";
			String outputFile2 = file + "_" + method + "_" + time + "_" + seed + ".trace";
			PrintWriter pw1 = new PrintWriter(outputFile1, "UTF-8");
			PrintWriter pw2 = new PrintWriter(outputFile2, "UTF-8");
			BufferedReader br = new BufferedReader(new FileReader(inputFile));
			for(int i1 = 0; i1 < 2; ++i1)
				br.readLine();
			String line = br.readLine();
			String[] splits = line.split(":");
			String[] poses = new String[3];
			int num = Integer.parseInt(splits[1].substring(1));
			double [][] positions = new double[num][2];
			for(int i1 = 0; i1 < 2; ++i1)
				br.readLine();
			int iter = 0;
			while(iter++ < num)
			{
				line = br.readLine();
				poses = line.split(" ");
				positions[Integer.parseInt(poses[0]) - 1][0] = Double.parseDouble(poses[1]);
				positions[Integer.parseInt(poses[0]) - 1][1] = Double.parseDouble(poses[2]);
			}
			int[][] matrix = new int[num][num];
			for(int i1 = 1; i1 < num; ++i1)
				for(int j = 0; j < num; ++j)
				{
					double xd = positions[i1][0] - positions[j][0];
					double yd = positions[i1][1] - positions[j][1];
					double rij = Math.sqrt((xd * xd + yd * yd));
					int tij = (int) Math.round(rij);
					matrix[i1][j] = matrix[j][i1] = tij;
				}
			ArrayList<double[]> timeList = new ArrayList<double[]>();
			ArrayList<int[]> qualityList = new ArrayList<int[]>();
			if(!method.equals("MSTApprox")) {
				if(method.equals("BnB")) {
					BranchAndBound solver = new BranchAndBound(matrix, time);
					solver.solve();
					timeList = solver.getTimeList();
					qualityList = solver.getQualityList();
					quality = solver.getQuality();
				}
				else if(method.equals("Heur")) {
					NearNeigh solver = new NearNeigh(matrix, time, seed);
					solver.TSP_NN();
					timeList = solver.getTimeList();
					qualityList = solver.getQualityList();
					quality = solver.getQuality();
				}
				else if(method.equals("LS1")) {
					TwoOpt solver = new TwoOpt(matrix);
					solver.TwoOptTour(time, seed);
					timeList = solver.getTimeList();
					qualityList = solver.getQualityList();
					quality = solver.getQuality();
				}
				else if(method.equals("LS2")) {
					ThreeOptTabu solver = new ThreeOptTabu(matrix);
					solver.ThreeOptTabuTour(time, seed);
					timeList = solver.getTimeList();
					qualityList = solver.getQualityList();
					quality = solver.getQuality();
				}
				pw1.println(Integer.toString(quality));
				for(int i1 = 0; i1 < qualityList.size(); ++i1)
					pw1.println(Integer.toString(qualityList.get(i1)[0]) + "	" + Integer.toString(qualityList.get(i1)[1]) + "	" + Integer.toString(qualityList.get(i1)[2]));
				for(int i1 = 0; i1 < timeList.size(); ++i1)
					pw2.println(String.format("%.2f",timeList.get(i1)[0]) + "," + Double.toString(timeList.get(i1)[1]));
				pw1.flush();
				pw2.flush();
			}
			else {
				Graph g = Graph.parseEdges2(matrix);
	            
	            long start = System.nanoTime();
	            Set<Edge> mstEdges = GraphAlgorithms.kruskals(g);
	            int total = 0;
	            for (Edge e : mstEdges) {
	               total += e.getWeight();
	            }
	            total *= 2;
	            Graph tspGraph = new Graph(mstEdges);
	            tspGraph.preOrder();
	            
	            ArrayList<Integer> pOL = tspGraph.getPreOrderList();
	            Set<Edge<Integer>> allEdges = g.getEdgeList();
	            Set<Edge> preOrderEdgeSet = tspGraph.getPreOrderEdgeSet();
	            
	            for (Edge e : allEdges) {
	                if ((int)e.getV().getData() == 1 && (int)e.getU().getData() == pOL.get((pOL.size() - 1))
	                || (int)e.getU().getData() == 1 && (int)e.getV().getData() == pOL.get((pOL.size() - 1))) {
	                  total += e.getWeight();
	                  Edge finalEdge = e;
	                  preOrderEdgeSet.add(finalEdge);
	                  
	                }
	            }
	            long end = System.nanoTime();
	            pw1.println(total);
	            for (Edge e : preOrderEdgeSet) {
	               pw1.println(e);
	            }
	            pw2.println((end - start) / 1000000000.0 + "," + total);
	            pw1.flush();
	            pw2.flush();

			}
		}
	}



package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.lang.Math;
public class Main
{

	private static int quality;
	@SuppressWarnings("resource")
	public static void main(String args[]) throws IOException
	{
		DecimalFormat df = new DecimalFormat("0.00"); 
		String [] data =  {"Boston", "Roanoke"};
		for(int i = 0; i < data.length; ++i)
		{
			System.out.println(data[i]);
			String inputFile = "./DATA/DATA/" + data[i] + ".tsp";
			String outputFile1 = data[i] + ".sol";
			String outputFile2 = data[i] + ".trace";
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
			BranchAndBound solver = new BranchAndBound(matrix);
			solver.solve();
			ArrayList<double[]> timeList = solver.getTimeList();
			ArrayList<int[]> qualityList = solver.getQualityList();
			int quality = solver.getQuality();
			pw1.println(Integer.toString(quality));
			for(int i1 = 0; i1 < qualityList.size(); ++i1)
				pw1.println(Integer.toString(qualityList.get(i1)[0]) + "	" + Integer.toString(qualityList.get(i1)[1]) + "	" + Integer.toString(qualityList.get(i1)[2]));
			for(int i1 = 0; i1 < timeList.size(); ++i1)
				pw2.println(df.format(timeList.get(i1)[0]) + "," + Double.toString(timeList.get(i1)[1]));
			pw1.flush();
			pw2.flush();
		}
	}

}

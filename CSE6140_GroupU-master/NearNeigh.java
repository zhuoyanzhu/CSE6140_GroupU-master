import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.Comparator;
import java.util.Random;

public class NearNeigh {
	
	private int[][] mtx;
	private int quality;
	private int[][] qualityList;
	private ArrayList<double[]> timeList;
	
	NearNeigh(int[][] mtx){
		this.mtx = Arrays.copyOf(mtx, mtx.length);
		quality = Integer.MAX_VALUE;
		qualityList = new int[mtx.length][3];
		timeList = new ArrayList<double[]>();
	}
	
	public int FindNearestNeighbor(int[] row, boolean[] visited, int ori){
		int[][] cityinfo = new int[row.length][2];	//city information, [0]: dist [1]: target city
		for (int i = 0; i < row.length; i++){
			cityinfo[i][0] = row[i];
			cityinfo[i][1] = i;
		}

		Arrays.sort(cityinfo, new Comparator<int[]>(){
			public int compare(int[] a, int[] b){
				return Integer.compare(a[0], b[0]);
			}
		});

		for (int i = 1; i < row.length; i++){
			if (!visited[cityinfo[i][1]]){
				return cityinfo[i][1];
			}
		}
		return ori;
	}
	
	public void TSP_NN(double cutoff){
		Date date = new Date();
		double stime = (double)date.getTime() /1000;
		double ctime = stime;
		//boolean[] tried = new boolean[mtx.length];

		int ori = 0;
		while (ctime - stime < cutoff){
			boolean[] visited = new boolean[mtx.length];
			Random generator = new Random(System.currentTimeMillis());	//seed RNG
			
			//find a solution
			int tempq = 0;												//temp quality
			int[][] tempqList = new int[mtx.length][3];					//temp quality list
			//int ori = generator.nextInt(mtx.length-1);				//randomly pick original city
			//tried[ori] = true;
			visited[ori] = true;
			int next = ori;												//initialize next city
			int num = 0;
			
			//find a solution given original city
			while (true){												//loop until find a solution
				int current = next;
				int[] row = Arrays.copyOf(mtx[current], mtx[current].length);
				next = FindNearestNeighbor(row, visited, ori);
				visited[next] = true;
				tempqList[num][0] = current;
				tempqList[num][1] = next;
				tempqList[num][2] = mtx[current][next];
				tempq += mtx[current][next];
				num++;
				
				if (next == ori)
					break;
			}
			if (tempq < quality){
				quality = tempq;
				qualityList = Arrays.copyOf(tempqList, tempqList.length);
				double[] timeElement = {ctime-stime, (double)quality};
				timeList.add(timeElement);
			}
			ori++;
			if (ori >= mtx.length)
				break;
			ctime = (double)date.getTime() /1000;					//update time
		}
	}
	
	public ArrayList<double[]> getTimeList(){
		return timeList;
	}
	
	public int[][] getQualityList(){
		return qualityList;
	}
	
	public int getQuality(){
		return quality;
	}
}

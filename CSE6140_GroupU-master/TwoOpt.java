import java.util.Collections;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class TwoOpt {
    
    private int[][] dist;
    private int quality;
    private int[][] qualityList;
    private ArrayList<double[]> timeList;

    TwoOpt(int[][] dist){
        this.dist = Arrays.copyOf(dist, dist.length);
        quality = Integer.MAX_VALUE;
        qualityList = new int[dist.length][3];
        timeList = new ArrayList<double[]>();
    }

    public void TwoOptTour(double cutoff) {
        int[] tour = GenerateRandomTour(dist.length, 600);
        int bestQuality = Integer.MAX_VALUE;
        Date date = new Date();
        double stime = (double)date.getTime() /1000;
        double ctime = stime;
        for (int i = 0; i < dist.length; i++) {
            for (int j = i + 1; j < dist.length; j++) {
                int[] newTour = Swap(i, j, tour);
                if (quality < bestQuality) {
                    tour = newTour;
                    bestQuality = quality;
                    double[] timeElement = {ctime-stime, (double)quality};
                    timeList.add(timeElement);
                }
                ctime = (double)date.getTime() /1000;
            }
        }
        quality = bestQuality;
        for (int i = 0; i < tour.length - 1; i++) {
            int[] edge = {tour[i], tour[i+1], dist[i][i+1]};
            qualityList[i] = edge;
        }
        int[] lastEdge = {tour[tour.length - 1], tour[0], dist[tour.length - 1][0]};
        qualityList[tour.length - 1] = lastEdge;
    }

    private int[] GenerateRandomTour(int size, int seed) {
        ArrayList<Integer> tour = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            tour.add(i);
        }
        Random rand = new Random(seed);
        Collections.shuffle(tour, rand);
        Integer[] Itour = new Integer[tour.size()];
        Itour = tour.toArray(Itour);
        int[] itour = Arrays.stream(Itour).mapToInt(Integer::intValue).toArray();
        quality = 0;
        for (int i = 0; i < size - 1; i++) {
            quality += dist[itour[i]][itour[i+1]];
        }
        return itour;
    }

    private int[] Swap(int a, int b, int[] tour) {
        int[] newTour = new int[tour.length];
        for (int i = 0; i <= a - 1; i++) {
            newTour[i] = tour[i];
        }
        for (int i = a; i <= b; i++) {
            newTour[i] = tour[b - (i - a)];
        }
        for (int i = b + 1; i < tour.length; i++) {
            newTour[i] = tour[i];
        }
        quality = 0;
        for (int i = 0; i < tour.length - 1; i++) {
            quality += dist[newTour[i]][newTour[i+1]];
        }
        return newTour;
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
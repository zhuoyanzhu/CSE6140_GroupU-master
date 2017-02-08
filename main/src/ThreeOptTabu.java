import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.util.*;

/**
 * Performs tabu search using the 3-opt neighborhood for potenial moves.
 * Iterates a set number of times before terminating.
 */

public class ThreeOptTabu {

    private int[][] dist;
    private int quality;
    private ArrayList<int[]> qualityList = new ArrayList<>();
    private ArrayList<double[]> timeList;
    private HashMap<ArrayList<Integer>, Integer> tabuList = new HashMap<>();
    private int tabuTime;
    private int iters;
    private Random rand;

    ThreeOptTabu(int[][] dist){
        this.dist = Arrays.copyOf(dist, dist.length);
        timeList = new ArrayList<double[]>();
        tabuTime = 100;
        iters = 1000;

    }

    /**
     * Runs the 3-opt tabu local search algorithm on the given distance matrix
     * for a set number of iterations. Continually looks for the best solution
     * in the 3-opt neighborhood that is not tabu.
     * @param cutoff the allowed running time in seconds
     * @param seed the RNG seed for creating the random starting tour.
     */

    public void ThreeOptTabuTour(double cutoff, long seed) {
        rand = new Random(seed);
        double stime = System.nanoTime() / (double) 1000000;
        ArrayList<Integer> tour = GenerateRandomTour(dist.length, rand);
        int iter = 0;
        ArrayList<Integer> bestTour = new ArrayList<>();
        int bestQuality = Integer.MAX_VALUE;
        double ctime = System.nanoTime() / (double) 1000000;
        double[] firstElement = {(ctime-stime) / 1000.0, (double)quality};
        timeList.add(firstElement);
        while (iter < iters && ctime - stime < cutoff * 1000) {
            tour = bestAcceptableNeighbor(tour);
            quality = getTourQuality(tour);
            if (quality < bestQuality) {
                bestTour = tour;
                bestQuality = quality;
                double[] timeElement = {(ctime-stime) / 1000.0, (double)quality};
                timeList.add(timeElement);
            }
            iter++;
            ctime = System.nanoTime() / (double) 1000000;
            updateTabuList();
        }
        quality = bestQuality;
        for (int i = 0; i < bestTour.size() - 1; i++) {
            int[] edge = {bestTour.get(i), bestTour.get(i+1), dist[i][i+1]};
            qualityList.add(edge);
        }
        int[] lastEdge = {bestTour.get(bestTour.size() - 1), bestTour.get(0),
                dist[bestTour
                .size() -
                1][0]};
        qualityList.add(lastEdge);
    }

    /**
     * Returns the 3-opt neighbor with the best solution quality that is
     * not tabu, then adds it to the tabu list.
     * @param tour the starting tour
     * @return the best tour in the 3-opt neighborhood that isn't tabu.
     */

    private ArrayList<Integer> bestAcceptableNeighbor(ArrayList<Integer> tour) {
        ArrayList<Integer> bestTour = tour;
        int bestQuality = Integer.MAX_VALUE;
        for (int i = 0; i < tour.size() - 2; i++) {
            for (int j = i + 1; j < tour.size() - 1; j++) {
                for (int k = j + 1; k < tour.size(); k++) {
                    ArrayList<Integer> newTour = Swap(i, j, k, tour);
                    int newQuality = getTourQuality(newTour);
                    if (newQuality < bestQuality && tabuList.get(newTour) ==
                            null) {
                        bestQuality = newQuality;
                        bestTour = newTour;
                    }
                }
            }
        }
        tabuList.put(bestTour, 0);
        return bestTour;
    }

    /**
     * Updates the tabu list, removing the entry that exceeds
     * the tabu time on this iteration.
     */
    private void updateTabuList() {
        int t = 0;
        tabuList.replaceAll((k, v) -> v + 1);
        ArrayList<Integer> expired = null;
        Iterator iter = tabuList.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry)iter.next();
            if ((int)entry.getValue() >= tabuTime) {
                expired = (ArrayList<Integer>) entry.getKey();
            }
        }
        tabuList.remove(expired);
    }

    /**
     * Generates a random starting tour.
     * @param size the length of the tour
     * @param rand the RNG seed for picking cities
     * @return a starting tour
     */

    private ArrayList<Integer> GenerateRandomTour(int size, Random rand) {
        ArrayList<Integer> tour = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            tour.add(i);
        }
        Collections.shuffle(tour, rand);
        quality = dist[tour.get(tour.size() - 1)][tour.get(0)];
        for (int i = 0; i < size - 1; i++) {
            quality += dist[tour.get(i)][tour.get(i+1)];
        }
        return tour;
    }

    /**
     * Swaps edges to generate a new tour
     * @param a The first endpoint of the first edge
     * @param b The first endpoint of the second edge
     * @param c The first endpoint of the third edge
     * @param tour The tour on which to perform the swap
     * @return The tour with the given edges swapped
     */

    private ArrayList<Integer> Swap(int a, int b, int c, ArrayList<Integer> tour) {
        ArrayList<Integer> newTour = new ArrayList<>(tour.size());
        for (int i = 0; i <= a; i++) {
            newTour.add(tour.get(i));
        }
        for (int i = b+1; i <= c; i++) {
            newTour.add(tour.get(i));
        }
        for (int i = b; i >= a+1; i--) {
            newTour.add(tour.get(i));
        }
        for (int i = c + 1; i < tour.size(); i++) {
            newTour.add(tour.get(i));
        }
        return newTour;
    }

    /**
     * Returns the quality of the tour.
     * @param tour some TSP tour
     * @return the quality of that tour
     */

    private int getTourQuality(ArrayList<Integer> tour) {
        int tourQuality = 0;
        for (int i = 0; i < tour.size() - 1; i++) {
            tourQuality += dist[tour.get(i)][tour.get(i+1)];
        }
        tourQuality += dist[tour.get(tour.size() - 1)][tour.get(0)];
        return tourQuality;
    }

    /**
     * Returns the list of solutions and their timestamps.
     * @return data for the .trace file
     */

    public ArrayList<double[]> getTimeList(){
        return timeList;
    }

    /**
     * Returns the list of edges and their weights.
     * @return data for the .sol file
     */

    public ArrayList<int[]> getQualityList(){
        return qualityList;
    }

    /**
     * Returns the quality of the final solution.
     * @return the final solution quality.
     */

    public int getQuality(){
        return quality;
    }
}
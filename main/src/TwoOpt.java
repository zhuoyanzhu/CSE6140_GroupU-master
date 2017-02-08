import java.util.Collections;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * An implementation of the hill climbing TSP approximation algorithm
 * using the 2-opt neighborhood for exchanges. Runs for the entire
 * allotted time, using random restarts when it gets stuck at a local
 * optimum.
 */

public class TwoOpt {

    private int[][] dist;
    private int quality;
    private ArrayList<int[]> qualityList = new ArrayList<>();
    private ArrayList<double[]> timeList;

    TwoOpt(int[][] dist) {
        this.dist = Arrays.copyOf(dist, dist.length);
        quality = 0;
        timeList = new ArrayList<double[]>();
    }

    /**
     * Performs the 2-opt hill climbing local search algorithm on the given
     * distance matrix until time expires.
     *
     * @param cutoff the allowed running time
     * @param seed   the RNG seed for generating a starting tour and subsequent
     *               random restart tours
     */

    public void TwoOptTour(double cutoff, long seed) {
        Random rand = new Random(seed);
        int[] tour = GenerateStartingTour(dist.length, rand);
        int newQuality = quality;
        int bestQuality = quality;
        int[] bestTour = tour;
        double stime = System.nanoTime() / (double) 1000000;
        double ctime = stime;
        boolean hasImproved = true;
        while (ctime - stime < cutoff*1000) {
            while (hasImproved) {
                hasImproved = false;
                for (int i = 0; i < dist.length; i++) {
                    for (int j = i + 1; j < dist.length; j++) {
                        int[] newTour = Swap(i, j, tour);
                        newQuality = getTourQuality(newTour);
                        if (newQuality < quality) {
                            tour = newTour;
                            quality = newQuality;
                            hasImproved = true;
                            if (newQuality < bestQuality) {
                                bestTour = newTour;
                                bestQuality = newQuality;
                                double[] timeElement = {(ctime - stime) / 1000.0, (double) quality};
                                timeList.add(timeElement);
                            }
                        }
                        ctime = System.nanoTime() / (double) 1000000;
                    }
                }
            }
            tour = GenerateRandomTour(dist.length, rand);
            hasImproved = true;
        }
        tour = bestTour;
        quality = bestQuality;
        for (int i = 0; i < tour.length - 1; i++) {
            int[] edge = {tour[i], tour[i + 1], dist[i][i + 1]};
            qualityList.add(edge);
        }
        int[] lastEdge = {tour[tour.length - 1], tour[0], dist[tour.length - 1][0]};
        qualityList.add(lastEdge);
    }

    /**
     * Generates a starting tour by randomly picking a starting city and
     * then following the nearest-neighbor heuristic to construct the rest
     * of the tour.
     *
     * @param size the length of the tour
     * @param rand the RNG seed for picking the first city
     * @return a starting tour
     */
    private int[] GenerateStartingTour(int size, Random rand) {
        ArrayList<Integer> unvisited = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            unvisited.add(i);
        }
        int start = rand.nextInt(size);
        int current = start;
        int[] tour = new int[size];
        int min;
        int minIndex = 0;
        int count = 1;
        tour[0] = start;
        unvisited.remove(new Integer(start));
        while (!unvisited.isEmpty()) {
            min = Integer.MAX_VALUE;
            for (int i = 0; i < size; i++) {
                if (unvisited.contains(new Integer(i))) {
                    if (dist[current][i] < min) {
                        min = dist[current][i];
                        minIndex = i;
                    }
                }
            }
            tour[count] = minIndex;
            count++;
            quality += dist[current][minIndex];
            current = minIndex;
            unvisited.remove(new Integer(minIndex));
        }
        quality += dist[tour[tour.length - 1]][tour[0]];
        return tour;
    }

    /**
     * Generates a random tour.
     * @param size the length of the tour
     * @param rand the RNG seed for picking cities
     * @return a starting tour
     */

    private int[] GenerateRandomTour(int size, Random rand) {
        ArrayList<Integer> tour = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            tour.add(i);
        }
        Collections.shuffle(tour, rand);
        Integer[] Itour = new Integer[tour.size()];
        Itour = tour.toArray(Itour);
        int[] itour = Arrays.stream(Itour).mapToInt(Integer::intValue).toArray();
        quality = dist[itour[itour.length - 1]][itour[0]];
        for (int i = 0; i < size - 1; i++) {
            quality += dist[itour[i]][itour[i+1]];
        }
        return itour;
    }


    /**
     * Swaps two edges in the tour.
     *
     * @param a    the first endpoint of the first edge
     * @param b    the first endpoint of the second edge
     * @param tour the tour on which to perform the swap
     * @return the tour with the given edges swapped
     */

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
        return newTour;
    }

    /**
     * Returns the quality of the tour.
     *
     * @param tour some TSP tour
     * @return the quality of that tour
     */

    private int getTourQuality(int[] tour) {
        int tourQuality = 0;
        for (int i = 0; i < tour.length - 1; i++) {
            tourQuality += dist[tour[i]][tour[i + 1]];
        }
        tourQuality += dist[tour[tour.length - 1]][tour[0]];
        return tourQuality;
    }

    /**
     * Returns the list of solutions and their timestamps.
     *
     * @return data for the .trace file
     */

    public ArrayList<double[]> getTimeList() {
        return timeList;
    }

    /**
     * Returns the list of edges and their weights.
     *
     * @return data for the .sol file
     */

    public ArrayList<int[]> getQualityList() {
        return qualityList;
    }

    /**
     * Returns the quality of the final solution.
     *
     * @return the final solution quality.
     */

    public int getQuality() {
        return quality;
    }
}
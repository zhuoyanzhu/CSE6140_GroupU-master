import java.util.*;

public class ThreeOptTabu {

    private int[][] dist;
    private int quality;
    private ArrayList<int[]> qualityList = new ArrayList<>();
    private ArrayList<double[]> timeList;
    private HashMap<int[], Integer> tabuList = new HashMap<>();
    private int tabuTime;
    private int iters;

    ThreeOptTabu(int[][] dist){
        this.dist = Arrays.copyOf(dist, dist.length);
        timeList = new ArrayList<double[]>();
        tabuTime = 1000;
        iters = 100000;

    }

    public void ThreeOptTabuTour(double cutoff) {
        int[] tour = GenerateRandomTour(dist.length, 600);
        int bestQuality = Integer.MAX_VALUE;
        double stime = System.nanoTime() / (double) 1000000;
        double ctime = stime;
        int iter = 0;
        int[] newTour;
        int newQuality;
        while (iter < iters && ctime - stime < cutoff * 1000) {
            newTour = bestAcceptableNeighbor(tour);
            newQuality = getQuality(newTour);
            if (newQuality < quality) {
                tour = newTour;
                quality = newQuality;
                double[] timeElement = {ctime-stime, (double)quality};
                timeList.add(timeElement);
            }
            iter++;
            ctime = System.nanoTime() / (double) 1000000;
            updateTabuList();
        }
        for (int i = 0; i < tour.length - 1; i++) {
            int[] edge = {tour[i], tour[i+1], dist[i][i+1]};
            qualityList.add(edge);
        }
        int[] lastEdge = {tour[tour.length - 1], tour[0], dist[tour.length - 1][0]};
        qualityList.add(lastEdge);
    }

    private int[] bestAcceptableNeighbor(int[] tour) {
        boolean haveResult = false;
        while (!haveResult) {
            int randA = 0, randB = 0, randC = 0;
            while (randA == randB || randC == randB || randA == randC) {
                randA = (int) (Math.random() * tour.length);
                randB = (int) (Math.random() * tour.length);
                randC = (int) (Math.random() * tour.length);
            }
            int a, b, c;
            if (randA < randB && randA < randC) {
                a = randA;
                b = Math.min(randB, randC);
                c = Math.max(randB, randC);
            } else if (randB < randA && randB < randC) {
                a = randB;
                b = Math.min(randA, randC);
                c = Math.max(randA, randC);
            } else {
                a = randC;
                b = Math.min(randB, randA);
                c = Math.max(randB, randA);
            }
            int[] tourA = SwapA(a, b, c, tour);
            int[] tourB = SwapB(a, b, c, tour);
            int[] tourC = SwapC(a, b, c, tour);
            int[] tourD = SwapD(a, b, c, tour);
            int qualityA = getQuality(tourA);
            int qualityB = getQuality(tourB);
            int qualityC = getQuality(tourC);
            int qualityD = getQuality(tourD);
            int minQuality;
            int[] minTour;
            int[] tabuEntry = {a, b, c};
            boolean allToursChecked = false;
            while (!allToursChecked) {
                minQuality = Math.min(Math.min(qualityA, qualityB), Math.min(qualityC, qualityD));
                if (minQuality == qualityA) minTour = tourA;
                else if (minQuality == qualityB) minTour = tourB;
                else if (minQuality == qualityC) minTour = tourC;
                else minTour = tourD;
                if (tabuList.get(tabuEntry) == null) {
                    tabuList.put(tabuEntry, 0);
                    return minTour;
                } else {
                    if (minQuality == qualityA) qualityA = Integer.MAX_VALUE;
                    else if (minQuality == qualityB) qualityB = Integer.MAX_VALUE;
                    else if (minQuality == qualityC) qualityC = Integer.MAX_VALUE;
                    else qualityD = Integer.MAX_VALUE;
                    if (qualityA == qualityB && qualityB == qualityC && qualityC == qualityD && qualityD == Integer.MAX_VALUE) {
                        allToursChecked = true;
                    }
                }
            }
        }
        return new int[]{0};
    }

    private void updateTabuList() {
        tabuList.replaceAll((k, v) -> v + 1);
        Iterator iter = tabuList.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry)iter.next();
            if ((int)entry.getValue() >= tabuTime) {
                tabuList.remove(entry.getKey());
            }
            iter.remove();
        }
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

    private int getQuality(int[] tour) {
        int tourQuality = 0;
        for (int i = 0; i < tour.length - 1; i++) {
            tourQuality += dist[tour[i]][tour[i+1]];
        }
        tourQuality += dist[tour[tour.length - 1]][tour[0]];
        return tourQuality;
    }

    private int[] SwapA(int a, int b, int c, int[] tour) {
        int[] newTour = new int[tour.length];
        int pos = 0;
        for (int i = 0; i <= a; i++, pos++) {
            newTour[pos] = tour[i];
        }
        for (int i = b+1; i <= c; i++, pos++) {
            newTour[pos] = tour[i];
        }
        for (int i = b; i >= a+1; i--, pos++) {
            newTour[pos] = tour[i];
        }
        for (int i = c + 1; i < tour.length; i++, pos++) {
            newTour[pos] = tour[i];
        }
        return newTour;
    }

    private int[] SwapB(int a, int b, int c, int[] tour) {
        int[] newTour = new int[tour.length];
        int pos = 0;
        for (int i = 0; i <= a; i++, pos++) {
            newTour[pos] = tour[i];
        }
        for (int i = b; i >= a+1; i--, pos++) {
            newTour[pos] = tour[i];
        }
        for (int i = c; i >= b+1; i--, pos++) {
            newTour[pos] = tour[i];
        }
        for (int i = c + 1; i < tour.length; i++, pos++) {
            newTour[pos] = tour[i];
        }
        return newTour;
    }

    private int[] SwapC(int a, int b, int c, int[] tour) {
        int[] newTour = new int[tour.length];
        int pos = 0;
        for (int i = 0; i <= a; i++, pos++) {
            newTour[pos] = tour[i];
        }
        for (int i = b+1; i <= c; i++, pos++) {
            newTour[pos] = tour[i];
        }
        for (int i = a+1; i <= b; i++, pos++) {
            newTour[pos] = tour[i];
        }
        for (int i = c + 1; i < tour.length; i++, pos++) {
            newTour[pos] = tour[i];
        }
        return newTour;
    }

    private int[] SwapD(int a, int b, int c, int[] tour) {
        int[] newTour = new int[tour.length];
        int pos = 0;
        for (int i = 0; i <= a; i++, pos++) {
            newTour[pos] = tour[i];
        }
        for (int i = c; i >= b+1; i--, pos++) {
            newTour[pos] = tour[i];
        }
        for (int i = a+1; i <= b; i++, pos++) {
            newTour[pos] = tour[i];
        }
        for (int i = c + 1; i < tour.length; i++, pos++) {
            newTour[pos] = tour[i];
        }
        return newTour;
    }

    public ArrayList<double[]> getTimeList(){
        return timeList;
    }

    public ArrayList<int[]> getQualityList(){
        return qualityList;
    }

    public int getQuality(){
        return quality;
    }
}
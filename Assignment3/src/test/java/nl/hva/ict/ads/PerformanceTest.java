package nl.hva.ict.ads;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Costa van Elsas
 */
class PerformanceTest {
    final static int MAX_ARCHERS = 5000000;
    final static int MAX_MILLISECONDS = 20000; // 20 seconds
    long totalTimeInsSort = 0;
    long totalTimeQuicksort = 0;
    int numberOfTimesInsertRan = 0;
    int numberOfTimesQuickRan = 0;
    private Random randomizer;

    protected Sorter<Archer> sorter = new ArcherSorter();
    protected List<Archer> archers;
    protected Comparator<Archer> scoringScheme = Archer::compareByHighestTotalScoreWithLeastMissesAndLowestId;


    @BeforeAll
    static void setupClass() {

    }

    @BeforeEach
    void setup() {

    }

    @Test
    void performanceTest() {
        boolean selSortRunning = true;
        boolean quickSortRunning = true;
        Stopwatch selSortTracker;
        Stopwatch quickSortTracker;
        double selSortTime;
        double quickSortTime;

        long generatedLong = new Random().nextLong();

        for (int i = 1; i <= 10; i++) {
            System.out.println("| Iteration: " + i + " |");
            int nrOfArchers = 100;
            selSortRunning = true;
            quickSortRunning = true;
            ChampionSelector championSelector = new ChampionSelector(generatedLong);

            while (nrOfArchers < MAX_ARCHERS && selSortRunning || quickSortRunning) {
                archers = new ArrayList(championSelector.enrollArchers(nrOfArchers));

                List<Archer> selSortList = new ArrayList<>(archers);
                List<Archer> quickSortList = new ArrayList<>(archers);

                System.out.printf("---- Number of archers: %d ---- \n", nrOfArchers);

                if (selSortRunning) {
                    selSortTracker = new Stopwatch();  // stopwatch created to keep track of how long this sorting is running
                    sorter.selInsSort(selSortList, scoringScheme);
                    selSortTime = selSortTracker.elapsedTime();
                    System.out.printf("Selection sort: %.2f ms \n", selSortTime);
                    if (selSortTracker.elapsedTime() >= MAX_MILLISECONDS) {
                        System.out.println("The selection sort took longer than 20 seconds");
                        selSortRunning = false;  // stop sorting after 20 seconds
                    }
                    totalTimeInsSort += selSortTracker.elapsedTime();
                    numberOfTimesInsertRan++;
                }

                if (quickSortRunning) {
                    quickSortTracker = new Stopwatch();  // stopwatch created to keep track of how long this sorting is running
                    sorter.quickSort(quickSortList, scoringScheme);
                    quickSortTime = quickSortTracker.elapsedTime();

                    System.out.printf("QuickSort sort: %.2f ms \n", quickSortTime);
                    if (quickSortTracker.elapsedTime() >= MAX_MILLISECONDS) {
                        System.out.println("The quick sort took longer than 20 seconds");
                        quickSortRunning = false;  // stop sorting after 20 seconds
                    }
                    totalTimeQuicksort += quickSortTracker.elapsedTime();
                    numberOfTimesQuickRan++;
                }
                nrOfArchers *= 2;
                System.out.println();
            }

            System.out.println("Total time insertionsort: " + totalTimeInsSort + " ms");
            System.out.println("Total time quicksort: " + totalTimeQuicksort + " ms");

            long avgIns = totalTimeInsSort / numberOfTimesInsertRan;
            long avgQuick = totalTimeQuicksort / numberOfTimesQuickRan;

            System.out.println("Insertion sort average time: " + avgIns + " ms");
            System.out.println("Quick sort average time: " + avgQuick + " ms");
            System.out.println();

        }
        assertFalse(selSortRunning);
        assertFalse(quickSortRunning);
    }
}

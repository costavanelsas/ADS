package nl.hva.ict.ads;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Costa van Elsas
 */
class ImplementationTest {

    private static Comparator<Archer> scoringScheme;
    Archer archer1, archer2;
    int[] scores1, scores2;

    @BeforeAll
    static void setupClass() {
        scoringScheme = Archer::compareByHighestTotalScoreWithLeastMissesAndLowestId;
    }

    @BeforeEach
    void setup() {
        archer1 = new Archer("Costa", "van Elsas");
        scores1 = new int[]{10,8,0};
        archer2 = new Archer("Gerard", "Joling");
        scores2 = new int[]{9,9,0};
    }

    @Test
    void compareArchersWithThemSelf() {
        archer1.registerScoreForRound(1, scores1);

        //check if the compare returns 0 if you compare the same archer
        assertEquals(0, scoringScheme.compare(archer1, archer1));
    }

    @Test
    void checkComparesWorksBothWays() {
        archer1.registerScoreForRound(1, scores1);
        archer2.registerScoreForRound(1, scores2);

        //check if the compare works both ways
        assertTrue(scoringScheme.compare(archer1, archer2) < 0);
        assertTrue(scoringScheme.compare(archer2, archer1) > 0);
    }
}

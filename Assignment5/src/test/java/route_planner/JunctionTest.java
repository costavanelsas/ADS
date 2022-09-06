package route_planner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JunctionTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void checkHashCodeAndEquals() {
        Junction a1 = new Junction("Amsterdam");
        Junction a2 = new Junction(new String("Amsterdam"));
        Junction r = new Junction("Rotterdam");
        assertEquals(a1, a1);
        assertEquals(a1.hashCode(), a1.hashCode());
        assertEquals(a1, a2,
                "Junctions with the same Id should be equal");
        assertEquals(a1.hashCode(), a2.hashCode(),
                "Equal Junctions should have the same hash code");
        assertNotSame(a1.getName(),a2.getName(),"The setup of this test should involve different strings for Amsterdam");
        assertNotEquals(a1,r,
                "Junctions with a different Id should be different");
        assertNotEquals(a1.hashCode(), r.hashCode(),
                "It is allowed but un-likely that different junctions have the same hash code");
    }
}
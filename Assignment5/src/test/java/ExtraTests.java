import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collection;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import graphs.Country;
import graphs.DirectedGraph;

/**
 * @author Costa van Elsas
 * Class with extra unit test to further support that the code is working correctly
 */
public class ExtraTests {
    Country nl, be, de, lux;
    DirectedGraph<Country, Integer> europe = new DirectedGraph<>();

    @BeforeEach
    void setUp() {
        nl = this.europe.addOrGetVertex(new Country("NL"));
        be = this.europe.addOrGetVertex(new Country("BE"));
        this.europe.addConnection("BE", "NL", 100);
        de = this.europe.addOrGetVertex(new Country("DE"));
        this.europe.addConnection("NL", "DE", 200);
        this.europe.addConnection("BE", "DE", 30);
        lux = this.europe.addOrGetVertex(new Country("LUX"));
        this.europe.addConnection("LUX", "BE", 60);
        this.europe.addConnection("LUX", "DE", 50);
    }

    @Test
    void checkGetNumEdges() {
        assertEquals(10, europe.getNumEdges());
    }

    @Test
    void checkNumVertices() {
        assertEquals(4, europe.getNumVertices());
    }

    @Test
    void checkCountryNeighbours() {

        Collection<Country> neighboursGermany;
        Collection<Country> expectedNeighbours = new HashSet<>();

        expectedNeighbours.add(new Country("BE"));
        expectedNeighbours.add(new Country("NL"));
        expectedNeighbours.add(new Country("LUX"));

        neighboursGermany = europe.getNeighbours(de);

        assertEquals(3, neighboursGermany.size());

        assertEquals(expectedNeighbours, neighboursGermany);
    }

    @Test
    void checkDSPSearchEndIsCorrect() {
        DirectedGraph<Country, Integer>.DGPath path = europe.dijkstraShortestPath("NL", "LUX", b -> 2.0);
        assertNotNull(path);
        assertEquals(europe.getVertexById("NL"), path.getVertices().peek());
        assertEquals(europe.getVertexById("LUX"), path.getVertices().stream().reduce((country1, country2) -> country2).get());
    }

    @Test
    void checkDFSearchEndIsCorrect() {
        DirectedGraph<Country, Integer>.DGPath path = europe.depthFirstSearch("NL", "DE");
        assertNotNull(path);
        assertEquals(europe.getVertexById("NL"), path.getVertices().peek());
        assertEquals(europe.getVertexById("DE"), path.getVertices().stream().reduce((country1, country2) -> country2).get());
    }

    @Test
    void checkBFSearchEndIsCorrect() {
        DirectedGraph<Country, Integer>.DGPath path = europe.breadthFirstSearch("BE", "NL");
        assertNotNull(path);
        assertEquals(europe.getVertexById("BE"), path.getVertices().peek());
        assertEquals(europe.getVertexById("NL"), path.getVertices().stream().reduce((country1, country2) -> country2).get());
    }
}

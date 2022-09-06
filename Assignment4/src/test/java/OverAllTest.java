import models.ClimateTracker;
import models.Measurement;
import models.Station;

import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.*;

public class OverAllTest {

    ClimateTracker climateTracker;
    Station deBilt, vlissingen;

    @BeforeEach
    private void setup() {
        climateTracker = new ClimateTracker();

        climateTracker.importClimateDataFromVault(ClimateTracker.class.getResource("/test").getPath());

        deBilt = climateTracker.findStationById(260);
        vlissingen = climateTracker.findStationById(310);
    }

    @Test
    public void checkStationById() {
        Station bilt = climateTracker.findStationById(260);
        Station vliss = climateTracker.findStationById(310);

        assertEquals(bilt, this.deBilt);
        assertEquals(vliss, this.vlissingen);
    }

    @Test
    public void allTimeMaxTemp() {
        double allTimeMaxTempDeBilt = deBilt.allTimeMaxTemperature();

        assertEquals(37.5, allTimeMaxTempDeBilt);
    }

    @Test
    public void totalPrecipitation() {
        double totalPrecipitationBilt = deBilt.totalPrecipitationBetween(LocalDate.of(2020,1,1), LocalDate.of(2020,12,31));

        assertEquals(690.6, totalPrecipitationBilt);
    }

    @Test
    public void averageTemperature() {
        double avgTemp = deBilt.averageBetween(LocalDate.of(2020,1,3), LocalDate.of(2020,1,4), Measurement::getAverageTemperature);

        assertEquals(7.15, avgTemp);
    }

    @Test
    public void coldestYear() {
        double coldestYear = climateTracker.coldestYear();

        assertEquals(2020, coldestYear);
    }

}
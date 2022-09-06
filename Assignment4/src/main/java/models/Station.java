package models;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Costa van Elsas
 * Station class, adding measurements and other methods to further accomodate the staion class
 */
public class Station {
    private final int stn;
    private final String name;
    private NavigableMap<LocalDate, Measurement> measurements;

    public Station(int id, String name) {
        this.stn = id;
        this.name = name;
        measurements = new TreeMap<>();
    }

    public Collection<Measurement> getMeasurements() {
        return measurements.values();
    }

    public int getStn() {
        return stn;
    }

    public String getName() {
        return name;
    }

    /**
     * import station number and name from a text line
     *
     * @param textLine
     * @return a new Station instance for this data
     * or null if the data format does not comply
     */
    public static Station fromLine(String textLine) {
        String[] fields = textLine.split(",");
        if (fields.length < 2) return null;
        try {
            return new Station(Integer.valueOf(fields[0].trim()), fields[1].trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * Add a collection of new measurements to this station.
     * Measurements that are not related to this station
     * and measurements with a duplicate date shall be ignored and not added
     *
     * @param newMeasurements
     * @return the nett number of measurements which have been added.
     */
    public int addMeasurements(Collection<Measurement> newMeasurements) {
        int oldSize = this.getMeasurements().size();

        //create a new dates hashset
        Set<LocalDate> dates = new HashSet<>();

        Stream<Measurement> filterStation = newMeasurements.stream() // stream the new measurements
                .filter(measurement -> measurement.getStation().equals(this)) //check if its the right station
                .filter(measurement -> dates.add(measurement.getDate())); //add the date to the new dates hashset

        Map<LocalDate, Measurement> newMap = filterStation
                .collect(Collectors.toMap(Measurement::getDate, measurement -> measurement)); //create a new map and add measurement to the date

        this.measurements.putAll(newMap); // add the new map values to the measurements

        return this.getMeasurements().size() - oldSize;
    }

    /**
     * calculates the all-time maximum temperature for this station
     *
     * @return the maximum temperature ever measured at this station
     * returns Double.NaN when no valid measurements are available
     */
    public double allTimeMaxTemperature() {

        double maxTemp = measurements.values().stream() // stream over the measurements from this station
                .map(measurement -> {
                    try {
                        return measurement.getMaxTemperature(); // try to return the max temperature otherwise return double.nan
                    } catch (NumberFormatException e) {
                        return Double.NaN;
                    }
                })
                .collect(Collectors.summarizingDouble(measurement -> measurement)).getMax();
                // get all the doubles from the map and return the highest double (temperature)

        // catch empty temperature values
        if (maxTemp == Double.NEGATIVE_INFINITY) {
            maxTemp = Double.NaN;
        }

        //return the max temperature measured at this station
        return maxTemp;
    }

    /**
     * @return the date of the first day of a measurement for this station
     * returns Optional.empty() if no measurements are available
     */
    public Optional<LocalDate> firstDayOfMeasurement() {
        return measurements.navigableKeySet().stream() //stream over the measurements in a key set
                .min(LocalDate::compareTo); // compare the measurements by date and get the smallest one
    }

    /**
     * calculates the number of valid values of the data field that is specified by the mapper
     * invalid or empty values should be are represented by Double.NaN
     * this method can be used to check on different types of measurements each with their own mapper
     *
     * @param mapper the getter method of the data field to be checked.
     * @return the number of valid values found
     */
    public int numValidValues(Function<Measurement, Double> mapper) {
         return Math.toIntExact(measurements.values().stream() //stream over measurements
                 .filter(measurement -> !Double.isNaN(mapper.apply(measurement)))
                 .count()); //check if its empty and count the number of valid values
    }

    /**
     * calculates the total precipitation at this station
     * across the time period between startDate and endDate (inclusive)
     *
     * @param startDate the start date of the period of accumulation (inclusive)
     * @param endDate   the end date of the period of accumulation (inclusive)
     * @return the total precipitation value across the period
     * 0.0 if no measurements have been made in this period.
     */
    public double totalPrecipitationBetween(LocalDate startDate, LocalDate endDate) {
        return measurements.subMap(startDate, true, endDate, true).values().stream() //create a submap and stream over it
                .mapToDouble(Measurement::getPrecipitation) //get the precipitation
                .filter(checkEmpty -> !Double.isNaN(checkEmpty)).sum(); //check if the sum is empty
    }

    /**
     * calculates the average of all valid measurements of the quantity selected by the mapper function
     * across the time period between startDate and endDate (inclusive)
     *
     * @param startDate the start date of the period of averaging (inclusive)
     * @param endDate   the end date of the period of averaging (inclusive)
     * @param mapper    a getter method that obtains the double value from a measurement instance to be averaged
     * @return the average of all valid values of the selected quantity across the period
     * Double.NaN if no valid measurements are available from this period.
     */
    public double averageBetween(LocalDate startDate, LocalDate endDate, Function<Measurement, Double> mapper) {
        return measurements.subMap(startDate, true, endDate, true).values().stream() //create a submap and stream over it
                .map(mapper)
                .filter(checkEmpty -> !Double.isNaN(checkEmpty)) //filter empty values
                .mapToDouble(average -> average).average().orElse(Double.NaN); // get the average otherwise double.nan
    }

    /**
     * to string method that gets the station id/Name
     */
    @Override
    public String toString() {
        return getStn() + "/" + getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return getStn() == station.getStn();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStn());
    }
}

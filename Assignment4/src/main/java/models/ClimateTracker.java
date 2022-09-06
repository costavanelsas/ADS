package models;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Costa van Elsas
 * Climate tracker class
 * All the functionalities to realise the climate tracker
 */
public class ClimateTracker {
    private final String MEASUREMENTS_FILE_PATTERN = ".*\\.txt";

    private Map<Integer, Station> stations;        // all available weather stations organised by Station Number (STN)

    /**
     * return the stations
     *
     * @return a hash set of all the stations
     */
    public Set<Station> getStations() {
        return new HashSet<>(stations.values());
    }

    /**
     * Gets a specific station by ID
     *
     * @param stn
     * @return station
     */
    public Station findStationById(int stn) {
        return stations.get(stn);
    }

    public ClimateTracker() {
        this.stations = new HashMap<>();
    }

    /**
     * calculates for each station how many Measurement instances have been registered
     *
     * @return the total number of measurements as an int
     */
    public Map<Station, Integer> numberOfMeasurementsByStation() {
        return stations.values().stream() //stream over stations
        .collect(Collectors.groupingBy(station -> station, //get the stations and group them
                LinkedHashMap::new,
                Collectors.summingInt(station -> station.getMeasurements().size()))); // get the total int of measurements
    }

    /**
     * calculates for each station the date of the first day of its measurements
     * stations without measurements shall be excluded from the result
     *
     * @return the first day of a measurement
     */
    public Map<Station, LocalDate> firstDayOfMeasurementByStation() {
        return stations.values().stream() //stream over stations
                .filter(station -> station.firstDayOfMeasurement().isPresent()) //filter stations and check if the first day is present
                .collect(Collectors.toMap(station -> station,
                        station -> station.firstDayOfMeasurement().get(), //get the first day
                        (value1, value2) -> value1, HashMap::new)); //compare the days and put them into a hashmap
    }

    /**
     * calculates for each station how many valid values it has available for the measurement quantity
     * that can be accessed by the mapper.
     * invalid values are registered as Double.NaN. They originate from empty or corrupt data in the source file
     *
     * @param mapper a getter method that accesses the selected quantity from a Measurement instance
     * @return the number of valid values per station
     */
    public Map<Station, Integer> numberOfValidValuesByStation(Function<Measurement, Double> mapper) {
        return stations.values().stream() //stream over stations
                .collect(Collectors.toMap(station -> station, 
                        station -> station.numValidValues(mapper), //check if values are correct
                        (value1, value2) -> value1, HashMap::new)); //compare the values and put them into a hashmap
    }

    /**
     * Calculates for each calendar year in the dataset the average daily temperatures
     * across all days in the year and all stations in this tracker
     * (invalid values shall be excluded from the averaging)
     *
     * @return a map(Y,T) that provides for each year Y the average temperature T of that year
     */
    public Map<Integer, Double> annualAverageTemperatureTrend() {
        return stations.values().stream() //stream over stations
                .flatMap(station -> station.getMeasurements().stream()) //create a new map and put the measurements in
                .filter(measurement -> !Double.isNaN(measurement.getAverageTemperature())) //filter on avg temperature and check if its not empty
                .collect(Collectors.groupingBy(measurement -> measurement.getDate().getYear(), //group per year
                        Collectors.averagingDouble(Measurement::getAverageTemperature))); //get the average temperature
    }

    /**
     * Calculates for each calendar year in the dataset the maximum of the selected daily quantity
     * across all days in the year and all stations in this tracker
     * (invalid values shall be excluded from the maximum aggregation)
     * (this method can be reused for maximum aggregation of different quantities in the Measurement data
     *
     * @param mapper a getter function on the Measurement class
     *               that selects the appropriate value for the maximum aggregation procedure
     * @return a map(Y,Q) that provides for each year Y the maximum value Q of the specified quantity
     */
    public Map<Integer, Double> annualMaximumTrend(Function<Measurement, Double> mapper) {
        return stations.values().stream() //stream over stations
                .flatMap(station -> station.getMeasurements().stream()) //create a new map and put the measurements in
                .filter(measurement -> !Double.isNaN(mapper.apply(measurement)))//filter and check if its not empty
                .collect(Collectors.toMap(measurement -> measurement.getDate().getYear(), mapper, Double::max)); //return the max double value of a specific year
    }

    /**
     * Calculates for each of the 12 calendar months the average daily hours of sunshine
     * across all years and all stations
     * The graph of these numbers can be found in touristic brochures.
     * (invalid values shall be excluded from the averaging)
     *
     * @return a map(M,SQ) that provides for each month M the average daily sunshine hours SQ across all times
     */
    public Map<Month, Double> allTimeAverageDailySolarByMonth() {
        return stations.values().stream() //stream over stations
                .flatMap(station -> station.getMeasurements().stream()  //create a new map and put the measurements in
                .filter(measurement -> !Double.isNaN(measurement.getSolarHours())))  //filter on avg temperature and check if its not empty
                .collect(Collectors.groupingBy(measurement -> measurement.getDate().getMonth(), //group by the month
                        LinkedHashMap::new, Collectors.averagingDouble(Measurement::getSolarHours))); //get the average solar hours linkedhashmap so the months are sorted in order
    }

    /**
     * calculate the coldest year of all times.
     * The coldest year is defined as the year with the lowest total sum of daily minimum temperatures below zero
     * accumulated across all stations
     * I.e. any daily value of a minimum temperature at a station that is above zero should not be included in its sum for the year
     * The lowest yearsum of negative minimum temperatures indicates the coldest year.
     *
     * @return the coldest year (a number between 1900 and 2099)
     * return -1 if no valid minimum temperature measurements are available
     */
    public int coldestYear() {
        return stations.values().stream() //stream over stations
                .flatMap(station -> station.getMeasurements().stream() //create a new map and put the measurements in
                .filter(measurement -> !Double.isNaN(measurement.getMinTemperature()) && measurement.getMinTemperature() <= 0)) //get the min temperature and check if its not empty and smaller than 0
                .collect(Collectors.groupingBy(measurement -> measurement.getDate().getYear(), 
                Collectors.summingDouble(Measurement::getMinTemperature))) //get the min temp per year
                .entrySet().stream() //stream over entryset
                .min(Map.Entry.comparingByValue()) //compare values
                .map(Map.Entry::getKey).orElse(-1); //get the min temp measurement otherwise return -1
    }

    /**
     * imports all station and measurement information
     *
     * @param folderPath
     */
    public void importClimateDataFromVault(String folderPath) {
        this.stations.clear();

        // load all stations from the text file
        importItemsFromFile(this.stations,
                folderPath + "/stations.txt", null,
                Station::fromLine, Station::getStn);

        // load all measurements from the folder
        importMeasurementsFromVault(folderPath + "/measurements");
    }

    /**
     * traverses the purchases vault recursively and processes every data file that it finds
     *
     * @param filePath
     */
    public void importMeasurementsFromVault(String filePath) {

        File file = new File(filePath);

        if (file.isDirectory()) {
            // the file is a folder (a.k.a. directory)
            //  retrieve a list of all files and sub folders in this directory
            File[] filesInDirectory = Objects.requireNonNullElse(file.listFiles(), new File[0]);

            // merge all purchases of all files and sub folders from the filesInDirectory list, recursively.
            for (File f : filesInDirectory) {
                this.importMeasurementsFromVault(f.getAbsolutePath());
            }
        } else if (file.getName().matches(MEASUREMENTS_FILE_PATTERN)) {
            // the file is a regular file that matches the target pattern for raw purchase files
            // merge the content of this file into this.purchases
            this.importMeasurementsFromFile(file.getAbsolutePath());
        }
    }

    /**
     * imports a collection of items from a text file which provides one line for each item
     *
     * @param items     the list to which imported items shall be added
     * @param filePath  the file path of the source text file
     * @param converter a function that can convert a text line into a new item instance
     * @param <E>       the (generic) type of each item
     */
    private static <K, E> void importItemsFromFile(Map<K, E> items, String filePath, String headerPrefix,
                                                   Function<String, E> converter, Function<E, K> mapper) {
        int originalNumItems = items.size();

        Scanner scanner = createFileScanner(filePath);

        // skip lines until the header is hit
        while (headerPrefix != null && scanner.hasNext()) {
            // import another line of the header
            String line = scanner.nextLine();

            if (line.startsWith(headerPrefix)) headerPrefix = null;
        }

        //  read all remaining source lines from the scanner,
        //  convert each line to an item of type E and
        //  and add each item to the map
        int newCount = 0;
        while (scanner.hasNext()) {
            // input another line
            String line = scanner.nextLine();

            // convert the line into an instance of E
            E newItem = converter.apply(line);

            //System.out.printf("extracted %s from line: %s\n", newItem, line);

            //  put the item into the map after successful conversion,
            //  using the mapper to determine the key
            //  do not overwrite existing items in the map
            if (newItem != null) {
                items.putIfAbsent(mapper.apply(newItem), newItem);
                newCount++;
            }
        }

        if (items.size() < originalNumItems + newCount) {
            throw new InputMismatchException(String.format("Duplicate items found in file %s", filePath));
        }

        //System.out.printf("Imported %d items from %s.\n", items.size() - originalNumItems, filePath);
    }

    /**
     * imports another batch of raw purchase data from the filePath text file
     * and merges the purchase amounts with the earlier imported and accumulated collection in this.purchases
     *
     * @param filePath
     */
    private void importMeasurementsFromFile(String filePath) {

        // create a temporary map to import the measurements, organised by date
        Map<LocalDate, Measurement> newMeasurementsByDate = new HashMap<>();

        // import all measurements from the specified file into the newMeasurementsByDate map
        importItemsFromFile(newMeasurementsByDate, filePath, "# STN",
                s -> Measurement.fromLine(s, this.stations), Measurement::getDate);

        //  add the measurements to their station
        //  (all measurements from the same file should belong to the same station)
        if (newMeasurementsByDate.size() > 0) {
            Station station = newMeasurementsByDate.values().stream().findAny().get().getStation();
            // add the newMeasurements to the map in the station
            int numAdded = station.addMeasurements(newMeasurementsByDate.values());
            if (numAdded != newMeasurementsByDate.size()) {
                throw new InputMismatchException(String.format("Some items in file %s could not be added", filePath));
            }
        }
    }

    /**
     * helper method to create a scanner on a file an handle the exception
     *
     * @param filePath
     * @return
     */
    private static Scanner createFileScanner(String filePath) {
        try {
            return new Scanner(new File(filePath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFound exception on path: " + filePath);
        }
    }
}

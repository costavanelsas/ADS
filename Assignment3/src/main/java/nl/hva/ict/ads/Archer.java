package nl.hva.ict.ads;

public class Archer {
    public static final int MAX_ARROWS = 3;
    public static final int MAX_ROUNDS = 10;

    private static int idNumbering = 135788;
    private final int id;
    private String firstName;
    private String lastName;

    private int numberOfZeros;
    private final int[][] scores = new int[MAX_ROUNDS][MAX_ARROWS];

    /**
     * Constructs a new instance of Archer and assigns a unique id to the instance.
     * Each new instance should be assigned a number that is 1 higher than the last one assigned.
     * The first instance created should have ID 135788;
     *
     * @param firstName the archers first name.
     * @param lastName  the archers surname.
     */
    public Archer(String firstName, String lastName) {
        this.id = idNumbering; // sets the id to idNumbering, so it starts at the preferred number (135788)
        idNumbering++;

        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Registers the points for each of the three arrows that have been shot during a round.
     *
     * @param round  the round for which to register the points. First round has number 1.
     * @param points the points shot during the round, one for each arrow.
     */
    public void registerScoreForRound(int round, int[] points) {
        //loop through the points and add the scores + round
        for (int i = 0; i < points.length; i++) {
            this.scores[round - 1][i] = points[i];

            //calculate how many 0 points there are scored
            if (this.scores[round - 1][i] == 0) {
                numberOfZeros++;
            }
        }
    }

    /**
     * Calculates/retrieves the total score of all arrows across all rounds
     *
     * @return total score
     */
    public int getTotalScore() {
        int totalScore = 0;
        // loop through the rounds and count the scores
        for (int i = 0; i < MAX_ROUNDS; i++) {
            for (int j = 0; j < MAX_ARROWS; j++) {
                totalScore += scores[i][j]; //scores per round added to total
            }
        }
        return totalScore;
    }

    /**
     * compares the scores/id of this archer with the scores/id of the other archer according to
     * the scoring scheme: highest total points -> least misses -> earliest registration
     * The archer with the lowest id has registered first
     *
     * @param other the other archer to compare against
     * @return negative number, zero or positive number according to Comparator convention
     */
    public int compareByHighestTotalScoreWithLeastMissesAndLowestId(Archer other) {
        //compare total score
        if (this.getTotalScore() < other.getTotalScore()) return +1;
        if (this.getTotalScore() > other.getTotalScore()) return -1;
        //compare number of zeros
        if (this.getNumberOfZeros() > other.getNumberOfZeros()) return +1;
        if (this.getNumberOfZeros() < other.getNumberOfZeros()) return -1;
        //compare by ID number
        if (this.getId() > other.getId()) return +1;
        if (this.getId() < other.getId()) return -1;
        return 0;
    }

    /**
     * Get the ID
     *
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Get the first name
     *
     * @return firstname
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Get the last name
     *
     * @return lastname
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Get the number of 0 scored
     *
     * @return numberofzeros
     */
    public int getNumberOfZeros() {
        return numberOfZeros;
    }

    /**
     * Create the toString method
     *
     * @return 135788 (100) Jerry Herry" for example
     */
    @Override
    public String toString() {
        return getId() + " (" + getTotalScore() + ") " + getFirstName() + " " + getLastName();
    }
}

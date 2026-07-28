import java.util.List;
import java.util.Objects;
import java.io.*;
import java.util.Scanner;

/**
 * Time class representing time in hours, minutes, and seconds
 * Handles time parsing, comparison, and conversion operations
 */
class Time implements Comparable<Time> {
    private int hours;
    private int minutes;
    private int seconds;

    /**
     * Constructor with individual time components
     * @param hours hour value (0-23)
     * @param minutes minute value (0-59)
     * @param seconds second value (0-59)
     */
    public Time(int hours, int minutes, int seconds) {
        if (hours > 23 || hours < 0) {
            throw new IllegalArgumentException("Hour must be valid");
        }
        if (minutes < 0 || minutes > 59) {
            throw new IllegalArgumentException("Minutes must be valid");
        }
        if (seconds < 0 || seconds > 59) {
            throw new IllegalArgumentException("Seconds must be valid");
        }
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    /**
     * Constructor with time string in HH:MM:SS format
     * @param timeStr time string to parse
     */
    public Time(String timeStr) {
        String[] parts = timeStr.split(":");
        this.hours = Integer.parseInt(parts[0]);
        this.minutes = Integer.parseInt(parts[1]);
        this.seconds = Integer.parseInt(parts[2]);
        if (hours > 23 || hours < 0) {
            throw new IllegalArgumentException("Hour must be valid");
        }
        if (minutes < 0 || minutes > 59) {
            throw new IllegalArgumentException("Minutes must be valid");
        }
        if (seconds < 0 || seconds > 59) {
            throw new IllegalArgumentException("Seconds must be valid");
        }
    }

    /**
     * Convert time to total seconds since midnight
     * @return total seconds since midnight
     */
    public int toSecondsSinceMidnight() {
        return hours * 3600 + minutes * 60 + seconds;
    }

    // Getter methods
    public int getHours() { return hours; }
    public int getMinutes() { return minutes; }
    public int getSeconds() { return seconds; }

    @Override
    public int compareTo(Time other) {
        return Integer.compare(this.toSecondsSinceMidnight(), other.toSecondsSinceMidnight());
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hours, minutes, seconds);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Time time = (Time) obj;
        return hours == time.hours && minutes == time.minutes && seconds == time.seconds;
    }

    /**
     * Static factory method to create Time from string
     * @param timeStr time string to parse
     * @return new Time object
     */
    public static Time valueOf(String timeStr) {
        return new Time(timeStr);
    }
}

/**
 * Coordinate class representing latitude and longitude
 * Handles GPS coordinate validation and distance calculations
 */
class Coordinate {
    private double latitude;
    private double longitude;

    /**
     * Constructor with latitude and longitude values
     * @param latitude latitude value (-90.0 to 90.0)
     * @param longitude longitude value (-180.0 to 180.0)
     */
    public Coordinate(double latitude, double longitude) {
        if (latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException("Latitude must be valid");
        }
        if (longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("Longitude must be valid");
        }
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Calculate distance to another coordinate using Haversine formula
     * @param other the other coordinate
     * @return distance in meters
     */
    public double distTo(Coordinate other) {
        // Haversine formula for distance between two points on Earth
        final double R = 6371000; // Earth's radius in meters
        double lat1Rad = Math.toRadians(this.latitude);
        double lat2Rad = Math.toRadians(other.latitude);
        double deltaLat = Math.toRadians(other.latitude - this.latitude);
        double deltaLon = Math.toRadians(other.longitude - this.longitude);

        double a = Math.sin(deltaLat/2) * Math.sin(deltaLat/2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon/2) * Math.sin(deltaLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return R * c;
    }

    // Getter methods
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    @Override
    public String toString() {
        return "( lat= " + latitude +", long= "  +  longitude + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Coordinate that = (Coordinate) obj;
        return Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    /**
     * Static factory method to create Coordinate from string
     * @param coordStr coordinate string in format "lat,lon"
     * @return new Coordinate object
     */
    public static Coordinate valueOf(String coordStr) {
        String[] parts = coordStr.split(",");
        return new Coordinate(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
    }
}

/**
 * Trackpoint class representing a single GPS point with time, location, and elevation
 * Handles trackpoint creation, validation, and CSV parsing
 */
class Trackpoint {
    private Coordinate coordinate;
    private double elevation;
    private Time time;

    /**
     * Constructor for Trackpoint
     * @param coordinate GPS coordinate
     * @param elevation elevation in meters
     * @param time timestamp
     */
    public Trackpoint(Coordinate coordinate, double elevation, Time time) {
        if (coordinate == null) {
            throw new IllegalArgumentException("Coordinate cannot be null");
        }
        if (time == null) {
            throw new IllegalArgumentException("Time cannot be null");
        }
        this.coordinate = coordinate;
        this.elevation = elevation;
        this.time = time;
    }

    /**
     * Parse a single trackpoint from CSV line
     * @param csvLine CSV line in format "Time,Lat,Lon,Elevation"
     * @return new Trackpoint object
     */
    public static Trackpoint readFromCSV(String csvLine) {
        if (csvLine == null || csvLine.trim().isEmpty()) {
            throw new IllegalArgumentException("CSV line cannot be null or empty");
        }
        String[] parts = csvLine.split(",");
        if (parts.length != 4) {
            throw new IllegalArgumentException("CSV line must have exactly 4 parts (Time,Lat,Lon,Elevation), got: " + parts.length);
        }

        try {
            Time time = new Time(parts[0].trim());
            double lat = Double.parseDouble(parts[1].trim());
            double lon = Double.parseDouble(parts[2].trim());
            double elevation = Double.parseDouble(parts[3].trim());

            return new Trackpoint(new Coordinate(lat, lon), elevation, time);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format in CSV line: " + csvLine, e);
        }
    }

    /**
     * Read all trackpoints from CSV file
     * @param filename path to CSV file
     * @return list of trackpoints
     * @throws IOException if file reading fails
     */
    public static java.util.List<Trackpoint> readAllFromCSV(String filename) throws IOException {
        java.util.List<Trackpoint> trackpoints = new java.util.ArrayList<>();

        try (Scanner scanner = new Scanner(new File(filename))) {
            if (scanner.hasNextLine()) {
                String headerLine = scanner.nextLine();
                if (!headerLine.toLowerCase().contains("time") &&
                        !headerLine.toLowerCase().contains("latitude")) {
                    trackpoints.add(readFromCSV(headerLine));
                }
            }

            int lineNumber = 2;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) {
                    try {
                        trackpoints.add(readFromCSV(line));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Warning: Skipping invalid line " + lineNumber + ": " + e.getMessage());
                    }
                }
                lineNumber++;
            }
        }

        return trackpoints;
    }

    /**
     * Calculate elevation distance to another trackpoint
     * @param other the other trackpoint
     * @return absolute elevation difference in meters
     */
    public double eleDist(Trackpoint other) {
        return Math.abs(this.elevation - other.elevation);
    }

    @Override
    public String toString() {
        return String.format("Trackpoint[time=%s, coord=%s, elevation=%.1f]",
                time, coordinate, elevation);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Trackpoint that = (Trackpoint) obj;
        return Double.compare(that.elevation, elevation) == 0 &&
                Objects.equals(coordinate, that.coordinate) &&
                Objects.equals(time, that.time);
    }

    // Getter methods
    public Coordinate getCoordinate() { return coordinate; }
    public double getElevation() { return elevation; }
    public Time getTime() { return time; }
}

/**
 * HikeData class for analyzing complete hike data
 * Provides methods for calculating hike statistics and metrics
 */
class HikeData {
    private Trackpoint[] hiketrip;
    private int restThreshold = 60; // Default rest threshold in seconds
    private static final double MOVEMENT_THRESHOLD = 0.0005; // ~55 meters for GPS drift
    private static final double ELEVATION_THRESHOLD = 1.0; // 1 meter elevation change threshold

    /**
     * Constructor for HikeData
     * @param csvfileread array of trackpoints representing the hike
     */
    public HikeData(Trackpoint[] csvfileread) {
        if (csvfileread == null) {
            throw new IllegalArgumentException("For the Hike to be a trip it must be an actual hike, and not null");
        }
        hiketrip = csvfileread;
    }

    /**
     * Set the rest threshold for determining rest periods
     * @param seconds threshold in seconds
     */
    public void setRestThreshold(int seconds) {
        this.restThreshold = seconds;
    }

    /**
     * Calculate time difference between two times, handling midnight wraparound
     * @param earlier the earlier time
     * @param later the later time
     * @return time difference in seconds
     */
    private int calculateTimeDifference(Time earlier, Time later) {
        int earlierSeconds = earlier.toSecondsSinceMidnight();
        int laterSeconds = later.toSecondsSinceMidnight();

        // Handle midnight wraparound
        if (laterSeconds < earlierSeconds) {
            laterSeconds += 24 * 3600; // Add 24 hours worth of seconds
        }

        return laterSeconds - earlierSeconds;
    }

    /**
     * Check if a segment should be considered a rest
     * @param index the index of the current trackpoint
     * @return true if this segment is a rest
     */
    private boolean isRestSegment(int index) {
        Trackpoint prev = hiketrip[index - 1];
        Trackpoint curr = hiketrip[index];

        double latDiff = Math.abs(prev.getCoordinate().getLatitude() - curr.getCoordinate().getLatitude());
        double lonDiff = Math.abs(prev.getCoordinate().getLongitude() - curr.getCoordinate().getLongitude());
        double eleDiff = Math.abs(prev.getElevation() - curr.getElevation());

        int timeDiff = calculateTimeDifference(prev.getTime(), curr.getTime());

        // Consider it a rest if position AND elevation haven't changed significantly
        // AND the time difference exceeds the rest threshold
        return (latDiff <= MOVEMENT_THRESHOLD &&
                lonDiff <= MOVEMENT_THRESHOLD &&
                eleDiff <= ELEVATION_THRESHOLD &&
                timeDiff >= restThreshold);
    }

    /**
     * Calculate total distance of the hike
     * @return total distance in meters
     */
    public double length() {
        if (hiketrip.length < 2) {
            return 0.0;
        }

        double totalDistance = 0.0;
        for (int i = 1; i < hiketrip.length; i++) {
            totalDistance += hiketrip[i - 1].getCoordinate().distTo(hiketrip[i].getCoordinate());
        }
        return totalDistance;
    }

    /**
     * Calculate total ascent (upward elevation gain)
     * @return total ascent in meters
     */
    public double ascent() {
        if (hiketrip.length < 2) {
            return 0.0;
        }

        double totalAscent = 0.0;
        for (int i = 1; i < hiketrip.length; i++) {
            double elevationChange = hiketrip[i].getElevation() - hiketrip[i - 1].getElevation();
            if (elevationChange > 0) {
                totalAscent += elevationChange;
            }
        }
        return totalAscent;
    }

    /**
     * Calculate total descent (downward elevation loss)
     * @return total descent in meters
     */
    public double descent() {
        if (hiketrip.length < 2) {
            return 0.0;
        }

        double totalDescent = 0.0;
        for (int i = 1; i < hiketrip.length; i++) {
            double elevationChange = hiketrip[i].getElevation() - hiketrip[i - 1].getElevation();
            if (elevationChange < 0) {
                totalDescent += Math.abs(elevationChange);
            }
        }
        return totalDescent;
    }

    /**
     * Calculate total elapsed time of the hike
     * @return elapsed time in seconds
     */
    public int elapsed() {
        if (hiketrip.length < 2) {
            return 0;
        }

        Time startTime = hiketrip[0].getTime();
        Time endTime = hiketrip[hiketrip.length - 1].getTime();

        return calculateTimeDifference(startTime, endTime);
    }

    /**
     * Calculate moving time (time when actually moving)
     * @return moving time in seconds
     */
    public int moving() {
        if (hiketrip.length < 2) {
            return 0;
        }

        int movingTime = 0;

        for (int i = 1; i < hiketrip.length; i++) {
            int timeDiff = calculateTimeDifference(hiketrip[i - 1].getTime(), hiketrip[i].getTime());

            // Only count as moving time if it's NOT a rest segment
            if (!isRestSegment(i)) {
                movingTime += timeDiff;
            }
        }
        return movingTime;
    }

    /**
     * Calculate average pace in minutes per mile
     * @return pace as formatted string "MM:SS"
     */
    public String pace() {
        double distanceMeters = length();
        int movingTimeSeconds = moving();

        if (distanceMeters == 0.0 || movingTimeSeconds == 0) {
            return "0:00";
        }

        double distanceMiles = distanceMeters * 0.000621371; // Convert meters to miles
        double minutesPerMile = (movingTimeSeconds / 60.0) / distanceMiles;

        int minutes = (int) minutesPerMile;
        int seconds = (int) ((minutesPerMile - minutes) * 60);

        return String.format("%d:%02d", minutes, seconds);
    }

    /**
     * Calculate average speed in mph
     * @return speed in miles per hour
     */
    public double speed() {
        double distanceMeters = length();
        int movingTimeSeconds = moving();

        if (distanceMeters == 0.0 || movingTimeSeconds == 0) {
            return 0.0;
        }

        double distanceMiles = distanceMeters * 0.000621371;
        double timeHours = movingTimeSeconds / 3600.0;

        return distanceMiles / timeHours;
    }

    /**
     * Calculate total rest time
     * @return rest time in seconds
     */
    public int restTime() {
        return elapsed() - moving();
    }

    /**
     * Find the longest rest period
     * @return longest rest time in seconds
     */
    public int longestRest() {
        if (hiketrip.length < 2) {
            return 0;
        }

        int maxRestTime = 0;

        for (int i = 1; i < hiketrip.length; i++) {
            if (isRestSegment(i)) {
                int timeDiff = calculateTimeDifference(hiketrip[i - 1].getTime(), hiketrip[i].getTime());
                if (timeDiff > maxRestTime) {
                    maxRestTime = timeDiff;
                }
            }
        }
        return maxRestTime;
    }

    /**
     * Count the number of rest periods
     * @return number of rest periods
     */
    public int numberRests() {
        if (hiketrip.length < 2) {
            return 0;
        }

        int restCount = 0;

        for (int i = 1; i < hiketrip.length; i++) {
            if (isRestSegment(i)) {
                restCount++;
            }
        }
        return restCount;
    }

    /**
     * Format time in seconds to HH:MM:SS format
     * @param seconds time in seconds
     * @return formatted time string
     */
    public static String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        return String.format("%d:%02d:%02d", hours, minutes, secs);
    }

    /**
     * Convert meters to feet
     * @param meters distance in meters
     * @return distance in feet
     */
    private double metersToFeet(double meters) {
        return meters * 3.28084;
    }

    /**
     * Convert meters to miles
     * @param meters distance in meters
     * @return distance in miles
     */
    private double metersToMiles(double meters) {
        return meters * 0.000621371;
    }
}

/**
 * ClimbSegment class representing a climbing segment in a hike
 * Contains start/end points, elevation gain, and distance information
 */
class ClimbSegment {
    private int startIndex;
    private int endIndex;
    private double totalAscent;
    private double length;
    private Trackpoint startPoint;
    private Trackpoint endPoint;

    /**
     * Constructor for ClimbSegment
     * @param startIndex starting trackpoint index
     * @param endIndex ending trackpoint index
     * @param totalAscent total elevation gain in meters
     * @param length total distance in meters
     * @param startPoint starting trackpoint
     * @param endPoint ending trackpoint
     */
    public ClimbSegment(int startIndex, int endIndex, double totalAscent, double length,
                        Trackpoint startPoint, Trackpoint endPoint) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.totalAscent = totalAscent;
        this.length = length;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    // Getter methods
    public int getStartIndex() { return startIndex; }
    public int getEndIndex() { return endIndex; }
    public double getTotalAscent() { return totalAscent; }
    public double getLength() { return length; }
    public Trackpoint getStartPoint() { return startPoint; }
    public Trackpoint getEndPoint() { return endPoint; }

    /**
     * Calculate grade percentage
     * @return grade percentage
     */
    public double getGrade() {
        if (length == 0) return 0;
        double ascentFeet = totalAscent * 3.28084; // Convert to feet
        double lengthFeet = length * 3.28084; // Convert to feet
        return (ascentFeet / lengthFeet) * 100;
    }

    /**
     * Calculate time duration in minutes
     * @param trackpoints array of all trackpoints
     * @return time in minutes
     */
    public double getTimeMinutes(Trackpoint[] trackpoints) {
        if (startIndex >= endIndex || endIndex >= trackpoints.length) return 0;

        Time startTime = trackpoints[startIndex].getTime();
        Time endTime = trackpoints[endIndex].getTime();

        int startSeconds = startTime.toSecondsSinceMidnight();
        int endSeconds = endTime.toSecondsSinceMidnight();

        // Handle midnight wraparound
        if (endSeconds < startSeconds) {
            endSeconds += 24 * 3600;
        }

        return (endSeconds - startSeconds) / 60.0;
    }

    /**
     * Calculate vertical ascent rate in meters/hour
     * @param trackpoints array of all trackpoints
     * @return ascent rate in meters per hour
     */
    public double getAscentRate(Trackpoint[] trackpoints) {
        double timeMinutes = getTimeMinutes(trackpoints);
        if (timeMinutes == 0) return 0;

        return (totalAscent / timeMinutes) * 60; // Convert to meters/hour
    }
}

/**
 * BigClimb main program for finding the biggest climb in a hike
 * Implements two heuristics for climb detection
 */
public class BigClimb {

    /**
     * Heuristic 1: Grade-based climb detection
     * Finds climbs based on percentage grade, ending if descent exceeds 5% of climb
     * @param trackpoints array of trackpoints
     * @return the biggest climb found
     */
    public static ClimbSegment findBiggestClimbGradeBased(Trackpoint[] trackpoints) {
        if (trackpoints.length < 2) {
            return new ClimbSegment(0, 0, 0, 0, trackpoints[0], trackpoints[0]);
        }

        ClimbSegment bestClimb = new ClimbSegment(0, 0, 0, 0, trackpoints[0], trackpoints[0]);

        for (int start = 0; start < trackpoints.length - 1; start++) {
            double totalAscent = 0;
            double totalLength = 0;
            double maxElevation = trackpoints[start].getElevation();

            for (int end = start + 1; end < trackpoints.length; end++) {
                double elevationChange = trackpoints[end].getElevation() - trackpoints[end-1].getElevation();
                double segmentLength = trackpoints[end-1].getCoordinate().distTo(trackpoints[end].getCoordinate());

                totalLength += segmentLength;

                // Track maximum elevation reached
                if (trackpoints[end].getElevation() > maxElevation) {
                    maxElevation = trackpoints[end].getElevation();
                }

                // Calculate total ascent (only positive elevation changes)
                if (elevationChange > 0) {
                    totalAscent += elevationChange;
                }

                // Calculate descent from peak
                double descentFromPeak = maxElevation - trackpoints[end].getElevation();
                double climbSoFar = maxElevation - trackpoints[start].getElevation();

                // End climb if descent exceeds 5% of total climb
                if (climbSoFar > 0 && descentFromPeak > (climbSoFar * 0.05)) {
                    break;
                }

                // Check if this is our best climb so far
                if (totalAscent > bestClimb.getTotalAscent()) {
                    bestClimb = new ClimbSegment(start, end, totalAscent, totalLength,
                            trackpoints[start], trackpoints[end]);
                }
            }
        }

        return bestClimb;
    }

    /**
     * Heuristic 2: Fixed threshold climb detection
     * Finds climbs that don't drop more than 100 feet (30.48 meters)
     * @param trackpoints array of trackpoints
     * @return the biggest climb found
     */
    public static ClimbSegment findBiggestClimbFixedThreshold(Trackpoint[] trackpoints) {
        if (trackpoints.length < 2) {
            return new ClimbSegment(0, 0, 0, 0, trackpoints[0], trackpoints[0]);
        }

        final double THRESHOLD_FEET = 100.0;
        final double THRESHOLD_METERS = THRESHOLD_FEET * 0.3048; // Convert feet to meters

        ClimbSegment bestClimb = new ClimbSegment(0, 0, 0, 0, trackpoints[0], trackpoints[0]);

        for (int start = 0; start < trackpoints.length - 1; start++) {
            double totalAscent = 0;
            double totalLength = 0;
            double cumulativeElevation = 0;
            double minElevationSeen = 0;

            for (int end = start + 1; end < trackpoints.length; end++) {
                double elevationChange = trackpoints[end].getElevation() - trackpoints[end-1].getElevation();
                double segmentLength = trackpoints[end-1].getCoordinate().distTo(trackpoints[end].getCoordinate());

                cumulativeElevation += elevationChange;
                totalLength += segmentLength;

                // Track minimum elevation to detect significant drops
                if (cumulativeElevation < minElevationSeen) {
                    minElevationSeen = cumulativeElevation;
                }

                // If we've dropped more than 100 feet threshold, stop this climb
                if (cumulativeElevation - minElevationSeen < -THRESHOLD_METERS) {
                    break;
                }

                // Calculate total ascent (only positive elevation changes)
                if (elevationChange > 0) {
                    totalAscent += elevationChange;
                }

                // Check if this is our best climb so far
                if (totalAscent > bestClimb.getTotalAscent()) {
                    bestClimb = new ClimbSegment(start, end, totalAscent, totalLength,
                            trackpoints[start], trackpoints[end]);
                }
            }
        }

        return bestClimb;
    }

    /**
     * Calculate cumulative distance to a specific trackpoint
     * @param trackpoints array of all trackpoints
     * @param index target trackpoint index
     * @return cumulative distance in meters
     */
    private static double calculateDistanceToPoint(Trackpoint[] trackpoints, int index) {
        double totalDistance = 0;
        for (int i = 1; i <= index; i++) {
            totalDistance += trackpoints[i-1].getCoordinate().distTo(trackpoints[i].getCoordinate());
        }
        return totalDistance;
    }

    /**
     * Print formatted climb information
     * @param climb the climb segment to print
     * @param trackpoints array of all trackpoints
     */
    private static void printClimbInfo(ClimbSegment climb, Trackpoint[] trackpoints) {
        double startMiles = calculateDistanceToPoint(trackpoints, climb.getStartIndex()) * 0.000621371;
        double endMiles = calculateDistanceToPoint(trackpoints, climb.getEndIndex()) * 0.000621371;

        Coordinate startCoord = climb.getStartPoint().getCoordinate();
        Coordinate endCoord = climb.getEndPoint().getCoordinate();

        double startElevationFeet = climb.getStartPoint().getElevation() * 3.28084;
        double endElevationFeet = climb.getEndPoint().getElevation() * 3.28084;
        double climbFeet = climb.getTotalAscent() * 3.28084;
        double lengthMiles = climb.getLength() * 0.000621371;

        System.out.printf("Start: mile %.2f (%.5fN %.5fW) @ %.0f feet%n",
                startMiles, startCoord.getLatitude(), Math.abs(startCoord.getLongitude()), startElevationFeet);
        System.out.printf("  End: mile %.2f (%.5fN %.5fW) @ %.0f feet%n",
                endMiles, endCoord.getLatitude(), Math.abs(endCoord.getLongitude()), endElevationFeet);
        System.out.printf("Climb = %.0f feet%n", climbFeet);
        System.out.printf("Length = %.1f miles%n", lengthMiles);
        System.out.printf("Grade = %.1f%%%n", climb.getGrade());
        System.out.printf("Time = %.1f minutes%n", climb.getTimeMinutes(trackpoints));
        System.out.printf("Rate = %.0f meters/hour%n", climb.getAscentRate(trackpoints));
        System.out.printf("Range = %d..%d%n", climb.getStartIndex(), climb.getEndIndex());
    }

    /**
     * Main method - entry point for the program
     * @param args command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Arguments received: " + java.util.Arrays.toString(args));

        int heuristic = 1;
        String filename = null;

        // Parse command line arguments
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-heuristic") && i + 1 < args.length) {
                heuristic = Integer.parseInt(args[i + 1]);
                i++;
            } else if (!args[i].startsWith("-")) {
                filename = args[i];
            }
        }

        if (filename == null) {
            System.err.println("Error: Filename must be provided");
            System.err.println("Usage: java BigClimb [-heuristic 1|2] <filename.csv>");
            System.exit(1);
        }

        try {
            System.out.println("Reading file: " + filename);
            List<Trackpoint> trackpointList = Trackpoint.readAllFromCSV(filename);
            Trackpoint[] trackpoints = trackpointList.toArray(new Trackpoint[0]);

            if (trackpoints.length < 2) {
                System.out.println("Need at least 2 trackpoints to find climbs.");
                System.exit(1);
            }

            System.out.println("Using heuristic: " + heuristic);
            ClimbSegment biggestClimb = null;

            if (heuristic == 1) {
                biggestClimb = findBiggestClimbGradeBased(trackpoints);
            } else if (heuristic == 2) {
                biggestClimb = findBiggestClimbFixedThreshold(trackpoints);
            } else {
                System.err.println("Unknown heuristic: " + heuristic);
                System.err.println("Available heuristics:");
                System.err.println("  1 - Grade-based (ends climb if descent exceeds 5% of total climb)");
                System.err.println("  2 - Fixed threshold (ends climb if descent exceeds 100 feet)");
                System.exit(1);
            }

            printClimbInfo(biggestClimb, trackpoints);

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
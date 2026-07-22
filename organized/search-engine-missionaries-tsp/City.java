import static java.lang.Math.sin;
import static java.lang.Math.cos;
import static java.lang.Math.acos;

public class City {
    private String name;       // City name
    private State state;       // State containing city
    private double latitude;   // Latitude in degrees
    private double longitude;  // Longitude in degrees

    // Constructor for city
    public City(String name, State state, double latitude, double longitude) {
        this.name = name;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Get city name
    public String name() { return this.name; }

    // Get state containing city
    public State state() { return this.state; }

    // Get latitude
    public double latitude() { return this.latitude; }

    // Get longitude
    public double longitude() { return this.longitude; }

    // Calculate distance to another city using great circle formula
    public double distanceTo(City to) {
        final double earthRadius = 6335.439; // Earth radius in kilometers
        double phi1 = Math.toRadians(this.latitude);
        double phi2 = Math.toRadians(to.latitude);
        double lambda1 = Math.toRadians(this.longitude);
        double lambda2 = Math.toRadians(to.longitude);
        double deltaLambda = lambda2 - lambda1;

        // Great circle distance formula
        double centralAngle = acos(sin(phi1) * sin(phi2) +
                cos(phi1) * cos(phi2) * cos(deltaLambda));

        return earthRadius * centralAngle;
    }

    // Alias for distanceTo for compatibility
    public double distance(City to) {
        return distanceTo(to);
    }

    @Override
    public String toString() {
        return this.name + ", " + this.state.code();
    }
}
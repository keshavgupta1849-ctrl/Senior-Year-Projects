package Plot;

import java.util.ArrayList;
import java.util.Scanner;

public class Length {

    public static final double MILES_PER_KILOMETER = 0.621371;

    public static ArrayList<City> readTour(Scanner scanner, Region[] regions) {
        ArrayList<City> tour = new ArrayList<>();
        while (scanner.hasNext()) {
            String item = scanner.next();
            Region region = Region.find(item, regions);
            if (region == null) {
                String kind = Region.kind(regions);
                throw new IllegalArgumentException(String.format("Invalid %s: %s", kind ,item));
            } else {
                tour.add(region.capital());
            }
        }
        return tour;
    }

    public static void help() {
        System.out.println();
        System.out.println("java Length <options> <list of cities on the tour>");
        System.out.println();
        System.out.println("    Checks a tour for validity and prints its length");
        System.out.println();
        System.out.println("    If a tour is not provided on the command line the");
        System.out.println("    tour is read from standard input which allows the");
        System.out.println("    tour produced by your code to be printed using a pipe:");
        System.out.println();
        System.out.println("        java TSP -canada | java Length -canada -miles");
        System.out.println();
        System.out.println("    Valid command line options are:");
        System.out.println();
        System.out.println("    -usa               Use the USA data set");
        System.out.println("    -us                   abbreviation for -usa");
        System.out.println("    -canada            Use the canada data set");
        System.out.println("    -can                  abbreviation for -can");
        System.out.println("    -central_america   Use the central america data set");
        System.out.println("    -central              abbreviation for -central_america");
        System.out.println("    -ca                   abbreviation for -central_americal");
        System.out.println("    -south_america     Use the south america data set");
        System.out.println("    -south                abbreviation for -south_america");
        System.out.println("    -sa                   abbreviation for -south_america");
        System.out.println("    -north_america     Use the north america data set");
        System.out.println("    -north                abbrevation for -north_america");
        System.out.println("    -na                   abbreviation for -north_america");
        System.out.println("    -caribbean         Use the caribbean data set");
        System.out.println("    -car                  abbreviation for -caribbean");
        System.out.println("    -europe            Use the europe data set");
        System.out.println("    -eu                   abbreviation for -europe");
        System.out.println("    -miles             Print distance in miles");
        System.out.println("    -kilometers        Print distance in kilometers");
        System.out.println("    -km                   abbreviation for -kilometers");
        System.out.println();
    }

    public static void main(String[] args) {
        ArrayList<City> tour = new ArrayList<>();
        Region[] regions = US.State.values();
        boolean miles = false;
        int count = 0;

        for (String arg : args) {
            switch (arg) {
                case "-us":
                case "-usa":
                    regions = US.State.values();
                    break;

                case "-can":
                case "-canada":
                    regions = Canada.Province.values();
                    break;

                case "-ca":
                case "-central":
                case "-central_america":
                    regions = CentralAmerica.Country.values();
                    break;

                case "-sa":
                case "-south":
                case "-south_america":
                    regions = SouthAmerica.Country.values();
                    break;

                case "-na":
                case "-north":
                case "-north_america":
                    regions = NorthAmerica.Country.values();
                    break;

                case "-car":
                case "-carribean":
                    regions = Caribbean.Country.values();
                    break;

                case "-eu":
                case "-europe":
                    regions = Europe.Country.values();
                    break;

                case "-miles":
                    miles = true;
                    break;

                case "-km":
                case "-kilometers":
                    miles = false;
                    break;

                case "-help":
                    help();
                    return;

                default:
                    if (arg.startsWith("-") || arg.startsWith("+")) {
                        System.err.println("Invalid option: " + arg);

                    } else {
                        Region region = Region.find(arg, regions);
                        if (region == null) {
                            String kind = Region.kind(regions);
                            System.err.println("Invalid " + kind + ": " + arg);
                        } else {
                            City city = region.capital();
                            if (tour.contains(city)) {
                                System.err.println("Duplicate region: " + arg);
                            } else {
                                tour.add(city);
                            }
                        }
                        count++;
                    }
                    break;
            }
        }

        if (count == 0) {
            tour = readTour(new Scanner(System.in), regions);
        }

        if (tour.size() < regions.length) {
            System.err.println("Tour is incomplete");
        }

        double distance = 0.0;
        if (tour.size() > 0) {
            City previous = tour.get(tour.size()-1);
            for (City current : tour) {
                distance += previous.distance(current);
                previous = current;
            }
        }

        if (miles) {
            distance *= MILES_PER_KILOMETER;
        }
        System.out.printf("%,.3f %s\n", distance, miles ? "mi" : "km");
    }
}


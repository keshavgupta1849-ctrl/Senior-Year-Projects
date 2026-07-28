package Plot;
// Puts this class into the Plot package so it can use Plot.Region / Plot.US / etc.
// You run it as: java Plot.TSP ...

import Plot.*;
// Imports everything in the Plot package (City, Region, US, Canada, Europe, Tour, etc.)

import java.util.*;
// Imports Java collections and Random, Arrays, Locale, etc.

public class TSP {
    // Main program class that generates tours.

    private enum Algo { SHUFFLE, NEAREST_NEIGHBOR, INSERT_NEAR, INSERT_FAR, INSERT_RANDOM }
    // Enum = a fixed set of algorithm choices the program supports.

    public static void main(String[] args) {
        // Program entry point. args[] contains command-line tokens.

        // Defaults
        Algo algo = null;
        // Which algorithm to use; starts null so we can error if user never provides one.

        int initialSize = 3;
        // For insertion methods: how many regions to start with if user didn't specify an initial tour.

        int count = 1;
        // Number of tours to generate (for shuffle or random-start NN or insertion).

        Long seed = null;
        // Seed for RNG. If null, RNG is non-deterministic.

        Region[] regions = US.State.values(); // default -usa
        // The dataset we’re using, represented as an array of Region.
        // Default dataset is USA states.

        List<String> positional = new ArrayList<>();
        // Holds non-flag arguments (like CA NY FL)

        for (int i = 0; i < args.length; i++) {
            // Iterate through all command-line arguments.

            String a = args[i];


            switch (a) {
                // Algorithm flags: choose which strategy we’ll use.
                case "-shuffle": algo = Algo.SHUFFLE; break;
                case "-nn":      algo = Algo.NEAREST_NEIGHBOR; break;
                case "-near":    algo = Algo.INSERT_NEAR; break;
                case "-far":     algo = Algo.INSERT_FAR; break;
                case "-random":  algo = Algo.INSERT_RANDOM; break;

                case "-size":
                    // -size expects a following integer argument
                    i = requireNext(args, i, "-size");

                    initialSize = parsePositiveInt(args[i], "-size");
                    // Parses the size and ensures it’s positive.
                    break;

                case "-count":
                    // -count expects a following integer argument
                    i = requireNext(args, i, "-count");
                    count = parsePositiveInt(args[i], "-count");
                    break;

                case "-seed":
                    // -seed expects a following integer (long)
                    i = requireNext(args, i, "-seed");
                    seed = parseLong(args[i], "-seed");
                    break;

                case "-usa":
                    // Switch dataset to USA states
                    regions = US.State.values();
                    break;
                case "-canada":
                    // Switch dataset to Canada provinces
                    regions = Canada.Province.values();
                    break;
                case "-europe":
                    // Switch dataset to Europe countries
                    regions = Europe.Country.values();
                    break;

                default:
                    // Anything that isn't recognized above:
                    if (a.startsWith("-")) {
                        // If it looks like a flag, it's an unknown option => error
                        usageAndExit("Unknown option: " + a, regions);
                    } else {
                        // Otherwise it's positional: a region code/name/capital name
                        positional.add(a);
                    }
            }
        }

        if (algo == null)
            usageAndExit("You must specify an algorithm option: -shuffle, -nn, -near, -far, or -random", regions);
        // If the user never picked an algorithm, tell them usage and exit.

        Random rng = (seed == null) ? new Random() : new Random(seed);
        // Create a RNG.

        // Build starting lists from positional args
        List<Region> specified = parseRegions(positional, regions);

        if (!positional.isEmpty() && specified.size() != positional.size()) {
            usageAndExit("Could not parse all region codes/names.", regions);
        }

        // Generate tours according to the chosen algorithm and rules in the prompt
        List<Tour> tours = new ArrayList<>();
        // We may produce multiple tours (count > 1, or multiple NN starts).

        switch (algo) {
            case SHUFFLE:
                // SHUFFLE: make "count" tours, each is a random permutation
                for (int t = 0; t < count; t++) {
                    tours.add(makeShuffleTour(regions, rng));
                }
                break;

            case NEAREST_NEIGHBOR:
                // Nearest Neighbor can either:
                // - generate one tour per specified start city
                // - or generate "count" tours from random start cities
                if (!specified.isEmpty()) {
                    // One tour per specified start city
                    for (Region start : specified) {
                        tours.add(makeNearestNeighborTour(regions, start));
                    }
                } else {
                    // If no explicit starts, pick random starts
                    for (int t = 0; t < count; t++) {
                        Region start = regions[rng.nextInt(regions.length)];
                        tours.add(makeNearestNeighborTour(regions, start));
                    }
                }
                break;

            case INSERT_NEAR:
            case INSERT_FAR:
            case INSERT_RANDOM:
                // Insertion methods all share the same structure:
                // - Choose which remaining region to insert next (near/far/random)
                // - Insert it into the tour using cheapest insertion (handled by Tour)
                if (!specified.isEmpty()) {
                    // If user provided an initial tour as positional args, use that as the seed.
                    tours.add(makeInsertionTour(regions, specified, algo, rng));
                } else {
                    // Otherwise, generate "count" tours by choosing a random initial seed tour
                    for (int t = 0; t < count; t++) {
                        List<Region> initial = pickDistinct(regions, initialSize, rng);
                        tours.add(makeInsertionTour(regions, initial, algo, rng));
                    }
                }
                break;
        }

        // Print tours
        for (Tour tour : tours) {
            System.out.println(formatTourLine(tour));
        }
        // Each printed line is: codes... then total length.
    }

    //Algorithms

    private static Tour makeShuffleTour(Region[] regions, Random rng) {
        // SHUFFLE algorithm:
        // 1) copy all regions into a list
        // 2) Knuth shuffle them uniformly at random
        // 3) return as a Tour

        List<Region> perm = new ArrayList<>(Arrays.asList(regions.clone()));
        // clone() prevents modifying the original array

        knuthShuffle(perm, rng);
        // randomizes the order in-place

        return new Tour(perm);
        // build the tour in that order
    }

    private static Tour makeNearestNeighborTour(Region[] regions, Region start) {
        // NEAREST NEIGHBOR (NN) heuristic:
        // Start at "start".
        // Repeatedly go to the closest unvisited region (closest capital-to-capital distance).

        Set<Region> unvisited = new HashSet<>(Arrays.asList(regions));
        // Put every region in a set so we can remove visited ones quickly.

        unvisited.remove(start);
        // Mark start as visited.

        List<Region> order = new ArrayList<>();
        // We'll build the tour order list.

        order.add(start);
        // First stop is start.

        Region current = start;
        // Track current location.

        while (!unvisited.isEmpty()) {
            // Keep choosing next until all regions are visited.

            Region next = null;
            // Best candidate we found so far.

            double best = Double.POSITIVE_INFINITY;
            // Best (smallest) distance so far.

            City c1 = current.capital();
            // Get the capital city of current region once (avoid repeated calls).

            for (Region candidate : unvisited) {
                // Check every unvisited region and find the nearest.
                double d = c1.distance(candidate.capital());
                // Distance from current capital to candidate capital.

                if (d < best) {
                    // If this candidate is closer, update best.
                    best = d;
                    next = candidate;
                }
            }

            // Safety: should never be null if unvisited not empty
            if (next == null) break;
            // Defensive programming: if somehow we didn't find one, stop.

            order.add(next);
            // Append chosen next region to tour order.

            unvisited.remove(next);
            // Mark it visited.

            current = next;
            // Move current forward.
        }

        return new Tour(order);
        // Return the constructed tour.
        // Note: Tour.lengthKm() treats it as a cycle (last->first edge included).
    }

    private static Tour makeInsertionTour(Region[] allRegions, List<Region> initialTour, Algo algo, Random rng) {
        // INSERTION heuristic framework:
        // Start with an initial cycle (size >= 3).
        // Maintain a set of remaining regions not yet in the tour.
        // Repeat until none remain:
        //   - choose WHICH region to insert next (near / far / random)
        //   - insert it at the cheapest position in the cycle (Tour.insertCheapest)

        if (initialTour.size() < 3) {
            usageAndExit("Insertion methods require an initial tour of size >= 3.", allRegions);
        }
        // Insertion needs at least a triangle to be a meaningful cycle.

        // Validate distinct and within dataset
        Set<Region> initialSet = new HashSet<>(initialTour);
        // Use a set to check duplicates.

        if (initialSet.size() != initialTour.size()) {
            // If sizes differ, duplicates exist.
            usageAndExit("Initial tour contains duplicates. Provide distinct regions.", allRegions);
        }

        Set<Region> remaining = new HashSet<>(Arrays.asList(allRegions));
        // Remaining regions initially includes everything.

        for (Region r : initialTour) {
            // Remove initial regions from remaining.
            if (!remaining.contains(r))
                usageAndExit("Initial region not in selected dataset: " + r.code(), allRegions);
            // Error if user gave a region not in the chosen dataset.

            remaining.remove(r);
            // Remove from remaining set.
        }

        Tour tour = new Tour(initialTour);
        // Build the current tour cycle with the initial seed order.

        while (!remaining.isEmpty()) {
            // Continue inserting until tour contains all regions.

            Region chosen;
            // The next region we will insert into the tour.

            switch (algo) {
                case INSERT_NEAR:
                    // Choose the region that is closest to the existing tour (by min distance to any tour node)
                    chosen = chooseNearestToTour(tour, remaining);
                    break;

                case INSERT_FAR:
                    // Choose the region that is farthest from the existing tour
                    chosen = chooseFarthestFromTour(tour, remaining);
                    break;

                case INSERT_RANDOM:
                    // Choose a random remaining region
                    chosen = chooseRandom(remaining, rng);
                    break;

                default:
                    // Should never happen because this method is only called for insertion algos.
                    throw new IllegalStateException("Unexpected algo for insertion: " + algo);
            }

            tour.insertCheapest(chosen);
            // CHEAPEST INSERTION step:
            // Given a chosen region, insert it into the cycle in the position that
            // increases total length the least.

            remaining.remove(chosen);
            // Mark chosen as inserted.
        }

        return tour;
        // Finished: tour contains all regions.
    }

    // Selection helpers

    private static Region chooseNearestToTour(Tour tour, Set<Region> remaining) {
        // Choose the remaining region with minimal distance to the tour.
        // "distance to tour" = minimum distance from that region to any region currently in the tour.

        Region bestRegion = null;
        double bestDist = Double.POSITIVE_INFINITY;

        for (Region r : remaining) {
            double d = distanceToTourMin(tour, r);
            // Compute min distance to any node in the tour.

            if (d < bestDist) {
                bestDist = d;
                bestRegion = r;
            }
        }
        return bestRegion;
        // Returns the closest remaining region to the tour.
    }

    private static Region chooseFarthestFromTour(Tour tour, Set<Region> remaining) {
        // Choose the remaining region with maximal distance to the tour
        // using the same "min distance to any tour node" metric.

        Region bestRegion = null;
        double bestDist = Double.NEGATIVE_INFINITY;

        for (Region r : remaining) {
            double d = distanceToTourMin(tour, r);

            if (d > bestDist) {
                bestDist = d;
                bestRegion = r;
            }
        }
        return bestRegion;
        // Returns the farthest remaining region from the tour.
    }

    // Distance from region r to the current tour = min distance from r to any city in the tour.
    private static double distanceToTourMin(Tour tour, Region r) {
        // Compute: min_{inTour} dist(capital(r), capital(inTour))

        double best = Double.POSITIVE_INFINITY;

        City c = r.capital();
        // Capital city of candidate region r.

        for (Region inTour : tour.asList()) {
            // Loop through all regions already in the tour.
            double d = c.distance(inTour.capital());
            // Distance from r's capital to that tour region's capital.

            if (d < best) best = d;
            // Track the minimum.
        }
        return best;
        // This "best" distance is how close r is to the tour.
    }

    private static Region chooseRandom(Set<Region> remaining, Random rng) {
        // Choose a random element from a Set.
        // Sets don't support O(1) indexing, so we walk until we hit the random index.

        int k = rng.nextInt(remaining.size());
        // Choose random index 0..size-1.

        int i = 0;
        for (Region r : remaining) {
            // Iterate through set elements.
            if (i == k) return r;
            i++;
        }
        // Should never happen, but if it does:
        return remaining.iterator().next();
    }

    // Utility

    private static void knuthShuffle(List<Region> list, Random rng) {
        // Knuth shuffle:
        // For i from end down to 1:
        //   pick random j in [0..i]
        //   swap list[i] and list[j]
        // This produces a uniform random permutation.

        for (int i = list.size() - 1; i >= 1; i--) {
            int j = rng.nextInt(i + 1);
            // random index among 0..i inclusive

            Region tmp = list.get(i);
            // save list[i]

            list.set(i, list.get(j));
            // move list[j] into i

            list.set(j, tmp);
            // move saved element into j
        }
    }

    private static List<Region> pickDistinct(Region[] regions, int k, Random rng) {
        // Pick k distinct regions uniformly at random by shuffling and taking first k.

        if (k > regions.length)
            usageAndExit("-size cannot exceed number of regions in the dataset.", regions);

        List<Region> list = new ArrayList<>(Arrays.asList(regions.clone()));
        // Copy the region array to a mutable list.

        knuthShuffle(list, rng);
        // Shuffle so first k are random.

        return new ArrayList<>(list.subList(0, k));
        // Take first k as the initial seed tour.
    }

    private static List<Region> parseRegions(List<String> tokens, Region[] regions) {
        // Convert command-line tokens (codes/names/capitals) into Region objects.
        List<Region> out = new ArrayList<>();

        for (String tok : tokens) {
            Region r = Region.find(tok, regions);
            // Try to match token to a region.

            if (r == null) {
                // If not found, error out with  message.
                usageAndExit("Unknown " + Region.kind(regions) + " code/name/capital: " + tok, regions);
            }
            out.add(r);
        }
        return out;
    }

    private static String formatTourLine(Tour tour) {
        // Convert a Tour into the required output format:
        // codes separated by spaces, then the total length.

        StringBuilder sb = new StringBuilder();
        boolean first = true;

        for (Region r : tour.asList()) {
            if (!first) sb.append(' ');
            // Add a space between codes.

            sb.append(r.code());
            // Append the region code (e.g., CA, BC, FR).

            first = false;
        }

        // Length in km (cycle)
        sb.append(' ');
        // Separator before length

        sb.append(String.format(Locale.US, "%.2f", tour.lengthKm()));
        // Append length with 2 decimals in a consistent decimal format.

        return sb.toString();
    }

    private static int requireNext(String[] args, int i, String opt) {
        // Utility: ensure there's another token after a flag like "-size".
        if (i + 1 >= args.length) usageAndExit("Missing value after " + opt, US.State.values());
        return i + 1;
    }

    private static int parsePositiveInt(String s, String opt) {
        // Parse an int and ensure it is > 0.
        try {
            int v = Integer.parseInt(s);
            if (v <= 0) throw new NumberFormatException();
            return v;
        } catch (NumberFormatException e) {
            usageAndExit("Expected a positive integer for " + opt + " but got: " + s, US.State.values());
            return 1; // unreachable because usageAndExit exits
        }
    }

    private static long parseLong(String s, String opt) {
        // Parse a long.
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            usageAndExit("Expected an integer for " + opt + " but got: " + s, US.State.values());
            return 1L; // unreachable
        }
    }

    private static void usageAndExit(String msg, Region[] regions) {
        // Print an error plus help text, then quit program with nonzero exit code.

        System.err.println("Error: " + msg);
        System.err.println();

        System.err.println("Usage examples:");
        System.err.println("  java TSP -shuffle -usa -count 5");
        System.err.println("  java TSP -nn -usa CA NY FL");
        System.err.println("  java TSP -nn -canada -count 8 -seed 42");
        System.err.println("  java TSP -far -usa CA WA OR");
        System.err.println("  java TSP -near -europe -size 3 -count 10");
        System.err.println();

        System.err.println("Options:");
        System.err.println("  -shuffle | -nn | -near | -far | -random");
        System.err.println("  -size N   (insertion only; default 3)");
        System.err.println("  -count N  (default 1)");
        System.err.println("  -seed S");
        System.err.println("  -usa | -canada | -europe (default -usa)");
        System.err.println();

        System.err.println("Positional args: region codes/names/capital names (e.g., CA, Ontario, Paris).");

        System.exit(2);
        // Exit with code 2 indicating incorrect usage / error.
    }
}
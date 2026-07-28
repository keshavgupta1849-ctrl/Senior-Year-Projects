package Plot;
// Put this class in the Plot package so it can use Plot.Region and the dataset classes.

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
// Collections/List/ArrayList are for storing tours and swapping elements.
// Random is used for the RANDOM improvement variant and reproducibility.

public class Improve {
    public static boolean improveOnce(
            List<Region> tour,
            Variant variant,
            Random rng,
            double probability
    ) {
        Move m = findMove(tour, null, variant, rng, probability);
        if (m == null) return false;
        apply2Opt(tour, m.i, m.k);
        return m.delta < 0;
    }
    // Program that reads a tour, improves it using 2-opt, and prints the improved tour.

    private enum Variant { BEST, FIRST, RANDOM }
    // Which hill-climbing behavior to use:
    // BEST  = pick the best improving 2-opt move each iteration
    // FIRST = pick the first improving move you find
    // RANDOM= pick a random improving move

    public static void main(String[] args) throws IOException {
        Variant variant = Variant.BEST;
        // Default improvement behavior is BEST.
        double negativeMoveProbability = 0.0;
        // Only used with RANDOM: probability of accepting a non-improving (worse/equal) move.

        long maxMoves = (long) Integer.MAX_VALUE;
        // Maximum number of 2-opt moves to apply before stopping.
        // Default is effectively "no limit".

        Long seed = null;
        // RNG seed for reproducibility; null means "random each run".

        // Dataset selection
        String dataset = null;
        // Which dataset flag was provided (-usa, -canada, etc.)

        // Parse args
        for (int i = 0; i < args.length; i++) {
            // Walk through all command-line tokens.

            String a = args[i];
            // Current token.

            switch (a) {
                case "-best":
                    variant = Variant.BEST;
                    // Choose BEST mode.
                    break;

                case "-first":
                    variant = Variant.FIRST;
                    // Choose FIRST mode.
                    break;

                case "-random":
                    variant = Variant.RANDOM;
                    // Choose RANDOM mode.
                    break;

                case "-probability":
                    // -probability expects a number after it
                    if (i + 1 >= args.length) {
                        die("Missing value after -probability");
                    }
                    negativeMoveProbability = Double.parseDouble(args[++i]);
                    // Read the next token as a double and advance i.

                    if (negativeMoveProbability < 0.0 || negativeMoveProbability > 1.0) {
                        die("-probability must be between 0.0 and 1.0");
                    }
                    // Validate probability bounds.
                    break;

                case "-count":
                    // -count expects a number after it
                    if (i + 1 >= args.length) {
                        die("Missing value after -count");
                    }
                    maxMoves = Long.parseLong(args[++i]);
                    // Read the next token as maxMoves.

                    if (maxMoves < 0) {
                        die("-count must be nonnegative");
                    }
                    break;

                case "-seed":
                    // -seed expects a number after it
                    if (i + 1 >= args.length) {
                        die("Missing value after -seed");
                    }
                    seed = Long.parseLong(args[++i]);
                    // Store RNG seed.
                    break;

                // Data sets
                case "-usa":
                case "-california":
                case "-bay":
                case "-canada":
                case "-caribbean":
                case "-central":
                case "-south":
                case "-europe":
                    dataset = a;
                    // Record the dataset flag.
                    break;

                default:
                    // Any unknown option is an error.
                    die("Unknown option: " + a);
            }
        }

        if (dataset == null) {
            die("You must specify a data set (e.g., -usa, -canada, -europe, ...)");
        }
        // Must choose a dataset so we can interpret region codes and compute distances.

        Region[] regions = loadRegions(dataset);
        // Load the Region enum constants for the dataset (via reflection).

        if (regions == null || regions.length == 0) {
            die("Could not load regions for dataset: " + dataset);
        }

        Random rng = (seed == null) ? new Random() : new Random(seed);
        // Create RNG: seeded => reproducible, unseeded => varies per run.

        // Read the initial tour from stdin (one line, but we accept any whitespace)
        List<String> tokens = readAllTokensFromStdin();
        // Read every whitespace-separated token from standard input.

        if (tokens.isEmpty()) {
            die("No tour provided on standard input.");
        }
        // If nothing was piped in, we can't improve anything.

        // Map tokens to Region objects, ignoring a trailing length if present
        List<Region> tour = parseTourTokens(tokens, regions);


        if (tour.size() < 4) {
            // 2-opt needs at least 4 nodes to do anything interesting
            printTour(tour);
            return;
        }
        // With <4, there's basically no meaningful 2-opt swap.

        // Improve using repeated 2-opt
        long moves = 0;
        // Counts how many moves we've applied so far.

        while (moves < maxMoves) {
            // Repeat until we hit move limit or can't find a move we want.

            Move move = findMove(tour, regions, variant, rng, negativeMoveProbability);
            // Find which 2-opt move to apply based on BEST/FIRST/RANDOM.

            if (move == null) {
                break; // no move chosen
            }
            // If no acceptable move exists, we stop.

            // If the chosen move is non-improving and probability==0, we would never choose it.
            // When probability>0 in RANDOM mode, we may occasionally accept non-improving moves.
            apply2Opt(tour, move.i, move.k);
            // Apply the 2-opt reversal in-place to modify the tour.
            moves++;
            // Count the move.
            // For BEST/FIRST: if we just applied a non-improving move (shouldn't happen), stop.
            if (variant != Variant.RANDOM && move.delta >= 0) {
                break;
            }
            // BEST/FIRST should only accept improving moves (negative delta),
            // For RANDOM: continue; stopping condition handled by findMove when no improving moves
        }

        printTour(tour);
        // Output the final improved tour as codes only (no length).
    }

    private static final class Move {
        // Represents a candidate 2-opt move:
        final int i;
        final int k;
        final double delta; // newLength - oldLength

        Move(int i, int k, double delta) {
            this.i = i;
            this.k = k;
            this.delta = delta;
        }
    }

    private static Move findMove(List<Region> tour, Region[] regions, Variant variant, Random rng, double negativeMoveProbability) {
        int n = tour.size();
        // Number of nodes in the tour (cycle).

        Move best = null;
        // Stores the best improving move found so far (for BEST variant).

        List<Move> improving = (variant == Variant.RANDOM) ? new ArrayList<>() : null;
        // For RANDOM variant we collect all improving moves so we can pick one at random.

        List<Move> nonImproving = (variant == Variant.RANDOM && negativeMoveProbability > 0.0)
                ? new ArrayList<>() : null;
        // If RANDOM and probability>0, we also collect non-improving moves
        // (delta >= 0) so we can sometimes accept them.

        // Standard 2-opt enumeration for a cycle:
        // pick edges (i,i+1) and (k,k+1), reverse segment (i+1..k)
        for (int i = 0; i < n; i++) {
            // Choose the first edge starting index i.

            int i2 = (i + 1) % n;
            // i2 is i+1 wrapped around because tour is cyclic.

            for (int k = i + 2; k < n; k++) {
                // Choose the second edge starting index k start at i+2 so we don't pick adjacent edges (which would be invalid / no-op).

                int k2 = (k + 1) % n;
                // k2 is k+1 wrapped around.

                // Skip adjacent edges and the move that just reverses the entire tour
                if (i == 0 && k == n - 1) continue;
                // This specific case corresponds to swapping the "first" and "last" edges, which would reverse the whole tour (same cycle, no real change).

                Region a = tour.get(i);
                Region b = tour.get(i2);
                Region c = tour.get(k);
                Region d = tour.get(k2);
                // These are the endpoints of the two edges we are "cutting":
                // old edges: (a-b) and (c-d)

                double oldCost = dist(a, b) + dist(c, d);
                // Current cost of those two edges.

                double newCost = dist(a, c) + dist(b, d);
                // Cost after doing 2-opt:
                // remove (a-b) and (c-d)
                // add (a-c) and (b-d)
                // and reverse the middle segment to keep a valid tour.

                double delta = newCost - oldCost;
                // delta < 0 means improvement (tour gets shorter).

                if (variant == Variant.FIRST) {
                    // FIRST: return as soon as we find ANY improving move
                    if (delta < -1e-12) {
                        return new Move(i, k, delta);
                    }
                } else if (variant == Variant.BEST) {
                    // BEST: scan all moves and keep the best (most negative delta)
                    if (delta < -1e-12 && (best == null || delta < best.delta)) {
                        best = new Move(i, k, delta);
                    }
                } else {
                    // RANDOM: collect improving moves (and optionally non-improving)
                    if (delta < -1e-12) {
                        improving.add(new Move(i, k, delta));
                    } else if (nonImproving != null) {
                        nonImproving.add(new Move(i, k, delta));
                    }
                }
            }
        }

        if (variant == Variant.BEST) {
            return best; // null => no improvement
        }
        // BEST returns the best improvement found (or null if none exist).

        if (variant == Variant.RANDOM) {
            if (!improving.isEmpty()) {
                // If there are improving moves, we will normally pick a random improving move.

                // With probability p, take a random non-improving move (if available).
                if (negativeMoveProbability > 0.0 && nonImproving != null && !nonImproving.isEmpty()) {
                    if (rng.nextDouble() < negativeMoveProbability) {
                        return nonImproving.get(rng.nextInt(nonImproving.size()));
                    }
                }
                // Otherwise pick a random improving move:
                return improving.get(rng.nextInt(improving.size()));
            }

            // No improving moves. If probability>0, allow an occasional non-improving move;
            // otherwise we stop.
            if (negativeMoveProbability > 0.0 && nonImproving != null && !nonImproving.isEmpty()) {
                if (rng.nextDouble() < negativeMoveProbability) {
                    return nonImproving.get(rng.nextInt(nonImproving.size()));
                }
            }
            return null;
            // No improving move and we didn't accept a worse move => stop.
        }

        // FIRST: would have returned above if found any
        return null;
        // If FIRST found no improving move, stop.
    }

    private static void apply2Opt(List<Region> tour, int i, int k) {
        // reversing the middle segment changes the connectivity of the tour
        // to match the new edges (a-c) and (b-d).

        int start = i + 1;
        // First index inside the segment to reverse.

        int end = k;
        // Last index in the segment to reverse.

        while (start < end) {
            // Swap ends and move inward (classic in-place reverse).
            Collections.swap(tour, start, end);
            start++;
            end--;
        }
    }

    private static double dist(Region a, Region b) {
        // Compute distance between two regions as distance between their capitals.
        return a.capital().distance(b.capital());
    }

    // -------------------- IO / parsing --------------------

    private static List<String> readAllTokensFromStdin() throws IOException {
        // Reads all lines from stdin, splits by whitespace, and returns tokens.

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        // BufferedReader reads text efficiently from System.in.

        List<String> tokens = new ArrayList<>();
        // Accumulates tokens.

        String line;
        while ((line = br.readLine()) != null) {
            // Read until EOF.

            line = line.trim();
            // Remove leading/trailing whitespace.

            if (line.isEmpty()) continue;
            // Skip blank lines.

            String[] parts = line.split("\\s+");
            // Split on one-or-more whitespace.

            for (String p : parts) {
                if (!p.isEmpty()) tokens.add(p);
                // Add each token.
            }
        }
        return tokens;
    }

    private static List<Region> parseTourTokens(List<String> tokens, Region[] regions) {
        // Convert tokens into Region objects.
        // Also ignores numbers (like the length from checkpoint #2).

        List<Region> tour = new ArrayList<>();

        for (String tok : tokens) {
            // Strip commas or trailing punctuation if someone copy/pasted
            String t = tok.trim();
            while (!t.isEmpty() && (t.endsWith(",") || t.endsWith(";"))) {
                t = t.substring(0, t.length() - 1);
            }
            if (t.isEmpty()) continue;

            // Ignore a numeric token (often the length from checkpoint #2)
            if (isNumeric(t)) {
                continue;
            }

            Region r = Region.find(t, regions);
            // Try to map token to a region (code/name/capital).

            if (r == null) {
                die("Unknown region code/name in tour: '" + t + "'");
            }
            tour.add(r);
        }

        // Optional: basic duplicate check (helps catch bad input)
        // We won't die on duplicates here, but it's generally not a valid tour.
        return tour;
    }

    private static boolean isNumeric(String s) {
        // Returns true if s can be parsed as a double (e.g., "123", "123.45").
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static void printTour(List<Region> tour) {
        // Print codes separated by spaces, no length.
        // This matches what Plot expects on stdin.

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < tour.size(); i++) {
            if (i > 0) sb.append(' ');
            sb.append(tour.get(i).code());
        }
        System.out.println(sb.toString());
    }

    //  dataset loading

    private static Region[] loadRegions(String datasetFlag) {
        // Map a dataset flag to the correct nested enum class name.
        // Example: -canada -> "Canada$Province"
        // Nested enums compile to Outer$Inner in bytecode.

        final String enumClassName;
        switch (datasetFlag) {
            case "-usa":
                enumClassName = "US$State";
                break;
            case "-california":
                enumClassName = "California$County";
                break;
            case "-bay":
                enumClassName = "BayArea$County";
                break;
            case "-canada":
                enumClassName = "Canada$Province";
                break;
            case "-caribbean":
                enumClassName = "Caribbean$Country";
                break;
            case "-central":
                enumClassName = "CentralAmerica$Country";
                break;
            case "-south":
                enumClassName = "SouthAmerica$Country";
                break;
            case "-europe":
                enumClassName = "Europe$Country";
                break;
            default:
                return null;
        }

        try {
            Class<?> enumCls = Class.forName("Plot." + enumClassName);
            // Reflection: load the enum class by its full name (package + class).

            Object[] constants = enumCls.getEnumConstants();
            // Get all enum values (e.g., all provinces/countries/states).

            if (constants == null) return null;

            Region[] out = new Region[constants.length];
            // Convert Object[] to Region[].

            for (int i = 0; i < constants.length; i++) {
                out[i] = (Region) constants[i];
                // Each enum constant implements Region.
            }
            return out;
        } catch (ClassNotFoundException e) {
            die("Dataset class not found: " + enumClassName + " (did you compile the dataset .java files?)");
            return null;
        } catch (ClassCastException e) {
            die("Dataset enum does not implement Region: " + enumClassName);
            return null;
        }
    }

    private static void die(String msg) {
        // Print an error message and exit.
        System.err.println(msg);
        System.exit(1);
    }
}
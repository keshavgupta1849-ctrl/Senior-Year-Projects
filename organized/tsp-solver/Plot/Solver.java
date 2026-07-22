package Plot;

import java.util.*;

/**
 * Solver:
 * Full TSP solver using initial tour generation + 2-opt hill climbing,
 * with support for random restart and chained local search.
 *
 * Output: ONE line of region codes ONLY (no length).
 */
public class Solver {

    // Initial tour generation algorithms
    private enum InitAlgo { SHUFFLE, NN, INSERT_NEAR, INSERT_FAR, INSERT_RANDOM }

    // 2-opt variants
    private enum Variant { BEST, FIRST, RANDOM }

    // Represents a 2-opt move
    private static final class Move {
        final int i, k;
        final double delta;
        Move(int i, int k, double delta) {
            this.i = i;
            this.k = k;
            this.delta = delta;
        }
    }

    public static void main(String[] args) {
        //Defaults
        InitAlgo initAlgo = InitAlgo.INSERT_FAR;
        Variant variant = Variant.BEST;

        int initialSize = 3;
        long maxMoves = Long.MAX_VALUE;
        double probability = 0.0;
        Long seed = null;

        boolean restart = false;
        boolean chain = false;

        String datasetFlag = null;
        List<String> positional = new ArrayList<>();

        //Parse args
        for (int i = 0; i < args.length; i++) {
            String a = args[i];
            switch (a) {
                case "-restart": restart = true; break;
                case "-chain": chain = true; break;

                case "-best": variant = Variant.BEST; break;
                case "-first": variant = Variant.FIRST; break;
                case "-random": variant = Variant.RANDOM; initAlgo = InitAlgo.INSERT_RANDOM; break;

                case "-shuffle": initAlgo = InitAlgo.SHUFFLE; break;
                case "-nn": initAlgo = InitAlgo.NN; break;
                case "-near": initAlgo = InitAlgo.INSERT_NEAR; break;
                case "-far": initAlgo = InitAlgo.INSERT_FAR; break;

                case "-size":
                    initialSize = Integer.parseInt(args[++i]);
                    break;

                case "-count":
                    maxMoves = Long.parseLong(args[++i]);
                    break;

                case "-probability":
                    probability = Double.parseDouble(args[++i]);
                    break;

                case "-seed":
                    seed = Long.parseLong(args[++i]);
                    break;

                case "-usa":
                case "-california":
                case "-bay":
                case "-canada":
                case "-caribbean":
                case "-central":
                case "-south":
                case "-europe":
                case "-africa":
                    datasetFlag = a;
                    break;

                default:
                    if (a.startsWith("-")) die("Unknown option: " + a);
                    positional.add(a);
            }
        }

        if (datasetFlag == null) die("Must specify dataset");

        if (restart && chain) restart = false; // prefer chain

        Region[] regions = loadRegions(datasetFlag);
        Random rng = (seed == null) ? new Random() : new Random(seed);

        List<Region> specified = parseRegions(positional, regions);

        // Main search
        long usedMoves = 0;

        List<Region> bestTour = null;
        double bestLen = Double.POSITIVE_INFINITY;

        List<Region> current = generateInitialTour(regions, initAlgo, initialSize, specified, rng);

        while (usedMoves < maxMoves) {
            long used = improve2Opt(current, variant, rng, probability, maxMoves - usedMoves);
            usedMoves += used;

            double len = lengthKm(current);
            if (len < bestLen) {
                bestLen = len;
                bestTour = new ArrayList<>(current);
            }

            if (used == 0) {
                if (restart) {
                    current = generateInitialTour(regions, initAlgo, initialSize, specified, rng);
                } else if (chain) {
                    usedMoves += perturb(current, rng, 5);
                } else {
                    break;
                }
            }
        }

        if (bestTour == null) bestTour = current;
        printCodesOnly(bestTour);
    }

    // Initial Tour Generation

    private static List<Region> generateInitialTour(
            Region[] regions, InitAlgo algo, int size,
            List<Region> specified, Random rng
    ) {
        switch (algo) {
            case SHUFFLE:
                List<Region> list = new ArrayList<>(Arrays.asList(regions.clone()));
                Collections.shuffle(list, rng);
                return list;

            case NN:
                Region start = specified.isEmpty()
                        ? regions[rng.nextInt(regions.length)]
                        : specified.get(0);
                return nearestNeighbor(regions, start);

            case INSERT_NEAR:
            case INSERT_FAR:
            case INSERT_RANDOM:
                List<Region> seed = specified.isEmpty()
                        ? pickDistinct(regions, size, rng)
                        : new ArrayList<>(specified);
                return insertion(regions, seed, algo, rng);

            default:
                throw new IllegalStateException();
        }
    }

    private static List<Region> nearestNeighbor(Region[] regions, Region start) {
        Set<Region> unvisited = new HashSet<>(Arrays.asList(regions));
        List<Region> tour = new ArrayList<>();
        Region cur = start;
        tour.add(cur);
        unvisited.remove(cur);

        while (!unvisited.isEmpty()) {
            Region next = null;
            double best = Double.POSITIVE_INFINITY;
            for (Region r : unvisited) {
                double d = cur.capital().distance(r.capital());
                if (d < best) { best = d; next = r; }
            }
            tour.add(next);
            unvisited.remove(next);
            cur = next;
        }
        return tour;
    }

    private static List<Region> insertion(
            Region[] all, List<Region> seed, InitAlgo algo, Random rng
    ) {
        List<Region> tour = new ArrayList<>(seed);
        Set<Region> remaining = new HashSet<>(Arrays.asList(all));
        remaining.removeAll(seed);

        while (!remaining.isEmpty()) {
            Region chosen;
            if (algo == InitAlgo.INSERT_NEAR) chosen = nearestToTour(tour, remaining);
            else if (algo == InitAlgo.INSERT_FAR) chosen = farthestFromTour(tour, remaining);
            else chosen = randomFrom(remaining, rng);

            insertCheapest(tour, chosen);
            remaining.remove(chosen);
        }
        return tour;
    }

    //2-opt Improvement

    private static long improve2Opt(
            List<Region> tour, Variant variant,
            Random rng, double prob, long budget
    ) {
        long used = 0;
        while (used < budget) {
            Move m = findMove(tour, variant, rng, prob);
            if (m == null) break;
            apply2Opt(tour, m.i, m.k);
            used++;
            if (variant != Variant.RANDOM && m.delta >= 0) break;
        }
        return used;
    }

    private static Move findMove(
            List<Region> tour, Variant variant,
            Random rng, double prob
    ) {
        int n = tour.size();
        Move best = null;
        List<Move> improving = new ArrayList<>();
        List<Move> nonImproving = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            int i2 = (i + 1) % n;
            for (int k = i + 2; k < n; k++) {
                if (i == 0 && k == n - 1) continue;
                int k2 = (k + 1) % n;

                Region a = tour.get(i);
                Region b = tour.get(i2);
                Region c = tour.get(k);
                Region d = tour.get(k2);

                double oldCost = dist(a, b) + dist(c, d);
                double newCost = dist(a, c) + dist(b, d);
                double delta = newCost - oldCost;

                if (delta < -1e-9) {
                    Move m = new Move(i, k, delta);
                    if (variant == Variant.FIRST) return m;
                    if (variant == Variant.BEST) {
                        if (best == null || delta < best.delta) best = m;
                    }
                    improving.add(m);
                } else {
                    nonImproving.add(new Move(i, k, delta));
                }
            }
        }

        if (variant == Variant.BEST) return best;
        if (variant == Variant.RANDOM) {
            if (!improving.isEmpty()) {
                if (prob > 0 && rng.nextDouble() < prob)
                    return nonImproving.get(rng.nextInt(nonImproving.size()));
                return improving.get(rng.nextInt(improving.size()));
            }
        }
        return null;
    }

    private static void apply2Opt(List<Region> tour, int i, int k) {
        int a = i + 1, b = k;
        while (a < b) {
            Collections.swap(tour, a++, b--);
        }
    }

    // Helpers

    private static double dist(Region a, Region b) {
        return a.capital().distance(b.capital());
    }

    private static double lengthKm(List<Region> tour) {
        double sum = 0;
        for (int i = 0; i < tour.size(); i++) {
            Region a = tour.get(i);
            Region b = tour.get((i + 1) % tour.size());
            sum += dist(a, b);
        }
        return sum;
    }

    private static int perturb(List<Region> tour, Random rng, int k) {
        int n = tour.size();
        for (int i = 0; i < k; i++) {
            int a = rng.nextInt(n);
            int b = rng.nextInt(n);
            if (a > b) { int t = a; a = b; b = t; }
            if (a == 0 && b == n - 1) continue;
            apply2Opt(tour, a, b);
        }
        return k;
    }

    private static void insertCheapest(List<Region> tour, Region x) {
        int n = tour.size();
        int best = 0;
        double inc = Double.POSITIVE_INFINITY;
        for (int i = 0; i < n; i++) {
            Region a = tour.get(i);
            Region b = tour.get((i + 1) % n);
            double d = dist(a, x) + dist(x, b) - dist(a, b);
            if (d < inc) { inc = d; best = i; }
        }
        tour.add(best + 1, x);
    }

    private static Region nearestToTour(List<Region> tour, Set<Region> rem) {
        Region best = null;
        double d0 = Double.POSITIVE_INFINITY;
        for (Region r : rem) {
            for (Region t : tour) {
                double d = dist(r, t);
                if (d < d0) { d0 = d; best = r; }
            }
        }
        return best;
    }

    private static Region farthestFromTour(List<Region> tour, Set<Region> rem) {
        Region best = null;
        double d0 = Double.NEGATIVE_INFINITY;
        for (Region r : rem) {
            for (Region t : tour) {
                double d = dist(r, t);
                if (d > d0) { d0 = d; best = r; }
            }
        }
        return best;
    }

    private static Region randomFrom(Set<Region> set, Random rng) {
        int k = rng.nextInt(set.size());
        int i = 0;
        for (Region r : set) if (i++ == k) return r;
        return set.iterator().next();
    }

    private static List<Region> pickDistinct(Region[] regions, int k, Random rng) {
        List<Region> list = new ArrayList<>(Arrays.asList(regions.clone()));
        Collections.shuffle(list, rng);
        return list.subList(0, k);
    }

    private static List<Region> parseRegions(List<String> toks, Region[] regions) {
        List<Region> out = new ArrayList<>();
        for (String t : toks) {
            Region r = Region.find(t, regions);
            if (r == null) die("Unknown region: " + t);
            out.add(r);
        }
        return out;
    }

    private static void printCodesOnly(List<Region> tour) {
        for (int i = 0; i < tour.size(); i++) {
            if (i > 0) System.out.print(" ");
            System.out.print(tour.get(i).code());
        }
        System.out.println();
    }

    // Dataset loader

    private static Region[] loadRegions(String flag) {
        String cls;
        switch (flag) {
            case "-usa": cls = "US$State"; break;
            case "-california": cls = "California$County"; break;
            case "-bay": cls = "BayArea$County"; break;
            case "-canada": cls = "Canada$Province"; break;
            case "-caribbean": cls = "Caribbean$Country"; break;
            case "-central": cls = "CentralAmerica$Country"; break;
            case "-south": cls = "SouthAmerica$Country"; break;
            case "-europe": cls = "Europe$Country"; break;
            case "-africa": cls = "Africa$Country"; break;
            default: return null;
        }
        try {
            Class<?> c = Class.forName("Plot." + cls);
            Object[] o = c.getEnumConstants();
            Region[] r = new Region[o.length];
            for (int i = 0; i < o.length; i++) r[i] = (Region) o[i];
            return r;
        } catch (Exception e) {
            die("Dataset load failed: " + cls);
            return null;
        }
    }

    private static void die(String msg) {
        System.err.println(msg);
        System.exit(1);
    }
}
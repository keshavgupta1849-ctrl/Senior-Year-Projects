import java.io.*;
import java.util.*;

public class GradientDescent {

    static class Data {
        double[][] X;   // feature matrix
        double[] y;     // target labels
        String[] rowLabels; // row identifiers
        int m, n;       // row count, feature count
    }

    static final int ACT_LINEAR   = 0; // linear activation mode
    static final int ACT_LOGISTIC = 1; // logistic activation mode
    static final int ACT_ARCTAN   = 2; // arctan activation mode

    // returns activated value based on mode
    static double activate(double z, int mode) {
        switch (mode) {
            case ACT_LOGISTIC: return 1.0 / (1.0 + Math.exp(-z)); // sigmoid
            case ACT_ARCTAN:   return Math.atan(z);                // arctan
            default:           return z;                           // linear passthrough
        }
    }

    // returns derivative of activation for gradient computation
    static double activateDeriv(double z, int mode) {
        switch (mode) {
            case ACT_LOGISTIC: {
                double s = 1.0 / (1.0 + Math.exp(-z)); // compute sigmoid
                return s * (1.0 - s);                   // sigmoid derivative
            }
            case ACT_ARCTAN: return 1.0 / (1.0 + z * z); // arctan derivative
            default:         return 1.0;                   // linear derivative
        }
    }

    // reads csv from file or stdin and returns parsed Data
    static Data readCsv(String filename, boolean hasHeaders, boolean hasLabels, String yesLabel, String noLabel) throws IOException {
        BufferedReader br = (filename != null)          // use file if given
                ? new BufferedReader(new FileReader(filename))
                : new BufferedReader(new InputStreamReader(System.in)); // else stdin

        List<double[]> rows = new ArrayList<>();  // accumulate parsed rows
        List<String> labels = new ArrayList<>();  // accumulate row labels
        String line;
        boolean firstLine = true; // track if we're on first line
        int cols = -1;            // number of columns, set on first data row

        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue; // skip blank lines

            if (firstLine && hasHeaders) { // skip header row if flagged
                firstLine = false;
                continue;
            }
            firstLine = false;

            String[] parts = line.split(","); // split on comma
            for (int i = 0; i < parts.length; i++) {
                parts[i] = parts[i].trim().replaceAll("^\"|\"$", ""); // strip quotes
            }

            int startCol = 0;       // first column index for data
            String rowLabel = null; // label for this row

            if (hasLabels) {        // first column is a label
                rowLabel = parts[0];
                startCol = 1;       // data starts after label
            }

            int dataCols = parts.length - startCol; // number of data columns
            if (cols == -1) cols = dataCols;         // set expected column count
            if (dataCols != cols) die("Inconsistent column count."); // validate

            double[] row = new double[cols]; // parsed numeric row

            for (int i = 0; i < cols; i++) {
                String s = parts[startCol + i]; // current cell value
                if (i == cols - 1) {            // last column is class label
                    if (s.equals(yesLabel)) {
                        row[i] = 1.0;           // positive class
                    } else if (s.equals(noLabel)) {
                        row[i] = 0.0;           // negative class
                    } else {
                        try {
                            row[i] = Double.parseDouble(s); // try numeric
                        } catch (NumberFormatException e) {
                            die("Unrecognized class label: '" + s + "'. Use -yes and -no to specify labels.");
                        }
                    }
                } else {
                    try {
                        row[i] = Double.parseDouble(s); // parse attribute
                    } catch (NumberFormatException e) {
                        die("Non-numeric value in attribute column: '" + s + "'");
                    }
                }
            }

            rows.add(row);  // store parsed row
            labels.add(rowLabel != null ? rowLabel : "Item " + String.format("%03d", rows.size())); // store label
        }

        if (rows.isEmpty()) die("No data read."); // nothing parsed

        int m = rows.size();  // number of examples
        int n = cols - 1;     // number of features
        if (n < 1) die("Need at least 1 attribute column.");

        double[][] X = new double[m][n]; // feature matrix
        double[] y = new double[m];      // target vector

        for (int i = 0; i < m; i++) {
            System.arraycopy(rows.get(i), 0, X[i], 0, n); // copy features
            y[i] = rows.get(i)[n];                         // copy target
        }

        Data d = new Data();       // package results
        d.X = X;
        d.y = y;
        d.rowLabels = labels.toArray(new String[0]);
        d.m = m;
        d.n = n;
        return d;
    }

    static class NormInfo {
        double[] xMean, xStd; // per-feature mean and std
        double yMean, yStd;   // target mean and std
    }

    // computes mean of array
    static double mean(double[] a) {
        double s = 0;
        for (double v : a) s += v; // sum all values
        return s / a.length;       // divide by count
    }

    // computes standard deviation given precomputed mean
    static double std(double[] a, double mu) {
        double s = 0;
        for (double v : a) { double d = v - mu; s += d * d; } // sum squared deviations
        double sd = Math.sqrt(s / a.length);                   // population std
        return sd == 0 ? 1.0 : sd;                             // avoid divide by zero
    }

    // computes normalization parameters from X and y
    static NormInfo computeNorm(double[][] X, double[] y) {
        int m = X.length, n = X[0].length;
        NormInfo ni = new NormInfo();
        ni.xMean = new double[n];
        ni.xStd  = new double[n];
        for (int j = 0; j < n; j++) {
            double[] col = new double[m];
            for (int i = 0; i < m; i++) col[i] = X[i][j]; // extract column
            ni.xMean[j] = mean(col);
            ni.xStd[j]  = std(col, ni.xMean[j]);
        }
        ni.yMean = mean(y);
        ni.yStd  = std(y, ni.yMean);
        return ni;
    }

    // applies z-score normalization in-place to X and y
    static void applyZScore(double[][] X, double[] y, NormInfo ni, int actMode) {
        int m = X.length, n = X[0].length;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++)
                X[i][j] = (X[i][j] - ni.xMean[j]) / ni.xStd[j]; // normalize feature
            if (actMode == ACT_LINEAR)
                y[i] = (y[i] - ni.yMean) / ni.yStd; // only normalize y for linear
        }
    }

    // converts normalized weights back to original feature space
    static double[] denormalizeWeights(double[] w, NormInfo ni) {
        int n = ni.xMean.length;
        double[] c = new double[n + 1]; // output coefficients
        for (int j = 1; j <= n; j++)
            c[j] = ni.yStd * w[j] / ni.xStd[j - 1]; // rescale slope
        double c0 = ni.yMean + ni.yStd * w[0];        // start intercept
        for (int j = 1; j <= n; j++)
            c0 -= c[j] * ni.xMean[j - 1];             // adjust for feature means
        c[0] = c0;
        return c;
    }

    // computes dot product of two vectors
    static double dot(double[] a, double[] b) {
        double s = 0;
        for (int i = 0; i < a.length; i++) s += a[i] * b[i]; // sum products
        return s;
    }

    // computes euclidean norm of vector
    static double l2Norm(double[] v) {
        double s = 0;
        for (double x : v) s += x * x; // sum squares
        return Math.sqrt(s);
    }

    // computes gradient of squared loss over given batch indices
    static double[] computeGradient(double[] w, double[][] Xw, double[] y, int[] idxs, int actMode) {
        int p = w.length;
        double[] g = new double[p]; // gradient accumulator
        int B = idxs.length;        // batch size

        for (int id : idxs) {
            double z    = dot(w, Xw[id]);           // linear combination
            double yhat = activate(z, actMode);      // apply activation
            double err  = yhat - y[id];              // prediction error
            double da   = activateDeriv(z, actMode); // activation derivative

            for (int j = 0; j < p; j++) {
                g[j] += err * da * Xw[id][j]; // chain rule gradient contribution
            }
        }

        double scale = 2.0 / B; // average and account for squared loss factor
        for (int j = 0; j < p; j++) g[j] *= scale; // scale gradient

        return g;
    }

    // prints coefficients as space-separated line
    static void printCoefficients(double[] c) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < c.length; i++) {
            if (i > 0) sb.append(' ');
            sb.append(String.format(Locale.US, "%.4f", c[i])); // format to 4 decimals
        }
        System.out.println(sb);
    }

    // prints error message and exits
    static void die(String msg) {
        System.err.println(msg);
        System.exit(1);
    }
    // converts normalized logistic weights back to original feature space
    static double[] denormalizeLogisticWeights(double[] w, NormInfo ni) {
        int n = ni.xMean.length;
        double[] c = new double[n + 1];
        for (int j = 1; j <= n; j++)
            c[j] = w[j] / ni.xStd[j - 1];
        double c0 = w[0];
        System.err.println("c0 start: " + c0);
        for (int j = 1; j <= n; j++) {
            System.err.println("subtracting c[" + j + "]=" + c[j] + " * xMean[" + (j-1) + "]=" + ni.xMean[j-1] + " = " + (c[j] * ni.xMean[j-1]));
            c0 -= c[j] * ni.xMean[j - 1];
        }
        System.err.println("c0 final: " + c0);
        c[0] = c0;
        return c;
    }

    public static void main(String[] argv) throws Exception {
        double step      = 0.01;   // default step size
        int iterations   = 1000;   // default max iterations
        double stop      = 0.0;    // default gradient norm stop threshold
        Integer batchOpt = null;   // null means full batch
        boolean stochastic = false; // stochastic gradient descent flag
        boolean normalize  = false; // z-score normalization flag
        boolean verbose    = false; // verbose output flag
        int actMode        = ACT_LINEAR; // default activation
        double[] initial   = null;  // optional initial weights
        String filename    = null;  // optional input file
        boolean hasHeaders = false; // skip header row flag
        boolean hasLabels  = false; // first column is label flag
        String yesLabel    = "yes"; // default positive class name
        String noLabel     = "no";  // default negative class name

        for (int i = 0; i < argv.length; i++) {
            switch (argv[i]) {
                case "-file":       filename   = argv[++i]; break;                     // input file path
                case "-headers":    hasHeaders = true; break;                          // has header row
                case "-labels":     hasLabels  = true; break;                          // has row labels
                case "-step":       step       = Double.parseDouble(argv[++i]); break; // learning rate
                case "-iterations": iterations = Integer.parseInt(argv[++i]); break;   // max iterations
                case "-stop":       stop       = Double.parseDouble(argv[++i]); break; // stop threshold
                case "-batch":      batchOpt   = Integer.parseInt(argv[++i]); break;   // batch size
                case "-stochastic": stochastic = true; break;                          // use SGD
                case "-normalize":  normalize  = true; break;                          // normalize data
                case "-logistic":   actMode    = ACT_LOGISTIC; break;                  // logistic mode
                case "-arctan":     actMode    = ACT_ARCTAN; break;                    // arctan mode
                case "-verbose":    verbose    = true; break;                          // verbose mode
                case "-yes":        yesLabel   = argv[++i]; break;                     // positive label
                case "-no":         noLabel    = argv[++i]; break;                     // negative label
                case "-initial": {
                    List<Double> vals = new ArrayList<>();
                    while (i + 1 < argv.length && !argv[i + 1].startsWith("-")) {
                        vals.add(Double.parseDouble(argv[++i])); // read initial weights
                    }
                    initial = new double[vals.size()];
                    for (int k = 0; k < vals.size(); k++) initial[k] = vals.get(k);
                    break;
                }
                default: die("Unknown argument: " + argv[i]);
            }
        }

        Data d = readCsv(filename, hasHeaders, hasLabels, yesLabel, noLabel); // load dataset
        int m = d.m, n = d.n, p = n + 1; // p includes bias weight

        if (step <= 0) die("Step must be > 0.");
        if (iterations < 0) die("Iterations must be >= 0.");

        int batch = (batchOpt == null) ? m : batchOpt; // default to full batch
        if (stochastic) batch = 1;                      // SGD forces batch=1
        if (batch <= 0) die("Batch size must be >= 1.");
        if (batch > m) batch = m;                       // cap at dataset size

        NormInfo normInfo = null;
        if (normalize) {
            normInfo = computeNorm(d.X, d.y);
            System.err.println("xMean: " + Arrays.toString(normInfo.xMean));
            System.err.println("xStd:  " + Arrays.toString(normInfo.xStd));// compute stats
            applyZScore(d.X, d.y, normInfo, actMode);  // normalize in-place
        }

        double[][] Xw = new double[m][p]; // augmented matrix with bias column
        for (int i = 0; i < m; i++) {
            Xw[i][0] = 1.0;                                          // bias term
            for (int j = 1; j < p; j++) Xw[i][j] = d.X[i][j - 1]; // copy features
        }

        double[] w = new double[p]; // weight vector, defaults to zero
        if (initial != null) {
            if (initial.length != p) die("-initial needs exactly " + p + " values.");
            System.arraycopy(initial, 0, w, 0, p); // copy provided initial weights
        }

        Random rng = new Random(0); // seeded rng for reproducibility

        for (int t = 0; t < iterations; t++) {
            int[] idxs = new int[batch]; // indices for this batch
            if (stochastic) {
                idxs[0] = rng.nextInt(m); // random single example
            } else {
                int start = (t * batch) % m; // cycling start index
                for (int k = 0; k < batch; k++) idxs[k] = (start + k) % m; // fill batch
            }

            double[] grad = computeGradient(w, Xw, d.y, idxs, actMode); // compute gradient
            if (stop > 0.0 && l2Norm(grad) < stop) break;                // check stop condition
            for (int j = 0; j < p; j++) w[j] -= step * grad[j];         // update weights
        }

        double[] out = !normalize ? w
                : (actMode == ACT_LINEAR)
                ? denormalizeWeights(w, normInfo)          // linear: undo x and y normalization
                : denormalizeLogisticWeights(w, normInfo); // logistic: undo x normalization only

        printCoefficients(out); // output final weights

        if (verbose) {
            for (int i = 0; i < m; i++) {
                double z           = dot(w, Xw[i]);           // compute linear combination
                double yhat        = activate(z, actMode);    // apply activation
                boolean classified = yhat > 0.5;              // threshold at 0.5
                boolean actual     = d.y[i] >= 0.5;           // true class
                String prediction  = classified ? yesLabel : noLabel; // predicted label
                String truth       = actual ? yesLabel : noLabel;     // actual label
                String correct     = classified == actual ? "CORRECT" : "WRONG"; // result
                System.out.printf("%s: classified as %s (actual: %s) [%s]%n",
                        d.rowLabels[i], prediction, truth, correct);
            }
        }
    }
}
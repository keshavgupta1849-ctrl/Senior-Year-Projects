import java.util.ArrayList;
import java.util.Scanner;
import java.io.IOException;
import java.io.File;

public class Predict {

    // A program to determine the error rate for a particular linear regression
	// classifier model on a given data set.  It reads the dataset from standard
	// input (or via the file name specified by the -file <filename> command line
	// option).  The dataset is expected to be a CSV file.  Each line contains
	// the attribute values followed by the correct classification.  A line may
	// optionally contain an identifying label as the first column (see the -labels
	// command line option).  The first line may be a row of column headers (see
	// the -headers command line option).  The coefficients for attributes in the
	// linear regression model are specified (in order, starting with the bias)
	// on the command line as floating-point numbers.  The output will be the
	// number of correctly classified items.

	// Command line options
	//
	//    -file <name>            The name of the file containing the dataset
	//    -yes <classification>   The value of the label to be used for an object classified as "yes"
	//    -no <classifification>  The value of the label to be used for an obect classified as "no"
	//    -logistic               Use the logistic function for sigma(w * x)
	//    -arctan                 Use the arctan function for sigma(w * x)
	//    -relu                   Use the RELU function for sigma(w * x)
	//    -header                 The first line of the dataset contains column labels
	//    -labels                 The first column of the data set contains row labels
	//    -verbose                Display the classification for each item in the dataset

    private enum Function {
        LOGISTIC() {
            @Override
            public double eval(double x) {
                return 1.0 / (1.0 + Math.exp(-x));
            }

            @Override
            public boolean classify(double x) {
                return eval(x) > 0.5;
            }
        },

        ARCTAN() {
            @Override
            public double eval(double x) {
                return Math.atan(x);
            }

            @Override
            public boolean classify(double x) {
                return eval(x) > 0.0;
            }
        },

        RELU() {
            @Override
            public double eval(double x) {
                return (x > 0.0) ? x : 0.0;
            }
            
            @Override
            public boolean classify(double x) {
                return eval(x) > 0.0;
            }
        };

        public abstract double eval(double x);
        public abstract boolean classify(double x);
    }

    private static String noLabel = "no";
    private static String yesLabel = "yes";
    private static boolean labels = false;
    private static boolean header = false;
    private static boolean verbose = false;
    private static boolean trace = false;
    private static String filename = null;

    private static Function function = Function.LOGISTIC;
    private static Double[] coefficients;

    private static double coefficient(int index) {
        return (index < coefficients.length) ? coefficients[index] : 0.0;
    }

    private static double attribute(String[] fields, int index) {
        if (labels) index++;
        String field = fields[index].trim();
        return (index < fields.length-1) ? Double.parseDouble(field) : 0.0;
    }

    private static int attributeCount(String[] fields) {
        int count = fields.length - 1;
        return labels ? count - 1 : count;
    }

    public static void printLine(int line, String[] fields, String prediction) {
        String answer = fields[fields.length-1].trim();
        if (labels) {
            System.out.print(fields[0] + ": ");
        } else {
            System.out.print("Item #" + line + ": ");
        }
        System.out.print(prediction);
        if (!answer.equals(prediction)) {
            System.out.print(" (ERROR)");
        }
        System.out.println();
    }

    public static void traceLine(int line, String[] fields, double x) {
        String answer = fields[fields.length-1].trim();
        if (labels) {
            System.out.print(fields[0] + ": ");
        } else {
            System.out.print("Item #" + line + ": ");
        }

        System.out.printf("%s(%.1f",  function.name(), coefficient(0));
        for (int i = 0; i < attributeCount(fields); i++) {
            System.out.printf(" + (%.1f * %.1f)", coefficient(i+1), attribute(fields, i));
        }
        System.out.printf(") = %s(%.1f) = %.2f\n", function.name(), x, function.eval(x));
    }

    public static boolean processLine(String line, int number) {
        String[] fields = line.trim().split("[ \t]*,");
        int first = labels ? 1 : 0;
        int last = fields.length - 2;
        int count = fields.length - first - 1;

        double x = coefficient(0);
        for (int i = 0; i < attributeCount(fields); i++) {
            x += coefficient(i+1) * attribute(fields, i);
        }

        String answer = fields[fields.length-1].trim();
        String prediction = function.classify(x) ? yesLabel : noLabel;
        boolean correct = prediction.equals(answer);
        if (verbose) printLine(number, fields, prediction);
        if (trace) traceLine(number, fields, x);
        return correct;
    }

    public static void main (String[] args) {
        ArrayList<Double> list = new ArrayList<>();
        String option = "";

        for (String arg : args) {
            switch (option) {
                case "-no":
                    noLabel = arg;
                    option = "";
                    continue;

                case "-yes":
                    yesLabel = arg;
                    option = "";
                    continue;

                case "-file":
                    filename = arg;
                    option = "";
                    continue;
            }

            switch (arg) {
                case "-header":
                    header = true;
                    continue;

                case "+header":
                    header = false;
                    continue;

                case "-labels":
                    labels = true;
                    continue;

                case "+labels":
                    labels = false;
                    continue;

                case "-verbose":
                    verbose = true;
                    continue;

                case "+verbose":
                    verbose = false;
                    continue;

                case "-trace":
                    trace = true;
                    continue;

                case "+trace":
                    trace = false;
                    continue;

                case "-logistic":
                    function = Function.LOGISTIC;
                    continue;

                case "-arctan":
                    function = Function.ARCTAN;
                    continue;

                case "-relu":
                    function = Function.RELU;
                    continue;

                case "-no":
                case "-yes":
                case "-file":
                    option = arg;
                    continue;

                default:
                    try {
                        list.add(Double.parseDouble(arg));
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid coefficient: " + arg);
                        return;
                    }
            }
        }

        Scanner scanner = new Scanner(System.in);
        if (filename != null) {
            try {
                scanner = new Scanner(new File(filename));
            } catch (IOException e) {
                System.err.println("Cannot open " + filename + ": " + e.getMessage());
            }
        }
        if (header) scanner.nextLine();
        
        int count = 0;
        int errors = 0;
        coefficients = list.toArray(new Double[]{});

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            count++;
            boolean correct = processLine(line, count);
            if (!correct) errors++;
        }

        System.out.printf("Error rate: %.1f%% (%d/%d)", (100.0 * errors) / count, errors, count);
    }
}

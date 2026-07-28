package Plot;

import java.util.Scanner;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;

import javax.swing.*;
import java.awt.Color;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Dimension;

public class Plot {

    private static class Point {

        public double x;
        public double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    private static class MapPanel extends JPanel {

        private int width;
        private int height;

        private Image map;
        private Point[] points;

        public MapPanel(int width, int height, Image map, Point[] points) {
            this.setPreferredSize(new Dimension(width, height));
            this.width = width;
            this.height = height;
            this.points = points;
            this.map = map;
        }


        private void drawLine(Graphics g, Point from, Point to) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(3));
            int xfrom = (int) (width * from.x);
            int yfrom = (int) (height * from.y);
            int xto   = (int) (width * to.x);
            int yto   = (int) (height * to.y);
            g2.drawLine(xfrom, yfrom, xto, yto);
        }

        private void drawLines(Graphics g, Point[] points) {
            Point previous = points[points.length-1];
            for (int i = 0; i < points.length; i++) {
                Point current = points[i];
                drawLine(g, previous, current);
                previous = current;
            }
        }

        @Override
        public void paintComponent(Graphics g) {
            g.drawImage(map, 0, 0, null);
            g.setColor(Color.RED);
            if (points.length > 1) {
                drawLines(g, points);
            }
        }
    }


    public static void plot(String title, Image image, int width, int height, Point[] tour) {
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);

        if (width == 0) {
            if (height == 0) {
                height = imageHeight;
            }
            width = height * imageWidth / imageHeight;
        } else if (height == 0) {
            height = width * imageHeight / imageWidth;
        }

        Image scaled =
            image.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        JFrame frame = new JFrame(title);
        JPanel panel = new MapPanel(width, height, scaled, tour);

        panel.setPreferredSize(new Dimension(width, height));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.setSize(width, height);
        frame.setVisible(true);
        frame.pack();
    }


    public static Image readMap(String filename) throws IOException {
        return ImageIO.read(new File(filename));
    }

    public static ArrayList<Region> readTour(Scanner scanner, Region[] regions) {
        ArrayList<Region> tour = new ArrayList<>();
        while (scanner.hasNext()) {
            String item = scanner.next();
            Region region = Region.find(item, regions);
            if (region == null) {
                String kind = Region.kind(regions);
                throw new IllegalArgumentException(String.format("Invalid %s: %s", kind ,item));
            } else {
                tour.add(region);
            }
        }
        return tour;
    }

    public static ArrayList<Region> readTour(String filename, Region[] regions) throws IOException {
        return readTour(new Scanner(new File(filename)), regions);
    }

    public static Point[] readLocations(Scanner scanner, Region[] regions) {
        Point[] locations = new Point[regions.length];
        while (scanner.hasNextLine()) {
            String[] fields = scanner.nextLine().split(",");
            Region region = Region.find(fields[0], regions);
            if (region == null) {
                String kind = Region.kind(regions);
                throw new IllegalArgumentException(String.format("Invalid %s: %s", kind, fields[0]));
            }
            double x = Double.parseDouble(fields[1]);
            double y = Double.parseDouble(fields[2]);
            locations[region.ordinal()] = new Point(x, y);
        }
        return locations;
    }

    public static Point[] readLocations(String filename, Region[] regions) throws IOException {
        return readLocations(new Scanner(new File(filename)), regions);
    }

    public static Point[] getPoints(ArrayList<Region> tour, Point[] locations) {
        Point[] points = new Point[tour.size()];
        int count = 0;
        for (Region region : tour) {
            points[count++] = locations[region.ordinal()];
        }
        return points;
    }

    public static String basename(String filename) {
        int dot = filename.lastIndexOf('.');
        return filename.substring(0, dot);
    }

    public static void help() {
        System.out.println();
        System.out.println("java Plot <options> <list of cities on the tour>");
        System.out.println();
        System.out.println("    Draws the specified tour on map of the region");
        System.out.println("    Files containing the maps (png) and coordinates (csv)");
        System.out.println("    are assumed to be in the 'Maps' sub-folder of the current");
        System.out.println("    folder but may be override with command line options");
        System.out.println();
        System.out.println("    If a tour is not provided on the command line");
        System.out.println("    the tour is read from standard input which allows");
        System.out.println("    the tour produced by your code to be displayed using");
        System.out.println("    a pipe ... and the -echo option allows it to be printed:");
        System.out.println();
        System.out.println("        java TSP -canada | java Plot -canada -echo");
        System.out.println();
        System.out.println("    Valid command line options are:");
        System.out.println();
        System.out.println("    -width #           Width of the map window in pixels");
        System.out.println("    -height #          Height of the map window in pixels");
        System.out.println("    -echo              Display (echo) the tour on standard output");
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
        System.out.println("    -maps <folder>     The folder in which to find the map/csv files");
        System.out.println("    -map <filenane>    The name of the map (PNG) file to display");
        System.out.println("    -csv <filename>    The name of the CSV file containing coordinates");
        System.out.println();
    }


    public static void main(String[] args) {
        ArrayList<Region> tour = new ArrayList<>();
        Region[] regions = US.State.values();
        String mapfile = "US.png";
        String csvfile = null;
        String folder = "Maps";
        String option = "";
        boolean echo = false;
        int errors = 0;
        int height = 0;
        int width = 0;

        for (String arg : args) {
            switch (arg) {
                case "-csv":
                case "-map":
                case "-maps":
                case "-width":
                case "-height":
                    option = arg;
                    continue;

                case "-echo":
                    echo = true;
                    continue;

                case "+echo":
                    echo = false;
                    continue;

                case "-us":
                case "-usa":
                    regions = US.State.values();
                    mapfile = "US.png";
                    continue;

				case "-ca":
				case "-california":
					regions = California.County.values();
					mapfile = "California.jpg";
					continue;

				case "-bay":
				case "-bay_area":
					regions = BayArea.County.values();
					mapfile = "BayArea.jpg";
					continue;

                case "-can":
                case "-canada":
                    regions = Canada.Province.values();
                    mapfile = "Canada.png";
                    continue;

                case "-central":
                case "-central_america":
                    regions = CentralAmerica.Country.values();
                    mapfile = "Central America.png";
                    continue;

                case "-sa":
                case "-south":
                case "-south_america":
                    regions = SouthAmerica.Country.values();
                    mapfile = "South America.png";
                    continue;

                case "-na":
                case "-north":
                case "-north_america":
                    regions = NorthAmerica.Country.values();
                    mapfile = "North America.png";
                    continue;

                case "-car":
                case "-caribbean":
                    regions = Caribbean.Country.values();
                    mapfile = "Caribbean.png";
                    continue;

                case "-eu":
                case "-europe":
                    regions = Europe.Country.values();
                    mapfile = "Europe.jpg";
                    continue;

                case "-help":
                    help();
                    return;

                default:
                    if (arg.startsWith("-") || arg.startsWith("+")) {
                        System.err.println("Invalid option: " + arg);
                        errors++;
                        continue;
                    }
            }

            try {
                switch (option) {
                    case "-map":
                        mapfile = arg;
                        break;

                    case "-csv":
                        csvfile = arg;
                        break;

                    case "-maps":
                        folder = arg;
                        break;

                    case "-width":
                        width = Integer.parseInt(arg);
                        break;

                    case "-height":
                        height = Integer.parseInt(arg);
                        break;

                    default:
                        Region region = Region.find(arg, regions);
                        String kind = Region.kind(regions);
                        if (region == null) {
                            System.err.printf("Invalid %s: %s\n", kind, arg);
                            errors++;
                        } else {
                            if (tour.contains(region)) {
                                System.err.printf("Duplicate %s: %s\n", kind, arg);
                                errors++;
                            } else {
                                tour.add(region);
                            }
                        }

                }

            } catch (NumberFormatException e) {
                System.err.println("Invalid value for " + option + ": " + arg);
                errors++;
            }

            option = "";
        }

        if (errors > 0) return;

        Image image;
        Point[] locations;

        mapfile = folder + "/" + mapfile;
        try {
            image = readMap(mapfile);
        } catch (IOException e) {
            System.err.println("Could not read map file: " + mapfile);
            return;
        }

        if (csvfile == null) {
            csvfile = basename(mapfile) + ".csv";
        } else {
            csvfile = folder + "/" + csvfile;
        }

        try {
            locations = readLocations(csvfile, regions);
        } catch (Exception e) {
            System.err.println("Could not read csv file: " + csvfile);
            return;
        }

        if (tour.size() == 0) {
            try {
                tour = readTour(new Scanner(System.in), regions);
            } catch (Exception e) {
                System.err.println("Could not read tour file: " + e.getMessage());
                return;
            }
        }

        String name = basename(mapfile);
        plot(name, image, width, height, getPoints(tour, locations));

        if (echo) {
            String separator = "";
            for (Region region : tour) {
                System.out.print(separator);
                System.out.print(region.code());
                separator = " ";
            }
            System.out.println();
        }
    }
}

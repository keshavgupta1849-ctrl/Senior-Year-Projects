package Plot;

public class SouthAmerica {

    public static enum Country implements Region {
        
        Argentina      ("ARG", "Argentina",     "Buenos Aires", -34.6037, -58.3816),
        Bolivia        ("BOL", "Bolivia",       "La Paz",       -16.4897, -68.1193),
        Brazil         ("BRA", "Brazil",        "Brasilia",     -15.7975, -47.8919),
        Chile          ("CHL", "Chile",         "Santiago",     -33.4489, -70.6693),
        Columnbia      ("COL", "Columnbia",     "Bogota",         4.7110, -74.0721),
        Ecuador        ("ECU", "Ecuador",       "Quito",         -0.2233, -78.5141),
        FrenchGuiana   ("GUF", "French Guiana", "Cayenne",        4.9380, -52.3354),
        Guyana         ("GUY", "Guyana",        "Georgetown",     6.8013, -58.1551),
        Paraguay       ("PRY", "Paraguay",      "Asuncion",     -25.2637, -57.5759),
        Peru           ("PER", "Peru",          "Lima",         -12.0467, -77.0431),
        Suriname       ("SUR", "Suriname",      "Paramaribo",     5.8520, -55.2038),
        Uruguay        ("URY", "Uruguay",       "Montevideo",   -34.9055, -56.1851),
        Venezuela      ("VEN", "Venezuela",     "Caracas",       10.4806, -66.9036);

        private String code;
        private String name;
        private City capital;

        private Country(String code, String name, String capital, double lat, double lon) {
            this.capital = new City(capital, this, lat, lon);
            this.code = code;
            this.name = name;
        }

        @Override
        public String code() {
            return this.code;
        }

        @Override
        public City capital() {
            return this.capital;
        }

        @Override
        public String kind() {
            return "Country";
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public static Country find(String name) {
        return (Country) Region.find(name, Country.values());
    }
}

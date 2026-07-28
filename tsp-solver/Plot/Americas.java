package Plot;

public class Americas {

    public static enum Country implements Region {
        
        // North America

        Bermuda      ("BMU", "Bermuda",       "Hamilton",       32.2951, -67.7842, "UK"),
        Canada       ("CAN", "Canada",        "Ottawa",         45.4201, -75.7003),
        Greenland    ("GRL", "Greenland",     "Nuuk",           64.1743, -51.7373, "DK"),
        Mexico       ("MEX", "Mexico",        "Mexico Ciy",     19.4326, -99.1332),
        UnitedStates ("USA", "United States", "Washington DC",  38.9072, -77.0369),

        // Central America 

        Belize       ("BLZ", "Belize",        "Belmopan",       17.2510, -88.7590),
        CostaRica    ("CRI", "Costa Rica",    "San Jose",        9.9281, -84.0907),
        ElSalvador   ("SLV", "El Salvador",   "San Salvador",   13.6929, -89.2182),
        Guatemala    ("GTM", "Guatemala",     "Guatamala City", 14.6349, -90.5069),
        Honduras     ("HND", "Honduras",      "Tegucigalpa",    14.0650, -87.1715),
        Nicaragua    ("NIC", "Nicaragua",     "Managua",        12.1150, -86.2362),
        Panama       ("PAN", "Panama",        "Panama City",     8.9824, -79.5199),

        // South America 

        Argentina    ("ARG", "Argentina",     "Buenos Aires",  -34.6037, -58.3816),
        Bolivia      ("BOL", "Bolivia",       "La Paz",        -16.4897, -68.1193),
        Brazil       ("BRA", "Brazil",        "Brasilia",      -15.7975, -47.8919),
        Chile        ("CHL", "Chile",         "Santiago",      -33.4489, -70.6693),
        Columnbia    ("COL", "Columnbia",     "Bogota",          4.7110, -74.0721),
        Ecuador      ("ECU", "Ecuador",       "Quito",          -0.2233, -78.5141),
        FrenchGuiana ("GUF", "French Guiana", "Cayenne",         4.9380, -52.3354),
        Guyana       ("GUY", "Guyana",        "Georgetown",      6.8013, -58.1551),
        Paraguay     ("PRY", "Paraguay",      "Asuncion",      -25.2637, -57.5759),
        Peru         ("PER", "Peru",          "Lima",          -12.0467, -77.0431),
        Suriname     ("SUR", "Suriname",      "Paramaribo",     5.8520, -55.2038),
        Uruguay      ("URY", "Uruguay",       "Montevideo",   -34.9055, -56.1851),
        Venezuela    ("VEN", "Venezuela",     "Caracas",       10.4806, -66.9036),

        // Caribbean

        Anguilla                     ("AIA", "Anguilla",                        "The Valley",       18.2148, 63.05574, "UK"),
        AntiguaAndBarbuda            ("ATG", "Antigua and Barbuda",             "Saint John's",     17.1274, -61.8468),
        Aruba                        ("AWB", "Aruba",                           "Oranjestad",       12.5227, -70.0353, "NL"),
        Bahamas                      ("BHS", "Bahamas",                         "Nassau",           25.0443, -77.3504),
        Bonaire                      ("BES", "Bonaire",                         "Kralendik",        12.1498, -68.2763, "NL"),
        BritishVirginIslands         ("VGB", "British Virgin Islands",          "Road Town",        18.4286, -64.6185, "UK"),
        CaymanIslands                ("CYM", "Cayman Islands",                  "George Town",      19.2966, -81.3819, "UK"),
        Cuba                         ("CUB", "Cuba",                            "Havana",           32.1136, -82.3666),
        Curaçao                      ("CUW", "Curaçao",                         "Willemstad",       12.1039, -68.9324, "NL"),
        Dominica                     ("DMA", "Dominica",                        "Roseau",           15.3017, -61.3881),
        DominicanRepublic            ("DOM", "Dominican Republic",              "Santo Domingo",    18.4626, -69.9361),
        Grenada                      ("GRD", "Grenada",                         "Saint George's",   12.0561, -61.7488),
        Guadaloupe                   ("GLP", "Guadaloupe",                      "Basse-Terre",      15.9966, -61.7317, "FR"),
        Haiti                        ("HTI", "Haiti",                           "Port-au-Prince",   18.5944, -72.3074),
        Jamaica                      ("JAM", "Jamaica",                         "Kingston",         18.0179, -76.8099),
        Martinique                   ("MTQ", "Martinique",                      "Fort-de-France",   14.6061, -61.0699, "FR"),
        Montserrat                   ("MSR", "Montserrat",                      "Plymouth",         16.7065, -62.2157, "UK"),
        PuertoRico                   ("PRI", "Puerto Rico",                     "San Juan",         18.4153, -66.0594, "US"),
        SaintBarthelemy              ("BLM", "Saint Barthelemy",                "Gustavia",         17.8964, -62.8522, "FR"),
        SaintKittsAndNevis           ("KNA", "Saint Kitts and Nevis",           "Basseterre",       17.3026, -62.7177),
        SaintLucia                   ("LCA", "Saint Lucia",                     "Castries",         14.0110, -60.9897),
        SaintMartin                  ("MAF", "Saint Martin",                    "Marigot",          18.0675, -63.0825, "FR"),
        SaintVincentAndTheGrenadines ("VCT", "Saint Vincent and TheGrenadines", "Kingstown",        13.1600, -61.2248),
        SintMaarten                  ("SXM", "Sint Maaren",                     "Philipsburg",      18.0296, -63.0471, "NL"),
        TrinidadAndTobago            ("TTO", "Trinidad and Tobago",             "Port of Spain",    10.6603, -61.5086),
        TurksAndCaicosIslands        ("TCA", "Turks and Caicos",                "Cockburn Town",    21.4675, -71.1389, "UK"),
        USVirginIslands              ("VIR", "US Virgin Islands",               "Charlotte Amalie", 18.3419, -64.9311, "US");


        private String code;
        private String name;
        private City capital;
        private String sovereignty;

        private Country(String code, String name, String capital, double lat, double lon, String sovereignty) {
            this.capital = new City(capital, this, lat, lon);
            this.code = code;
            this.name = name;
            this.sovereignty = sovereignty;
        }

        private Country(String code, String name, String capital, double lat, double lon) {
            this(code, name, capital, lat, lon, null);
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

        public String sovereignty() {
            return this.sovereignty;
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

package Plot;

public class US {

    public enum State implements Region {
        
        Alabama        ("AL", "Alabama",        "Montgomery",      32.3617, -86.2792),
        // Alaska      ("AK", "Alaska",         "Juneau",          58.3014, -134.422),
        Arizona        ("AZ", "Arizona",        "Phoenix",         33.4500, -112.067),
        Arkansas       ("AR", "Arkansas",       "Little Rock",     34.7361, -92.3311),
        California     ("CA", "California",     "Sacramento",      38.5556, -121.469),
        Colorado       ("CO", "Colorado",       "Denver",          39.7618, -104.881),
        Connecticut    ("CT", "Connecticut",    "Hartford",        41.7627, -72.6743),
        Delaware       ("DE", "Delaware",       "Dover",           39.1619, -75.5267),
        Florida        ("FL", "Florida",        "Tallahassee",     30.4550, -84.2533),
        Georgia        ("GA", "Georgia",        "Atlanta",         33.7550, -84.3900),
        // Hawaii      ("HI", "Hawaii",         "Honolulu",        21.3000, -157.817),
        Idaho          ("ID", "Idaho",          "Boise",           43.6167, -116.200),
        Illinois       ("IL", "Illinois",       "Springfield",     39.6983, -89.6197),
        Indiana        ("IN", "Indiana",        "Indianapolis",    39.7910, -86.1480),
        Iowa           ("IA", "Iowa",           "Des Moines",      41.5908, -93.6208),
        Kansas         ("KS", "Kansas",         "Topeka",          39.0558, -95.6894),
        Kentucky       ("KY", "Kentucky",       "Frankfort",       38.1970, -84.8630),
        Louisiana      ("LA", "Louisiana",      "Baton Rouge",     30.4500, -91.1400),
        Maine          ("ME", "Maine",          "Augusta",         44.3070, -69.7820),
        Maryland       ("MD", "Maryland",       "Annapolis",       38.9729, -76.5012),
        Massachusetts  ("MA", "Massachusetts",  "Boston",          42.3581, -71.0636),
        Michigan       ("MI", "Michigan",       "Lansing",         42.7336, -84.5467),
        Minnesota      ("MN", "Minnesota",      "Saint Paul",      44.9442, -93.0936),
        Mississippi    ("MS", "Mississippi",    "Jackson",         32.2989, -90.1847),
        Missouri       ("MO", "Missouri",       "Jefferson City",  38.5767, -92.1736),
        Montana        ("MT", "Montana",        "Helena",          46.5958, -112.027),
        Nebraska       ("NE", "Nebraska",       "Lincoln",         40.8106, -96.6803),
        Nevada         ("NV", "Nevada",         "Carson City",     39.1608, -119.754),
        NewHampshire   ("NH", "New Hampshire",  "Concord",         43.2067, -71.5381),
        NewJersey      ("NJ", "New Jersey",     "Trenton",         40.2237, -74.7640),
        NewMexico      ("NM", "New Mexico",     "Santa Fe",        35.6672, -105.964),
        NewYork        ("NY", "New York",       "Albany",          42.6525, -73.7572),
        NorthCarolina  ("NC", "North Carolina", "Raleigh",         35.7667, -78.6333),
        NorthDakota    ("ND", "North Dakota",   "Bismarck",        46.8133, -100.779),
        Ohio           ("OH", "Ohio",           "Columbus",        39.9833, -82.9833),
        Oklahoma       ("OK", "Oklahoma",       "Oklahoma City",   35.4822, -97.5350),
        Oregon         ("OR", "Oregon",         "Salem",           44.9308, -123.029),
        Pennsylvania   ("PA", "Pennsylvania",   "Harrisburg",      40.2697, -76.8756),
        RhodeIsland    ("RI", "Rhode Island",   "Providence",      41.8236, -71.4222),
        SouthCarolina  ("SC", "South Carolina", "Columbia",        34.0006, -81.0347),
        SouthDakota    ("SD", "South Dakota",   "Pierre",          44.3680, -100.336),
        Tennessee      ("TN", "Tennessee",      "Nashville",       36.1667, -86.7833),
        Texas          ("TX", "Texas",          "Austin",          30.2500, -97.7500),
        Utah           ("UT", "Utah",           "Salt Lake City",  40.7500, -111.883),
        Vermont        ("VT", "Vermont",        "Montpelier",      44.2597, -72.5750),
        Virginia       ("VA", "Virginia",       "Richmond",        37.5333, -77.4667),
        Washington     ("WA", "Washington",     "Olympia",         47.0425, -122.893),
        WestVirginia   ("WV", "West Virginia",  "Charleston",      38.3472, -81.6333),
        Wisconsin      ("WI", "Wisconsin",      "Madison",         43.0667, -89.4000),
        Wyoming        ("WY", "Wyoming",        "Cheyenne",        41.1456, -104.802);

        private String code;
        private String name;
        private City capital;

        private State(String code, String name, String capital, double lat, double lon) {
            this.capital = new City(capital, this, lat, lon);
            this.code = code;
            this.name = name;
        }

        private State(String code, String capital, double lat, double lon) {
            this(code, null, capital, lat, lon);
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
            return "State";
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public static State find(String name) {
        return (State) Region.find(name, State.values());
    }
}

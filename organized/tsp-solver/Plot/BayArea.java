package Plot;

public class BayArea {

    public static enum County implements Region {
        
        Alameda      ("ALA", "Alameda",       "Oakland",       37.8044, -122.2711),
        ContraCosta  ("CC",  "Contra Costa",  "Martinez",      38.0194, -122.1341),
        Marin        ("MRN", "Marin",         "San Rafael",    37.9735, -122.5311),
        Napa         ("NAP", "Napa",          "Napa",          38.2975, -122.2869),
        SanFrancisco ("SF",  "San Francisco", "San Francisco", 37.7749, -122.4194),
        SanMateo     ("SM",  "San Mateo",     "Redwood City",  37.4869, -122.2364),
        SantaClara   ("SCL", "Santa Clara",   "San Jose",      37.3382, -121.8863),
        Solano       ("SOL", "Solano",        "Fairfield",     38.2494, -122.0399),
        Sonoma       ("SON", "Sonoma",        "Santa Rosa",    38.4404, -122.7141);

        private String code;
        private String name;
        private City capital;

        private County(String code, String name, String capital, double lat, double lon) {
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
            return "County";
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public static County find(String name) {
        return (County) Region.find(name, County.values());
    }
}

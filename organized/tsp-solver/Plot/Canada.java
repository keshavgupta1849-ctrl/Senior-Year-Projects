package Plot;

public class Canada {

    public static enum Province implements Region {
        
        Alberta              ("AB", "Alberta",              "Edmonton",      53.5461, -113.4938),
        BritishColumbia      ("BC", "BritishColumbia",      "Victoria",      48.4284, -123.3656),
        Manitoba             ("MB", "Manitoba",             "Winnipeg",      49.8951,  -97.1384),
        NewBrunswick         ("NB", "NewBrunswick",         "Fredericton",   45.9636,  -66.6431),
        Newfoundland         ("NL", "Newfoundland",         "St. John's",    47.5615,  -52.7126),
        NorthwestTerritories ("NT", "NorthwestTerritories", "Yellowknife",   62.4540, -114.3718),
        NovaScotia           ("NS", "NovaScotia",           "Halifax",       44.6488,  -63.5752),
        Nunavut              ("NU", "Nunavut",              "Iqaluit",       63.7467,  -68.5170),
        Ontario              ("ON", "Ontario",              "Toronto",       43.6532,  -79.3832),
        PrinceEdwardIsland   ("PE", "PrinceEdwardIsland",   "Charlottetown", 46.2382,  -63.1311),
        Quebec               ("QC", "Quebec",               "Quebec City",   46.8139,  -71.2080),
        Saskatchewan         ("SK", "Saskatchewan",         "Regina",        50.4452, -104.6189),
        Yukon                ("YT", "Yukon",                "Whitehorse",    60.7212, -135.0568);

        private String code;
        private String name;
        private City capital;

        private Province(String code, String name, String capital, double lat, double lon) {
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
            return "Province";
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public static Province find(String name) {
        return (Province) Region.find(name, Province.values());
    }
}

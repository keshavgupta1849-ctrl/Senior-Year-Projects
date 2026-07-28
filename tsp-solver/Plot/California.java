package Plot;

public class California {

    public static enum County implements Region {
        
        Alameda       ("ALA", "Alameda",         "Oakland",         37.8044, -122.2711),
        Alpine        ("ALP", "Alpine",          "Markleeville",    38.6498, -119.7817),
        Amador        ("AMA", "Amador",          "Jackson",         38.3487, -120.7748),
        Butte         ("BUT", "Butte",           "Oroville",        39.5130, -121.5564),
        Calaveras     ("CAL", "Calaveras",       "San Andreas",     38.1908, -120.6801),
        Colusa        ("COL", "Colusa",          "Colusa",          39.2140, -122.0090),
        ContraCosta   ("CC",  "Contra Costa",    "Martinez",        38.0194, -122.1341),
        DelNorte      ("DN",  "Del Norte",       "Crescent City",   41.7558, -124.2026),
        ElDorado      ("ED",  "El Dorado",       "Placerville",     38.7296, -120.7985),
        Fresno        ("FRE", "Fresno",          "Fresno",          36.7378, -119.7871),
        Glenn         ("GLE", "Glenn",           "Willows",         39.5333, -122.0197),
        Humboldt      ("HUM", "Humboldt",        "Eureka",          40.8021, -124.1637),
        Imperial      ("IMP", "Imperial",        "El Centro",       32.7920, -115.5631),
        Inyo          ("INY", "Inyo",            "Independence",    36.7782, -118.2059),
        Kern          ("KER", "Kern",            "Bakersfield",     35.3733, -119.0187),
        Kings         ("KIN", "Kings",           "Hanford",         36.3275, -119.6457),
        Lake          ("LAK", "Lake",            "Lakeport",        39.0427, -122.9106),
        Lassen        ("LAS", "Lassen",          "Susanville",      40.4160, -120.6524),
        LosAngeles    ("LA",  "Los Angeles",     "Los Angeles",     34.0522, -118.2437),
        Madera        ("MAD", "Madera",          "Madera",          36.9613, -120.0607),
        Marin         ("MRN", "Marin",           "San Rafael",      37.9735, -122.5311),
        Mariposa      ("MPA", "Mariposa",        "Mariposa",        37.4848, -119.9669),
        Mendocino     ("MEN", "Mendocino",       "Ukiah",           39.1502, -123.2078),
        Merced        ("MER", "Merced",          "Merced",          37.3022, -120.4820),
        Modoc         ("MOD", "Modoc",           "Alturas",         41.4878, -120.5423),
        Mono          ("MNO", "Mono",            "Bridgeport",      38.2558, -119.2297),
        Monterey      ("MON", "Monterey",        "Salinas",         36.6777, -121.6555),
        Napa          ("NAP", "Napa",            "Napa",            38.2975, -122.2869),
        Nevada        ("NEV", "Nevada",          "Nevada City",     39.2610, -121.0161),
        Orange        ("ORA", "Orange",          "Santa Ana",       33.7455, -117.8677),
        Placer        ("PLA", "Placer",          "Auburn",          38.8974, -121.0769),
        Plumas        ("PLU", "Plumas",          "Quincy",          39.9365, -120.9475),
        Riverside     ("RIV", "Riverside",       "Riverside",       33.9806, -117.3755),
        Sacramento    ("SAC", "Sacramento",      "Sacramento",      38.5816, -121.4944),
        SanBenito     ("SBT", "San Benito",      "Hollister",       36.8520, -121.4015),
        SanBernardino ("SBE", "San Bernardino",  "San Bernardino",  34.1083, -117.2898),
        SanDiego      ("SD",  "San Diego",       "San Diego",       32.7157, -117.1611),
        SanFrancisco  ("SF",  "San Francisco",   "San Francisco",   37.7749, -122.4194),
        SanJoaquin    ("SJ",  "San Joaquin",     "Stockton",        37.9577, -121.2908),
        SanLuisObispo ("SLO", "San Luis Obispo", "San Luis Obispo", 35.2828, -120.6596),
        SanMateo      ("SM",  "San Mateo",       "Redwood City",    37.4869, -122.2364),
        SantaBarbara  ("SBA", "Santa Barbara",   "Santa Barbara",   34.4208, -119.6982),
        SantaClara    ("SCL", "Santa Clara",     "San Jose",        37.3382, -121.8863),
        SantaCruz     ("SCZ", "Santa Cruz",      "Santa Cruz",      36.9741, -122.0308),
        Shasta        ("SHA", "Shasta",          "Redding",         40.5865, -122.3917),
        Sierra        ("SIE", "Sierra",          "Downieville",     39.5349, -120.8149),
        Siskiyou      ("SIS", "Siskiyou",        "Yreka",           41.7350, -122.6367),
        Solano        ("SOL", "Solano",          "Fairfield",       38.2494, -122.0399),
        Sonoma        ("SON", "Sonoma",          "Santa Rosa",      38.4404, -122.7141),
        Stanislaus    ("STA", "Stanislaus",      "Modesto",         37.6391, -120.9969),
        Sutter        ("SUT", "Sutter",          "Yuba City",       39.1404, -121.6169),
        Tehama        ("TEH", "Tehama",          "Red Bluff",       40.1780, -122.2359),
        Trinity       ("TRI", "Trinity",         "Weaverville",     40.7426, -122.9365),
        Tulare        ("TUL", "Tulare",          "Visalia",         36.3302, -119.2921),
        Tuolumne      ("TUO", "Tuolumne",        "Sonora",          37.9848, -120.3824),
        Ventura       ("VEN", "Ventura",         "Ventura",         34.2746, -119.2290),
        Yolo          ("YOL", "Yolo",            "Woodland",        38.6785, -121.7733),
        Yuba          ("YUB", "Yuba",            "Marysville",      39.1450, -121.5914);
 
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

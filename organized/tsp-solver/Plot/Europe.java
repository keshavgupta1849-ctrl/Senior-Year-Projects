package Plot;

public class Europe {

    public enum Country implements Region {

        Albania       ("ALB", "Albania",        "Tirane",           41.3275,  19.8187),
        Andorra       ("AND", "Andorra",        "Andorra la Vella", 42.5063,  01.5218),
        Austria       ("AUT", "Austria",        "Vienna",           48.2081,  16.3713),
        Belarus       ("BLR", "Belarus",        "Minsk",            53.9006,  27.5590),
        Belgium       ("BEL", "Belgium",        "Brussels",         50.8476,  04.3572),
        Bosnia        ("BIH", "Bosnia",         "Sarajevo",         43.8563,  18.4131),
        Bulgaria      ("BGR", "Bulgaria",       "Sofia",            42.6977,  23.3219),
        Croatia       ("HRV", "Croatia",        "Zagreb",           45.8150,  15.9819),
        Cyprus        ("CYP", "Cyprus",         "Nicosia",          35.1856,  33.3823),
        Czechia       ("CZE", "Czechia",        "Prague",           50.0755,  14.4378),
        Denmark       ("DNK", "Denmark",        "Copenhagen",       55.6761,  12.5683),
        Estonia       ("EST", "Estonia",        "Tallinn",          59.4370,  24.7536),
        Finland       ("FIN", "Finland",        "Helsinki",         60.1699,  24.9384),
        France        ("FRA", "France",         "Paris",            48.8575,  02.3514),
        Germany       ("DEU", "Germany",        "Berlin",           52.5200,  13.4050),
        Greece        ("GRC", "Greece",         "Athens",           37.9838,  23.7275),
        Hungary       ("HUN", "Hungary",        "Budapest",         47.4979,  19.0402),
        Iceland       ("ISL", "Iceland",        "Reykjavik",        64.1470, -21.9408),
        Ireland       ("IRE", "Ireland",        "Dublin",           53.3498, -06.2603),
        Italy         ("ITA", "Italy",          "Rome",             41.8967,  12.4822),
        Latvia        ("LVA", "Latvia",         "Riga",             56.9677,  24.1056),
        Liechtenstein ("LIE", "Liechtenstein",  "Vaduz",            47.1411,  09.5211),
        Lithuania     ("LTU", "Lithuania",      "Vilnius",          54.6872,  25.2797),
        Luxembourg    ("LUX", "Luxembourg",     "Luxembourg",       49.6116,  06.1319),
        Macedonia     ("MKD", "Macedonia",      "Skopje",           41.9981,  21.4254),
        Malta         ("MLT", "Malta",          "Valletta",         35.8992,  14.5141),
        Moldova       ("MDA", "Moldova",        "Chisinau",         47.0105,  28.8638),
        Monaco        ("MCO", "Monaco",         "Monaco",           43.7384,  07.4246),
        Montenegro    ("MNE", "Montenegro",     "Podgorica",        42.4304,  19.2594),
        Netherlands   ("NLD", "Netherlands",    "Amsterdam",        52.3776,  04.9041),
        Norway        ("NOR", "Norway",         "Oslo",             59.9139,  10.7522),
        Poland        ("POL", "Poland",         "Warsaw",           52.2297,  21.0122),
        Portuagal     ("PRT", "Portugal",       "Lisbon",           38.7223, -09.1393),
        Romania       ("ROU", "Romania",        "Bucurest",         44.4268,  26.1025),
        Russia        ("RUS", "Russia",         "Moscow",           55.7558,  37.6173),
        SanMarino     ("SMR", "SanMarino",      "San Marino",       43.9424,  12.4578),
        Serbia        ("SRB", "Serbia",         "Belgrade",         44.8125,  20.4612),
        Slovakia      ("SVK", "Slovakia",       "Bratislava",       48.1486,  17.1077),
        Slovenia      ("SVN", "Slovenia",       "Ljubljana",        46.0569,  14.5058),
        Spain         ("ESP", "Spain",          "Madrid",           40.4167, -03.7033),
        Sweden        ("SWE", "Sweden",         "Stockholm",        59.3293,  18.0686),
        Switzerland   ("CHE", "Switzerland",    "Bern",             46.9480,  07.4474),
        Turkey        ("TUR", "Turkey",         "Ankara",           39.9334,  32.8597),
        Ukraine       ("UKR", "Ukraine",        "Kyiv",             50.4504,  30.5245),
        UnitedKingdom ("GBR", "United Kingdom", "London",           51.5072, -00.1276);

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

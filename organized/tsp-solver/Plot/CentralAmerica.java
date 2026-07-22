package Plot;

public class CentralAmerica {

    public static enum Country implements Region {
        

        Belize     ("BLZ", "Belize",      "Belmopan",       17.2510, -88.7590),
        CostaRica  ("CRI", "Costa Rica",  "San Jose",        9.9281, -84.0907),
        ElSalvador ("SLV", "El Salvador", "San Salvador",   13.6929, -89.2182),
        Guatemala  ("GTM", "Guatemala",   "Guatamala City", 14.6349, -90.5069),
        Honduras   ("HND", "Honduras",    "Tegucigalpa",    14.0650, -87.1715),
        Nicaragua  ("NIC", "Nicaragua",   "Managua",        12.1150, -86.2362),
        Panama     ("PAN", "Panama",      "Panama City",     8.9824, -79.5199);

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

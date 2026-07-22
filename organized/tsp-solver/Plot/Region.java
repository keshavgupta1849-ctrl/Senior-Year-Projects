package Plot;

public interface Region {

    // A class to represent general political subdivisions such as
    // countries, states, provinces, etc.

    public int ordinal();  // Ordinal position of this region in its country/etc.
    public String name();  // Full name of this region
    public String code();  // ISO code for this region
    public City capital(); // Capital city of this region
    public String kind();  // State, Province, Country, etc

    public static Region find(String name, Region[] regions) {
        for (Region region : regions) {
            if (name.equalsIgnoreCase(region.code())) return region;
            if (name.equalsIgnoreCase(region.name())) return region;
            if (name.equalsIgnoreCase(region.capital().name())) return region;
        }
        return null;
    }

    public static String kind(Region[] regions) {
        return regions[0].kind();
    }
}

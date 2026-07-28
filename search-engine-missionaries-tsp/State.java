// Class representing US states with capital cities and neighbor relationships
public class State {

    private final String code;    // Two-letter state code
    private final String name;    // Full state name
    private final City capital;   // Capital city of state
    private State[] neighbors;    // Array of neighboring states

    // Private constructor for state creation
    private State(String code, String name, String capital, double lat, double lon) {
        this.capital = new City(capital, this, lat, lon);
        this.code = code;
        this.name = name;
    }

    // Get state code
    public String code() { return this.code; }

    // Get state name  
    public String name() { return this.name; }

    // Get capital city
    public City capital() { return this.capital; }

    // Get array of neighboring states
    public State[] neighbors() { return this.neighbors; }

    // Check if another state is a neighbor
    public boolean isNeighbor(State other) {
        for (State neighbor : this.neighbors) {
            if (neighbor.equals(other)) return true;
        }
        return false;
    }

    // Check equality with another state by code
    public boolean equals(State other) {
        return this.code.equals(other.code);
    }

    // Override Object equals method
    @Override
    public boolean equals(Object other) {
        return other instanceof State && this.equals((State) other);
    }

    // Hash code based on state code
    @Override
    public int hashCode() {
        return this.code.hashCode();
    }

    // String representation using state code
    @Override
    public String toString() {
        return this.code;
    }

    // Find state by code, name, or capital city name
    public static State find(String name) {
        for (State state : State.states) {
            if (name.equalsIgnoreCase(state.code())) return state;
            if (name.equalsIgnoreCase(state.name())) return state;
            if (name.equalsIgnoreCase(state.capital().name())) return state;
        }
        return null;
    }

    // Set neighbors for this state
    private void neighbors(State[] neighbors) {
        this.neighbors = neighbors;
    }

    // State constants with codes, names, capitals, and coordinates
    public static final State AL = new State ("AL", "Alabama",        "Montgomery",      32.3617, -86.2792);
    public static final State AK = new State ("AK", "Alaska",         "Juneau",          58.3014, -134.422);
    public static final State AZ = new State ("AZ", "Arizona",        "Phoenix",         33.4500, -112.067);
    public static final State AR = new State ("AR", "Arkansas",       "Little Rock",     34.7361, -92.3311);
    public static final State CA = new State ("CA", "California",     "Sacramento",      38.5556, -121.469);
    public static final State CO = new State ("CO", "Colorado",       "Denver",          39.7618, -104.881);
    public static final State CT = new State ("CT", "Connecticut",    "Hartford",        41.7627, -72.6743);
    public static final State DE = new State ("DE", "Delaware",       "Dover",           39.1619, -75.5267);
    public static final State FL = new State ("FL", "Florida",        "Tallahassee",     30.4550, -84.2533);
    public static final State GA = new State ("GA", "Georgia",        "Atlanta",         33.7550, -84.3900);
    public static final State HI = new State ("HI", "Hawaii",         "Honolulu",        21.3000, -157.817);
    public static final State ID = new State ("ID", "Idaho",          "Boise",           43.6167, -116.200);
    public static final State IL = new State ("IL", "Illinois",       "Springfield",     39.6983, -89.6197);
    public static final State IN = new State ("IN", "Indiana",        "Indianapolis",    39.7910, -86.1480);
    public static final State IA = new State ("IA", "Iowa",           "Des Moines",      41.5908, -93.6208);
    public static final State KS = new State ("KS", "Kansas",         "Topeka",          39.0558, -95.6894);
    public static final State KY = new State ("KY", "Kentucky",       "Frankfort",       38.1970, -84.8630);
    public static final State LA = new State ("LA", "Louisiana",      "Baton Rouge",     30.4500, -91.1400);
    public static final State ME = new State ("ME", "Maine",          "Augusta",         44.3070, -69.7820);
    public static final State MD = new State ("MD", "Maryland",       "Annapolis",       38.9729, -76.5012);
    public static final State MA = new State ("MA", "Massachusetts",  "Boston",          42.3581, -71.0636);
    public static final State MI = new State ("MI", "Michigan",       "Lansing",         42.7336, -84.5467);
    public static final State MN = new State ("MN", "Minnesota",      "Saint Paul",      44.9442, -93.0936);
    public static final State MS = new State ("MS", "Mississippi",    "Jackson",         32.2989, -90.1847);
    public static final State MO = new State ("MO", "Missouri",       "Jefferson City",  38.5767, -92.1736);
    public static final State MT = new State ("MT", "Montana",        "Helena",          46.5958, -112.027);
    public static final State NE = new State ("NE", "Nebraska",       "Lincoln",         40.8106, -96.6803);
    public static final State NV = new State ("NV", "Nevada",         "Carson City",     39.1608, -119.754);
    public static final State NH = new State ("NH", "New Hampshire",  "Concord",         43.2067, -71.5381);
    public static final State NJ = new State ("NJ", "New Jersey",     "Trenton",         40.2237, -74.7640);
    public static final State NM = new State ("NM", "New Mexico",     "Santa Fe",        35.6672, -105.964);
    public static final State NY = new State ("NY", "New York",       "Albany",          42.6525, -73.7572);
    public static final State NC = new State ("NC", "North Carolina", "Raleigh",         35.7667, -78.6333);
    public static final State ND = new State ("ND", "North Dakota",   "Bismarck",        46.8133, -100.779);
    public static final State OH = new State ("OH", "Ohio",           "Columbus",        39.9833, -82.9833);
    public static final State OK = new State ("OK", "Oklahoma",       "Oklahoma City",   35.4822, -97.5350);
    public static final State OR = new State ("OR", "Oregon",         "Salem",           44.9308, -123.029);
    public static final State PA = new State ("PA", "Pennsylvania",   "Harrisburg",      40.2697, -76.8756);
    public static final State RI = new State ("RI", "Rhode Island",   "Providence",      41.8236, -71.4222);
    public static final State SC = new State ("SC", "South Carolina", "Columbia",        34.0006, -81.0347);
    public static final State SD = new State ("SD", "South Dakota",   "Pierre",          44.3680, -100.336);
    public static final State TN = new State ("TN", "Tennessee",      "Nashville",       36.1667, -86.7833);
    public static final State TX = new State ("TX", "Texas",          "Austin",          30.2500, -97.7500);
    public static final State UT = new State ("UT", "Utah",           "Salt Lake City",  40.7500, -111.883);
    public static final State VT = new State ("VT", "Vermont",        "Montpelier",      44.2597, -72.5750);
    public static final State VA = new State ("VA", "Virginia",       "Richmond",        37.5333, -77.4667);
    public static final State WA = new State ("WA", "Washington",     "Olympia",         47.0425, -122.893);
    public static final State WV = new State ("WV", "West Virginia",  "Charleston",      38.3472, -81.6333);
    public static final State WI = new State ("WI", "Wisconsin",      "Madison",         43.0667, -89.4000);
    public static final State WY = new State ("WY", "Wyoming",        "Cheyenne",        41.1456, -104.802);

    // Full name constants for convenience
    public static final State Alabama = AL;
    public static final State Alaska = AK;
    public static final State Arizona = AZ;
    public static final State Arkansas = AR;
    public static final State California = CA;
    public static final State Colorado = CO;
    public static final State Connecticut = CT;
    public static final State Delaware = DE;
    public static final State Florida = FL;
    public static final State Georgia = GA;
    public static final State Hawaii = HI;
    public static final State Idaho = ID;
    public static final State Illinois = IL;
    public static final State Indiana = IN;
    public static final State Iowa = IA;
    public static final State Kansas = KS;
    public static final State Kentucky = KY;
    public static final State Louisiana = LA;
    public static final State Maine = ME;
    public static final State Maryland = MD;
    public static final State Massachusetts = MA;
    public static final State Michigan = MI;
    public static final State Minnesota = MN;
    public static final State Mississippi = MS;
    public static final State Missouri = MO;
    public static final State Montana = MT;
    public static final State Nebraska = NE;
    public static final State Nevada = NV;
    public static final State NewHampshire = NH;
    public static final State NewJersey = NJ;
    public static final State NewMexico = NM;
    public static final State NewYork = NY;
    public static final State NorthCarolina = NC;
    public static final State NorthDakota = ND;
    public static final State Ohio = OH;
    public static final State Oklahoma = OK;
    public static final State Oregon = OR;
    public static final State Pennsylvania = PA;
    public static final State RhodeIsland = RI;
    public static final State SouthCarolina = SC;
    public static final State SouthDakota = SD;
    public static final State Tennessee = TN;
    public static final State Texas = TX;
    public static final State Utah = UT;
    public static final State Vermont = VT;
    public static final State Virginia = VA;
    public static final State Washington = WA;
    public static final State WestVirginia = WV;
    public static final State Wisconsin = WI;
    public static final State Wyoming = WY;

    // Array of all states
    public static final State[] states = {
            AL, AK, AZ, AR, CA, CO, CT, DE, FL, GA,
            HI, ID, IL, IN, IA, KS, KY, LA, ME, MD,
            MA, MI, MN, MS, MO, MT, NE, NV, NH, NJ,
            NM, NY, NC, ND, OH, OK, OR, PA, RI, SC,
            SD, TN, TX, UT, VT, VA, WA, WV, WI, WY
    };

    // Static initializer to set up neighbor relationships
    static {
        AL.neighbors(new State[]{GA, TN, MS, FL});
        AK.neighbors(new State[]{});
        AZ.neighbors(new State[]{NM, UT, NV, CA});
        AR.neighbors(new State[]{MS, TN, MO, OK, TX, LA});
        CA.neighbors(new State[]{AZ, NV, OR});
        CO.neighbors(new State[]{KS, NE, WY, UT, NM, OK});
        CT.neighbors(new State[]{RI, MA, NY});
        DE.neighbors(new State[]{NJ, PA, MD});
        FL.neighbors(new State[]{GA, AL});
        GA.neighbors(new State[]{SC, NC, TN, AL, FL});
        HI.neighbors(new State[]{});
        ID.neighbors(new State[]{WY, MT, WA, OR, NV, UT});
        IL.neighbors(new State[]{IN, WI, IA, MO, KY});
        IN.neighbors(new State[]{OH, MI, IL, KY});
        IA.neighbors(new State[]{IL, WI, MN, SD, NE, MO});
        KS.neighbors(new State[]{MO, NE, CO, OK});
        KY.neighbors(new State[]{VA, WV, OH, IN, IL, MO, TN});
        LA.neighbors(new State[]{MS, AR, TX});
        ME.neighbors(new State[]{NH});
        MD.neighbors(new State[]{DE, PA, WV, VA});
        MA.neighbors(new State[]{NH, VT, NY, CT, RI});
        MI.neighbors(new State[]{WI, IN, OH});
        MN.neighbors(new State[]{WI, ND, SD, IA});
        MS.neighbors(new State[]{AL, TN, AR, LA});
        MO.neighbors(new State[]{KY, TN, IL, IA, NE, KS, OK, AR});
        MT.neighbors(new State[]{SD, ND, ID, WY});
        NE.neighbors(new State[]{MO, IA, SD, WY, CO, KS});
        NV.neighbors(new State[]{AZ, UT, ID, OR, CA});
        NH.neighbors(new State[]{ME, VT, MA});
        NJ.neighbors(new State[]{NY, PA, DE});
        NM.neighbors(new State[]{TX, OK, CO, AZ});
        NY.neighbors(new State[]{CT, MA, VT, PA, NJ});
        NC.neighbors(new State[]{VA, TN, GA, SC});
        ND.neighbors(new State[]{MN, MT, SD});
        OH.neighbors(new State[]{WV, PA, MI, IN, KY});
        OK.neighbors(new State[]{AR, MO, KS, CO, NM, TX});
        OR.neighbors(new State[]{ID, WA, CA, NV});
        PA.neighbors(new State[]{NJ, NY, OH, WV, MD, DE});
        RI.neighbors(new State[]{MA, CT});
        SC.neighbors(new State[]{NC, GA});
        SD.neighbors(new State[]{IA, MN, ND, MT, WY, NE});
        TN.neighbors(new State[]{NC, VA, KY, MO, AR, MS, AL, GA});
        TX.neighbors(new State[]{LA, AR, OK, NM});
        UT.neighbors(new State[]{CO, WY, ID, NV, AZ});
        VT.neighbors(new State[]{NH, NY, MA});
        VA.neighbors(new State[]{MD, WV, KY, TN, NC});
        WA.neighbors(new State[]{ID, OR});
        WV.neighbors(new State[]{VA, MD, PA, OH, KY});
        WI.neighbors(new State[]{MI, MN, IA, IL});
        WY.neighbors(new State[]{NE, SD, MT, ID, UT, CO});
    }
}
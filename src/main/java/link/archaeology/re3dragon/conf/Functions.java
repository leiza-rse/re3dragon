package link.archaeology.re3dragon.conf;

import org.json.simple.JSONArray;

/**
 * functions that could be needed
 *
 * @author Florian Thiery
 */
public class Functions {

    private static final double rho = Math.PI / 180;
    private static final double r_earth = 6378.388; // Hayford-Ellipsoid, 1910/24, Äquatorradius

    /**
     * round doubles
     *
     * @param value
     * @param frac
     * @return
     */
    public static double round(final double value, final int frac) {
        return Math.round(Math.pow(10.0, frac) * value) / Math.pow(10.0, frac);
    }

    /**
     * calculate distance in kilometers of two WGS84 points
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return
     */
    public static double getKilometers(double lat1, double lon1, double lat2, double lon2) {
        lat1 = lat1 * rho;
        lon1 = lon1 * rho;
        lat2 = lat2 * rho;
        lon2 = lon2 * rho;
        // http://www.kompf.de/gps/distcalc.html
        return r_earth * Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1));
    }

    /**
     * calculate centroid of a WGS84 bounding box
     *
     * @param lowerrightLon
     * @param upperleftLon
     * @param upperleftLat
     * @param upperrightLat
     * @return
     */
    public static JSONArray bboxCenter(Double lowerrightLon, Double upperleftLon, Double upperleftLat, Double upperrightLat) {
        JSONArray result = new JSONArray();
        Double lonDiff = upperleftLon + (lowerrightLon - upperleftLon) / 2;
        Double latDiff = upperrightLat + (upperleftLat - upperrightLat) / 2;
        result.add(round(lonDiff, 5));
        result.add(round(latDiff, 5));
        return result;
    }

}

package link.archaeology.re3dragon.conf;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GeoJSONFeature {

    private String type;
    private double longitude;
    private double latitude;
    private int epsg;

    public GeoJSONFeature(String type, double longitude, double latitude, int epsg) {
        this.type = type;
        this.longitude = longitude;
        this.latitude = latitude;
        this.epsg = epsg;
    }

    public JSONObject getGeoJSONFeaturePoint() {
        JSONObject point = new JSONObject();
        point.put("type", type);
        JSONArray parr = new JSONArray();
        parr.add(longitude);
        parr.add(latitude);
        point.put("coordinates", parr);
        point.put("epsg", epsg);
        return point;
    }

}

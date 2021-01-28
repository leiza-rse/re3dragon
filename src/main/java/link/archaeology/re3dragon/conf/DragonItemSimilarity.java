package link.archaeology.re3dragon.conf;

import org.json.simple.JSONObject;

public class DragonItemSimilarity extends DragonItem {

    private JSONObject DRAGON = new JSONObject();  
    private String searchString = "";
    private String lairString = "";
    private double levenshtein = -1.0;
    private double normalizedlevenshtein = -1.0;
    private double dameraulevenshtein = -1.0;
    private double jarowinkler = -1.0;
    private String bboxcenter = "";
    private String point = "";
    private double distance = -1.0;

    public DragonItemSimilarity(String URL) {
        super(URL);
        /*
        // get prefName
        double levenshtein = StringSimilarity.Levenshtein(searchString, prefName);
        double normalizedlevenshtein = StringSimilarity.NormalizedLevenshtein(searchString, prefName);
        double dameraulevenshtein = StringSimilarity.Damerau(searchString, prefName);
        double jarowinkler = StringSimilarity.JaroWinkler(searchString, prefName);
        feature.setPropertiesStringSimilarity(levenshtein, normalizedlevenshtein, dameraulevenshtein, jarowinkler, searchString, prefName);
            public void setPropertiesStringSimilarity(Double levenshtein, Double normalizedlevenshtein, Double dameraulevenshtein, Double jarowinkler, String searchString, String gazetteerString) {
            JSONObject properties = (JSONObject) super.get("properties");
            JSONObject similarity = new JSONObject();
            similarity.put("searchString", searchString);
            similarity.put("gazetteerString", gazetteerString);
            similarity.put("levenshtein", Functions.round(levenshtein, 2));
            similarity.put("normalizedlevenshtein", Functions.round(normalizedlevenshtein, 2));
            similarity.put("dameraulevenshtein", Functions.round(dameraulevenshtein, 2));
            similarity.put("jarowinkler", Functions.round(jarowinkler, 2));
            properties.put("similarity", similarity);
            super.put("properties", properties);
        }

        // get distance
                JSONArray bbox = Functions.bboxCenter(Double.parseDouble(lowerrightLon), Double.parseDouble(upperleftLon), Double.parseDouble(upperleftLat), Double.parseDouble(upperrightLat));
                Double bboxlon = (Double) bbox.get(1);
                Double bboxlat = (Double) bbox.get(0);
                feature.setPropertiesDistanceSimilarity(bboxlon, bboxlat, lat, lon);
     public void setPropertiesDistanceSimilarity(double lat1, double lon1, double lat2, double lon2) {
        JSONObject properties = (JSONObject) super.get("properties");
        JSONObject similarity = new JSONObject();
        JSONArray bboxcenter = new JSONArray();
        JSONArray point = new JSONArray();
        bboxcenter.add(lon1);
        bboxcenter.add(lat1);
        point.add(lon2);
        point.add(lat2);
        similarity.put("bboxcenter", bboxcenter);
        similarity.put("point", point);
        similarity.put("distance", Functions.round(Functions.getKilometers(lat1, lon1, lat2, lon2), 2));
        properties.put("similarity", similarity);
        super.put("properties", properties);
    }
         */
    }

    public JSONObject getDragonItem() {
        DRAGON = super.getDragonItem();
        JSONObject similarity = new JSONObject();
        similarity.put("searchstring", getSearchString());
        similarity.put("lairstring", geLairString());
        similarity.put("levenshtein", Functions.round(getLevenshtein(), 2));
        similarity.put("normalizedlevenshtein", Functions.round(getNormalizedLevenshtein(), 2));
        similarity.put("dameraulevenshtein", Functions.round(getDamerauLevenshtein(), 2));
        similarity.put("jarowinkler", Functions.round(getJaroWinkler(), 2));
        //similarity.put("bboxcenter", getBBOXCenter());
        //similarity.put("point", getPoint());
        //similarity.put("distance", Functions.round(Functions.getKilometers(lat1, lon1, lat2, lon2), 2));
        DRAGON.put("similarity", similarity);
        return DRAGON;
    }

    public void setSearchString(String s) {
        searchString = s;
    }

    public String getSearchString() {
        return searchString;
    }
    
    public void setLairString(String s) {
        lairString = s;
    }

    public String geLairString() {
        return lairString;
    }
    
    public void setLevenshtein(Double d) {
        levenshtein = d;
    }

    public Double getLevenshtein() {
        return levenshtein;
    }
    
    public void setNormalizedLevenshtein(Double d) {
        normalizedlevenshtein = d;
    }

    public Double getNormalizedLevenshtein() {
        return normalizedlevenshtein;
    }
    
    public void setDamerauLevenshtein(Double d) {
        dameraulevenshtein = d;
    }

    public Double getDamerauLevenshtein() {
        return dameraulevenshtein;
    }
    
    public void setJaroWinkler(Double d) {
        jarowinkler = d;
    }

    public Double getJaroWinkler() {
        return jarowinkler;
    }
    
    public void setBBOXCenter(String s) {
        bboxcenter = s;
    }

    public String getBBOXCenter() {
        return bboxcenter;
    }
    
    public void setPoint(String s) {
        point = s;
    }

    public String getPoint() {
        return point;
    }
    
    public void setDistance(Double d) {
        distance = d;
    }

    public Double getDistance() {
        return distance;
    }

}

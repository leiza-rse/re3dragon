package link.archaeology.re3dragon.conf;

import java.io.IOException;
import java.util.HashMap;
import link.archaeology.re3dragon.action.Lair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.mainzed.re3dragon.exceptions.ResourceNotAvailableException;
import org.mainzed.re3dragon.exceptions.RetcatException;

public class DragonItemSimilarity extends DragonItem {

    private JSONObject DRAGON = new JSONObject();
    private String url = "";
    private String label = "";
    private String labellang = "";
    private String desc = "";
    private String desclang = "";
    private HashMap<String, String> labels = new HashMap<String, String>();
    private HashMap<String, String> descriptions = new HashMap<String, String>();
    private HashMap<String, String> broader = new HashMap<String, String>();
    private HashMap<String, String> narrower = new HashMap<String, String>();
    private JSONObject LAIR = new JSONObject();

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
        DRAGON.putIfAbsent("uri", getURL());
        // prefLabel
        JSONObject labellang = new JSONObject();
        labellang.put("label", getLabel());
        labellang.put("lang", getLanguage());
        DRAGON.putIfAbsent("preflabel", labellang);
        // prefDesc
        JSONObject desclabellang = new JSONObject();
        desclabellang.put("label", getDescLabel());
        desclabellang.put("lang", getDescLanguage());
        DRAGON.putIfAbsent("prefdesc", desclabellang);
        // labels
        JSONArray labels = new JSONArray();
        HashMap labelsHM = getLabels();
        labelsHM.forEach((k, v) -> {
            JSONObject tmp = new JSONObject();
            tmp.put("label", k);
            tmp.put("lang", v);
            labels.add(tmp);
        });
        DRAGON.put("labels", labels);
        // descriptions
        JSONArray descriptions = new JSONArray();
        HashMap descriptionsHM = getDescriptions();
        descriptionsHM.forEach((k, v) -> {
            JSONObject tmp = new JSONObject();
            tmp.put("label", k);
            tmp.put("lang", v);
            descriptions.add(tmp);
        });
        DRAGON.put("descriptions", descriptions);
        // broader terms
        JSONArray broaderTerms = new JSONArray();
        HashMap broaderTermsHM = getBroaderTerms();
        broaderTermsHM.forEach((k, v) -> {
            JSONObject tmp = new JSONObject();
            tmp.put("uri", k);
            tmp.put("label", v);
            tmp.put("lang", "en");
            broaderTerms.add(tmp);
        });
        DRAGON.put("broader", broaderTerms);
        // narrower terms
        JSONArray narrowerTerms = new JSONArray();
        HashMap narrowerTermsHM = getNarrowerTerms();
        narrowerTermsHM.forEach((k, v) -> {
            JSONObject tmp = new JSONObject();
            tmp.put("uri", k);
            tmp.put("label", v);
            tmp.put("lang", "en");
            narrowerTerms.add(tmp);
        });
        DRAGON.put("narrower", narrowerTerms);
        // lair info
        DRAGON.put("lair", LAIR);
        return DRAGON;
    }

    // URL
    public void setURL(String url) {
        this.url = url;
    }

    public String getURL() {
        return url;
    }

    // prefLabel + Language
    public void setLabelLang(String label, String lang) {
        this.label = label;
        this.labellang = lang;
    }

    public String getLabel() {
        return label;
    }

    public String getLanguage() {
        return labellang;
    }

    // prefDescLabel + Language
    public void setDescLabelLang(String label, String lang) {
        this.desc = label;
        this.desclang = lang;
    }

    public String getDescLabel() {
        return desc;
    }

    public String getDescLanguage() {
        return desclang;
    }

    // Labels
    public void setLabel(String label, String lang) {
        this.labels.putIfAbsent(label, lang);
    }

    public HashMap getLabels() {
        return labels;
    }

    // Descriptions
    public void setDescriptions(String desc, String lang) {
        this.descriptions.putIfAbsent(desc, lang);
    }

    public HashMap getDescriptions() {
        return descriptions;
    }

    // Broader Terms
    public void setBroaderTerms(String uri, String label) {
        this.broader.putIfAbsent(uri, label);
    }

    public HashMap getBroaderTerms() {
        return broader;
    }

    // Narrower Terms
    public void setNarrowerTerms(String uri, String label) {
        this.narrower.putIfAbsent(uri, label);
    }

    public HashMap getNarrowerTerms() {
        return narrower;
    }

    // Lair
    public void setLairInfo(String id) throws IOException, ResourceNotAvailableException, ParseException, RetcatException {
        LAIR = Lair.shortinfo(id);
    }

    public JSONObject getLairInfo() {
        return LAIR;
    }

}

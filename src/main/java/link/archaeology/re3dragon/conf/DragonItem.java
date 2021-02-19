package link.archaeology.re3dragon.conf;

import java.io.IOException;
import java.util.HashMap;
import link.archaeology.re3dragon.action.Lair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.mainzed.re3dragon.exceptions.ResourceNotAvailableException;
import org.mainzed.re3dragon.exceptions.RetcatException;

public class DragonItem {

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
    private JSONObject location = new JSONObject();
    private JSONArray depiction = new JSONArray();
    private String startDate = "";
    private String endDate = "";
    private JSONObject LAIR = new JSONObject();

    /**
     * Create Dragon Item
     *
     * @param URI
     */
    public DragonItem(String URI) {
        url = URI;
    }

    /**
     * Get full Dragon Item
     *
     * @return Dragon Item
     */
    public JSONObject getDragonItem() {
        // mapping to JSKOS: https://gbv.github.io/jskos/jskos.html#item

        // uri
        DRAGON.putIfAbsent("uri", getURL());

        // type
        if (getURL().contains("iconclass") || getURL().contains("getty") || getURL().contains("gnd")) {
            JSONArray typeArr = new JSONArray();
            typeArr.add("http://www.w3.org/2004/02/skos/core#Concept");
            DRAGON.putIfAbsent("type", typeArr);
        } else if (getURL().contains("wikidata")) {
            JSONArray typeArr = new JSONArray();
            typeArr.add("http://wikiba.se/ontology#Item");
            DRAGON.putIfAbsent("type", typeArr);
        }

        // display information
        // displayLabel
        JSONObject labellang = new JSONObject();
        labellang.put(getLanguage(), getLabel());
        DRAGON.putIfAbsent("displayLabel", labellang);
        // displayDesc
        JSONObject desclabellang = new JSONObject();
        desclabellang.put(getDescLanguage(), getDescLabel());
        DRAGON.putIfAbsent("displayDesc", desclabellang);

        // labels and descriptions
        // prefLabel
        JSONObject prefLabel = new JSONObject();
        HashMap labelsHM = getLabels();
        JSONArray unknown = new JSONArray();
        labelsHM.forEach((k, v) -> {
            if (v.equals("-")) {
                unknown.add(k);
            }
            if (unknown.size() > 0) {
                prefLabel.put("-", unknown);
            } else {
                prefLabel.put(v, k);
            }
        });
        DRAGON.put("prefLabel", prefLabel);
        // scopeNote
        JSONObject scopeNote = new JSONObject();
        HashMap descriptionsHM = getDescriptions();
        descriptionsHM.forEach((k, v) -> {
            scopeNote.put(v, k);
        });
        DRAGON.put("scopeNote", scopeNote);

        // hierarchical / semantical relations
        // broader terms
        JSONArray broaderTerms = new JSONArray();
        HashMap broaderTermsHM = getBroaderTerms();
        broaderTermsHM.forEach((k, v) -> {
            JSONObject tmp3 = new JSONObject();
            tmp3.put("uri", k);
            JSONObject tmp3_1 = new JSONObject();
            if (getURL().contains("gnd")) {
                tmp3_1.put("de", v);
            } else {
                tmp3_1.put("en", v);
            }
            tmp3.put("prefLabel", tmp3_1);
            broaderTerms.add(tmp3);
        });
        DRAGON.put("broader", broaderTerms);
        // narrower terms
        JSONArray narrowerTerms = new JSONArray();
        HashMap narrowerTermsHM = getNarrowerTerms();
        narrowerTermsHM.forEach((k, v) -> {
            JSONObject tmp4 = new JSONObject();
            tmp4.put("uri", k);
            JSONObject tmp4_1 = new JSONObject();
            if (getURL().contains("gnd")) {
                tmp4_1.put("de", v);
            } else {
                tmp4_1.put("en", v);
            }
            tmp4.put("prefLabel", tmp4_1);
            narrowerTerms.add(tmp4);
        });
        DRAGON.put("narrower", narrowerTerms);

        // geo, time and images
        // location
        DRAGON.put("location", getLocation());
        // date
        DRAGON.put("startDate", getStartDate());
        DRAGON.put("endDate", getEndDate());
        // depiction
        DRAGON.put("depiction", getDepiction());

        // lair info
        DRAGON.put("lair", LAIR);

        // return
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
        this.labels.put(label, lang);
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

    // Location
    public void setLocation(JSONObject location) {
        this.location = location;
    }

    public JSONObject getLocation() {
        return location;
    }

    // Date
    public void setStartDate(String date) {
        this.startDate = date;
    }

    public void setEndDate(String date) {
        this.endDate = date;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    // Depiction
    public void setDepiction(String url) {
        this.depiction.add(url);
    }

    public JSONArray getDepiction() {
        return depiction;
    }

    // Lair
    public void setLairInfo(String id) throws IOException, ResourceNotAvailableException, ParseException, RetcatException {
        LAIR = Lair.shortinfo(id);
    }

    public JSONObject getLairInfo() {
        return LAIR;
    }

}

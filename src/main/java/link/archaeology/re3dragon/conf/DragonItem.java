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
    private JSONObject LAIR = new JSONObject();

    public DragonItem(String URL) {
        url = URL;
    }

    public JSONObject getDragonItem() {
        // mapping to JSKOS: https://gbv.github.io/jskos/jskos.html#concept
        // uri
        DRAGON.putIfAbsent("uri", getURL());
        // type
        if (getURL().contains("iconclass")) {
            JSONArray typeArr = new JSONArray();
            typeArr.add("http://www.w3.org/2004/02/skos/core#Concept");
            DRAGON.putIfAbsent("type", typeArr);
        }
        // displayLabel
        JSONObject labellang = new JSONObject();
        labellang.put(getLanguage(), getLabel());
        DRAGON.putIfAbsent("displayLabel", labellang);
        // displayDesc
        JSONObject desclabellang = new JSONObject();
        desclabellang.put(getDescLanguage(), getDescLabel());
        DRAGON.putIfAbsent("displayDesc", desclabellang);
        // prefLabel
        JSONObject prefLabel = new JSONObject();
        HashMap labelsHM = getLabels();
        labelsHM.forEach((k, v) -> {
            prefLabel.put(v, k);
        });
        DRAGON.put("prefLabel", prefLabel);
        // scopeNote
        JSONObject scopeNote = new JSONObject();
        HashMap descriptionsHM = getDescriptions();
        descriptionsHM.forEach((k, v) -> {
            scopeNote.put(v, k);
        });
        DRAGON.put("scopeNote", scopeNote);
        // broader terms
        JSONArray broaderTerms = new JSONArray();
        HashMap broaderTermsHM = getBroaderTerms();
        JSONObject tmp3 = new JSONObject();
        broaderTermsHM.forEach((k, v) -> {
            tmp3.put("uri", k);
            JSONObject tmp3_1 = new JSONObject();
            tmp3_1.put("en", v);
            tmp3.put("prefLabel", tmp3_1);
        });
        broaderTerms.add(tmp3);
        DRAGON.put("broader", broaderTerms);
        // narrower terms
        JSONArray narrowerTerms = new JSONArray();
        HashMap narrowerTermsHM = getNarrowerTerms();
        JSONObject tmp4 = new JSONObject();
        narrowerTermsHM.forEach((k, v) -> {
            tmp4.put("uri", k);
            JSONObject tmp4_1 = new JSONObject();
            tmp4_1.put("en", v);
            tmp4.put("prefLabel", tmp4_1);
        });
        narrowerTerms.add(tmp4);
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

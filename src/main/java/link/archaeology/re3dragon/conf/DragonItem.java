package link.archaeology.re3dragon.conf;

import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
    private String scheme = "";
    private String type = "";
    private String legaltype = "";
    private String quality = "";

    public DragonItem(String URL) {
        url = URL;
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
        // set additional information
        DRAGON.put("scheme", getScheme());
        DRAGON.put("type", getType());
        DRAGON.put("legaltype", getLegalType());
        DRAGON.put("quality", getQuality());
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

    // more descriptions
    public void setScheme(String scheme) {
        this.scheme = scheme;
    }
    
    public void setType(String type) {
        this.type = type;
    }

    public void setLegalType(String legaltype) {
        this.legaltype = legaltype;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getScheme() {
        return scheme;
    }
    
    public String getType() {
        return type;
    }

    public String getLegalType() {
        return legaltype;
    }

    public String getQuality() {
        return quality;
    }

}

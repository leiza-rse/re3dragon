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

    /*private String id = "";
    private String schemeTitle = "";
    private String schemeURI = "";
    private HashSet<HashMap<String, String>> labels = new HashSet<HashMap<String, String>>();
    private HashSet<HashMap<String, String>> altlabels = new HashSet<HashMap<String, String>>();
    private HashSet<HashMap<String, String>> broaderTerms = new HashSet<HashMap<String, String>>();
    private HashSet<HashMap<String, String>> narrowerTerms = new HashSet<HashMap<String, String>>();
    private String type = "";
    private String quality = "";
    private String group = "";
    private String creator = "";
    private String orcid = "";*/

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

    
    /*public HashSet<HashMap<String, String>> getNarrowerTerms() {
        return narrowerTerms;
    }

    public HashSet<HashMap<String, String>> getBroaderTerms() {
        return broaderTerms;
    }

    public void setBroaderTerm(HashMap<String, String> broader) {
        this.broaderTerms.add(broader);
    }

    public HashSet<HashMap<String, String>> getNarrowerTerms() {
        return narrowerTerms;
    }

    public void setNarrowerTerm(HashMap<String, String> narrower) {
        this.narrowerTerms.add(narrower);
    }

    public String getSchemeTitle() {
        return schemeTitle;
    }

    public void setSchemeTitle(String schemeTitle) {
        this.schemeTitle = schemeTitle;
    }

    public HashSet<String> getLabels() {
        return labels;
    }

    public void setLabel(String label) {
        this.labels.add(label);
    }

    public HashSet<String> getAltLabels() {
        return altLabels;
    }

    public void setAltLabel(String label) {
        this.altLabels.add(label);
    }

    public HashSet<String> getDescriptions() {
        return descriptions;
    }

    public void setDescription(String description) {
        this.descriptions.add(description);
    }

    public String getSchemeURI() {
        return schemeURI;
    }

    public void setSchemeURI(String schemeURI) {
        this.schemeURI = schemeURI;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }*/

}

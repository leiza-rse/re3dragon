package link.archaeology.re3dragon.conf;

import org.json.simple.JSONObject;

public class DragonLairItem {

    private JSONObject DRAGONLAIR = new JSONObject();
    private String id = "";
    private String label = "";
    private String legaltype = "";
    private String type = "";
    private String quality = "";
    private String group = "";
    private String wikidataitem = "";

    public DragonLairItem(String ID) {
        id = ID;
    }

    public JSONObject getDragonLairItem() {
        DRAGONLAIR.putIfAbsent("id", getID());
        // set additional information
        DRAGONLAIR.put("scheme", getScheme());
        DRAGONLAIR.put("type", getType());
        DRAGONLAIR.put("legaltype", getLegalType());
        DRAGONLAIR.put("quality", getQuality());
        DRAGONLAIR.put("group", getGroup());
        DRAGONLAIR.put("wikidata", getWikidataItem());
        return DRAGONLAIR;
    }

    // URL
    public void setID(String ID) {
        this.id = ID;
    }

    public String getID() {
        return id;
    }

    // more descriptions
    public void setScheme(String scheme) {
        this.label = scheme;
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

    public void setGroup(String group) {
        this.group = group;

    }

    public void setWikidataItem(String wikidataitem) {
        this.wikidataitem = wikidataitem;
    }

    public String getScheme() {
        return label;
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

    public String getGroup() {
        return group;
    }

    public String getWikidataItem() {
        return wikidataitem;
    }

}

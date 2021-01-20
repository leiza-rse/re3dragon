package org.mainzed.re3dragon.rest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import link.archaeology.re3dragon.conf.DragonItem;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.mainzed.re3dragon.config.RetcatUtils;
import org.mainzed.re3dragon.config.SuggestionItem;
import org.mainzed.re3dragon.exceptions.ResourceNotAvailableException;
import org.mainzed.re3dragon.exceptions.RetcatException;

public class IconClass {

    public static JSONObject info(String url) throws IOException, ResourceNotAvailableException, ParseException, RetcatException {
        try {
            // query sparql endpoint
            String sparqlendpoint = "https://api.data.netwerkdigitaalerfgoed.nl/datasets/rkd/iconclass/services/iconclass/sparql";
            String sparql = "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> ";
            sparql += "SELECT * { ";
            sparql += "<" + url.replace("(", "%28").replace(")", "%29") + "> skos:prefLabel ?label. ";
            sparql += "FILTER(LANGMATCHES(LANG(?label), \"en\"))";
            sparql += "<" + url.replace("(", "%28").replace(")", "%29") + "> skos:prefLabel ?labels. ";
            sparql += "<" + url.replace("(", "%28").replace(")", "%29") + "> <http://purl.org/dc/elements/1.1/subject> ?subjects. ";
            sparql += " }";
            URL obj = new URL(sparqlendpoint);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept", "application/sparql-results+json");
            String urlParameters = "query=" + sparql;
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF8"));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            // parse SPARQL results json
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(response.toString());
            JSONObject resultsObject = (JSONObject) jsonObject.get("results");
            JSONArray bindingsArray = (JSONArray) resultsObject.get("bindings");
            // create object
            DragonItem DRAGON = new DragonItem(url);
            // set label
            for (Object element : bindingsArray) {
                JSONObject tmpElement = (JSONObject) element;
                JSONObject label = (JSONObject) tmpElement.get("label");
                String labelValue = (String) label.get("value");
                String labelLang = (String) label.get("xml:lang");
                DRAGON.setLabelLang(labelValue, labelLang);
            }
            return DRAGON.getDragonItem();
            /*// create unique list of ids
            for (Object element : bindingsArray) {
                JSONObject tmpElement = (JSONObject) element;
                JSONObject prefLabel = (JSONObject) tmpElement.get("prefLabel");
                String labelValue = (String) prefLabel.get("value");
                String labelLang = (String) prefLabel.get("xml:lang");
                jsonOut.put("label", labelValue);
                jsonOut.put("lang", labelLang);
            }
            for (Object element : bindingsArray) {
                JSONObject tmpElement = (JSONObject) element;
                JSONObject scopeNote = (JSONObject) tmpElement.get("scopeNote");
                String descValue = "";
                if (scopeNote != null) {
                    descValue = (String) scopeNote.get("value");
                }
                jsonOut.put("description", descValue);
            }
            for (Object element : bindingsArray) {
                JSONObject tmpElement = (JSONObject) element;
                JSONObject scopeNote = (JSONObject) tmpElement.get("schemeTitle");
                String descValue = (String) scopeNote.get("value");
                jsonOut.put("scheme", descValue);
            }
            HashMap<String, String> hmBroader = new HashMap();
            for (Object element : bindingsArray) {
                JSONObject tmpElement = (JSONObject) element;
                JSONObject bpObj = (JSONObject) tmpElement.get("BroaderPreferred");
                JSONObject bptObj = (JSONObject) tmpElement.get("BroaderPreferredTerm");
                if (bpObj != null) {
                    String bp = (String) bpObj.get("value");
                    String bpt = (String) bptObj.get("value");
                    hmBroader.put(bpt, bp);
                }
            }
            JSONArray tmpArrayBroader = new JSONArray();
            Iterator itB = hmBroader.entrySet().iterator();
            while (itB.hasNext()) {
                Map.Entry pair = (Map.Entry) itB.next();
                JSONObject tmpObject = new JSONObject();
                tmpObject.put("label", pair.getKey());
                tmpObject.put("uri", pair.getValue());
                tmpArrayBroader.add(tmpObject);
                itB.remove();
            }
            jsonOut.put("broaderTerms", tmpArrayBroader);
            HashMap<String, String> hmNarrower = new HashMap();
            for (Object element : bindingsArray) {
                JSONObject tmpElement = (JSONObject) element;
                JSONObject npObj = (JSONObject) tmpElement.get("NarrowerPreferred");
                JSONObject nptObj = (JSONObject) tmpElement.get("NarrowerPreferredTerm");
                if (npObj != null) {
                    String np = (String) npObj.get("value");
                    String npt = (String) nptObj.get("value");
                    hmNarrower.put(npt, np);
                }
            }
            JSONArray tmpArrayNarrower = new JSONArray();
            Iterator itN = hmNarrower.entrySet().iterator();
            while (itN.hasNext()) {
                Map.Entry pair = (Map.Entry) itN.next();
                JSONObject tmpObject = new JSONObject();
                tmpObject.put("label", pair.getKey());
                tmpObject.put("uri", pair.getValue());
                tmpArrayNarrower.add(tmpObject);
                itN.remove();
            }
            jsonOut.put("narrowerTerms", tmpArrayNarrower);
            // get retcat info
            String type = "getty";
            String quality = "";
            String group = "";
            /*for (RetcatItem item : RetcatItems.getReferenceThesaurusCatalogue()) {
                if (item.getType().equals(type)) {
                    quality = item.getQuality();
                    group = item.getGroup();
                }
            }
            jsonOut.put("type", type);
            jsonOut.put("quality", quality);
            jsonOut.put("group", group);
            jsonOut.put("uri", url);
            if (jsonOut.get("label") != null && !jsonOut.get("label").equals("")) {
                return jsonOut;
            } else {
                throw new RetcatException("no label for this uri available");
            }*/
        } catch (Exception e) {
            return new JSONObject();
        }
    }

}

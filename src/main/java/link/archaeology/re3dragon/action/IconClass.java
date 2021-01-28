package link.archaeology.re3dragon.action;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import link.archaeology.re3dragon.conf.DragonItem;
import link.archaeology.re3dragon.conf.DragonItemSimilarity;
import link.archaeology.re3dragon.conf.StringSimilarity;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.mainzed.re3dragon.exceptions.ResourceNotAvailableException;
import org.mainzed.re3dragon.exceptions.RetcatException;
import org.mainzed.re3dragon.log.Logging;

public class IconClass {

    public static JSONObject item(String url) throws IOException, ResourceNotAvailableException, ParseException, RetcatException {
        try {
            // query sparql endpoint
            String sparqlendpoint = "https://api.data.netwerkdigitaalerfgoed.nl/datasets/rkd/iconclass/services/iconclass/sparql";
            String sparql = "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> ";
            sparql += "SELECT * { ";
            sparql += "<" + url.replace("(", "%2528").replace(")", "%2529") + "> skos:prefLabel ?preflabel. ";
            sparql += "FILTER(LANGMATCHES(LANG(?preflabel), \"en\"))";
            sparql += "<" + url.replace("(", "%2528").replace(")", "%2529") + "> skos:prefLabel ?label. ";
            sparql += "OPTIONAL{ <" + url.replace("(", "%2528").replace(")", "%2529") + "> skos:broader ?broader. ?broader skos:prefLabel ?bLabel. FILTER(LANGMATCHES(LANG(?bLabel), \"en\")) }";
            sparql += "OPTIONAL{ <" + url.replace("(", "%2528").replace(")", "%2529") + "> skos:narrower ?narrower. ?narrower skos:prefLabel ?nLabel. FILTER(LANGMATCHES(LANG(?nLabel), \"en\")) }";
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
            if (bindingsArray.size() > 0) {
                // set preflabel
                for (Object element : bindingsArray) {
                    JSONObject tmpElement = (JSONObject) element;
                    JSONObject label = (JSONObject) tmpElement.get("preflabel");
                    String labelValue = (String) label.get("value");
                    String labelLang = (String) label.get("xml:lang");
                    DRAGON.setLabelLang(labelValue, labelLang);
                }
                // set prefdesc
                for (Object element : bindingsArray) {
                }
                // set other labels
                for (Object element : bindingsArray) {
                    JSONObject tmpElement = (JSONObject) element;
                    JSONObject label = (JSONObject) tmpElement.get("label");
                    String labelValue = (String) label.get("value");
                    String labelLang = (String) label.get("xml:lang");
                    DRAGON.setLabel(labelValue, labelLang);
                }
                // set other descriptions
                for (Object element : bindingsArray) {
                }
                // set broader terms
                for (Object element : bindingsArray) {
                    JSONObject tmpElement = (JSONObject) element;
                    JSONObject broader = (JSONObject) tmpElement.get("broader");
                    if (broader != null) {
                        String broaderValue = (String) broader.get("value");
                        JSONObject label = (JSONObject) tmpElement.get("bLabel");
                        String labelValue = (String) label.get("value");
                        DRAGON.setBroaderTerms(broaderValue, labelValue);
                    }
                }
                // set narrower terms
                for (Object element : bindingsArray) {
                    JSONObject tmpElement = (JSONObject) element;
                    JSONObject narrower = (JSONObject) tmpElement.get("narrower");
                    String narrowerValue = (String) narrower.get("value");
                    JSONObject label = (JSONObject) tmpElement.get("nLabel");
                    String labelValue = (String) label.get("value");
                    DRAGON.setNarrowerTerms(narrowerValue, labelValue);
                }
                // set additional information from triplestore
                DRAGON.setLairInfo("MWGDYW5S");
            } else {
                throw new ResourceNotAvailableException(url);
            }
            return DRAGON.getDragonItem();
        } catch (Exception e) {
            JSONParser parser = new JSONParser();
            JSONObject error = (JSONObject) parser.parse(Logging.getMessageJSON(e, "link.archaeology.re3dragon.action.IconClass"));
            return error;
        }
    }

    public static JSONArray items(String ids) throws IOException, ResourceNotAvailableException, ParseException, RetcatException {
        try {
            String[] id_array = ids.split(",");
            String id_list = "";
            for (String element : id_array) {
                id_list += "<" + element.replace("(", "%2528").replace(")", "%2529") + ">,";
            }
            id_list = id_list.substring(0, id_list.length() - 1);
            // query sparql endpoint
            String sparqlendpoint = "https://api.data.netwerkdigitaalerfgoed.nl/datasets/rkd/iconclass/services/iconclass/sparql";
            String sparql = "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> ";
            sparql += "SELECT * { ";
            sparql += "?s skos:prefLabel ?preflabel. ";
            sparql += "FILTER(LANGMATCHES(LANG(?preflabel), \"en\"))";
            //sparql += "?s skos:prefLabel ?label. ";
            //sparql += "OPTIONAL{ ?s skos:broader ?broader. ?broader skos:prefLabel ?bLabel. FILTER(LANGMATCHES(LANG(?bLabel), \"en\")) }";
            //sparql += "OPTIONAL{ ?s skos:narrower ?narrower. ?narrower skos:prefLabel ?nLabel. FILTER(LANGMATCHES(LANG(?nLabel), \"en\")) }";
            sparql += "FILTER (?s IN (" + id_list + "))";
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
            // create objects
            HashSet elements = new HashSet();
            HashMap<String, DragonItem> dragonItems = new HashMap();
            System.out.println("bindingsArray.size " + bindingsArray.size());
            if (bindingsArray.size() > 0) {
                for (Object element : bindingsArray) {
                    JSONObject tmpElement = (JSONObject) element;
                    JSONObject s = (JSONObject) tmpElement.get("s");
                    String sValue = (String) s.get("value");
                    elements.add(sValue);
                }
                for (Object element : elements) {
                    dragonItems.put((String) element, new DragonItem((String) element));
                }
            }
            System.out.println("dragonItems.size " + dragonItems.size());
            // set dragon properties
            JSONArray dragonItems_out = new JSONArray();
            if (bindingsArray.size() > 0) {
                for (Object element : bindingsArray) {
                    JSONObject tmpElement = (JSONObject) element;
                    JSONObject s = (JSONObject) tmpElement.get("s");
                    String sValue = (String) s.get("value");
                    // set preflabel
                    JSONObject label = (JSONObject) tmpElement.get("preflabel");
                    String labelValue = (String) label.get("value");
                    String labelLang = (String) label.get("xml:lang");
                    for (Map.Entry me : dragonItems.entrySet()) {
                        if (sValue == me.getKey()) {
                            DragonItem tmp = (DragonItem) me.getValue();
                            tmp.setLabelLang(labelValue, labelLang);
                        }
                    }
                    // set prefdesc
                    /*// set other labels
                    label = (JSONObject) tmpElement.get("label");
                    labelValue = (String) label.get("value");
                    labelLang = (String) label.get("xml:lang");
                    for (Map.Entry me : dragonItems.entrySet()) {
                        if (sValue.equals(me.getKey())) {
                            DragonItem tmp = (DragonItem) me.getValue();
                            tmp.setLabel(labelValue, labelLang);
                        }
                    }
                    // set other descriptions
                    JSONObject broader = (JSONObject) tmpElement.get("broader");
                    if (broader != null) {
                        String broaderValue = (String) broader.get("value");
                        label = (JSONObject) tmpElement.get("bLabel");
                        labelValue = (String) label.get("value");
                        for (Map.Entry me : dragonItems.entrySet()) {
                            if (sValue.equals(me.getKey())) {
                                DragonItem tmp = (DragonItem) me.getValue();
                                tmp.setBroaderTerms(broaderValue, labelValue);
                            }
                        }
                    }
                    // set narrower terms
                    JSONObject narrower = (JSONObject) tmpElement.get("narrower");
                    if (narrower != null) {
                        String narrowerValue = (String) narrower.get("value");
                        label = (JSONObject) tmpElement.get("nLabel");
                        labelValue = (String) label.get("value");
                        for (Map.Entry me : dragonItems.entrySet()) {
                            if (sValue.equals(me.getKey())) {
                                DragonItem tmp = (DragonItem) me.getValue();
                                tmp.setNarrowerTerms(narrowerValue, labelValue);
                            }
                        }
                    }*/
                    // set additional information from triplestore
                    for (Map.Entry me : dragonItems.entrySet()) {
                        if (sValue.equals(me.getKey())) {
                            DragonItem tmp = (DragonItem) me.getValue();
                            tmp.setLairInfo("MWGDYW5S");
                        }
                    }
                }
            } else {
                throw new ResourceNotAvailableException();
            }
            for (Map.Entry me : dragonItems.entrySet()) {
                DragonItem tmp = (DragonItem) me.getValue();
                dragonItems_out.add(tmp.getDragonItem());
            }
            return dragonItems_out;
        } catch (Exception e) {
            JSONParser parser = new JSONParser();
            JSONObject error = (JSONObject) parser.parse(Logging.getMessageJSON(e, "link.archaeology.re3dragon.action.IconClass"));
            JSONArray error_arr = new JSONArray();
            error_arr.add(error);
            return error_arr;
        }
    }

    public static JSONArray search(String q) throws IOException, ResourceNotAvailableException, ParseException, RetcatException {
        try {
            // output
            JSONArray dragonItems_out = new JSONArray();
            // query API
            String api = "http://iconclass.org/rkd/0/?q=" + q + "&q_s=1&fmt=json";
            URL url = new URL(api);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            System.out.println("IconClass.search() - " + responseCode + " - " + url);
            if (responseCode < 400) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                JSONObject apiResults = (JSONObject) new JSONParser().parse(response.toString());
                JSONArray records = (JSONArray) apiResults.get("records");
                String ids = "";
                for (Object element : records) {
                    JSONObject tmpElement = (JSONObject) element;
                    String n = (String) tmpElement.get("n");
                    ids += "http://iconclass.org/" + n + ",";
                }
                ids = ids.substring(0, ids.length() - 1);
                String[] id_array = ids.split(",");
                String id_list = "";
                for (String element : id_array) {
                    id_list += "<" + element.replace("(", "%2528").replace(")", "%2529").replace(" ", "%2B").replace("'", "%2527") + ">,";
                }
                id_list = id_list.substring(0, id_list.length() - 1);
                // query sparql endpoint
                String sparqlendpoint = "https://api.data.netwerkdigitaalerfgoed.nl/datasets/rkd/iconclass/services/iconclass/sparql";
                String sparql = "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> ";
                sparql += "SELECT * { ";
                sparql += "?s skos:prefLabel ?preflabel. ";
                sparql += "FILTER(LANGMATCHES(LANG(?preflabel), \"en\"))";
                //sparql += "?s skos:prefLabel ?label. ";
                //sparql += "OPTIONAL{ ?s skos:broader ?broader. ?broader skos:prefLabel ?bLabel. FILTER(LANGMATCHES(LANG(?bLabel), \"en\")) }";
                //sparql += "OPTIONAL{ ?s skos:narrower ?narrower. ?narrower skos:prefLabel ?nLabel. FILTER(LANGMATCHES(LANG(?nLabel), \"en\")) }";
                sparql += "FILTER (?s IN (" + id_list + "))";
                sparql += " }";
                URL obj2 = new URL(sparqlendpoint);
                HttpURLConnection con2 = (HttpURLConnection) obj2.openConnection();
                con2.setRequestMethod("POST");
                con2.setRequestProperty("Accept", "application/sparql-results+json");
                String urlParameters = "query=" + sparql;
                con2.setDoOutput(true);
                DataOutputStream wr2 = new DataOutputStream(con2.getOutputStream());
                wr2.writeBytes(urlParameters);
                wr2.flush();
                wr2.close();
                BufferedReader in2 = new BufferedReader(new InputStreamReader(con2.getInputStream(), "UTF8"));
                String inputLine2 = "";
                StringBuilder response2 = new StringBuilder();
                while ((inputLine2 = in2.readLine()) != null) {
                    response2.append(inputLine2);
                }
                in2.close();
                // parse SPARQL results json
                JSONObject jsonObject = (JSONObject) new JSONParser().parse(response2.toString());
                JSONObject resultsObject = (JSONObject) jsonObject.get("results");
                JSONArray bindingsArray = (JSONArray) resultsObject.get("bindings");
                // create objects
                HashSet elements = new HashSet();
                HashMap<String, DragonItem> dragonItems = new HashMap();
                System.out.println("bindingsArray.size " + bindingsArray.size());
                if (bindingsArray.size() > 0) {
                    for (Object element : bindingsArray) {
                        JSONObject tmpElement = (JSONObject) element;
                        JSONObject s = (JSONObject) tmpElement.get("s");
                        String sValue = (String) s.get("value");
                        elements.add(sValue);
                    }
                    for (Object element : elements) {
                        dragonItems.put((String) element, new DragonItemSimilarity((String) element));
                    }
                }
                System.out.println("dragonItems.size " + dragonItems.size());
                // set dragon properties
                if (bindingsArray.size() > 0) {
                    for (Object element : bindingsArray) {
                        JSONObject tmpElement = (JSONObject) element;
                        JSONObject s = (JSONObject) tmpElement.get("s");
                        String sValue = (String) s.get("value");
                        // set preflabel
                        JSONObject label = (JSONObject) tmpElement.get("preflabel");
                        String labelValue = (String) label.get("value");
                        String labelLang = (String) label.get("xml:lang");
                        for (Map.Entry me : dragonItems.entrySet()) {
                            if (sValue == me.getKey()) {
                                DragonItemSimilarity tmp = (DragonItemSimilarity) me.getValue();
                                tmp.setLabelLang(labelValue, labelLang);
                                // similarity
                                tmp.setLairString(labelValue);
                                tmp.setSearchString(q);
                                tmp.setLevenshtein(StringSimilarity.Levenshtein(q, labelValue));
                                tmp.setNormalizedLevenshtein(StringSimilarity.NormalizedLevenshtein(q, labelValue));
                                tmp.setDamerauLevenshtein(StringSimilarity.Damerau(q, labelValue));
                                tmp.setJaroWinkler(StringSimilarity.JaroWinkler(q, labelValue));
                            }
                        }
                        // set prefdesc
                        /*// set other labels
                        label = (JSONObject) tmpElement.get("label");
                        labelValue = (String) label.get("value");
                        labelLang = (String) label.get("xml:lang");
                        for (Map.Entry me : dragonItems.entrySet()) {
                            if (sValue.equals(me.getKey())) {
                                DragonItemSimilarity tmp = (DragonItemSimilarity) me.getValue();
                                tmp.setLabel(labelValue, labelLang);
                            }
                        }
                        // set other descriptions
                        JSONObject broader = (JSONObject) tmpElement.get("broader");
                        if (broader != null) {
                            String broaderValue = (String) broader.get("value");
                            label = (JSONObject) tmpElement.get("bLabel");
                            labelValue = (String) label.get("value");
                            for (Map.Entry me : dragonItems.entrySet()) {
                                if (sValue.equals(me.getKey())) {
                                    DragonItemSimilarity tmp = (DragonItemSimilarity) me.getValue();
                                    tmp.setBroaderTerms(broaderValue, labelValue);
                                }
                            }
                        }
                        // set narrower terms
                        JSONObject narrower = (JSONObject) tmpElement.get("narrower");
                        if (narrower != null) {
                            String narrowerValue = (String) narrower.get("value");
                            label = (JSONObject) tmpElement.get("nLabel");
                            labelValue = (String) label.get("value");
                            for (Map.Entry me : dragonItems.entrySet()) {
                                if (sValue.equals(me.getKey())) {
                                    DragonItemSimilarity tmp = (DragonItemSimilarity) me.getValue();
                                    tmp.setNarrowerTerms(narrowerValue, labelValue);
                                }
                            }
                        }*/
                        // set additional information from triplestore
                        for (Map.Entry me : dragonItems.entrySet()) {
                            if (sValue.equals(me.getKey())) {
                                DragonItemSimilarity tmp = (DragonItemSimilarity) me.getValue();
                                tmp.setLairInfo("MWGDYW5S");
                            }
                        }
                    }
                } else {
                    throw new ResourceNotAvailableException();
                }
                for (Map.Entry me : dragonItems.entrySet()) {
                    DragonItemSimilarity tmp = (DragonItemSimilarity) me.getValue();
                    dragonItems_out.add(tmp.getDragonItem());
                }
            }
            return dragonItems_out;
        } catch (Exception e) {
            JSONParser parser = new JSONParser();
            JSONObject error = (JSONObject) parser.parse(Logging.getMessageJSON(e, "link.archaeology.re3dragon.action.IconClass"));
            JSONArray error_arr = new JSONArray();
            error_arr.add(error);
            return error_arr;
        }
    }

}

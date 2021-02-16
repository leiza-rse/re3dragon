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
import link.archaeology.re3dragon.conf.GeoJSONFeature;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.mainzed.re3dragon.exceptions.ResourceNotAvailableException;
import org.mainzed.re3dragon.exceptions.RetcatException;
import org.mainzed.re3dragon.log.Logging;

public class Wikidata {

    public static JSONObject info(String url) throws IOException, ResourceNotAvailableException, ParseException, RetcatException {
        try {
            // query sparql endpoint
            String sparqlendpoint = "https://query.wikidata.org/sparql";
            String sparql = "";
            sparql += "PREFIX wd: <http://www.wikidata.org/entity/> ";
            sparql += "PREFIX wdt: <http://www.wikidata.org/prop/direct/> ";
            sparql += "PREFIX p: <http://www.wikidata.org/prop/> ";
            sparql += "PREFIX psv: <http://www.wikidata.org/prop/statement/value/> ";
            sparql += "PREFIX wikibase: <http://wikiba.se/ontology#> ";
            sparql += "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ";
            sparql += "PREFIX schema: <http://schema.org/> ";
            sparql += "SELECT ?displaylabel ?preflabel (lang(?preflabel) as ?preflabel_lang) ?displaydesc ?desc (lang(?desc) as ?desc_lang) ?broader ?broaderlabel ?narrower ?narrowerlabel ?wkt ?lat ?lon { ";
            sparql += "?s rdfs:label ?displaylabel. ";
            sparql += "?s schema:description ?displaydesc. ";
            sparql += "?s rdfs:label ?preflabel. ";
            sparql += "?s schema:description ?desc. ";
            sparql += "FILTER (lang(?displaydesc) = 'en') ";
            sparql += "FILTER (lang(?displaylabel) = 'en') ";
            sparql += "FILTER (lang(?preflabel) = lang(?desc)) ";
            sparql += "OPTIONAL { ?s wdt:P31 ?broader. ?broader rdfs:label ?broaderlabel. FILTER(lang(?broaderlabel) = 'en')} ";
            sparql += "OPTIONAL { ?s wdt:P279 ?broader. ?broader rdfs:label ?broaderlabel. FILTER(lang(?broaderlabel) = 'en')} ";
            sparql += "OPTIONAL { ?narrower wdt:P31 ?s. ?narrower rdfs:label ?narrowerlabel. FILTER(lang(?narrowerlabel) = 'en')} ";
            sparql += "OPTIONAL { ?narrower wdt:P279 ?s. ?narrower rdfs:label ?narrowerlabel. FILTER(lang(?narrowerlabel) = 'en')} ";
            sparql += "OPTIONAL { ?s wdt:P625 ?wkt. ?s p:P625 ?geostatement . ?geostatement psv:P625 ?coordinate_node . ?coordinate_node wikibase:geoLatitude ?lat . ?coordinate_node wikibase:geoLongitude ?lon . } ";
            sparql += "FILTER (?s = " + url.replace("http://www.wikidata.org/entity/", "wd:") + ")";
            sparql += " }";
            URL obj = new URL(sparqlendpoint);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept", "application/sparql-results+json");
            String urlParameters = "format=json&query=" + sparql;
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
                // set displaylabel
                for (Object element : bindingsArray) {
                    JSONObject tmpElement = (JSONObject) element;
                    JSONObject label = (JSONObject) tmpElement.get("displaylabel");
                    String labelValue = (String) label.get("value");
                    String labelLang = (String) label.get("xml:lang");
                    DRAGON.setLabelLang(labelValue, labelLang);
                }
                // set displaydesc
                for (Object element : bindingsArray) {
                    JSONObject tmpElement = (JSONObject) element;
                    JSONObject desc = (JSONObject) tmpElement.get("displaydesc");
                    if (desc != null) {
                        String descValue = (String) desc.get("value");
                        String descLang = (String) desc.get("xml:lang");
                        DRAGON.setDescLabelLang(descValue, descLang);
                    }
                }
                // set preflabel
                for (Object element : bindingsArray) {
                    JSONObject tmpElement = (JSONObject) element;
                    JSONObject label = (JSONObject) tmpElement.get("preflabel");
                    String labelValue = (String) label.get("value");
                    String labelLang = (String) label.get("xml:lang");
                    DRAGON.setLabel(labelValue, labelLang);
                }
                // set scope notes
                for (Object element : bindingsArray) {
                    JSONObject tmpElement = (JSONObject) element;
                    JSONObject desc = (JSONObject) tmpElement.get("desc");
                    if (desc != null) {
                        String descValue = (String) desc.get("value");
                        String descLang = (String) desc.get("xml:lang");
                        DRAGON.setDescriptions(descValue, descLang);
                    }
                }
                // set broader terms
                for (Object element : bindingsArray) {
                    JSONObject tmpElement = (JSONObject) element;
                    JSONObject broader = (JSONObject) tmpElement.get("broader");
                    if (broader != null) {
                        String broaderValue = (String) broader.get("value");
                        JSONObject label = (JSONObject) tmpElement.get("broaderlabel");
                        String labelValue = (String) label.get("value");
                        DRAGON.setBroaderTerms(broaderValue, labelValue);
                    }
                }
                // set narrower terms
                for (Object element : bindingsArray) {
                    JSONObject tmpElement = (JSONObject) element;
                    JSONObject narrower = (JSONObject) tmpElement.get("narrower");
                    if (narrower != null) {
                        String narrowerValue = (String) narrower.get("value");
                        JSONObject label = (JSONObject) tmpElement.get("narrowerlabel");
                        String labelValue = (String) label.get("value");
                        DRAGON.setNarrowerTerms(narrowerValue, labelValue);
                    }
                }
                // set location
                for (Object element : bindingsArray) {
                    JSONObject tmpElement = (JSONObject) element;
                    JSONObject lat = (JSONObject) tmpElement.get("lat");
                    JSONObject lon = (JSONObject) tmpElement.get("lon");
                    if (lat != null && lon != null) {
                        String latValue = (String) lat.get("value");
                        String lonValue = (String) lon.get("value");
                        GeoJSONFeature gjf = new GeoJSONFeature("Point", Double.parseDouble(lonValue), Double.parseDouble(latValue), 4326);
                        DRAGON.setLocation(gjf.getGeoJSONFeaturePoint());
                    }
                }
                // set additional information from triplestore
                DRAGON.setLairInfo("7D2HP57S");
            } else {
                throw new ResourceNotAvailableException(url);
            }
            return DRAGON.getDragonItem();
        } catch (Exception e) {
            JSONParser parser = new JSONParser();
            JSONObject error = (JSONObject) parser.parse(Logging.getMessageJSON(e, "link.archaeology.re3dragon.action.Wikidata"));
            return error;
        }
    }

    public static JSONArray search(String q) throws IOException, ResourceNotAvailableException, ParseException, RetcatException {
        try {
            // output
            JSONArray dragonItems_out = new JSONArray();
            // query sparql endpoint
            String sparqlendpoint = "https://query.wikidata.org/sparql";
            String sparql = "";
            sparql += "PREFIX wd: <http://www.wikidata.org/entity/> ";
            sparql += "PREFIX wdt: <http://www.wikidata.org/prop/direct/> ";
            sparql += "PREFIX p: <http://www.wikidata.org/prop/> ";
            sparql += "PREFIX psv: <http://www.wikidata.org/prop/statement/value/> ";
            sparql += "PREFIX wikibase: <http://wikiba.se/ontology#> ";
            sparql += "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ";
            sparql += "PREFIX schema: <http://schema.org/> ";
            sparql += "PREFIX bd: <http://www.bigdata.com/rdf#> ";
            sparql += "SELECT ?item ?prefLabel ?prefDesc ?wkt ?lat ?lon { ";
            sparql += "?item rdfs:label ?prefLabel. ";
            sparql += " ?item schema:description ?prefDesc. ";
            sparql += "FILTER(CONTAINS(LCASE(?prefLabel), \"" + q + "\")). ";
            sparql += "FILTER(langMatches(lang(?prefLabel), \"EN\")).";
            sparql += "FILTER(langMatches(lang(?prefDesc), \"EN\")).";
            sparql += "OPTIONAL { ?item wdt:P625 ?wkt. ?item p:P625 ?geostatement . ?geostatement psv:P625 ?coordinate_node . ?coordinate_node wikibase:geoLatitude ?lat . ?coordinate_node wikibase:geoLongitude ?lon . } ";
            sparql += " } LIMIT 100";
            URL obj = new URL(sparqlendpoint);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept", "application/sparql-results+json");
            String urlParameters = "format=json&query=" + sparql;
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
                    JSONObject s = (JSONObject) tmpElement.get("item");
                    String sValue = (String) s.get("value");
                    elements.add(sValue);
                }
                for (Object element : elements) {
                    dragonItems.put((String) element, new DragonItem((String) element));
                }
            }
            System.out.println("dragonItems.size " + dragonItems.size());
            // set dragon properties
            if (bindingsArray.size() > 0) {
                for (Object element : bindingsArray) {
                    JSONObject tmpElement = (JSONObject) element;
                    JSONObject s = (JSONObject) tmpElement.get("item");
                    String sValue = (String) s.get("value");
                    // set preflabel
                    JSONObject label = (JSONObject) tmpElement.get("prefLabel");
                    String labelValue = (String) label.get("value");
                    String labelLang = (String) label.get("xml:lang");
                    for (Map.Entry me : dragonItems.entrySet()) {
                        if (sValue == me.getKey()) {
                            DragonItem tmp = (DragonItem) me.getValue();
                            tmp.setLabelLang(labelValue, labelLang);
                        }
                    }
                    // set prefdesc
                    JSONObject scopenote = (JSONObject) tmpElement.get("prefDesc");
                    if (scopenote != null) {
                        String scopenoteValue = (String) scopenote.get("value");
                        String scopenoteLang = (String) scopenote.get("xml:lang");
                        for (Map.Entry me : dragonItems.entrySet()) {
                            if (sValue == me.getKey()) {
                                DragonItem tmp = (DragonItem) me.getValue();
                                tmp.setDescLabelLang(scopenoteValue, scopenoteLang);
                            }
                        }
                    }
                    // set location
                    JSONObject lat = (JSONObject) tmpElement.get("lat");
                    JSONObject lon = (JSONObject) tmpElement.get("lon");
                    if (lat != null && lon != null) {
                        String latValue = (String) lat.get("value");
                        String lonValue = (String) lon.get("value");
                        GeoJSONFeature gjf = new GeoJSONFeature("Point", Double.parseDouble(lonValue), Double.parseDouble(latValue), 4326);
                        for (Map.Entry me : dragonItems.entrySet()) {
                            if (sValue == me.getKey()) {
                                DragonItem tmp = (DragonItem) me.getValue();
                                tmp.setLocation(gjf.getGeoJSONFeaturePoint());
                            }
                        }
                        // set additional information from triplestore
                        for (Map.Entry me : dragonItems.entrySet()) {
                            if (sValue.equals(me.getKey())) {
                                DragonItem tmp = (DragonItem) me.getValue();
                                tmp.setLairInfo("7D2HP57S");
                            }
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
            JSONObject error = (JSONObject) parser.parse(Logging.getMessageJSON(e, "link.archaeology.re3dragon.action.Wikidata"));
            JSONArray error_arr = new JSONArray();
            error_arr.add(error);
            return error_arr;
        }
    }

    public static JSONArray items(String ids) throws IOException, ResourceNotAvailableException, ParseException, RetcatException {
        try {
            // set ids
            String[] id_array = ids.split(",");
            String id_list = "";
            for (String element : id_array) {
                id_list += element.replace("http://www.wikidata.org/entity/", "wd:") + " ";
            }
            id_list = id_list.substring(0, id_list.length() - 1);
            // output
            JSONArray dragonItems_out = new JSONArray();
            // query sparql endpoint
            String sparqlendpoint = "https://query.wikidata.org/sparql";
            String sparql = "";
            sparql += "PREFIX wd: <http://www.wikidata.org/entity/> ";
            sparql += "PREFIX wdt: <http://www.wikidata.org/prop/direct/> ";
            sparql += "PREFIX p: <http://www.wikidata.org/prop/> ";
            sparql += "PREFIX psv: <http://www.wikidata.org/prop/statement/value/> ";
            sparql += "PREFIX wikibase: <http://wikiba.se/ontology#> ";
            sparql += "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ";
            sparql += "PREFIX schema: <http://schema.org/> ";
            sparql += "PREFIX bd: <http://www.bigdata.com/rdf#> ";
            sparql += "SELECT ?item ?itemLabel ?itemDescription ?wkt ?lat ?lon { ";
            sparql += "VALUES ?item { " + id_list + " } ";
            sparql += "SERVICE wikibase:label { bd:serviceParam wikibase:language \"en\" } ";
            sparql += "OPTIONAL { ?item wdt:P625 ?wkt. ?item p:P625 ?geostatement . ?geostatement psv:P625 ?coordinate_node . ?coordinate_node wikibase:geoLatitude ?lat . ?coordinate_node wikibase:geoLongitude ?lon . } ";
            sparql += " }";
            URL obj = new URL(sparqlendpoint);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept", "application/sparql-results+json");
            String urlParameters = "format=json&query=" + sparql;
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
                    JSONObject s = (JSONObject) tmpElement.get("item");
                    String sValue = (String) s.get("value");
                    elements.add(sValue);
                }
                for (Object element : elements) {
                    dragonItems.put((String) element, new DragonItem((String) element));
                }
            }
            System.out.println("dragonItems.size " + dragonItems.size());
            // set dragon properties
            if (bindingsArray.size() > 0) {
                for (Object element : bindingsArray) {
                    JSONObject tmpElement = (JSONObject) element;
                    JSONObject s = (JSONObject) tmpElement.get("item");
                    String sValue = (String) s.get("value");
                    // set preflabel
                    JSONObject label = (JSONObject) tmpElement.get("itemLabel");
                    String labelValue = (String) label.get("value");
                    String labelLang = (String) label.get("xml:lang");
                    for (Map.Entry me : dragonItems.entrySet()) {
                        if (sValue == me.getKey()) {
                            DragonItem tmp = (DragonItem) me.getValue();
                            tmp.setLabelLang(labelValue, labelLang);
                        }
                    }
                    // set prefdesc
                    JSONObject scopenote = (JSONObject) tmpElement.get("itemDescription");
                    if (scopenote != null) {
                        String scopenoteValue = (String) scopenote.get("value");
                        String scopenoteLang = (String) scopenote.get("xml:lang");
                        for (Map.Entry me : dragonItems.entrySet()) {
                            if (sValue == me.getKey()) {
                                DragonItem tmp = (DragonItem) me.getValue();
                                tmp.setDescLabelLang(scopenoteValue, scopenoteLang);
                            }
                        }
                    }
                    // set location
                    JSONObject lat = (JSONObject) tmpElement.get("lat");
                    JSONObject lon = (JSONObject) tmpElement.get("lon");
                    if (lat != null && lon != null) {
                        String latValue = (String) lat.get("value");
                        String lonValue = (String) lon.get("value");
                        GeoJSONFeature gjf = new GeoJSONFeature("Point", Double.parseDouble(lonValue), Double.parseDouble(latValue), 4326);
                        for (Map.Entry me : dragonItems.entrySet()) {
                            if (sValue == me.getKey()) {
                                DragonItem tmp = (DragonItem) me.getValue();
                                tmp.setLocation(gjf.getGeoJSONFeaturePoint());
                            }
                        }
                        // set additional information from triplestore
                        for (Map.Entry me : dragonItems.entrySet()) {
                            if (sValue.equals(me.getKey())) {
                                DragonItem tmp = (DragonItem) me.getValue();
                                tmp.setLairInfo("7D2HP57S");
                            }
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
            JSONObject error = (JSONObject) parser.parse(Logging.getMessageJSON(e, "link.archaeology.re3dragon.action.Wikidata"));
            JSONArray error_arr = new JSONArray();
            error_arr.add(error);
            return error_arr;
        }
    }

}

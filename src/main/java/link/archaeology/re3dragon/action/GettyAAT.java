package link.archaeology.re3dragon.action;

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

public class GettyAAT {

    public static JSONObject info(String url) throws IOException, ResourceNotAvailableException, ParseException, RetcatException {
        try {
            // query sparql endpoint
            String sparqlendpoint = "http://vocab.getty.edu/sparql";
            String sparql = "SELECT * { ";
            sparql += "<" + url + "> gvp:prefLabelGVP [xl:literalForm ?preflabel]. ";
            sparql += "<" + url + "> skos:prefLabel ?label. ";
            if (url.contains("aat") || url.contains("ulan")) {
                sparql += "OPTIONAL {<" + url + "> skos:scopeNote [dct:language gvp_lang:" + "en" + "; rdf:value ?scopeNote]} . ";
                sparql += "OPTIONAL {<" + url + "> skos:scopeNote [rdf:value ?desc]} . ";
            } else if (url.contains("tgn")) {
                sparql += "OPTIONAL {<" + url + "> gvp:parentString ?scopeNote . } ";
            }
            sparql += "OPTIONAL {<" + url + "> gvp:broaderPreferred ?BroaderPreferred . ?BroaderPreferred gvp:prefLabelGVP [xl:literalForm ?BroaderPreferredTerm].} . ";
            sparql += "OPTIONAL {?NarrowerPreferred gvp:broaderPreferred <" + url + "> . ?NarrowerPreferred gvp:prefLabelGVP [xl:literalForm ?NarrowerPreferredTerm].} . ";
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
                    JSONObject tmpElement = (JSONObject) element;
                    JSONObject desc = (JSONObject) tmpElement.get("scopeNote");
                    if (desc != null) {
                        String descValue = (String) desc.get("value");
                        String descLang = (String) desc.get("xml:lang");
                        DRAGON.setDescLabelLang(descValue, descLang);
                    }
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
                    JSONObject broader = (JSONObject) tmpElement.get("BroaderPreferred");
                    if (broader != null) {
                        String broaderValue = (String) broader.get("value");
                        JSONObject label = (JSONObject) tmpElement.get("BroaderPreferredTerm");
                        String labelValue = (String) label.get("value");
                        DRAGON.setBroaderTerms(broaderValue, labelValue);
                    }
                }
                // set narrower terms
                for (Object element : bindingsArray) {
                    JSONObject tmpElement = (JSONObject) element;
                    JSONObject narrower = (JSONObject) tmpElement.get("NarrowerPreferred");
                    if (narrower != null) {
                        String narrowerValue = (String) narrower.get("value");
                        JSONObject label = (JSONObject) tmpElement.get("NarrowerPreferredTerm");
                        String labelValue = (String) label.get("value");
                        DRAGON.setNarrowerTerms(narrowerValue, labelValue);
                    }
                }
                // set additional information from triplestore
                DRAGON.setLairInfo("ULBU3XXM");
            } else {
                throw new ResourceNotAvailableException(url);
            }
            return DRAGON.getDragonItem();
        } catch (Exception e) {
            JSONParser parser = new JSONParser();
            JSONObject error = (JSONObject) parser.parse(Logging.getMessageJSON(e, "link.archaeology.re3dragon.action.GettyAAT"));
            return error;
        }
    }

    public static JSONArray search(String q) throws IOException, ResourceNotAvailableException, ParseException, RetcatException {
        try {
            // output
            JSONArray dragonItems_out = new JSONArray();
            // query sparql endpoint
            String lang = "en"; // language for scopeNote
            String url = "http://vocab.getty.edu/sparql";
            String sparql = "SELECT ?s ?preflabel ?scopenote ?BroaderPreferredTerm ?BroaderPreferred ?NarrowerPreferredTerm ?NarrowerPreferred { "
                    + "?s a skos:Concept. "
                    + "?s luc:term '" + q + "' . "
                    + "?s skos:inScheme aat: . "
                    //+ "?s skos:inScheme ?scheme . "
                    //+ "?scheme rdfs:label ?schemeTitle . "
                    + "?s gvp:prefLabelGVP [xl:literalForm ?preflabel]. "
                    + "OPTIONAL {?s skos:scopeNote [dct:language gvp_lang:" + lang + "; rdf:value ?scopenote]} . "
                    //+ "OPTIONAL {?s gvp:broaderPreferred ?BroaderPreferred . ?BroaderPreferred gvp:prefLabelGVP [xl:literalForm ?BroaderPreferredTerm].} . "
                    //+ "OPTIONAL {?NarrowerPreferred gvp:broaderPreferred ?s . ?NarrowerPreferred gvp:prefLabelGVP [xl:literalForm ?NarrowerPreferredTerm].} . "
                    + " } ORDER BY ASC(LCASE(STR(?Term)))";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept", "application/sparql-results+json");
            String urlParameters = "query=" + sparql;
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(wr, "UTF-8"));
            writer.write(urlParameters);
            writer.close();
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
                    JSONObject scopenote = (JSONObject) tmpElement.get("scopenote");
                    if (scopenote != null) {
                        String scopenoteValue = (String) scopenote.get("value");
                        String scopenoteLang = (String) scopenote.get("xml:lang");
                        for (Map.Entry me : dragonItems.entrySet()) {
                            if (sValue == me.getKey()) {
                                DragonItemSimilarity tmp = (DragonItemSimilarity) me.getValue();
                                tmp.setDescLabelLang(scopenoteValue, scopenoteLang);
                            }
                        }
                    }
                    // set additional information from triplestore
                    for (Map.Entry me : dragonItems.entrySet()) {
                        if (sValue.equals(me.getKey())) {
                            DragonItemSimilarity tmp = (DragonItemSimilarity) me.getValue();
                            tmp.setLairInfo("ULBU3XXM");
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
            return dragonItems_out;
        } catch (Exception e) {
            JSONParser parser = new JSONParser();
            JSONObject error = (JSONObject) parser.parse(Logging.getMessageJSON(e, "link.archaeology.re3dragon.action.GettyAAT"));
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
                id_list += "<" + element.replace("(", "%2528").replace(")", "%2529") + ">,";
            }
            id_list = id_list.substring(0, id_list.length() - 1);
            // output
            JSONArray dragonItems_out = new JSONArray();
            // query sparql endpoint
            String lang = "en"; // language for scopeNote
            String url = "http://vocab.getty.edu/sparql";
            String sparql = "SELECT ?s ?preflabel ?scopenote ?BroaderPreferredTerm ?BroaderPreferred ?NarrowerPreferredTerm ?NarrowerPreferred { "
                    + "?s a skos:Concept. "
                    + "FILTER (?s IN (" + id_list + ")) "
                    + "?s skos:inScheme aat: . "
                    //+ "?s skos:inScheme ?scheme . "
                    //+ "?scheme rdfs:label ?schemeTitle . "
                    + "?s gvp:prefLabelGVP [xl:literalForm ?preflabel]. "
                    + "OPTIONAL {?s skos:scopeNote [dct:language gvp_lang:" + lang + "; rdf:value ?scopenote]} . "
                    //+ "OPTIONAL {?s gvp:broaderPreferred ?BroaderPreferred . ?BroaderPreferred gvp:prefLabelGVP [xl:literalForm ?BroaderPreferredTerm].} . "
                    //+ "OPTIONAL {?NarrowerPreferred gvp:broaderPreferred ?s . ?NarrowerPreferred gvp:prefLabelGVP [xl:literalForm ?NarrowerPreferredTerm].} . "
                    + " } ORDER BY ASC(LCASE(STR(?Term)))";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept", "application/sparql-results+json");
            String urlParameters = "query=" + sparql;
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(wr, "UTF-8"));
            writer.write(urlParameters);
            writer.close();
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
                    JSONObject scopenote = (JSONObject) tmpElement.get("scopenote");
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
                    // set additional information from triplestore
                    for (Map.Entry me : dragonItems.entrySet()) {
                        if (sValue.equals(me.getKey())) {
                            DragonItem tmp = (DragonItem) me.getValue();
                            tmp.setLairInfo("ULBU3XXM");
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
            JSONObject error = (JSONObject) parser.parse(Logging.getMessageJSON(e, "link.archaeology.re3dragon.action.GettyAAT"));
            JSONArray error_arr = new JSONArray();
            error_arr.add(error);
            return error_arr;
        }
    }

}

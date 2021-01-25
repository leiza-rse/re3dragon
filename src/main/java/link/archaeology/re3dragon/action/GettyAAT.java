package link.archaeology.re3dragon.action;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import link.archaeology.re3dragon.conf.DragonItem;
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
                // set additional information
                // from dragonlair triplestore
                DRAGON.setScheme("Getty Art & Architecture Thesaurus (AAT)");
                DRAGON.setType("lado:Collection");
                DRAGON.setLegalType("lado:ResearchInstitution");
                DRAGON.setQuality("lado:qualityHigh");
            } else {
                throw new ResourceNotAvailableException(url);
            }
            return DRAGON.getDragonItem();
        } catch (Exception e) {
            JSONParser parser = new JSONParser();
            JSONObject error = (JSONObject) parser.parse(Logging.getMessageJSON(e, "org.mainzed.re3dragon.rest.GettyAAT"));
            return error;
        }
    }

}

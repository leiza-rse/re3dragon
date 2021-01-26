package link.archaeology.re3dragon.action;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import link.archaeology.re3dragon.conf.DragonLairItem;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.mainzed.re3dragon.exceptions.ResourceNotAvailableException;
import org.mainzed.re3dragon.exceptions.RetcatException;
import org.mainzed.re3dragon.log.Logging;

public class Lair {

    public static JSONObject shortinfo(String id) throws IOException, ResourceNotAvailableException, ParseException, RetcatException {
        try {
            // query sparql endpoint
            String sparqlendpoint = "https://java-dev.rgzm.de/rdf4j-server/repositories/re3dragonlair";
            String sparql = "PREFIX lair: <http://lod.datadragon.link/data/dragonlair/> PREFIX lado: <http://archaeology.link/ontology#> PREFIX owl:<http://www.w3.org/2002/07/owl#> ";
            sparql += "SELECT * { ";
            sparql += "lair:" + id + " rdfs:label ?label. ";
            sparql += "lair:" + id + " lado:hasLegalType ?legaltype. ";
            sparql += "lair:" + id + " lado:hasQuality ?quality. ";
            sparql += "lair:" + id + " lado:hasType ?type. ";
            sparql += "lair:" + id + " lado:lairGroup ?group. ";
            sparql += "lair:" + id + " owl:sameAs ?sameas. ";
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
            DragonLairItem DRAGONLAIR = new DragonLairItem(id);
            if (bindingsArray.size() > 0) {
                for (Object element : bindingsArray) {
                    JSONObject tmpElement = (JSONObject) element;
                    JSONObject label = (JSONObject) tmpElement.get("label");
                    String labelValue = (String) label.get("value");
                    DRAGONLAIR.setScheme(labelValue);
                }
                for (Object element : bindingsArray) {
                    JSONObject tmpElement = (JSONObject) element;
                    JSONObject legaltype = (JSONObject) tmpElement.get("legaltype");
                    String legaltypeValue = (String) legaltype.get("value");
                    DRAGONLAIR.setLegalType(legaltypeValue);
                }
                for (Object element : bindingsArray) {
                    JSONObject tmpElement = (JSONObject) element;
                    JSONObject type = (JSONObject) tmpElement.get("type");
                    String typeValue = (String) type.get("value");
                    DRAGONLAIR.setType(typeValue);
                }
                for (Object element : bindingsArray) {
                    JSONObject tmpElement = (JSONObject) element;
                    JSONObject quality = (JSONObject) tmpElement.get("quality");
                    String qualityValue = (String) quality.get("value");
                    DRAGONLAIR.setQuality(qualityValue);
                }
                for (Object element : bindingsArray) {
                    JSONObject tmpElement = (JSONObject) element;
                    JSONObject group = (JSONObject) tmpElement.get("group");
                    String groupValue = (String) group.get("value");
                    DRAGONLAIR.setGroup(groupValue);
                }
                for (Object element : bindingsArray) {
                    JSONObject tmpElement = (JSONObject) element;
                    JSONObject sameas = (JSONObject) tmpElement.get("sameas");
                    String sameasValue = (String) sameas.get("value");
                    DRAGONLAIR.setWikidataItem(sameasValue);
                }
            } else {
                throw new ResourceNotAvailableException(id);
            }
            return DRAGONLAIR.getDragonLairItem();
        } catch (Exception e) {
            JSONParser parser = new JSONParser();
            JSONObject error = (JSONObject) parser.parse(Logging.getMessageJSON(e, "link.archaeology.re3dragon.action.Lair"));
            return error;
        }
    }

    public static JSONArray lairs() throws IOException, ResourceNotAvailableException, ParseException, RetcatException {
        JSONArray lairs = new JSONArray();
        try {
            // query sparql endpoint
            String sparqlendpoint = "https://java-dev.rgzm.de/rdf4j-server/repositories/re3dragonlair";
            String sparql = "PREFIX lair: <http://lod.datadragon.link/data/dragonlair/> PREFIX lado: <http://archaeology.link/ontology#> PREFIX owl:<http://www.w3.org/2002/07/owl#> ";
            sparql += "SELECT * { ";
            sparql += "?lair a lado:DataDragonLair. ";
            sparql += "?lair rdfs:label ?label. ";
            sparql += "?lair lado:hasLegalType ?legaltype. ";
            sparql += "?lair lado:hasQuality ?quality. ";
            sparql += "?lair lado:hasType ?type. ";
            sparql += "?lair lado:lairGroup ?group. ";
            sparql += "?lair owl:sameAs ?sameas. ";
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
            if (bindingsArray.size() > 0) {
                for (Object element : bindingsArray) {
                    JSONObject tmpElement = (JSONObject) element;
                    JSONObject lair = (JSONObject) tmpElement.get("lair");
                    String lairValue = (String) lair.get("value");
                    DragonLairItem DRAGONLAIR = new DragonLairItem(lairValue);
                    JSONObject label = (JSONObject) tmpElement.get("label");
                    String labelValue = (String) label.get("value");
                    DRAGONLAIR.setScheme(labelValue);
                    JSONObject legaltype = (JSONObject) tmpElement.get("legaltype");
                    String legaltypeValue = (String) legaltype.get("value");
                    DRAGONLAIR.setLegalType(legaltypeValue);
                    JSONObject type = (JSONObject) tmpElement.get("type");
                    String typeValue = (String) type.get("value");
                    DRAGONLAIR.setType(typeValue);
                    JSONObject quality = (JSONObject) tmpElement.get("quality");
                    String qualityValue = (String) quality.get("value");
                    DRAGONLAIR.setQuality(qualityValue);
                    JSONObject group = (JSONObject) tmpElement.get("group");
                    String groupValue = (String) group.get("value");
                    DRAGONLAIR.setGroup(groupValue);
                    JSONObject sameas = (JSONObject) tmpElement.get("sameas");
                    String sameasValue = (String) sameas.get("value");
                    DRAGONLAIR.setWikidataItem(sameasValue);
                    lairs.add(DRAGONLAIR.getDragonLairItem());
                }
            } else {
                throw new ResourceNotAvailableException();
            }
            return lairs;
        } catch (Exception e) {
            JSONParser parser = new JSONParser();
            JSONObject error = (JSONObject) parser.parse(Logging.getMessageJSON(e, "link.archaeology.re3dragon.action.Lair"));
            lairs.add(error);
            return lairs;
        }
    }

}

package org.mainzed.re3dragon.config;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import org.json.simple.JSONObject;

/**
 * Class for POM details
 * @author thiery
 */
public class POM {
    
    /**
     * get POM info as JSON
     * @return pom JSON
     * @throws IOException 
     */
    public static JSONObject getInfo() throws IOException {
        JSONObject outObj = new JSONObject();
        JSONObject maven = new JSONObject();
        maven.put("modelVersion", ConfigProperties.getPropertyParam("modelVersion"));
        outObj.put("maven", maven);
        JSONObject gitObject = new JSONObject();
        gitObject.put("buildNumber", ConfigProperties.getPropertyParam("buildNumber"));
        gitObject.put("buildNumberShort", ConfigProperties.getPropertyParam("buildNumber").substring(0, 7));
        gitObject.put("buildRepository", ConfigProperties.getPropertyParam("url").replace(".git", "/tree/" + ConfigProperties.getPropertyParam("buildNumber")));
        gitObject.put("url", ConfigProperties.getPropertyParam("url"));
        outObj.put("git", gitObject);
        JSONObject project = new JSONObject();
        project.put("artifactId", ConfigProperties.getPropertyParam("artifactId"));
        project.put("groupId", ConfigProperties.getPropertyParam("groupId"));
        project.put("version", ConfigProperties.getPropertyParam("version"));
        project.put("packaging", ConfigProperties.getPropertyParam("packaging"));
        project.put("name", ConfigProperties.getPropertyParam("name"));
        project.put("description", ConfigProperties.getPropertyParam("description"));
        project.put("encoding", ConfigProperties.getPropertyParam("sourceEncoding"));
        outObj.put("project", project);
        JSONObject warObject = new JSONObject();
        File file = new File(POM.class.getClassLoader().getResource("config.properties").getFile());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        warObject.put("last build", sdf.format(file.lastModified()));
        outObj.put("war", warObject);
        return outObj;
    }

}

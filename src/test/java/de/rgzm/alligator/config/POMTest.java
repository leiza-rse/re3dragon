package de.rgzm.alligator.config;

import org.mainzed.re3dragon.config.POM;
import org.mainzed.re3dragon.config.ConfigProperties;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Testing Class
 * @author thiery
 */
public class POMTest {
    
    public POMTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testPOMInfoNotNull() throws Exception {
        System.out.println("testPOMInfoNotNull");
        JSONObject info = POM.getInfo();
        assertNotNull(info);
    }
    
    @Test
    public void testLoadPomInfoAndPackagingIsWAR() throws Exception {
        System.out.println("testLoadPomInfoAndPackagingIsWAR");
        String packaging = ConfigProperties.getPropertyParam("packaging");
        assertEquals("war",packaging);
    }
    
}

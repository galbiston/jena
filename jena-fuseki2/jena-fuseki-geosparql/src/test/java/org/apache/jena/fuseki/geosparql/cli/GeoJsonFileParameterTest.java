/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jena.fuseki.geosparql.cli;

import com.beust.jcommander.ParameterException;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 *
 */
public class GeoJsonFileParameterTest {

    public GeoJsonFileParameterTest() {
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

    /**
     * Test of convert method, of class RDFFileParameter.
     */
    @Test
    public void testConvert() {
        System.out.println("convert");
        String value = "test.json|http://example.org/geoJsonTest#>test,test2.rdf|http://example.org/geoJsonTest#";
        GeoJsonFileParameter instance = new GeoJsonFileParameter();
        List<GeoJsonFileBaseGraph> expResult = Arrays.asList(new GeoJsonFileBaseGraph(new File("test.json"), "http://example.org/geoJsonTest#", "test"), new GeoJsonFileBaseGraph(new File("test2.rdf"), "http://example.org/geoJsonTest#", ""));
        List<GeoJsonFileBaseGraph> result = instance.convert(value);

        //System.out.println("Exp: " + expResult);
        //System.out.println("Res: " + result);
        assertEquals(expResult, result);
    }

    /**
     * Test of build method, of class RDFFileParameter.
     */
    @Test
    public void testBuild() {
        System.out.println("build");
        String value = "geospatial.json>test";
        GeoJsonFileParameter instance = new GeoJsonFileParameter();
        GeoJsonFileBaseGraph expResult = new GeoJsonFileBaseGraph(new File("test.rdf"), "", "test");
        GeoJsonFileBaseGraph result = instance.build(value);

        //System.out.println("Exp: " + expResult);
        //System.out.println("Res: " + result);
        assertEquals(expResult, result);
    }

    /**
     * Test of validate method, of class RDFFileParameter.
     */
    @Test(expected = ParameterException.class)
    public void testValidate() {
        System.out.println("validate");
        String name = "--geojson_file";
        String value = "test.geojson>xml|test";
        GeoJsonFileParameter instance = new GeoJsonFileParameter();
        instance.validate(name, value);
    }

}

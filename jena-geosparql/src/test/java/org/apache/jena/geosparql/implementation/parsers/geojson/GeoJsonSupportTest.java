/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.jena.geosparql.implementation.parsers.geojson;

import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonNumber;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.geosparql.implementation.datatype.GeoJsonDatatype;
import org.apache.jena.geosparql.implementation.vocabulary.Geo;
import org.apache.jena.geosparql.implementation.vocabulary.GeoSPARQL_URI;
import org.apache.jena.geosparql.implementation.vocabulary.SRS_URI;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Gerg
 */
public class GeoJsonSupportTest {

    private static final String BASE_URI = "http://example.org/TestData#";

    public GeoJsonSupportTest() {
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

    private JsonObject createPointGeometry() {
        JsonObject geometry = new JsonObject();

        geometry.put("type", "Point");
        geometry.put("srsURI", SRS_URI.OSGB36_CRS);
        JsonArray coords = new JsonArray();
        coords.add(JsonNumber.value(102.0));
        coords.add(JsonNumber.value(0.5));
        geometry.put("coordinates", coords);

        return geometry;
    }

    private JsonObject createPointFeature() {
        JsonObject feature = new JsonObject();
        feature.put("type", "Feature");
        feature.put("id", "PointA");
        feature.put("geometry", createPointGeometry());
        JsonObject properties = new JsonObject();
        properties.put("prop0", "value0");
        feature.put("properties", properties);
        return feature;
    }

    private void addPointResource(Model model) {

        Resource feature = model.createResource(BASE_URI + "PointA");
        feature.addProperty(RDF.type, Geo.FEATURE_RES);
        feature.addLiteral(ResourceFactory.createProperty(BASE_URI, "id"), "PointA");
        feature.addLiteral(ResourceFactory.createProperty(BASE_URI, "prop0"), "value0");

        Resource geometry = model.createResource(BASE_URI + "PointA-Geometry");
        geometry.addProperty(RDF.type, Geo.GEOMETRY_RES);
        feature.addProperty(Geo.HAS_GEOMETRY_PROP, geometry);
        feature.addProperty(Geo.HAS_DEFAULT_GEOMETRY_PROP, geometry);

        Literal geometryLiteral = ResourceFactory.createTypedLiteral("{ \n"
                + "  \"type\" : \"Point\" ,\n"
                + "  \"srsURI\" : \"http://www.opengis.net/def/crs/EPSG/0/27700\" ,\n"
                + "  \"coordinates\" : [ \n"
                + "      102.0 ,\n"
                + "      0.5\n"
                + "    ]\n"
                + "}", GeoJsonDatatype.INSTANCE);
        geometry.addProperty(Geo.AS_GEO_JSON_PROP, geometryLiteral);
        geometry.addProperty(Geo.HAS_SERIALIZATION_PROP, geometryLiteral);
    }

    private JsonObject createLineStringGeometry() {
        JsonObject geometry = new JsonObject();

        geometry.put("type", "LineString");
        geometry.put("uri", BASE_URI + "MyLineString");
        JsonArray coords = new JsonArray();

        JsonArray coords1 = new JsonArray();
        coords1.add(JsonNumber.value(102.0));
        coords1.add(JsonNumber.value(0.0));
        coords.add(coords1);

        JsonArray coords2 = new JsonArray();
        coords2.add(JsonNumber.value(103.0));
        coords2.add(JsonNumber.value(1.0));
        coords.add(coords2);

        JsonArray coords3 = new JsonArray();
        coords3.add(JsonNumber.value(104.0));
        coords3.add(JsonNumber.value(0.0));
        coords.add(coords3);

        JsonArray coords4 = new JsonArray();
        coords4.add(JsonNumber.value(105.0));
        coords4.add(JsonNumber.value(1.0));
        coords.add(coords4);

        geometry.put("coordinates", coords);
        return geometry;
    }

    private JsonObject createLineStringFeature() {
        JsonObject feature = new JsonObject();
        feature.put("type", "Feature");
        feature.put("geometry", createLineStringGeometry());

        JsonObject properties = new JsonObject();
        properties.put("prop1", JsonNumber.value(0.0));
        feature.put("properties", properties);
        return feature;
    }

    private void addLineStringResource(Model model) {

        Resource feature = model.createResource(BASE_URI + "Feature0");
        feature.addProperty(RDF.type, Geo.FEATURE_RES);
        Literal prop1Lit = ResourceFactory.createTypedLiteral("0.0", XSDDatatype.XSDdecimal);
        feature.addLiteral(ResourceFactory.createProperty(BASE_URI, "prop1"), prop1Lit);

        Resource geometry = model.createResource(BASE_URI + "MyLineString");
        geometry.addProperty(RDF.type, Geo.GEOMETRY_RES);
        feature.addProperty(Geo.HAS_GEOMETRY_PROP, geometry);
        feature.addProperty(Geo.HAS_DEFAULT_GEOMETRY_PROP, geometry);

        Literal geometryLiteral = ResourceFactory.createTypedLiteral("{ \n"
                + "  \"type\" : \"LineString\" ,\n"
                + "  \"uri\" : \"http://example.org/TestData#MyLineString\" ,\n"
                + "  \"coordinates\" : [ \n"
                + "      [ 102.0 ,\n"
                + "        0.0\n"
                + "      ] ,\n"
                + "      [ 103.0 ,\n"
                + "        1.0\n"
                + "      ] ,\n"
                + "      [ 104.0 ,\n"
                + "        0.0\n"
                + "      ] ,\n"
                + "      [ 105.0 ,\n"
                + "        1.0\n"
                + "      ]\n"
                + "    ]\n"
                + "}", GeoJsonDatatype.INSTANCE);
        geometry.addProperty(Geo.AS_GEO_JSON_PROP, geometryLiteral);
        geometry.addProperty(Geo.HAS_SERIALIZATION_PROP, geometryLiteral);
    }

    private JsonObject createPolygonGeometry() {
        JsonObject geometry = new JsonObject();

        geometry.put("type", "Polygon");
        JsonArray shell = new JsonArray();

        JsonArray coords1 = new JsonArray();
        coords1.add(JsonNumber.value(100.0));
        coords1.add(JsonNumber.value(0.0));
        shell.add(coords1);

        JsonArray coords2 = new JsonArray();
        coords2.add(JsonNumber.value(101.0));
        coords2.add(JsonNumber.value(0.0));
        shell.add(coords2);

        JsonArray coords3 = new JsonArray();
        coords3.add(JsonNumber.value(101.0));
        coords3.add(JsonNumber.value(1.0));
        shell.add(coords3);

        JsonArray coords4 = new JsonArray();
        coords4.add(JsonNumber.value(100.0));
        coords4.add(JsonNumber.value(1.0));
        shell.add(coords4);

        JsonArray coords5 = new JsonArray();
        coords5.add(JsonNumber.value(100.0));
        coords5.add(JsonNumber.value(0.0));
        shell.add(coords5);

        JsonArray polygon = new JsonArray();
        polygon.add(shell);
        geometry.put("coordinates", polygon);
        return geometry;
    }

    private JsonObject createPolygonFeature() {
        JsonObject feature = new JsonObject();
        feature.put("type", "Feature");
        feature.put("geometry", createPolygonGeometry());

        JsonObject properties = new JsonObject();
        properties.put("prop0", "value0");
        properties.put("prop1", JsonNumber.value(0.5));
        feature.put("properties", properties);
        return feature;
    }

    private void addPolygonResource(Model model) {

        Resource feature = model.createResource(BASE_URI + "Feature1");
        feature.addProperty(RDF.type, Geo.FEATURE_RES);
        feature.addLiteral(ResourceFactory.createProperty(BASE_URI, "prop0"), "value0");
        Literal prop1Lit = ResourceFactory.createTypedLiteral("0.5", XSDDatatype.XSDdecimal);
        feature.addLiteral(ResourceFactory.createProperty(BASE_URI, "prop1"), prop1Lit);

        Resource geometry = model.createResource(BASE_URI + "Feature1-Geometry");
        geometry.addProperty(RDF.type, Geo.GEOMETRY_RES);
        feature.addProperty(Geo.HAS_GEOMETRY_PROP, geometry);
        feature.addProperty(Geo.HAS_DEFAULT_GEOMETRY_PROP, geometry);

        Literal geometryLiteral = ResourceFactory.createTypedLiteral("{ \n"
                + "  \"type\" : \"Polygon\" ,\n"
                + "  \"coordinates\" : [ \n"
                + "      [ [ 100.0 ,\n"
                + "          0.0\n"
                + "        ] ,\n"
                + "        [ 101.0 ,\n"
                + "          0.0\n"
                + "        ] ,\n"
                + "        [ 101.0 ,\n"
                + "          1.0\n"
                + "        ] ,\n"
                + "        [ 100.0 ,\n"
                + "          1.0\n"
                + "        ] ,\n"
                + "        [ 100.0 ,\n"
                + "          0.0\n"
                + "        ]\n"
                + "      ]\n"
                + "    ]\n"
                + "}", GeoJsonDatatype.INSTANCE);
        geometry.addProperty(Geo.AS_GEO_JSON_PROP, geometryLiteral);
        geometry.addProperty(Geo.HAS_SERIALIZATION_PROP, geometryLiteral);
    }

    /**
     * Test of convert method, of class GeoJsonSupport.
     */
    @Test
    public void testConvert() {
        System.out.println("convert");
        JsonObject rootObject = new JsonObject();
        rootObject.put("type", "FeatureCollection");

        JsonArray features = new JsonArray();
        rootObject.put("features", features);
        features.add(createPointFeature());
        features.add(createLineStringFeature());
        features.add(createPolygonFeature());

        Model convertedModel = GeoJsonSupport.convert(rootObject, BASE_URI);

        Model testModel = ModelFactory.createDefaultModel();
        testModel.setNsPrefixes(GeoSPARQL_URI.getPrefixes());
        addPointResource(testModel);
        addLineStringResource(testModel);
        addPolygonResource(testModel);

        boolean expResult = true;
        boolean testInConverted = convertedModel.containsAll(testModel);
        boolean convertedInTest = convertedModel.containsAll(testModel);
        boolean result = testInConverted && convertedInTest;

        System.out.println("Exp: " + testModel);
        System.out.println("Res: " + convertedModel);

        assertEquals(expResult, result);
    }

}

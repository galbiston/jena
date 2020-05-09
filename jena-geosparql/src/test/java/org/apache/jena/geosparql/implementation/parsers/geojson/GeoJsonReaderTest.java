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
package org.apache.jena.geosparql.implementation.parsers.geojson;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonNumber;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;
import org.apache.jena.datatypes.DatatypeFormatException;
import org.apache.jena.geosparql.implementation.DimensionInfo;
import org.apache.jena.geosparql.implementation.jts.CoordinateSequenceDimensions;
import org.apache.jena.geosparql.implementation.jts.CustomCoordinateSequence;
import org.apache.jena.geosparql.implementation.jts.CustomGeometryFactory;
import org.apache.jena.geosparql.implementation.vocabulary.SRS_URI;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

/**
 *
 *
 */
public class GeoJsonReaderTest {

    private static final GeometryFactory GEOMETRY_FACTORY = CustomGeometryFactory.theInstance();

    public GeoJsonReaderTest() {
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

    private JsonArray buildArrayDouble(List<Double> coords) {

        JsonArray jsonArray = new JsonArray();
        for (Double coord : coords) {
            jsonArray.add(JsonNumber.value(coord));
        }
        return jsonArray;
    }

    private JsonArray buildArrayInt(List<Integer> coords) {

        JsonArray jsonArray = new JsonArray();
        for (Integer coord : coords) {
            jsonArray.add(JsonNumber.value(coord));
        }
        return jsonArray;
    }

    /**
     * Test of getDimensionInfo method, of class GeoJsonReader.
     */
    @Test
    public void testGetDimensionInfo0() {
        Iterator<JsonValue> point = buildArrayDouble(Arrays.asList(11.0, 12.0)).iterator();
        GeoJsonReader instance = new GeoJsonReader("Point", point);
        DimensionInfo expResult = new DimensionInfo(2, 2, 0);
        DimensionInfo result = instance.getDimensionInfo();

        assertEquals(expResult, result);
    }

    /**
     * Test of getDimensionInfo method, of class GeoJsonReader.
     */
    @Test
    public void testGetDimensionInfo3a() {

        Iterator<JsonValue> point3 = buildArrayDouble(Arrays.asList(11.0, 12.0, 8.0)).iterator();
        GeoJsonReader instance = new GeoJsonReader("Point", point3);
        DimensionInfo expResult = new DimensionInfo(3, 3, 0);
        DimensionInfo result = instance.getDimensionInfo();

        assertEquals(expResult, result);
    }

    /**
     * Test of getGeometry method, of class GeoJsonReader.
     */
    @Test
    public void testGetGeometryPoint() {

        Iterator<JsonValue> point = buildArrayDouble(Arrays.asList(11.0, 12.0)).iterator();
        GeoJsonReader instance = new GeoJsonReader("Point", point);
        Geometry expResult = GEOMETRY_FACTORY.createPoint(new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "11.0 12.0"));
        Geometry result = instance.getGeometry();

        assertEquals(expResult, result);
    }

    /**
     * Test of getGeometry method, of class GeoJsonReader.
     */
    @Test
    public void testGetGeometryPointZ() {

        Iterator<JsonValue> point3 = buildArrayDouble(Arrays.asList(11.0, 12.0, 8.0)).iterator();
        GeoJsonReader instance = new GeoJsonReader("Point", point3);
        Geometry expResult = GEOMETRY_FACTORY.createPoint(new CustomCoordinateSequence(CoordinateSequenceDimensions.XYZ, "11.0 12.0 8.0"));
        Geometry result = instance.getGeometry();

        assertEquals(expResult, result);
    }

    /**
     * Test of extract method, of class GeoJsonReader.
     */
    @Test
    public void testExtractPoint2() {

        Iterator<JsonValue> point = buildArrayDouble(Arrays.asList(11.0, 12.0)).iterator();
        String geosonText = "{ \"type\": \"Point\", \"coordinates\": [11.0, 12.0] }";
        GeoJsonReader expResult = new GeoJsonReader("Point", point);
        GeoJsonReader result = GeoJsonReader.extract(geosonText);

        assertEquals(expResult, result);
    }

    /**
     * Test of extract method, of class GeoJsonReader.
     */
    @Test
    public void testExtractPoint3() {

        Iterator<JsonValue> point3 = buildArrayDouble(Arrays.asList(11.0, 12.0, 8.0)).iterator();
        String geosonText = "{ \"type\": \"Point\", \"coordinates\": [11.0, 12.0, 8.0] }";
        GeoJsonReader expResult = new GeoJsonReader("Point", point3);
        GeoJsonReader result = GeoJsonReader.extract(geosonText);

        assertEquals(expResult, result);
    }

    /**
     * Test of extract method, of class GeoJsonReader.
     */
    @Test
    public void testExtractPoint_SRS_URI() {

        Iterator<JsonValue> point3 = buildArrayDouble(Arrays.asList(11.0, 12.0, 8.0)).iterator();
        String geosonText = "{ \"type\": \"Point\", \"coordinates\": [11.0, 12.0, 8.0], \"srsURI\": \"http://www.opengis.net/def/crs/EPSG/0/27700\" }";
        GeoJsonReader expResult = new GeoJsonReader("Point", point3, SRS_URI.OSGB36_CRS);
        GeoJsonReader result = GeoJsonReader.extract(geosonText);

        assertEquals(expResult, result);
    }

    /**
     * Test of extract method, of class GeoJsonReader.
     */
    @Test
    public void testExtractLineString() {

        String geosonText = "{ \"type\": \"LineString\", \"coordinates\": [[30, 10], [10, 30], [40, 40]] }";

        JsonArray lineString = new JsonArray();
        lineString.add(buildArrayInt(Arrays.asList(30, 10)));
        lineString.add(buildArrayInt(Arrays.asList(10, 30)));
        lineString.add(buildArrayInt(Arrays.asList(40, 40)));

        GeoJsonReader expResult = new GeoJsonReader("LineString", lineString.iterator());
        GeoJsonReader result = GeoJsonReader.extract(geosonText);

        assertEquals(expResult, result);
    }

    /**
     * Test of extract method, of class GeoJsonReader.
     */
    @Test
    public void testExtractPolygon() {

        String geosonText = "{ \"type\": \"Polygon\", \"coordinates\": [[[30, 10], [40, 40], [20, 40], [10, 20], [30, 10]]] }";

        JsonArray shell = new JsonArray();
        shell.add(buildArrayInt(Arrays.asList(30, 10)));
        shell.add(buildArrayInt(Arrays.asList(40, 40)));
        shell.add(buildArrayInt(Arrays.asList(20, 40)));
        shell.add(buildArrayInt(Arrays.asList(10, 20)));
        shell.add(buildArrayInt(Arrays.asList(30, 10)));

        JsonArray polygon = new JsonArray();
        polygon.add(shell);

        GeoJsonReader expResult = new GeoJsonReader("Polygon", polygon.iterator());
        GeoJsonReader result = GeoJsonReader.extract(geosonText);

        assertEquals(expResult, result);
    }

    /**
     * Test of extract method, of class GeoJsonReader.
     */
    @Test
    public void testExtractPolygonHole() {

        String geosonText = "{ \"type\": \"Polygon\", \"coordinates\": [[[35, 10], [45, 45], [15, 40], [10, 20], [35, 10]], [[20, 30], [35, 35], [30, 20], [20, 30]]]}";

        JsonArray shell = new JsonArray();
        shell.add(buildArrayInt(Arrays.asList(35, 10)));
        shell.add(buildArrayInt(Arrays.asList(45, 45)));
        shell.add(buildArrayInt(Arrays.asList(15, 40)));
        shell.add(buildArrayInt(Arrays.asList(10, 20)));
        shell.add(buildArrayInt(Arrays.asList(35, 10)));

        JsonArray hole = new JsonArray();
        hole.add(buildArrayInt(Arrays.asList(20, 30)));
        hole.add(buildArrayInt(Arrays.asList(35, 35)));
        hole.add(buildArrayInt(Arrays.asList(30, 20)));
        hole.add(buildArrayInt(Arrays.asList(20, 30)));

        JsonArray polygon = new JsonArray();
        polygon.add(shell);
        polygon.add(hole);

        GeoJsonReader expResult = new GeoJsonReader("Polygon", polygon.iterator());
        GeoJsonReader result = GeoJsonReader.extract(geosonText);

        assertEquals(expResult, result);
    }

    /**
     * Test of extract method, of class GeoJsonReader.
     */
    @Test
    public void testExtractMultiPoint() {

        String geosonText = "{ \"type\": \"MultiPoint\", \"coordinates\": [[30, 10], [10, 30], [40, 40]] }";
        JsonArray points = new JsonArray();
        points.add(buildArrayInt(Arrays.asList(30, 10)));
        points.add(buildArrayInt(Arrays.asList(10, 30)));
        points.add(buildArrayInt(Arrays.asList(40, 40)));
        GeoJsonReader expResult = new GeoJsonReader("MultiPoint", points.iterator());
        GeoJsonReader result = GeoJsonReader.extract(geosonText);

        assertEquals(expResult, result);
    }

    /**
     * Test of extract method, of class GeoJsonReader.
     */
    @Test
    public void testExtractMutliLineString() {

        String geosonText = "{ \"type\": \"MultiLineString\", \"coordinates\": [[[10, 10], [20, 20], [10, 40]], [[40, 40], [30, 30], [40, 20], [30, 10]]] }";

        JsonArray lineString = new JsonArray();
        lineString.add(buildArrayInt(Arrays.asList(10, 10)));
        lineString.add(buildArrayInt(Arrays.asList(20, 20)));
        lineString.add(buildArrayInt(Arrays.asList(10, 40)));

        JsonArray lineString2 = new JsonArray();
        lineString2.add(buildArrayInt(Arrays.asList(40, 40)));
        lineString2.add(buildArrayInt(Arrays.asList(30, 30)));
        lineString2.add(buildArrayInt(Arrays.asList(40, 20)));
        lineString2.add(buildArrayInt(Arrays.asList(30, 10)));

        JsonArray multiLineString = new JsonArray();
        multiLineString.add(lineString);
        multiLineString.add(lineString2);

        GeoJsonReader expResult = new GeoJsonReader("MultiLineString", multiLineString.iterator());
        GeoJsonReader result = GeoJsonReader.extract(geosonText);

        assertEquals(expResult, result);
    }

    /**
     * Test of extract method, of class GeoJsonReader.
     */
    @Test
    public void testExtractMultiPolygon() {

        String geosonText = "{ \"type\": \"MultiPolygon\", \"coordinates\": [[[[30, 20], [45, 40], [10, 40], [30, 20]]], [[[15, 5], [40, 10], [10, 20], [5, 10], [15, 5]]]] }";

        JsonArray polygon = new JsonArray();
        JsonArray shell = new JsonArray();
        shell.add(buildArrayInt(Arrays.asList(30, 20)));
        shell.add(buildArrayInt(Arrays.asList(45, 40)));
        shell.add(buildArrayInt(Arrays.asList(10, 40)));
        shell.add(buildArrayInt(Arrays.asList(30, 20)));
        polygon.add(shell);

        JsonArray polygon2 = new JsonArray();
        JsonArray shell2 = new JsonArray();
        shell2.add(buildArrayInt(Arrays.asList(15, 5)));
        shell2.add(buildArrayInt(Arrays.asList(40, 10)));
        shell2.add(buildArrayInt(Arrays.asList(10, 20)));
        shell2.add(buildArrayInt(Arrays.asList(5, 10)));
        shell2.add(buildArrayInt(Arrays.asList(15, 5)));
        polygon2.add(shell2);

        JsonArray multiPolygon = new JsonArray();
        multiPolygon.add(polygon);
        multiPolygon.add(polygon2);

        GeoJsonReader expResult = new GeoJsonReader("MultiPolygon", multiPolygon.iterator());
        GeoJsonReader result = GeoJsonReader.extract(geosonText);

        assertEquals(expResult, result);
    }

    /**
     * Test of extract method, of class GeoJsonReader.
     */
    @Test
    public void testExtractMultiPolygon2() {

        String geosonText = "{ \"type\": \"MultiPolygon\", \"coordinates\": [[[[40, 40], [20, 45], [45, 30], [40, 40]]], [[[20, 35], [10, 30], [10, 10], [30, 5], [45, 20], [20, 35]], [[30, 20], [20, 15], [20, 25], [30, 20]]]] }";

        JsonArray polygon = new JsonArray();
        JsonArray shell = new JsonArray();
        shell.add(buildArrayInt(Arrays.asList(40, 40)));
        shell.add(buildArrayInt(Arrays.asList(20, 45)));
        shell.add(buildArrayInt(Arrays.asList(45, 30)));
        shell.add(buildArrayInt(Arrays.asList(40, 40)));
        polygon.add(shell);

        JsonArray polygon2 = new JsonArray();
        JsonArray shell2 = new JsonArray();
        shell2.add(buildArrayInt(Arrays.asList(20, 35)));
        shell2.add(buildArrayInt(Arrays.asList(10, 30)));
        shell2.add(buildArrayInt(Arrays.asList(10, 10)));
        shell2.add(buildArrayInt(Arrays.asList(30, 5)));
        shell2.add(buildArrayInt(Arrays.asList(45, 20)));
        shell2.add(buildArrayInt(Arrays.asList(20, 35)));
        polygon2.add(shell2);

        JsonArray hole2 = new JsonArray();
        hole2.add(buildArrayInt(Arrays.asList(30, 20)));
        hole2.add(buildArrayInt(Arrays.asList(20, 15)));
        hole2.add(buildArrayInt(Arrays.asList(20, 25)));
        hole2.add(buildArrayInt(Arrays.asList(30, 20)));
        polygon2.add(hole2);

        JsonArray multiPolygon = new JsonArray();
        multiPolygon.add(polygon);
        multiPolygon.add(polygon2);

        GeoJsonReader expResult = new GeoJsonReader("MultiPolygon", multiPolygon.iterator());
        GeoJsonReader result = GeoJsonReader.extract(geosonText);

        assertEquals(expResult, result);
    }

    /**
     * Test of extract method, of class GeoJsonReader.
     */
    @Test
    public void testExtractGeometryCollection() {

        String geosonText = "{ \"type\": \"GeometryCollection\", \"geometries\": [ { \"type\": \"Point\", \"coordinates\": [40, 10] }, { \"type\": \"LineString\", \"coordinates\": [ [10, 10], [20, 20], [10, 40] ] }, { \"type\": \"MultiPoint\", \"coordinates\": [ [40, 40], [20, 45], [45, 30], [40, 40] ] } ] }";
        JsonArray point = buildArrayInt(Arrays.asList(40, 10));
        JsonObject pointObj = new JsonObject();
        pointObj.put("type", "Point");
        pointObj.put("coordinates", point);

        JsonArray lineString = new JsonArray();
        lineString.add(buildArrayInt(Arrays.asList(10, 10)));
        lineString.add(buildArrayInt(Arrays.asList(20, 20)));
        lineString.add(buildArrayInt(Arrays.asList(10, 40)));
        JsonObject lineStringObj = new JsonObject();
        lineStringObj.put("type", "LineString");
        lineStringObj.put("coordinates", lineString);

        JsonArray multiPoint = new JsonArray();
        multiPoint.add(buildArrayInt(Arrays.asList(40, 40)));
        multiPoint.add(buildArrayInt(Arrays.asList(20, 45)));
        multiPoint.add(buildArrayInt(Arrays.asList(45, 30)));
        multiPoint.add(buildArrayInt(Arrays.asList(40, 40)));
        JsonObject multiPointObj = new JsonObject();
        multiPointObj.put("type", "MultiPoint");
        multiPointObj.put("coordinates", multiPoint);

        JsonArray collection = new JsonArray();
        collection.add(pointObj);
        collection.add(lineStringObj);
        collection.add(multiPointObj);

        GeoJsonReader expResult = new GeoJsonReader("GeometryCollection", collection.iterator());
        GeoJsonReader result = GeoJsonReader.extract(geosonText);

        assertEquals(expResult, result);
    }

    /**
     * Test of extract method, of class GeoJsonReader.
     */
    @Test(expected = DatatypeFormatException.class)
    public void testMissingGeometries() {

        String geosonText = "{ \"type\": \"GeometryCollection\", \"coordinates\": [ { \"type\": \"Point\", \"coordinates\": [40, 10] }] }";
        JsonArray point = buildArrayInt(Arrays.asList(40, 10));
        JsonObject pointObj = new JsonObject();
        pointObj.put("type", "Point");
        pointObj.put("coordinates", point);

        JsonArray collection = new JsonArray();
        collection.add(pointObj);

        GeoJsonReader expResult = new GeoJsonReader("GeometryCollection", collection.iterator());
        GeoJsonReader result = GeoJsonReader.extract(geosonText);

        assertEquals(expResult, result);
    }

    /**
     * Test of buildPointEmpty method, of class GeoJsonReader.
     */
    @Test
    public void testBuildPointEmpty() {

        GeoJsonReader instance = GeoJsonReader.extract("{ \"type\": \"Point\", \"coordinates\": [] }");
        Geometry result = instance.getGeometry();

        CustomCoordinateSequence PointSequence = new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "");
        Geometry expResult = GEOMETRY_FACTORY.createPoint(PointSequence);

        assertEquals(expResult, result);
    }

    /**
     * Test of buildLineStringEmpty method, of class GeoJsonReader.
     */
    @Test
    public void testBuildLineStringEmpty() {

        GeoJsonReader instance = GeoJsonReader.extract("{ \"type\": \"LineString\", \"coordinates\": [] }");
        Geometry result = instance.getGeometry();

        CustomCoordinateSequence PointSequence = new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "");
        Geometry expResult = GEOMETRY_FACTORY.createLineString(PointSequence);

        assertEquals(expResult, result);
    }

    /**
     * Test of buildPolygonEmpty method, of class GeoJsonReader.
     */
    @Test
    public void testBuildPolygonEmpty() {

        GeoJsonReader instance = GeoJsonReader.extract("{ \"type\": \"Polygon\", \"coordinates\": [] }");
        Geometry result = instance.getGeometry();

        CustomCoordinateSequence PointSequence = new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "");
        Geometry expResult = GEOMETRY_FACTORY.createPolygon(PointSequence);

        assertEquals(expResult, result);
    }

    /**
     * Test of buildMultiPointEmpty method, of class GeoJsonReader.
     */
    @Test
    public void testBuildMultiPointEmpty() {

        GeoJsonReader instance = GeoJsonReader.extract("{ \"type\": \"MultiPoint\", \"coordinates\": [] }");
        Geometry result = instance.getGeometry();

        Geometry expResult = GEOMETRY_FACTORY.createMultiPoint(new Point[0]);

        assertEquals(expResult, result);
    }

    /**
     * Test of buildMultiLineString method, of class GeoJsonReader.
     */
    @Test
    public void testBuildMultiLineStringEmpty() {

        GeoJsonReader instance = GeoJsonReader.extract("{ \"type\": \"MultiLineString\", \"coordinates\": [] }");
        Geometry result = instance.getGeometry();

        Geometry expResult = GEOMETRY_FACTORY.createMultiLineString(new LineString[0]);

        assertEquals(expResult, result);
    }

    /**
     * Test of buildMultiPolygonEmpty method, of class GeoJsonReader.
     */
    @Test
    public void testBuildMultiPolygonEmpty() {

        GeoJsonReader instance = GeoJsonReader.extract("{ \"type\": \"MultiPolygon\", \"coordinates\": [] }");
        Geometry result = instance.getGeometry();

        Geometry expResult = GEOMETRY_FACTORY.createMultiPolygon(new Polygon[0]);

        assertEquals(expResult, result);
    }

    /**
     * Test of buildGeometryCollectionEmpty method, of class GeoJsonReader.
     */
    @Test
    public void testBuildGeometryCollectionEmpty() {

        GeoJsonReader instance = GeoJsonReader.extract("{ \"type\": \"GeometryCollection\", \"geometries\": [] }");
        Geometry result = instance.getGeometry();

        Geometry expResult = GEOMETRY_FACTORY.createGeometryCollection(new Geometry[0]);

        assertEquals(expResult, result);
    }

    /**
     * Test of buildEmpty method, of class GeoJsonReader.<br>
     */
    @Test
    public void testBuildEmpty() {

        GeoJsonReader instance = GeoJsonReader.extract("");
        Geometry result = instance.getGeometry();

        CustomCoordinateSequence PointSequence = new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "");
        Geometry expResult = GEOMETRY_FACTORY.createPoint(PointSequence);

        assertEquals(expResult, result);
    }

    /**
     * Test of buildEmpty method, of class GeoJsonReader.<br>
     */
    @Test
    public void testBuildEmpty2() {

        GeoJsonReader instance = GeoJsonReader.extract("{}");
        Geometry result = instance.getGeometry();

        CustomCoordinateSequence PointSequence = new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "");
        Geometry expResult = GEOMETRY_FACTORY.createPoint(PointSequence);

        assertEquals(expResult, result);
    }

    /**
     * Test of DatatypeFormatException of class GeoJsonReader.
     */
    @Test(expected = DatatypeFormatException.class)
    public void testMissingCoordinate() {

        GeoJsonReader instance = GeoJsonReader.extract(" \"type\": \"Point\", \"coordinates\": [-83.38] }");
    }

    /**
     * Test of DatatypeFormatException of class GeoJsonReader.
     */
    @Test(expected = DatatypeFormatException.class)
    public void testFourthCoordinate() {

        GeoJsonReader instance = GeoJsonReader.extract(" \"type\": \"Point\", \"coordinates\": [-83.38, 20, 1, 3] }");
    }

    /**
     * Test of DatatypeFormatException of class GeoJsonReader.
     */
    @Test(expected = DatatypeFormatException.class)
    public void testMissingStartBrace() {

        GeoJsonReader instance = GeoJsonReader.extract(" \"type\": \"Point\", \"coordinates\": [-83.38, 33.95] }");
    }

    /**
     * Test of DatatypeFormatException of class GeoJsonReader.
     */
    @Test(expected = DatatypeFormatException.class)
    public void testMissingEndBrace() {

        GeoJsonReader instance = GeoJsonReader.extract("{ \"type\": \"Point\", \"coordinates\": [-83.38, 33.95]");
    }

    /**
     * Test of DatatypeFormatException of class GeoJsonReader.
     */
    @Test(expected = DatatypeFormatException.class)
    public void testMissingTypeProperty() {

        GeoJsonReader instance = GeoJsonReader.extract("{ \"coordinates\": [-83.38, 33.95] }");
    }

    /**
     * Test of DatatypeFormatException of class GeoJsonReader.
     */
    @Test(expected = DatatypeFormatException.class)
    public void testMissingCoordinatesProperty() {

        GeoJsonReader instance = GeoJsonReader.extract("{ \"type\": \"Point\" }");
    }

    /**
     * Test of DatatypeFormatException of class GeoJsonReader.
     */
    @Test(expected = DatatypeFormatException.class)
    public void testMissingGeometriesProperty() {

        GeoJsonReader instance = GeoJsonReader.extract("{ \"type\": \"GeometryCollection\" }");
    }

    /**
     * Test of DatatypeFormatException of class GeoJsonReader.
     */
    @Test(expected = DatatypeFormatException.class)
    public void testInvalidType() {

        GeoJsonReader instance = GeoJsonReader.extract("{ \"type\": \"invalid\", \"coordinates\": [-83.38, 33.95] }");
    }

}

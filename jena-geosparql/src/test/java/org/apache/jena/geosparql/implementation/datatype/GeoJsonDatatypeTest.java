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
package org.apache.jena.geosparql.implementation.datatype;

import org.apache.jena.geosparql.configuration.GeoSPARQLConfig;
import org.apache.jena.geosparql.implementation.DimensionInfo;
import org.apache.jena.geosparql.implementation.GeometryWrapper;
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
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

/**
 *
 *
 */
public class GeoJsonDatatypeTest {

    public GeoJsonDatatypeTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        GeoSPARQLConfig.setupNoIndex();
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

    private static final GeometryFactory GEOMETRY_FACTORY = CustomGeometryFactory.theInstance();
    private static final GeoJsonDatatype GEO_JSON_DATATYPE = GeoJsonDatatype.INSTANCE;

    /**
     * Test of parse method, of class GeoJsonDatatype.
     */
    @Test
    public void testReadXYPoint() {

        String lexicalForm = "{ \"type\": \"Point\", \"coordinates\": [-83.38, 33.95] }";

        GeometryWrapper result = GEO_JSON_DATATYPE.parse(lexicalForm);

        Coordinate coord = new Coordinate(-83.38, 33.95);
        Point expGeometry = GEOMETRY_FACTORY.createPoint(coord);
        String expSRSURI = SRS_URI.DEFAULT_WKT_CRS84;

        DimensionInfo dimensionInfo = new DimensionInfo(2, 2, 0);

        GeometryWrapper expResult = new GeometryWrapper(expGeometry, expSRSURI, GeoJsonDatatype.URI, dimensionInfo, lexicalForm);

        System.out.println("Exp: " + expResult);
        System.out.println("Res: " + result);
        assertEquals(expResult, result);
    }

    /**
     * Test of read method, of class GeoJsonReader.
     */
    @Test
    public void testReadXYZPoint() {

        String lexicalForm = "{ \"type\": \"Point\", \"coordinates\": [-83.38, 33.95, 12.0] }";

        Geometry geometry = GEOMETRY_FACTORY.createPoint(new CustomCoordinateSequence(CoordinateSequenceDimensions.XYZ, "-83.38 33.95 12.0"));
        GeometryWrapper expResult = new GeometryWrapper(geometry, SRS_URI.DEFAULT_WKT_CRS84, GeoJsonDatatype.URI, new DimensionInfo(3, 3, 0));
        GeometryWrapper result = GEO_JSON_DATATYPE.read(lexicalForm);

        assertEquals(expResult, result);
    }

    /**
     * Test of read method, of class GeoJsonReader.
     */
    @Test
    public void testReadLineString() {

        String lexicalForm = "{ \"type\": \"LineString\", \"coordinates\": [ [30, 10], [10, 30], [40, 40] ] }";

        Geometry geometry = GEOMETRY_FACTORY.createLineString(new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "30 10, 10 30, 40 40"));
        GeometryWrapper expResult = new GeometryWrapper(geometry, SRS_URI.DEFAULT_WKT_CRS84, GeoJsonDatatype.URI, new DimensionInfo(2, 2, 1));
        GeometryWrapper result = GEO_JSON_DATATYPE.read(lexicalForm);

        assertEquals(expResult, result);
    }

    /**
     * Test of read method, of class GeoJsonReader.
     */
    @Test
    public void testReadPolygon() {

        String lexicalForm = "{ \"type\": \"Polygon\", \"coordinates\": [ [[30, 10], [40, 40], [20, 40], [10, 20], [30, 10]] ] }";

        Geometry geometry = GEOMETRY_FACTORY.createPolygon(new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "30 10, 40 40, 20 40, 10 20, 30 10"));
        GeometryWrapper expResult = new GeometryWrapper(geometry, SRS_URI.DEFAULT_WKT_CRS84, GeoJsonDatatype.URI, new DimensionInfo(2, 2, 2), lexicalForm);
        GeometryWrapper result = GEO_JSON_DATATYPE.read(lexicalForm);

        //
        //
        assertEquals(expResult, result);
    }

    /**
     * Test of read method, of class GeoJsonReader.
     */
    @Test
    public void testReadPolygon2() {

        String lexicalForm = "{ \"type\": \"Polygon\", \"coordinates\": [ [[35, 10], [45, 45], [15, 40], [10, 20], [35, 10]], [[20, 30], [35, 35], [30, 20], [20, 30]] ] }";

        LinearRing shell = GEOMETRY_FACTORY.createLinearRing(new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "35 10, 45 45, 15 40, 10 20, 35 10"));
        LinearRing[] holes = new LinearRing[]{GEOMETRY_FACTORY.createLinearRing(new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "20 30, 35 35, 30 20, 20 30"))};
        Geometry geometry = GEOMETRY_FACTORY.createPolygon(shell, holes);

        GeometryWrapper expResult = new GeometryWrapper(geometry, SRS_URI.DEFAULT_WKT_CRS84, GeoJsonDatatype.URI, new DimensionInfo(2, 2, 2));
        GeometryWrapper result = GEO_JSON_DATATYPE.read(lexicalForm);

        assertEquals(expResult, result);
    }

    /**
     * Test of read method, of class GeoJsonReader.
     */
    @Test
    public void testReadMultiPoint() {

        String lexicalForm = "{ \"type\": \"MultiPoint\", \"coordinates\": [ [10, 40], [40, 30], [20, 20], [30, 10] ] }";

        Geometry geometry = GEOMETRY_FACTORY.createMultiPoint(new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "10 40, 40 30, 20 20, 30 10"));
        GeometryWrapper expResult = new GeometryWrapper(geometry, SRS_URI.DEFAULT_WKT_CRS84, GeoJsonDatatype.URI, new DimensionInfo(2, 2, 0));
        GeometryWrapper result = GEO_JSON_DATATYPE.read(lexicalForm);

        assertEquals(expResult, result);
    }

    /**
     * Test of read method, of class GeoJsonReader.
     */
    @Test
    public void testReadMultiLineString() {

        String lexicalForm = "{ \"type\": \"MultiLineString\", \"coordinates\": [ [[10, 10], [20, 20], [10, 40]], [[40, 40], [30, 30], [40, 20], [30, 10]] ] }";

        LineString[] lineStrings = new LineString[2];
        lineStrings[0] = GEOMETRY_FACTORY.createLineString(new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "10 10, 20 20, 10 40"));
        lineStrings[1] = GEOMETRY_FACTORY.createLineString(new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "40 40, 30 30, 40 20, 30 10"));
        Geometry geometry = GEOMETRY_FACTORY.createMultiLineString(lineStrings);

        GeometryWrapper expResult = new GeometryWrapper(geometry, SRS_URI.DEFAULT_WKT_CRS84, GeoJsonDatatype.URI, new DimensionInfo(2, 2, 1));
        GeometryWrapper result = GEO_JSON_DATATYPE.read(lexicalForm);

        assertEquals(expResult, result);
    }

    /**
     * Test of read method, of class GeoJsonReader.
     */
    @Test
    public void testReadMultiPolygon() {

        String lexicalForm = "{ \"type\": \"MultiPolygon\", \"coordinates\": [ [ [[30, 20], [45, 40], [10, 40], [30, 20]] ], [ [[15, 5], [40, 10], [10, 20], [5, 10], [15, 5]] ] ] }";

        Polygon[] polygons = new Polygon[2];
        polygons[0] = GEOMETRY_FACTORY.createPolygon(new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "30 20, 45 40, 10 40, 30 20"));
        polygons[1] = GEOMETRY_FACTORY.createPolygon(new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "15 5, 40 10, 10 20, 5 10, 15 5"));
        Geometry geometry = GEOMETRY_FACTORY.createMultiPolygon(polygons);

        GeometryWrapper expResult = new GeometryWrapper(geometry, SRS_URI.DEFAULT_WKT_CRS84, GeoJsonDatatype.URI, new DimensionInfo(2, 2, 2));
        GeometryWrapper result = GEO_JSON_DATATYPE.read(lexicalForm);

        assertEquals(expResult, result);
    }

    /**
     * Test of read method, of class GeoJsonReader.
     */
    @Test
    public void testReadMultiPolygon2() {

        String lexicalForm = "{ \"type\": \"MultiPolygon\", \"coordinates\": [ [ [[40, 40], [20, 45], [45, 30], [40, 40]] ], [ [[20, 35], [10, 30], [10, 10], [30, 5], [45, 20], [20, 35]], [[30, 20], [20, 15], [20, 25], [30, 20]] ] ] }";

        Polygon[] polygons = new Polygon[2];
        polygons[0] = GEOMETRY_FACTORY.createPolygon(new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "40 40, 20 45, 45 30, 40 40"));
        LinearRing shell = GEOMETRY_FACTORY.createLinearRing(new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "20 35, 10 30, 10 10, 30 5, 45 20, 20 35"));
        LinearRing[] holes = new LinearRing[]{GEOMETRY_FACTORY.createLinearRing(new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "30 20, 20 15, 20 25, 30 20"))};
        polygons[1] = GEOMETRY_FACTORY.createPolygon(shell, holes);
        Geometry geometry = GEOMETRY_FACTORY.createMultiPolygon(polygons);

        GeometryWrapper expResult = new GeometryWrapper(geometry, SRS_URI.DEFAULT_WKT_CRS84, GeoJsonDatatype.URI, new DimensionInfo(2, 2, 2));
        GeometryWrapper result = GEO_JSON_DATATYPE.read(lexicalForm);

        assertEquals(expResult, result);
    }

    /**
     * Test of read method, of class GeoJsonReader.
     */
    @Test
    public void testReadGeometryCollection() {

        String lexicalForm = "{ \"type\": \"GeometryCollection\", \"geometries\": [ { \"type\": \"Point\", \"coordinates\": [40, 10] }, { \"type\": \"LineString\", \"coordinates\": [ [10, 10], [20, 20], [10, 40] ] }, { \"type\": \"Polygon\", \"coordinates\": [ [[40, 40], [20, 45], [45, 30], [40, 40]] ] } ] }";

        Geometry[] geometries = new Geometry[3];
        geometries[0] = GEOMETRY_FACTORY.createPoint(new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "40 10"));
        geometries[1] = GEOMETRY_FACTORY.createLineString(new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "10 10, 20 20, 10 40"));
        geometries[2] = GEOMETRY_FACTORY.createPolygon(new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "40 40, 20 45, 45 30, 40 40"));
        Geometry geometry = GEOMETRY_FACTORY.createGeometryCollection(geometries);

        GeometryWrapper expResult = new GeometryWrapper(geometry, SRS_URI.DEFAULT_WKT_CRS84, GeoJsonDatatype.URI, new DimensionInfo(2, 2, 2));
        GeometryWrapper result = GEO_JSON_DATATYPE.read(lexicalForm);

        assertEquals(expResult, result);
    }

    /**
     * Test of empty geometry literal, of class GeoJsonDatatype.<br>
     * Req 16 An empty geo:gmlLiteral shall be interpreted as an empty geometry.
     */
    @Test
    public void testEmpty() {
        GeometryWrapper geo = GEO_JSON_DATATYPE.read("{ \"type\": \"Point\", \"coordinates\": [] }");
        Geometry test = GEOMETRY_FACTORY.createPoint();
        GeometryWrapper expResult = new GeometryWrapper(test, SRS_URI.DEFAULT_WKT_CRS84, GeoJsonDatatype.URI, new DimensionInfo(2, 2, 0));

        assertEquals(geo, expResult);
    }

}

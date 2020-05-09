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

import org.apache.jena.geosparql.implementation.DimensionInfo;
import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.apache.jena.geosparql.implementation.datatype.GeoJsonDatatype;
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
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;

/**
 *
 *
 */
public class GeoJsonWriterTest {

    public GeoJsonWriterTest() {
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

    private static final GeometryFactory GEOMETRY_FACTORY = CustomGeometryFactory.theInstance();

    private String unPrettyPrint(String result) {
        return result.replaceAll("[\\n\\t ]", "");
    }

    /**
     * Test of write method, of class GeoJsonWriter.
     */
    @Test
    public void testWritePoint() {

        Geometry geometry = GEOMETRY_FACTORY.createPoint(new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "11.0 12.0"));

        GeometryWrapper geometryWrapper = new GeometryWrapper(geometry, SRS_URI.DEFAULT_WKT_CRS84, GeoJsonDatatype.URI, new DimensionInfo(2, 2, 0));

        String expResult = "{ \"type\": \"Point\", \"coordinates\": [11.0, 12.0] }";
        String result = GeoJsonWriter.write(geometryWrapper);

        assertEquals(unPrettyPrint(expResult), unPrettyPrint(result));
    }

    /**
     * Test of write method, of class GeoJsonWriter.
     */
    @Test
    public void testWritePoint_SRS_URI() {

        Geometry geometry = GEOMETRY_FACTORY.createPoint(new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "11.0 12.0"));

        GeometryWrapper geometryWrapper = new GeometryWrapper(geometry, SRS_URI.OSGB36_CRS, GeoJsonDatatype.URI, new DimensionInfo(2, 2, 0));

        String expResult = "{ \"type\": \"Point\", \"coordinates\": [11.0, 12.0], \"srsURI\": \"http://www.opengis.net/def/crs/EPSG/0/27700\" }";
        String result = GeoJsonWriter.write(geometryWrapper);

        assertEquals(unPrettyPrint(expResult), unPrettyPrint(result));
    }

    /**
     * Test of write method, of class GeoJsonWriter.
     */
    @Test
    public void testWritePoint2() {

        Geometry geometry = GEOMETRY_FACTORY.createPoint(new CustomCoordinateSequence(CoordinateSequenceDimensions.XYZ, "11.0 12.0 8.0"));

        GeometryWrapper geometryWrapper = new GeometryWrapper(geometry, SRS_URI.DEFAULT_WKT_CRS84, GeoJsonDatatype.URI, new DimensionInfo(3, 3, 0));

        String expResult = "{ \"type\": \"Point\", \"coordinates\": [11.0, 12.0, 8.0] }";
        String result = GeoJsonWriter.write(geometryWrapper);

        assertEquals(unPrettyPrint(expResult), unPrettyPrint(result));
    }

    /**
     * Test of write method, of class GeoJsonWriter.
     */
    @Test
    public void testWriteLineString() {

        Geometry geometry = GEOMETRY_FACTORY.createLineString(new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "30 10, 10 30, 40 40"));

        GeometryWrapper geometryWrapper = new GeometryWrapper(geometry, SRS_URI.DEFAULT_WKT_CRS84, GeoJsonDatatype.URI, new DimensionInfo(2, 2, 1));

        String expResult = "{ \"type\": \"LineString\", \"coordinates\": [[30.0, 10.0], [10.0, 30.0], [40.0, 40.0]] }";
        String result = GeoJsonWriter.write(geometryWrapper);

        assertEquals(unPrettyPrint(expResult), unPrettyPrint(result));
    }

    /**
     * Test of write method, of class GeoJsonWriter.
     */
    @Test
    public void testWriteLineString2() {

        Geometry geometry = GEOMETRY_FACTORY.createLineString(new CustomCoordinateSequence(CoordinateSequenceDimensions.XYZ, "30 10 8, 10 30 7, 40 40 6"));

        GeometryWrapper geometryWrapper = new GeometryWrapper(geometry, SRS_URI.DEFAULT_WKT_CRS84, GeoJsonDatatype.URI, new DimensionInfo(3, 3, 1));

        String expResult = "{ \"type\": \"LineString\", \"coordinates\": [[30.0, 10.0, 8.0], [10.0, 30.0, 7.0], [40.0, 40.0, 6.0]] }";
        String result = GeoJsonWriter.write(geometryWrapper);

        assertEquals(unPrettyPrint(expResult), unPrettyPrint(result));
    }

    /**
     * Test of write method, of class GeoJsonWriter.
     */
    @Test
    public void testWritePolygon() {

        Geometry geometry = GEOMETRY_FACTORY.createPolygon(new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "30 10, 40 40, 20 40, 10 20, 30 10"));

        GeometryWrapper geometryWrapper = new GeometryWrapper(geometry, SRS_URI.DEFAULT_WKT_CRS84, GeoJsonDatatype.URI, new DimensionInfo(2, 2, 2));

        String expResult = "{ \"type\": \"Polygon\", \"coordinates\": [[[30.0, 10.0], [40.0, 40.0], [20.0, 40.0], [10.0, 20.0], [30.0, 10.0]]] }";
        String result = GeoJsonWriter.write(geometryWrapper);

        assertEquals(unPrettyPrint(expResult), unPrettyPrint(result));
    }

    /**
     * Test of write method, of class GeoJsonWriter.
     */
    @Test
    public void testWritePolygon2() {

        Geometry geometry = GEOMETRY_FACTORY.createPolygon(new CustomCoordinateSequence(CoordinateSequenceDimensions.XYZ, "30 10 8, 40 40 7, 20 40 6, 10 20 5, 30 10 4"));

        GeometryWrapper geometryWrapper = new GeometryWrapper(geometry, SRS_URI.DEFAULT_WKT_CRS84, GeoJsonDatatype.URI, new DimensionInfo(3, 3, 2));

        String expResult = "{ \"type\": \"Polygon\", \"coordinates\": [[[30.0, 10.0, 8.0], [40.0, 40.0, 7.0], [20.0, 40.0, 6.0], [10.0, 20.0, 5.0], [30.0, 10.0, 4.0]]] }";
        String result = GeoJsonWriter.write(geometryWrapper);

        assertEquals(unPrettyPrint(expResult), unPrettyPrint(result));
    }

    /**
     * Test of write method, of class GeoJsonWriter.
     */
    @Test
    public void testWritePolygon3() {

        LinearRing shell = GEOMETRY_FACTORY.createLinearRing(new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "35 10, 45 45, 15 40, 10 20, 35 10"));
        LinearRing[] holes = new LinearRing[]{GEOMETRY_FACTORY.createLinearRing(new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "20 30, 35 35, 30 20, 20 30"))};
        Geometry geometry = GEOMETRY_FACTORY.createPolygon(shell, holes);

        GeometryWrapper geometryWrapper = new GeometryWrapper(geometry, SRS_URI.DEFAULT_WKT_CRS84, GeoJsonDatatype.URI, new DimensionInfo(2, 2, 2));

        String expResult = "{ \"type\": \"Polygon\", \"coordinates\": [[[35.0, 10.0], [45.0, 45.0], [15.0, 40.0], [10.0, 20.0], [35.0, 10.0]], [[20.0, 30.0], [35.0, 35.0], [30.0, 20.0], [20.0, 30.0]]]}";
        String result = GeoJsonWriter.write(geometryWrapper);

        assertEquals(unPrettyPrint(expResult), unPrettyPrint(result));
    }

    /**
     * Test of write method, of class GeoJsonWriter.
     */
    @Test
    public void testWriteMultiPoint() {

        Geometry geometry = GEOMETRY_FACTORY.createMultiPoint(new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "30.0 10.0, 10.0 30.0, 40.0 40.0"));

        GeometryWrapper geometryWrapper = new GeometryWrapper(geometry, SRS_URI.DEFAULT_WKT_CRS84, GeoJsonDatatype.URI, new DimensionInfo(2, 2, 0));

        String expResult = "{ \"type\": \"MultiPoint\", \"coordinates\": [[30.0, 10.0], [10.0, 30.0], [40.0, 40.0]] }";
        String result = GeoJsonWriter.write(geometryWrapper);

        assertEquals(unPrettyPrint(expResult), unPrettyPrint(result));
    }

    /**
     * Test of write method, of class GeoJsonWriter.
     */
    @Test
    public void testWriteMultiLineString() {

        LineString[] lineStrings = new LineString[2];
        lineStrings[0] = GEOMETRY_FACTORY.createLineString(new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "10 10, 20 20, 10 40"));
        lineStrings[1] = GEOMETRY_FACTORY.createLineString(new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "40 40, 30 30, 40 20, 30 10"));
        Geometry geometry = GEOMETRY_FACTORY.createMultiLineString(lineStrings);

        GeometryWrapper geometryWrapper = new GeometryWrapper(geometry, SRS_URI.DEFAULT_WKT_CRS84, GeoJsonDatatype.URI, new DimensionInfo(2, 2, 1));

        String expResult = "{ \"type\": \"MultiLineString\", \"coordinates\": [[[10.0, 10.0], [20.0, 20.0], [10.0, 40.0]], [[40.0, 40.0], [30.0, 30.0], [40.0, 20.0], [30.0, 10.0]]] }";
        String result = GeoJsonWriter.write(geometryWrapper);

        assertEquals(unPrettyPrint(expResult), unPrettyPrint(result));
    }

    /**
     * Test of extract method, of class GeoJsonWriter.
     */
    @Test
    public void testWriteMultiPolygon() {

        Polygon[] polygons = new Polygon[2];
        polygons[0] = GEOMETRY_FACTORY.createPolygon(new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "30 20, 45 40, 10 40, 30 20"));
        polygons[1] = GEOMETRY_FACTORY.createPolygon(new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "15 5, 40 10, 10 20, 5 10, 15 5"));
        Geometry geometry = GEOMETRY_FACTORY.createMultiPolygon(polygons);

        GeometryWrapper geometryWrapper = new GeometryWrapper(geometry, SRS_URI.DEFAULT_WKT_CRS84, GeoJsonDatatype.URI, new DimensionInfo(2, 2, 2));

        String expResult = "{ \"type\": \"MultiPolygon\", \"coordinates\": [[[[30.0, 20.0], [45.0, 40.0], [10.0, 40.0], [30.0, 20.0]]], [[[15.0, 5.0], [40.0, 10.0], [10.0, 20.0], [5.0, 10.0], [15.0, 5.0]]]] }";
        String result = GeoJsonWriter.write(geometryWrapper);

        assertEquals(unPrettyPrint(expResult), unPrettyPrint(result));
    }

    /**
     * Test of write method, of class GeoJsonWriter.
     */
    @Test
    public void testWriteMultiPolygon2() {

        Polygon[] polygons = new Polygon[2];
        polygons[0] = GEOMETRY_FACTORY.createPolygon(new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "40 40, 20 45, 45 30, 40 40"));
        LinearRing shell = GEOMETRY_FACTORY.createLinearRing(new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "20 35, 10 30, 10 10, 30 5, 45 20, 20 35"));
        LinearRing[] holes = new LinearRing[]{GEOMETRY_FACTORY.createLinearRing(new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "30 20, 20 15, 20 25, 30 20"))};
        polygons[1] = GEOMETRY_FACTORY.createPolygon(shell, holes);
        Geometry geometry = GEOMETRY_FACTORY.createMultiPolygon(polygons);

        GeometryWrapper geometryWrapper = new GeometryWrapper(geometry, SRS_URI.DEFAULT_WKT_CRS84, GeoJsonDatatype.URI, new DimensionInfo(4, 3, 2));

        String expResult = "{ \"type\": \"MultiPolygon\", \"coordinates\": [[[[40.0, 40.0], [20.0, 45.0], [45.0, 30.0], [40.0, 40.0]]], [[[20.0, 35.0], [10.0, 30.0], [10.0, 10.0], [30.0, 5.0], [45.0, 20.0], [20.0, 35.0]], [[30.0, 20.0], [20.0, 15.0], [20.0, 25.0], [30.0, 20.0]]]] }";
        String result = GeoJsonWriter.write(geometryWrapper);

        assertEquals(unPrettyPrint(expResult), unPrettyPrint(result));
    }

    /**
     * Test of write method, of class GeoJsonWriter.
     */
    @Test
    public void testWriteGeometryCollection() {

        Geometry[] geometries = new Geometry[3];
        geometries[0] = GEOMETRY_FACTORY.createPoint(new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "40 10"));
        geometries[1] = GEOMETRY_FACTORY.createLineString(new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "10 10, 20 20, 10 40"));
        geometries[2] = GEOMETRY_FACTORY.createMultiPoint(new CustomCoordinateSequence(CoordinateSequenceDimensions.XY, "40 40, 20 45, 45 30, 40 40"));
        Geometry geometry = GEOMETRY_FACTORY.createGeometryCollection(geometries);

        GeometryWrapper geometryWrapper = new GeometryWrapper(geometry, SRS_URI.DEFAULT_WKT_CRS84, GeoJsonDatatype.URI, new DimensionInfo(4, 3, 1));

        String expResult = "{ \"type\": \"GeometryCollection\", \"geometries\": [ { \"type\": \"Point\", \"coordinates\": [40.0, 10.0] }, { \"type\": \"LineString\", \"coordinates\": [ [10.0, 10.0], [20.0, 20.0], [10.0, 40.0] ] }, { \"type\": \"MultiPoint\", \"coordinates\": [ [40.0, 40.0], [20.0, 45.0], [45.0, 30.0], [40.0, 40.0] ] } ] }";
        String result = GeoJsonWriter.write(geometryWrapper);

        assertEquals(unPrettyPrint(expResult), unPrettyPrint(result));
    }

    /**
     * Test of writePointEmpty method, of class GeoJsonWriter.
     */
    @Test
    public void testWritePointEmpty() {

        Geometry geometry = GEOMETRY_FACTORY.createPoint();
        GeometryWrapper geometryWrapper = new GeometryWrapper(geometry, SRS_URI.DEFAULT_WKT_CRS84, GeoJsonDatatype.URI, new DimensionInfo(2, 2, 0));
        String result = GeoJsonWriter.write(geometryWrapper);
        String expResult = "{ \"type\": \"Point\", \"coordinates\": [] }";

        assertEquals(unPrettyPrint(expResult), unPrettyPrint(result));
    }

    /**
     * Test of writeLineStringEmpty method, of class GeoJsonWriter.
     */
    @Test
    public void testWriteLineStringEmpty() {

        Geometry geometry = GEOMETRY_FACTORY.createLineString();
        GeometryWrapper geometryWrapper = new GeometryWrapper(geometry, SRS_URI.DEFAULT_WKT_CRS84, GeoJsonDatatype.URI, new DimensionInfo(2, 2, 1));
        String result = GeoJsonWriter.write(geometryWrapper);
        String expResult = "{ \"type\": \"LineString\", \"coordinates\": [] }";

        assertEquals(unPrettyPrint(expResult), unPrettyPrint(result));
    }

    /**
     * Test of writePolygonEmpty method, of class GeoJsonWriter.
     */
    @Test
    public void testWritePolygonEmpty() {

        Geometry geometry = GEOMETRY_FACTORY.createPolygon();
        GeometryWrapper geometryWrapper = new GeometryWrapper(geometry, SRS_URI.DEFAULT_WKT_CRS84, GeoJsonDatatype.URI, new DimensionInfo(2, 2, 2));
        String result = GeoJsonWriter.write(geometryWrapper);
        String expResult = "{ \"type\": \"Polygon\", \"coordinates\": [] }";

        assertEquals(unPrettyPrint(expResult), unPrettyPrint(result));
    }

    /**
     * Test of writeMultiPointEmpty method, of class GeoJsonWriter.
     */
    @Test
    public void testWriteMultiPointEmpty() {

        Geometry geometry = GEOMETRY_FACTORY.createMultiPoint();
        GeometryWrapper geometryWrapper = new GeometryWrapper(geometry, SRS_URI.DEFAULT_WKT_CRS84, GeoJsonDatatype.URI, new DimensionInfo(2, 2, 0));
        String result = GeoJsonWriter.write(geometryWrapper);
        String expResult = "{ \"type\": \"MultiPoint\", \"coordinates\": [] }";

        assertEquals(unPrettyPrint(expResult), unPrettyPrint(result));
    }

    /**
     * Test of writeMultiLineString method, of class GeoJsonWriter.
     */
    @Test
    public void testWriteMultiLineStringEmpty() {

        Geometry geometry = GEOMETRY_FACTORY.createMultiLineString();
        GeometryWrapper geometryWrapper = new GeometryWrapper(geometry, SRS_URI.DEFAULT_WKT_CRS84, GeoJsonDatatype.URI, new DimensionInfo(2, 2, 1));
        String result = GeoJsonWriter.write(geometryWrapper);
        String expResult = "{ \"type\": \"MultiLineString\", \"coordinates\": [] }";

        assertEquals(unPrettyPrint(expResult), unPrettyPrint(result));
    }

    /**
     * Test of writeMultiPolygonEmpty method, of class GeoJsonWriter.
     */
    @Test
    public void testWriteMultiPolygonEmpty() {

        Geometry geometry = GEOMETRY_FACTORY.createMultiPolygon();
        GeometryWrapper geometryWrapper = new GeometryWrapper(geometry, SRS_URI.DEFAULT_WKT_CRS84, GeoJsonDatatype.URI, new DimensionInfo(2, 2, 2));
        String result = GeoJsonWriter.write(geometryWrapper);
        String expResult = "{ \"type\": \"MultiPolygon\", \"coordinates\": [] }";

        assertEquals(unPrettyPrint(expResult), unPrettyPrint(result));
    }

    /**
     * Test of writeGeometryCollectionEmpty method, of class GeoJsonWriter.
     */
    @Test
    public void testWriteGeometryCollectionEmpty() {

        Geometry geometry = GEOMETRY_FACTORY.createGeometryCollection();
        GeometryWrapper geometryWrapper = new GeometryWrapper(geometry, SRS_URI.DEFAULT_WKT_CRS84, GeoJsonDatatype.URI, new DimensionInfo(2, 2, 2));
        String result = GeoJsonWriter.write(geometryWrapper);
        String expResult = "{ \"type\": \"GeometryCollection\", \"geometries\": [] }";

        assertEquals(unPrettyPrint(expResult), unPrettyPrint(result));
    }

}

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;
import org.apache.jena.datatypes.DatatypeFormatException;
import org.apache.jena.geosparql.implementation.DimensionInfo;
import org.apache.jena.geosparql.implementation.jts.CoordinateSequenceDimensions;
import org.apache.jena.geosparql.implementation.jts.CustomCoordinateSequence;
import org.apache.jena.geosparql.implementation.jts.CustomGeometryFactory;
import org.apache.jena.geosparql.implementation.parsers.ParserReader;
import org.apache.jena.geosparql.implementation.vocabulary.SRS_URI;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.CoordinateXY;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

/**
 * Section 4 states that all GeoJSON coordinates are WGS84 coordinate reference
 * systems, unless agreed by all parties involved.
 * <br>"This is equivalent to the coordinate reference system identified by the
 * Open Geospatial Consortium (OGC) URN urn:ogc:def:crs:OGC::CRS84. An OPTIONAL
 * third-position element SHALL be the height in meters above or below the WGS
 * 84 reference ellipsoid."
 * <br>Only this SRS will be supported and only XY and XYZ coordinates now be
 * serialised.
 * <br>Geometry types are case-sensitive, see page 5 section 1.3.
 * <link>https://tools.ietf.org/html/rfc7946#page-17
 */
public class GeoJsonReader implements ParserReader {

    private static final GeometryFactory GEOMETRY_FACTORY = CustomGeometryFactory.theInstance();

    private final CoordinateSequenceDimensions dims;
    private final Geometry geometry;
    private final DimensionInfo dimensionInfo;
    private final String srsURI;

    protected GeoJsonReader(String geometryType, Iterator<JsonValue> coordinates) {
        this.geometry = buildGeometry(geometryType, coordinates);
        this.dims = CoordinateSequenceDimensions.find(geometry.getCoordinate());
        this.dimensionInfo = new DimensionInfo(dims, geometry.getDimension());
        this.srsURI = SRS_URI.DEFAULT_WKT_CRS84;
    }

    @Override
    public Geometry getGeometry() {
        return geometry;
    }

    @Override
    public CoordinateSequenceDimensions getDimensions() {
        return dims;
    }

    @Override
    public DimensionInfo getDimensionInfo() {
        return dimensionInfo;
    }

    @Override
    public String getSrsURI() {
        return srsURI;
    }

    private Geometry buildGeometry(String geometryType, Iterator<JsonValue> coordinates) throws DatatypeFormatException {

        Geometry geo;

        try {
            switch (geometryType) {
                case "Point":
                    geo = buildPoint(coordinates);
                    break;
                case "LineString":
                    geo = buildLineString(coordinates);
                    break;
                case "Polygon":
                    geo = buildPolygon(coordinates);
                    break;
                case "MultiPoint":
                    geo = buildMultiPoint(coordinates);
                    break;
                case "MultiLineString":
                    geo = buildMultiLineString(coordinates);
                    break;
                case "MultiPolygon":
                    geo = buildMultiPolygon(coordinates);
                    break;
                case "GeometryCollection":
                    geo = buildGeometryCollection(coordinates);
                    break;
                default:
                    throw new DatatypeFormatException("Geometry type not supported: " + geometryType);
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new DatatypeFormatException("Build WKT Geometry Exception - Type: " + geometryType + ", Coordinates: " + coordinates + ". " + ex.getMessage());
        }
        return geo;
    }

    private Point buildPoint(Iterator<JsonValue> coordinates) {

        if (!coordinates.hasNext()) {
            return GEOMETRY_FACTORY.createPoint();
        }
        Coordinate coord = getCoordinate(coordinates);
        Coordinate[] coords = new Coordinate[]{coord};
        CustomCoordinateSequence sequence = new CustomCoordinateSequence(coords);
        return GEOMETRY_FACTORY.createPoint(sequence);
    }

    private Coordinate getCoordinate(Iterator<JsonValue> iter) {

        if (!iter.hasNext()) {
            return new Coordinate();
        }

        double coordX = iter.next().getAsNumber().value().doubleValue();
        double coordY = iter.next().getAsNumber().value().doubleValue();

        Coordinate coord;
        if (iter.hasNext()) {
            double coordZ = iter.next().getAsNumber().value().doubleValue();
            coord = new Coordinate(coordX, coordY, coordZ);
        } else {
            coord = new CoordinateXY(coordX, coordY);
        }

        return coord;
    }

    private CoordinateSequence getCoordinateSequence(Iterator<JsonValue> coordinates) {
        List<Coordinate> coords = new ArrayList<>();
        while (coordinates.hasNext()) {
            JsonArray coordinateArray = coordinates.next().getAsArray();
            Coordinate coord = getCoordinate(coordinateArray.iterator());
            coords.add(coord);
        }

        Coordinate[] coordsArray = coords.toArray(new Coordinate[0]);
        return new CustomCoordinateSequence(coordsArray);
    }

    private LineString buildLineString(Iterator<JsonValue> coordinates) {

        if (!coordinates.hasNext()) {
            return GEOMETRY_FACTORY.createLineString();
        }

        CoordinateSequence sequence = getCoordinateSequence(coordinates);
        return GEOMETRY_FACTORY.createLineString(sequence);
    }

    private Polygon buildPolygon(Iterator<JsonValue> coordinates) {

        if (!coordinates.hasNext()) {
            return GEOMETRY_FACTORY.createPolygon();
        }

        JsonValue outer = coordinates.next();

        if (!outer.isArray()) {
            throw new DatatypeFormatException("GeoJSON Polygon does not have a coordinate array.");
        }

        CoordinateSequence outerSequence = getCoordinateSequence(outer.getAsArray().iterator());

        // No holes within the polygon so create the shape.
        if (!coordinates.hasNext()) {
            return GEOMETRY_FACTORY.createPolygon(outerSequence);
        }

        LinearRing outerLinerRing = GEOMETRY_FACTORY.createLinearRing(outerSequence);
        List<LinearRing> innerLinearRings = new ArrayList<>();
        while (coordinates.hasNext()) {
            JsonValue inner = coordinates.next();
            CoordinateSequence innerSequence = getCoordinateSequence(inner.getAsArray().iterator());
            LinearRing innerLinerRing = GEOMETRY_FACTORY.createLinearRing(innerSequence);
            innerLinearRings.add(innerLinerRing);
        }

        return GEOMETRY_FACTORY.createPolygon(outerLinerRing, innerLinearRings.toArray(new LinearRing[0]));
    }

    private MultiPoint buildMultiPoint(Iterator<JsonValue> coordinates) {

        List<Point> points = new ArrayList<>();
        while (coordinates.hasNext()) {
            JsonValue coordValue = coordinates.next();
            Point point = buildPoint(coordValue.getAsArray().iterator());
            points.add(point);
        }

        return GEOMETRY_FACTORY.createMultiPoint(points.toArray(new Point[0]));
    }

    private MultiLineString buildMultiLineString(Iterator<JsonValue> coordinates) {

        List<LineString> lineStrings = new ArrayList<>();
        while (coordinates.hasNext()) {
            JsonValue coordValue = coordinates.next();
            LineString lineString = buildLineString(coordValue.getAsArray().iterator());
            lineStrings.add(lineString);
        }

        return GEOMETRY_FACTORY.createMultiLineString(lineStrings.toArray(new LineString[0]));
    }

    private MultiPolygon buildMultiPolygon(Iterator<JsonValue> coordinates) {

        List<Polygon> polygons = new ArrayList<>();
        while (coordinates.hasNext()) {
            JsonValue coordValue = coordinates.next();
            Polygon polygon = buildPolygon(coordValue.getAsArray().iterator());
            polygons.add(polygon);
        }

        return GEOMETRY_FACTORY.createMultiPolygon(polygons.toArray(new Polygon[0]));
    }

    private GeometryCollection buildGeometryCollection(Iterator<JsonValue> coordinates) throws DatatypeFormatException {

        if (!coordinates.hasNext()) {
            return GEOMETRY_FACTORY.createGeometryCollection();
        }

        List<Geometry> geometries = new ArrayList<>();
        while (coordinates.hasNext()) {

            JsonObject jsonObject = coordinates.next().getAsObject();
            if (!jsonObject.hasKey("type")) {
                throw new DatatypeFormatException("GeoJSON object does not have 'type' property.");
            }

            String type = jsonObject.getString("type");

            if (!jsonObject.hasKey("coordinates")) {
                throw new DatatypeFormatException("GeoJSON object does not have 'coordinates' or 'geometries' property.");
            }

            Iterator<JsonValue> subCoordinates = jsonObject.getIterator("coordinates");

            Geometry subGeometry = buildGeometry(type, subCoordinates);
            geometries.add(subGeometry);
        }

        return GEOMETRY_FACTORY.createGeometryCollection(geometries.toArray(new Geometry[0]));
    }

    public static GeoJsonReader extract(String geometryLiteral) throws DatatypeFormatException {

        JsonObject jsonObject = JSON.parse(geometryLiteral);

        if (!jsonObject.hasKey("type")) {
            throw new DatatypeFormatException("GeoJSON object does not have 'type' property.");
        }
        String type = jsonObject.getString("type");
        Iterator<JsonValue> coordinates;
        if (jsonObject.hasKey("coordinates")) {
            coordinates = jsonObject.getIterator("coordinates");
        } else if (jsonObject.hasKey("geometries")) {
            coordinates = jsonObject.getIterator("geometries");
        } else {
            throw new DatatypeFormatException("GeoJSON object does not have 'coordinates' or 'geometries' property.");
        }

        return new GeoJsonReader(type, coordinates);
    }

    @Override
    public String toString() {
        return "GeoJSONReader{" + "dims=" + dims + ", geometry=" + geometry + ", dimensionInfo=" + dimensionInfo + ", srsURI=" + srsURI + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.dims);
        hash = 23 * hash + Objects.hashCode(this.geometry);
        hash = 23 * hash + Objects.hashCode(this.dimensionInfo);
        hash = 23 * hash + Objects.hashCode(this.srsURI);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GeoJsonReader other = (GeoJsonReader) obj;
        if (!Objects.equals(this.srsURI, other.srsURI)) {
            return false;
        }
        if (this.dims != other.dims) {
            return false;
        }
        if (!Objects.equals(this.geometry, other.geometry)) {
            return false;
        }
        return Objects.equals(this.dimensionInfo, other.dimensionInfo);
    }

}

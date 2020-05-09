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

import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonNumber;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.apache.jena.geosparql.implementation.parsers.ParserWriter;
import org.apache.jena.geosparql.implementation.vocabulary.SRS_URI;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

/**
 *
 *
 */
public class GeoJsonWriter implements ParserWriter {

    private static final String DEFAULT_SRS_URI = SRS_URI.DEFAULT_WKT_CRS84;
    private static final String COORDINATES_PROP = "coordinates";
    private static final String GEOMETRIES_PROP = "geometries";
    private static final String TYPE_PROP = "type";
    private static final String SRS_URI_PROP = "srsURI";

    public static final String write(GeometryWrapper geometryWrapper) {

        Geometry geometry = geometryWrapper.getParsingGeometry();
        JsonObject jsonObject = extract(geometry);

        String srsURI = geometryWrapper.getSrsURI();
        if (!srsURI.equals(DEFAULT_SRS_URI)) {
            jsonObject.put(SRS_URI_PROP, srsURI);
        }

        return jsonObject.toString();
    }

    private static JsonObject extract(final Geometry geometry) {

        JsonObject jsonObject = new JsonObject();
        String type = geometry.getGeometryType();
        jsonObject.put(TYPE_PROP, type);
        switch (type) {
            case "Point":
                jsonObject.put(COORDINATES_PROP, extractPoint((Point) geometry));
                break;
            case "LineString":
                jsonObject.put(COORDINATES_PROP, extractLineString((LineString) geometry));
                break;
            case "Polygon":
                jsonObject.put(COORDINATES_PROP, extractPolygon((Polygon) geometry));
                break;
            case "MultiPoint":
                jsonObject.put(COORDINATES_PROP, extractMultiPoint((MultiPoint) geometry));
                break;
            case "MultiLineString":
                jsonObject.put(COORDINATES_PROP, extractMultiLineString((MultiLineString) geometry));
                break;
            case "MultiPolygon":
                jsonObject.put(COORDINATES_PROP, extractMultiPolygon((MultiPolygon) geometry));
                break;
            case "GeometryCollection":
                jsonObject.put(GEOMETRIES_PROP, extractGeometryCollection((GeometryCollection) geometry));
                break;
        }

        return jsonObject;
    }

    private static JsonArray extractCoordinate(Coordinate coordinate) {

        JsonArray jsonArray = new JsonArray();

        //Empty geometries can have a null coordinate.
        if (coordinate == null) {
            return jsonArray;
        }
        jsonArray.add(JsonNumber.value(coordinate.x));
        jsonArray.add(JsonNumber.value(coordinate.y));
        double zCoord = coordinate.getZ();

        if (!Double.isNaN(zCoord)) {
            jsonArray.add(JsonNumber.value(zCoord));
        }

        return jsonArray;
    }

    private static JsonArray extractPoint(Point point) {
        return extractCoordinate(point.getCoordinate());
    }

    private static JsonArray extractLineString(LineString lineString) {

        JsonArray line = new JsonArray();
        Coordinate[] coords = lineString.getCoordinates();
        for (Coordinate coord : coords) {
            JsonArray extractCoord = extractCoordinate(coord);
            line.add(extractCoord);
        }

        return line;
    }

    private static JsonArray extractPolygon(Polygon polygon) {

        JsonArray poly = new JsonArray();
        JsonArray outer = extractLineString(polygon.getExteriorRing());
        if (!outer.isEmpty()) {
            poly.add(outer);

            for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
                JsonArray inner = extractLineString(polygon.getInteriorRingN(i));
                poly.add(inner);
            }
        }
        return poly;
    }

    private static JsonArray extractMultiPoint(MultiPoint multiPoint) {

        JsonArray points = new JsonArray();
        Coordinate[] coords = multiPoint.getCoordinates();
        for (Coordinate coord : coords) {
            JsonArray extractCoord = extractCoordinate(coord);
            points.add(extractCoord);
        }

        return points;
    }

    private static JsonArray extractMultiLineString(MultiLineString multiLineString) {
        JsonArray lines = new JsonArray();

        for (int i = 0; i < multiLineString.getNumGeometries(); i++) {
            LineString lineString = (LineString) multiLineString.getGeometryN(i);
            JsonArray line = extractLineString(lineString);
            lines.add(line);
        }

        return lines;
    }

    private static JsonArray extractMultiPolygon(MultiPolygon multiPolygon) {
        JsonArray polygons = new JsonArray();

        for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
            Polygon polygon = (Polygon) multiPolygon.getGeometryN(i);
            JsonArray poly = extractPolygon(polygon);
            polygons.add(poly);
        }

        return polygons;
    }

    private static JsonArray extractGeometryCollection(GeometryCollection geometryCollection) {

        JsonArray geoms = new JsonArray();

        for (int i = 0; i < geometryCollection.getNumGeometries(); i++) {
            Geometry geometry = geometryCollection.getGeometryN(i);
            JsonObject geom = extract(geometry);
            geoms.add(geom);
        }

        return geoms;

    }

}

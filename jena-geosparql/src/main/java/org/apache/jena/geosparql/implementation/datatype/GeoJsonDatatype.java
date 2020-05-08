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

import org.apache.jena.datatypes.DatatypeFormatException;
import org.apache.jena.geosparql.implementation.DimensionInfo;
import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.apache.jena.geosparql.implementation.parsers.geojson.GeoJsonReader;
import org.apache.jena.geosparql.implementation.parsers.geojson.GeoJsonWriter;
import org.apache.jena.geosparql.implementation.vocabulary.Geo;
import org.locationtech.jts.geom.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GeoJSONDatatype class allows the URI "geo:geoJsonLiteral" to be used as a
 * datatype and it will parse that datatype to a JTS Geometry.
 *
 * The GeoJSONDatatype is used to convert the RDF Geometry literal containing
 * the GeoJSON serialisation of the geometry. Conversion of GeoJSON into RDF
 * will need to be undertaken prior to this stage.
 *
 */
public class GeoJsonDatatype extends GeometryDatatype {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeoJsonDatatype.class);

    /**
     * The default GeoJSON type URI.
     */
    public static final String URI = Geo.GEO_JSON;

    /**
     * A static instance of GeoJsonDatatype.
     */
    public static final GeoJsonDatatype INSTANCE = new GeoJsonDatatype();

    /**
     * private constructor - single global instance.
     */
    private GeoJsonDatatype() {
        super(URI);
    }

    /**
     * This method Un-parses the JTS Geometry to the GeoJSON literal
     *
     * @param geometry - the JTS Geometry to be un-parsed
     * @return GeoJSON - the returned GeoJSON Literal.
     * <br> Notice that the Spatial Reference System is not specified in
     * returned GeoJSON literal.
     *
     */
    @Override
    public String unparse(Object geometry) {

        if (geometry instanceof GeometryWrapper) {
            GeometryWrapper geometryWrapper = (GeometryWrapper) geometry;
            return GeoJsonWriter.write(geometryWrapper);
        } else {
            throw new DatatypeFormatException("Object to unparse GeoJSONDatatype is not a GeometryWrapper: " + geometry);
        }
    }

    @Override
    public GeometryWrapper read(String geometryLiteral) {

        GeoJsonReader geoJSONReader = GeoJsonReader.extract(geometryLiteral);

        Geometry geometry = geoJSONReader.getGeometry();
        String srsURI = geoJSONReader.getSrsURI();
        DimensionInfo dimensionInfo = geoJSONReader.getDimensionInfo();

        return new GeometryWrapper(geometry, srsURI, URI, dimensionInfo, geometryLiteral);
    }

    @Override
    public String toString() {
        return "GeoJSONDatatype{" + URI + '}';
    }

}

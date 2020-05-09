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

import java.lang.invoke.MethodHandles;
import java.util.Map;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;
import org.apache.jena.geosparql.implementation.datatype.GeoJsonDatatype;
import org.apache.jena.geosparql.implementation.vocabulary.Geo;
import org.apache.jena.geosparql.implementation.vocabulary.GeoJson;
import org.apache.jena.geosparql.implementation.vocabulary.GeoSPARQL_URI;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 */
public class GeoJsonSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String ID_KEY = GeoJson.ID_KEY;
    private static final String TYPE_KEY = GeoJson.TYPE_KEY;
    private static final String URI_KEY = GeoJson.URI_KEY;
    private static final String GEOMETRY_KEY = GeoJson.GEOMETRY_KEY;
    private static final String PROPERTIES_KEY = GeoJson.PROPERTIES_KEY;
    private static final String FEATURES_KEY = GeoJson.FEATURES_KEY;
    private static final String FEATURE_KEY = GeoJson.FEATURE_KEY;

    public static final Model convert(JsonObject rootObject, String baseURI) throws GeoJsonException {

        Model model = ModelFactory.createDefaultModel();
        model.setNsPrefixes(GeoSPARQL_URI.getPrefixes());

        if (rootObject.hasKey(TYPE_KEY) && rootObject.hasKey(FEATURES_KEY)) {

            JsonArray features = rootObject.get(FEATURES_KEY).getAsArray();
            int featureCounter = 0;
            for (JsonValue featureVal : features) {

                JsonObject featureObj = featureVal.getAsObject();

                if (featureObj.hasKey(TYPE_KEY) && featureObj.getString(TYPE_KEY).equals(FEATURE_KEY)) {

                    Info featureInfo = extractInfo(featureObj, baseURI, featureCounter);
                    String featureID = featureInfo.id;
                    String featureURI = featureInfo.uri;
                    featureCounter = featureInfo.counter;

                    Resource feature = model.createResource(featureURI);
                    feature.addProperty(RDF.type, Geo.FEATURE_RES);
                    feature.addProperty(model.createProperty(baseURI, ID_KEY), featureID);

                    // Convert the geometry
                    if (featureObj.hasKey(GEOMETRY_KEY)) {
                        JsonObject geometryObj = featureObj.getObj(GEOMETRY_KEY);

                        String geomURI;
                        if (geometryObj.hasKey(URI_KEY)) {
                            geomURI = geometryObj.getString(URI_KEY);
                        } else {
                            geomURI = featureURI + "-Geometry";
                        }

                        Resource geometry = model.createResource(geomURI);
                        geometry.addProperty(RDF.type, Geo.GEOMETRY_RES);

                        // Add Feature-Geometry properties.
                        feature.addProperty(Geo.HAS_GEOMETRY_PROP, geometry);
                        feature.addProperty(Geo.HAS_DEFAULT_GEOMETRY_PROP, geometry);   // GeoJSON only allows a single Geometry per Feature.

                        // Add GeometryLiteral to the Geometry.
                        Literal geoLiteral = model.createTypedLiteral(geometryObj.toString(), GeoJsonDatatype.INSTANCE);
                        geometry.addLiteral(Geo.AS_GEO_JSON_PROP, geoLiteral);
                        geometry.addLiteral(Geo.HAS_SERIALIZATION_PROP, geoLiteral);
                    } else {
                        throw new GeoJsonException("GeoJSON feature does not have 'geometry' property: - " + featureID);
                    }

                    // Convert any Properties.
                    if (featureObj.hasKey(PROPERTIES_KEY)) {
                        JsonObject propsObj = featureObj.getObj(PROPERTIES_KEY);
                        for (Map.Entry<String, JsonValue> propObj : propsObj.entrySet()) {
                            Property prop = model.createProperty(baseURI, propObj.getKey());
                            JsonValue value = propObj.getValue();
                            try {
                                Literal literal = createJsonLiteral(value);
                                feature.addLiteral(prop, literal);
                            } catch (GeoJsonException ex) {
                                throw new GeoJsonException(ex.getLocalizedMessage() + " - " + featureID);
                            }
                        }
                    }

                    //Convert any other foreign members on the Feature.
                    for (Map.Entry<String, JsonValue> foreignObj : featureObj.entrySet()) {
                        String foreignName = foreignObj.getKey();
                        if (!foreignName.equals(ID_KEY) && !foreignName.equals(TYPE_KEY) && !foreignName.equals(PROPERTIES_KEY) && !foreignName.equals(GEOMETRY_KEY)) {
                            try {
                                Literal literal = createJsonLiteral(foreignObj.getValue());
                                Property prop = model.createProperty(baseURI, foreignName);
                                feature.addLiteral(prop, literal);
                            } catch (GeoJsonException ex) {
                                throw new GeoJsonException(ex.getLocalizedMessage() + " - " + featureID + " - " + foreignName);
                            }
                        }
                    }

                } else {
                    throw new GeoJsonException("GeoJSON FeatureCollection ignored object without 'type: \"Feature\"'");
                }
            }

        } else {
            throw new GeoJsonException("GeoJson 'type' and 'features' not found in root");
        }

        return model;
    }

    private static Info extractInfo(JsonObject object, String baseURI, int counter) {
        // Construct Feature Resource.
        String uri;
        String id;
        if (object.hasKey(ID_KEY)) {

            /**
             * GeoJSON 2016, Section 3.2: "If a Feature has a commonly used
             * identifier, that identifier SHOULD be included as a member of the
             * Feature object with the name "id", and the value of this member
             * is either a JSON string or number."
             */
            JsonValue key = object.get(ID_KEY);
            if (key.isString()) {
                id = key.getAsString().value();
            } else {
                id = key.getAsNumber().toString();
            }

        } else {
            id = FEATURE_KEY + counter;
            counter++;
        }
        if (object.hasKey(URI_KEY)) {
            uri = object.getString(URI_KEY);
        } else {
            uri = baseURI + id;
        }

        return new Info(id, uri, counter);
    }

    private static Literal createJsonLiteral(JsonValue value) throws GeoJsonException {
        if (value.isBoolean()) {
            return ResourceFactory.createTypedLiteral(value.getAsBoolean().value());
        } else if (value.isNumber()) {
            return ResourceFactory.createTypedLiteral(value.getAsNumber().value());
        } else if (value.isString()) {
            return ResourceFactory.createTypedLiteral(value.getAsString().value());
        } else {
            throw new GeoJsonException("GeoJSON property type not supported: " + value.toString());
        }
    }

    private static class Info {

        public final String id;
        public final String uri;
        public final int counter;

        public Info(String id, String uri, int counter) {
            this.id = id;
            this.uri = uri;
            this.counter = counter;
        }
    }

}

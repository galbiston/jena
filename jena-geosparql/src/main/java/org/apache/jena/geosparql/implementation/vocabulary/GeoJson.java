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
package org.apache.jena.geosparql.implementation.vocabulary;

/**
 *
 *
 */
public interface GeoJson {

    public static final String TYPE_KEY = "type";

    public static final String GEOMETRY_KEY = "geometry";
    public static final String PROPERTIES_KEY = "properties";
    public static final String FEATURES_KEY = "features";
    public static final String FEATURE_KEY = "Feature";
    public static final String GEOMETRIES_KEY = "geometries";
    public static final String COORDINATES_KEY = "coordinates";

    /**
     * GeoJSON 2016, Section 3.2: "If a Feature has a commonly used identifier,
     * that identifier SHOULD be included as a member of the Feature object with
     * the name "id", and the value of this member is either a JSON string or
     * number."
     * <br>"id" is used either as a URI or to construct a URI.
     */
    public static final String ID_KEY = "id";

    /**
     * GeoJSON 2016, Section 6.1: "Members not described in this specification
     * ("foreign members") MAY be used in a GeoJSON document."
     * <br>Extension of URI to allow a Feature or Geometry to explicitly define
     * the resource URI.
     */
    public static final String URI_KEY = "uri";

    /**
     * GeoJSON 2016, Section 6.1: "Members not described in this specification
     * ("foreign members") MAY be used in a GeoJSON document."
     * <br>Extension of URI to allow a Geometry to state a non-default SRS.
     */
    public static final String SRS_URI_KEY = "srsURI";

}

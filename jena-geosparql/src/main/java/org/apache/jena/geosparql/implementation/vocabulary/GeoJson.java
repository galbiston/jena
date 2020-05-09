/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

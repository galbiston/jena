/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.jena.geosparql.implementation.parsers.geojson;

/**
 *
 *
 */
public class GeoJsonException extends RuntimeException {

    public GeoJsonException(String msg) {
        super(msg);
    }

    public GeoJsonException(String msg, Throwable cause) {
        super(msg, cause);
    }

}

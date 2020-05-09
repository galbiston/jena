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
package org.apache.jena.fuseki.geosparql.cli;

import java.io.File;
import java.util.Objects;

/**
 *
 *
 */
public class GeoJsonFileBaseGraph {

    private final File geoJsonFile;
    private final String graphName;
    private final String baseURI;

    public GeoJsonFileBaseGraph(File geoJsonFile, String baseURI, String graphName) {
        this.geoJsonFile = geoJsonFile;
        this.baseURI = baseURI;
        this.graphName = graphName;
    }

    public File getGeoJsonFile() {
        return geoJsonFile;
    }

    public String getGraphName() {
        return graphName;
    }

    public String getBaseURI() {
        return baseURI;
    }

    @Override
    public String toString() {
        return "FileGraphFormat{" + "geoJsonFile=" + geoJsonFile + ", rdfFormat=" + baseURI + ", graphName=" + graphName + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.geoJsonFile);
        hash = 67 * hash + Objects.hashCode(this.graphName);
        hash = 67 * hash + Objects.hashCode(this.baseURI);
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
        final GeoJsonFileBaseGraph other = (GeoJsonFileBaseGraph) obj;
        if (!Objects.equals(this.graphName, other.graphName)) {
            return false;
        }
        if (!Objects.equals(this.baseURI, other.baseURI)) {
            return false;
        }
        return Objects.equals(this.geoJsonFile, other.geoJsonFile);
    }

}

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

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 */
public class GeoJsonFileParameter implements IStringConverter<List<GeoJsonFileBaseGraph>>, IParameterValidator {

    private static final String NAME_SEP = ">";
    private static final String BASE_URI_SEP = "|";

    @Override
    public List<GeoJsonFileBaseGraph> convert(String value) {
        String[] values = value.split(",");
        List<GeoJsonFileBaseGraph> fileList = new ArrayList<>();
        for (String val : values) {
            GeoJsonFileBaseGraph file = build(val);
            fileList.add(file);
        }
        return fileList;
    }

    public GeoJsonFileBaseGraph build(String value) {
        File file;
        String name = "";
        String baseURI = "http://example.org/undefined_base_geoJson#";

        String target = value;
        if (target.contains(NAME_SEP)) {
            String[] parts = target.split(NAME_SEP);
            name = parts[1];
            target = parts[0];
        }

        if (target.contains(BASE_URI_SEP)) {
            String[] parts = target.split(BASE_URI_SEP);
            baseURI = parts[1];
            target = parts[0];
        }

        file = new File(target);

        return new GeoJsonFileBaseGraph(file, baseURI, name);

    }

    @Override
    public void validate(String name, String value) throws ParameterException {

        int nameIndex;
        int baseURIIndex;
        String[] values = value.split(",");
        for (String val : values) {
            nameIndex = val.indexOf(NAME_SEP);
            baseURIIndex = val.indexOf(BASE_URI_SEP);
            if (nameIndex > -1 && baseURIIndex > -1) {
                if (nameIndex < baseURIIndex) {
                    throw new ParameterException("Parameter " + name + " and value " + val + " must have graph name (" + nameIndex + ") after base URI (" + baseURIIndex + ").");
                }
            }
        }
    }

}

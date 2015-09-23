/**
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

package org.apache.jena.system;

import org.apache.jena.assembler.Assembler ;
import org.apache.jena.vocabulary.OWL ;
import org.apache.jena.vocabulary.RDF ;
import org.apache.jena.vocabulary.RDFS ;

public class InitJenaCore  implements JenaSubsystemLifecycle {

    @Override
    public void start() {
        init() ;
    }

    @Override
    public void stop() {}
    
    @Override
    public int level() {
        return 10 ;
    }
    
    private static volatile boolean initialized = false ;
    private static Object           initLock    = new Object() ;

    public static void init() {
        if ( initialized )
            return ;
        synchronized (initLock) {
            if ( initialized ) {
                if ( JenaSystem.DEBUG_INIT )
                    System.err.println("JenaCore.init - skip") ;
                return ;
            }
            initialized = true ;
            if ( JenaSystem.DEBUG_INIT )
                System.err.println("JenaCore.init - start") ;

            // Initialization
            // Touch classes with constants.  
            // This isn't necessary but it makes it more deterministic.
            // These constants are resused in various places.  
            RDF.getURI() ;
            RDFS.getURI() ;
            OWL.getURI() ;
            // Necessary. for the case of Jena first used via Assemblers
            // Can't delay because ARQ, TDB touch Assembler for initialization.
            // Assembler is an interface with statics final constants.
            Assembler.general.hashCode() ;
            if ( JenaSystem.DEBUG_INIT )
                System.err.println("JenaCore.init - finish") ;
        }
    }
}
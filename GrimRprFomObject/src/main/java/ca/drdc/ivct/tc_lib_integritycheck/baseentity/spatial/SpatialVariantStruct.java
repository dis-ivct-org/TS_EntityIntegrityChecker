/*******************************************************************************
 * Copyright (C) Her Majesty the Queen in Right of Canada, 
 * as represented by the Minister of National Defence, 2018
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package ca.drdc.ivct.tc_lib_integritycheck.baseentity.spatial;

import ca.drdc.ivct.tc_lib_integritycheck.baseentity.spatial.element.DeadReckoningAlgorithm;

public class SpatialVariantStruct {

    /**
     * Dead reckoning algorithm
     */
    private DeadReckoningAlgorithm deadReckoningAlgorithm;
    
    /**
     * Spatial structure for Dead Reckoning Algorithm RVW (4) and RVB (8). 
     * Variant for representing an object with high speed or maneuvering at 
     * any speed, including rotation information, in body axis coordinates. 
     */
    private SpatialRepresentation spatialRepresentation;

    
    
    public SpatialVariantStruct(DeadReckoningAlgorithm deadReckoningAlgorithm,
            SpatialRepresentation spatialRepresentation) {
        super();
        this.deadReckoningAlgorithm = deadReckoningAlgorithm;
        this.spatialRepresentation = spatialRepresentation;
    }

    public DeadReckoningAlgorithm getDeadReckoningAlgorithm() {
        return deadReckoningAlgorithm;
    }

    public void setDeadReckoningAlgorithm(DeadReckoningAlgorithm deadReckoningAlgorithm) {
        this.deadReckoningAlgorithm = deadReckoningAlgorithm;
    }

    public SpatialRepresentation getSpatialRepresentation() {
        return spatialRepresentation;
    }

    public void setSpatialRepresentation(SpatialRepresentation spatialRepresentation) {
        this.spatialRepresentation = spatialRepresentation;
    }
}

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
package ca.drdc.ivct.tc_lib_integritycheck.baseentity;

import ca.drdc.ivct.tc_lib_integritycheck.baseentity.entitytype.EntityType;
import ca.drdc.ivct.tc_lib_integritycheck.baseentity.spatial.SpatialRepresentation;

public class BaseEntity {
    private String entityIdentifier;
    private EntityType entityType;
    private SpatialRepresentation spatialRepresentation;

    public void setEntityType(final String entityType) {
        this.entityType = new EntityType(entityType);
    }

    public void setEntityIdentifier(final String entityIdentifier) {
        this.entityIdentifier = entityIdentifier;
    }
    
    /**
     * @return the type value
     */
    public String getEntityType() {
        return this.entityType.toString();
    }

    /**
     * @return the identifier value
     */
    public String getEntityIdentifier() {
        return this.entityIdentifier;
    }
    
    public void setSpatialRepresentation(SpatialRepresentation spatialRepresentation) {
        this.spatialRepresentation = spatialRepresentation;
    }
    
    public SpatialRepresentation getSpatialRepresentation() {
        return spatialRepresentation;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;    
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((entityIdentifier == null) ? 0 : entityIdentifier.hashCode());
        result = prime * result + ((entityType == null) ? 0 : entityType.hashCode());
        result = prime * result + ((spatialRepresentation == null) ? 0 : spatialRepresentation.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BaseEntity other = (BaseEntity) obj;
        if (entityIdentifier == null) {
            if (other.entityIdentifier != null)
                return false;
        } else if (!entityIdentifier.equals(other.entityIdentifier))
            return false;
        if (entityType == null) {
            if (other.entityType != null)
                return false;
        } else if (!entityType.equals(other.entityType))
            return false;
        if (spatialRepresentation == null) {
            if (other.spatialRepresentation != null)
                return false;
        } else if (!spatialRepresentation.equals(other.spatialRepresentation))
            return false;
        return true;
    }
    

    @Override
    public String toString() {
        return "Entity [" + this.entityIdentifier + " " + this.getEntityType() + " " + this.getSpatialRepresentation() +"]";
    }


}

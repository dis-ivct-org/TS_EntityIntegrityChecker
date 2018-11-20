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
package ca.drdc.ivct.tc_lib_integritycheck.baseentity.entitytype;

import java.util.StringTokenizer;

/**
 *  Entity type of a base entity.
 *  
 * @author mlavallee
 */
public class EntityType {

    private byte entityKind;
    private byte domain;
    private short countryCode;
    private byte category;
    private byte subcategory;
    private byte specific;
    private byte extra;

    public EntityType(String entityType) {
        this.entityKind = 0;
        this.domain = 0;
        this.countryCode = 0;
        this.category = 0;
        this.subcategory = 0;
        this.specific = 0;
        this.extra = 0;

        if (entityType != null) {
            StringTokenizer stk = new StringTokenizer(entityType, ".");
            if (stk.hasMoreTokens()) {
                this.entityKind = Byte.parseByte(stk.nextToken());
            }
            if (stk.hasMoreTokens()) {
                this.domain = Byte.parseByte(stk.nextToken());
            }
            if (stk.hasMoreTokens()) {
                this.countryCode = Short.parseShort(stk.nextToken());
            }
            if (stk.hasMoreTokens()) {
                this.category = Byte.parseByte(stk.nextToken());
            }
            if (stk.hasMoreTokens()) {
                this.subcategory = Byte.parseByte(stk.nextToken());
            }
            if (stk.hasMoreTokens()) {
                this.specific = Byte.parseByte(stk.nextToken());
            }
            if (stk.hasMoreTokens()) {
                this.extra = Byte.parseByte(stk.nextToken());
            }
        }
    }

    public EntityType(byte entityKind, byte domain, short countryCode, byte category, byte subcategory, byte specific,
            byte extra) {

        super();
        this.entityKind = entityKind;
        this.domain = domain;
        this.countryCode = countryCode;
        this.category = category;
        this.subcategory = subcategory;
        this.specific = specific;
        this.extra = extra;
    }

    @Override
    public String toString() {
        return entityKind + "." + domain + "." + countryCode + "." + category + "." + subcategory + "." + specific + "."
                + extra;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + category;
        result = prime * result + countryCode;
        result = prime * result + domain;
        result = prime * result + entityKind;
        result = prime * result + extra;
        result = prime * result + specific;
        result = prime * result + subcategory;
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
        EntityType other = (EntityType) obj;
        if (category != other.category)
            return false;
        if (countryCode != other.countryCode)
            return false;
        if (domain != other.domain)
            return false;
        if (entityKind != other.entityKind)
            return false;
        if (extra != other.extra)
            return false;
        if (specific != other.specific)
            return false;
        if (subcategory != other.subcategory)
            return false;
        return true;
    }

}

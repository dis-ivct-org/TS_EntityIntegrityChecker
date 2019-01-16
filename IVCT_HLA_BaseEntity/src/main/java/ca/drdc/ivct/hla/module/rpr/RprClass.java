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
package ca.drdc.ivct.hla.module.rpr;

public enum RprClass {
    BASE_ENTITY ("BaseEntity"),
    AIRCRAFT ("BaseEntity.PhysicalEntity.Platform.Aircraft"),
    AMPHIBIOUS_VEHICLE ("BaseEntity.PhysicalEntity.Platform.AmphibiousVehicle"),
    GROUND_VEHICLE ("BaseEntity.PhysicalEntity.Platform.GroundVehicle"),
    MULTI_DOMAIN_PLATFORM ("BaseEntity.PhysicalEntity.Platform.MultiDomainPlatform"),
    SPACECRAFT ("BaseEntity.PhysicalEntity.Platform.Spacecraft"),
    SUBMERSIBLE ("BaseEntity.PhysicalEntity.Platform.SubmersibleVessel"),
    SURFACE_VESSEL ("BaseEntity.PhysicalEntity.Platform.SurfaceVessel");
    
    private String name;
    
    private RprClass(String name){
        this.name = name;
    }
    
    public String getName(){
        return this.name;
    }
}
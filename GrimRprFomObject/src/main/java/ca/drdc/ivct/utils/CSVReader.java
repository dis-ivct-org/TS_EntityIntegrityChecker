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
package ca.drdc.ivct.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.drdc.ivct.baseentity.BaseEntity;
import ca.drdc.ivct.baseentity.EntityIdentifier;
import ca.drdc.ivct.baseentity.EntityType;
import ca.drdc.ivct.baseentity.spatial.SpatialFPStruct;
import ca.drdc.ivct.baseentity.spatial.SpatialRVStruct;
import ca.drdc.ivct.baseentity.spatial.element.AccelerationVectorStruct;
import ca.drdc.ivct.baseentity.spatial.element.AngularVelocityVectorStruct;
import ca.drdc.ivct.baseentity.spatial.element.DeadReckoningAlgorithm;
import ca.drdc.ivct.baseentity.spatial.element.OrientationStruct;
import ca.drdc.ivct.baseentity.spatial.element.VelocityVectorStruct;
import ca.drdc.ivct.baseentity.spatial.element.WorldLocationStruct;

/**
 * CSV reader for base Entity. The CSV must contain the following item in this
 * order: entityId,entityType,description
 * 
 * The CSV must contain a header.
 * 
 * @author laurenceo, mlavallee
 */
public class CSVReader {

    private static Logger logger = LoggerFactory.getLogger(CSVReader.class);

    /**
     * Order of field in the CSV
     */
    private enum EntityHeader {
        ENTITY_ID("entityId"),
        ENTITY_TYPE("entityType"),
        DESCRIPTION("description"),
        DEAD_RECKONING_ALGORITHM( "deadReckoningAlgorithm"),
        WORLD_LOCATION("worldLocation"),
        IS_FROZEN("isFrozen"),
        ORIENTATION("orientation"),
        VELOCITY_VECTOR("velocityVector"),
        ACCELERATION_VECTOR("accelerationVector"),
        ANGULAR_VELOCITY("angularVelocity");
        
        private final String value;

        private EntityHeader(String value) {
            this.value = value;
        }
    }

    public static List<BaseEntity> loadCSVFileToBaseEntityList(List<URL> csvFiles, Logger logger) throws IOException, ParseException {

        List<BaseEntity> fadEntities = new ArrayList<>();
        String line = "";
        String csvSplitBy = ",";

        for (int i = 0; i < csvFiles.size(); i++) {
            logger.info("Reading {}", csvFiles.get(i).getPath());
            try (BufferedReader br = new BufferedReader(new InputStreamReader(csvFiles.get(i).openStream()))) {
                // skip header
                line = br.readLine();

                // process each entity
                while ((line = br.readLine()) != null && !line.isEmpty()) {
                    List<String> csvItems = Arrays.asList(line.split(csvSplitBy));

                    BaseEntity newEntity = new BaseEntity();
                    newEntity.setEntityIdentifier(new EntityIdentifier(csvItems.get(EntityHeader.ENTITY_ID.ordinal())));
                    newEntity.setEntityType(new EntityType(csvItems.get(EntityHeader.ENTITY_TYPE.ordinal())));
                    
                    WorldLocationStruct worldLocation = new WorldLocationStruct(csvItems.get(EntityHeader.WORLD_LOCATION.ordinal()));
                    boolean isFrozen = Boolean.parseBoolean(csvItems.get(EntityHeader.IS_FROZEN.ordinal()));
                    OrientationStruct orientation = new OrientationStruct(csvItems.get(EntityHeader.ORIENTATION.ordinal()));
                    VelocityVectorStruct velocityVector = new VelocityVectorStruct(csvItems.get(EntityHeader.VELOCITY_VECTOR.ordinal()));
                    
                    DeadReckoningAlgorithm itemDeadReckoningAlgorithm = DeadReckoningAlgorithm.valueOf(csvItems.get(EntityHeader.DEAD_RECKONING_ALGORITHM.ordinal()));
                    
                    switch (itemDeadReckoningAlgorithm) {
                        case DRM_FPW:
                        case DRM_FPB:
                            newEntity.setSpatialRepresentation(
                                    new SpatialFPStruct(itemDeadReckoningAlgorithm, worldLocation, isFrozen, orientation, velocityVector));
                            break;
                            
                        case DRM_RVW:
                        case DRM_RVB:
                            AngularVelocityVectorStruct angularVelocityVector = 
                                    new AngularVelocityVectorStruct(csvItems.get(EntityHeader.ANGULAR_VELOCITY.ordinal()));
                            AccelerationVectorStruct accelerationVector = 
                                    new AccelerationVectorStruct(csvItems.get(EntityHeader.ACCELERATION_VECTOR.ordinal()));
                            
                            newEntity.setSpatialRepresentation(
                                    new SpatialRVStruct(itemDeadReckoningAlgorithm, worldLocation, 
                                            isFrozen, orientation, velocityVector, accelerationVector, angularVelocityVector));
                            break;
                        default:
                            logger.warn("The algorithm {} is not yet implemented, the entity won't have any spatial reprentation.", itemDeadReckoningAlgorithm);
                            break;
                    }

                    fadEntities.add(newEntity);
                }

            } catch (IOException iOException) {
                logger.error("Error parsing the FADs", iOException);
                throw iOException;
            }
        }
        return fadEntities;
    }


    /**
     * Print all files in the specified absolute path for debugging purpose.
     * @param dir the absolute path of the directory.
     */
    public static void printAllFilesInDir(String dir) {
        File currentDir = new File(dir + File.separator);
        if (currentDir.isDirectory()) {
            File[] files = currentDir.listFiles();
            for (File file : files) {
                logger.info("{}\n{}",file.getAbsolutePath(),file.getName());
            }
        }
    }
    

}

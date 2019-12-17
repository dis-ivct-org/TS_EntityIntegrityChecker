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
package ca.drdc.ivct.tc_integritycheck_dis;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import ca.drdc.ivct.fom.utils.BaseEntityEqualUtils;
import ca.drdc.ivct.tc_lib_integritycheck_dis.CountdownTimer;
import org.slf4j.Logger;

import ca.drdc.ivct.fom.base.BaseEntity;
import ca.drdc.ivct.fom.base.structs.EntityIdentifierStruct;
import ca.drdc.ivct.fom.utils.BaseEntityCSVReader;
import de.fraunhofer.iosb.tc_lib.TcFailed;
import de.fraunhofer.iosb.tc_lib.TcInconclusive;
import de.fraunhofer.iosb.tc_lib.converter.DisModelConverter;
import de.fraunhofer.iosb.tc_lib.dis.DISAbstractTestCase;

public class BaseEntityIntegrityTC_0001 extends DISAbstractTestCase {

    private List<BaseEntity> fad;
    private static final String LINE_SEPARATOR = "---------------------------------------------------------------------";
    private Logger logger;

    @Override
    protected void logTestPurpose(Logger logger) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n"+LINE_SEPARATOR+"\n");
        stringBuilder.append("TEST PURPOSE\n");
        stringBuilder.append("Tests if a SuT federate creates BaseEntity objects with EntityIdentifiers\n");
        stringBuilder.append("that match the ones defined in the federation agreement document (FAD). \n");
        stringBuilder.append("The FAD is publised as a csv document containing listings of EntityId, \n");
        stringBuilder.append("EntityType and Spatial string enumerations.\n");
        stringBuilder.append(LINE_SEPARATOR+"\n");
        stringBuilder.append("TC_0001 check the entities' identifiers, type and spatial match.\n");
        stringBuilder.append(LINE_SEPARATOR+"\n");
        String testPurpose = stringBuilder.toString();

        logger.info(testPurpose);
    }

    @Override
    protected void preambleAction(Logger logger) throws TcInconclusive {

        this.logger = logger;
        // Load all files in test cases folder. This constitutes the federation
        // agreement document (FAD)
        try {
            this.fad = BaseEntityCSVReader.loadCSVFileToBaseEntityList(super.param.getFadUrls());
            if (fad.isEmpty()) {
                throw new TcInconclusive("The FAD is empty.");
            }
        } catch (IOException | ParseException e) {
            throw new TcInconclusive("Could not load the FAD.", e);
        }

        logger.info("Send entity from the to this DIS federate. You have 10 seconds");

        // Let five second to client to discover the base Entity.
        new CountdownTimer(super.param.getWaitingPeriod(), logger).run();
    }

    /**
     * Tests discovered BaseEntity objects by comparing them with the ones in the
     * FAD.
     * 
     * @throws TcInconclusive due to connection errors or csv
     * @throws TcFailed       due to entities not being the same
     */
    @Override
    protected void performTest(Logger logger) throws TcInconclusive, TcFailed {
        // Gather received entities
        List<BaseEntity> receivedEntities = super.disManager.getReceivedEntities().stream()
                .map(DisModelConverter::disEntityToRpr)
                .collect(Collectors.toList());

        if (receivedEntities.isEmpty()) {
            logger.warn("No entity received");
            throw new TcInconclusive("No BaseEntity objects found on the RTI bus. A system "
                    + "under test must create discoverable BaseEntity objects before attempting the test.");
        }
        
        // take only the first instance of the received each Entities
        Map<EntityIdentifierStruct, BaseEntity> receivedEntitiesWithoutDuplicate = new HashMap<>();
        receivedEntities.stream().forEachOrdered(
                receivedEntitie -> receivedEntitiesWithoutDuplicate.computeIfAbsent(receivedEntitie.getEntityIdentifier(),
                key -> receivedEntitie ));

        // Traverse the discovered BaseEntity objects.
        boolean testPassed = true;

        for (BaseEntity fadEntity : fad) {
            
            boolean isEntityIdentifierEqual = receivedEntitiesWithoutDuplicate.containsKey(fadEntity.getEntityIdentifier());

            if (!isEntityIdentifierEqual){
                testPassed = false;
                logger.info("\n"+LINE_SEPARATOR+"\nFAIL: BaseEntity from FAD with identifier {} found no identity match in " +
                        "discovered Base Entities", fadEntity.getEntityIdentifier()+"\n"+LINE_SEPARATOR);
            } else {
                BaseEntity receivedBaseEntity = receivedEntitiesWithoutDuplicate.get(fadEntity.getEntityIdentifier());
                boolean isEntityTypeEqual = fadEntity.getEntityType().equals(receivedBaseEntity.getEntityType());
                boolean isSpatialEqual = isSpatialEqual(fadEntity, receivedBaseEntity);
                testPassed = isEntityTypeEqual && isSpatialEqual;
            }
        }

        // test if received the good amount of entities according to the fad
        boolean isNumberOfEntityReceivedGood = isNumberOfEntityReceivedGood(receivedEntitiesWithoutDuplicate.size(), fad.size());
        testPassed = testPassed && isNumberOfEntityReceivedGood;

        if (!testPassed) {
            throw new TcFailed("Test failed due to errors in entity(ies) or absent/unrecognized entity(ies).");
        } else {
            logger.info("\n"+LINE_SEPARATOR+ "\n" +
                    "TEST IS COMPLETED SUCCESFULLY.\n" +LINE_SEPARATOR);
        }
    }

    /**
     *  test if the spatial parameter of the received baseentity match the fad
     *
     *  Note orientation && velocity && deadReckoning is not taken into account in the final judgment
     *
     * @param fadEntity entity from the fad
     * @param receivedBaseEntity entity received
     * @return true if it match
     */
    private boolean isSpatialEqual(BaseEntity fadEntity, BaseEntity receivedBaseEntity){
        boolean testPassed = true;
        BaseEntityEqualUtils equalUtils = new BaseEntityEqualUtils(super.param.getSpatialValueThreshold());

        boolean worldLocationEqual = equalUtils.worlLocationEqual(fadEntity.getSpatialRepresentation().getWorldLocation(), receivedBaseEntity.getSpatialRepresentation().getWorldLocation());

        boolean frozenEqual = fadEntity.getSpatialRepresentation().isFrozen() == receivedBaseEntity.getSpatialRepresentation().isFrozen();


        // The rest of the spatialIntegrity check is still performed but does not affect the final judgment.
        boolean orientationEqual = equalUtils.orientationEqual(fadEntity.getSpatialRepresentation().getOrientation(),  receivedBaseEntity.getSpatialRepresentation().getOrientation());
        boolean velocityEqual = equalUtils.velocityEqual(fadEntity.getSpatialRepresentation().getVelocityVector(), receivedBaseEntity.getSpatialRepresentation().getVelocityVector());
        boolean deadReckonEqual = equalUtils.baseEntityDeadReckonEqual(fadEntity.getSpatialRepresentation(),receivedBaseEntity.getSpatialRepresentation());


        if(!(orientationEqual && velocityEqual && deadReckonEqual)){
            logger.warn("WARNING: BaseEntity from FAD with identifier {} does match the received entity but some spatial properties that does not affect the final judgment mismatch: \n"
                    + "Orientation [{}]; Velocity [{}], DeadReckoning [{}]",fadEntity.getEntityIdentifier(), orientationEqual, velocityEqual, deadReckonEqual);
        }

        if (!worldLocationEqual || !frozenEqual ) {
            testPassed = false;
            logger.info("\n"+LINE_SEPARATOR+ "\nFAIL: BaseEntity from FAD with identifier {} does not match the received entity: \n"+LINE_SEPARATOR
                    + "worldLocationEqual [{}]; frozenEqual [{}]",fadEntity.getEntityIdentifier(), worldLocationEqual, frozenEqual);
        }
        else {
            logger.info("\n"+LINE_SEPARATOR+ "\nOKAY: BaseEntity from FAD with identifier " + fadEntity.getEntityIdentifier()
                    + " was found in discovered Base Entities\n"+LINE_SEPARATOR);
        }
        return testPassed;
    }

    /**
     * test if received the good amount of entities according to the fad
     * @param receivedBaseEntitySize number of received entity
     * @param fadSize nubmer of entity in the fad
     * @return true if the receivedBaseEntitySize match fadSize
     */
    private boolean isNumberOfEntityReceivedGood( int receivedBaseEntitySize, int fadSize) {
        boolean testPassed = true;
        if (receivedBaseEntitySize < fadSize) {
            testPassed = false;
            logger.info(LINE_SEPARATOR+"+\nFAIL: Received less entities then expected. Received : {}, Expected : {}\n" +
                    ""+LINE_SEPARATOR, receivedBaseEntitySize, fadSize);

        }
        else if (receivedBaseEntitySize > fad.size()){
            testPassed = false;
            logger.info(LINE_SEPARATOR+"+\nFAIL: Received more entities then expected. Received : {}, Expected : {}\n" +
                            ""+LINE_SEPARATOR,
                    receivedBaseEntitySize, fad.size());
        }
        return testPassed;
    }
}

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

package ca.drdc.ivct.tc_lib_integritycheck;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;

import ca.drdc.ivct.baseentity.BaseEntity;
import ca.drdc.ivct.baseentity.EntityIdentifier;
import ca.drdc.ivct.baseentity.EntityType;
import ca.drdc.ivct.hla.coders.entitytypecoders.EntityIdentifierStructCoder;
import ca.drdc.ivct.hla.coders.entitytypecoders.EntityTypeStructCoder;
import ca.drdc.ivct.hla.coders.spatialcoders.SpatialCoder;
import de.fraunhofer.iosb.tc_lib.IVCT_RTIambassador;
import de.fraunhofer.iosb.tc_lib.IVCT_TcParam;
import de.fraunhofer.iosb.tc_lib.TcFailed;
import de.fraunhofer.iosb.tc_lib.TcInconclusive;
import de.fraunhofer.iosb.tc_lib.IVCT_BaseModel;
import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.FederateAmbassador;
import hla.rti1516e.FederateHandle;
import hla.rti1516e.LogicalTime;
import hla.rti1516e.MessageRetractionHandle;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.OrderType;
import hla.rti1516e.TransportationTypeHandle;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.exceptions.AttributeNotDefined;
import hla.rti1516e.exceptions.FederateHandleNotKnown;
import hla.rti1516e.exceptions.FederateInternalError;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.InvalidFederateHandle;
import hla.rti1516e.exceptions.InvalidObjectClassHandle;
import hla.rti1516e.exceptions.NameNotFound;
import hla.rti1516e.exceptions.NotConnected;
import hla.rti1516e.exceptions.ObjectClassNotDefined;
import hla.rti1516e.exceptions.RTIinternalError;
import hla.rti1516e.exceptions.RestoreInProgress;
import hla.rti1516e.exceptions.SaveInProgress;

/**
 *  Base Model container for Integrity check testing.
 *  
 *  Use the IVCT ambassador to connect to the federation and gather entities from the federation.
 * 
 * @author mlavallee, laurenceo
 */
public class IntegrityCheckBaseModel extends IVCT_BaseModel {

    private AttributeHandleSet baseEntAttrSet;
    private AttributeHandle attrEntityType;
    private AttributeHandle attrEntityIdentifier;
    private AttributeHandle attrSpatial;
    private IVCT_RTIambassador ivctRti;
    private final Map<ObjectInstanceHandle, BaseEntity> discoveredEntities = new HashMap<>();
    private Logger logger;

    /**
     * @param logger reference to a logger
     * @param ivctRti reference to the RTI ambassador
     * @param ivctTcParam ivct_TcParam
     */
    public IntegrityCheckBaseModel(Logger logger, IVCT_RTIambassador ivctRti, IVCT_TcParam ivctTcParam) {
        super(ivctRti, logger, ivctTcParam);
        this.logger = logger;
        this.ivctRti = ivctRti;
    }

    /**
     * @param federateHandle the federate handle
     * @return the federate name or null
     */
    public String getFederateName(FederateHandle federateHandle) {

        try {
            return this.ivctRti.getFederateName(federateHandle);
        } catch (InvalidFederateHandle | FederateHandleNotKnown | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
            logger.error("Error extracting federate name from the ambassador",e);
            return null;
        }

    }

    /**
     * @return true means error, false means correct
     */
    public boolean init() {
        ObjectClassHandle baseEntityHandle;

        try {
            baseEntityHandle = this.ivctRti.getObjectClassHandle("BaseEntity");
            this.attrEntityIdentifier = this.ivctRti.getAttributeHandle(baseEntityHandle, "EntityIdentifier");
            this.attrEntityType = this.ivctRti.getAttributeHandle(baseEntityHandle, "EntityType");
            this.attrSpatial = this.ivctRti.getAttributeHandle(baseEntityHandle, "Spatial");

        } catch (NameNotFound | FederateNotExecutionMember | NotConnected | RTIinternalError | InvalidObjectClassHandle ex) {
            this.logger.error("Cannot get object class handle or attribute handle");
            return true;
        }

        try {
            baseEntAttrSet = this.ivctRti.getAttributeHandleSetFactory().create();
            baseEntAttrSet.add(this.attrEntityIdentifier);
            baseEntAttrSet.add(this.attrEntityType);
            baseEntAttrSet.add(this.attrSpatial);
        } catch (FederateNotExecutionMember | NotConnected ex) {
            this.logger.error("Cannot build attribute set");
            return true;
        }

        try {
            // Only need to subscribe to the object class
            this.ivctRti.subscribeObjectClassAttributes(baseEntityHandle, baseEntAttrSet);
        } catch (AttributeNotDefined | ObjectClassNotDefined | SaveInProgress | RestoreInProgress
                | FederateNotExecutionMember | NotConnected | RTIinternalError ex) {
            this.logger.error("Cannot publish/subscribe attributes");
            return true;
        }

        return false;
    }

    /**
     * @param fad The list of base entity included in the fad.
     * @return true means error, false means correct
     * @throws TcFailed due to discrepancies between fad and discovered entities
     * @throws TcInconclusive when no entities are found
     */
    public boolean testBaseEntityIntegrityIdentity(List<BaseEntity> fad) throws TcFailed, TcInconclusive {

        logger.info("Executing Test");

        String lineSeparator = "\n---------------------------------------------------------------------\n";

        if (discoveredEntities.isEmpty()) {
            throw new TcInconclusive("No BaseEntity objects found on the RTI bus. A system "
                    + "under test must create discoverable BaseEntity objects before attempting the test.");
        }
        // Traverse the discovered BaseEntity objects.
        boolean testPassed = true;
        boolean fadEntityPassesTest;
        StringBuilder failedStringBuilder = new StringBuilder();
        if (fad.size() != discoveredEntities.size()) {
            testPassed = false;
            String failedMessage = "FAIL: Fad and discovered entities sizes do not match";
            failedStringBuilder.append(failedMessage);
            logger.info(failedMessage);
        }

        for (BaseEntity fadEntity : fad) {
            // Loop over each discovered entity to check if this fadEntity is present
            fadEntityPassesTest = discoveredEntities.values().stream()
                    .anyMatch(discoveredEntity -> discoveredEntity.getEntityIdentifier().equals(fadEntity.getEntityIdentifier()));
            String failedMessage;
            if (!fadEntityPassesTest) {
                testPassed = false;
                failedMessage = "FAIL: BaseEntity from FAD with identifier " + fadEntity.getEntityIdentifier()
                                     + " found no identity match in discovered Base Entities";
                failedStringBuilder.append("\n"+failedMessage);
            } else {
                failedMessage = "OKAY: BaseEntity from FAD with identifier " + fadEntity.getEntityIdentifier()
                                       + "was found in discovered Base Entities";
                failedStringBuilder.append("\n"+failedMessage);
            }
            logger.info(lineSeparator+failedMessage+lineSeparator);

        }
        if (!testPassed) {
            throw new TcFailed("Test failed due to errors in entity(ies) or absent/unrecognized entity(ies) : "+failedStringBuilder.toString()) ;
        } else {
            logger.info("{} TEST IS COMPLETED SUCCESFULLY. {}",lineSeparator,lineSeparator);
            return false;
        }
    }

    /**
     * @param fad The list of base entity included in the fad.
     * @return true means error, false means correct
     * @throws TcFailed due to discrepancies between fad and discovered entities
     * @throws TcInconclusive when no entities are found
     */
    public boolean testBaseEntityIntegrityType(List<BaseEntity> fad) throws TcFailed, TcInconclusive {

        logger.info("Executing Test");

        if (discoveredEntities.isEmpty()) {
            throw new TcInconclusive("No BaseEntity objects found on the RTI bus. A system "
                    + "under test must create discoverable BaseEntity objects before attempting the test.");
        }
        // Traverse the discovered BaseEntity objects.
        boolean testPassed = true;
        boolean fadEntityPassesTest;
        for (BaseEntity fadEntity : fad) {
            // Loop over each discovered entity to check if this fadEntity is present
            Optional<BaseEntity> optionalBase = 
                    discoveredEntities.values()
                    .stream()
                    .filter(discoveredEntity -> discoveredEntity.getEntityIdentifier().equals(fadEntity.getEntityIdentifier()))
                    .findFirst();
            
            if (!optionalBase.isPresent()) {
                testPassed = false;
                logger.info("---------------------------------------------------------------------");
                logger.info("FAIL: BaseEntity from FAD with identifier " + fadEntity.getEntityIdentifier()
                        + " found no ID match in discovered Base Entities");
                logger.info("---------------------------------------------------------------------");
            } else {
              
                fadEntityPassesTest = fadEntity.getEntityType().equals(optionalBase.get().getEntityType());
                if (!fadEntityPassesTest) {
                    testPassed = false;
                    logger.info("---------------------------------------------------------------------");
                    logger.info("FAIL: BaseEntity from FAD with identifier " + fadEntity.getEntityIdentifier()
                            + " has not the same type as its discovered Base Entity");
                    logger.info("---------------------------------------------------------------------");
                } else {
                    logger.info("---------------------------------------------------------------------");
                    logger.info("OKAY: BaseEntity from FAD with identifier " + fadEntity.getEntityIdentifier()
                            + " found an ID and type match in discovered Base Entities");
                    logger.info("---------------------------------------------------------------------");
                }
            }
        }
        if (!testPassed) {
            throw new TcFailed("Test failed due to errors in entity(ies) or absent/unrecognized entity(ies).");
        } else {
            logger.info("---------------------------------------------------------------------");
            logger.info("TEST IS COMPLETED SUCCESFULLY.");
            logger.info("---------------------------------------------------------------------");
            return false;
        }
    }
    
    /**
     * @param fad The list of base entity included in the fad.
     * @return true means error, false means correct
     * @throws TcFailed due to discrepancies between fad and discovered entities
     * @throws TcInconclusive when no entities are found
     */
    public boolean testBaseEntityIntegritySpatial(List<BaseEntity> fad) throws TcFailed, TcInconclusive {

        logger.info("Executing Test");

        if (discoveredEntities.isEmpty()) {
            throw new TcInconclusive("No BaseEntity objects found on the RTI bus. A system "
                    + "under test must create discoverable BaseEntity objects before attempting the test.");
        }
        // Traverse the discovered BaseEntity objects.
        boolean testPassed = true;
        boolean fadEntityPassesTest;
        for (BaseEntity fadEntity : fad) {
            // Loop over each discovered entity to check if this fadEntity is present
            Optional<BaseEntity> optionalBase = 
                    discoveredEntities.values()
                    .stream()
                    .filter(discoveredEntity -> discoveredEntity.getEntityIdentifier().equals(fadEntity.getEntityIdentifier()))
                    .findFirst();
            
            if (!optionalBase.isPresent()) {
                testPassed = false;
                logger.info("---------------------------------------------------------------------");
                logger.info("FAIL: BaseEntity from FAD with identifier " + fadEntity.getEntityIdentifier()
                        + " found no ID match in discovered Base Entities");
                logger.info("---------------------------------------------------------------------");
            } else {
                logger.debug("\nFrom fad:{}\nFrom fed:{}",
                        fadEntity.getSpatialRepresentation(),
                        optionalBase.get().getSpatialRepresentation());
                fadEntityPassesTest = fadEntity.getSpatialRepresentation().equals(optionalBase.get().getSpatialRepresentation());
                if (!fadEntityPassesTest) {
                    testPassed = false;
                    logger.info("---------------------------------------------------------------------");
                    logger.info("FAIL: BaseEntity from FAD with identifier " + fadEntity.getEntityIdentifier()
                            + " has not the same spatial info as its discovered Base Entity");
                    logger.info("---------------------------------------------------------------------");
                } else {
                    logger.info("---------------------------------------------------------------------");
                    logger.info("OKAY: BaseEntity from FAD with identifier " + fadEntity.getEntityIdentifier()
                            + " found an ID and spatial match in discovered Base Entities");
                    logger.info("---------------------------------------------------------------------");
                }
            }
        }
        if (!testPassed) {
            throw new TcFailed("Test failed due to errors in entity(ies) or absent/unrecognized entity(ies).");
        } else {
            logger.info("---------------------------------------------------------------------");
            logger.info("TEST IS COMPLETED SUCCESFULLY.");
            logger.info("---------------------------------------------------------------------");
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void discoverObjectInstance(ObjectInstanceHandle theObject, ObjectClassHandle theObjectClass, String objectName) throws FederateInternalError {

        if (!this.discoveredEntities.containsKey(theObject)) {
            // This part request an update for attribute values. 
            // Calling this update should trigger a value update for out discoveredEntity
            try {
                ivctRti.requestAttributeValueUpdate(theObjectClass, baseEntAttrSet, null);
            } catch (AttributeNotDefined | ObjectClassNotDefined | SaveInProgress | RestoreInProgress
                    | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
                logger.error("Error requesting Attribute Value Update",e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeObjectInstance(ObjectInstanceHandle theObject, byte[] userSuppliedTag,
             OrderType sentOrdering, FederateAmbassador.SupplementalRemoveInfo removeInfo) {

        final BaseEntity baseEntity = this.discoveredEntities.remove(theObject);
        if (baseEntity != null) {
            logger.info("{} has been deleted", baseEntity);
        }
    }

    /**
     * @param theObject the object instance handle
     * @param theAttributes the map of attribute handle / value
     */
    public void doReflectAttributeValues(ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes) {

        if (theAttributes.containsKey(this.attrEntityIdentifier) && theAttributes.containsKey(this.attrEntityType)) {
            BaseEntity baseEntity = new BaseEntity();

            try {
                EntityIdentifierStructCoder entityIdentifierStructCoder = new EntityIdentifierStructCoder(ivctRti.getEncoderFactory());
                EntityTypeStructCoder entityTypeStructCoder = new EntityTypeStructCoder(ivctRti.getEncoderFactory());
                
                EntityIdentifier entityIdentifier = entityIdentifierStructCoder.decodeToType(theAttributes.get(this.attrEntityIdentifier));
                EntityType entityType = entityTypeStructCoder.decodeToType(theAttributes.get(this.attrEntityType));
                
                baseEntity.setEntityIdentifier(entityIdentifier);
                baseEntity.setEntityType(entityType);
            } catch (DecoderException e) {
                logger.error("Error decoding entity : ", e);
            }
            
            if (theAttributes.containsKey(this.attrSpatial)) {
                try { 
                    SpatialCoder spatialCoder = new SpatialCoder(ivctRti.getEncoderFactory());
                    baseEntity.setSpatialRepresentation(spatialCoder.decode(theAttributes.get(this.attrSpatial)));
                } catch (DecoderException e) {
                    logger.error("Error decoding entity's spatial info ", e);
                }
            }

            discoveredEntities.put(theObject, baseEntity);
        }
    }

    /**
     * @param theObject the object instance handle
     * @param theAttributes the map of attribute handle / value
     */
    @Override
    public void reflectAttributeValues(ObjectInstanceHandle theObject,
            AttributeHandleValueMap theAttributes, byte[] userSuppliedTag, OrderType sentOrdering,
            TransportationTypeHandle theTransport, SupplementalReflectInfo reflectInfo)
            throws FederateInternalError {
        
        this.doReflectAttributeValues(theObject, theAttributes);
    }

    /**
     * @param theObject the object instance handle
     * @param theAttributes the map of attribute handle / value
     */
    @Override
    public void reflectAttributeValues(final ObjectInstanceHandle theObject,
            AttributeHandleValueMap theAttributes, byte[] userSuppliedTag, OrderType sentOrdering,
            TransportationTypeHandle theTransport, LogicalTime theTime, OrderType receivedOrdering,
            SupplementalReflectInfo reflectInfo) throws FederateInternalError {

        this.doReflectAttributeValues(theObject, theAttributes);
    }

    /**
     * @param theObject the object instance handle
     * @param theAttributes the map of attribute handle / value
     */
    @Override
    public void reflectAttributeValues(ObjectInstanceHandle theObject,
            AttributeHandleValueMap theAttributes, byte[] userSuppliedTag, OrderType sentOrdering,
            TransportationTypeHandle theTransport, LogicalTime theTime, OrderType receivedOrdering,
            MessageRetractionHandle retractionHandle, SupplementalReflectInfo reflectInfo)
            throws FederateInternalError {

        this.doReflectAttributeValues(theObject, theAttributes);
    }
}

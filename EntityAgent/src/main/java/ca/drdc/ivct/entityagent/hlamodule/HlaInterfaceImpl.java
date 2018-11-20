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

package ca.drdc.ivct.entityagent.hlamodule;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.drdc.ivct.entityagent.Controller;
import ca.drdc.ivct.entityagent.hlamodule.rpr.RprClass;
import ca.drdc.ivct.entityagent.hlamodule.rpr.RprPlatformPackage;
import ca.drdc.ivct.tc_lib_integritycheck.baseentity.BaseEntity;
import ca.drdc.ivct.tc_lib_integritycheck.baseentity.coders.entitytypecoders.EntityIdentifierStructCoder;
import ca.drdc.ivct.tc_lib_integritycheck.baseentity.coders.entitytypecoders.EntityTypeStructCoder;
import ca.drdc.ivct.tc_lib_integritycheck.baseentity.coders.spatialcoders.SpatialCoder;
import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.CallbackModel;
import hla.rti1516e.NullFederateAmbassador;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.ResignAction;
import hla.rti1516e.RtiFactory;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.exceptions.AlreadyConnected;
import hla.rti1516e.exceptions.AttributeNotDefined;
import hla.rti1516e.exceptions.AttributeNotOwned;
import hla.rti1516e.exceptions.CallNotAllowedFromWithinCallback;
import hla.rti1516e.exceptions.ConnectionFailed;
import hla.rti1516e.exceptions.CouldNotCreateLogicalTimeFactory;
import hla.rti1516e.exceptions.CouldNotOpenFDD;
import hla.rti1516e.exceptions.ErrorReadingFDD;
import hla.rti1516e.exceptions.FederateAlreadyExecutionMember;
import hla.rti1516e.exceptions.FederateInternalError;
import hla.rti1516e.exceptions.FederateIsExecutionMember;
import hla.rti1516e.exceptions.FederateNameAlreadyInUse;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.FederateOwnsAttributes;
import hla.rti1516e.exceptions.FederateServiceInvocationsAreBeingReportedViaMOM;
import hla.rti1516e.exceptions.FederatesCurrentlyJoined;
import hla.rti1516e.exceptions.FederationExecutionAlreadyExists;
import hla.rti1516e.exceptions.FederationExecutionDoesNotExist;
import hla.rti1516e.exceptions.InconsistentFDD;
import hla.rti1516e.exceptions.InvalidLocalSettingsDesignator;
import hla.rti1516e.exceptions.InvalidObjectClassHandle;
import hla.rti1516e.exceptions.InvalidResignAction;
import hla.rti1516e.exceptions.NameNotFound;
import hla.rti1516e.exceptions.NotConnected;
import hla.rti1516e.exceptions.ObjectClassNotDefined;
import hla.rti1516e.exceptions.ObjectClassNotPublished;
import hla.rti1516e.exceptions.ObjectInstanceNotKnown;
import hla.rti1516e.exceptions.OwnershipAcquisitionPending;
import hla.rti1516e.exceptions.RTIinternalError;
import hla.rti1516e.exceptions.RestoreInProgress;
import hla.rti1516e.exceptions.SaveInProgress;
import hla.rti1516e.exceptions.UnsupportedCallbackModel;

/*
 * This class implements the HlaInterface.
 */
class HlaInterfaceImpl extends NullFederateAmbassador implements HlaInterface {

    private static Logger logger = LoggerFactory.getLogger(HlaInterfaceImpl.class);

    private RTIambassador ambassador;

    private EntityTypeStructCoder entTypeStructCoder;
    private EntityIdentifierStructCoder entIdStructCoder;
    private SpatialCoder spatialCoder;

    private RprPlatformPackage rprPlatform;

    private AttributeHandle attrEntityType;
    private AttributeHandle attrEntityIdentifier;
    private AttributeHandle attrSpatial;

    private final Controller controller;

    public HlaInterfaceImpl(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void start(String localSettingsDesignator, String fomPath, String federationName, String federateName)
            throws RestoreInProgress, SaveInProgress, NotConnected, FederateServiceInvocationsAreBeingReportedViaMOM,
            RTIinternalError, ConnectionFailed, InvalidLocalSettingsDesignator, ErrorReadingFDD, CouldNotOpenFDD,
            InconsistentFDD {

        RtiFactory rtiFactory = RtiFactoryFactory.getRtiFactory();
        ambassador = rtiFactory.getRtiAmbassador();

        EncoderFactory encoderFactory = rtiFactory.getEncoderFactory();

        entIdStructCoder = new EntityIdentifierStructCoder(encoderFactory);
        entTypeStructCoder = new EntityTypeStructCoder(encoderFactory);
        spatialCoder = new SpatialCoder(encoderFactory);

        try {
            ambassador.connect(this, CallbackModel.HLA_IMMEDIATE, localSettingsDesignator);
        } catch (AlreadyConnected ignored) {
        } catch (UnsupportedCallbackModel | CallNotAllowedFromWithinCallback e) {
            throw new RTIinternalError("HlaInterfaceFailure", e);
        }

        try {
            ambassador.destroyFederationExecution(federationName);
        } catch (FederatesCurrentlyJoined e) {
            logger.warn(this.toString() + ": Tried to destroy federation " + federationName
                    + " but federation still has active federates.");
        } catch (FederationExecutionDoesNotExist ignored) {
        }

        URL[] url = loadFomModules(fomPath);
        try {
            ambassador.createFederationExecution(federationName, url);
        } catch (FederationExecutionAlreadyExists e) {
            logger.warn(this.toString() + ": Tried to create federation " + federationName
                    + " but the federation already exists.");
        }

        try {
            boolean joined = false;
            String federateNameSuffix = "";
            int federateNameIndex = 1;
            while (!joined) {
                try {
                    ambassador.joinFederationExecution(federateName + federateNameSuffix, "EntSimJ", federationName,
                            url);
                    joined = true;
                    federateName = federateName + federateNameSuffix;
                } catch (FederateNameAlreadyInUse e) {
                    federateNameSuffix = "-" + federateNameIndex++;
                }
            }
        } catch (FederateAlreadyExecutionMember ignored) {
        } catch (CouldNotCreateLogicalTimeFactory | FederationExecutionDoesNotExist
                | CallNotAllowedFromWithinCallback e) {
            throw new RTIinternalError("HlaInterfaceFailure", e);
        }

        try {
            getHandles();

            registerPublicationBaseEntity();
        } catch (FederateNotExecutionMember e) {
            throw new RTIinternalError("HlaInterfaceFailure", e);
        }
    }

    @Override
    public void stop() throws RTIinternalError {
        if (ambassador == null) {
            logger.warn("HlaInterfaceImpl.stop: abassador doesn't exist!");
            return;
        }
        try {
            try {
                ambassador.resignFederationExecution(ResignAction.CANCEL_THEN_DELETE_THEN_DIVEST);
            } catch (FederateNotExecutionMember ignored) {
                logger.error("HlaInterface.stop: FederateNotExecutionMember exception: ", ignored);
            } catch (FederateOwnsAttributes e) {
                logger.error("HlaInterface.stop: FederateOwnsAttributes exception: ", e);
                throw new RTIinternalError("HlaInterfaceFailure", e);
            } catch (OwnershipAcquisitionPending e) {
                logger.error("HlaInterface.stop: OwnershipAcquisitionPending exception: ", e);
                throw new RTIinternalError("HlaInterfaceFailure", e);
            } catch (CallNotAllowedFromWithinCallback e) {
                logger.error("HlaInterface.stop: CallNotAllowedFromWithinCallback1 exception: ", e);
                throw new RTIinternalError("HlaInterfaceFailure", e);
            } catch (InvalidResignAction e) {
                logger.error("HlaInterface.stop: InvalidResignAction exception: ", e);
                throw new RTIinternalError("HlaInterfaceFailure", e);
            }

            try {
                ambassador.disconnect();
            } catch (FederateIsExecutionMember e) {
                logger.error("HlaInterface.stop: FederateIsExecutionMember exception: ", e);
                throw new RTIinternalError("HlaInterfaceFailure", e);

            } catch (CallNotAllowedFromWithinCallback e) {
                logger.error("HlaInterface.stop: CallNotAllowedFromWithinCallback exception: ", e);
                throw new RTIinternalError("HlaInterfaceFailure", e);
            }
        } catch (NotConnected ignored) {
            logger.error("HlaInterface.stop: NotConnected exception: ", ignored);
        }
    }

    private void getHandles() throws RTIinternalError, FederateNotExecutionMember, NotConnected {
        try {
            rprPlatform = new RprPlatformPackage(ambassador);
            rprPlatform.getClassHandles();

            attrEntityType = ambassador.getAttributeHandle(
                    rprPlatform.getClassHandles().get(RprClass.BASE_ENTITY.getName()), "EntityType");

            attrEntityIdentifier = ambassador.getAttributeHandle(
                    rprPlatform.getClassHandles().get(RprClass.BASE_ENTITY.getName()), "EntityIdentifier");

            attrSpatial = ambassador.getAttributeHandle(rprPlatform.getClassHandles().get(RprClass.BASE_ENTITY.getName()), "Spatial");

        } catch (NameNotFound | InvalidObjectClassHandle e) {
            throw new RTIinternalError("HlaInterfaceFailure", e);
        }
    }

    private void registerPublicationBaseEntity()
            throws FederateNotExecutionMember, NotConnected, RestoreInProgress, SaveInProgress, RTIinternalError {

        try {
            AttributeHandleSet baseEntAttrSet = ambassador.getAttributeHandleSetFactory().create();
            baseEntAttrSet.add(attrEntityType);
            baseEntAttrSet.add(attrEntityIdentifier);
            baseEntAttrSet.add(attrSpatial);

            ambassador.publishObjectClassAttributes(
                    rprPlatform.getClassHandles().get(RprClass.BASE_ENTITY.getName()),
                    baseEntAttrSet);

        } catch (AttributeNotDefined | ObjectClassNotDefined e) {
            throw new RTIinternalError("HlaInterfaceFailure", e);
        } catch (NameNotFound e) {
            logger.error("Class Handles Name Not Found", e);
        }

    }

    @Override
    public void connectionLost(String faultDescription) throws FederateInternalError {
        logger.error("HlaInterfaceImpl.connectionLost: Lost Connection because: {}", faultDescription);
    }

    @Override
    public void createBaseEntity(BaseEntity baseEntity)
            throws FederateNotExecutionMember, RestoreInProgress, SaveInProgress, NotConnected, RTIinternalError {

        try {
            ObjectInstanceHandle baseEntityHandle = ambassador
                    .registerObjectInstance(rprPlatform.getClassHandles().get(RprClass.BASE_ENTITY.getName()));
            AttributeHandleValueMap attributeMap = ambassador.getAttributeHandleValueMapFactory().create(3);

            attributeMap.put(attrEntityIdentifier, entIdStructCoder.encode(baseEntity.getEntityIdentifier()));
            attributeMap.put(attrEntityType, entTypeStructCoder.encode(baseEntity.getEntityType()));
            attributeMap.put(attrSpatial, spatialCoder.encode(baseEntity.getSpatialRepresentation()));

            ambassador.updateAttributeValues(baseEntityHandle, attributeMap, "new Base Entity".getBytes());

            controller.getBaseEntityInstanceMap().put(baseEntityHandle, attributeMap);

        } catch (ObjectInstanceNotKnown | AttributeNotDefined | AttributeNotOwned | ObjectClassNotPublished
                | ObjectClassNotDefined | NameNotFound e) {

            throw new RTIinternalError("HlaInterfaceFailure", e);
        }
    }

    @Override
    public void provideAttributeValueUpdate(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes,
            byte[] userSuppliedTag) throws FederateInternalError {

        try {
            ambassador.updateAttributeValues(theObject, controller.getBaseEntityInstanceMap().get(theObject), null);
        } catch (AttributeNotOwned | AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress
                | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
            logger.error("UpdateAttributeValues error", e);
        }

    }

    private URL[] loadFomModules(String pathToFomDirectory) {
        List<URL> urls = new ArrayList<>();
        File dir = null;
        try {
            dir = new File(pathToFomDirectory);
        } catch (NullPointerException e) {
            logger.error("No path to FOM directory provided. Check \"fom\" path in config file.", e);
            System.exit(0);
        }

        // Fill a list of URLs.
        File[] dirListing = dir.listFiles();
        if (dirListing != null) {
            for (File child : dirListing) {
                try {
                    urls.add(child.toURI().toURL());
                } catch (MalformedURLException e) {
                    logger.error("File not found at url : {}", child.toURI(), e);
                }
            }
        }

        // Convert the List<URL> to URL[]
        return urls.toArray(new URL[urls.size()]);
    }
}

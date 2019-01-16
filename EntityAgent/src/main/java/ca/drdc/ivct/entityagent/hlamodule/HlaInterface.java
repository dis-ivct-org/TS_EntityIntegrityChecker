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

import ca.drdc.ivct.baseentity.BaseEntity;
import ca.drdc.ivct.entityagent.Controller;
import hla.rti1516e.exceptions.ConnectionFailed;
import hla.rti1516e.exceptions.CouldNotOpenFDD;
import hla.rti1516e.exceptions.ErrorReadingFDD;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.FederateServiceInvocationsAreBeingReportedViaMOM;
import hla.rti1516e.exceptions.InconsistentFDD;
import hla.rti1516e.exceptions.InvalidLocalSettingsDesignator;
import hla.rti1516e.exceptions.NotConnected;
import hla.rti1516e.exceptions.RTIinternalError;
import hla.rti1516e.exceptions.RestoreInProgress;
import hla.rti1516e.exceptions.SaveInProgress;

public interface HlaInterface {

    /**
     * Connect to a CRC and join federation
     *
     * @param localSettingsDesignator The name to load settings for or "" to load default settings
     * @param fomPath path to FOM file
     * @param federationName Name of the federation to join
     * @param federateName The name you want for your federate
     * 
     * @throws FederateNotExecutionMember if the federate is not an executionMember
     * @throws RestoreInProgress the action cannot be done because the system is restoring state
     * @throws SaveInProgress the action cannot be done because the system is saving state
     * @throws NotConnected if the federate is not connected to a CRC
     * @throws FederateServiceInvocationsAreBeingReportedViaMOM FederateServiceInvocationsAreBeingReportedViaMOM
     * @throws RTIinternalError if the RTI fail unexpectedly
     * @throws ConnectionFailed if the RTI fail
     * @throws InvalidLocalSettingsDesignator  InvalidLocalSettingsDesignator
     * @throws ErrorReadingFDD if error with the FDD
     * @throws CouldNotOpenFDD if error with the FDD
     * @throws InconsistentFDD if error with the FDD
     */
    void start(String localSettingsDesignator, String fomPath, String federationName, String federateName)
            throws FederateNotExecutionMember, RestoreInProgress, SaveInProgress, NotConnected,
            FederateServiceInvocationsAreBeingReportedViaMOM, RTIinternalError, ConnectionFailed,
            InvalidLocalSettingsDesignator, ErrorReadingFDD, CouldNotOpenFDD, InconsistentFDD;

    /**
     * Resign and disconnect from CRC
     * @throws RTIinternalError if an internal error happen when stopping.
     */
    void stop() throws RTIinternalError;

    /*****************************************************************************
     * Interactions
     ****************************************************************************/

    /*****************************************************************************
     * Objects
     ****************************************************************************/

    /**
     * Create a base entity in the federate and publish it to the federation.
     * @param baseEntity the base entity to inject in the federation
     * @throws FederateNotExecutionMember the federate is not a member
     * @throws RestoreInProgress the action cannot be done because the system is restoring state
     * @throws SaveInProgress the action cannot be done because the system is saving state
     * @throws NotConnected if the federate is not connected to a CRC
     * @throws RTIinternalError if the RTI fail unexpectedly
     */
    public void createBaseEntity(BaseEntity baseEntity)
            throws FederateNotExecutionMember, RestoreInProgress, SaveInProgress, NotConnected, RTIinternalError;

    /**
     * HlaInterface factory generating concrete hlaInterface implementation
     */
    public static class Factory {
        private Factory() {}
        public static HlaInterface newInterface(Controller controller) {
            return new HlaInterfaceImpl(controller);
        }
    }
}

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
package ca.drdc.ivct.tc_integritycheck;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;

import ca.drdc.ivct.baseentity.BaseEntity;
import ca.drdc.ivct.tc_lib_integritycheck.CountdownTimer;
import ca.drdc.ivct.tc_lib_integritycheck.IntegrityCheckBaseModel;
import ca.drdc.ivct.tc_lib_integritycheck.IntegrityCheckTcParam;
import ca.drdc.ivct.utils.CSVReader;
import de.fraunhofer.iosb.tc_lib.AbstractTestCase;
import de.fraunhofer.iosb.tc_lib.IVCT_BaseModel;
import de.fraunhofer.iosb.tc_lib.IVCT_LoggingFederateAmbassador;
import de.fraunhofer.iosb.tc_lib.IVCT_RTI_Factory;
import de.fraunhofer.iosb.tc_lib.IVCT_RTIambassador;
import de.fraunhofer.iosb.tc_lib.TcFailed;
import de.fraunhofer.iosb.tc_lib.TcInconclusive;

public class BaseEntityIntegrityTC_0001 extends AbstractTestCase {

    private static final String FEDERATE_NAME = "BaseEntityTester";
    private IntegrityCheckTcParam tcParam;
    private IVCT_RTIambassador ivctRtiAmbassador;
    private IVCT_LoggingFederateAmbassador loggingFedAmbassador;
    private IntegrityCheckBaseModel baseEntityDataModel;
    private List<BaseEntity> fad ;
    private static final int FIVE_SECOND = 5;

    @Override
    protected IVCT_BaseModel getIVCT_BaseModel(String tcParamJson, Logger logger) throws TcInconclusive {
        tcParam = new IntegrityCheckTcParam(tcParamJson);
        ivctRtiAmbassador = IVCT_RTI_Factory.getIVCT_RTI(logger);
        baseEntityDataModel = new IntegrityCheckBaseModel(logger, ivctRtiAmbassador, tcParam);
        loggingFedAmbassador = new IVCT_LoggingFederateAmbassador( baseEntityDataModel, logger);

        return baseEntityDataModel;
    }

    @Override
    protected void logTestPurpose(Logger logger) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        stringBuilder.append("---------------------------------------------------------------------\n");
        stringBuilder.append("TEST PURPOSE\n");
        stringBuilder.append("Tests if a SuT federate creates BaseEntity objects with EntityIdentifiers\n");
        stringBuilder.append("that match the ones defined in the federation agreement document (FAD). \n");
        stringBuilder.append("The FAD is publised as a csv document containing listings of EntityId, \n");
        stringBuilder.append("EntityType and Spatial string enumerations.\n");
        stringBuilder.append("---------------------------------------------------------------------\n");
        stringBuilder.append("TC_0001 focus is to check the entities' identifiers match.\n");
        stringBuilder.append("---------------------------------------------------------------------\n");
        String testPurpose = stringBuilder.toString();

        logger.info(testPurpose);
    }

    @Override
    protected void preambleAction(Logger logger) throws TcInconclusive {
        logger.info("Attempting to connect to RTI with federate: {}", FEDERATE_NAME);
        // Initiate RTI
        baseEntityDataModel.initiateRti(FEDERATE_NAME, loggingFedAmbassador);

        // Get handles and publish / subscribe interactions
        if (baseEntityDataModel.init()) {
            throw new TcInconclusive("baseEntityDataModel.init() failed to execute");
        }

        // Load all files in test cases folder. This constitutes the federation agreement document (FAD)
        try {
            this.fad = CSVReader.loadCSVFileToBaseEntityList(Arrays.asList(tcParam.getFadUrls()), logger);
            if (fad.isEmpty()) {
                throw new TcInconclusive("The FAD is empty.");
            }
        } catch (IOException | ParseException e) {
            throw new TcInconclusive("Could not load the FAD.", e);
        }

        // Let five second to IVCT federation client to discover entity.
        new CountdownTimer(FIVE_SECOND, logger).run();
    }

    /**
     * Tests discovered BaseEntity objects by comparing them with the ones in
     * the FAD.
     * 
     * @throws TcInconclusive due to connection errors or csv
     * @throws TcFailed due to entities not being the same
     */
    @Override
    protected void performTest(Logger logger) throws TcInconclusive, TcFailed {
        logger.info("Welcome to the TestMaster Federate of the IVCT Federation");
        logger.info("Make sure that the Entity Agent federate has joined the federation!");

        baseEntityDataModel.testBaseEntityIntegrityIdentity(fad);
    }

    @Override
    protected void postambleAction(Logger logger) throws TcInconclusive {
        baseEntityDataModel.terminateRti();
        baseEntityDataModel = new IntegrityCheckBaseModel(logger, ivctRtiAmbassador, tcParam);
    }

}

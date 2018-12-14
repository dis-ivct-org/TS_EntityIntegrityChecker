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

package ca.drdc.ivct.entityagent;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.drdc.ivct.entityagent.hlamodule.HlaInterface;
import ca.drdc.ivct.tc_lib_integritycheck.baseentity.BaseEntity;
import ca.drdc.ivct.tc_lib_integritycheck.utils.CSVReader;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.NotConnected;
import hla.rti1516e.exceptions.RTIexception;
import hla.rti1516e.exceptions.RTIinternalError;
import hla.rti1516e.exceptions.RestoreInProgress;
import hla.rti1516e.exceptions.SaveInProgress;

public class Controller {

    private static Logger logger = LoggerFactory.getLogger(Controller.class);

    private HlaInterface hlaInterface;
    private EntityAgentConfig config;
    private Map<ObjectInstanceHandle, AttributeHandleValueMap> baseEntityInstanceMap;

    public void execute(EntityAgentConfig configInput) throws IOException, ParseException {

        if (configInput == null) {
            try {
                URL configFileUrl = this.getClass().getResource("/config/config.properties");
                logger.info("Found config file at: {}", configFileUrl.getPath());
                config = new EntityAgentConfig(configFileUrl.getFile());

            } catch (IOException | URISyntaxException e) {
                logger.error("Could not read EntityAgent config.properties", e);
            }
        } else {
            config = configInput;
        }

        hlaInterface = HlaInterface.Factory.newInterface(this);
        // Instantiate map for baseEntity references
        baseEntityInstanceMap = new HashMap<>();

        try {
            hlaInterface.start(config.getLocalSettingsDesignator(), config.getFom(), config.getFederationName(), config.getFederateName());
        } catch (RTIexception e) {
            logger.error("Could not connect to the RTI using the local settings designator {}", config.getLocalSettingsDesignator(), e);
            return;
        }

        // Load all files in testcases folder. This constitutes the federation agreement document (FAD)
        List<BaseEntity> fad = CSVReader.loadCSVFileToBaseEntityList(config.getTestcaseList(), logger);

        // Create BaseEntity objects in RTI
        for (BaseEntity baseEntity : fad) {
            try {
                hlaInterface.createBaseEntity(baseEntity);
                logger.info("Created BaseEntity {}", baseEntity);
            } catch (FederateNotExecutionMember | RestoreInProgress | SaveInProgress | NotConnected | RTIinternalError e) {
                logger.error("Error creating a base Entity", e);
            }
        }
    }

    public void stop() {
        try {
            if (hlaInterface == null) {
                logger.warn("Controller.stop: hlaInterface doesn't exist!");
                return;
            }
            hlaInterface.stop();
        } catch (RTIexception e) {
            logger.error(e.getMessage(),e);
        }

    }

    public Map<ObjectInstanceHandle, AttributeHandleValueMap> getBaseEntityInstanceMap() {
        return baseEntityInstanceMap;
    }
}

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

import ca.drdc.ivct.entityagent.hlamodule.HlaInterface;
import ca.drdc.ivct.fom.base.BaseEntity;
import ca.drdc.ivct.fom.utils.BaseEntityCSVReader;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.ConfigurationException;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controller {

    private static Logger logger = LoggerFactory.getLogger(Controller.class);

    private HlaInterface hlaInterface;
    private Map<ObjectInstanceHandle, AttributeHandleValueMap> baseEntityInstanceMap;

    public void execute(EntityAgentConfig config) throws ConfigurationException, IOException, ParseException, RTIexception {

        if (config == null) {
            String externalResourceFolder = System.getenv("IVCT_CONF");
            if (externalResourceFolder == null) {
                throw new ConfigurationException("IVCT_CONF is not defined");
            }
            config = new EntityAgentConfig(externalResourceFolder+"/IVCTsut/EntityAgent/resources");

        }

        hlaInterface = HlaInterface.Factory.newInterface(this);
        // Instantiate map for baseEntity references
        baseEntityInstanceMap = new HashMap<>();

        try {
            hlaInterface.start(config.getLocalSettingsDesignator(), config.getFom().getAbsolutePath(), config.getFederationName(), config.getFederateName());
        } catch (RTIexception e) {
            logger.error("Could not connect to the RTI using the local settings designator {}", config.getLocalSettingsDesignator(), e);
            throw e;
        }

        // Load all files in testcases folder. This constitutes the federation agreement document (FAD)
        List<BaseEntity> fad = BaseEntityCSVReader.loadCSVFileToBaseEntityList(config.getTestcaseList());

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

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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.ConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityAgentConfig {

    private static Logger logger = LoggerFactory.getLogger(EntityAgentConfig.class);

    private static final String LOCAL_SETTINGS_DESIGNATOR_PROP = "localSettingsDesignator";
    private static final String FEDERATION_NAME_PROP = "federationName";
    private static final String FEDERATE_NAME_PROP = "federateName";

    private static final String DEFAULT_LOCAL_SETTINGS_DESIGNATOR = "crcAddress=localhost";
    private static final String DEFAULT_FEDERATION_NAME = "IVCTFederation";
    private static final String DEFAULT_FEDERATE_NAME = "EntityAgent";

    private static final String TEST_CASE_DIR = "testcaseDir";
    private static final String FOM = "fom";

    private static final String CONFIG_DEFAULT_PATH = "/config/config.properties";

    private final String localSettingsDesignator;
    private final String federationName;
    private final String federateName;

    private List<URL> testcaseList;
    private File fomPath;

    /**
     *  create a EntityAgentConfig from a file
     * @param resourcePath path from the /resourceFolder
     * @throws ConfigurationException if either the config.properties is not found or the testcase directory define in the config is not found.
     */
    public EntityAgentConfig(String resourcePath) throws ConfigurationException {

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(resourcePath+CONFIG_DEFAULT_PATH));
        }
        catch(IOException e){
            throw new ConfigurationException("Could not load the config.properties file located in the folder "+resourcePath+CONFIG_DEFAULT_PATH);
        }
        localSettingsDesignator = properties.getProperty(LOCAL_SETTINGS_DESIGNATOR_PROP, DEFAULT_LOCAL_SETTINGS_DESIGNATOR);
        federationName = properties.getProperty(FEDERATION_NAME_PROP, DEFAULT_FEDERATION_NAME);
        federateName = properties.getProperty(FEDERATE_NAME_PROP, DEFAULT_FEDERATE_NAME);

        String testcaseDir = properties.getProperty(TEST_CASE_DIR, ".");

        File testcaseDirFile = new File(resourcePath + "/" + testcaseDir + "/");

        try {
            this.loadTestCasefiles(testcaseDirFile);
        }
        catch(IOException e){
            throw new ConfigurationException("Could not load the testcase files in directory located at "+testcaseDirFile.getPath());
        }
        fomPath = new File(resourcePath + "/" + FOM + "/");

    }

    private void loadTestCasefiles(File testcaseDirFile) throws MalformedURLException {

        testcaseList = new ArrayList<>();
        if (testcaseDirFile.isDirectory()) {
            File[] files = testcaseDirFile.listFiles();
            if (files != null) {
                for (File file : files) {
                    logger.info("Found test case: {}", file.getAbsolutePath());
                    testcaseList.add(file.toURI().toURL());
                }
            }
        } else {
            logger.error("{} directory was not found", testcaseDirFile);
        }
    }

    public String getLocalSettingsDesignator() {
        return localSettingsDesignator;
    }

    public String getFederationName() {
        return federationName;
    }

    public String getFederateName() {
        return federateName;
    }

    public File getFom() {
        return fomPath;
    }

    public List<URL> getTestcaseList() {
        return testcaseList;
    }

}

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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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

    private final String localSettingsDesignator;
    private final String federationName;
    private final String federateName;

    private List<URL> testcaseList;
    private String fomPath;

    public EntityAgentConfig(String fileName) throws IOException, URISyntaxException {
        this(new File(fileName));
    }

    public EntityAgentConfig(File file) throws IOException, URISyntaxException {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            logger.error("Could not find a valid properties file. " + file.toString(), e);
        }

        localSettingsDesignator = properties.getProperty(LOCAL_SETTINGS_DESIGNATOR_PROP,
                DEFAULT_LOCAL_SETTINGS_DESIGNATOR);
        federationName = properties.getProperty(FEDERATION_NAME_PROP, DEFAULT_FEDERATION_NAME);
        federateName = properties.getProperty(FEDERATE_NAME_PROP, DEFAULT_FEDERATE_NAME);

        String testcaseDir = properties.getProperty(TEST_CASE_DIR, ".");

        URL testcaseDirFileUrl = this.getClass().getResource("/" + testcaseDir + "/");
        File testcaseDirFile = null;
        try {
            testcaseDirFile = new File(testcaseDirFileUrl.toURI());
        } catch (URISyntaxException e) {
            testcaseDirFile = new File(testcaseDirFileUrl.getPath());
        }

        URL fomDir = this.getClass().getResource("/" + FOM + "/");
        if (new File(fomDir.getPath()).isDirectory()) {
            fomPath = fomDir.getPath();
            logger.info("Found FOM directory from config at: {}", fomPath);
        } else {
            logger.warn("Couldn't find FOM directory. Looked in : {}", fomPath);
        }

        this.loadTestCasefiles(testcaseDirFile);
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

    public String getFom() {
        return fomPath;
    }

    public List<URL> getTestcaseList() {
        return testcaseList;
    }

}

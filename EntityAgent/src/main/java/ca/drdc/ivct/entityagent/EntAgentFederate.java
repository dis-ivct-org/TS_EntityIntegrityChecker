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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntAgentFederate {

    private static Logger logger = LoggerFactory.getLogger(EntAgentFederate.class);

    public static final boolean DEBUG = false;

    private Controller controller;

    public void run() {
        
        this.controller = new Controller();
        EntityAgentConfig noConfig = null;
        try {
            controller.execute(noConfig);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        System.out.println("Press Q and Enter to exit");
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                String in = inputReader.readLine();
                if (in.equalsIgnoreCase("q")) {
                    stop();
                    break;
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public void stop() {
        controller.stop();
    }

    public static void main(String[] args) {
        new EntAgentFederate().run();
    }
}

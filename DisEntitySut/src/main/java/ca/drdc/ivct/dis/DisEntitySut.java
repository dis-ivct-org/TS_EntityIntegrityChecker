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
package ca.drdc.ivct.dis;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

import ca.drdc.ivct.fom.utils.WeaponFireCSVReader;
import ca.drdc.ivct.fom.warfare.WeaponFire;
import edu.nps.moves.dis.EntityStatePdu;
import edu.nps.moves.dis.FirePdu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.drdc.ivct.dis.config.DisSutConfig;
import ca.drdc.ivct.fom.base.BaseEntity;
import ca.drdc.ivct.fom.utils.BaseEntityCSVReader;
import de.fraunhofer.iosb.tc_lib.converter.DisModelConverter;
import de.fraunhofer.iosb.tc_lib.dis.DisSender;
import static java.util.concurrent.TimeUnit.SECONDS;

public class DisEntitySut {

    private static final int PERIOD = 6;
    private static Logger logger = LoggerFactory.getLogger(DisEntitySut.class);

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    public static void main(String[] args) {
        DisSutConfig config = new DisSutConfig("/config/config.properties");
        try {
            new DisEntitySut(config);
        } catch (IOException | ParseException e) {
            logger.error("Could not load the testcase");
        }
    }

    public DisEntitySut(DisSutConfig config) throws IOException, ParseException {
        DisSender sender = new DisSender(config.getBroadCastNetwork().getIpAdress(),
                config.getBroadCastNetwork().getPort());

        List<EntityStatePdu> pdus = BaseEntityCSVReader.loadCSVFileToBaseEntityList(config.getTestcaseList()).stream()
                .map(DisModelConverter::rprEntityToDis)
                .collect(Collectors.toList());

        scheduler.scheduleAtFixedRate(()->
            pdus.stream().forEach(sender::sendPdu),
        0, PERIOD, SECONDS);
        
    }
}

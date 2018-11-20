/*
* Copyright (C) Her Majesty the Queen in Right of Canada, as represented by the Minister of National Defence, 2017, 2018
*
* Contract:  W7707-145677/001/HAL
*            Call-up 16 
* Author:    OODA Technologies Inc.
* Version:   1.0
* Date:      March 31, 2017
*
*/
 
package ca.drdc.ivct.entityagent.hlamodule.rpr;

import java.util.ArrayList;

import ca.drdc.ivct.entityagent.hlamodule.ClassHandleLoader;
import hla.rti1516e.RTIambassador;

public class RprPlatformPackage extends ClassHandleLoader{
    
    public RprPlatformPackage(RTIambassador ambassador){
        this.ambassador = ambassador;
        this.classNames = new ArrayList<String>();

        for(RprClass rprClass : RprClass.values()){
            this.classNames.add(rprClass.getName());
        }
    }
}
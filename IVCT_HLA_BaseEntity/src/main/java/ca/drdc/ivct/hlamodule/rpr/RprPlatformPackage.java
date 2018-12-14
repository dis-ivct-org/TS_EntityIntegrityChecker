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
 
package ca.drdc.ivct.hlamodule.rpr;

import java.util.ArrayList;

import ca.drdc.ivct.hlamodule.ClassHandleLoader;
import hla.rti1516e.RTIambassador;

public class RprPlatformPackage extends ClassHandleLoader {
    
    public RprPlatformPackage(RTIambassador ambassador){
        this.ambassador = ambassador;
        this.classNames = new ArrayList<String>();

        for(RprClass rprClass : RprClass.values()){
            this.classNames.add(rprClass.getName());
        }
    }
}
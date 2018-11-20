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


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.NameNotFound;
import hla.rti1516e.exceptions.NotConnected;
import hla.rti1516e.exceptions.RTIinternalError;

/**
 * An abstract class containing a list of class names to get ObjectClassHandle on. The fetchClassHandles() method
 * gets all available class handles in the classHandles variable.
 * The specific content of the list is defined by implementing classes.
 */
public abstract class ClassHandleLoader{
    protected RTIambassador ambassador;
    protected List<String> classNames;
    protected Map<String, ObjectClassHandle> classHandles;

    /**
     * Fetches the class handles specified in classNames and stores the results in the local instance.
     * @throws NameNotFound if the name is not found.
     * @throws FederateNotExecutionMember if the federate is not an executionMember
     * @throws NotConnected if the federate is not connected to a CRC
     * @throws RTIinternalError if the RTI fail unexpectedly
     */
    private void fetchClassHandles() throws NameNotFound, FederateNotExecutionMember, NotConnected, RTIinternalError{
        classHandles = new HashMap<String, ObjectClassHandle>();
        
        for(String className : classNames){
            classHandles.put(className, this.ambassador.getObjectClassHandle(className));
        }
    }
    
    /**
     * Fetches the class handles specified in classNames and returns the handles.
     * @return A map of className, ObjectClassHandle. 
     * @throws NameNotFound if the name is not found.
     * @throws FederateNotExecutionMember if the federate is not an executionMember
     * @throws NotConnected if the federate is not connected to a CRC
     * @throws RTIinternalError if the RTI fail unexpectedly
     */
    public Map<String, ObjectClassHandle> getClassHandles() throws NameNotFound, FederateNotExecutionMember, NotConnected, RTIinternalError{
        if(classHandles == null){
            fetchClassHandles();
        }
        return this.classHandles;
    }
    
    /**
     * @return The list of class names defined in the constructor.
     */
    public List<String> getClassNames(){
        return this.classNames;
    }
}
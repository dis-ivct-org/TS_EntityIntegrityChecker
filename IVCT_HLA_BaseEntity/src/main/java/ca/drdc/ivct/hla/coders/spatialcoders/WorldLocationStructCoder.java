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
package ca.drdc.ivct.hla.coders.spatialcoders;

import ca.drdc.ivct.baseentity.spatial.element.WorldLocationStruct;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAfixedRecord;
import hla.rti1516e.encoding.HLAfloat64BE;

public class WorldLocationStructCoder {

    private final HLAfixedRecord _recCoder;

    private final HLAfloat64BE _XCoder;
    private final HLAfloat64BE _YCoder;
    private final HLAfloat64BE _ZCoder;

    public WorldLocationStructCoder(EncoderFactory encoderFactory) {
        
        _recCoder = encoderFactory.createHLAfixedRecord();
        
        _XCoder = encoderFactory.createHLAfloat64BE();
        _YCoder = encoderFactory.createHLAfloat64BE();
        _ZCoder = encoderFactory.createHLAfloat64BE();
        
        _recCoder.add(_XCoder);
        _recCoder.add(_YCoder);
        _recCoder.add(_ZCoder);

    }
    
    public HLAfixedRecord getHLAfixedRecord() {
        return this._recCoder;
    }

    public byte[] encode(WorldLocationStruct worldLocation) {
        
        if ((worldLocation) == null) {
            return null;
        }

        _XCoder.setValue(worldLocation.getxPosition());
        _YCoder.setValue(worldLocation.getyPosition());
        _ZCoder.setValue(worldLocation.getzPosition());
        return _recCoder.toByteArray();
    }
    
    /**
     * It is necessary for the bytes to be set beforehand through the
     * spatialFPCoder/spatialRVCoder decode function.
     * 
     * @return WorldLocationStruct the world location.
     */
    public WorldLocationStruct getWorldLocationStructValue() {
        return new WorldLocationStruct(_XCoder.getValue(), _YCoder.getValue(), _ZCoder.getValue());
    }
    
    public WorldLocationStruct decodeToWorldLocStruct(byte[] worldLocBytes) throws DecoderException {
        _recCoder.decode(worldLocBytes);
        return new WorldLocationStruct(_XCoder.getValue(), _YCoder.getValue(), _ZCoder.getValue());
    }
    
}

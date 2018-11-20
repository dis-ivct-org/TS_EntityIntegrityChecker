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
package ca.drdc.ivct.tc_lib_integritycheck.baseentity.coders.spatialcoders;

import ca.drdc.ivct.tc_lib_integritycheck.baseentity.spatial.element.VelocityVectorStruct;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAfixedRecord;
import hla.rti1516e.encoding.HLAfloat32BE;

public class VelocityStructCoder {

    private final HLAfixedRecord _recCoder;

    private final HLAfloat32BE _VxCoder;
    private final HLAfloat32BE _VyCoder;
    private final HLAfloat32BE _VzCoder;

    public VelocityStructCoder(EncoderFactory encoderFactory) {

        _recCoder = encoderFactory.createHLAfixedRecord();

        _VxCoder = encoderFactory.createHLAfloat32BE();
        _VyCoder = encoderFactory.createHLAfloat32BE();
        _VzCoder = encoderFactory.createHLAfloat32BE();

        _recCoder.add(_VxCoder);
        _recCoder.add(_VyCoder);
        _recCoder.add(_VzCoder);
    }

    public HLAfixedRecord getHLAfixedRecord() {
        return this._recCoder;
    }

    public byte[] encode(VelocityVectorStruct velocity) {

        if ((velocity) == null) {
            return null;
        }

        _VxCoder.setValue(velocity.getxVelocity());
        _VyCoder.setValue(velocity.getyVelocity());
        _VzCoder.setValue(velocity.getzVelocity());
        return _recCoder.toByteArray();
    }

    /**
     * It is necessary for the bytes to be set beforehand through the
     * spatialFPCoder/spatialRVCoder decode function.
     * 
     * @return VelocityVectorStruct
     */
    public VelocityVectorStruct getAccelerationStructValue() {
        return new VelocityVectorStruct(_VxCoder.getValue(), _VyCoder.getValue(), _VzCoder.getValue());
    }

    public VelocityVectorStruct decodeToVelocityVectorStruct(byte[] velocityBytes) throws DecoderException {
        _recCoder.decode(velocityBytes);
        return new VelocityVectorStruct(_VxCoder.getValue(), _VyCoder.getValue(), _VzCoder.getValue());
    }
}

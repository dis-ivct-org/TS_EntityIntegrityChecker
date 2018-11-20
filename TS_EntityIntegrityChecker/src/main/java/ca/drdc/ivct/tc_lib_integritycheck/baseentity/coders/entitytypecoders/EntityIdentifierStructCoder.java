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

package ca.drdc.ivct.tc_lib_integritycheck.baseentity.coders.entitytypecoders;

import java.util.StringTokenizer;

import ca.drdc.ivct.tc_lib_integritycheck.baseentity.coders.UnsignedDecoder;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAfixedRecord;
import hla.rti1516e.encoding.HLAinteger16BE;

public class EntityIdentifierStructCoder {

    private final HLAfixedRecord recCoder;
    private final HLAinteger16BE entNumberCoder;
    private final FederateIdentifierStructCoder federateIdentifierStructCoder;

    public EntityIdentifierStructCoder(EncoderFactory encoderFactory) {
        recCoder = encoderFactory.createHLAfixedRecord();
        federateIdentifierStructCoder = new FederateIdentifierStructCoder(encoderFactory);
        HLAfixedRecord federateStructCoder = federateIdentifierStructCoder.getHLAfixedRecord();
        recCoder.add(federateStructCoder);
        entNumberCoder = encoderFactory.createHLAinteger16BE();
        recCoder.add(entNumberCoder);
    }

    public byte[] encode(String entIdDotStr) {
        String[] entIdFieldStrArr = new String[3];
        if (entIdDotStr == null || entIdDotStr.indexOf('.') < 0) {
            return new byte[0];
        }
        StringTokenizer stk = new StringTokenizer(entIdDotStr, ".");
        if (stk.hasMoreTokens())
            entIdFieldStrArr[0] = stk.nextToken();
        if (stk.hasMoreTokens())
            entIdFieldStrArr[1] = stk.nextToken();
        if (stk.hasMoreTokens())
            entIdFieldStrArr[2] = stk.nextToken();

        federateIdentifierStructCoder.setValues(entIdDotStr);
        entNumberCoder.setValue((short) (Integer.parseInt(entIdFieldStrArr[2])));

        return recCoder.toByteArray();
    }

    public String decodeToIdDotString(byte[] bytes) throws DecoderException {
        recCoder.decode(bytes);

        String resultDotStr = federateIdentifierStructCoder.decodeFederateIdToDotString(bytes);
        resultDotStr += ("." + Integer.toString(UnsignedDecoder.unsignedToSignedSixteeen(entNumberCoder.getValue())));

        return resultDotStr;
    }
}

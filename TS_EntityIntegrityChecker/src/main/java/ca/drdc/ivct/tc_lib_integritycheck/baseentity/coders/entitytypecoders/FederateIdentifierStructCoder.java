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

public class FederateIdentifierStructCoder {

    private final HLAfixedRecord recCoder;

    private final HLAinteger16BE siteIdCoder;
    private final HLAinteger16BE appIdCoder;

    public FederateIdentifierStructCoder(EncoderFactory encoderFactory) {
        recCoder = encoderFactory.createHLAfixedRecord();

        siteIdCoder = encoderFactory.createHLAinteger16BE();
        recCoder.add(siteIdCoder);
        appIdCoder = encoderFactory.createHLAinteger16BE();
        recCoder.add(appIdCoder);
    }

    public String decodeFederateIdToDotString(byte[] bytes) throws DecoderException {
        String resuDotStr = Integer.toString(UnsignedDecoder.unsignedToSignedSixteeen(siteIdCoder.getValue()))
        		+ ("." + Integer.toString(UnsignedDecoder.unsignedToSignedSixteeen(appIdCoder.getValue())));

        return resuDotStr;
    }

    public HLAfixedRecord getHLAfixedRecord() {
        return this.recCoder;
    }

    byte[] encode(String federateIdDotStr) {
        setValues(federateIdDotStr);
        return recCoder.toByteArray();
    }

    public void setValues(String federateIdDotStr) {
        String[] federateIdFieldStrArr = new String[3];
        if (federateIdDotStr == null || federateIdDotStr.indexOf('.') < 0) {
            return;
        }

        StringTokenizer stk = new StringTokenizer(federateIdDotStr, ".");
        if (stk.hasMoreTokens())
            federateIdFieldStrArr[0] = stk.nextToken();
        if (stk.hasMoreTokens())
            federateIdFieldStrArr[1] = stk.nextToken();

        siteIdCoder.setValue((short) (Integer.parseInt(federateIdFieldStrArr[0])));
        appIdCoder.setValue((short) (Integer.parseInt(federateIdFieldStrArr[1])));
    }
}

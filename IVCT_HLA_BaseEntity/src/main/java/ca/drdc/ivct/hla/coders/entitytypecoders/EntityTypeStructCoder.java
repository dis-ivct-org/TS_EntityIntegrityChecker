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
package ca.drdc.ivct.hla.coders.entitytypecoders;

import java.util.StringTokenizer;

import ca.drdc.ivct.baseentity.EntityType;
import ca.drdc.ivct.hla.coders.UnsignedDecoder;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAfixedRecord;
import hla.rti1516e.encoding.HLAinteger16BE;
import hla.rti1516e.encoding.HLAoctet;

public class EntityTypeStructCoder {
    private HLAfixedRecord fixdRec;

    private HLAoctet entityKind;
    private HLAoctet domain;
    private HLAinteger16BE countryCode;
    private HLAoctet category;
    private HLAoctet subCategory;
    private HLAoctet specific;
    private HLAoctet extra;

    public EntityTypeStructCoder(EncoderFactory encoderFactory) {
        entityKind = encoderFactory.createHLAoctet();
        domain = encoderFactory.createHLAoctet();
        countryCode = encoderFactory.createHLAinteger16BE();
        category = encoderFactory.createHLAoctet();
        subCategory = encoderFactory.createHLAoctet();
        specific = encoderFactory.createHLAoctet();
        extra = encoderFactory.createHLAoctet();

        fixdRec = encoderFactory.createHLAfixedRecord();
        fixdRec.add(entityKind);
        fixdRec.add(domain);
        fixdRec.add(countryCode);
        fixdRec.add(category);
        fixdRec.add(subCategory);
        fixdRec.add(specific);
        fixdRec.add(extra);
    }

    private void initializeAttributes() {
        Byte unusedVal = (byte) 0;

        entityKind.setValue(unusedVal);
        domain.setValue(unusedVal);
        countryCode.setValue((short) 0);
        category.setValue(unusedVal);
        subCategory.setValue(unusedVal);
        specific.setValue(unusedVal);
        extra.setValue(unusedVal);
    }

    /**
     * Sets Byte value from a String on an HLAoctet object. The only reason this
     * is used is to handle passed null values by not throwing an error and not
     * replacing the previous value.
     * 
     * @param de
     *            The attribute (which extends HLAoctet) to set the value on.
     * @param value
     *            The value to set.
     */
    private <T extends HLAoctet> void setByte(T de, String value) {
        if (value == null || value.isEmpty())
            return;

        de.setValue(Byte.parseByte(value));
    }

    /**
     * Sets Short value String on an HLAinteger16BE object. The only reason this
     * is used is to handle passed null values by not throwing an error
     * 
     * @param s
     *            The attribute (which extends HLAinteger16BE) to set the value
     *            on.
     * @param value
     *            The value to set.
     */
    private <T extends HLAinteger16BE> void setShort(T s, String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        s.setValue(Short.parseShort(value));
    }

    public byte[] encode(String entTypeDotStr) {
        if (entTypeDotStr == null || entTypeDotStr.indexOf('.') < 0) {
            throw new IllegalArgumentException();
        }

        initializeAttributes();

        StringTokenizer stk = new StringTokenizer(entTypeDotStr, ".");
        String[] splitString = new String[7];

        int idx = 0;
        while (stk.hasMoreElements()) {
            splitString[idx] = stk.nextToken();
            idx++;
        }

        setByte(entityKind, splitString[0]);
        setByte(domain, splitString[1]);
        setShort(countryCode, splitString[2]);
        setByte(category, splitString[3]);
        setByte(subCategory, splitString[4]);
        setByte(specific, splitString[5]);
        setByte(extra, splitString[6]);

        return fixdRec.toByteArray();
    }
    
    public byte[] encode(EntityType entType) {
        if (entType == null ) {
            throw new IllegalArgumentException();
        }

        initializeAttributes();

        StringTokenizer stk = new StringTokenizer(entType.toString(), ".");
        String[] splitString = new String[7];

        int idx = 0;
        while (stk.hasMoreElements()) {
            splitString[idx] = stk.nextToken();
            idx++;
        }

        setByte(entityKind, splitString[0]);
        setByte(domain, splitString[1]);
        setShort(countryCode, splitString[2]);
        setByte(category, splitString[3]);
        setByte(subCategory, splitString[4]);
        setByte(specific, splitString[5]);
        setByte(extra, splitString[6]);

        return fixdRec.toByteArray();
    }

    public EntityType decodeToType(byte[] bytes) throws DecoderException {
        fixdRec.decode(bytes);
        return new EntityType(entityKind.getValue(),domain.getValue(),(short) UnsignedDecoder.unsignedToSignedSixteeen(countryCode.getValue()), category.getValue(), subCategory.getValue(), specific.getValue(), extra.getValue());
    }
    
    public String decodeToDotString(byte[] bytes) throws DecoderException {
        fixdRec.decode(bytes);

        return entityKind.getValue() + "." + domain.getValue()
                + "."
                + UnsignedDecoder
                        .unsignedToSignedSixteeen(countryCode.getValue())
                + "." + category.getValue() + "." + subCategory.getValue()
                + "." + specific.getValue() + "." + extra.getValue();

    }

    public void setValues(String entTypeDotStr) {
        if (entTypeDotStr == null || entTypeDotStr.indexOf('.') < 0) {
            throw new IllegalArgumentException();
        }

        StringTokenizer stk = new StringTokenizer(entTypeDotStr, ".");
        if (stk.hasMoreTokens())
            entityKind.setValue(Byte.parseByte(stk.nextToken()));
        if (stk.hasMoreTokens())
            domain.setValue(Byte.parseByte(stk.nextToken()));
        if (stk.hasMoreTokens())
            countryCode.setValue((short) Integer.parseInt(stk.nextToken()));
        if (stk.hasMoreTokens())
            category.setValue(Byte.parseByte(stk.nextToken()));
        if (stk.hasMoreTokens())
            subCategory.setValue(Byte.parseByte(stk.nextToken()));
        if (stk.hasMoreTokens())
            specific.setValue(Byte.parseByte(stk.nextToken()));
        if (stk.hasMoreTokens())
            extra.setValue(Byte.parseByte(stk.nextToken()));
    }

    public HLAfixedRecord getHLAfixedRecord() {
        return this.fixdRec;
    }
}

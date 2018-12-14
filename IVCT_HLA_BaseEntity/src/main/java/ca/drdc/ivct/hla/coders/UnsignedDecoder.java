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
package ca.drdc.ivct.hla.coders;

public class UnsignedDecoder {

    private UnsignedDecoder() {
    }

    /**
     * @param unsigned short
     * @return a signed int of an unsigned short
     */
    public static int unsignedToSignedSixteeen(short unsigned) {
        if (unsigned > 0)
            return (int) unsigned;
        else {
            int signed;
            signed = (int) (unsigned + Math.pow(2, 16));

            return signed;
        }
    }

    /**
     * 
     * @param unsigned int
     * @return a signed long of an int
     */
    public static long unsignedToSignedThirtyTwo(int unsigned) {
        if (unsigned > 0)
            return (long) unsigned;
        else {
            long signed;
            signed = (long) (unsigned + Math.pow(2, 32));
            return signed;
        }
    }

    /**
     * Switch the signed16 int to an unsigned short by only taking the necessary
     * number of bits.
     * 
     * Only the first 16bits will be used
     * 
     * @param signed int 
     * @return an unsigned short encoded in a short
     */
    public static short signedToUnsign16(int signed) {
        return (short) (signed - Math.pow(2, 16));
    }

}

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
package ca.drdc.ivct.tc_lib_integritycheck.baseentity.spatial.element;

import java.util.StringTokenizer;

import org.apache.commons.math.util.MathUtils;

/**
 * The rate at which the orientation is changing over time, in body coordinates.
 * 
 * @author mlavallee
 */
public class AngularVelocityVectorStruct {

    // Threshold value for validation in radians/s
    private static final float THRESHOLD = (float) 0.0001; // 10^-4 radians/s

    /**
     *Magnitude of the change in rotation around the X axis. in rad/s
     */
    private float xAngularVelocity;

    /**
     *Magnitude of the change in rotation around the Y axis. in rad/s
     */
    private float yAngularVelocity;

    /**
     *Magnitude of the change in rotation around the Z axis. in rad/s
     */
    private float zAngularVelocity;

    public AngularVelocityVectorStruct(float xAngularVelocity, float yAngularVelocity, float zAngularVelocity) {
        this.xAngularVelocity = xAngularVelocity;
        this.yAngularVelocity = yAngularVelocity;
        this.zAngularVelocity = zAngularVelocity;
    }

    /**
     * Constructor from String with xyz separated by ";" 
     * @param angVelocityStr String semi-colon separated value
     */
    public AngularVelocityVectorStruct(String angVelocityStr) {
        StringTokenizer token = new StringTokenizer(angVelocityStr, ";");
        
        if (token.hasMoreTokens()) {
            this.xAngularVelocity = Float.parseFloat(token.nextToken());
        }
        
        if (token.hasMoreTokens()) {
            this.yAngularVelocity = Float.parseFloat(token.nextToken());
        }
        
        if (token.hasMoreTokens()) {
            this.zAngularVelocity = Float.parseFloat(token.nextToken());
        }
    }

    public float getxAngularVelocity() {
        return xAngularVelocity;
    }

    public void setxAngularVelocity(float xAngularVelocity) {
        this.xAngularVelocity = xAngularVelocity;
    }

    public float getyAngularVelocity() {
        return yAngularVelocity;
    }

    public void setyAngularVelocity(float yAngularVelocity) {
        this.yAngularVelocity = yAngularVelocity;
    }

    public float getzAngularVelocity() {
        return zAngularVelocity;
    }

    public void setzAngularVelocity(float zAngularVelocity) {
        this.zAngularVelocity = zAngularVelocity;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(xAngularVelocity);
        result = prime * result + Float.floatToIntBits(yAngularVelocity);
        result = prime * result + Float.floatToIntBits(zAngularVelocity);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AngularVelocityVectorStruct other = (AngularVelocityVectorStruct) obj;
        if (!MathUtils.equals(this.xAngularVelocity, other.xAngularVelocity, THRESHOLD))
            return false;
        if (!MathUtils.equals(this.yAngularVelocity, other.yAngularVelocity, THRESHOLD))
            return false;
        if (!MathUtils.equals(this.zAngularVelocity, other.zAngularVelocity, THRESHOLD))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "AngularVelocityVectorStruct [x=" + xAngularVelocity + ", y="
                + yAngularVelocity + ", z=" + zAngularVelocity + "]";
    }

}

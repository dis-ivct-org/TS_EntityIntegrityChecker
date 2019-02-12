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
package ca.drdc.ivct.baseentity.spatial.element;

import java.util.StringTokenizer;

/**
 * The rate at which an object's position is changing over time.
 * 
 * @author mlavallee
 */
public class VelocityVectorStruct {

    /**
     * Velocity component along the X axis. (Meter per second)
     */
    private float xVelocity;
    /**
     * Velocity component along the Y axis. (Meter per second)
     */
    private float yVelocity;
    /**
     * Velocity component along the Z axis. (Meter per second)
     */
    private float zVelocity;

    public VelocityVectorStruct(float xVelocity, float yVelocity, float zVelocity) {
        this.xVelocity = xVelocity;
        this.yVelocity = yVelocity;
        this.zVelocity = zVelocity;
    }

    /**
     * Constructor from String with xyz separated by ";" 
     * @param velocityStr String semi-colon separated value
     */
    public VelocityVectorStruct(String velocityStr) {
        StringTokenizer token = new StringTokenizer(velocityStr, ";");

        if (token.hasMoreTokens()) {
            this.xVelocity = Float.parseFloat(token.nextToken());
        }

        if (token.hasMoreTokens()) {
            this.yVelocity = Float.parseFloat(token.nextToken());
        }

        if (token.hasMoreTokens()) {
            this.zVelocity = Float.parseFloat(token.nextToken());
        }

    }

    public float getxVelocity() {
        return xVelocity;
    }

    public void setxVelocity(float xVelocity) {
        this.xVelocity = xVelocity;
    }

    public float getyVelocity() {
        return yVelocity;
    }

    public void setyVelocity(float yVelocity) {
        this.yVelocity = yVelocity;
    }

    public float getzVelocity() {
        return zVelocity;
    }

    public void setzVelocity(float zVelocity) {
        this.zVelocity = zVelocity;
    }

    @Override
    public String toString() {
        return "VelocityVectorStruct [x=" + xVelocity + ", y=" + yVelocity + ", z=" + zVelocity + "]";
    }
}

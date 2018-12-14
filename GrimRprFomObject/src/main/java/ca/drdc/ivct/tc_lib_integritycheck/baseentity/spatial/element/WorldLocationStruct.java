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

/**
 * From the HLA firedRecord Position is in meter and are represented by a
 * float64(double)
 * 
 * @author mlavallee
 */
public class WorldLocationStruct {

    // Threshold value for validation in meter
    private static final double THRESHOLD = 0.001; // 1 mm

    /**
     * Distance from the origin along the X axis.
     */
    private double xPosition;
    /**
     * Distance from the origin along the Y axis.
     */
    private double yPosition;
    /**
     * Distance from the origin along the Z axis.
     */
    private double zPosition;

    public WorldLocationStruct(double xPosition, double yPosition, double zPosition) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.zPosition = zPosition;
    }

    /**
     * Constructor from String with xyz separated by ";" 
     * @param worldLocStr String semi-colon separated value
     */
    public WorldLocationStruct(String worldLocStr) {
        StringTokenizer token = new StringTokenizer(worldLocStr, ";");
        
        if (token.hasMoreTokens()) {
            this.xPosition = Double.parseDouble(token.nextToken());
        }
        
        if (token.hasMoreTokens()) {
            this.yPosition = Double.parseDouble(token.nextToken());
        }
        
        if (token.hasMoreTokens()) {
            this.zPosition = Double.parseDouble(token.nextToken());
        }
        
    }

    public double getxPosition() {
        return xPosition;
    }

    public void setxPosition(double xPosition) {
        this.xPosition = xPosition;
    }

    public double getyPosition() {
        return yPosition;
    }

    public void setyPosition(double yPosition) {
        this.yPosition = yPosition;
    }

    public double getzPosition() {
        return zPosition;
    }

    public void setzPosition(double zPosition) {
        this.zPosition = zPosition;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(xPosition);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(yPosition);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(zPosition);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        WorldLocationStruct other = (WorldLocationStruct) obj;
        if (Math.abs(this.xPosition - other.xPosition) >= THRESHOLD)
            return false;
        if (Math.abs(this.yPosition - other.yPosition) >= THRESHOLD)
            return false;
        if (Math.abs(this.zPosition - other.zPosition) >= THRESHOLD)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "WorldLocationStruct [x=" + xPosition + ", y=" + yPosition + ", z=" + zPosition + "]";
    }
}

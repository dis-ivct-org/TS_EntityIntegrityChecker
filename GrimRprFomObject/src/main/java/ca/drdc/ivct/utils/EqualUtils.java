package ca.drdc.ivct.utils;

import java.util.Map;

import ca.drdc.ivct.baseentity.BaseEntity;
import ca.drdc.ivct.baseentity.EntityIdentifier;
import ca.drdc.ivct.baseentity.EntityType;
import ca.drdc.ivct.baseentity.spatial.SpatialRVStruct;
import ca.drdc.ivct.baseentity.spatial.SpatialRepresentation;
import ca.drdc.ivct.baseentity.spatial.element.AccelerationVectorStruct;
import ca.drdc.ivct.baseentity.spatial.element.AngularVelocityVectorStruct;
import ca.drdc.ivct.baseentity.spatial.element.DeadReckoningAlgorithm;
import ca.drdc.ivct.baseentity.spatial.element.OrientationStruct;
import ca.drdc.ivct.baseentity.spatial.element.VelocityVectorStruct;
import ca.drdc.ivct.baseentity.spatial.element.WorldLocationStruct;

public class EqualUtils {
    // in meter
    private double worldLocationThreshold = 0.001;
    // in radian
    private double orientationThreshold = 0.0001;
    // in meter/second
    private double velocityThreshold = 0.001;
    // in meter/second^2
    private double accelerationThreshold = 0.001;
    //in rad/second
    private double angularVelocityThreshold =0.0001;

    /**
     * Set the threshold value for the spatial comparison, if the value is null, set the default value
     * @param worldLocationThreshold default value is 0.001 meter
     * @param orientationThreshold default value is 0.0001 radian
     * @param velocityThreshold default value is 0.001 meter/second
     * @param accelerationThreshold default value is 0.001 meter/second^2
     * @param angularVelocityThreshold default value is 0.0001 rad/second
     */
    public EqualUtils(Double worldLocationThreshold, Double orientationThreshold, Double velocityThreshold,
            Double accelerationThreshold, Double angularVelocityThreshold) {

        // set the threshold value, if the value is null, set the default value
        this.worldLocationThreshold = (worldLocationThreshold != null)?worldLocationThreshold:this.worldLocationThreshold;
        this.orientationThreshold = (orientationThreshold != null)?orientationThreshold:this.orientationThreshold;
        this.velocityThreshold = (velocityThreshold != null)?velocityThreshold:this.velocityThreshold;
        this.accelerationThreshold = (accelerationThreshold != null)?accelerationThreshold:this.accelerationThreshold;
        this.angularVelocityThreshold = (angularVelocityThreshold != null)?angularVelocityThreshold:this.angularVelocityThreshold;
    } 

    /**
     * Set the threshold value for the spatial comparison, if the value is null, set the default value
     * @param thresholds containing value for:
     *  worldLocation default value is 0.001 meter
     *  orientation default value is 0.0001 radian
     *  velocity default value is 0.001 meter/second
     *  acceleration default value is 0.001 meter/second^2
     *  angularVelocity default value is 0.0001 rad/second
     */
    public EqualUtils(Map<String, Double> thresholds) {

        // set the threshold value, if the value is null, set the default value
        this.worldLocationThreshold = (thresholds.get("worldLocation") != null)?thresholds.get("worldLocation"):this.worldLocationThreshold;
        this.orientationThreshold = (thresholds.get("orientation") != null)?thresholds.get("orientation"):this.orientationThreshold;
        this.velocityThreshold = (thresholds.get("velocity") != null)?thresholds.get("velocity"):this.velocityThreshold;
        this.accelerationThreshold = (thresholds.get("acceleration") != null)?thresholds.get("acceleration"):this.accelerationThreshold;
        this.angularVelocityThreshold = (thresholds.get("angularVelocity") != null)?thresholds.get("angularVelocity"):this.angularVelocityThreshold;
    } 

    /**
     * Use the default Threshold
     */
    public EqualUtils() {
    } 

    public boolean baseEntityIEqual(BaseEntity first, BaseEntity second ){
        return (baseEntityIdentifierEqual(first.getEntityIdentifier(), second.getEntityIdentifier()) && 
                baseEntityTypeEqual(first.getEntityType(), second.getEntityType()) && 
                baseEntitySpatialEqual(first.getSpatialRepresentation(), second.getSpatialRepresentation()));
    }

    public boolean baseEntityIdentifierEqual(EntityIdentifier firstEntityIdentifier, EntityIdentifier secondEntityIdentifier) {
        return firstEntityIdentifier.equals(secondEntityIdentifier);
    }

    public boolean baseEntityTypeEqual(EntityType firstEntityType, EntityType secondEntityType) {
        return firstEntityType.equals(secondEntityType);
    }

    public boolean baseEntitySpatialEqual(SpatialRepresentation first, SpatialRepresentation second) {

        if (!(first.getDeadReckoningAlgorithm().equals(second.getDeadReckoningAlgorithm()))) {
            // not the same DRA
            return false;
        }
        DeadReckoningAlgorithm commonDRA = first.getDeadReckoningAlgorithm();
        if ((commonDRA == DeadReckoningAlgorithm.DRM_FPW || commonDRA == DeadReckoningAlgorithm.DRM_FPB ||
                commonDRA == DeadReckoningAlgorithm.DRM_RVW || commonDRA == DeadReckoningAlgorithm.DRM_RVB )) {

            OrientationStruct firstOrientation = first.getOrientation();
            OrientationStruct secondOrientation = second.getOrientation();
            boolean orientationIsSame = (
                    Math.abs(firstOrientation.getPhi() - secondOrientation.getPhi()) <= orientationThreshold &&
                    Math.abs(firstOrientation.getPsi() - secondOrientation.getPsi()) <= orientationThreshold &&
                    Math.abs(firstOrientation.getTheta() - secondOrientation.getTheta()) <= orientationThreshold);

            WorldLocationStruct firstWorldLocation = first.getWorldLocation();
            WorldLocationStruct secondWorldLocation = second.getWorldLocation();
            boolean worldLocationIsSame = (
                    Math.abs(firstWorldLocation.getxPosition() - secondWorldLocation.getxPosition()) <= worldLocationThreshold &&
                    Math.abs(firstWorldLocation.getyPosition() - secondWorldLocation.getyPosition()) <= worldLocationThreshold &&
                    Math.abs(firstWorldLocation.getzPosition() - secondWorldLocation.getzPosition()) <= worldLocationThreshold);

            VelocityVectorStruct firstVelocityVectorStruct = first.getVelocityVector();
            VelocityVectorStruct secondVelocityVectorStruct = second.getVelocityVector();
            boolean velocityVectorIsSame = (
                    Math.abs(firstVelocityVectorStruct.getxVelocity() - secondVelocityVectorStruct.getxVelocity()) <= velocityThreshold &&
                    Math.abs(firstVelocityVectorStruct.getyVelocity() - secondVelocityVectorStruct.getyVelocity()) <= velocityThreshold &&
                    Math.abs(firstVelocityVectorStruct.getzVelocity() - secondVelocityVectorStruct.getzVelocity()) <= velocityThreshold
                    );

            if (!( commonDRA == DeadReckoningAlgorithm.DRM_RVW || commonDRA == DeadReckoningAlgorithm.DRM_RVB)) {
                return (orientationIsSame && worldLocationIsSame && velocityVectorIsSame);
            }

            // has an RV* algorithm, so it need accelerationVector and angularVelocityVector

            SpatialRVStruct firstAsRV = (SpatialRVStruct) first;
            SpatialRVStruct secondAsRV = (SpatialRVStruct) second;

            AccelerationVectorStruct firstAccelerationVectorStruct = firstAsRV.getAccelerationVector();
            AccelerationVectorStruct secondAccelerationVectorStruct = secondAsRV.getAccelerationVector();
            boolean accelerationVectorStructIsSame = (
                    Math.abs(firstAccelerationVectorStruct.getxAcceleration() - secondAccelerationVectorStruct.getxAcceleration()) <= accelerationThreshold &&
                    Math.abs(firstAccelerationVectorStruct.getyAcceleration() - secondAccelerationVectorStruct.getyAcceleration()) <= accelerationThreshold &&
                    Math.abs(firstAccelerationVectorStruct.getzAcceleration() - secondAccelerationVectorStruct.getzAcceleration()) <= accelerationThreshold
                    );
            AngularVelocityVectorStruct firstAngularVelocityVectorStruct = firstAsRV.getAngularVelocityVector();
            AngularVelocityVectorStruct secondAngularVelocityVectorStruct = secondAsRV.getAngularVelocityVector();
            boolean angularVelocityVectorIsSame = (
                    Math.abs(firstAngularVelocityVectorStruct.getxAngularVelocity() - secondAngularVelocityVectorStruct.getxAngularVelocity()) <= angularVelocityThreshold &&
                    Math.abs(firstAngularVelocityVectorStruct.getyAngularVelocity() - secondAngularVelocityVectorStruct.getyAngularVelocity()) <= angularVelocityThreshold &&
                    Math.abs(firstAngularVelocityVectorStruct.getzAngularVelocity() - secondAngularVelocityVectorStruct.getzAngularVelocity()) <= angularVelocityThreshold
                    );

            return (orientationIsSame && worldLocationIsSame && velocityVectorIsSame && accelerationVectorStructIsSame && angularVelocityVectorIsSame);
        }
        else {
            // the DeadReckoningAlgorithm is not yet implemented so can't be validated.
            return false;
        }
    }

}

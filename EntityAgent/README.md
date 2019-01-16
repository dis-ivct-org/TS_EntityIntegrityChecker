# EntityAgent

The EntityAgentis a Simulated SuT that creates BaseEntities from .csv files located in the directory <IVCT_CONF>/IVCTsut/EntityAgent/resources/config/config.properties. These BaseEntities will be published to the federation located at the "localSettingsDesignator" address in the config.properties.

## Behavior
This EntityAgent will publish information on a BaseEntity containing its Identifier, Type and Spatial attributes. When the TS_EntityIntegrityChecker loads the fad (which should be the same as the .csv file from the testcases folder), the test will compare received Entity to the one sent by the SuT grant the "EntityIntegrityCheck" badge if the entities are the same. To get an example of a FAIL, make sure the TS and the Agent refer to different .csv files describing entities that present difference. These differences will be highlighted during the logging process.

### fad
The fad is a csv file containing the code for each element of a baseEntity. Please refer to the GrimRpr Fom to get the meaning of all the code

```csv
entityId,entityType,description,deadReckoningAlgorithm,worldLocation,isFrozen,orientation,velocityVector,accelerationVector,angularVelocity
60000.1618.3875,1.1.225.0.1.1.1,M901 40-mm machine gun,DRM_FPW,0;0;0,TRUE,0;0;0,0;0;0,,
512.1658.375,1.1.225.0.1.2.0,(deprecated),DRM_FPW,1;2;3,TRUE,4;5;6,7;8;9,,
3103.7522.6157,1.1.225.0.1.3.9,Liquid propellant guns,DRM_RVW,1.2;0.2;0.9,TRUE,0.9;0.9;0.9,0.9;0.9;0.9,0.9;0.9;0.9,0.9;0.9;0.9
```
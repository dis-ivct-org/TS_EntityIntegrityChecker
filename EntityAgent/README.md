# EntityAgent

The SiSuT creates BaseEntities from .csv files located in the directory provided under src/main/resources/config/config.properties. These BaseEntities will be published to the federation located at the "localSettingsDesignator" address in the config.properties.

This EntityAgent will publish information on a BaseEntity containing its Identifier, Type and Spatial. When the TS_EntityIntegrityChecker loads the fad (which should be the same as the .csv file from the testcases folder), the comparison will give a PASS and grant the "EntityIntegrityCheck" badge to the SiSuT. To get an example of a FAIL, make sure the TS and the Agent refer to different .csv files describing entities that present difference. These differences will be highlighted during the logging process.

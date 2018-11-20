# TS_EntityIntegrityChecker

The test validates the integrity of Base Entities instances published by the System under Test (SuT). The information validated is based on a shared predefined scenario between both the IVCT_TestRunner and the SuT. The currently in use scenario describes multiple base entities taken from the GRIM-RPR standard which are then loaded through a csv file.

The TestSuite is divided in 3 testcases which will verify the following elements of a BaseEntity instance:
- EntityIdentifier: The testcase verifies that each entity from the FAD finds a match in ID with the discovered entities.
- EntityType: The testcase verifies that each ID-matched entities present the same Type information.
- Spatial: The testcase verifies that each ID-matched entities present the same Spatial information.


## Requirement 
The use of entities from the GRIM-RPR FOM. 
The SuT and the IVCTtest use the same scenario document and are both connected to the same federation. The SuT has the capability to create the entities as requested in the .csv scenario.


## TestCase

The TestCase flow is as follows: 

### Precondition
The SuT needs to connect to the federation and publish every entity in the scenario document. The Simulated System under test does the following steps before the execution of the test: 

### SuT Steps
1. The SuT connects to the federation.
2. The SuT register publishing of the Base Entities and the following attributes: EntityIdentifier, EntityType, Spatial in accordance to the FOM.
3. The SuT reads the scenario document and publish the generated base entities instances listed in the scenario document.

The test flow is  the same for each testCase and is described in the following diagram:

1. Operator starts the TestCase using the GUI or the UI then the TestRunner execute the testcase.
2. TestRunner registers the subscription to read the base Entity and their following attributes: EntityIdentifier, EntityType and Spatial.
3. TestRunner discovers the Entities. The federation returns the BaseEntity Handles.
4. For each Entity in the system the TestRunner requests all its attributes.
5. The SuT provides the requested attributes.
6. TestRunner reads the scenario document and validates that all the entities requested respect the scenario document.
7. Enact judgment.

![IntegrityCheckerFlow](https://github.com/MSG134/TS_EntityIntegrityChecker/raw/master/IntegrityChecker.png "IntegrityCheckerFlow")

Note: 
Only 2 dead reckoning algorithms as been implemented: DRM_FPW and DRM_RVW

------

SiSut : Simulated System Under Test provided as EntityAgent in the repository.  
Sut : System Under Test

------
The folder IVCT_Runtime contains the required configuration to run the testcases using the IVCT test runner.
More information on this subject can be found in the wiki (https://github.com/MSG134/IVCT_Runtime/wiki)

Note: 
Only 2 dead reckoning algorithm as been implemented: DRM (FPW) and DRM(RVW)

How to Build
-------
./gradlew clean  
./gradlew build  
./gradlew eclipse  
./gradlew installDist  

It is then important to copy and paste some files to the IVCT_Runtime folder. The TS_EntityIntegrityChecker folder in the /build/install 
folder (That will appear after the above commands) needs to be dragged into IVCT_Runtime/TestSuites/.

The badge and the SuT information are already present in the IVCT_Runtime





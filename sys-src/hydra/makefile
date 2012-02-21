##### HYDRA KEY FILES
hydra-jar	:= hydra-0.2-SNAPSHOT.jar 

##### KEY FILES/DIRECTORIES
HYD_SDIR	:= src/main/java/org/hydra
HYD_TDIR	:= src/test/java/org/hydra
HCO_SDIR	:= src/main/java/org/hydra/core
HCO_TDIR	:= src/test/java/org/hydra/core
HPS_SDIR	:= src/main/java/org/hydra/persistence
HPS_TDIR	:= src/test/java/org/hydra/persistence
HUI_SDIR	:= src/main/java/org/hydra/ui
HUI_TDIR	:= src/test/java/org/hydra/ui
HUT_SDIR	:= src/main/java/org/hydra/utilities
HUT_TDIR	:= src/test/java/org/hydra/utilities
TOC			:= toc
TEST_MK		:= .test


##### HYDRA SOURCES & TESTS
hsources	:= $(shell find $(SRC_DIR) -name '*.java')
tsources	:= $(shell find $(TEST_DIR) -name '*.java')

#################################################
############## PRIMARY TARGET ###################
all: $(TOC) $(TEST_MK)

$(TOC): $(hsources) $(tsources)
	@echo "##### Updating the Table of Contents"
	@echo "##### Hydra Soures #####" >> $(TOC)
	@find $(HYD_SDIR) -maxdepth 1 -name '*.java' | sort >> $(TOC)
	@echo "##### Hydra Tests #####" >> $(TOC)
	@find $(HYD_TDIR) -maxdepth 1 -name '*.java' | sort >> $(TOC)
	@echo "" >> $(TOC)
	@echo "##### Hydra Core Soures #####" >> $(TOC)
	@find $(HCO_SDIR) -name '*.java' | sort >> $(TOC)
	@echo "##### Hydra Core Tests #####" >> $(TOC)
	@find $(HCO_TDIR) -name '*.java' | sort >> $(TOC)
	@echo "" >> $(TOC)
	@echo "##### Hydra Persistence Soures #####" >> $(TOC)
	@find $(HPS_SDIR) -name '*.java' | sort >> $(TOC)
	@echo "##### Hydra Persistence Tests #####" >> $(TOC)
	@find $(HPS_TDIR) -name '*.java' | sort >> $(TOC)
	@echo "" >> $(TOC)
	@echo "##### Hydra UI Sources #####" >> $(TOC)
	@find $(HUI_SDIR) -name '*.java' | sort >> $(TOC)
	@echo "##### Hydra UI Tests #####" >> $(TOC)
	@find $(HUI_TDIR) -name '*.java' | sort >> $(TOC)
	@echo "" >> $(TOC)
	@echo "##### Hydra Utilities Sources #####" >> $(TOC)
	@find $(HUT_SDIR) -name '*.java' | sort >> $(TOC)
	@echo "##### Hydra Utilities Tests #####" >> $(TOC)
	@find $(HUT_TDIR) -name '*.java' | sort >> $(TOC)
	@echo "" >> $(TOC)
	@echo "##### Other Files #####" >> $(TOC)
	@echo "makefile" >> $(TOC)
	@echo "pom.xml" >> $(TOC)

$(TEST_MK): $(hsrouces) $(tsources)
	mvn test -e -Dmaven.test.skip=false


package:
	mvn package

run: package
	cd target; java -jar $(hydra-jar) --cwd manualTest --initialize

run-gui: package
	cd target; java -jar $(hydra-jar) --cwd manualTest --initialize --gui

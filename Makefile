.PHONY: run

# Windows paths use semicolons, Mac/Linux use colons
WINDOWS_MODULE_PATH = "target/pencarian-soal-osk-informatika-pdf-1.0.0.jar;target/libs"
MAC_MODULE_PATH = "target/pencarian-soal-osk-informatika-pdf-1.0.0.jar:target/libs"

# Base jpackage flags (without module-path and icon)
JPACKAGE_BASE = --input target/ --dest bin/ --name PencariSoalOSK --module ma3052/ma3052.Launcher

# Windows-specific flags
WINDOWS_UUID = be175e8a-06b3-44d9-90a5-4695ba1b3369
WINDOWS_FLAGS = --win-upgrade-uuid "${WINDOWS_UUID}" --win-shortcut --win-menu --win-dir-chooser --win-shortcut-prompt

run:
	mvn clean javafx:run

package:
	mvn clean package

build-msi: package
	jpackage --type msi ${JPACKAGE_BASE} --module-path ${WINDOWS_MODULE_PATH} --icon favicon.ico ${WINDOWS_FLAGS} --win-upgrade-uuid ${UUID}

build-exe: package
	jpackage --type exe ${JPACKAGE_BASE} --module-path ${WINDOWS_MODULE_PATH} --icon favicon.ico ${WINDOWS_FLAGS} --win-upgrade-uuid ${UUID}

build-dmg: package
	jpackage --type dmg ${JPACKAGE_BASE} --module-path ${MAC_MODULE_PATH} --icon favicon.icns

build-pkg: package
	jpackage --type pkg ${JPACKAGE_BASE} --module-path ${MAC_MODULE_PATH} --icon favicon.icns
	  
#!/bin/sh
wget "https://github.com/AppImage/AppImageKit/releases/download/continuous/appimagetool-x86_64.AppImage"
chmod a+x appimagetool-x86_64.AppImage
BASEDIR=$(dirname "$0")
PARENT_DIR="$(dirname "$BASEDIR")"
VAR="./appimagetool-x86_64.AppImage -n ${PARENT_DIR}/build/AppImage/AdvancedPortChecker.AppDir ${PARENT_DIR}/build/AppImage/AdvancedPortChecker-x86_64-$1.AppImage"
eval $VAR
rm appimagetool-x86_64.AppImage

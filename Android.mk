#
# Copyright (C) 2020 Manos Saratsis
#
# This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 3.
#
# This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
#
LOCAL_PATH := $(call my-dir)
KATSUNA_COMMON_PATH := $(ANDROID_BUILD_TOP)/frameworks/KatsunaCommon
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, widgets/src/main)

LOCAL_MANIFEST_FILE := widgets/src/main/AndroidManifest.xml
LOCAL_FULL_LIBS_MANIFEST_FILES := $(KATSUNA_COMMON_PATH)/commons/src/main/AndroidManifest.xml
LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/widgets/src/main/res
LOCAL_RESOURCE_DIR += frameworks/support/v7/appcompat/res
LOCAL_RESOURCE_DIR += frameworks/support/v7/cardview/res
LOCAL_RESOURCE_DIR += frameworks/support/design/res

# Include KatsunaCommon into this app
LOCAL_REQUIRED_MODULES := KatsunaCommon
LOCAL_STATIC_JAVA_LIBRARIES := KatsunaCommon
# Include KatsunaCommon resources
LOCAL_RESOURCE_DIR += frameworks/KatsunaCommon/commons/src/main/res

LOCAL_STATIC_JAVA_LIBRARIES += android-support-v4
LOCAL_STATIC_JAVA_LIBRARIES += android-support-v7-appcompat
LOCAL_STATIC_JAVA_LIBRARIES += android-support-v7-cardview
LOCAL_STATIC_JAVA_LIBRARIES += android-support-design
LOCAL_STATIC_JAVA_LIBRARIES += dashclock
LOCAL_STATIC_JAVA_LIBRARIES += greendao

LOCAL_STATIC_JAVA_AAR_LIBRARIES := roundedimageview
LOCAL_STATIC_JAVA_AAR_LIBRARIES += bottombar
LOCAL_STATIC_JAVA_AAR_LIBRARIES += williamchart

LOCAL_AAPT_INCLUDE_ALL_RESOURCES := true
LOCAL_AAPT_FLAGS := --version-code 1
LOCAL_AAPT_FLAGS += --version-name "1.0"
LOCAL_AAPT_FLAGS += --auto-add-overlay
LOCAL_AAPT_FLAGS += --generate-dependencies
LOCAL_AAPT_FLAGS += --extra-packages com.katsuna.commons
LOCAL_AAPT_FLAGS += --extra-packages android.support.v7.appcompat
LOCAL_AAPT_FLAGS += --extra-packages android.support.v7.cardview
LOCAL_AAPT_FLAGS += --extra-packages android.support.design
LOCAL_AAPT_FLAGS += --extra-packages com.roughike.bottombar
LOCAL_AAPT_FLAGS += --extra-packages com.db.williamchart

LOCAL_PACKAGE_NAME := KatsunaWidgets
LOCAL_CERTIFICATE := platform

LOCAL_PROGUARD_FLAGS := -ignorewarnings -include build/core/proguard_basic_keeps.flags
LOCAL_PROGUARD_ENABLED := nosystem
LOCAL_PROGUARD_FLAG_FILES := widgets/proguard-rules.pro

include $(BUILD_PACKAGE)

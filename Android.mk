LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, app/src/main)

LOCAL_MANIFEST_FILE := app/src/main/AndroidManifest.xml
LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/app/src/main/res
LOCAL_RESOURCE_DIR += frameworks/support/v7/appcompat/res
LOCAL_RESOURCE_DIR += frameworks/support/v7/cardview/res
LOCAL_RESOURCE_DIR += frameworks/support/design/res

LOCAL_STATIC_JAVA_LIBRARIES := android-support-v4
LOCAL_STATIC_JAVA_LIBRARIES += android-support-v7-appcompat
LOCAL_STATIC_JAVA_LIBRARIES += android-support-v7-cardview
LOCAL_STATIC_JAVA_LIBRARIES += android-support-design
LOCAL_STATIC_JAVA_LIBRARIES += dashclock
LOCAL_STATIC_JAVA_LIBRARIES += greendao

LOCAL_STATIC_JAVA_AAR_LIBRARIES := bottombar
LOCAL_STATIC_JAVA_AAR_LIBRARIES += williamchart

LOCAL_AAPT_INCLUDE_ALL_RESOURCES := true
LOCAL_AAPT_FLAGS := --auto-add-overlay
LOCAL_AAPT_FLAGS += --generate-dependencies
LOCAL_AAPT_FLAGS += --extra-packages android.support.v7.appcompat
LOCAL_AAPT_FLAGS += --extra-packages android.support.v7.cardview
LOCAL_AAPT_FLAGS += --extra-packages android.support.design
# This is to include the aar's RESOURCES into this app
# Notice the full packagename
LOCAL_AAPT_FLAGS += --extra-packages com.roughike.bottombar
LOCAL_AAPT_FLAGS += --extra-packages com.db.williamchart

LOCAL_PACKAGE_NAME := KatsunaWidgetCollection
LOCAL_CERTIFICATE := platform
#LOCAL_PROGUARD_FLAG_FILES := app/proguard-rules.pro

LOCAL_PROGUARD_ENABLED := disabled

include $(BUILD_PACKAGE)

include $(CLEAR_VARS)

# Define here, which extra jar/aar this app needs
# These should NOT be included in KatsunaCommon
# These should reside inside aosp/libs of this app
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := dashclock:aosp/libs/dashclock-2.0.0.jar
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += greendao:aosp/libs/greendao-3.2.0.jar
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += williamchart:aosp/libs/williamchart-2.2.aar
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += bottombar:aosp/libs/bottombar-1.4.0.1.aar

include $(BUILD_MULTI_PREBUILT)

include $(call all-makefiles-under,$(LOCAL_PATH))

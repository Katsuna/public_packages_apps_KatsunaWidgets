package com.katsuna.widgets.commons.entities;

import java.util.Objects;

/**
 * Encapsulates all user profile characteristics.
 */

public class UserProfile {

    public ProfileType opticalSizeProfile;
    public ProfileType opticalContrastProfile;
    public ColorProfile colorProfile;
    public ProfileType cognityProfile;
    public ProfileType memoryProfile;
    public boolean isRightHanded;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof UserProfile)) {
            return false;
        }
        UserProfile userProfile = (UserProfile) obj;
        return opticalSizeProfile == userProfile.opticalSizeProfile &&
                opticalContrastProfile == userProfile.opticalContrastProfile &&
                colorProfile == userProfile.colorProfile &&
                cognityProfile == userProfile.cognityProfile &&
                memoryProfile == userProfile.memoryProfile &&
                isRightHanded == userProfile.isRightHanded;
    }

    @Override
    public int hashCode() {
        return Objects.hash(opticalSizeProfile, opticalContrastProfile, colorProfile,
                cognityProfile, memoryProfile, isRightHanded);
    }
}

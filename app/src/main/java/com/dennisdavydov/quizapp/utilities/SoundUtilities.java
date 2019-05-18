package com.dennisdavydov.quizapp.utilities;
// a helper class to prepare paths to audio resources and work with their properties
public class SoundUtilities {
    private String mAssetPath;
    private String mName;
    private Integer mSoundId;

    public SoundUtilities(String assetPath) {
        this.mAssetPath = assetPath;
        String[] components = assetPath.split("/");
        String fileName = components [components.length - 1];
        mName = fileName.replace(".wav","");
    }

    public String getAssetPath() {
        return mAssetPath;
    }

    public String getName() {
        return mName;
    }

    public Integer getSoundId() {
        return mSoundId;
    }

    public void setSoundId(Integer soundId) {
        this.mSoundId = soundId;
    }
}

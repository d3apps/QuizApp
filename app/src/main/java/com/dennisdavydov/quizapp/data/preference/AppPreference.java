package com.dennisdavydov.quizapp.data.preference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dennisdavydov.quizapp.R;
import com.dennisdavydov.quizapp.constants.AppConstants;

public class AppPreference {

    private static Context mContext;

    private static AppPreference mAppPreference = null;
    private SharedPreferences mSharedPreferences, mSettingsPreferences;
    private SharedPreferences.Editor mEditor;

    public static AppPreference getInstance(Context context){
        if (mAppPreference==null){
            mContext = context;
            mAppPreference = new AppPreference();
        }
        return mAppPreference;
    }

    @SuppressLint("CommitPrefEdits")
    private AppPreference(){
        mSharedPreferences = mContext.getSharedPreferences(AppConstants.APP_PREF_NAME,Context.MODE_PRIVATE);
        mSettingsPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mEditor = mSharedPreferences.edit();
    }

    public void setQuizResult (String category, int score){
        String scoreStr = Integer.toString(score);
        mEditor.putString(category,scoreStr);
        mEditor.apply();
    }

    public void setQuizQuestionCount (String category, int count){

        String quetionsCount = Integer.toString(count);
        mEditor.putString(category + AppConstants.QUESTIONS_IN_TEST,quetionsCount);
        mEditor.apply();
    }

    public String getString (String key) {return mSharedPreferences.getString(key,null);}

    public Boolean getBoolean (String key, boolean defaultValue) {
        return mSharedPreferences.getBoolean(key,defaultValue);
    }

    public boolean isNotificationOn() {
        return mSettingsPreferences.getBoolean(AppConstants.PREF_NOTIFICATION,true);
    }

    public String getTextSize() {
        return mSettingsPreferences.getString(AppConstants.PREF_FONT_SIZE,mContext.getString(R.string.middle));
    }

    public void setBoolean(String key, boolean value) {
        mEditor.putBoolean(key, value);
        mEditor.apply();
    }
}

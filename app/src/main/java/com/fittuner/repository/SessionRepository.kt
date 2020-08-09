package com.fittuner.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

const val APP_PREFERENCE_NAME="PREF_FIT_TUNNER"
const val USER_NAME="USER_NAME"
const val USER_WEIGHT="USER_WEIGHT"
const val USER_DATA_UPDATED="USER_DATA_UPDATED"

class SessionRepository @Inject constructor(val preferences: SharedPreferences) {

    fun setUserName(name:String){
        preferences.edit(commit = true) {
            putString(USER_NAME,name)
        }
    }

    fun setUserWeight(weight:Long){
        preferences.edit(commit = true) {
            putLong(USER_WEIGHT,weight)
        }
    }

    fun setProfile(status:Boolean){
        preferences.edit(commit = true) {
            putBoolean(USER_DATA_UPDATED,status)
        }
    }

    fun getUserName()=preferences.getString(USER_NAME,null)

    fun getUserWeight()=preferences.getLong(USER_WEIGHT,0)

    fun isProfileUpdated()=preferences.getBoolean(USER_DATA_UPDATED,false)
}
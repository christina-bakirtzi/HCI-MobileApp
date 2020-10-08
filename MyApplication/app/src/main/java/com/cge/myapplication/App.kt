package com.cge.myapplication

import android.app.Application
import android.content.Context
import android.content.res.Resources

class App : Application() {
    companion object {
        lateinit var instance: Application
        lateinit var resourses: Resources
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        resourses = resources
        context = this!!.applicationContext
    }
}


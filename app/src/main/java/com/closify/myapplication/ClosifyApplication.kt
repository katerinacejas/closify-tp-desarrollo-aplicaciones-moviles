package com.closify.myapplication

import android.app.Application
import com.closify.myapplication.data.repository.GarmentRepository
import com.closify.myapplication.data.repository.OutfitRepository
import com.closify.myapplication.data.repository.UserRepository

class ClosifyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        UserRepository.initialize(this)
        GarmentRepository.initialize(this)
        OutfitRepository.initialize(this)
    }
}

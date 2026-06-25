package com.closify.myapplication

import android.app.Application
import com.closify.myapplication.data.repository.GarmentRepository
import com.closify.myapplication.data.repository.NotificationRepository
import com.closify.myapplication.data.repository.OutfitPostRepository
import com.closify.myapplication.data.repository.OutfitRepository
import com.closify.myapplication.data.repository.ProfileRepository
import com.closify.myapplication.data.repository.SocialRepository
import com.closify.myapplication.data.repository.UserRepository

class ClosifyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        UserRepository.initialize(this)
        GarmentRepository.initialize(this)
        NotificationRepository.initialize(this)
        OutfitPostRepository.initialize(this)
        OutfitRepository.initialize(this)
        SocialRepository.initialize(this)
        ProfileRepository.initialize()
    }
}

package com.example.chatapp.di

import android.content.Context
import androidx.room.Room
//import com.example.chatapp.domain.data.local.AppDatabase
import com.example.chatapp.domain.data.local.LocalMessageDao
import com.example.chatapp.domain.repository.AuthRepository
import com.example.chatapp.domain.repository.UserRepository
import com.example.chatapp.utils.Prefs
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }

    @Provides
    @Singleton
    fun provideUserRepository(firestore: FirebaseFirestore): UserRepository {
        return UserRepository(firestore)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(firebaseAuth: FirebaseAuth, userRepository: UserRepository, prefs: Prefs): AuthRepository {
        return AuthRepository(firebaseAuth, userRepository, prefs)
    }

    @Provides
    @Singleton
    fun providePrefs(@ApplicationContext context: Context): Prefs = Prefs(context)

//    @Provides
//    @Singleton
//    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
//        Room.databaseBuilder(context, AppDatabase::class.java, "chatapp.db").build()
//
//    @Provides
//    @Singleton
//    fun provideLocalMessageDao(db: AppDatabase): LocalMessageDao = db.localMessageDao()

}
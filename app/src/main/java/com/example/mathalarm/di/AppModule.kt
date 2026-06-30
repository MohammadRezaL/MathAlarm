package com.example.mathalarm.di

import android.content.Context
import androidx.room.Room
import com.example.mathalarm.data.local.dao.AlarmDao
import com.example.mathalarm.data.local.database.MathAlarmDatabase
import com.example.mathalarm.data.repository.AlarmRepositoryImpl
import com.example.mathalarm.domain.repository.AlarmRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.mathalarm.alarm.AlarmScheduler
import com.example.mathalarm.alarm.AndroidAlarmScheduler

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAlarmRepository(
        alarmRepositoryImpl: AlarmRepositoryImpl
    ): AlarmRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMathAlarmDatabase(
        @ApplicationContext context: Context
    ): MathAlarmDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = MathAlarmDatabase::class.java,
            name = "math_alarm_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideAlarmDao(
        database: MathAlarmDatabase
    ): AlarmDao {
        return database.alarmDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmSchedulerModule {

    @Binds
    @Singleton
    abstract fun bindAlarmScheduler(
        androidAlarmScheduler: AndroidAlarmScheduler
    ): AlarmScheduler
}
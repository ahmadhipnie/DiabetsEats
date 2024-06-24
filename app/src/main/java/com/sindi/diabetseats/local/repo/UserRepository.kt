package com.sindi.diabetseats.local.repo

import android.app.Application
import com.sindi.diabetseats.local.entity.UserEntity
import com.sindi.diabetseats.local.room.DiabetsDao
import com.sindi.diabetseats.local.room.DiabetsDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class UserRepository private constructor(private val application: Application) {


    private val diabetsDao: DiabetsDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = DiabetsDatabase.getDatabase(application)
        diabetsDao = db.diabetsDao()
    }

    fun insertUser(user: UserEntity) {
        executorService.execute { diabetsDao.insertUser(user) }
    }

    fun loginUser(email: String, password: String) {
        executorService.execute { diabetsDao.login(email, password) }
    }


    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(application: Application): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(application).also { instance = it }
            }
    }
}
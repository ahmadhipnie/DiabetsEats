package com.example.diabetseats.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.diabetseats.local.entity.IndexRasioEntity
import com.example.diabetseats.local.entity.MakananEntity
import com.example.diabetseats.local.entity.NutrisiEntity
import com.example.diabetseats.local.entity.PerbandinganKriteriaEntity
import com.example.diabetseats.local.entity.UserEntity
import com.example.diabetseats.local.entity.PvKriteriaEntity


@Database(entities = [UserEntity::class, MakananEntity::class, IndexRasioEntity::class, PerbandinganKriteriaEntity::class, PvKriteriaEntity::class, NutrisiEntity::class], version = 1, exportSchema = false)
abstract class DiabetsDatabase : RoomDatabase() {

    abstract fun diabetsDao(): DiabetsDao

    companion object{
        @Volatile
        private var INSTANCE: DiabetsDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): DiabetsDatabase {
            if (INSTANCE == null) {
                synchronized(DiabetsDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        DiabetsDatabase::class.java, "diabets_database"
                    )
                        .build()
                }
            }
            return INSTANCE as DiabetsDatabase
        }
    }

}
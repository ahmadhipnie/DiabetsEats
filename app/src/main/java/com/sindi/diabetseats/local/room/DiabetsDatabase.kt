package com.sindi.diabetseats.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sindi.diabetseats.local.entity.IndexRasioEntity
import com.sindi.diabetseats.local.entity.MakananEntity
import com.sindi.diabetseats.local.entity.NutrisiEntity
import com.sindi.diabetseats.local.entity.PerbandinganKriteriaEntity
import com.sindi.diabetseats.local.entity.UserEntity
import com.sindi.diabetseats.local.entity.PvKriteriaEntity


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
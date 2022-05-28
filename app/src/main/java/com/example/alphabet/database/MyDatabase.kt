package com.example.alphabet.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [BacktestResultSchema::class, StrategySchema::class, PortfolioResultSchema::class],
    version = 6,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class MyDatabase: RoomDatabase() {
    abstract fun backtestResultDao(): BacktestResultDao

    companion object{
        @Volatile
        private var INSTANCE: MyDatabase? = null

        fun getDatabase(context: Context): MyDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null)
                return tempInstance
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyDatabase::class.java,
                    "my_database"
                )
//                    .fallbackToDestructiveMigration()
                    .createFromAsset("database/database.db")
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
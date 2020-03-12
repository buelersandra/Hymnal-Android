package co.beulahana.hymnal.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import co.beulahana.hymnal.data.dao.HymnDao
import co.beulahana.hymnal.data.dao.VerseDao
import co.beulahana.hymnal.data.entity.HymnEntity
import co.beulahana.hymnal.data.entity.VerseEntity

/**
 * Created by Sandra Konotey on 2019-08-11.
 */

@Database(entities = [HymnEntity::class, VerseEntity::class],version = 1,exportSchema = false)
abstract class AppDatabase : RoomDatabase(){

    abstract fun hymnDao():HymnDao
    abstract fun verseDao():VerseDao


    companion object{

        private val DATABASE_NAME = "cac-hymn-database"
        private var mInstance: AppDatabase? = null


        @Synchronized
        fun getDatabaseInstance(context: Context): AppDatabase {
            if (mInstance == null) {
                mInstance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DATABASE_NAME)
                    .fallbackToDestructiveMigration()//remove on release
                    .build()
            }
            return mInstance!!
        }


        /**
         *
         *
         */
        fun destroyInstance() {
            mInstance = null
        }
    }


}

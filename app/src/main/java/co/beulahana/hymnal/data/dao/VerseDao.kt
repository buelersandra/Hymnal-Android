package co.beulahana.hymnal.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import co.beulahana.hymnal.data.entity.HymnEntity
import co.beulahana.hymnal.data.entity.VerseEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

/**
 * Created by Sandra Konotey on 2019-08-12.
 */

@Dao
interface VerseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVerses(hyms:List<VerseEntity>):Maybe<List<Long>>

    @Query("SELECT * FROM verse WHERE hymnId==:hymnId ORDER BY verseNumber asc")
    fun getVerses(hymnId:String): LiveData<List<VerseEntity>>
}
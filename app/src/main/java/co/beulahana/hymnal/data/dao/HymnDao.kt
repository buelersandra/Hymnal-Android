package co.beulahana.hymnal.data.dao

import android.database.Cursor
import androidx.annotation.VisibleForTesting
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import co.beulahana.hymnal.data.entity.HymnEntity
import io.reactivex.Completable
import io.reactivex.Flowable

/**
 * Created by Sandra Konotey on 2019-08-12.
 */

@Dao
interface HymnDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHymns(hyms:List<HymnEntity>):Completable

    @Query("DELETE FROM hymn")
    fun deleteHymns(): Completable

    @Query("SELECT * FROM hymn ORDER BY number asc")
    fun getHymns():Flowable<List<HymnEntity>>

    @Query("SELECT * FROM hymn ORDER BY number asc")
    fun getHymnsCursor(): Cursor

    @VisibleForTesting
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(hyms:List<HymnEntity>)
}
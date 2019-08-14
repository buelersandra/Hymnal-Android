package co.beulahana.hymnal.data.dao

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


    @Query("SELECT * FROM hymn")
    fun getHymns():Flowable<List<HymnEntity>>
}
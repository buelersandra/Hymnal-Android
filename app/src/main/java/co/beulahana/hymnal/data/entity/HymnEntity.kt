package co.beulahana.hymnal.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Created by Sandra Konotey on 2019-08-11.
 */

@Entity(tableName = "hymn")
data class HymnEntity(
    @PrimaryKey(autoGenerate = false)
    var id:String,
    val title:String,
    val chorus:String

) {

    constructor():this("","","")
}
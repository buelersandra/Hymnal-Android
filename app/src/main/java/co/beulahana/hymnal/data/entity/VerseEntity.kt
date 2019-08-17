package co.beulahana.hymnal.data.entity

import androidx.room.*

/**
 * Created by Sandra Konotey on 2019-08-11.
 */

@Entity(tableName = "verse",
    indices = [Index(value = ["hymnId","verseNumber"],unique = true)]//,
    //foreignKeys = [ForeignKey(entity = HymnEntity::class,parentColumns =["id"] ,childColumns = ["hymnId"])]
)
data class VerseEntity(
    @PrimaryKey(autoGenerate = true)
    var id:Int,
    var hymnId:String,
    var verseNumber:Int,
    var verse:String) {


    constructor():this(0,"",0,"")
}
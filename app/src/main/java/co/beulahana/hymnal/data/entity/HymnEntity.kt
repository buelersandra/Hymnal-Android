package co.beulahana.hymnal.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Created by Sandra Konotey on 2019-08-11.
 */

@Parcelize
@Entity(tableName = "hymn",indices = [Index(value = ["title","number"],unique = true)])
data class HymnEntity(
    @PrimaryKey(autoGenerate = false)
    var id:String,
    var title:String,
    var number:Int,
    var chorus:String

) :Parcelable{

    constructor():this("","",0,"")
}
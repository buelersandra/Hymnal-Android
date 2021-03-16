package co.beulahana.hymnal.data.contentprovider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import co.beulahana.hymnal.data.AppDatabase
import co.beulahana.hymnal.data.contentprovider.ContentProviderContract.AUTHORITY

class HymnContentProvider : ContentProvider() {


    internal var mDatabase:AppDatabase?=null
    var sUriMatcher:UriMatcher

    val CODE_HYMN=1

    init{
        sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        sUriMatcher.addURI(AUTHORITY, ContentProviderContract.Hymn.PATH, CODE_HYMN)
    }

    override fun onCreate(): Boolean {
        mDatabase = AppDatabase.getDatabaseInstance(context!!,)
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val code=sUriMatcher.match(uri)
        when(code){
            CODE_HYMN->{
                return mDatabase!!.hymnDao().getHymnsCursor()
            }
        }
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {

        TODO("Implement this to handle requests to delete one or more rows")
    }

    override fun getType(uri: Uri): String? {
        TODO(
            "Implement this to handle requests for the MIME type of the data" +
                    "at the given URI"
        )
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        TODO("Implement this to handle requests to insert a new row.")
    }


    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        TODO("Implement this to handle query requests from clients.")

    }
}

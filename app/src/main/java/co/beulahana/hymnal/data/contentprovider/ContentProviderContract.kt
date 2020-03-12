package co.beulahana.hymnal.data.contentprovider

import android.net.Uri

/**
 * Created by Sandra Konotey on 2019-08-27.
 */
object ContentProviderContract {

    var AUTHORITY = "co.beulahana.hymnal.provider"
    val URI_AUTHORITY = Uri.parse("content://"+AUTHORITY)

    object Hymn{
        val PATH="hymn"
        val CONTENT_URI= Uri.withAppendedPath(URI_AUTHORITY,PATH)
    }
}
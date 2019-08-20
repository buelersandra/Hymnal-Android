package co.beulahana.hymnal.resource

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Created by Sandra Konotey on 2019-08-11.
 */
class FirebaseUtil {

    companion object{
        val COLLECTION_HYMN="hymn"
        val COLLECTION_VERSE="verse"
        private var mFirebaseUtil:FirebaseUtil?=null

        private var firebaseDatabase: FirebaseFirestore?=null
        private var mCollectionReference:CollectionReference?=null


        fun openFbReference(reference:String):CollectionReference{
            if(mFirebaseUtil==null){
                mFirebaseUtil= FirebaseUtil()
                firebaseDatabase= FirebaseFirestore.getInstance()
            }
            return firebaseDatabase!!.collection(reference)


        }

        fun isConnectedToInternet(context: Context): Boolean {
            val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = connectivity.allNetworkInfo
            if (info != null)
                for (i in info.indices)
                    if (info[i].state == NetworkInfo.State.CONNECTED) {
                        return true
                    }

            return false
        }
    }
}
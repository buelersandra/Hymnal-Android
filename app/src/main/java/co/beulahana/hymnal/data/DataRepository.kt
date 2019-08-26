package co.beulahana.hymnal.data

import co.beulahana.hymnal.R
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import co.beulahana.hymnal.data.entity.HymnEntity
import co.beulahana.hymnal.data.entity.VerseEntity
import co.beulahana.hymnal.resource.FirebaseUtil
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers


/**
 * Created by Sandra Konotey on 2019-08-26.
 *
 * Repository modules are responsible should handle data operations.
 * By ensuring this, Repository modules can provide a clean API to the rest of the app and simplify the job of the consumer ViewModel.
 * Repository modules should know where to get the data from and what API calls to make when data is updated if necessary.
 * They can be considered as mediators between different data sources (REST services, Databases, XML files, …etc).
 */
class DataRepository constructor(context: Context){

    private val TAG=DataRepository::class.java.simpleName
    private var mContext: Context
    private var mDatabase:AppDatabase
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    companion object{
        private var mDataRepository:DataRepository?=null

        fun getInstance(context: Context):DataRepository{
            if(mDataRepository!=null){
                return mDataRepository!!
            }else{
                return DataRepository(context)
            }
        }
    }

    init {
        mContext=context
        mDatabase= AppDatabase.getDatabaseInstance(context)
    }


    fun getHymnsData():Flowable<List<HymnEntity>>{// response will be livedata
        return mDatabase.hymnDao().getHymns().subscribeOn(Schedulers.io()).map {list->
           if(list.isNullOrEmpty()) fetchHymns()
            return@map list
           }
    }

    fun getHymnVerses(hymnId:String): LiveData<List<VerseEntity>> {
       return mDatabase!!.verseDao().getVerses(hymnId)
    }



    fun fetchHymns(){
        var hymnList= emptyList<HymnEntity>()
        val mHymnIds = arrayListOf<String>()

        FirebaseUtil.openFbReference(FirebaseUtil.COLLECTION_HYMN).get().
            addOnSuccessListener {querySnapshot ->
                if(!querySnapshot.isEmpty){
                    hymnList=querySnapshot.toObjects(HymnEntity::class.java)
                    querySnapshot.documents.forEach {document->
                        mHymnIds.add(document.id) }

                    hymnList.forEachIndexed { index, hymnEntity ->
                        hymnEntity.id=mHymnIds[index]
                        hymnEntity.number=hymnEntity.title.trim().substring(9,hymnEntity.title.trim().length).trim().toInt()
                    }

                    compositeDisposable.add(mDatabase!!.hymnDao().insertHymns(hymnList)
                        .subscribeOn(Schedulers.io())
                        .subscribe(object : Action {
                            override fun run() {
                                fetchVerses()
                            } })
                    )
                    Log.e(TAG,"initView : "+hymnList.size)
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG,"initView : "+exception.localizedMessage)
            }
            .addOnCompleteListener {task ->

            }
    }

    private fun fetchVerses(){
        FirebaseUtil.openFbReference(FirebaseUtil.COLLECTION_VERSE).get()
            .addOnSuccessListener {querySnapshot ->
                if(!querySnapshot.isEmpty){
                    val verseList=querySnapshot.toObjects(VerseEntity::class.java)
                    compositeDisposable.add(mDatabase!!
                        .verseDao().insertVerses(verseList)
                        .subscribeOn(Schedulers.io())
                        .subscribe(object : Consumer<List<Long>> {
                            override fun accept(t: List<Long>?) {
                                Log.e(TAG,"fetchVerses : completed")

                            }

                        }))

                    Log.e(TAG,"initView : "+verseList.size)

                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG,"initView : "+exception.localizedMessage)
            }
    }
}
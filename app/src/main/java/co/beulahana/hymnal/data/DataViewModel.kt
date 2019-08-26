package co.beulahana.hymnal.data

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import co.beulahana.hymnal.R
import co.beulahana.hymnal.data.entity.HymnEntity
import co.beulahana.hymnal.data.entity.VerseEntity
import co.beulahana.hymnal.resource.FirebaseUtil
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

/**
 * Created by Sandra Konotey on 2019-08-22.
 */
class DataViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var mDataRepository: DataRepository


    private val TAG="DataViewModel"

    init {
        mDataRepository= DataRepository.getInstance(application)
    }




    fun getHymnsObserable(): Flowable<List<HymnEntity>> {
        return mDataRepository.getHymnsData()
    }

    fun getVerseObservable(hymnId:String): LiveData<List<VerseEntity>> {
        return mDataRepository.getHymnVerses(hymnId)
    }

    fun fetchData(){
        mDataRepository.fetchHymns()
    }

    override fun onCleared() {
        super.onCleared()
    }
}
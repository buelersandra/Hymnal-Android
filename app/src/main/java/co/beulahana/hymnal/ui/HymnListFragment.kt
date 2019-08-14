package co.beulahana.hymnal.ui


import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import co.beulahana.hymnal.R
import co.beulahana.hymnal.data.AppDatabase
import co.beulahana.hymnal.data.entity.HymnEntity
import co.beulahana.hymnal.data.entity.VerseEntity
import co.beulahana.hymnal.resource.FirebaseUtil
import co.beulahana.hymnal.resource.FirebaseUtil.Companion.COLLECTION_HYMN
import co.beulahana.hymnal.resource.FirebaseUtil.Companion.COLLECTION_VERSE
import co.beulahana.hymnal.resource.HolderClickListener
import co.beulahana.hymnal.resource.HymnAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_hymn_list.view.*


/**
 * A simple [Fragment] subclass.
 * Use the [HymnListFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class HymnListFragment : Fragment() {

    private val TAG=HymnListFragment::class.java.simpleName
    private var adapter: HymnAdapter?=null
    private var mDatabase:AppDatabase?=null
    private var compositeDisposable:CompositeDisposable= CompositeDisposable()
    private var mSwipeRefreshLayout:SwipeRefreshLayout?=null
    private var mHymnIds= arrayListOf<String>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_hymn_list, container, false)
        initView(view)
        return view
    }



    override fun onAttach(context: Context?) {
        mDatabase= AppDatabase.getDatabaseInstance(context!!)
        super.onAttach(context)
    }

    fun initView(view: View){
        mSwipeRefreshLayout=view.swipeRefreshLayout
        view.recyclerView.layoutManager=LinearLayoutManager(activity,RecyclerView.VERTICAL,false)
        adapter = HymnAdapter(emptyList())
        view.recyclerView.adapter=adapter
        adapter!!.setHolderClickListener(object: HolderClickListener {
            override fun onClick(id: Int, hymnId: String) {
                when(id){
                    R.id.item_layout->{
                        activity?.supportFragmentManager?.beginTransaction()?.
                            replace(R.id.container,VersesFragment.newInstance(hymnId))?.
                            addToBackStack(null)?.
                            commit()
                    }
                }
            }


        })
        initData()
    }


    fun initData(){
        compositeDisposable.add(mDatabase!!
            .hymnDao().getHymns()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Consumer<List<HymnEntity>>{
                override fun accept(t: List<HymnEntity>?) {
                    t?.let {
                        if(it.isNotEmpty()){
                            adapter!!.updateList(it)
                            mSwipeRefreshLayout!!.isRefreshing=false

                        }else{
                            fetchHymns()
                        }

                    }

                }

            }))
    }

    fun fetchHymns(){
        var hymnList= emptyList<HymnEntity>()
        mHymnIds= arrayListOf<String>()
        mSwipeRefreshLayout!!.isRefreshing=true
        FirebaseUtil.openFbReference(COLLECTION_HYMN).get().
            addOnSuccessListener {querySnapshot ->
                if(!querySnapshot.isEmpty){
                     hymnList=querySnapshot.toObjects(HymnEntity::class.java)
                    Log.e(TAG,"initView : "+hymnList.size)
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG,"initView : "+exception.localizedMessage)
            }
            .addOnCompleteListener {task ->
                if(task.isSuccessful){
                    task.result?.forEach { document->
                        mHymnIds.add(document.id)
                        hymnList.forEachIndexed { index, hymnEntity ->
                            hymnEntity.id=mHymnIds[index]
                        }

                        compositeDisposable.add(mDatabase!!
                            .hymnDao().insertHymns(hymnList)
                            .subscribeOn(Schedulers.io())
                            .subscribe(object :Action{
                                override fun run() {
                                    fetchVerses()
                                }

                            })
                        )
                    }
                }

            }
    }

    override fun onDetach() {
        if(!compositeDisposable.isDisposed){
            compositeDisposable.dispose()
        }
        super.onDetach()
    }


    fun fetchVerses(){
        FirebaseUtil.openFbReference(COLLECTION_VERSE).get()
            .addOnSuccessListener {querySnapshot ->
                if(!querySnapshot.isEmpty){
                    val verseList=querySnapshot.toObjects(VerseEntity::class.java)

                    compositeDisposable.add(mDatabase!!
                        .verseDao().insertVerses(verseList)
                        .subscribeOn(Schedulers.io())
                        .subscribe(object :Consumer<List<Long>>{
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

package co.beulahana.hymnal.ui


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
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
    private var mContext:Context?=null


    override fun onAttach(context: Context?) {
        mDatabase= AppDatabase.getDatabaseInstance(context!!)
        mContext=context
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_hymn_list, container, false)
        initView(view)
        return view
    }

    override fun onResume() {
        super.onResume()
        activity!!.setTitle(R.string.app_name)


    }

    override fun onCreateOptionsMenu(menu: Menu?,inflater: MenuInflater?) {
        menu?.clear();
        inflater!!.inflate(R.menu.activity_main,menu!!)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.menu_refresh->{
                fetchHymns()
                return true
            }
            R.id.menu_about->{
                activity!!.supportFragmentManager.beginTransaction().add(R.id.content,AboutFragment()).addToBackStack(null).commit()
                return true
            }
            R.id.menu_add->{
                activity!!.supportFragmentManager.beginTransaction().add(R.id.content,NewHymnFragment()).addToBackStack(null).commit()
                return true

            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun initView(view: View){
        mSwipeRefreshLayout=view.swipeRefreshLayout
        mSwipeRefreshLayout!!.isEnabled=false
        view.recyclerView.layoutManager=LinearLayoutManager(activity,RecyclerView.VERTICAL,false)
        adapter = HymnAdapter(emptyList())
        view.recyclerView.adapter=adapter
        adapter!!.setHolderClickListener(object: HolderClickListener {
            override fun onClick(id: Int, hymn: HymnEntity) {
                when(id){
                    R.id.item_layout->{
                        activity?.supportFragmentManager?.beginTransaction()?.
                            replace(R.id.container,VersesFragment.newInstance(hymn))?.
                            addToBackStack(null)?.
                            commit()
                    }
                }
            }


        })
        adapter!!.setBottomSheet(view.bottomsheet)
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
                                mSwipeRefreshLayout!!.isEnabled=false
                                Toast.makeText(mContext,getString(R.string.message_fetch_hymn_done),Toast.LENGTH_LONG).show()


                            }else{
                                fetchHymns()
                            }

                        }

                    }

                }))


    }

    fun fetchHymns(){

        if(FirebaseUtil.isConnectedToInternet(context!!)){
            var hymnList= emptyList<HymnEntity>()
            mHymnIds= arrayListOf<String>()
            mSwipeRefreshLayout!!.isEnabled=false
            mSwipeRefreshLayout!!.isRefreshing=true
            Toast.makeText(mContext,getString(R.string.message_fetch_hymn),Toast.LENGTH_LONG).show()
            FirebaseUtil.openFbReference(COLLECTION_HYMN).get().
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
                            .subscribe(object :Action{
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
        }else{
            Toast.makeText(context,getString(R.string.message_no_internet), Toast.LENGTH_LONG).show()
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

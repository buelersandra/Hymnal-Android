package co.beulahana.hymnal.ui


import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import co.beulahana.hymnal.R
import co.beulahana.hymnal.data.DataViewModel
import co.beulahana.hymnal.data.entity.HymnEntity
import co.beulahana.hymnal.resource.FirebaseUtil
import co.beulahana.hymnal.resource.HolderClickListener
import co.beulahana.hymnal.resource.HymnAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
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

    private var mSwipeRefreshLayout:SwipeRefreshLayout?=null

    private var mContext: Activity?=null
    private var searchView:SearchView?=null
    lateinit  var mDataViewModel:DataViewModel
    private var compositeDisposable=CompositeDisposable()


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext=context as Activity

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mDataViewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)
        initData()

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



//    override fun onResume() {
//        super.onResume()
//        activity!!.setTitle(R.string.app_name)
//    }


    override fun onCreateOptionsMenu(menu: Menu?,inflater: MenuInflater?) {
        menu?.clear();
        inflater!!.inflate(R.menu.fragment_hymn_list,menu!!)
        searchView?.setQuery("",true)
        val searchManager:SearchManager = mContext!!.getSystemService(Context.SEARCH_SERVICE)  as SearchManager
        searchView = menu.findItem(R.id.menu_search).actionView as SearchView?
        searchView!!.setSearchableInfo(searchManager.getSearchableInfo(mContext!!.getComponentName()))
        searchView!!.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter!!.filter.filter(newText)
                return true
            }
        })

    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.menu_refresh->{
                mDataViewModel.fetchData()
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


    }




    fun initData(){
        if(FirebaseUtil.isConnectedToInternet(context!!)){
            mSwipeRefreshLayout!!.isEnabled=false
            mSwipeRefreshLayout!!.isRefreshing=true
            mDataViewModel!!.getHymnsObserable().observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                adapter!!.setList(it)
                mSwipeRefreshLayout!!.isRefreshing=false
                mSwipeRefreshLayout!!.isEnabled=false
                Toast.makeText(mContext,R.string.message_fetch_hymn_done,Toast.LENGTH_LONG).show()

            })

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







}

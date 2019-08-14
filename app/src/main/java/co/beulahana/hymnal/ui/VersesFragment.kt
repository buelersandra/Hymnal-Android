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
import co.beulahana.hymnal.R
import co.beulahana.hymnal.data.AppDatabase
import co.beulahana.hymnal.data.entity.VerseEntity
import co.beulahana.hymnal.resource.HolderClickListener
import co.beulahana.hymnal.resource.HymnAdapter
import co.beulahana.hymnal.resource.VerseAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_hymn_list.view.*
import java.util.function.Consumer

/**
 * A simple [Fragment] subclass.
 *
 */
class VersesFragment : Fragment() {


    var hymnId:String?=null
    private var mDatabase:AppDatabase?=null
    private var adapter: VerseAdapter?=null

    private var TAG=VersesFragment::class.java.simpleName


    companion object{
        private val ARG_HYMN_ID = "hymn_id"

        fun newInstance(hymnId:String)=
            VersesFragment().apply {
                arguments= Bundle().apply {
                   putString(ARG_HYMN_ID,hymnId)
                }
            }
    }

    override fun onAttach(context: Context?) {
        mDatabase= AppDatabase.getDatabaseInstance(context!!)
        super.onAttach(context)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            hymnId= it.getString(ARG_HYMN_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_hymn, container, false)
        initView(view)
        return view
    }

    fun initView(view: View){
        view.recyclerView.layoutManager= LinearLayoutManager(context, RecyclerView.VERTICAL,false)
        adapter = VerseAdapter(emptyList())
        view.recyclerView.adapter=adapter
        activity?.setTitle("Verses")

        initData()
    }


    fun initData(){
        mDatabase!!.verseDao().getVerses(hymnId!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({verseList->
               verseList?.let {
                   Log.e(TAG,it.size.toString())
                   if(it.isNotEmpty()){
                       adapter!!.updateList(it)

                   }
               }
            })

    }


}

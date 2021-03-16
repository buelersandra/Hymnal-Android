package co.beulahana.hymnal.ui


import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.beulahana.hymnal.R
import co.beulahana.hymnal.data.AppDatabase
import co.beulahana.hymnal.data.DataViewModel
import co.beulahana.hymnal.data.entity.HymnEntity
import co.beulahana.hymnal.data.entity.VerseEntity
import co.beulahana.hymnal.resource.VerseAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.botton_sheet_chorus.view.*
import kotlinx.android.synthetic.main.fragment_hymn_list.view.*
import com.google.android.material.bottomsheet.BottomSheetDialog


/**
 * A simple [Fragment] subclass.
 *
 */
class VersesFragment : Fragment() {


    var hymn:HymnEntity?=null
    private var mDatabase:AppDatabase?=null
    private var adapter: VerseAdapter?=null

    private var TAG=VersesFragment::class.java.simpleName


    companion object{
        private val ARG_HYMN_ID = "hymn_id"

        fun newInstance(hymnId:HymnEntity)=
            VersesFragment().apply {
                arguments= Bundle().apply {
                   putParcelable(ARG_HYMN_ID,hymnId)
                }
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).supportActionBar?.setHomeButtonEnabled(true)
        mDatabase= AppDatabase.getDatabaseInstance(context!!)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu?.clear()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            hymn= it.getParcelable(ARG_HYMN_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(co.beulahana.hymnal.R.layout.fragment_verse, container, false)
        initView(view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val dataViewModel=ViewModelProviders.of(this).get(DataViewModel::class.java)
        initData(dataViewModel)
    }



    fun initView(view: View){
        view.recyclerView.layoutManager= LinearLayoutManager(context, RecyclerView.VERTICAL,false)
        adapter = VerseAdapter(emptyList())
        view.recyclerView.adapter=adapter
        activity?.setTitle(hymn!!.title)

        val chorus=hymn!!.chorus.replace("\\n","\n")

        val dialogView = layoutInflater.inflate(R.layout.botton_sheet_chorus, null)
        val dialog = BottomSheetDialog(context!!)
        dialogView.findViewById<TextView>(R.id.text_chorus)!!.setText(chorus)
        dialog.setContentView(dialogView)

        dialog.setOnShowListener(object :DialogInterface.OnShowListener{
            override fun onShow(dialog: DialogInterface?) {

            }
        })

        view.text_chorus_label.setOnClickListener {
            dialog.show()
        }
    }


    fun initData(dataViewModel: DataViewModel){
        dataViewModel.getVerseObservable(hymn!!.id).observe(this,object :Observer<List<VerseEntity>>{
            override fun onChanged(list: List<VerseEntity>?) {
                list?.let {
                    Log.e(TAG,it.size.toString())
                    if(it.isNotEmpty()){
                        adapter!!.updateList(it)

                    }
                }

            }
        })

    }


}

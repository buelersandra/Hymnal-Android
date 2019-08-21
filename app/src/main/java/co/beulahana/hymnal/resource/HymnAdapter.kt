package co.beulahana.hymnal.resource

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import co.beulahana.hymnal.R
import co.beulahana.hymnal.data.entity.HymnEntity
import com.flipboard.bottomsheet.BottomSheetLayout
import com.flipboard.bottomsheet.commons.IntentPickerSheetView

/**
 * Created by Sandra Konotey on 2019-08-11.
 */
class HymnAdapter constructor(_hymnList:List<HymnEntity>) : RecyclerView.Adapter<HymnAdapter.HymnViewHolder>(),Filterable {

    var filterHymnList = _hymnList
    var hymnList = _hymnList
    private var holderClickListener:HolderClickListener?=null
    private var bottomSheetLayout:BottomSheetLayout?=null
    private var customFilter:CustomFilter= CustomFilter()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HymnViewHolder {
        return HymnViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_hymn,parent,false))
    }

    override fun getItemCount(): Int {
        return filterHymnList.size
    }

    override fun onBindViewHolder(holder: HymnViewHolder, position: Int) {
        val hymnEntity=filterHymnList.get(position)
        holder.bind(hymnEntity)
        holder.item_layout.setOnClickListener {
            holderClickListener?.onClick(it.id,hymnEntity)
        }
    }


    fun setList(_hymnList:List<HymnEntity>){
        this.filterHymnList=_hymnList
        this.hymnList=_hymnList
        notifyDataSetChanged()
    }
    
    
    fun setHolderClickListener(_holderClickListener: HolderClickListener){
        this.holderClickListener=_holderClickListener
        
    }

    fun setBottomSheet(bottomsheet:BottomSheetLayout){
        bottomSheetLayout=bottomsheet
    }

    override fun getFilter(): Filter {
        return customFilter
    }

    inner class HymnViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        val text_title=itemView.findViewById(R.id.text_title) as TextView
        val image_fav=itemView.findViewById<ImageView>(R.id.image_fav)
        val image_share=itemView.findViewById<ImageView>(R.id.image_share)
        val item_layout=itemView.findViewById<LinearLayout>(R.id.item_layout)

        fun bind(hymnEntity: HymnEntity){
            text_title.setText(hymnEntity.title)
            image_share.setOnClickListener {
                share(hymnEntity)
            }
        }

        fun share(hymnEntity: HymnEntity){
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, itemView.context.getResources().getString(R.string.app_name))
            shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                hymnEntity.title + ".\n\nChorus\n" + hymnEntity.chorus+"\n"+itemView.context.getString(R.string.share_message))
            shareIntent.type = "text/plain"

            val intentPickerSheetView = IntentPickerSheetView(itemView.context, shareIntent, "Share with...",
                IntentPickerSheetView.OnIntentPickedListener { activityInfo ->
                    bottomSheetLayout!!.dismissSheet()
                    itemView.context.startActivity(activityInfo.getConcreteIntent(shareIntent).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                })

            intentPickerSheetView.setFilter { info -> !info.componentName.packageName.startsWith("com.android") }
            bottomSheetLayout!!.showWithSheetView(intentPickerSheetView)
        }

    }

    inner class CustomFilter: Filter(){

        override fun performFiltering(search: CharSequence?): FilterResults {
            val filterResults=FilterResults()
            val filteredList= arrayListOf<HymnEntity>()
            if(!search.isNullOrEmpty()){
                hymnList.forEach { hymn->
                    if(hymn.title.contains(search.toString(),true)){
                        filteredList.add(hymn)
                    }
                }

                filterResults.values=filteredList
                filterResults.count=filteredList.size
            }else{
                filterResults.values=hymnList
                filterResults.count=hymnList.size
            }


            return filterResults

        }

        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
            p1?.let {
                this@HymnAdapter.filterHymnList= it.values as List<HymnEntity>
                notifyDataSetChanged()
            }

        }
    }
}
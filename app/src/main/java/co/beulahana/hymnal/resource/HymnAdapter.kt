package co.beulahana.hymnal.resource

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import co.beulahana.hymnal.R
import co.beulahana.hymnal.data.entity.HymnEntity

/**
 * Created by Sandra Konotey on 2019-08-11.
 */
class HymnAdapter constructor(_hymnList:List<HymnEntity>) : RecyclerView.Adapter<HymnAdapter.HymnViewHolder>() {

    var hymnList= _hymnList
    private var holderClickListener:HolderClickListener?=null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HymnViewHolder {
        return HymnViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_hymn,parent,false))

    }

    override fun getItemCount(): Int {
        return hymnList.size
    }

    override fun onBindViewHolder(holder: HymnViewHolder, position: Int) {
        val hymnEntity=hymnList.get(position)
        holder.bind(hymnEntity)
        holder.item_layout.setOnClickListener {
            holderClickListener?.onClick(it.id,hymnEntity.id)
        }
    }


    fun updateList(_hymnList:List<HymnEntity>){
        this.hymnList=_hymnList
        notifyDataSetChanged()
    }
    
    
    fun setHolderClickListener(_holderClickListener: HolderClickListener){
        this.holderClickListener=_holderClickListener
        
    }
    


    class HymnViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        val text_title=itemView.findViewById(R.id.text_title) as TextView
        val image_fav=itemView.findViewById<ImageView>(R.id.image_fav)
        val image_share=itemView.findViewById<ImageView>(R.id.image_share)
        val item_layout=itemView.findViewById<LinearLayout>(R.id.item_layout)

        fun bind(hymnEntity: HymnEntity){
            text_title.setText(hymnEntity.title)
            
            

        }

    }
}
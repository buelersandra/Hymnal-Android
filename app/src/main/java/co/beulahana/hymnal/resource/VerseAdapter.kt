package co.beulahana.hymnal.resource

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.beulahana.hymnal.R
import co.beulahana.hymnal.data.entity.HymnEntity
import co.beulahana.hymnal.data.entity.VerseEntity

/**
 * Created by Sandra Konotey on 2019-08-11.
 */
class VerseAdapter constructor(_verseList:List<VerseEntity>) : RecyclerView.Adapter<VerseAdapter.VerseViewHolder>() {

    var verseList= _verseList


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerseViewHolder {
        return VerseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_verse,parent,false))

    }

    override fun getItemCount(): Int {
        return verseList.size
    }

    override fun onBindViewHolder(holder: VerseViewHolder, position: Int) {
        val verseEntity=verseList.get(position)
        holder.bind(verseEntity)

    }


    fun updateList(_hymnList:List<VerseEntity>){
        this.verseList=_hymnList
        notifyDataSetChanged()
    }



    class VerseViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        val text_verse_number=itemView.findViewById(R.id.text_verse_number) as TextView
        val text_verse=itemView.findViewById(R.id.text_verse) as TextView


        fun bind(verseEntity:VerseEntity){

            text_verse_number.setText(verseEntity.verseNumber.toString())

            val new=verseEntity.verse.replace("\\n","\n")
            text_verse.setText(new)
            
            

        }

    }
}
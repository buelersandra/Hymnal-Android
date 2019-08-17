package co.beulahana.hymnal.ui


import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

import co.beulahana.hymnal.R
import co.beulahana.hymnal.data.entity.HymnEntity
import co.beulahana.hymnal.data.entity.VerseEntity
import co.beulahana.hymnal.resource.FirebaseUtil
import co.beulahana.hymnal.resource.FirebaseUtil.Companion.COLLECTION_HYMN
import co.beulahana.hymnal.resource.FirebaseUtil.Companion.COLLECTION_VERSE
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference


/**
 * A simple [Fragment] subclass.
 *
 */
class NewHymnFragment : Fragment() {

    private var mTitle:EditText?=null
    private var mVersesContainer:ViewGroup?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view= inflater.inflate(R.layout.fragment_new_hymn, container, false)
        initView(view)
        return view
    }

    fun initView(view:View){
        mTitle=view.findViewById(R.id.edit_title)
        mVersesContainer=view.findViewById(R.id.container_verses)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear();
        inflater!!.inflate(R.menu.fragment_add_hymn,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.menu_save->{
                prepareHymn()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveHymn(hymn:HymnEntity,verses:ArrayList<VerseEntity>) {
        FirebaseUtil.openFbReference(COLLECTION_HYMN).add(hymn).addOnCompleteListener (activity!!,object :
            OnCompleteListener<DocumentReference>{
            override fun onComplete(task: Task<DocumentReference>) {
                if(task.isSuccessful){
                    val hymnId=task.result!!.id
                    verses.forEach { verse->
                        verse.hymnId=hymnId
                    }
                    saveVerses(verses)
                }
            }

        })

    }

    fun saveVerses(verses:ArrayList<VerseEntity>){
        var count=0
        verses.forEach {
            FirebaseUtil.openFbReference(COLLECTION_VERSE).add(it).addOnSuccessListener {
                count++
                if(count==verses.size) clear()
            }

        }


    }

    fun clear(){
        Toast.makeText(context,"Hymn Added",Toast.LENGTH_LONG).show()
        activity!!.supportFragmentManager.beginTransaction().add(R.id.content,NewHymnFragment()).commit()
    }

    fun prepareHymn(){
        val newHymn=HymnEntity()
        val verseList= arrayListOf<VerseEntity>()
        if(mTitle!!.text.isNotEmpty()){
            newHymn.title=mTitle!!.text.toString().trim().toUpperCase()
            val chorus=mVersesContainer!!.findViewById<EditText>(R.id.edit_chorus)
            //if(chorus.text.isNotEmpty()){
                newHymn.chorus=chorus.text.toString().trim()
                val verseViews= arrayListOf<View>()
                mVersesContainer!!.findViewsWithText(verseViews,"container_partial_verse",View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION)
                verseViews.forEach {
                    val verseNum=it.findViewById<EditText>(R.id.edit_number)
                    val verse=it.findViewById<EditText>(R.id.edit_verse)
                    if(verseNum.text.isNotEmpty() && verseNum.text.isNotEmpty()){
                        verseList.add(VerseEntity(0,"", verseNum.text.toString().toInt(),verse.text.toString()))
                    }

                }
                saveHymn(newHymn,verseList)


            //}
        }
    }


}

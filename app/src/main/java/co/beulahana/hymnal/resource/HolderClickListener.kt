package co.beulahana.hymnal.resource

import androidx.annotation.IdRes

interface HolderClickListener{
    fun onClick(@IdRes id:Int,hymnId:String)

}


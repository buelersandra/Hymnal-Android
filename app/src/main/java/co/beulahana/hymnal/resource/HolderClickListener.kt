package co.beulahana.hymnal.resource

import androidx.annotation.IdRes
import co.beulahana.hymnal.data.entity.HymnEntity

interface HolderClickListener{
    fun onClick(@IdRes id:Int,hymn:HymnEntity)

}


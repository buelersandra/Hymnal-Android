package co.beulahana.hymnal.data

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.beulahana.hymnal.data.dao.HymnDao
import co.beulahana.hymnal.data.entity.HymnEntity
import org.mockito.MockitoAnnotations
import java.util.*
import androidx.test.platform.app.*
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.observers.TestObserver
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import kotlin.collections.ArrayList
import org.mockito.Spy
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Created by Sandra Konotey on 2019-08-29.
 */

@RunWith(RobolectricTestRunner::class)
class HymnDaoTest {

    val list= arrayListOf<HymnEntity>()
    var appDatabase:AppDatabase?=null
    var hymnDao:HymnDao?=null

    @Mock
    lateinit var observer:Observer<List<HymnEntity>>

    @Before
    fun initialise(){
        println("initialise")
        MockitoAnnotations.initMocks(this)
        val context=InstrumentationRegistry.getInstrumentation().targetContext
        appDatabase=Room.inMemoryDatabaseBuilder(context,AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        hymnDao=appDatabase!!.hymnDao()

//
//        observer = object : Observer<List<HymnEntity>> {
//            override fun onSubscribe(d: Disposable) {
//                println("onSubscribe")
//            }
//
//            override fun onNext(t: List<HymnEntity>) {
//                println("onNext : "+ t.size)
//            }
//
//            override fun onError(e: Throwable) {
//                println("onError")
//            }
//
//            override fun onComplete() {
//                println("onComplete")
//            }
//        }
    }

    fun createRandomHymns(num:Int): ArrayList<HymnEntity> {
        for(i in 1..num){
            list.add(HymnEntity(i.toString(),"Hymn "+i,i,"chorus"))
        }
        return list
    }

    @Test
    fun insertHymns(){


        val latch = CountDownLatch(1)
        val list=createRandomHymns(10)
        hymnDao!!.getHymns().toObservable().subscribe(observer)
        hymnDao!!.insert(list)
        latch.await(50, TimeUnit.MILLISECONDS)//wait for insertion
        Mockito.verify(observer, atLeastOnce()).onNext(list)



    }

    @Test
    fun insertDuplicate(){

        val observer=TestObserver<List<HymnEntity>>()
        val latch = CountDownLatch(1)
        val list=createRandomHymns(10)
        hymnDao!!.getHymns().toObservable().subscribe(observer)
        hymnDao!!.insert(list)
        latch.await(50, TimeUnit.MILLISECONDS)//wait for insertion

        hymnDao!!.insert(list)
        latch.await(50, TimeUnit.MILLISECONDS)//wait for insertion


        observer.assertValueAt(0,list)
        observer.assertValueAt(1,list)


    }

    @After
    fun clearData(){
        println("clearData")
        appDatabase!!.clearAllTables()
    }
}
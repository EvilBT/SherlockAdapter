package cn.sherlockzp.sample

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.Menu
import android.view.MenuItem
import cn.sherlockzp.adapter.BaseExpandableAdapter
import cn.sherlockzp.adapter.BaseMultiAdapter
import cn.sherlockzp.adapter.IMultiItem
import cn.sherlockzp.sample.model.Image
import cn.sherlockzp.sample.model.Text

import kotlinx.android.synthetic.main.activity_multi_item.*

class MultiItemActivity : AppCompatActivity() {

    private val gridLayoutManager by lazy {
        GridLayoutManager(this,3)
    }

    private val staggeredGridLayoutManager by lazy {
        StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    private val adapter = BaseMultiAdapter() //BaseExpandableAdapter()
    private val data = ArrayList<IMultiItem>()

    private var isGrid = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_item)

        initView()
    }

    private fun initView() {
        rv_list_multi.layoutManager = gridLayoutManager
        rv_list_multi.adapter = adapter

        adapter.setOnItemLongClickListener { _, adapterPosition ->
            AlertDialog.Builder(this)
                    .setTitle("是否删除第${adapterPosition}项")
                    .setPositiveButton("确定"){
                        _, _ ->
                        adapter.removeDataAt(adapterPosition)
                    }
                    .setNegativeButton("取消",null)
                    .create()
                    .show()
            return@setOnItemLongClickListener true
        }

        initGridData()

        sr_refresh.setOnRefreshListener {
            sr_refresh.postDelayed({
                adapter.setData(data)
                sr_refresh.isRefreshing = false
            },500)
        }
    }

    private fun initGridData(){
        data.clear()
        val list = resources.getStringArray(R.array.list)
        for (i in list.indices) {
            data.add(Text(list[i], i % 3 + 1))
        }

        val width = resources.displayMetrics.widthPixels / gridLayoutManager.spanCount

        data.add(2,Image(R.drawable.girl,width * 2,2))
        data.add(3,Image(R.drawable.easter_eggs,width))
        data.add(4,Image(R.drawable.adult,width * 3,3))

        data.add(7,Image(R.drawable.girl1,width * 3,3))
        data.add(7,Image(R.drawable.sunset,width))
        data.add(7,Image(R.drawable.horses,width))

        adapter.setData(data)
    }

    private fun initStaggeredGridData(){
        data.clear()
        val list = resources.getStringArray(R.array.list)
        for (i in list.indices) {
            data.add(Text(list[i], i % 3 + 1))
        }

        val width = resources.displayMetrics.widthPixels / staggeredGridLayoutManager.spanCount

        data.add(2,Image(R.drawable.girl,width))
        data.add(2,Image(R.drawable.girl1,width))
        data.add(2,Image(R.drawable.easter_eggs,width))
        data.add(2,Image(R.drawable.adult,width))
        data.add(2,Image(R.drawable.sunset,width))
        data.add(2,Image(R.drawable.fashion,width))
        data.add(2,Image(R.drawable.notes,width))
        data.add(7,Image(R.drawable.girl2,width))

        adapter.setData(data)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.action_grid -> {
                if (isGrid) {
                    return true
                }
                isGrid = true
                rv_list_multi.layoutManager = gridLayoutManager
                adapter.onAttachedToRecyclerView(rv_list_multi)
                initGridData()
                return true
            }
            R.id.action_staggered -> {
                if (!isGrid) {
                    return true
                }
                isGrid = false
                rv_list_multi.layoutManager = staggeredGridLayoutManager
                adapter.onAttachedToRecyclerView(rv_list_multi)
                initStaggeredGridData()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.multiitem_menu, menu)
        return true
    }
}

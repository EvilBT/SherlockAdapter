package cn.sherlockzp.sample

import android.os.Bundle
import android.app.Activity
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import cn.sherlockzp.adapter.BaseMultiAdapter
import cn.sherlockzp.adapter.IMultiItem
import cn.sherlockzp.sample.model.Image
import cn.sherlockzp.sample.model.Text

import kotlinx.android.synthetic.main.activity_basic.*

class BasicActivity : AppCompatActivity() {

    private val adapter = BaseMultiAdapter()
    private val data = ArrayList<IMultiItem>()

    private val gridLayoutManager by lazy {
        GridLayoutManager(this,3)
    }

    private val staggeredGridLayoutManager by lazy {
        StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    private var isGrid = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic)

        initView()
        initGridData()
    }

    private fun initView() {
        rv_list_basic.layoutManager = gridLayoutManager
        rv_list_basic.adapter = adapter

        with(adapter) {
            addHeadLayout(R.layout.item_head1,false)
            addHeadLayout(R.layout.item_head2,false)
            setOnItemLongClickListener { _, adapterPosition ->
                AlertDialog.Builder(this@BasicActivity)
                        .setTitle("是否删除第${application}项")
                        .setPositiveButton("确定"){
                            _,_ ->
                            adapter.removeDataAt(adapterPosition)
                        }.setNegativeButton("取消",null)
                        .create()
                        .show()
                return@setOnItemLongClickListener true
            }
        }

        sr_refresh.setOnRefreshListener {
            sr_refresh.postDelayed({
                adapter.setData(data)
                sr_refresh.isRefreshing = false
            },500)
        }

        cb_head1.setOnCheckedChangeListener{
            _, _ ->
            changeHead()
        }
        cb_head2.setOnCheckedChangeListener{
            _, _ ->
            changeHead()
        }

        et_span_count1.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                changeHead()
            }
        })
        et_span_count2.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                changeHead()
            }
        })
    }

    private fun changeHead() {
        adapter.removeAllHead()

        var spanSize1 = 0
        var text = et_span_count1.text.toString()
        if (text.isNotEmpty() && TextUtils.isDigitsOnly(text)) {
            spanSize1 = text.toInt()
        }
        var spanSize2 = 0
        text = et_span_count2.text.toString()
        if (text.isNotEmpty() && TextUtils.isDigitsOnly(text)) {
            spanSize2 = text.toInt()
        }

        with(adapter){
            addHeadLayout(R.layout.item_head1, cb_head1.isChecked, spanSize1)
            addHeadLayout(R.layout.item_head2, cb_head2.isChecked, spanSize2)
            adapter.doNotifyDataSetChanged()
        }
    }

    private fun initGridData(){
        data.clear()
        val list = resources.getStringArray(R.array.list)
        for (i in list.indices) {
            data.add(Text(list[i], i % 3 + 1))
        }

        val width = resources.displayMetrics.widthPixels / gridLayoutManager.spanCount

        data.add(2, Image(R.drawable.girl,width * 2,2))
        data.add(3, Image(R.drawable.easter_eggs,width))
        data.add(4, Image(R.drawable.adult,width * 3,3))

        data.add(7, Image(R.drawable.girl1,width * 3,3))
        data.add(7, Image(R.drawable.sunset,width))
        data.add(7, Image(R.drawable.horses,width))

        adapter.setData(data)
    }

    private fun initStaggeredGridData(){
        data.clear()
        val list = resources.getStringArray(R.array.list)
        for (i in list.indices) {
            data.add(Text(list[i], i % 3 + 1))
        }

        val width = resources.displayMetrics.widthPixels / staggeredGridLayoutManager.spanCount

        data.add(2, Image(R.drawable.girl,width))
        data.add(2, Image(R.drawable.girl1,width))
        data.add(2, Image(R.drawable.easter_eggs,width))
        data.add(2, Image(R.drawable.adult,width))
        data.add(2, Image(R.drawable.sunset,width))
        data.add(2, Image(R.drawable.fashion,width))
        data.add(2, Image(R.drawable.notes,width))
        data.add(7, Image(R.drawable.girl2,width))

        adapter.setData(data)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.action_grid -> {
                if (isGrid) {
                    return true
                }
                isGrid = true
                rv_list_basic.layoutManager = gridLayoutManager
                adapter.onAttachedToRecyclerView(rv_list_basic)
                initGridData()
                return true
            }
            R.id.action_staggered -> {
                if (!isGrid) {
                    return true
                }
                isGrid = false
                rv_list_basic.layoutManager = staggeredGridLayoutManager
                adapter.onAttachedToRecyclerView(rv_list_basic)
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

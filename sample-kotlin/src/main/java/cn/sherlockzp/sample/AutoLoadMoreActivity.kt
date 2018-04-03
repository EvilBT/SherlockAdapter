package cn.sherlockzp.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import cn.sherlockzp.adapter.BaseAdapter
import cn.sherlockzp.adapter.BaseExpandableAdapter
import cn.sherlockzp.adapter.BaseViewHolder
import cn.sherlockzp.adapter.LayoutConfig
import cn.sherlockzp.sample.model.Card
import cn.sherlockzp.sample.model.ImageCard
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_auto_load_more.*

class AutoLoadMoreActivity : AppCompatActivity() , View.OnClickListener{

    private val LOAD_ADD = 0
    private val LOAD_FAILED = 1
    private val LOAD_COMPLETED = 2

    private var state = LOAD_ADD

    private var showEmpty = true

    private val width by lazy {
        resources.displayMetrics.widthPixels
    }

    private val titles = arrayOf("Adult","Easter Eggs","Girl", "Sunset")
    private val imageResIds = arrayOf(R.drawable.adult, R.drawable.easter_eggs, R.drawable.girl, R.drawable.sunset)

    private val data = ArrayList<ImageCard>()
    private val adapter = object : BaseAdapter<ImageCard>() {
        override fun getLayoutRes(index: Int) = R.layout.item_image_card

        override fun convert(holder: BaseViewHolder, data: ImageCard, index: Int) {
            holder.setText(R.id.tv_image_title, data.title)
                    .setImage(R.id.iv_bg){
                        Glide.with(it.context)
                                .load(data.imageResId)
                                .apply(RequestOptions.fitCenterTransform())
                                .apply(RequestOptions.overrideOf(width))
                                .into(it)
                    }
        }

        override fun convertHead(holder: BaseViewHolder, headLayout: LayoutConfig) {
            holder.setText(R.id.tv_head, "这是加载更多")
        }
    }
    /*
    private val data = ArrayList<Card>()
    private val adapter = object : BaseExpandableAdapter() {
        override fun convertHead(holder: BaseViewHolder, headLayout: LayoutConfig) {
            holder.setText(R.id.tv_head, "这是加载更多")
        }
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto_load_more)

        initView()
        initData()
    }

    private fun initView(){
        rv_list_load_more.layoutManager = LinearLayoutManager(this)
        rv_list_load_more.adapter = adapter

        with(adapter) {
            alwaysShowFoot = true
            alwaysShowHead = true
            addHeadLayout(R.layout.item_head)
            addFootLayout(R.layout.item_foot2)

            // 必须同时设置事件监听与开启auto
            openAutoLoadMore()
            setOnLoadMoreListener {
                // 模拟加载更多
                rv_list_load_more.postDelayed({
                    when(state) {
                        LOAD_ADD -> adapter.addData(this@AutoLoadMoreActivity.data)
                        LOAD_COMPLETED -> adapter.loadCompleted()
                        LOAD_FAILED -> adapter.loadFailed()
                    }
                }, 800)
            }
        }

        sr_refresh.setOnRefreshListener {
            // 模拟刷新
            rv_list_load_more.postDelayed({
                sr_refresh.isRefreshing = false
                adapter.setData(data)
            }, 500)
        }

        cb_show_head.isChecked = true
        cb_show_head.setOnCheckedChangeListener{
            _,isChecked -> adapter.alwaysShowHead = isChecked
        }
        cb_show_foot.isChecked = true
        cb_show_foot.setOnCheckedChangeListener{
            _,isChecked -> adapter.alwaysShowFoot = isChecked
        }

        action_add.setOnClickListener(this)
        action_failed.setOnClickListener(this)
        action_completed.setOnClickListener(this)
        action_empty.setOnClickListener(this)
        action_close.setOnClickListener(this)
    }

    private fun initData() {
        for (i in titles.indices) {
            data.add(ImageCard(imageResIds[i], titles[i]))
            //data.add(Card(width, imageResIds[i], titles[i]))
        }
        adapter.setData(data)
    }

    override fun onClick(v: View?) {
        when(v) {
            action_add -> {
                title = "加载更多"
                adapter.openAutoLoadMore()
                adapter.setData(data)
                state = LOAD_ADD
            }
            action_failed -> {
                title = "加载更多失败"
                adapter.openAutoLoadMore()
                adapter.setData(data)
                state = LOAD_FAILED
            }
            action_completed -> {
                title = "没有更多数据"
                adapter.openAutoLoadMore()
                adapter.setData(data)
                state = LOAD_COMPLETED
            }
            action_empty -> {
                if (showEmpty) {
                    title = "没有数据"
                    adapter.setData(null)
                    showEmpty = false
                    action_empty.text = "异常数据"
                } else {
                    title = "数据异常"
                    adapter.showErrorView()
                    showEmpty = true
                    action_empty.text = "空数据"
                }
            }
            action_close -> {
                adapter.openAutoLoadMore(false)
            }
        }
    }
}

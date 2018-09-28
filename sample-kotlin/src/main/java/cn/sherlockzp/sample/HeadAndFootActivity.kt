package cn.sherlockzp.sample

import android.os.Bundle
import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import cn.sherlockzp.adapter.BaseAdapter
import cn.sherlockzp.adapter.BaseViewHolder
import cn.sherlockzp.adapter.ImageCallback
import cn.sherlockzp.adapter.LayoutConfig
import cn.sherlockzp.sample.model.ImageCard
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import kotlinx.android.synthetic.main.activity_head_and_foot.*

class HeadAndFootActivity : AppCompatActivity() {

    private var changeOrder = true

    private val titles = arrayOf("Adult","Easter Eggs","Girl", "Sunset")
    private val imageResIds = arrayOf(R.drawable.adult, R.drawable.easter_eggs, R.drawable.girl, R.drawable.sunset)

    private val callback = object : DiffUtil.ItemCallback<ImageCard>(){
        override fun areItemsTheSame(oldItem: ImageCard, newItem: ImageCard): Boolean {
            return oldItem.imageResId == newItem.imageResId
        }

        override fun areContentsTheSame(oldItem: ImageCard, newItem: ImageCard): Boolean {
            return oldItem.title == newItem.title
        }
    }

    private val adapter = object : BaseAdapter<ImageCard>(){
        override fun getLayoutRes(index: Int) = R.layout.item_image_card

        override fun convert(holder: BaseViewHolder, data: ImageCard, index: Int) {
            holder.setText(R.id.tv_image_title, data.title)
                    .setImage(R.id.iv_bg) {
                        Glide.with(this@HeadAndFootActivity)
                                .load(data.imageResId)
                                .apply(RequestOptions.fitCenterTransform())
                                .into(it)
                    }
        }

        override fun convertHead(holder: BaseViewHolder, headLayout: LayoutConfig) {
            holder.setText(R.id.tv_head,"这是头部啦")
        }

        override fun convertFoot(holder: BaseViewHolder, footLayout: LayoutConfig) {
            holder.setText(R.id.tv_foot, "这是尾部")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_head_and_foot)

        rv_list_girls.layoutManager = LinearLayoutManager(this)
        rv_list_girls.adapter = adapter

        initAdapter()

        sr_refresh.setOnRefreshListener {
            refresh()
            sr_refresh.isRefreshing = false
        }
    }

    private fun refresh(){
        if (changeOrder){
            val data = ArrayList<ImageCard>()
            for (i in titles.indices) {
                val card = ImageCard(imageResIds[i], titles[i])
                data.add(card)
            }
            adapter.setData(data.reversed())
        } else {
            val data = ArrayList<ImageCard>()
            for (i in titles.indices) {
                val card = ImageCard(imageResIds[i], titles[i])
                data.add(card)
            }
            adapter.setData(data)
        }
        changeOrder = !changeOrder
    }

    private fun initAdapter() {
        val data = ArrayList<ImageCard>()
        for (i in titles.indices) {
            val card = ImageCard(imageResIds[i], titles[i])
            data.add(card)
        }

        with(adapter) {
            addHeadLayout(R.layout.item_head)
            addFootLayout(R.layout.item_foot)
            addFootLayout(R.layout.item_foot2)
            setData(data)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_anim -> adapter.setItemDiffCallback(callback)
            R.id.action_not_anim -> adapter.setItemDiffCallback(null)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.head_menu, menu)
        return true
    }
}

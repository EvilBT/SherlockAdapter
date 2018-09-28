package cn.sherlockzp.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SimpleItemAnimator
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.Menu
import android.view.MenuItem
import cn.sherlockzp.adapter.*
import cn.sherlockzp.sample.model.Card
import cn.sherlockzp.sample.model.ImageLabel

import kotlinx.android.synthetic.main.activity_expandable.*

class ExpandableActivity : AppCompatActivity() {

    private val labels = arrayOf("Animal","Beauty","Scenery","Tranquil")

    // Animal Card
    private val animalIds = arrayOf(R.drawable.fuchs,R.drawable.horses)
    private val animalTitles = arrayOf("Fuchs","Horses")

    //Beauty Card
    private val beautyIds = arrayOf(R.drawable.adult, R.drawable.girl,
            R.drawable.girl1,R.drawable.girl2,R.drawable.smile,R.drawable.fashion)
    private val beautyTitles = arrayOf("Adult","Girl", "Girl MM",
            "MM Girl","Smile","fashion")

    // Scenery Card
    private val sceneryIds = arrayOf(R.drawable.architecture,R.drawable.denmark,R.drawable.sunset)
    private val sceneryTitles = arrayOf("Architecture","Denmark","Sunset")

    // Tranquil Card
    private val tranquilIds = arrayOf(R.drawable.easter_eggs,R.drawable.notes)
    private val tranquilTitle = arrayOf("Easter Eggs","Notes")

    private val adapter = object : BaseExpandableAdapter(){
        override fun convertHead(holder: BaseViewHolder, headLayout: LayoutConfig) {
            holder.setText(R.id.tv_head, "这是图片展")
        }
    }

    private val gridLayoutManager by lazy {
        GridLayoutManager(this,2)
    }
    private val staggeredGridLayoutManager by lazy {
        StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }
    private var isGrid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expandable)

        initView()
        initData()
    }

    private fun initView() {
        rv_list_expand.layoutManager = staggeredGridLayoutManager
        rv_list_expand.adapter = adapter
        val animator = rv_list_expand.itemAnimator
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }

        with(adapter) {
            addHeadLayout(R.layout.item_head,spanSize = 2)
            addFootLayout(R.layout.item_foot2, spanSize = 2)
            setOnItemClickListener { view, adapterPosition ->
                if (view.id == R.id.label_root) {
                    val item = getData(adapterPosition)
                    if (item is IExpandable) {
                        if (item.expandable) {
                            // 修复issue#2
                            // 这个支持包的bug: https://issuetracker.google.com/issues/37034096
                            staggeredGridLayoutManager.invalidateSpanAssignments()
                            collapseAll(adapterPosition)
                        } else {
                            expandAll(adapterPosition)
                        }
                    }
                }
            }
            setOnItemLongClickListener { _, adapterPosition ->
                if (adapterPosition < headSize) {
                    removeHead(adapterPosition)
                    return@setOnItemLongClickListener true
                }
                if (adapterPosition >= headSize + getShowDataSize()) {
                    removeFoot(adapterPosition)
                    return@setOnItemLongClickListener true
                }
                removeDataAt(adapterPosition)
                return@setOnItemLongClickListener true
            }
            setItemDiffCallback(object : DiffUtil.ItemCallback<IMultiItem>(){
                override fun areItemsTheSame(oldItem: IMultiItem, newItem: IMultiItem): Boolean {
                    if (oldItem is ImageLabel && newItem is ImageLabel) {
                        return oldItem.label == newItem.label
                    }
                    if (oldItem is Card && newItem is Card) {
                        return oldItem.imageResId == newItem.imageResId
                    }
                    return false
                }

                override fun areContentsTheSame(oldItem: IMultiItem, newItem: IMultiItem): Boolean {
                    if (oldItem is ImageLabel && newItem is ImageLabel) {
                        return true
                    }
                    if (oldItem is Card && newItem is Card) {
                        return oldItem.title == newItem.title
                    }
                    return false
                }
            })
        }

        sr_refresh.setOnRefreshListener {
            refreshData()
            sr_refresh.isRefreshing = false
        }
    }

    private fun refreshData(){
        val oldData = adapter.data

        val newData = ArrayList(oldData).reversed()

        if (newData.size > 1) {
            val expandable = newData[1]
            if (expandable is ImageLabel){
                expandable.getSubItems()?.reverse()
            }
        }
        if (newData.size > 2) {
            val expandable = newData[2]
            if (expandable is ImageLabel){
                expandable.getSubItems()?.reverse()
            }
        }
        adapter.setData(newData)
    }

    private fun initData() {
        val width = resources.displayMetrics.widthPixels

        val data = ArrayList<IMultiItem>()
        labels.forEach {
            data.add(ImageLabel(it))
        }

        // Add Animals
        val animalData = ArrayList<IMultiItem>(2)
        for (i in animalIds.indices) {
            animalData.add(Card(width,animalIds[i],animalTitles[i]))
        }
        (data[0] as ImageLabel).subItems.addAll(animalData)

        // Add Beauties
        val beautyData = ArrayList<IMultiItem>(beautyIds.size)
        for (i in beautyIds.indices) {
            beautyData.add(Card(width, beautyIds[i], beautyTitles[i]))
        }
        (data[1] as ImageLabel).subItems.addAll(beautyData)

        // Add scenery
        val sceneryData = ArrayList<IMultiItem>(sceneryIds.size)
        for (i in sceneryIds.indices) {
            sceneryData.add(Card(width, sceneryIds[i], sceneryTitles[i]))
        }
        (data[2] as ImageLabel).subItems.addAll(sceneryData)

        // Add tranquil
        val tranquilData = ArrayList<IMultiItem>(tranquilIds.size)
        for (i in tranquilIds.indices) {
            tranquilData.add(Card(width, tranquilIds[i], tranquilTitle[i]))
        }
        (data[3] as ImageLabel).subItems.addAll(animalData)

        adapter.setData(data)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.action_grid -> {
                if (isGrid) {
                    return true
                }
                isGrid = true
                rv_list_expand.layoutManager = gridLayoutManager
                adapter.onAttachedToRecyclerView(rv_list_expand)
                initData()
                return true
            }
            R.id.action_staggered -> {
                if (!isGrid) {
                    return true
                }
                isGrid = false
                rv_list_expand.layoutManager = staggeredGridLayoutManager
                adapter.onAttachedToRecyclerView(rv_list_expand)
                initData()
                return true
            }
            R.id.action_clear -> adapter.setData(null)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.multiitem_menu, menu)
        return true
    }
}

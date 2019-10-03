package cn.sherlockzp.sample

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import cn.sherlockzp.adapter.BaseAdapter
import cn.sherlockzp.adapter.BaseViewHolder
import cn.sherlockzp.adapter.OnItemClickListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val adapter = object : BaseAdapter<ResolveInfo>() {
        override fun getLayoutRes(index: Int) = R.layout.item_activity

        override fun convert(holder: BaseViewHolder, data: ResolveInfo, index: Int) {
            holder.setText(R.id.tv_activity_title, data.activityInfo.labelRes)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rv_list_activity.layoutManager = LinearLayoutManager(this)
        rv_list_activity.adapter = adapter

        adapter.onItemClickListener = object : OnItemClickListener{
            override fun onItemClick(view: View, adapterPosition: Int) {
                val resolveInfo = adapter.getData(adapterPosition)
                resolveInfo?.let {
                    val intent = Intent()
                    intent.action = "Sherlock"
                    intent.component = ComponentName(it.activityInfo.packageName,it.activityInfo.name)
                    startActivity(intent)
                }
            }
        }

        initData()

        rv_list_activity.postDelayed({
            adapter.doNotifyItemChanged(1)
        },1000)
    }

    private fun initData() {
        val intent = Intent()
        intent.action = "Sherlock"
        val list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        adapter.setData(list)
    }
}

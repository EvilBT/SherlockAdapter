package cn.sherlockzp.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import android.widget.Toast
import cn.sherlockzp.adapter.BaseMultiSelectAdapter
import cn.sherlockzp.adapter.BaseViewHolder
import cn.sherlockzp.adapter.IMultiSelectItem
import cn.sherlockzp.sample.model.Item
import cn.sherlockzp.sample.model.NoSelected
import kotlinx.android.synthetic.main.activity_multi_select_item.*

class MultiSelectItemActivity : AppCompatActivity() {

    private val adapter = object : BaseMultiSelectAdapter(){
        override fun bind(holder: BaseViewHolder, layoutRes: Int) {
            // 设置 cb_select 可点击，不要忘记这一项，另外，记得也要调用super.bind(BaseViewHolder,Int)
            holder.setCheckable(R.id.cb_select)
            super.bind(holder, layoutRes)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_select_item)

        initView()
    }

    private fun initView() {
        with(rv_list_multi_select) {
            layoutManager = GridLayoutManager(this@MultiSelectItemActivity, 3)
            adapter = this@MultiSelectItemActivity.adapter
        }
        val animator = rv_list_multi_select.itemAnimator
        if (animator is SimpleItemAnimator)
            animator.supportsChangeAnimations = false

        adapter.setOnItemCheckChangeListener { view, isChecked, adapterPosition ->
            if (view.id == R.id.cb_select) {
                val item = adapter.getData(adapterPosition)
                if (item != null) {
                    if (isChecked && adapter.getSelectedItems().size > 9) {
                        Toast.makeText(this@MultiSelectItemActivity,"您最多只能选择9个", Toast.LENGTH_SHORT).show()
                        item.checked = false
                        adapter.doNotifyItemChanged(adapterPosition)
                    }
                }
            }
        }

        val data = ArrayList<IMultiSelectItem>()

        for (i in 1..40) {
            data.add(Item(i))
        }

        // 添加一个不可选中的数据在列表上
        data.add(0, NoSelected("我们不一样，不一样"))

        adapter.setData(data)

        bt_select_all.setOnClickListener {
            // 全选中
            adapter.selectAll()
        }

        bt_clear_select.setOnClickListener {
            // 全不选
            adapter.clearSelectAll()
        }

        bt_get_all_select.setOnClickListener {
            // 拿到选中的
            Toast.makeText(this,"一共选中${adapter.getSelectedItems().size}项",Toast.LENGTH_SHORT).show()
        }

        bt_add_data.setOnClickListener {
            adapter.addData(Item(3, true),3)
            Toast.makeText(this,"从[3]中插入一条数据",Toast.LENGTH_SHORT).show()
        }
    }

}

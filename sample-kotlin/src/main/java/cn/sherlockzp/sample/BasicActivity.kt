package cn.sherlockzp.sample

import android.os.Bundle
import android.app.Activity

import kotlinx.android.synthetic.main.activity_basic.*

class BasicActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic)
    }

}

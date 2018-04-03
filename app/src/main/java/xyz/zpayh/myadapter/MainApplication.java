package xyz.zpayh.myadapter;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

import xyz.zpayh.adapter.BaseAdapter;
import xyz.zpayh.adapter.DefaultViewCreator;

/**
 * 文 件 名: MainApplication
 * 创 建 人: 陈志鹏
 * 创建日期: 2017/4/11 22:18
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public class MainApplication extends Application {

    static {
        BaseAdapter.setDefaultViewCreator(new DefaultViewCreator() {
            @Override
            public int getEmptyViewLayout() {
                return R.layout.default_empty;
            }

            @Override
            public int getErrorViewLayout() {
                return R.layout.default_error;
            }

            @Override
            public int getLoadMoreViewLayout() {
                return R.layout.default_loadmore;
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true)
                .build();
        Fresco.initialize(this,config);
    }
}

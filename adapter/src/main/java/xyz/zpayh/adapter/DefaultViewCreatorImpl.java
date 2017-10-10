package xyz.zpayh.adapter;

/**
 * 创建人： zp
 * 创建时间：2017/10/10
 */

public class DefaultViewCreatorImpl implements DefaultViewCreator {
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
}

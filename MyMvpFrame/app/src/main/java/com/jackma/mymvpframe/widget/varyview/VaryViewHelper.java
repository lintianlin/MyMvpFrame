package com.jackma.mymvpframe.widget.varyview;

import android.content.Context;
import android.view.View;

/**
 * IVaryViewHelper
 *
 * @author: mhj
 * @date: 16/4/1 上午9:49
 * view变化接口
 */
public interface VaryViewHelper {

    public abstract View getCurrentLayout();

    public abstract void restoreView();

    public abstract void showLayout(View view);

    public abstract View inflate(int layoutId);

    public abstract Context getContext();

    public abstract View getView();

    public abstract void removeView(View view);

}
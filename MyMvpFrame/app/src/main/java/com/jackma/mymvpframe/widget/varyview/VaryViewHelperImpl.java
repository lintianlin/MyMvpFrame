package com.jackma.mymvpframe.widget.varyview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * VaryViewHelper
 *
 * @author: mhj
 * @date: 16/4/1 上午9:49
 * view变化接口的实现者
 */
public class VaryViewHelperImpl implements VaryViewHelper {
    private View view;
    private ViewGroup parentView;
    private int viewIndex;
    private ViewGroup.LayoutParams params;
    private View currentView;

    public VaryViewHelperImpl(View view) {
        super();
        this.view = view;
    }

    private void init() {
        //获得view的参数
        params = view.getLayoutParams();
        //获得view的父view
        if (view.getParent() != null) {
            parentView = (ViewGroup) view.getParent();
        } else {
            parentView = (ViewGroup) view.getRootView().findViewById(android.R.id.content);
        }
        //获得该父view的孩子的数量
        int count = parentView.getChildCount();
        //获得当前view在父view中的下标位置
        for (int index = 0; index < count; index++) {
            if (view == parentView.getChildAt(index)) {
                viewIndex = index;
                break;
            }
        }

        currentView = view;
    }
    /**
     * 获得当前的view
     *
     * @return
     */
    @Override
    public View getCurrentLayout() {
        return currentView;
    }

    /**
     * 恢复view
     */
    @Override
    public void restoreView() {
        showLayout(view);
    }

    /**
     * 显示view
     *
     * @param view
     */
    @Override
    public void showLayout(View view) {
        if (parentView == null) {
            init();
        }
        this.currentView = view;
        // 如果已经是那个view，那就不需要再进行替换操作了
        if (parentView.getChildAt(viewIndex) != view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
            parentView.removeViewAt(viewIndex);
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            parentView.addView(view, viewIndex, params);
        }
    }

    @Override
    public View inflate(int layoutId) {
        return LayoutInflater.from(view.getContext()).inflate(layoutId, null);
    }

    @Override
    public Context getContext() {
        return view.getContext();
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public void removeView(View view) {
        if (parentView == null) {
            init();
        }
        parentView.removeView(view);
    }
}

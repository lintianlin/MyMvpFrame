/*
 * Copyright (c) 2015 [1076559197@qq.com | tchen0707@gmail.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License”);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jackma.mymvpframe.ui.base;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jackma.mymvpframe.ui.view.base.BaseView;
import com.jackma.mymvpframe.widget.varyview.VaryViewHelperController;
import com.orhanobut.logger.Logger;

import java.lang.reflect.Field;

import butterknife.ButterKnife;


/**
 * BaseLazyFragment 懒加载fragment
 *
 * @author: mhj
 * @date: 16/4/6 下午4:15
 */
public abstract class BaseFragment extends Fragment implements BaseView {

    /**
     * Log tag
     */
    protected static String TAG_LOG = "mhj";

    /**
     * 手机屏幕信息
     */
    protected int mScreenWidth = 0;
    protected int mScreenHeight = 0;
    protected float mScreenDensity = 0.0f;

    /**
     * context
     */
    protected Context mContext = null;

    private boolean isFirstResume = true;
    private boolean isFirstVisible = true;
    private boolean isFirstInvisible = true;
    private boolean isPrepared;

    private VaryViewHelperController mVaryViewHelperController = null;

    private static Toast toast;


    private static AlertDialog alertDialog;
    private static AlertDialog tokenAlertDialog;
    private static AlertDialog loginTipsDialog;

    public BaseFragment() {
        super();
        if (getArguments() == null)
            setArguments(new Bundle());

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG_LOG = this.getClass().getSimpleName();

    }

    /**
     * onCreateView返回的就是fragment要显示的view。
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getContentViewLayoutID() != 0) {
            return inflater.inflate(getContentViewLayoutID(), null);
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }


    }

    /**
     * onViewCreated在onCreateView执行完后立即执行。
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        if (null != getLoadingTargetView()) {
            Logger.i("getLoadingTargetView():" + getLoadingTargetView());
            mVaryViewHelperController = new VaryViewHelperController(getLoadingTargetView());
        } else {
            Logger.i("getLoadingTargetView()=null");
        }

        /**
         * 获得屏幕的宽度\高度\密度
         */
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        mScreenDensity = displayMetrics.density;
        mScreenHeight = displayMetrics.heightPixels;
        mScreenWidth = displayMetrics.widthPixels;

        //初始化view
        initView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    /**
     * onDetach方法：Fragment和Activity解除关联的时候调用。
     */
    @Override
    public void onDetach() {
        super.onDetach();
        // for bug ---> java.lang.IllegalStateException: Activity has been destroyed
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initPrepare();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstResume) {
            isFirstResume = false;
            return;
        }
        if (getUserVisibleHint()) {
            onFragmentVisible();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            onFragmentInvisible();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //判断这个Fragment的UI是否是可见的
        if (isVisibleToUser) {
            //判断是否是第一次显示
            if (isFirstVisible) {
                isFirstVisible = false;
                initPrepare();
            } else {
                onFragmentVisible();
            }
        } else {//这个Fragment的UI是不可见时
            //判断是否是第一次不可见
            if (isFirstInvisible) {
                isFirstInvisible = false;
                onFirstFragmentInvisible();
            } else {
                onFragmentInvisible();
            }
        }
    }

    private synchronized void initPrepare() {
        //判断是否已经初始化完毕
        if (isPrepared) {

            onFirstFragmentVisible();

        } else {
            isPrepared = true;
        }
    }

    protected abstract void initView();

    /**
     * 当fragment第一次显示的时候,在这个方法里我们可以做一些仅仅执行一次的初始化或者刷新数据的操作
     */
    protected abstract void onFirstFragmentVisible();

    /**
     * 当fragment显示的时候执行的方法,类似fragment的生命周期中的onResume()方法
     */
    protected abstract void onFragmentVisible();

    /**
     * 当fragment第一次隐藏的时候
     */
    private void onFirstFragmentInvisible() {
        // here we do not recommend do something
    }

    /**
     * 这个方法类似fragment的生命周期中的onPause()方法
     */
    protected abstract void onFragmentInvisible();

    /**
     * 获得加载状态时的界面
     */
    protected abstract View getLoadingTargetView();


    /**
     * 获得布局文件的id
     *
     * @return id of layout resource
     */
    protected abstract int getContentViewLayoutID();


    /**
     * 获得 support fragment manager
     *
     * @return
     */
    protected FragmentManager getSupportFragmentManager() {
        return getActivity().getSupportFragmentManager();
    }



    /**
     * 显示加载状态界面
     *
     * @param toggle
     */
    protected void toggleShowLoading(boolean toggle, String msg) {
        if (null == mVaryViewHelperController) {
            throw new IllegalArgumentException("You must return a right target view for loading");
        }

        if (toggle) {
            mVaryViewHelperController.showLoading(msg);
        } else {
            mVaryViewHelperController.restore();
        }
    }

    /**
     * 显示为空状态界面
     *
     * @param toggle
     */
    protected void toggleShowEmpty(boolean toggle, String msg, View.OnClickListener onClickListener) {
        if (null == mVaryViewHelperController) {
            throw new IllegalArgumentException("You must return a right target view for loading");
        }

        if (toggle) {
            mVaryViewHelperController.showEmpty(msg, onClickListener);
        } else {
            mVaryViewHelperController.restore();
        }
    }

    /**
     * 显示错误状态界面
     *
     * @param toggle
     */
    protected void toggleShowError(boolean toggle, String msg, View.OnClickListener onClickListener) {
        if (null == mVaryViewHelperController) {
            throw new IllegalArgumentException("You must return a right target view for loading");
        }

        if (toggle) {
            mVaryViewHelperController.showError(msg, onClickListener);
        } else {
            mVaryViewHelperController.restore();
        }
    }

    /**
     * 显示网络错误状态界面
     *
     * @param toggle
     */
    protected void toggleNetworkError(boolean toggle, View.OnClickListener onClickListener) {
        if (null == mVaryViewHelperController) {
            throw new IllegalArgumentException("You must return a right target view for loading");
        }

        if (toggle) {
            mVaryViewHelperController.showNetworkError(onClickListener);
        } else {
            mVaryViewHelperController.restore();
        }
    }


    /**
     * 显示错误界面
     *
     * @param msg
     */
    @Override
    public void showError(String msg, View.OnClickListener onClickListener) {
        toggleShowError(true, msg, onClickListener);
    }

    /**
     * 显示异常界面
     *
     * @param msg
     */
    @Override
    public void showEmpty(String msg, View.OnClickListener onClickListener) {
        toggleShowEmpty(true, msg, onClickListener);
    }

    /**
     * 显示网络错误界面
     */
    @Override
    public void showNetError(View.OnClickListener onClickListener) {
        toggleNetworkError(true, onClickListener);
    }


    /**
     * 显示加载进度界面
     *
     * @param msg
     */
    @Override
    public void showLoading(String msg) {
        toggleShowLoading(true, msg);
    }

    /**
     * 隐藏加载界面
     */
    @Override
    public void hideLoading() {
        toggleShowLoading(false, null);
    }

    /**
     * 获得当前显示view
     *
     * @return
     */
    public View getCurrentView() {
        if (null == mVaryViewHelperController) {
            throw new IllegalArgumentException("You must return a right target view for loading");
        }
        return mVaryViewHelperController.getCurrentView();
    }


    public void showLayout(View view) {
        if (null == mVaryViewHelperController) {
            throw new IllegalArgumentException("You must return a right target view for loading");
        }
        mVaryViewHelperController.showLayout(view);
    }

    public void removeView(View view) {
        if (null == mVaryViewHelperController) {
            throw new IllegalArgumentException("You must return a right target view for loading");
        }
        mVaryViewHelperController.removeView(view);
    }


}

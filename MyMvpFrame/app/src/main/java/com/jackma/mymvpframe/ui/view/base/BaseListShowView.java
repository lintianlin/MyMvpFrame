package com.jackma.mymvpframe.ui.view.base;

/**
 * Author : imac
 * Date : 2016 16/9/13
 * Description:
 * <p>
 * 列表页的公用base  view
 */
public interface BaseListShowView {


    /**
     * 显示错误状态
     */
    void showError();

    /**
     * 显示空数据页面
     */
    void showEmpty();

    /**
     * 显示网络错误状态
     */
    void showNetError();


    void navigateToDetail(int position);


    void hideRefresh();

    /**
     * 刷新列表
     */
    void refreshListData();


}

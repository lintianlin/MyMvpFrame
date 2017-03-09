package com.jackma.mymvpframe.widget.varyview;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jackma.mymvpframe.R;
import com.jackma.mymvpframe.utils.netstatus.CommonUtils;


/**
 * VaryViewHelperController
 *
 * @author: mhj
 * @date: 16/4/1 上午9:45
 * view 变化帮助控制器
 */
public class VaryViewHelperController {

    private VaryViewHelper helper;

    public VaryViewHelperController(View view) {
        this(new VaryViewHelperImpl(view));
    }

    public VaryViewHelperController(VaryViewHelper helper) {
        super();
        this.helper = helper;
    }

    /**
     * 展示网络状态错误状态页面
     *
     * @param onClickListener
     */
    public void showNetworkError(View.OnClickListener onClickListener) {
        View layout = helper.inflate(R.layout.message);
        TextView textView = (TextView) layout.findViewById(R.id.message_info);
        textView.setText(helper.getContext().getResources().getString(R.string.common_no_network_msg));

        ImageView imageView = (ImageView) layout.findViewById(R.id.message_icon);
        imageView.setImageResource(R.drawable.ic_exception);

        if (null != onClickListener) {
            layout.setOnClickListener(onClickListener);
        }

        helper.showLayout(layout);
    }


    /**
     * 展示错误状态页面
     *
     * @param errorMsg
     * @param onClickListener
     */
    public void showError(String errorMsg, View.OnClickListener onClickListener) {
        View layout = helper.inflate(R.layout.message);
        TextView textView = (TextView) layout.findViewById(R.id.message_info);
        if (!CommonUtils.isEmpty(errorMsg)) {
            textView.setText(errorMsg);
        } else {
            textView.setText(helper.getContext().getResources().getString(R.string.common_error_msg));
        }

        ImageView imageView = (ImageView) layout.findViewById(R.id.message_icon);
        imageView.setImageResource(R.drawable.ic_error);

        if (null != onClickListener) {
            layout.setOnClickListener(onClickListener);
        }

        helper.showLayout(layout);
    }

    /**
     * 展示空页面状态页面
     *
     * @param emptyMsg
     * @param onClickListener
     */
    public void showEmpty(String emptyMsg, View.OnClickListener onClickListener) {
        View layout = helper.inflate(R.layout.message);
        TextView textView = (TextView) layout.findViewById(R.id.message_info);
        if (!CommonUtils.isEmpty(emptyMsg)) {
            textView.setText(emptyMsg);
        } else {
            textView.setText(helper.getContext().getResources().getString(R.string.common_empty_msg));
        }

        ImageView imageView = (ImageView) layout.findViewById(R.id.message_icon);
        imageView.setImageResource(R.drawable.ic_exception);

        if (null != onClickListener) {
            layout.setOnClickListener(onClickListener);
        }

        helper.showLayout(layout);
    }

    /**
     * 展示加载状态页面
     *
     * @param msg
     */
    public void showLoading(String msg) {
        View layout = helper.inflate(R.layout.loading);
        if (!CommonUtils.isEmpty(msg)) {
            TextView textView = (TextView) layout.findViewById(R.id.loading_msg);
            textView.setText(msg);
        }
        helper.showLayout(layout);
    }

    /**
     * 恢复还原view
     */
    public void restore() {
        helper.restoreView();
    }

    /**
     * 获得当前显示的view
     *
     * @return
     */
    public View getCurrentView() {
        return helper.getCurrentLayout();
    }

    public void showLayout(View view) {

        helper.showLayout(view);
    }

    public void removeView(View view) {

        helper.removeView(view);
    }


}

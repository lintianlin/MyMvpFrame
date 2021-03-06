package com.jackma.mymvpframe.ui.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.jackma.mymvpframe.R;
import com.jackma.mymvpframe.ui.view.base.BaseView;
import com.jackma.mymvpframe.utils.ToastUtils;
import com.jackma.mymvpframe.utils.netstatus.CommonUtils;
import com.jackma.mymvpframe.utils.netstatus.NetChangeObserver;
import com.jackma.mymvpframe.utils.netstatus.NetStateReceiver;
import com.jackma.mymvpframe.utils.netstatus.NetUtils;
import com.jackma.mymvpframe.widget.varyview.VaryViewHelperController;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import butterknife.ButterKnife;


/**
 * BaseActivity
 *
 * @author: mhj
 * @date: 17/3/8 下午5:58
 * activity 父类
 */
public abstract class BaseActivity extends AppCompatActivity implements BaseView {

    private static final int NUM_OF_ITEMS = 100;
    private static final int NUM_OF_ITEMS_FEW = 3;


    /**
     * Log tag
     */
    protected static String TAG_LOG = null;

    /**
     * 屏幕信息
     */
    protected int mScreenWidth = 0;
    protected int mScreenHeight = 0;
    protected float mScreenDensity = 0.0f;

    /**
     * context
     */
    protected Context mContext = null;

    /**
     * 网络状态
     */
    protected NetChangeObserver mNetChangeObserver = null;

    /**
     * 加载view的控制器
     */
    private VaryViewHelperController mVaryViewHelperController = null;

    /**
     * toolbar上资源ID
     */
    private int leftIconResource;
    private String leftTextResource = "";
    protected int titleTextResource;
    private int rightTextResource;
    private int rightIconResounce;

    protected Toolbar mToolbar;
    private TextView mToolbar_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mContext = this;
        //log tag名称
        TAG_LOG = this.getClass().getSimpleName();
        //将activity添加到activity列表中
        BaseAppManager.getInstance().addActivity(this);
        //设置全屏
        setNoTitleFullScreen(isNoTitleFullScreen());
        /**
         * 设置activity布局文件的ID
         */
        if (getContentViewLayoutID() != 0) {
            setContentView(getContentViewLayoutID());
        } else {
            throw new IllegalArgumentException("You must return a right contentView layout resource Id");
        }

        // base setup
        Bundle extras = getIntent().getExtras();
        if (null != extras) {
            getBundleExtras(extras);
        }


        if (isApplyKitKatTranslucency()) {
            setSystemBarTintDrawable(CommonUtils.getDrawable(R.drawable.sr_primary));
        }

        /**
         * 观察网络的改变
         */
        mNetChangeObserver = new NetChangeObserver() {
            @Override
            public void onNetConnected(NetUtils.NetType type) {
                super.onNetConnected(type);
                onNetworkConnected(type);
            }

            @Override
            public void onNetDisConnect() {
                super.onNetDisConnect();
                onNetworkDisConnected();
            }
        };

        NetStateReceiver.registerObserver(mNetChangeObserver);


        //获得屏幕的 宽度 和 高度 以及 密度
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        mScreenDensity = displayMetrics.density;
        mScreenHeight = displayMetrics.heightPixels;
        mScreenWidth = displayMetrics.widthPixels;


        //初始化view
        initView();


    }


    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        /**
         * butterknife注解
         */
        ButterKnife.bind(this);
        if (null != getLoadingTargetView()) {
            mVaryViewHelperController = new VaryViewHelperController(getLoadingTargetView());
        }
    }

    /**
     * 设置全屏
     *
     * @param b
     */
    public void setNoTitleFullScreen(boolean b) {
        if (b) {//全屏
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {

            case R.id.toolbar_right_button:
                onRightButtonOnClickListener();

                break;
            case android.R.id.home:
                onLeftButtonOnClickListener();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }


    /**
     * 是否显示toolbar
     */
    public void isShowToolbar(boolean isshow) {
        if (isshow) {
            mToolbar.setVisibility(View.VISIBLE);
        } else {
            mToolbar.setVisibility(View.GONE);
        }

    }

    /**
     * 设置自定义toolbar中的控件
     *
     * @param leftIcon  左侧图标ID
     * @param titleText title ID
     * @param rightIcon 右侧图标ID
     * @param rightText 右侧文字ID
     *                  <p/>
     *                  1.当ID为0的时候,表示不显示该控件
     */
    public void setCustomToolbar(int leftIcon, int titleText, int rightIcon, int rightText) {
        this.leftIconResource = leftIcon;
        this.titleTextResource = titleText;
        this.rightIconResounce = rightIcon;
        this.rightTextResource = rightText;

        //初始化toolbar
        initToolbar();
    }


    /**
     * 初始化toolbar
     */
    private void initToolbar() {

        mToolbar = (Toolbar) findViewById(R.id.common_toolbar);
        //title
        mToolbar_title = (TextView) findViewById(R.id.common_toolbar_title);

        if (null != mToolbar) {
            mToolbar.setTitle("");
            //设置toolbar的title
            if (0 != titleTextResource) {
                mToolbar_title.setText(titleTextResource);
            }
            setSupportActionBar(mToolbar);
            ActionBar actionBar = getSupportActionBar();
            //左侧返回按钮
            if (actionBar != null) {
                if (0 != leftIconResource) {
                    actionBar.setDisplayShowHomeEnabled(true);
                    actionBar.setDisplayHomeAsUpEnabled(true);
                } else {
                    actionBar.setDisplayShowHomeEnabled(false);
                    actionBar.setDisplayHomeAsUpEnabled(false);
                }
            }
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLeftButtonOnClickListener();
                }
            });
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.toolbar_right_button);
        if (0 == rightIconResounce && 0 == rightTextResource) {
            item.setEnabled(false);
        } else {
            item.setEnabled(true);
        }
        //设置右侧按钮文字,    1.当0的时候右侧文字不显示   2.当不为0的时候显示
        if (0 != rightTextResource) {
            item.setTitle(rightTextResource);
        }
        //设置右侧按钮图标,    1.当0的时候右侧图标不显示   2.当不为0的时候显示
        if (0 != rightIconResounce) {
            item.setIcon(rightIconResounce);
        }

        return true;
    }

    @Override
    public void finish() {
        super.finish();
        //关闭时将activity移除activity链表
        BaseAppManager.getInstance().removeActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //移除网络状态观察者
        NetStateReceiver.removeRegisterObserver(mNetChangeObserver);
    }


    /**
     * 获得activiy的布局文件id
     *
     * @return id of layout resource
     */
    protected abstract int getContentViewLayoutID();

    /**
     * 初始化view
     */
    protected abstract void initView();


    /**
     * toolbar右边按钮的点击事件处理
     */
    public abstract void onRightButtonOnClickListener();

    /**
     * toolbar左边按钮的点击事件处理
     */
    public abstract void onLeftButtonOnClickListener();


    /**
     * 获得 bundle 数据
     *
     * @param extras
     */
    protected abstract void getBundleExtras(Bundle extras);

    /**
     * get loading target view
     */
    protected abstract View getLoadingTargetView();


    /**
     * 网络已连接
     */
    protected abstract void onNetworkConnected(NetUtils.NetType type);

    /**
     * 网络未连接
     */
    protected abstract void onNetworkDisConnected();


    /**
     * 全屏
     *
     * @return
     */
    protected abstract boolean isNoTitleFullScreen();


    /**
     * 显示加载页面
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
     * 显示为空页面
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
     * 显示错误页面
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
     * 显示网络加载错误页面
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


    protected void showLayout(View view) {
        if (null == mVaryViewHelperController) {
            throw new IllegalArgumentException("You must return a right target view for loading");
        }
        mVaryViewHelperController.showLayout(view);
    }


    /**
     * 使用 SytemBarTintManager
     *
     * @param tintDrawable
     */
    protected void setSystemBarTintDrawable(Drawable tintDrawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
//        SystemBarTintManager mTintManager = new SystemBarTintManager(this);
//        if (tintDrawable != null) {
//            mTintManager.setStatusBarTintEnabled(true);
//            mTintManager.setNavigationBarTintEnabled(true);
//            mTintManager.setStatusBarTintResource(R.color.colorAccent);
//        } else {
//            mTintManager.setStatusBarTintEnabled(false);
//            mTintManager.setTintDrawable(null);
//        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.colorAccent);//通知栏所需颜色

    }


    /**
     * set status bar translucency
     *
     * @param on
     */
    @TargetApi(19)
    protected void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
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
     * 显示空数据界面
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
        toggleShowLoading(true, null);
    }

    /**
     * 隐藏加载界面
     */
    @Override
    public void hideLoading() {
        toggleShowLoading(false, null);
    }

    /**
     * 设置状态栏颜色
     *
     * @return
     */
    protected abstract boolean isApplyKitKatTranslucency();


    //------------小工具----------------------------

    private long exitTime;

    /**
     * 点击两次返回按键退出程序
     */
    public void exitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtils.showToast("再按一次退出");
            exitTime = System.currentTimeMillis();
        } else {
            BaseAppManager.getInstance().clear();
            //退出杀死进程
//            android.os.Process.killProcess(android.os.Process.myPid());
//            System.exit(0);
            finish();
        }
    }


    private static long lastClickTime;

    /**
     * 防止重复点击
     *
     * @return
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 1500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    //------------小工具----------------------------

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }


//=====================================================================================================


    protected int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}

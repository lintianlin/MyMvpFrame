

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.WindowManager;
import android.widget.TextView;


import com.jackma.mymvpframe.app.MyApplication;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * CommonUtils
 *
 * @author: mhj
 * @date: 16/3/30 下午5:47
 * 公共方法工具类
 */
public class CommonUtils {

    /**
     * 在主线程执行任务
     *
     * @param r
     */
    public static void runOnUIThread(Runnable r) {
        MyApplication.getMainHandler().post(r);
    }

    /**
     * 获取Resource对象
     *
     * @return
     */
    public static Resources getResources() {
        return MyApplication.getContext().getResources();
    }

    /**
     * 获取drawable资源
     *
     * @param resId
     * @return
     */
    public static Drawable getDrawable(int resId) {
        return ContextCompat.getDrawable(MyApplication.getContext(), resId);
    }

    /**
     * 获取字符串资源
     *
     * @param resId
     * @return
     */
    public static String getString(int resId) {
        return getResources().getString(resId);
    }

    /**
     * 获取color资源
     *
     * @param resId
     * @return
     */
    public static int getColor(int resId) {
        return ContextCompat.getColor(MyApplication.getContext(), resId);
    }

    /**
     * 获取dimens资源,就是dp值
     *
     * @param resId
     * @return
     */
    public static float getDimens(int resId) {
        return getResources().getDimension(resId);
    }

    /**
     * 获取字符串数组资源
     *
     * @param resId
     * @return
     */
    public static String[] getStringArray(int resId) {
        return getResources().getStringArray(resId);
    }


    public static int getInteger(int resId) {
        return getResources().getInteger(resId);
    }

    /**
     * 获得屏幕宽度
     *
     * @return
     */
    public static int getWindowWidth() {
        WindowManager wm = (WindowManager) MyApplication.getContext().getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }

    /**
     * 获得屏幕高度
     *
     * @return
     */
    public static int getWindowHeight() {
        WindowManager wm = (WindowManager) MyApplication.getContext().getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        return height;
    }


    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (str == null || str.length() == 0 || str.equalsIgnoreCase("null") || str.isEmpty() || str.equals("")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 日期格式转换
     *
     * @param timemillis
     * @return
     */
    public static String getFormatDate(long timemillis) {
        return new SimpleDateFormat("yyyy年MM月dd日").format(new Date(timemillis));
    }

    /**
     * 字符串转码
     *
     * @param s
     * @return
     */

    public static String decodeUnicodeStr(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '\\' && chars[i + 1] == 'u') {
                char cc = 0;
                for (int j = 0; j < 4; j++) {
                    char ch = Character.toLowerCase(chars[i + 2 + j]);
                    if ('0' <= ch && ch <= '9' || 'a' <= ch && ch <= 'f') {
                        cc |= (Character.digit(ch, 16) << (3 - j) * 4);
                    } else {
                        cc = 0;
                        break;
                    }
                }
                if (cc > 0) {
                    i += 5;
                    sb.append(cc);
                    continue;
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * 字符串解码
     *
     * @param s
     * @return
     */
    public static String encodeUnicodeStr(String s) {
        StringBuilder sb = new StringBuilder(s.length() * 3);
        for (char c : s.toCharArray()) {
            if (c < 256) {
                sb.append(c);
            } else {
                sb.append("\\u");
                sb.append(Character.forDigit((c >>> 12) & 0xf, 16));
                sb.append(Character.forDigit((c >>> 8) & 0xf, 16));
                sb.append(Character.forDigit((c >>> 4) & 0xf, 16));
                sb.append(Character.forDigit((c) & 0xf, 16));
            }
        }
        return sb.toString();
    }

    /**
     * 时间转字符串
     *
     * @param time
     * @return
     */
    public static String convertTime(int time) {

        time /= 1000;
        int minute = time / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second);
    }

    /**
     * 判断URL时候能够访问
     *
     * @param url
     * @return
     */
    public static boolean isUrlUsable(String url) {
        if (CommonUtils.isEmpty(url)) {
            return false;
        }

        URL urlTemp = null;
        HttpURLConnection connt = null;
        try {
            urlTemp = new URL(url);
            connt = (HttpURLConnection) urlTemp.openConnection();
            connt.setRequestMethod("HEAD");
            int returnCode = connt.getResponseCode();
            if (returnCode == HttpURLConnection.HTTP_OK) {
                return true;
            }
        } catch (Exception e) {
            return false;
        } finally {
            connt.disconnect();
        }
        return false;
    }

    /**
     * 判断是否时URL
     *
     * @param url
     * @return
     */
    public static boolean isUrl(String url) {
        Pattern pattern = Pattern.compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+$");
        return pattern.matcher(url).matches();
    }

    /**
     * 获得 toolbar 的高度
     *
     * @param context
     * @return
     */
    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }


    /**
     * 判断手机号格式
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNum(String mobiles) {
        Pattern p = Pattern
//                .compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
                .compile("^13[\\d]{9}$|^14[5,7]{1}\\d{8}$|^15[^4]{1}\\d{8}$|^17[0,6,7,8]{1}\\d{8}$|^18[\\d]{9}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();

    }


   


    public static String getCurrentTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        String date = sDateFormat.format(new Date());
        return date;
    }



    /**
     * 获取状态栏的高度
     * @param acitivity
     * @return
     */
    public static int getStatusBarHeight(Activity acitivity){

        int resourceId = acitivity.getResources().getIdentifier("status_bar_height", "dimen", "android");

        return acitivity.getResources().getDimensionPixelOffset(resourceId);
    }


    /**
     *  textview设置部分文字点击事件
     * @param textView
     * @param content
     * @param clickText
     * @param textClick
     */
    public static void setTextClick(TextView textView,String content,String clickText,ClickableSpan textClick){
//        String content = "点击click文字";
//        String clickTxt = "click";
        textView.setText(content);

        SpannableStringBuilder spannable = new SpannableStringBuilder(content);
        int startIndex = content.indexOf(clickText);
        int endIndex = startIndex + clickText.length();
        //文字点击
        spannable.setSpan(textClick,startIndex,endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //一定要记得设置，不然点击不生效
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spannable);
    }


    /**
     * 设置部分文字颜色
     * @param textView
     * @param content
     * @param clickText
     * @param color
     */
    public static void setTextColor(TextView textView,String content,String clickText,int color ){
        textView.setText(content);
        SpannableStringBuilder spannable = new SpannableStringBuilder(content);
        int startIndex = content.indexOf(clickText);
        int endIndex = startIndex + clickText.length();
        //文字变色
        spannable.setSpan(new ForegroundColorSpan(color),startIndex,endIndex
                , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //一定要记得设置，不然点击不生效
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spannable);
    }


}

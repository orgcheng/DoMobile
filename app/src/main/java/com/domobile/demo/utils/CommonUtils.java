package com.domobile.demo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.TypedValue;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2018/4/13.
 */

public class CommonUtils {
    public static void loadIcon(final Context context, ImageView imageView, final String url) {
        Glide.with(context).load(url)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        Toast.makeText(context, "网站没有有效图标", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(imageView);
    }

    private static final String DEFAULT_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_3 like" +
            "Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko)" +
            "Version/10.3 Mobile/14E277 Safari/603.1.30";

    public static String pareHtml(Context context, final String url, String html) {
        try {
            long startTime = System.nanoTime();
            Document doc = Jsoup.parse(html);
//            Document doc = Jsoup.connect(url)
//                    .userAgent(DEFAULT_AGENT)
//                    .get();
            Element head = doc.head();

            // 使用css类型的查询选择器
            Elements links = head.select("link[rel='shortcut icon'],link[rel^='apple-touch-icon']");

            long endTime = System.nanoTime();
            Log.e("DoMobile", String.format(Locale.getDefault(), "解析html耗时：%.1fms%n", (endTime - startTime) / 1e6d));
            Toast.makeText(context, String.format(Locale.getDefault(), "解析html耗时：%.1fms%n", (endTime - startTime) / 1e6d), Toast.LENGTH_SHORT).show();
            if (links == null || links.size() == 0) {
                return null;
            }

            Element element = links.get(0);
            String href = element.attr("href");

            // 边界情况处理
            if (URLUtil.isNetworkUrl(href)) {
                return href;
            }
            if (href.startsWith("//") && URLUtil.isHttpsUrl(url)) {
                return "https:" + href;
            } else if (href.startsWith("//") && URLUtil.isHttpsUrl(url)) {
                return "http:" + href;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean needUpdateExchangeRate(Context context) {
        String updateDate = getExchangeRateUpdateDate(context);
        Log.e("DoMobile", "udpateDate = " + updateDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(new Date(System.currentTimeMillis()));
        Log.e("DoMobile", "currentDate = " + updateDate);
        if (!currentDate.equals(updateDate)) {
            return true;
        }
        return false;
    }

    public static String getExchangeRateUpdateDate(Context context) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences("domobile", Context.MODE_PRIVATE);
        return sp.getString("update_date", "");
    }

    public static void setExchangeRateUpdateDate(Context context, String updateDate) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences("domobile", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("updateDate", updateDate);
        edit.apply();
    }

    public static int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int pxToDip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}

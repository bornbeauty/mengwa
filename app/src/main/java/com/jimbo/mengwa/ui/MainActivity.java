package com.jimbo.mengwa.ui;

import android.os.Bundle;
import android.widget.TextView;

import com.jimbo.mengwa.Config;
import com.jimbo.mengwa.R;
import com.jimbo.mengwa.data.Image;
import com.jimbo.mengwa.ui.base.BaseActivity;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

public class MainActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtils.inject(this);
        getData();
    }

    /**
     * 获取数据
     */
    private void getData() {
        HttpUtils http = new HttpUtils(3000);
        http.send(
                HttpRequest.HttpMethod.GET,
                Config.URL,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        parseHtml(responseInfo.result);
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {

                    }
                });
    }

    /**
     * 解析保存数据
     * @param html html
     */
    private void parseHtml(String html) {
        Image image = new Image();
        DbUtils dbUtils = DbUtils.create(MainActivity.this);
        try {
            Document document = Jsoup.parse(html);
            Elements lis = document.getElementsByClass("qqhead_list_box");
            for (Element li : lis) {
                image.title = li.getElementsByTag("h3").text();
                image.thumbnailUrl = li.select("img.lazy").attr("original");
                image.time = li.select("div.l").select("span").text();
                image.url = li.select("h3 > a").attr("href");
                dbUtils.save(image);
            }
        } catch (Exception e) {
        }
        tt();
    }

    private void tt() {
        DbUtils dbUtils = DbUtils.create(MainActivity.this);
        String t="";
        try {
            List<Image> image = dbUtils.findAll(Image.class);
            int g = 0;
            for (Image i : image) {
                t += ++g + i.title+"\n";
            }

        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}

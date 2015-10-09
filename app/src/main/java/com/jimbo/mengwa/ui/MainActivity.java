package com.jimbo.mengwa.ui;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.jimbo.mengwa.Config;
import com.jimbo.mengwa.R;
import com.jimbo.mengwa.data.Image;
import com.jimbo.mengwa.ui.adapter.MengWaWaRecycleAdapter;
import com.jimbo.mengwa.ui.base.BaseActivity;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends BaseActivity {

    @ViewInject(R.id.mengWaWaRecyclerView)
    RecyclerView mMengWaWaRecyler;

    MengWaWaRecycleAdapter adapter;

    @ViewInject(R.id.toolbar)
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtils.inject(this);

        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        tt();

        getData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
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
     *
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

                Pattern p = Pattern.compile("[^0-9]");
                Matcher m = p.matcher(image.url);
                image.shunxu = Integer.valueOf(m.replaceAll(""));
                System.out.println(image.shunxu);
                dbUtils.save(image);
            }
        } catch (Exception e) {
        }
        tt();
    }

    private void tt() {
        DbUtils dbUtils = DbUtils.create(MainActivity.this);
        try {
            List<Image> image = dbUtils.findAll(Selector.from(Image.class).orderBy("shunxu", true));
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(
                    2, StaggeredGridLayoutManager.VERTICAL
            );
            if (null == image) {
                Toast.makeText(MainActivity.this, "cuowu", Toast.LENGTH_SHORT).show();
                return;
            }
            adapter = new MengWaWaRecycleAdapter(this, image);
            mMengWaWaRecyler.setLayoutManager(layoutManager);
            mMengWaWaRecyler.setAdapter(adapter);

        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}

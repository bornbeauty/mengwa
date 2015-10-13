package com.jimbo.mengwa.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.jimbo.mengwa.widget.MultiSwipeRefreshLayout;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends BaseActivity {

    @ViewInject(R.id.test)
    TextView tv_test;
    String string_test = "";

    @ViewInject(R.id.mengWaWaRecyclerView)
    RecyclerView mMengWaWaRecyler;

    @ViewInject(R.id.refreshLayout)
    MultiSwipeRefreshLayout mRefreshLayout;

    //适配器
    MengWaWaRecycleAdapter mMengWaWaAdapter;

    //布局管理器
    StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(
            2, StaggeredGridLayoutManager.VERTICAL
    );

    //记录页面数量
    private static int mPageConut = 0;
    //image
    List<Image> mImages = new ArrayList<>();

    public static MainActivity mMainActivity;

    @ViewInject(R.id.toolbar)
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMainActivity = this;

        ViewUtils.inject(this);

        init();

    }

    private void init() {

        //这个时候是空白数据
        mMengWaWaAdapter = new MengWaWaRecycleAdapter(this, mImages);
        mMengWaWaRecyler.setLayoutManager(layoutManager);
        mMengWaWaRecyler.setAdapter(mMengWaWaAdapter);

        //设置toolbar
        mToolbar.setTitle(R.string.app_name);
        setSupportActionBar(mToolbar);

        //添加刷新事件
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startRefreshing();
                stopRefreshing();
            }
        });

        //加载缓存数据
        getDataFromDB();

        //同时向网络请求最新的数据 在保存成功后再次刷新页面
        //刷新动画
        getDataNet();

    }

    public void loadNextPage() {
        // 开启刷新动画
        startRefreshing();
        mPageConut++;
        getDataNet();
    }

    /**
     * 获取数据
     */
    private void getDataNet() {
        String url = Config.URL;
        if (0 != mPageConut) {
            url = url + "list_" + (mPageConut + 1) + ".html";
        }
        HttpUtils http = new HttpUtils(3000);
        http.send(
                HttpRequest.HttpMethod.GET,
                url,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        parseHtml(responseInfo.result);
                        //System.out.println(responseInfo.result);
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

        if (0 == mPageConut) {
            mImages.clear();
        }


        DbUtils dbUtils = DbUtils.create(MainActivity.this);
        try {
            Document document = Jsoup.parse(html);
            Elements lis = document.getElementsByClass("qqhead_list_box");

            int i = 0;

            for (Element li : lis) {
                Image image = new Image();
                image.title = li.getElementsByTag("h3").text();
                image.thumbnailUrl = li.select("img.lazy").attr("original");
                image.time = li.select("div.l").select("span").text();
                image.url = li.select("h3 > a").attr("href");

                //System.out.println(++i + "----" + image.thumbnailUrl);

                //获取到图片url中的图片编号
                Pattern p = Pattern.compile("[^0-9]");
                Matcher m = p.matcher(image.url);
                image.shunxu = Integer.valueOf(m.replaceAll(""));
                if (!mImages.contains(image)) {
                    mImages.add(image);
                }
                try {
                    dbUtils.save(image);
                } catch (Exception e) {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        string_test = "前\n";
//        Toast.makeText(MainActivity.this, "qian", Toast.LENGTH_SHORT).show();
        print(mImages, false);
        changeData();
    }

    private void getDataFromDB() {
        DbUtils dbUtils = DbUtils.create(MainActivity.this);
        try {
            List<Image> image = dbUtils.findAll(Selector.from(Image.class).
                    orderBy("shunxu", true));

            if (null == image) {
                Toast.makeText(MainActivity.this, "数据库为空", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                mImages = image.subList(0, 16);
            } catch (Exception e) {
                mImages = image;
            }
            changeData();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    //处理刷新动画
    private void startRefreshing() {
        try {
            mRefreshLayout.setRefreshing(true);
        } catch (NullPointerException e) {
            // do nothing
            //对程序运行没太大影响
        }
    }

    private void stopRefreshing() {
        mRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(false);
            }
        }, 1500);
    }

    private void changeData() {
        string_test = "后\n";
        print(mImages, false);
        mMengWaWaAdapter.setImages(mImages);
        mMengWaWaAdapter.notifyDataSetChanged();
        stopRefreshing();
    }

    //测试使用
    private void print(List<Image> l, boolean is) {
        int c = 0;
        for (Image i : l) {
            string_test += ++c + i.thumbnailUrl + "\n";
        }
        if (is) {
            tv_test.setText(string_test);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //在activity彻底运行起来后开启刷新
        startRefreshing();
        stopRefreshing();
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
}

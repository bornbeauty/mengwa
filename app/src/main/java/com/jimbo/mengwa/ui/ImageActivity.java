package com.jimbo.mengwa.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jimbo.mengwa.Config;
import com.jimbo.mengwa.R;
import com.jimbo.mengwa.data.Image;
import com.jimbo.mengwa.ui.adapter.MengWaWaRecycleAdapter;
import com.jimbo.mengwa.ui.base.BaseActivity;
import com.jimbo.mengwa.ui.fragment.ImageDetailFragment;
import com.jimbo.mengwa.utils.ShareUtils;
import com.jimbo.mengwa.widget.HackyViewPager;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 大图Activity
 * Created by Administrator on 2015/10/11.
 */
public class ImageActivity extends BaseActivity {

    private static final String STATE_POSITION = "STATE_POSITION";

    private final String IMAGE_PATH = Environment.getExternalStorageDirectory() + "/mengwa";

    @ViewInject(R.id.toolbar)
    Toolbar mToolbar;

    @ViewInject(R.id.pager)
    private HackyViewPager mPager;

    @ViewInject(R.id.progress)
    private TextView mProgress;

    private int pagerPosition;

    private int mCurrentImage = 0;

    @ViewInject(R.id.progressBar)
    ProgressBar mProgressBar;

    Image image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image);

        ViewUtils.inject(this);

        //显示进度条
        mProgressBar.setVisibility(View.VISIBLE);

        image = (Image) getIntent().getSerializableExtra(MengWaWaRecycleAdapter.IMAGE_NAME);
        if (null == image) {
            Toast.makeText(ImageActivity.this, "程序错误 请重试", Toast.LENGTH_SHORT).show();
            return;
        }

        if (savedInstanceState != null) {
            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
        }

        init();

        //拿到图片地址
//        ArrayList<String> imagesUrl = getUrl();
//
//        if (null == imagesUrl) {
//            getUrlFromNet();
//        } else {
//            Toast.makeText(ImageActivity.this, "chengle", Toast.LENGTH_SHORT).show();
//            setAdapterToPager(imagesUrl);
//        }

        getUrlFromNet();

    }

    private void setAdapterToPager(ArrayList<String> urls) {

        ImagePagerAdapter mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), urls);

        mPager.setAdapter(mAdapter);
        CharSequence text = getString(R.string.viewpager_indicator, 1, mPager.getAdapter().getCount());
        mProgress.setText(text);

        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int arg0) {
                mCurrentImage = arg0;
                CharSequence text = getString(R.string.viewpager_indicator, arg0 + 1, mPager.getAdapter().getCount());
                mProgress.setText(text);
                System.out.println("tt--"+image.detailUrl.get(arg0)+"-"+arg0);
            }

        });

        mPager.setCurrentItem(pagerPosition);

    }

//    private void saveUrls(final ArrayList<String> urls) {
//        System.out.println("test-开始");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("test-子线程开始");
//                DbUtils db = DbUtils.create(ImageActivity.this);
//                System.out.println("test-创建成功");
//                image.detailUrl = urls;
//                System.out.println("test-"+image.toString());
//                try {
//                    db.update(image);
//                    System.out.println("test-"+ getUrl());
//                } catch (DbException e) {
//                    System.out.println("test-更新出错");
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, mPager.getCurrentItem());
    }


    private void init() {
        //toolbar初始化工作
        mToolbar.setTitle(image.title);
        mToolbar.setNavigationIcon(R.mipmap.ssdk_back_arr);
        setSupportActionBar(mToolbar);
    }


    public ArrayList<String> getUrl() {
        System.out.println("test-formDB-start");
        DbUtils dbUtils = DbUtils.create(this);
        System.out.println("test-formDB-end");
        try {
            System.out.println("test-formDB");
            Image newImage = dbUtils.findById(Image.class, image.url);
            //Toast.makeText(ImageActivity.this, image.url+image.detailUrl, Toast.LENGTH_SHORT).show();
            if (null == newImage || null == newImage.detailUrl) {
                System.out.println("test-formDB-kong");
                getUrlFromNet();
            } else {
                return newImage.detailUrl;
            }
        } catch (DbException e) {
            System.out.println("test-formDB-yichang");
            getUrlFromNet();
        }
        return null;
    }

    public void getUrlFromNet() {

        HttpUtils httpUtils = new HttpUtils(3000);

        httpUtils.send(
                HttpRequest.HttpMethod.GET,
                Config.BASE_URL + image.url,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        //try {
                            parseHtml(responseInfo.result);
                        //} catch (Exception e) {
                          //  e.printStackTrace();
                        //}
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {

                    }
                }
        );

    }

    private void parseHtml(String result) {
//        System.out.println(result);
        Document document = Jsoup.parse(result);
        Elements es = document.getElementsByClass("lazy");
        ArrayList<String> urls = new ArrayList<>();
        for (Element e : es) {
            urls.add(e.attr("original"));
//            System.out.println(e.attr("original"));
        }

        try {
            urls.remove(urls.size() - 1);
            urls.remove(urls.size() - 1);
            image.detailUrl = urls;
        } catch (Exception e) {
            e.printStackTrace();
        }

        setAdapterToPager(urls);
        mProgressBar.setVisibility(View.GONE);
        System.out.println("test-调用save");
        //saveUrls(urls);
    }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.action_share:
//                Toast.makeText(ImageActivity.this, image.shunxu+"分享", Toast.LENGTH_SHORT).show();
                saveImageToGallery(new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        if (0 == msg.what) {
                            Toast.makeText(ImageActivity.this, "我现在心情不好,不想见别人,你待会试试吧...", Toast.LENGTH_SHORT).show();
                        } else {
                            ShareUtils.shareImage(ImageActivity.this, (Uri)msg.obj, "分享给好友");
                        }
                        return true;
                    }
                }));
                break;
            
            case R.id.action_save:
//                Toast.makeText(ImageActivity.this, "保存", Toast.LENGTH_SHORT).show();
                saveImageToGallery(new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        if (0 == msg.what) {
                            Toast.makeText(ImageActivity.this, "保存失败啦,要不你再试试...", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ImageActivity.this, "以保存图片到"+IMAGE_PATH+"去.", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                }));
                break;
        }

        return true;
    }

    private class ImagePagerAdapter extends FragmentStatePagerAdapter {

        public ArrayList<String> fileList;

        public ImagePagerAdapter(FragmentManager fm, ArrayList<String> fileList) {
            super(fm);
            this.fileList = fileList;
        }

        @Override
        public int getCount() {
            return fileList == null ? 0 : fileList.size();
        }

        @Override
        public Fragment getItem(int position) {
            String url = fileList.get(position);
            return ImageDetailFragment.newInstance(url);
        }

    }

    private void saveImageToGallery(final Handler handler){
        final Bitmap bitmap = ImageDetailFragment.bitmapUtils.getBitmapFromMemCache(image.detailUrl.get(mCurrentImage), null);
        if (null != bitmap) {
            new Runnable() {
                @Override
                public void run() {
                    File dir = new File(IMAGE_PATH);
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    String fileName = image.title.replace('/', '-') + mCurrentImage + ".jpg";
                    File file = new File(dir, fileName);
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(0);
                    } finally {
                        if (null != fos) {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    Uri uri = Uri.fromFile(file);
                    // 通知图库更新
                    Intent scannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
                    ImageActivity.this.sendBroadcast(scannerIntent);
                    Message m = Message.obtain();
                    m.what = 1;
                    m.obj = uri;
                    handler.sendMessage(m);
                }
            }.run();
        } else {
            Toast.makeText(ImageActivity.this, "保存失败啦,要不你再试试...", Toast.LENGTH_SHORT).show();
        }
    }

}

package com.jimbo.mengwa.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jimbo.mengwa.R;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.view.annotation.ViewInject;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 *
 * Created by Administrator on 2015/10/11.
 */
public class ImageDetailFragment extends Fragment {

    @ViewInject(R.id.image)
    private ImageView mImageView;
    @ViewInject(R.id.loading)
    private ProgressBar progressBar;
    private PhotoViewAttacher mAttacher;
    private String mImageUrl;

    public static BitmapUtils bitmapUtils = null;

    public static ImageDetailFragment newInstance(String imageUrl) {
        final Bundle args = new Bundle();
        args.putString("url", imageUrl);
        final ImageDetailFragment fragment = new ImageDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_image, null);

        ViewUtils.inject(this, view);

        mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        mAttacher = new PhotoViewAttacher(mImageView);
        mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {

            @Override
            public void onPhotoTap(View arg0, float arg1, float arg2) {
                getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (null == bitmapUtils) {
            bitmapUtils = new BitmapUtils(getContext());
            bitmapUtils.configDefaultBitmapMaxSize(2048, 2048);
        }

        bitmapUtils.display(mImageView, mImageUrl, new BitmapLoadCallBack<ImageView>() {

            @Override
            public void onLoadStarted(ImageView container, String uri, BitmapDisplayConfig config) {
                super.onLoadStarted(container, uri, config);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadCompleted(ImageView container, String uri, Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
                progressBar.setVisibility(View.GONE);

//                Toast.makeText(getActivity(), "tipian", Toast.LENGTH_SHORT).show();
                container.setScaleType(ImageView.ScaleType.FIT_CENTER);
                container.setImageBitmap(bitmap);
                mAttacher.update();
            }

            @Override
            public void onLoadFailed(ImageView container, String uri, Drawable drawable) {

                Toast.makeText(getActivity(), "图片下载失败", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUrl = getArguments() != null ? getArguments().getString("url") : null;
    }

}

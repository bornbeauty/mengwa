package com.jimbo.mengwa.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jimbo.mengwa.R;
import com.jimbo.mengwa.data.Image;
import com.jimbo.mengwa.ui.ImageActivity;
import com.jimbo.mengwa.ui.MainActivity;
import com.jimbo.mengwa.widget.RatioImageView;
import com.lidroid.xutils.BitmapUtils;

import java.util.List;

/**
 *
 * Created by jimbo on 2015/10/9.
 */
public class MengWaWaRecycleAdapter extends RecyclerView.Adapter<MengWaWaRecycleAdapter.ViewHolder>{

    //0 代表下一页的布局
    //1 代表正常的图片布局
    private static final int TYPE_NEXT = 0;
    private static final int TYPE_NORMAL = 1;

    //参数名称
    public static final String IMAGE_NAME = "IMAGE_NAME";

    private Context mContext;


    private List<Image> images;

    private BitmapUtils bitmapUtils;

    public MengWaWaRecycleAdapter(Context mContext, List<Image> images) {
        this.mContext = mContext;
        this.images = images;
        bitmapUtils = new BitmapUtils(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meng_thuail, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_NORMAL) {
            Image image = images.get(position);
            holder.image = image;
            holder.title.setText(image.title);
            holder.wawa.setImageResource(R.mipmap.ic_launcher);
            bitmapUtils.display(holder.wawa,
                    image.thumbnailUrl);

            System.out.println("adapter-"+image.thumbnailUrl+"-position"+position
                +"count"+getItemCount());

        } else {
            Image image = new Image();
            image.shunxu = -1;
            holder.image = image;
            holder.title.setText("下一页");
            holder.wawa.setImageResource(R.mipmap.next);
        }
    }

    //因为多了一个下一页布局
    @Override
    public int getItemCount() {
        return images.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItemCount() == (position + 1)) {
            return TYPE_NEXT;
        } else {
            return TYPE_NORMAL;
        }
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RatioImageView wawa;
        TextView title;
        Image image;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            wawa = (RatioImageView) itemView.findViewById(R.id.iv_wawa);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            if (-1 == image.shunxu) {

                MainActivity.mMainActivity.loadNextPage();

            } else {
                Intent intent = new Intent(mContext, ImageActivity.class);
                intent.putExtra(IMAGE_NAME, image);
                mContext.startActivity(intent);
            }
        }
    }
}

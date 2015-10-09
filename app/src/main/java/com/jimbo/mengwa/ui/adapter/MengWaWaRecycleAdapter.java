package com.jimbo.mengwa.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jimbo.mengwa.R;
import com.jimbo.mengwa.data.Image;
import com.jimbo.mengwa.widget.RatioImageView;
import com.lidroid.xutils.BitmapUtils;

import java.util.List;

/**
 *
 * Created by jimbo on 2015/10/9.
 */
public class MengWaWaRecycleAdapter extends RecyclerView.Adapter<MengWaWaRecycleAdapter.ViewHolder>{

    private Context mContext;
    private List<Image> images;

    private BitmapUtils bitmapUtils;


    public MengWaWaRecycleAdapter(Context mContext, List<Image> images) {
        this.mContext = mContext;
        this.images = images;
        bitmapUtils = new BitmapUtils(mContext);
//        bitmapUtils.
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meng_thuail, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Image image = images.get(position);
        holder.image = image;
        holder.title.setText(image.time + image.title);
        holder.wawa.setImageResource(R.mipmap.ic_launcher);
        bitmapUtils.display(holder.wawa,
                image.thumbnailUrl);
    }

    @Override
    public int getItemCount() {
        return images.size();
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
            Toast.makeText(mContext, image.shunxu+image.url, Toast.LENGTH_SHORT).show();
        }
    }
}

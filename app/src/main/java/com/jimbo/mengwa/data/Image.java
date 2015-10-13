package com.jimbo.mengwa.data;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页显示的缩略图
 * Created by jimbo on 2015/10/1.
 */
@Table(name = "images")
public class Image implements Serializable{

    @Id
    @Column(column = "url")
    public String url;
    @Column(column = "thumbnailUrl")
    public String thumbnailUrl;
    @Column(column = "title")
    public String title;
    @Column(column = "time")
    public String time;
    @Column(column = "detailUrl")
    public ArrayList<String> detailUrl;
    @Column(column = "shunxu")
    public int shunxu;

    @Override
    public boolean equals(Object o) {
        Image image;
        try {
            image = (Image) o;
        } catch (Exception e) {
            return false;
        }
        return image.url == this.url;
    }

    @Override
    public String toString() {
        return "Image{" +
                "url='" + url + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", title='" + title + '\'' +
                ", time='" + time + '\'' +
                ", detailUrl=" + detailUrl +
                ", shunxu=" + shunxu +
                '}';
    }
}

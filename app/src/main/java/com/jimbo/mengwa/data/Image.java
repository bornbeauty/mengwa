package com.jimbo.mengwa.data;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

import java.util.List;

/**
 * 首页显示的缩略图
 * Created by jimbo on 2015/10/1.
 */
@Table(name = "images")
public class Image {
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
    public List<String> detailUrl;
}

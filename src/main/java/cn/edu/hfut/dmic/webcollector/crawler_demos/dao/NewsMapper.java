package cn.edu.hfut.dmic.webcollector.crawler_demos.dao;

import cn.edu.hfut.dmic.webcollector.crawler_demos.bean.News;

import java.util.List;

public interface NewsMapper {

    // 查全部新闻信息
    List<News> queryAll();


    // 插入数据
//    Integer addNewsUrl();
}

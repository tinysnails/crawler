package cn.edu.hfut.dmic.webcollector.crawler_demos;

import cn.edu.hfut.dmic.webcollector.crawler_demos.bean.News;

import java.io.*;

public class Write2Csv {
    public static void writeLineAppend(News news) throws IOException {
        FileWriter writer = new FileWriter("news", true);
        writer.write(news.toString());
    }
}

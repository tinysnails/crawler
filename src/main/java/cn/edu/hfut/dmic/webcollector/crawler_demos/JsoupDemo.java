package cn.edu.hfut.dmic.webcollector.crawler_demos;

import cn.edu.hfut.dmic.webcollector.plugin.net.OkHttpRequester;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.*;

/**
 * 关于jsonp的一些使用
 *
 */
public class JsoupDemo {
    String url = "https://github.blog/page/2/";

    public void testJsoup()throws IOException {
        File input = new File("github_demo.html");
        Document doc = Jsoup.parse(input,"UTF-8");
        Elements links = doc.select("a[href]");
        Elements pngs = doc.select("img[src$=.png]");

        // 打印Element里面的元素
        for (Element link:links
        ) {
            System.out.println(String.format("url = %s", link.attributes().get("href")));       // 获取Element的href属性

        }
    }


    /**
     * 将给定url写进html文件中
     * @param url
     * @return
     */
    public String WriteUrlHtmlToFile(String url) {
        File file = new File("demo_test.html");
        PrintStream ps = null;
        try{
            ps = new PrintStream(new FileOutputStream(file));
            String html = new OkHttpRequester().getResponse(url).html();
            ps.print(html);
        }  catch (Exception e) {
            e.printStackTrace();
        }finally {
            ps.close();
        }
        return file.toString();
    }




    public static void main(String[] args) throws IOException {
//        new JsoupDemo().WriteUrlHtmlToFile("https://github.blog/page/2/");
        new JsoupDemo().testJsoup();


    }
}

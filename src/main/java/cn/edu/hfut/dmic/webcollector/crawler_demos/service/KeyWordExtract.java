package cn.edu.hfut.dmic.webcollector.crawler_demos.service;

import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.net.OkHttpRequester;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;


/**
 * 抽取2-4个字的关键字,领域新闻
 */
public class KeyWordExtract {
    //    https://sports.sohu.com/
    OkHttpRequester okHttpRequester = new OkHttpRequester();
    HashSet<String> set = new HashSet<String>();
    PrintWriter writer = new PrintWriter("sport.txt", "UTF-8");

    public KeyWordExtract() throws FileNotFoundException, UnsupportedEncodingException {
    }

    public void extractKeyword(String url) throws FileNotFoundException, UnsupportedEncodingException {


        Page page = null;
        try {
            page = okHttpRequester.getResponse(url);
            System.out.println(String.format("已下载url: %s",page.url()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (page != null) {
            Elements elements =  page.select("a[href]");
            for (Element element : elements) {
                String anchorText = element.text();
                if(anchorText.length() < 5) {
                    set.add(anchorText);
                }
            }
        }

    }

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        KeyWordExtract keyWordExtract = new KeyWordExtract();
        keyWordExtract.extractKeyword("https://sports.sohu.com/");
        keyWordExtract.extractKeyword("https://sports.sina.com.cn/");
        keyWordExtract.extractKeyword("https://sports.163.com//");
        keyWordExtract.extractKeyword("http://sports.people.com.cn/");
        keyWordExtract.extractKeyword("https://sports.ifeng.com//");
        keyWordExtract.extractKeyword("https://sports.qq.com/");
        keyWordExtract.extractKeyword("http://sports.sohu.com/s/fifa/");
        keyWordExtract.extractKeyword("https://sports.sohu.com/s/nba");
        keyWordExtract.extractKeyword("http://sports.sina.com.cn/chess/");
        keyWordExtract.extractKeyword("http://sports.sohu.com/hotnews//");
        for (String str : keyWordExtract.set) {
            keyWordExtract.writer.println(str);
        }
        keyWordExtract.writer.close();
    }

}

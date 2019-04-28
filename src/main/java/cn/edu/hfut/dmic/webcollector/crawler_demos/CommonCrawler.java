package cn.edu.hfut.dmic.webcollector.crawler_demos;

import cn.edu.hfut.dmic.classifier.PageClassifer;
import cn.edu.hfut.dmic.contentextractor.ContentExtractor;
import cn.edu.hfut.dmic.contentextractor.News;
import cn.edu.hfut.dmic.webcollector.crawler_demos.bean.NewsBean;
import cn.edu.hfut.dmic.webcollector.crawler_demos.dao.NewsCommonDao;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.net.OkHttpRequester;
import cn.edu.hfut.dmic.webcollector.plugin.ram.RamCrawler;
import cn.edu.hfut.dmic.webcollector.util.UrlDomainUtils;
import okhttp3.Request;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CommonCrawler extends RamCrawler {

    NewsCommonDao newsCommonDao;


    public CommonCrawler(boolean autoParse) {
        super(autoParse);

        addSeed("https://news.sohu.com/");

        addRegex(".*?sohu.com.*?");  // 站内搜索
        newsCommonDao = new NewsCommonDao();

        setRequester(new MyRequester());
    }

    @Override
    public void visit(Page page, CrawlDatums next) {
        // 遇到302跳转
        if (page.code() == 301 || page.code() == 302) {
            next.addAndReturn(page.location()).meta("depth");
            return;
        }
        if (page.matchUrl(".*?sohu.com.*?")) {

            /*对于新任务,没有depth,就默认1*/
            int depth = 1;  //初始值设为1
            try {
                depth = page.metaAsInt("depth");
            } catch (Exception e) {
                // 不含有元数据"depth"
                page.meta("depth", depth);
            }
            if(depth > 4) return;       //只爬深度为4的



            /* 获取网页元数据 */
            String url = page.url();
            String host = UrlDomainUtils.getDomainByUrl(url);
            String html = page.html();
            String content = "none";
            String title = page.doc().title();
            try {
                News news = ContentExtractor.getNewsByHtml(html);
                content = news.getContent();
//            title = news.getTitle();
            } catch (Exception e) {
//            e.printStackTrace();
                System.out.println("===========未提取到主题内容==========");
            }

            /* 对于种子页来说,没有锚文本,采用其标题 */
            String anchor = (page.meta("s_t") == null) ? title : page.meta("s_t");

            int anchorLen = anchor.length();
            int urlLen = url.length();
            String parametes = UrlDomainUtils.getUrlParams(url);



            /* 加入数据库 */
            NewsBean newsBean = new NewsBean(depth, host, title, url, content, html,urlLen,anchorLen,anchor,parametes);

            try {
                newsCommonDao.insertNews(newsBean);
                LOG.info("插入news成功");
            } catch (IOException e) {
                e.printStackTrace();
            }
/*            *//* 加入下一级的种子, 如果开了autoparse,跟这个效果重复 *//*
            Elements elements = page.select("a[href]");
            for (Element element : elements) {
                *//* 找到锚文本和url *//*
                String anchorText = element.text();
                String anchorUrl = element.absUrl("href");      // 获取绝对地址
                if(UrlDomainUtils.matchUrlAndHost(".*?sohu.com.*?",anchorUrl)){     // 符合主站正则才进行提取
                    CrawlDatum demo = new CrawlDatum(anchorUrl, anchorText);        // 添加锚文本,锚文本的key是"s_t"
                    next.add(demo);
                }
            }*/

        }

    }

    // 可以自定义User-Agent和Cookies
    public static class MyRequester extends OkHttpRequester {

        public static List<String> userAgents = Arrays.asList("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/22.0.1207.1 Safari/537.1",
                "Mozilla/5.0 (X11; CrOS i686 2268.111.0) AppleWebKit/536.11 (KHTML, like Gecko) Chrome/20.0.1132.57 Safari/536.11",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.6 (KHTML, like Gecko) Chrome/20.0.1092.0 Safari/536.6",
                "Mozilla/5.0 (Windows NT 6.2) AppleWebKit/536.6 (KHTML, like Gecko) Chrome/20.0.1090.0 Safari/536.6",
                "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/19.77.34.5 Safari/537.1",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.9 Safari/536.5",
                "Mozilla/5.0 (Windows NT 6.0) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.36 Safari/536.5",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1063.0 Safari/536.3",
                "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1063.0 Safari/536.3",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_0) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1063.0 Safari/536.3",
                "Mozilla/5.0 (Windows NT 6.2) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1062.0 Safari/536.3",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1062.0 Safari/536.3",
                "Mozilla/5.0 (Windows NT 6.2) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1061.1 Safari/536.3",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1061.1 Safari/536.3",
                "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1061.1 Safari/536.3",
                "Mozilla/5.0 (Windows NT 6.2) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1061.0 Safari/536.3",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.24 (KHTML, like Gecko) Chrome/19.0.1055.1 Safari/535.24",
                "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/535.24 (KHTML, like Gecko) Chrome/19.0.1055.1 Safari/535.24");
        String userAgent ;
        String cookie ;


        // 每次发送请求前都会执行这个方法来构建请求
        @Override
        public Request.Builder createRequestBuilder(CrawlDatum crawlDatum) {
            userAgent = userAgents.get(new Random().nextInt(userAgents.size()));
            // 这里使用的是OkHttp中的Request.Builder
            // 可以参考OkHttp的文档来修改请求头
            return super.createRequestBuilder(crawlDatum)
                    .addHeader("User-Agent", userAgent);
        }

    }

    @AfterParse
    public void afterParse(Page page, CrawlDatums next) {
        //当前页面的depth为x，则从当前页面解析的后续任务的depth为x+1
        int depth = 1;
        //如果在添加种子时忘记添加depth信息，可以通过这种方式保证程序不出错
        try {
            depth = page.metaAsInt("depth");
        } catch (Exception ex) {

        }
        depth++;
        next.meta("depth", depth);
    }



    public static void main(String[] args) throws Exception {
        CommonCrawler topicCrawler = new CommonCrawler(true);       //通用的就是false,需要自己的classifer判别
        topicCrawler.setThreads(20);
        topicCrawler.start(4);      // 爬取深度
    }
}

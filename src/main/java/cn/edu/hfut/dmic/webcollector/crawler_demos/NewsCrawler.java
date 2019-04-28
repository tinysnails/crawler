package cn.edu.hfut.dmic.webcollector.crawler_demos;

import cn.edu.hfut.dmic.contentextractor.ContentExtractor;
import cn.edu.hfut.dmic.contentextractor.News;
import cn.edu.hfut.dmic.webcollector.crawler_demos.bean.NewsBean;
import cn.edu.hfut.dmic.webcollector.crawler_demos.dao.NewsDao;
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
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class NewsCrawler extends RamCrawler {

//    JdbcTemplate jdbcTemplate = null;

    NewsDao newsDao;
    List regexList;     // 正则列表
    String regexStr;        // 正则语句
    int code = 0;

    public NewsCrawler(boolean autoParse) {
        super(autoParse);

        // FIXME webcollecto的网址过滤和内容过滤机制.

        List<String> seeds = Arrays.asList(
//                "https://www.163.com/"
//                        "http://news.sohu.com/"
//                        "http://www.people.com.cn/"
//                        "http://www.cankaoxiaoxi.com/"
//                        "http://www.takungpao.com/"
//                        "http://www.huanqiu.com/?agt=6260"
//                        "https://news.sina.com.cn/"
//                        "https://www.guancha.cn/"
//                        "https://news.qq.com/",
//                        "http://www.thepaper.cn/"
//                        "http://news.ifeng.com/"
//                "http://www.xinhuanet.com/"
//                "http://cn.chinadaily.com.cn/"
//                "http://www.china.com.cn/"
//                "http://m.ce.cn/"
//                "http://www.cnr.cn/"
//                "http://www.qstheory.cn/"
//                "http://news.baidu.com/",
                    "http://www.chinanews.com/"
                        );
        regexList = new ArrayList();
        // 添加种子与规则
        for (String seed :seeds) {
            addSeed(new CrawlDatum(seed).meta("depth",1));
            String firstHost = UrlDomainUtils.getFirstDomainByUrl(seed);
            regexList.add(firstHost);
            this.addRegex(firstHost);
        }
        regexStr = UrlDomainUtils.getRegexStr(regexList);
        this.addRegex("-.*#.*");

        // 设置请求插件
        setRequester(new MyRequester());

        setThreads(50);
//        getConf().setTopN(100);
        newsDao = new NewsDao();
    }

    // 可以自定义User-Agent和Cookies
    public static class MyRequester extends OkHttpRequester{

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

    @Override
    public void visit(Page page, CrawlDatums next) {

        // 遇到302跳转
        if (page.code() == 301 || page.code() == 302) {
            next.addAndReturn(page.location()).meta("depth");
            return;
        }

        // 判断符合主站的网页才进行下一步
        if (UrlDomainUtils.matchUrlAndHost(regexStr,page.url())) {

            /* 进行网页的深度判别 */
            int depth = Integer.valueOf(page.meta("depth"));
            if (depth == 4) return;         // 各个网站的遍历深度为准

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
            String anchor = (page.meta("s_t") == null) ? "" : page.meta("s_t");

            int anchorLen = anchor.length();
            int urlLen = url.length();
            String parametes = UrlDomainUtils.getUrlParams(url);



            /* 加入数据库 */
            NewsBean newsBean = new NewsBean(depth, host, title, url, content, html,urlLen,anchorLen,anchor,parametes);

            /* 定量爬取网页 */
            try {
                this.code = newsDao.insertNews(newsBean);
                if (this.code % 5000 == 0) {    // 此处可能不是5000的时候访问
                    System.out.println("==========================The count is satisfied.====================================");
                    this.stop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 加入种子url
//        next.add(page.links("a[href]"));

            Elements elements = page.doc().select("a[href]");
            for (Element element : elements) {
                /* 找到锚文本和url */
                String anchorText = element.text();
                String anchorUrl = element.absUrl("href");      // 获取绝对地址
                if(UrlDomainUtils.matchUrlAndHost(regexStr,anchorUrl)){     // 符合主站正则才进行提取
                    CrawlDatum demo = new CrawlDatum(anchorUrl, anchorText);        // 锚文本的key是"s_t"
                    next.add(demo);
                }
            }
        }

        //写入文件
//        try {
//            FileWriter writer = new FileWriter("news.csv", true);
//            StringBuilder sb = new StringBuilder();
//            writer.write(depth +"," + host + ","+ title +","+ url +","
//                    + content + "," + html +'\n');
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

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
//        NewsCrawler crawler = new NewsCrawler(true);
//        crawler.dbInitialize();
//        crawler.start();


        /** 效率低，1000个网页8343ms  */
        long start_time = System.currentTimeMillis();
        for (int i = 1; i < 1000; i++) {
            OkHttpRequester okHttpRequester  =  new OkHttpRequester();
            String result = okHttpRequester.getResponse("http://127.0.0.1:5000?text=体育").html();
            System.out.println(result);
        }
        long result = System.currentTimeMillis() - start_time;
        System.out.println(String.format("耗时 %d ms",result));

    }
}

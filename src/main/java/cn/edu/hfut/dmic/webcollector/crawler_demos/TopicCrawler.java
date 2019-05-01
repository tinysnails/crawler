package cn.edu.hfut.dmic.webcollector.crawler_demos;

import cn.edu.hfut.dmic.classifier.AnchorClassifer;
import cn.edu.hfut.dmic.classifier.PageClassifer;
import cn.edu.hfut.dmic.classifier.TopicDict;
import cn.edu.hfut.dmic.contentextractor.ContentExtractor;
import cn.edu.hfut.dmic.contentextractor.News;
import cn.edu.hfut.dmic.webcollector.crawler_demos.bean.NewsBean;
import cn.edu.hfut.dmic.webcollector.crawler_demos.dao.NewsTopicDao;
import cn.edu.hfut.dmic.webcollector.fetcher.Visitor;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.net.OkHttpRequester;
import cn.edu.hfut.dmic.webcollector.plugin.ram.RamCrawler;
import cn.edu.hfut.dmic.webcollector.util.UrlDomainUtils;
import okhttp3.Request;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class TopicCrawler extends RamCrawler {

    public static final Logger LOG = LoggerFactory.getLogger(TopicCrawler.class);
    NewsTopicDao newsTopicDao;
    PageClassifer pageClassifer;
    HashSet<String> topicDict;
    String topic;
    public TopicCrawler(boolean autoParse) {
        super(autoParse);

        addSeed("https://news.sohu.com/");

        addRegex(".*?sohu.com.*?");  // 站内爬取


        addRegex("-https://search.*?");
        addRegex("-http://search.*?");
        newsTopicDao = new NewsTopicDao();

        /*更换请求头*/
        setRequester(new TopicCrawler.MyRequester());

        /*获取网页分类器*/
        pageClassifer = new PageClassifer();

        /*获取topic字典*/
        try {
            topicDict = TopicDict.getDict("sport.txt");
        } catch (IOException e) {
            LOG.error("获取topic字典失败");
        }
    }

    /**
     * 输入锚文本,判断是否与目标主题相同
     * @param anchorText
     * @return
     */
    public boolean matchTopic(String anchorText) {
        if (anchorText.length() <= 4) {
            // 长度小于等于4的领域,采用领域主题词典来处理
            return topicDict.contains(anchorText)? true : false;
        }else{
            // 长度大于4的采用cnn分类器来处理
            return AnchorClassifer.getAnchorType(anchorText).equals(topic);
        }
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

            /* 获取网页元数据 */
            String url = page.url();
            String host = UrlDomainUtils.getDomainByUrl(url);
            String html = page.html();
            String content = "none";
            String title = page.doc().title();

            /*对于初始网页*/
            if(page.meta("page_type") == null) {
                page.meta("page_type",pageClassifer.getPageType(url,url.length(),depth) );
            }

            /* 根据网页类型不同选择不同策略 */
            if(page.meta("page_type").equals("详情页")){
                /*详情页处理策略, 索引页不处理这些*/

                /*提取正文*/
                try {
                    News news = ContentExtractor.getNewsByHtml(html);
                    content = news.getContent();
                } catch (Exception e) {
                    LOG.info("===========未提取到正文内容==========");
                }
                String anchor = (page.meta("s_t") == null) ? title : page.meta("s_t");

                int anchorLen = anchor.length();
                int urlLen = url.length();
                String parametes = UrlDomainUtils.getUrlParams(url);

                /* 加入数据库 */
                NewsBean newsBean = new NewsBean(depth, topic, host, title, url, content, html,urlLen,anchorLen,anchor,parametes);

                try {
                    newsTopicDao.insertNews(newsBean);
                    LOG.info("插入news成功");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            /*索引页和详情页都要进行种子解析*/
            /* 加入下一级的种子 */
            Elements elements = page.select("a[href]");
            for (Element element : elements) {
                /* 找到锚文本和url */
                String anchorText = element.text();
                String anchorUrl = element.absUrl("href");      // 获取绝对地址
                if(UrlDomainUtils.matchUrlAndHost(".*?sohu.com.*?",anchorUrl)){     // 符合主站正则才进行提取

                    if(!this.matchTopic(anchorText)){ // 先判断是否符合topic主题,符合的话进行类型判断,不符合接着下一条
                        continue;
                    }{
                        /* 已经匹配主题 */
                        /* 对网页进行分类 */
                        String pageType = null;
                        pageType = pageClassifer.getPageType(anchorUrl,anchorText.length(),depth);
                        CrawlDatum demo = new CrawlDatum(anchorUrl, anchorText);        // 添加锚文本,锚文本的key是"s_t"
                        demo.meta("page_type", pageType);
                        next.add(demo);         //加入种子
                    }
                }
            }
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

    @Visitor.AfterParse
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


    /*
    * 三层 体育测试
    *
    * 2019-04-30 01:50:27 INFO cn.edu.hfut.dmic.webcollector.fetcher.Fetcher  - -activeThreads=1, spinWaiting=0, fetchQueue.size=0
2019-04-30 01:50:28 INFO cn.edu.hfut.dmic.webcollector.fetcher.Fetcher  - -activeThreads=0, spinWaiting=0, fetchQueue.size=0
2019-04-30 01:50:28 INFO cn.edu.hfut.dmic.webcollector.fetcher.Fetcher  - clear all activeThread
2019-04-30 01:50:28 INFO cn.edu.hfut.dmic.webcollector.fetcher.Fetcher  - close generator:cn.edu.hfut.dmic.webcollector.plugin.ram.RamGenerator
2019-04-30 01:50:28 INFO cn.edu.hfut.dmic.webcollector.fetcher.Fetcher  - close segmentWriter:cn.edu.hfut.dmic.webcollector.plugin.ram.RamDBManager
2019-04-30 01:50:28 INFO cn.edu.hfut.dmic.webcollector.crawler.Crawler  - depth 3 finish:
	total urls:	476
	total time:	48 seconds
    * */


    public static void main(String[] args) throws Exception {
        TopicCrawler topicCrawler = new TopicCrawler(false);       //通用的就是false,需要自己的classifer判别
        topicCrawler.setThreads(20);
        topicCrawler.getConf().setExecuteInterval(1000);
        topicCrawler.topic = "体育";
        topicCrawler.start(3);      // 爬取深度
    }
}

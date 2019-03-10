package cn.edu.hfut.dmic.webcollector.crawler_demos;

import cn.edu.hfut.dmic.contentextractor.ContentExtractor;
import cn.edu.hfut.dmic.contentextractor.News;
import cn.edu.hfut.dmic.webcollector.crawler_demos.bean.NewsBean;
import cn.edu.hfut.dmic.webcollector.crawler_demos.dao.NewsDao;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.ram.RamCrawler;
import cn.edu.hfut.dmic.webcollector.util.UrlDomainUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewsCrawler extends RamCrawler {

//    JdbcTemplate jdbcTemplate = null;

    NewsDao newsDao;

    public NewsCrawler(boolean autoParse) {
        super(autoParse);

        // FIXME webcollecto的网址过滤和内容过滤机制.

//        List<String> seeds = Arrays.asList("https://www.163.com/",
//                "https://tuijian.hao123.com/",
//                        "http://news.sohu.com/",
//                        "http://www.people.com.cn/",
//                        "http://www.cankaoxiaoxi.com/",
//                        "http://www.takungpao.com/",
//                        "http://www.huanqiu.com/?agt=6260",
//                        "http://news.sina.com.cn/",
//                        "http://www.guancha.cn/",
//                        "http://news.qq.com/",
//                        "http://www.thepaper.cn/",
//                        "http://news.ifeng.com/"
//                        );
        List<String> seeds = Arrays.asList("https://www.163.com/");

        // 添加种子与规则
        for (String seed :seeds) {
            addSeed(new CrawlDatum(seed).meta("depth",1));
            this.addRegex(UrlDomainUtils.getFirstDomainByUrl(seed));
        }
        this.addRegex("-.*#.*");


        setThreads(50);
        getConf().setTopN(100);
        newsDao = new NewsDao();
        // 利用JDBC连接数据库
//        JdbcTemplate jdbcTemplate = null;
//        try {
//            jdbcTemplate = new MysqlHelper("jdbc:mysql://localhost/db_whukg?useUnicode=true&characterEncoding=utf8",
//                    "root", "password", 5, 30).getTemplate();
//
//            /*创建数据表*/
//            jdbcTemplate.execute("drop table if exists news;");
//            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS news (" +
//                    "id int(10) NOT NULL AUTO_INCREMENT," +
//                    "depth int(10) NOT NULL," +
//                    "host VARCHAR(25)," +
//                    "title VARCHAR(50)," +
//                    "url VARCHAR(200)," +
//                    "content LONGTEXT," +
//                    "html LONGTEXT," +
//                    "type int(1)," +
//                    "PRIMARY KEY(id)" +
//                    ") ENGINE=innodb DEFAULT CHARSET=utf8;");
//            System.out.println("成功创建数据表 tb_content");
//        } catch (Exception ex) {
//            jdbcTemplate = null;
//            System.out.println("mysql未开启或JDBCHelper.createMysqlTemplate中参数配置不正确!");
//        }
    }

    @Override
    public void visit(Page page, CrawlDatums next) {
        int depth = Integer.valueOf(page.meta("depth"));
        if(depth == 4) return ;         // 各个网站的遍历深度为准
        String url = page.url();
        String host = UrlDomainUtils.getDomainByUrl(url);
        String html = page.html();
        String content = "none";
        String title = "none";
        try {
            News news = ContentExtractor.getNewsByHtml(html);
            content = news.getContent();
            title = news.getTitle();
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("===========未提取到主题内容==========");
        }

        // 加入种子url
//        next.add(page.links("a[href]"));

        Elements elements = page.doc().select("a[href]");
        for (Element element : elements) {
            next.add(UrlDomainUtils.getAbsoluteUrl(page.url(),element.attributes().get("href")));
        }


        NewsBean newsBean = new NewsBean(depth,host,title,url,content,html);

        try {
            newsDao.insertNews(newsBean);
        } catch (Exception e) {
            e.printStackTrace();
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
//
//    /**
//     * 初始化数据库相关操作
//     */
//    public void dbInitialize() {
//        JdbcTemplate template =
//                JDBCHelper.getJdbcTemplate("news");
//        template.execute("drop table if exists news;");
//        template.execute("CREATE TABLE IF NOT EXISTS news (" +
//                "id int(10) NOT NULL AUTO_INCREMENT," +
//                "depth int(10) NOT NULL," +
//                "host VARCHAR(25)," +
//                "title VARCHAR(50)," +
//                "url VARCHAR(200)," +
//                "context LONGTEXT," +
//                "html LONGTEXT," +
//                "type int(1)," +
//                "PRIMARY KEY(id)" +
//                ") ENGINE=innodb DEFAULT CHARSET=utf8;");
//        this.template = template;
//    }



    public static void main(String[] args) throws Exception {
        NewsCrawler crawler = new NewsCrawler(true);
//        crawler.dbInitialize();
        crawler.start(1);
    }
}

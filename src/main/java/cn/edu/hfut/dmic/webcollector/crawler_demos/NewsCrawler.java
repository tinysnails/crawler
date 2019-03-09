package cn.edu.hfut.dmic.webcollector.crawler_demos;

import cn.edu.hfut.dmic.contentextractor.ContentExtractor;
import cn.edu.hfut.dmic.contentextractor.News;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.ram.RamCrawler;
import cn.edu.hfut.dmic.webcollector.util.UrlDomainUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;

public class NewsCrawler extends RamCrawler {


    public NewsCrawler(boolean autoParse) {
        super(autoParse);

        addSeed(new CrawlDatum("https://tuijian.hao123.com/").meta("depth",1));
        addSeed(new CrawlDatum("http://news.sohu.com/").meta("depth",1));


        this.addRegex("hao123.com");
        this.addRegex("sohu.com");
        this.addRegex("-.*#.*");

        setThreads(50);
        getConf().setTopN(100);
    }

    @Override
    public void visit(Page page, CrawlDatums next) {
        String url = page.url();
        String host = UrlDomainUtils.getDomainByUrl(url);
        int depth = Integer.valueOf(page.meta("depth"));
        String html = page.html();
        String content = null;
        String title = null;
        try {
            News news = ContentExtractor.getNewsByHtml(html);
            content = news.getContent();
            title = news.getTitle();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("===========未提取到主题内容==========");
        }

        // 加入种子url
        Elements elements = page.doc().select("a[href]");
        for (Element element : elements) {
            next.add(element.attributes().get("href"));
        }

        //写入文件
        try {
            FileWriter writer = new FileWriter("news.csv", true);
            StringBuilder sb = new StringBuilder();
            writer.write(depth +"," + host + ","+ title +","+ url +","
                    + content + "," + html +'\n');
        } catch (IOException e) {
            e.printStackTrace();
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

    /**
     * 初始化数据库相关操作
     */
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
        crawler.start(4);
    }
}

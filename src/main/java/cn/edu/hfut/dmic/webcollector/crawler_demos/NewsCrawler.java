package cn.edu.hfut.dmic.webcollector.crawler_demos;

import cn.edu.hfut.dmic.contentextractor.ContentExtractor;
import cn.edu.hfut.dmic.contentextractor.News;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.ram.RamCrawler;
import cn.edu.hfut.dmic.webcollector.util.UrlDomainUtils;

public class NewsCrawler extends RamCrawler {


    public NewsCrawler(boolean autoParse) {
        super(autoParse);

        addSeed(new CrawlDatum("https://tuijian.hao123.com/").meta("depth",1));
        addSeed(new CrawlDatum("http://news.sohu.com/").meta("depth",1));


        this.addRegex(UrlDomainUtils.getDomainByUrl("https://tuijian.hao123.com"+".*"));
        this.addRegex(UrlDomainUtils.getDomainByUrl("http://news.sohu.com"+".*"));
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

//        if (this.template != null) {
//            int update = this.template.update("INSERT INTO news " +
//                    "(depth,host,title,url,content,html) value(?,?,?,?,?,?)",depth,host,title,url,content,html);
//            if(update == 1) System.out.println("insert successful");
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
//        crawler.start(3);
    }
}

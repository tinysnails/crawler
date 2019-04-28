package cn.edu.hfut.dmic.webcollector.crawler_demos.dao;

import cn.edu.hfut.dmic.contentextractor.News;
import cn.edu.hfut.dmic.webcollector.crawler_demos.bean.NewsBean;
import cn.edu.hfut.dmic.webcollector.crawler_demos.db.MysqlDataConnection;
import org.apache.ibatis.session.SqlSession;

import java.io.IOException;


/**
 * 对应数据库表是news_common
 */
public class NewsCommonDao {

    public MysqlDataConnection connection = new MysqlDataConnection();

    /* 插入新闻 */
    public int insertNews(NewsBean news) throws IOException {
        SqlSession sqlSession = connection.getSqlSession();
        sqlSession.insert("news.insertNewsCommon",news);
        int code = news.getId();
        System.out.println(String.format("========================insert_id = %d=============================================", code));
        sqlSession.commit();
        sqlSession.close();
        return code;
    }


    /* 删除新闻 */
    public void deleteNewsAll() throws IOException {
        SqlSession sqlSession = connection.getSqlSession();
        sqlSession.delete("news.deleteNewsCommonAll");
        sqlSession.commit();
        sqlSession.close();
    }

    /* 删除新闻,并且清楚自增序列*/
    public void cleanNewsAll() throws IOException {
        SqlSession sqlSession = connection.getSqlSession();
        sqlSession.update("news.cleanNewsCommonAll");
        sqlSession.commit();
        sqlSession.close();
    }

    public void cleanAll() throws IOException {
        this.deleteNewsAll();
        this.cleanNewsAll();
    }

    public static void main(String[] args) throws IOException {
        NewsBean news = new NewsBean(2,"host","title","url","content","html",12,12,"anchor","params");
        NewsCommonDao newsCommonDao = new NewsCommonDao();
        newsCommonDao.cleanAll();
//        newsCommonDao.insertNews(news);
    }

}

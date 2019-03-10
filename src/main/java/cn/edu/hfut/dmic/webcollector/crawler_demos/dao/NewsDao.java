package cn.edu.hfut.dmic.webcollector.crawler_demos.dao;

import cn.edu.hfut.dmic.webcollector.crawler_demos.bean.NewsBean;
import cn.edu.hfut.dmic.webcollector.crawler_demos.db.MysqlDataConnection;
import org.apache.ibatis.session.SqlSession;

import java.io.IOException;

public class NewsDao {
    public MysqlDataConnection connection = new MysqlDataConnection();

    /* 插入新闻 */
    public void insertNews(NewsBean news) throws IOException {
        SqlSession sqlSession = connection.getSqlSession();
        sqlSession.insert("news.insertNews",news);
        System.out.println(String.format("insert_id = %d", news.getId()));
        sqlSession.commit();
        sqlSession.close();
    }

    /* 删除新闻 */
    public void deleteNewsAll() throws IOException {
        SqlSession sqlSession = connection.getSqlSession();
        sqlSession.delete("news.deleteNewsAll");
        sqlSession.commit();
        sqlSession.close();
    }

    /* 删除新闻,并且清楚自增序列*/
    public void cleanNewsAll() throws IOException {
        SqlSession sqlSession = connection.getSqlSession();
        sqlSession.update("news.cleanNewsAll");
        sqlSession.commit();
        sqlSession.close();
    }


}

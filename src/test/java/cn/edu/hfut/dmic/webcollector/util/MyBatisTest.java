package cn.edu.hfut.dmic.webcollector.util;

import cn.edu.hfut.dmic.webcollector.crawler_demos.bean.NewsBean;
import cn.edu.hfut.dmic.webcollector.crawler_demos.dao.NewsDao;
import cn.edu.hfut.dmic.webcollector.crawler_demos.db.MysqlDataConnection;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.io.IOException;
import java.util.regex.Pattern;

public class MyBatisTest {

    public MysqlDataConnection connection = new MysqlDataConnection();

    @Test
    public void testFindNewsById() throws IOException {
        SqlSession sqlSession = connection.getSqlSession();
        NewsBean news = sqlSession.selectOne("news.findNewsById", 2);       //第一个参数是namespace + statement配置的id,第二个参数是parameterType类型的参数
        System.out.println(news);
        sqlSession.close();
    }

    @Test
    public void testInsert() throws IOException {
        SqlSession sqlSession = connection.getSqlSession();
        NewsBean news = new NewsBean(2,"host","title","url","content","html",12,12,"anchor","params");
//        sqlSession.insert("news.insertNews", news);
//        System.out.println(String.format("insert_id = %d", news.getId()));
//        sqlSession.commit();
//        sqlSession.close();
        new NewsDao().insertNews(news);
    }

    @Test
    public void testDeleteNewsAll() throws IOException {
        SqlSession sqlSession = connection.getSqlSession();
        sqlSession.delete("news.deleteNewsAll");
        sqlSession.commit();
        sqlSession.close();
    }


    @Test
    public void cleanNewsAll() throws IOException {
        SqlSession sqlSession = connection.getSqlSession();
        sqlSession.update("news.cleanNewsAll");
        sqlSession.commit();
        sqlSession.close();
    }

    public static void main(String[] args)  {
//        String url = "https://www.163.com/";
//        String urlRegex = "(163.com)|(hao123.com)|(sohu.com)|(people.com)|(cankaoxiaoxi.com)|(takungpao.com)|(huanqiu.com)|(sina.com)|(guancha.cn)|(thepaper.cn)|(ifeng.com)";
//
//        System.out.println(Pattern.matches(urlRegex, url));
    }
}

package cn.edu.hfut.dmic.webcollector.crawler_demos.bean;

import cn.edu.hfut.dmic.webcollector.crawler_demos.dao.DataConnection;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class Test {
    public static void main(String[] args) throws IOException {
        SqlSession sqlSession = new DataConnection().getSqlSession();
        News news = sqlSession.selectOne("test.findNewsById", 1);       //第一个参数是namespace + statement配置的id,第二个参数是parameterType类型的参数
        System.out.println(news);
        sqlSession.close();
    }
}

package cn.edu.hfut.dmic.webcollector.plugin.mysql;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;

/**
 * 可以调用JDBCHelper的getJdbcTemplate方法来获取一个JdbcTemplate，
 * 考虑到有些爬虫可能会用到多个数据库，这里可以为每个JdbcTemplate指定名称，
 * 如果已经创建了一个名为xxx的JdbcTemplate，第二次调用getJdbcTemplate(xxx)时，
 * 不会二次创建，而是返回已经创建的JdbcTemplate。
 */
public class JDBCHelper {

    public static HashMap<String, JdbcTemplate> templateMap = new HashMap<String, JdbcTemplate>();

    public static JdbcTemplate createMysqlTemplate(String templateName, String url, String username, String password,
                                                   int initialSize, int maxActive) {

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setInitialSize(initialSize);
        dataSource.setMaxActive(maxActive);
        JdbcTemplate template = new JdbcTemplate(dataSource);
        templateMap.put(templateName, template);
        return template;
    }

    public static JdbcTemplate getJdbcTemplate(String templateName) {
        return templateMap.get(templateName);
    }
}
package cn.edu.hfut.dmic.webcollector.crawler_demos.service;

import org.apache.commons.lang3.builder.ToStringExclude;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexDemo {
    public static void testPattern(){
        String content = "I am noob from runoob.com.";

        String pattern = ".*runoob.*";

        //
        boolean isMatch = Pattern.matches(pattern, content); // 必须完全匹配
        System.out.println("字符串中是否包含了 'runoob' 子字符串? " + isMatch);
    }

    public static void testMatcher() {
        // 按指定模式在字符串查找
        String line = "This order was placed for QT3000! OK?";
        String pattern = "(\\D*)(\\d+)(.*)";

        // 创建 Pattern 对象
        Pattern r = Pattern.compile(pattern);

        // 现在创建 matcher 对象
        Matcher m = r.matcher(line);
        if (m.find( )) {
            System.out.println("Found value: " + m.group(0) );
            System.out.println("Found value: " + m.group(1) );
            System.out.println("Found value: " + m.group(2) );
            System.out.println("Found value: " + m.group(3) );
        } else {
            System.out.println("NO MATCH");
        }
    }



    public static void main(String[] args) {
//        RegexDemo.testPattern();
        String regexStr = "163.com|sohu.com";
        Pattern p = Pattern.compile(regexStr);
        String content = "www.sohu.com/sdf/sdfsdf/";
        Matcher m = p.matcher(content);

        System.out.println(m.find());

    }
}

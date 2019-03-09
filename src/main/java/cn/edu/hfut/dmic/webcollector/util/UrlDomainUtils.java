package cn.edu.hfut.dmic.webcollector.util;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获得网址的域名
 */
public class UrlDomainUtils {

    public static String getFirstDomainByUrl(String url) {
        //定义好获取的域名后缀。如果还有要定义的	请添加 |(\\.域名的后缀) 。
        String regStr="[0-9a-zA-Z]+((\\.com)|(\\.cn)|(\\.org)|(\\.net)|(\\.edu)|(\\.com.cn)|(\\.xyz)|(\\.xin)|(\\.club)|(\\.shop)|(\\.site)|(\\.wang)" +
                "|(\\.top)|(\\.win)|(\\.online)|(\\.tech)|(\\.store)|(\\.bid)|(\\.cc)|(\\.ren)|(\\.lol)|(\\.pro)|(\\.red)|(\\.kim)|(\\.space)|(\\.link)|(\\.click)|(\\.news)|(\\.news)|(\\.ltd)|(\\.website)" +
                "|(\\.biz)|(\\.help)|(\\.mom)|(\\.work)|(\\.date)|(\\.loan)|(\\.mobi)|(\\.live)|(\\.studio)|(\\.info)|(\\.pics)|(\\.photo)|(\\.trade)|(\\.vc)|(\\.party)|(\\.game)|(\\.rocks)|(\\.band)" +
                "|(\\.gift)|(\\.wiki)|(\\.design)|(\\.software)|(\\.social)|(\\.lawyer)|(\\.engineer)|(\\.org)|(\\.net.cn)|(\\.org.cn)|(\\.gov.cn)|(\\.name)|(\\.tv)|(\\.me)|(\\.asia)|(\\.co)|(\\.press)|(\\.video)|(\\.market)" +
                "|(\\.games)|(\\.science)|(\\.中国)|(\\.公司)|(\\.网络)|(\\.pub)" +
                "|(\\.la)|(\\.auction)|(\\.email)|(\\.sex)|(\\.sexy)|(\\.one)|(\\.host)|(\\.rent)|(\\.fans)|(\\.cn.com)|(\\.life)|(\\.cool)|(\\.run)" +
                "|(\\.gold)|(\\.rip)|(\\.ceo)|(\\.sale)|(\\.hk)|(\\.io)|(\\.gg)|(\\.tm)|(\\.com.hk)|(\\.gs)|(\\.us))";
        Pattern p = Pattern.compile(regStr);
        Matcher m = p.matcher(url);
        String domain = "没获取到";
//获取一级域名
        while(m.find()){
            domain = m.group();
        }
//        System.out.println("一级域名:" + domain);
        return domain;
    }

    public static String getDomainByUrl(String url) {
        String host = null;
        try {
            URL url1 = new URL(url);
            host = url1.getHost();
//            System.out.println(url1.getPath());
//            System.out.println(url1.getQuery());
//            System.out.println(url1.getUserInfo());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }finally {
            return host;
        }
    }



    public static void main(String[] args) {
//        System.out.println(UrlDomainUtils.getFirstDomainByUrl("https://www.sojson.com/blog/209.html"));
        System.out.println(getDomainByUrl("http://mil.sohu.com/"));


    }
}

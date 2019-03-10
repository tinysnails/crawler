package cn.edu.hfut.dmic.webcollector.util;


import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
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


    /** 获得一般域名 */
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


    /**
     * 拼接网址
     * @param baseURI
     * @param relativePath
     * @return 拼接后的网址
     */
    public static String getAbsoluteUrl(String baseURI, String relativePath) {
        String abURL = null;
        try {
            if(relativePath.contains("javascript")) return "www.baidu.com#";
            URI base = new URI(baseURI);//基本网页URI
            URI abs = base.resolve(relativePath);//解析于上述网页的相对URL，得到绝对URI
            URL absURL = abs.toURL();//转成URL
            System.out.println("合并后的url:  " + absURL);
            abURL = absURL.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } finally {
            return abURL;
        }
    }


        public static void main(String[] args) {
//        System.out.println(UrlDomainUtils.getFirstDomainByUrl("https://www.sojson.com/blog/209.html"));
//        System.out.println(getDomainByUrl("http://mil.sohu.com/"));


    }
}

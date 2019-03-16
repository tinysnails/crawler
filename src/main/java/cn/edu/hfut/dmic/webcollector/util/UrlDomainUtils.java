package cn.edu.hfut.dmic.webcollector.util;


import javax.swing.text.StyledEditorKit;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Random;
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
     * 拼接网址,使用jsoup的abs代替
     * @param baseURI
     * @param relativePath
     * @return 拼接后的网址
     */
    @Deprecated
    public static String getAbsoluteUrl(String baseURI, String relativePath) {
        String abURL = null;
        try {
            if(relativePath.contains("javascript")) return "#";
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


    /**
     * 获得正则表达式语句
     */
    public static String getRegexStr(Collection<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String str: list
             ) {
            sb.append(str).append("|");
        }
        return sb.toString().substring(0, sb.length()-1);
    }


    /**
     * 判断url是否符合网址域名规定
     * @param regex 主站正则
     * @param content url
     * @return 找到符合的主站规则的url,返回true
     */
    public static boolean matchUrlAndHost(String regex, String content) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        return matcher.find() ?  true:  false;
    }


    /**
     * 获得url的参数名称集合
     * @param url
     * @return 参数的名称集合
     */
    public static String getUrlParams(String url) {
//        url = "https://www.baidu.com/s?wd=ss&rsv_spt=1&rsv_iqid=0x94550fc1000413c7&issp=1&f=8&rsv_bp=1&rsv_idx=2&ie=utf-8&tn=baiduhome_pg&rsv_enter=0&inputT=1717&rsv_sug3=11&rsv_sug4=1730&rsv_jmp=slow";
        try {
            String[] params = url.split("\\?")[1].split("&");
            StringBuilder sb = new StringBuilder();
            for (String str : params) {
                sb.append(str.split("=")[0]).append(",");
            }
            return sb.toString().substring(0, sb.length() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {        // 若没有参数返回空
            return "";
        }
    }

    /**
     * 根据url获取文件名
     */
    public static String getFileNameByUrl(String url) {
        return null;
    }


    public static void main(String[] args) {
//        System.out.println(UrlDomainUtils.getFirstDomainByUrl("https://www.sojson.com/blog/209.html"));
//        System.out.println(getDomainByUrl("http://mil.sohu.com/"));
//            for (int i = 0; i < 100; i++) {
//                int a = new Random().nextInt(5);
//                if( a >= 5 || a < 0){
//                    System.out.println("fail");
//                }
//            }
        String a = UrlDomainUtils.getUrlParams("wwwwwww");
        System.out.println(a);
    }
}

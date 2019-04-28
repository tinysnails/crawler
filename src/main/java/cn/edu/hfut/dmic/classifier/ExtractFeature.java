package cn.edu.hfut.dmic.classifier;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 负责提取url和锚文本的特征
 * 特征格式为 [index:value index:value]
 */
public class ExtractFeature {




    /**
     * url中是否含有time
     *      含有,返回 1;
     *      不含有,返回 0
     * @param url
     * @return
     */
    private static int containtTime(String url) {
//        List<String> list = Arrays.asList("http://cul.china.com.cn/zhenbao/2018-12/25/content_40624062.htm",
//                "http://tech.china.com.cn/internet/20180920/345977.shtml",
//                "http://fh.china.com.cn/ch2017/zx/2019-03-14/3579.html",
//                "http://finance.china.com.cn/money/special/2019315/20190316/4924688.shtml",
//                "http://www.chinadaily.com.cn/a/201903/16/WS5c8c2e70a3106c65c34eeec6.html",
//                "https://www.guancha.cn/LuoGang/2012_07_22_85953.shtml#comment",
//                "http://www.takungpao.com/news/232111/2018/1216/220818.html",
//                "http://money.163.com/19/0315/01/EA983Q7A002580S6.html");
        Pattern pattern = Pattern.compile("20\\d{2}[-/_]?[0,1]\\d[-/_]?\\d{2}?\\b|20\\d{2}[-/_]?[0,1]\\d\\b|\\d{2}[-/_]?[0,1]\\d[-/_]?\\d{2}?\\b");
        Matcher matcher = pattern.matcher(url);
        return matcher.find() ? 1:0;
    }

    /**
     * 返回一个url的相对路径,此相对路径,不含 #
     * @param url
     * @return
     */
    private static String getRelativeUrlPath(String url) {
        URI uri = null;
        try{
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return uri == null ? null : uri.getPath();
    }

    /**
     * 获取url对应的类型
     * @return
     */
    private static String getFileType(String strUrl) {
        return null;
    }

    private static String getFileName(String url) {
        return null;
    }

    /**
     * 根据url和锚文本提取特征
     *         特征格式为
     *         depth, anchor_length, last_url_length, time,file_type, file_name
     * @param url
     * @param anchorLen
     * @param depth
     * @return
     */
    public static String extractFeature(String url, int anchorLen, int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append("1:").append(depth).append(" 2:").
                append(anchorLen).append(" 3:").
                append(getRelativeUrlPath(url).length()).
                append(" 4:").append(containtTime(url));
        return sb.toString();
    }

    public static void main(String[] args) {
//        System.out.println(getRelativeUrlPath("http://www.xinhuanet.com/politics/leaders/2019-04/27/c_1124425067.htm"));
        System.out.println(extractFeature("http://www.xinhuanet.com/politics/leaders/2019-04/27/c_1124425067.htm",26,2));
    }
}

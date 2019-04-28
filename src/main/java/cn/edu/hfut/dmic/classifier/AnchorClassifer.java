package cn.edu.hfut.dmic.classifier;

import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.net.OkHttpRequester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 对锚文本的分类器
 *      面对首页的两个字的寻址问题,构建字典. //TODO 构建短语字典.
 */
public class AnchorClassifer {
    public static final Logger LOG = LoggerFactory.getLogger(AnchorClassifer.class);

    static OkHttpRequester okHttpRequester = new OkHttpRequester();

    /**
     * 获取anchor文本类型
     * @param anchor
     * @return
     */
    public static String getAnchorType(String anchor) {
        String topic = null;
        try {
            Page page= okHttpRequester.getResponse(String.format("http://127.0.0.1:5000?text=%s",anchor));
            topic = page.html();
            LOG.info(String.format("[%s]锚文本:%s ",topic, anchor));
        } catch (Exception e) {
            LOG.error(String.format("[请求出错] 锚文本: %s ", anchor));
        }
        return topic;
    }

    public static void main(String[] args) {
//        getAnchorType("习近平在第二届“一带一路”国际合作高峰论坛记者会上的讲话(全文）\n");
//        getAnchorType("近海能看鲸鱼白海豚，广西这片滨海旅游");
//        getAnchorType("库里无对抗扭脚回更衣室！ 近期内第三次了");
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1; i++) {
            getAnchorType("库里无对抗扭脚回更衣室！ 近期内第三次了");
            getAnchorType("从招商公园1872看到的购房警示:高层“被高低配");
        }
        long endTime = System.currentTimeMillis();
        System.out.println(String.format("耗时 %d ms" , (endTime-startTime)));
    }
}

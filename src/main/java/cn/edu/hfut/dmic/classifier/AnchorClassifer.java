package cn.edu.hfut.dmic.classifier;

import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.net.OkHttpRequester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
            if(topic.equals("体育")){
                LOG.info(String.format("[%s]锚文本:%s ",topic, anchor));
            }
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
        List<String> list = Arrays.asList("新疆队次战不会替换费尔德 阿的江：还没见亚当斯的影儿" ,
                "19:35直播CBA总决赛：广东VS新疆G2" ,
                "对话费尔德：膝伤不影响打比赛 今晚如何偷走胜利" ,
                "专访威姆斯：和易建联很默契 赵睿是更衣室开心果" ,
                "前瞻：广东全线占优 新疆次战需立足防守压节奏" ,
                "周琦：充分尊重CBA公司裁定 备战世界杯不容分心" ,
                "亚当斯抵达东莞 总决赛G2新疆继续使用费尔德" ,
                "组图：亚当斯抵东莞与新疆会合 与热情球迷合影" ,
                "CBA官方声明：周琦返回CBA 新疆有两年优先注册权" ,
                "疆媒：新疆工作的方向错了 拼进攻不是飞虎出路" ,
                "总决赛夜郭艾伦引关注 录跑男撕angelababy名牌" ,
                "费尔德深夜医院检查 亚当斯飞往东莞火线驰援新疆",
                "曝曼联已决意清洗博格巴+卢卡库 盼回收2亿英镑",
                        "曼联VS切尔西前瞻：争四决死战 谁能率先止颓？",
                        "28日赔率：曼联切尔西五五开 曼城或大球狂胜",
                        "曼城前瞻：遭遇卫冕冠军杀手 阿圭罗冲亨利纪录",
                        "曼联切尔西身价：总和17亿欧 扎球王力压博格巴",
                        "曼联切尔西对位：蓝军9分大胜 博格巴养生足球",
                        "阿森纳前瞻：坐看曼联蓝军死斗 奥巴梅扬出战成疑",
                        "啥态度？博格巴场上超6成时间散步 冲刺比仅0.3%",
                        "英超-南安普顿3-3提前保级成功 狼队2-1稳坐第7",
                        "英冠-诺维奇2-1提前一轮冲超 降级队全部揭晓",
                        "恭喜！诺维奇提前一轮升级 时隔三赛季重返英超",
                        "2018球衣销量榜：曼联居首位 巴萨意外未进前三",
                        "英超-孙兴慜错失单刀 热刺0-1西汉姆新球场首败",
                        "亏炸！曼联已付桑切斯工资3000万镑 赛季仅2球",
                        "曝博格巴拒绝参加季前热身 逼宫曼联放他去皇马",
                        "曼联名宿：索帅转正太匆忙 爵爷退休后步步走错",
                        "英媒：曼联锁定热刺大脑为重建基石 PK皇马竞购",
                        "红军夺10连胜重回榜首 造队史最快进球+悍将复出",
                        "萨拉赫百场进球领跑射手榜 联赛连年20+比肩苏神");
        ;
        for (String str : list) {
            getAnchorType(str);
        }

//        getAnchorType("巨坑！宁波体弱二胎妈妈被这家店忽悠做了这种手术！丈夫还说…");
//        getAnchorType("梅德韦杰夫自曝还没写博士论文：因为...");

        long endTime = System.currentTimeMillis();

//        int len = list.size();
//        for (int i = 0; i < len; i++, i++) {
//            if(!list.get(i).equals("体育")){
//                System.out.println("false");
//            }
//        }
        System.out.println(String.format("耗时 %d ms" , (endTime-startTime)));
    }
}

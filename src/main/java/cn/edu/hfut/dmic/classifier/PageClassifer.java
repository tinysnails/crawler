package cn.edu.hfut.dmic.classifier;

import libsvm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;

/**
 * 对网页的类型进行判断的类
 */
public class PageClassifer {

    public static final Logger LOG = LoggerFactory.getLogger(PageClassifer.class);

    private String modelPath = "url_classify.model";
    // svm的相关类
    private svm_parameter prm ;
    private svm_problem sp;
    private svm_model model;

    private HashMap<Double,String> type;
    // 特征相关
    private int featurNum =4;    //一个url中网页中提取特征的个数 ,未知的为0

    public PageClassifer() {
        // 创建分类器
        sp = new svm_problem();

        //配置参数
        prm = new svm_parameter();
        prm.svm_type = svm_parameter.C_SVC;
        prm.kernel_type = svm_parameter.RBF;
        prm.C = 1000;
        prm.eps = 0.0000001;
        prm.gamma = 10;
        prm.probability = 1;
        prm.cache_size=1024;
        LOG.info("classifier param check " + (svm.svm_check_parameter(sp, prm)==null));

        // 加载模型
        try {
            model = svm.svm_load_model(modelPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 存放详情页和索引页编码
        type = new HashMap<Double, String>();
        type.put(1.0,"索引页");
        type.put(-1.0,"详情页");

    }

    /**
     * 输入网页url,获得网页的类型数字代码
     *
     * @param url 待判断的url
     * @param anchorLen 锚文本长度
     * @param depth 爬取深度
     * @return 1: 详情页       0:索引页
     */
    public String getPageType(String url, int anchorLen, int depth) {

        String featureStr = ExtractFeature.extractFeature(url, anchorLen, depth);
        svm_node[] featurnArray = getFeatureArray(featureStr);
        Double aDouble = svm.svm_predict(model, featurnArray);    //不带概率的分类测试

        LOG.info(String.format("[%s]url为 %s" ,type.get(aDouble),url));

        return aDouble.intValue() == 1 ? "详情页" : "索引页";
    }

    /**
     * 输入是一串特征字符串 ,输出是一个svm_node[]的特征数组
     * @param str
     * @return
     */
    private svm_node[] getFeatureArray(String str) {
        String kvs[] = str.split(" ");
        // 创建特征数组, 用于存储特征,数组元素为svm_node对象,
        // svm_node包含两个属性,index和value,一个svm_node对应一个特征.
        svm_node[] featureAraay = new svm_node[featurNum];
        for (int i = 0; i < featurNum; i++) {
            featureAraay[i] = new svm_node();
        }

        // 循环输入特征
        for (int i = 0; i < featurNum; i++ ) {
            featureAraay[i].index = Integer.valueOf(kvs[i].split(":")[0]);
            featureAraay[i].value = Double.valueOf(kvs[i].split(":")[1]);
        }
        return featureAraay;
    }




//    private void test(String[] args) {
//        svm_problem sp = new svm_problem();
///*
//        svm_node[][] x = new svm_node[4][2];    // svm_nado是用来存index:value的,方便数据输入
//        for (int i = 0; i < 4; i++) {
//            for (int j = 0; j < 2; j++) {
//                x[i][j] = new svm_node();
//            }
//        }
//        x[0][0].index = 1;
//        x[0][0].value = 0;
//        x[0][1].index = 2;
//        x[0][1].value = 0;
//
//        x[1][0].index = 1;
//        x[1][0].value = 1;
//        x[1][1].index = 2;
//        x[1][1].value = 1;
//
//        x[2][0].index = 1;
//        x[2][0].value = 0;
//        x[2][1].index = 2;
//        x[2][1].value = 1;
//
//        x[3][0].index = 1;
//        x[3][0].value = 1;
//        x[3][1].value = 0;
//        x[3][1].index = 2;
//
//*/
//
////        double[] labels = new double[]{-1,-1,1,1};
////        sp.x = x;
////        sp.y = labels;
////        sp.l = 4;
//
////        svm_parameter prm = new svm_parameter();
////        prm.svm_type = svm_parameter.C_SVC;
////        prm.kernel_type = svm_parameter.RBF;
////        prm.C = 1000;
////        prm.eps = 0.0000001;
////        prm.gamma = 10;
////        prm.probability = 1;
////        prm.cache_size=1024;
//        /*
//         * svm_check_parameter
//         * 参数可行返回null，否则返回错误信息
//         */
//
////        System.out.println("Param Check " + (classifier.svm_check_parameter(sp, prm)==null));
///*
//        训练分类
//        svm_model model = classifier.svm_train(sp, prm);           //训练分类
//        try {
//            classifier.svm_save_model("svm_model_file", model);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//*/
///*        // 加载模型
//        svm_model model = null;
//        try {
//             model = classifier.svm_load_model("svm_model_file");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }*/
//        // 测试数据
////        String str = "1:0.75 2:-1 3:1 4:-0.660377 5:-0.894977 6:-1 7:-1 8:-0.175573 9:-1 10:-0.483871 12:-1 13:-1 ";
////        String str = "1:0 2:0";
////        String kvs[] = str.split(" ");
////        int kvNum = 13;
////        int kvsLen = kvs.length;
////        svm_node[] test = new svm_node[kvsLen];   //一维的{1:0 2:0}
////        for (int i = 0; i < kvsLen; i++) {
////            test[i] = new svm_node();
////        }
////
////
////        for (int i = 0; i < kvsLen; i++ ) {
////            int index = Integer.valueOf(kvs[i].split(":")[0]);
//////            if(index != i) continue;
////            test[i].index = Integer.valueOf(kvs[i].split(":")[0]);
////            test[i].value = Double.valueOf(kvs[i].split(":")[1]);
////        }
//
/////*      带概率的输出,输出各类别的概率,, 不能用 nr_class是啥
////        double[] l = new double[2];
////        double result_prob = classifier.svm_predict_probability(model, test,l);		//测试1，带预测概率的分类测试*/
////        double result_normal = classifier.svm_predict(model, test);    //测试2 不带概率的分类测试
////
////        System.out.println("Result with prob " + result_prob);
////        System.out.println("Result normal " + result_normal);
////        System.out.println("Probability " + Arrays.toString(l));
////        System.out.println("model.nr_class = " + model.nr_class);
//    }

    public static void main(String[] args) {
        PageClassifer classifer = new PageClassifer();
        classifer.getPageType("http://www.xinhuanet.com/politics/leaders/2019-04/27/c_1124425067.htm",26,3);
        classifer.getPageType("http://sports.sina.com.cn/basketball/nba/2019-04-27/doc-ihvhiqax5348114.shtml",26,3);
        classifer.getPageType("http://sports.sina.com.cn/",4,1);

    }


}
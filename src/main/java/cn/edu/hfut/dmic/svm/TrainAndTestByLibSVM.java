package cn.edu.hfut.dmic.svm;

import java.io.IOException;

public class TrainAndTestByLibSVM {
    //参数设置和满足LibSVM输入格式的训练文本
    public String[] str_trained = {"-g","2.0","-c","32","-t","2","-m","500.0","-h","0","E:\\test\\train\\IF_IDF\\allTrainVSM.txt"};
    private String str_model = "E:\\test\\train\\IF_IDF\\allTrainVSM.txt.model";    //训练后得到的模型文件
    private String testTxt = "E:\\test\\test\\IF_IDF\\allTestVSM.txt";
    //测试文件、模型文件、结果文件路径
    private String[] str_result = {testTxt, str_model, "E:\\test\\Res.txt"};
    private static TrainAndTestByLibSVM libSVM = null;

    /*
     * 私有化构造函数，并训练分类器，得到分类模型
     */
    private TrainAndTestByLibSVM(){

    }

    public static TrainAndTestByLibSVM getInstance(){
        if(libSVM==null)
            libSVM = new TrainAndTestByLibSVM();
        return libSVM;
    }

    /*
     * 训练分类模型
     */
    public void trainByLibSVM(){
        try {
            //训练返回的是模型文件，其实是一个路径，可以看出要求改svm_train.java
			str_model = svm_train.main(str_trained);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    /*
     * 预测分类,并返回准确率
     */
    public double tellByLibSVM(){
        double accuracy=0;
        try {
            //测试返回的是准确率，可以看出要求改svm_predict.java
			accuracy = svm_predict.main(str_result);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return accuracy;
    }

    public static void main(String[] args){
        TrainAndTestByLibSVM tat = TrainAndTestByLibSVM.getInstance();
        System.out.println("正在训练分类模型。。。。");
        tat.trainByLibSVM();
        System.out.println("正在应用分类模型进行分类。。。。");
        tat.tellByLibSVM();
    }
}

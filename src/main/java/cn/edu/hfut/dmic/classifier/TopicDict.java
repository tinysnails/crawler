package cn.edu.hfut.dmic.classifier;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 加载主题字典
 */
public class TopicDict {

    /**
     * 根据文件路径获得主题字典
     * @param fileName
     * @return
     */
    public static HashSet<String> getDict(String fileName) throws IOException {
        /*建立字典*/
        HashSet<String> set = new HashSet<String>();

        /*读取字典文件*/
        String cacheStr = "";
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        while ((cacheStr = br.readLine())!= null){
            set.add(cacheStr);
        }
        br.close();
        return set;
    }

    public static void main(String[] args) throws IOException {
        HashSet set = getDict("sport.txt");

        System.out.println(set.size());
        System.out.println(set.contains("体育"));
        System.out.println(set.contains("足球"));
        System.out.println(set.contains("篮球"));
        System.out.println(set.contains("棋牌"));
    }
}

package cn.edu.hfut.dmic.webcollector.crawler_demos.bean;

import java.io.Serializable;

public class NewsBean implements Serializable {
    int id;
    int depth ;
    String host ;
    String title   ;
    String url;
    String content ;
    String html ;
    int type;

    // MyBatis期望空的构造器
    public NewsBean() {

    }

    public NewsBean(int id, int depth, String host, String title, String url, String content, String html, int type) {
        this.id = id;
        this.depth = depth;
        this.host = host;
        this.title = title;
        this.url = url;
        this.content = content;
        this.html = html;
        this.type = type;

    }

    public NewsBean(int depth, String host, String title, String url, String content, String html) {
        this.depth = depth;
        this.host = host;
        this.title = title;
        this.url = url;
        this.content = content;
        this.html = html;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

//    @Override
//    public String toString() {
//        return depth +"," + host + ","+ title +","+ url +","
//                + content + "," + html +","+ type + '\n';
//    }


    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", depth=" + depth +
                ", host='" + host + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", content='" + content + '\'' +
                ", html='" + html + '\'' +
                ", type=" + type +
                '}';
    }

    public static void main(String[] args) {
        System.out.println(new NewsBean(1,"h","dsf","df","df","sdf").toString());
    }
}

package cn.edu.hfut.dmic.webcollector.crawler_demos.bean;

import java.io.Serializable;

public class News implements Serializable {
    int id;
    int depth ;
    String host ;
    String title   ;
    String url;
    String content ;
    String html ;
    String type;

    public News(int depth, String host, String title, String url, String content, String html) {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return depth +"," + host + ","+ title +","+ url +","
                + content + "," + html +","+ type + '\n';
    }

    public static void main(String[] args) {
        System.out.println(new News(1,"h","dsf","df","df","sdf").toString());
    }
}

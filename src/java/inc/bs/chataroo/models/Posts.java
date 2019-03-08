package inc.bs.chataroo.models;

/**
 * Created by AkshayeJH on 24/07/17.
 */

public class Posts {

    private String content,type,title,views,plus,minus,count,author,commentcount,postid;
    private long time;
    private boolean seen;

    public Posts(String type, long time, boolean seen,  String content, String title, String views, String plus, String minus, String count, String author, String commentcount, String postid) {
        this.content = content;
        this.title = title;
        this.views = views;
        this.plus = plus;
        this.minus = minus;
        this.count = count;
        this.author = author;
        this.commentcount = commentcount;
        this.postid = postid;
        this.type = type;
        this.time = time;
        this.seen = seen;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getPlus() {
        return plus;
    }

    public void setPlus(String plus) {
        this.plus = plus;
    }

    public String getMinus() {
        return minus;
    }

    public void setMinus(String minus) {
        this.minus = minus;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCommentcount() {
        return commentcount;
    }

    public void setCommentcount(String commentcount) {
        this.commentcount = commentcount;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }


    public Posts (){ }

}

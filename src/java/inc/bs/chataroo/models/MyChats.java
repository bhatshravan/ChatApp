package inc.bs.chataroo.models;

/**
 * Created by AkshayeJH on 19/06/17.
 */

public class MyChats {

    private String uid;

    private String username,token;
    private String last;
    private Long last_time;




    public MyChats(){

    }

    public MyChats(String username, String token, String uid, String last, Long last_time) {
        this.uid = uid;
        this.username = username;
        this.token = token;
        this.last = last;
        this.last_time = last_time;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public Long getLast_time() {
        return last_time;
    }

    public void setLast_time(Long last_time) {
        this.last_time = last_time;
    }
}

package inc.bs.chataroo.models;

/**
 * Created by AkshayeJH on 19/06/17.
 */

public class Users {

    public String uid;

    public String username;
    public String image;
    public String status;
    private String image_thumbnail;

    private String token;



    public Users(){

    }

    public Users(String username, String image, String status, String image_thumbnail, String uid,String token) {
        this.username = username;
        this.uid=uid;
        this.image = image;
        this.status = status;
        this.image_thumbnail = image_thumbnail;
        this.token=token;
    }

    public String getusername() {
        return username;
    }

    public void setusername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getImage_thumbnail() {
        return image_thumbnail;
    }

    public void setImage_thumbnail(String image_thumbnail) {
        this.image_thumbnail = image_thumbnail;
    }
}

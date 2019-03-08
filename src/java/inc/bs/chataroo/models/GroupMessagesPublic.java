package inc.bs.chataroo.models;

/**
 * Created by AkshayeJH on 24/07/17.
 */

public class GroupMessagesPublic {

    private String message, type,name;
    private long  time;
    private String from;
    private String image;


    public GroupMessagesPublic(String message, String type, long time,String name,String image) {
        this.message = message;
        this.type = type;
        this.time = time;
        this.name = name;
       // this.seen = seen;
        this.image=image;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


    public GroupMessagesPublic(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

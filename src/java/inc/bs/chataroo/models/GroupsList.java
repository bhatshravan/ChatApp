package inc.bs.chataroo.models;

/**
 * Created by Shravan Bhat on 19/06/17.
 */

public class GroupsList {

    public String gid;

    public String name,image,image_thumbnail,description;


    public GroupsList(){

    }

    public GroupsList(String name, String image ,String image_thumbnail ,String gid,String description) {
        this.name = name;
        this.description=description;
        this.gid = gid;
        this.image = image;
        this.image_thumbnail = image_thumbnail;
    }

    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public String getUid() {
        return gid;
    }

    public void setUid(String gid) {
        this.gid = gid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return gid;
    }

    public void setStatus(String gid) {
        this.gid = gid;
    }

    public String getThumb_image() {
        return image_thumbnail;
    }

    public void setThumb_image(String image_thumbnail) {
        this.image_thumbnail = image_thumbnail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}

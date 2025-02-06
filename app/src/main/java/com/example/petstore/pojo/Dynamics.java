package com.example.petstore.pojo;

public class Dynamics {
    private String id;
    private String username;
    private String name;
    private String sendtime;
    private String content;

    //用户头像
    private String image;
    private String image_path;
    private Integer likeCount;
    private Boolean isLike;

    //无参构造
    public Dynamics(){

    }

    //含参构造
    public Dynamics(String id, String username, String name, String sendtime,
                      String content, String image, String image_path, Integer likeCount, Boolean isLike) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.sendtime = sendtime;
        this.content = content;
        this.image = image;
        this.image_path = image_path;
        this.likeCount = likeCount;
        this.isLike = isLike;
    }


    @Override
    public String toString() {
        return "Dynamics{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", sendtime='" + sendtime + '\'' +
                ", content='" + content + '\'' +
                ", image='" + image + '\'' +
                ", image_path='" + image_path + '\'' +
                ", likeCount=" + likeCount +
                ", isLike=" + isLike +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSendtime() {
        return sendtime;
    }

    public void setSendtime(String sendtime) {
        this.sendtime = sendtime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Boolean getLike() {
        return isLike;
    }

    public void setLike(Boolean like) {
        isLike = like;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}


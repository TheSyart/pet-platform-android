package com.example.petstore.pojo;

public class FeedingSkill {
    private Long id;

    private String title;

    private String content;

    private String image_path;

    private String createDate;

    // 无参构造方法
    public FeedingSkill() {
    }

    // 有参构造方法
    public FeedingSkill(Long id, String title, String content, String image_path, String createDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.image_path = image_path;
        this.createDate = createDate;
    }

    // Getter 和 Setter 方法（可选）
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}

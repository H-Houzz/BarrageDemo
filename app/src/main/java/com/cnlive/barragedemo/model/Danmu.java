package com.cnlive.barragedemo.model;

/**
 * @Author Mr.hou
 * @time 2017/3/16
 * @Desc
 */

public class Danmu {
    public long   id;
    public int    userId;
    public String type;
    public int    avatarUrl;
    public String content;
    public int textColor;
    public int textSize;
    public int position;

    public Danmu() {
    }

    public Danmu(long id, int userId, String type, int avatarUrl, String content) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.avatarUrl = avatarUrl;
        this.content = content;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(int avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Danmu(long id, int userId, String type, int avatarUrl, String content, int textColor, int textSize, int position) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.avatarUrl = avatarUrl;
        this.content = content;
        this.textColor = textColor;
        this.textSize = textSize;
        this.position = position;
    }
//    public Danmu(long id, int userId, String type, int avatarUrl, String content) {
//        this.id = id;
//        this.userId = userId;
//        this.type = type;
//        this.avatarUrl = avatarUrl;
//        this.content = content;
//    }

}

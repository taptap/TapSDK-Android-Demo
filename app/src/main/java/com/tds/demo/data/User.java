package com.tds.demo.data;

import cn.leancloud.LCUser;

/**
 * 2023/1/28
 * Describe：
 */
public class User extends LCUser {
    private String name;
    private String nickname;
    private String objectId;

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setId(String objectId) {
        this.objectId = objectId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public String getObjectId() {
        return objectId;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", nickname='" + nickname + '\'' +
                ", id='" + objectId + '\'' +
                '}';
    }
}

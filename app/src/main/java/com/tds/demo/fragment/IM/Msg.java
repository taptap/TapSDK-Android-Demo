package com.tds.demo.fragment.IM;

/**
 * 2022/11/1
 * Describe：定义消息的实体类Msg 用两个常量来表示消息的类型（接收的还是发送的）
 */

public class Msg {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;
    private String content;
    private int type;

    public Msg(String content,int type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }
    public int getType() {
        return type;
    }
}
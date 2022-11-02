package com.tds.demo.fragment.IM;

/**
 * 2022/11/1
 * Describe：定义消息的实体类Msg 用两个常量来表示消息的类型（接收的还是发送的）
 */

public class Msg {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;
    public static final int TEXT_TYPE = -1; // 表示纯文本
    public static final int IMG_TYPE = -2;  // 表示图文
    public static final int PLACE_SENT = -5;  // 表示地理位置


    private String content;
    private String imagePath;
    private int type;
    private int msg_type;

    public Msg() {
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setMsg_type(int msg_type) {
        this.msg_type = msg_type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }
    public int getType() {
        return type;
    }

    public int getMsg_type() {
        return msg_type;
    }

    public String getImagePath() {
        return imagePath;
    }
}
package com.tds.demo.data;


import com.tds.demo.R;

import java.util.ArrayList;

public class SDKTypeData {

    private static volatile SDKTypeData sdkTypeData;

    public ArrayList<String> groupList = null;
    public ArrayList<ArrayList<String>> childList = null;
    public ArrayList<ArrayList> iconChildList = null;

    private SDKTypeData(){
        createData();
    }

    public static SDKTypeData getINSTANCE() {
        if (sdkTypeData == null){
            synchronized (SDKTypeData.class) {
                if (sdkTypeData == null){
                    sdkTypeData = new SDKTypeData();
                }
            }
        }
        return sdkTypeData;
    }

    public void createData(){

        groupList = new ArrayList<String>();
        groupList.add("生态服务");
        groupList.add("云服务");
        groupList.add("运营工具");
        groupList.add("营收变现");

        ArrayList<String> itemList1 = new ArrayList<String>();
        itemList1.add("TapTap登录");
        itemList1.add("内嵌动态");
        itemList1.add("正版验证");
        itemList1.add("防沉迷-合规认证");

        ArrayList<String> itemList2 = new ArrayList<String>();
        itemList2.add("内建账户");
//        itemList2.add("好友");
        itemList2.add("成就");
        itemList2.add("排行榜");
        itemList2.add("云存档");
        itemList2.add("数据存储");
//        itemList2.add("即时通讯");
//        itemList2.add("推送通知");

        ArrayList<String> itemList3 = new ArrayList<String>();
        itemList3.add("礼包系统");
        itemList3.add("客服系统");

        ArrayList<String> itemList4 = new ArrayList<String>();
        itemList4.add("支付系统");
        itemList4.add("DLC 内购");


        ArrayList iconList1 = new ArrayList();
        iconList1.add(R.mipmap.aa);
        iconList1.add(R.mipmap.bb);
        iconList1.add(R.mipmap.cc);
        iconList1.add(R.mipmap.dd);

        ArrayList iconList2 = new ArrayList();
        iconList2.add(R.mipmap.ee);
//        iconList2.add(R.mipmap.ff);
        iconList2.add(R.mipmap.hh);
        iconList2.add(R.mipmap.ii);
        iconList2.add(R.mipmap.jj);
        iconList2.add(R.mipmap.kk);
//        iconList2.add(R.mipmap.nn);
//        iconList2.add(R.mipmap.oo);

        ArrayList iconList3 = new ArrayList();
        iconList3.add(R.mipmap.rr);
        iconList3.add(R.mipmap.ee);


        ArrayList iconList4 = new ArrayList();
        iconList4.add(R.mipmap.nc);
        iconList4.add(R.mipmap.mm);


        childList = new ArrayList<ArrayList<String>>();
        childList.add(itemList1);
        childList.add(itemList2);
        childList.add(itemList3);
        childList.add(itemList4);

        iconChildList = new ArrayList<ArrayList>();
        iconChildList.add(iconList1);
        iconChildList.add(iconList2);
        iconChildList.add(iconList3);
        iconChildList.add(iconList4);

    }



}

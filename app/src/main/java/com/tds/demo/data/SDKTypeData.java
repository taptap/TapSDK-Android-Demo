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

        ArrayList<String> itemListEcological = new ArrayList<String>();
        itemListEcological.add("TapTap登录");
        itemListEcological.add("内嵌动态");
        itemListEcological.add("正版验证");
        itemListEcological.add("合规认证");

        ArrayList<String> itemListCloud = new ArrayList<String>();
        itemListCloud.add("内建账户");
        itemListCloud.add("成就");
        itemListCloud.add("排行榜");
        itemListCloud.add("云存档");
        itemListCloud.add("数据存储");


        ArrayList<String> itemListOperate = new ArrayList<String>();
        itemListOperate.add("礼包系统");
        itemListOperate.add("客服系统");


        ArrayList iconListEcological = new ArrayList();
        iconListEcological.add(R.mipmap.aa);
        iconListEcological.add(R.mipmap.bb);
        iconListEcological.add(R.mipmap.cc);
        iconListEcological.add(R.mipmap.dd);

        ArrayList iconListCloud = new ArrayList();
        iconListCloud.add(R.mipmap.ee);
        iconListCloud.add(R.mipmap.hh);
        iconListCloud.add(R.mipmap.ii);
        iconListCloud.add(R.mipmap.jj);
        iconListCloud.add(R.mipmap.kk);


        ArrayList iconListOperate = new ArrayList();
        iconListOperate.add(R.mipmap.rr);
        iconListOperate.add(R.mipmap.ee);



        childList = new ArrayList<>();
        childList.add(itemListEcological);
        childList.add(itemListCloud);
        childList.add(itemListOperate);

        iconChildList = new ArrayList<>();
        iconChildList.add(iconListEcological);
        iconChildList.add(iconListCloud);
        iconChildList.add(iconListOperate);

    }



}

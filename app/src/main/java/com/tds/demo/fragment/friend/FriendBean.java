package com.tds.demo.fragment.friend;

import com.tapsdk.friends.entities.TDSFriendInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.leancloud.LCFriendshipRequest;

/**
 * 2022/10/20
 * Describe：
 */
public class FriendBean implements Serializable {

    private List<LCFriendshipRequest> requests = new ArrayList<>();
    private List<TDSFriendInfo> tDSFriendInfo = new ArrayList<>();

    public List<LCFriendshipRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<LCFriendshipRequest> requests) {
        this.requests = requests;
    }

    public void settDSFriendInfo(List<TDSFriendInfo> tDSFriendInfo) {
        this.tDSFriendInfo = tDSFriendInfo;
    }

    public List<TDSFriendInfo> gettDSFriendInfo() {
        return tDSFriendInfo;
    }

    @Override
    public String toString() {
        return "FriendBean{" +
                "requests=" + requests +
                ", tDSFriendInfo=" + tDSFriendInfo +
                '}';
    }
}

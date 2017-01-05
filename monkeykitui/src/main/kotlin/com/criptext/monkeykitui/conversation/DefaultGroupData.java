package com.criptext.monkeykitui.conversation;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

import com.criptext.monkeykitui.recycler.MonkeyInfo;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by daniel on 8/29/16.
 */

public class DefaultGroupData implements com.criptext.monkeykitui.recycler.GroupChat{

    private String conversationId;
    private LinkedHashMap<String, MonkeyInfo> userHashMap;
    ArrayList<MonkeyInfo> infoList;
    private String membersOnline;
    private String membersTyping;
    private String admins;
    private List<Integer> colorsForUsersInGroup;
    private int MAX_PARTICIPANTS = 50;

    public DefaultGroupData(String conversationId, String members, Activity act){
        this.conversationId = conversationId;
        userHashMap = new LinkedHashMap<>();
        infoList = new ArrayList<>();
        membersOnline = "";
        membersTyping = "";
        initColorsForGroup();
        //TODO GET MEMBERS FROM DB
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setMembers(List<MonkeyInfo> monkeyInfoItems){
        userHashMap.clear();
        for (MonkeyInfo monkeyInfoItem : monkeyInfoItems) {
            monkeyInfoItem.setColor(colorsForUsersInGroup.get(userHashMap.size()%MAX_PARTICIPANTS));
            userHashMap.put(monkeyInfoItem.getInfoId(), monkeyInfoItem);
        }
    }

    public void removeMember(String monkeyId){
        userHashMap.remove(monkeyId);
    }

    public void addMember(MonkeyInfo infoItem){
        if(userHashMap.get(infoItem.getInfoId()) != null){
            return;
        }
        infoItem.setColor(colorsForUsersInGroup.get(userHashMap.size()%MAX_PARTICIPANTS));
        userHashMap.put(infoItem.getInfoId(), infoItem);
    }


    public HashMap<String, MonkeyInfo> getUsers() {
        return userHashMap;
    }

    public void removeMemberTyping(String monkeyId){
        membersTyping = membersTyping.replace(monkeyId, "");
        membersTyping = membersTyping.replace(",,", ",");
        if (membersTyping.endsWith(",")) {
            membersTyping = membersTyping.substring(0, membersTyping.length()-1);
        }
    }

    public void addMemberTyping(String memberId){
        if(membersTyping.contains(memberId)){
            return;
        }
        if(membersTyping.length() <= 0){
            membersTyping = memberId;
        }else{
            membersTyping = membersTyping.concat("," + memberId);
        }
    }


    public void setMembersOnline(String membersOnline){
        this.membersOnline = membersOnline;
    }

    public String getMembersOnline(){
        return membersOnline;
    }

    public void setAdmins(String admins){
        this.admins = admins;
    }

    public String getAdmins(){
        return admins;
    }

    @NotNull
    @Override
    public String getMemberName(@NotNull String monkeyId) {
        MonkeyInfo user = userHashMap.get(monkeyId);
        String finalName = "Unknown";
        if(user!=null && !user.getTitle().isEmpty() ){
            finalName = user.getTitle();
        }
        return finalName;
    }

    @Override
    public int getMemberColor(@NotNull String monkeyId) {
        if(userHashMap.get(monkeyId) != null){
            return userHashMap.get(monkeyId).getColor();
        }
        return colorsForUsersInGroup.get(0);
    }

    public void setInfoList(String myMonkeyId, String myName){
        infoList.clear();
        HashMap<String, MonkeyInfo> users = this.getUsers();

        for (MonkeyInfo user : users.values()){
            if(user.getInfoId() == null || user.getInfoId().isEmpty()){
                continue;
            }
            String connection = "Offline";
            String tag = "";

            if(this.getAdmins() != null && this.getAdmins().contains(user.getInfoId())){
                tag = "Admin";
            }
            if(this.getMembersOnline() != null && this.getMembersOnline().contains(user.getInfoId())){
                connection = "Online";
            }else if(myMonkeyId != null && myMonkeyId.equals(user.getInfoId())){
                connection = "Online";
            }
            user.setSubtitle(connection);
            user.setRightTitle(tag);
            infoList.add(user);
        }
        Collections.sort(infoList, new Comparator<MonkeyInfo>() {
            @Override
            public int compare(MonkeyInfo lhs, MonkeyInfo rhs) {
                return lhs.getTitle().toLowerCase().compareTo(rhs.getTitle().toLowerCase());
            }
        });
    }

    public String getMembersNameTyping(){
        HashMap<String, MonkeyInfo> users = this.getUsers();
        String members = "";

        for (MonkeyInfo user : users.values()){
            if(!user.getInfoId().isEmpty() && membersTyping.contains(user.getInfoId())){
                if(members.isEmpty()){
                    members += user.getTitle();
                }else{
                    members += ", " + user.getTitle();
                }
            }
        }

        if(!members.isEmpty()){
            return members + " typing...";
        }

        if(membersOnline.split(",").length > 1){
            return membersOnline.split(",").length + " members online";
        }else if(membersOnline.split(",").length == 1 && !membersOnline.isEmpty()){
            return "1 member online";
        }

        return "";
    }

    public ArrayList<MonkeyInfo> getInfoList(){
        return infoList;
    }

    private void initColorsForGroup(){
        colorsForUsersInGroup=new ArrayList<>();
        colorsForUsersInGroup.add(Color.rgb(111,6,123));
        colorsForUsersInGroup.add(Color.rgb(0,164,158));
        colorsForUsersInGroup.add(Color.rgb(179,0,124));
        colorsForUsersInGroup.add(Color.rgb(180,216,0));
        colorsForUsersInGroup.add(Color.rgb(226,0,104));
        colorsForUsersInGroup.add(Color.rgb(0,178,235));
        colorsForUsersInGroup.add(Color.rgb(236,135,14));
        colorsForUsersInGroup.add(Color.rgb(132,176,185));
        colorsForUsersInGroup.add(Color.rgb(58,106,116));
        colorsForUsersInGroup.add(Color.rgb(189, 167, 0));
        colorsForUsersInGroup.add(Color.rgb(130, 106, 169));
        colorsForUsersInGroup.add(Color.rgb(175,64,42));
        colorsForUsersInGroup.add(Color.rgb(115, 54, 16));
        colorsForUsersInGroup.add(Color.rgb(2,13,216));
        colorsForUsersInGroup.add(Color.rgb(126,101,101));
        colorsForUsersInGroup.add(Color.rgb(205,121,103));
        colorsForUsersInGroup.add(Color.rgb(253,120,167));
        colorsForUsersInGroup.add(Color.rgb(0,159,98));
        colorsForUsersInGroup.add(Color.rgb(51, 102, 51));
        colorsForUsersInGroup.add(Color.rgb(233,156,122));
        colorsForUsersInGroup.add(Color.rgb(111,6,123));
        colorsForUsersInGroup.add(Color.rgb(0,164,158));
        colorsForUsersInGroup.add(Color.rgb(179,0,124));
        colorsForUsersInGroup.add(Color.rgb(180,216,0));
        colorsForUsersInGroup.add(Color.rgb(226,0,104));
        colorsForUsersInGroup.add(Color.rgb(0,178,235));
        colorsForUsersInGroup.add(Color.rgb(236,135,14));
        colorsForUsersInGroup.add(Color.rgb(132,176,185));
        colorsForUsersInGroup.add(Color.rgb(58,106,116));
        colorsForUsersInGroup.add(Color.rgb(189, 167, 0));
        colorsForUsersInGroup.add(Color.rgb(130, 106, 169));
        colorsForUsersInGroup.add(Color.rgb(175,64,42));
        colorsForUsersInGroup.add(Color.rgb(115, 54, 16));
        colorsForUsersInGroup.add(Color.rgb(2,13,216));
        colorsForUsersInGroup.add(Color.rgb(126,101,101));
        colorsForUsersInGroup.add(Color.rgb(205,121,103));
        colorsForUsersInGroup.add(Color.rgb(253,120,167));
        colorsForUsersInGroup.add(Color.rgb(0,159,98));
        colorsForUsersInGroup.add(Color.rgb(51, 102, 51));
        colorsForUsersInGroup.add(Color.rgb(233,156,122));
        colorsForUsersInGroup.add(Color.rgb(111,6,123));
        colorsForUsersInGroup.add(Color.rgb(0,164,158));
        colorsForUsersInGroup.add(Color.rgb(179,0,124));
        colorsForUsersInGroup.add(Color.rgb(180,216,0));
        colorsForUsersInGroup.add(Color.rgb(226,0,104));
        colorsForUsersInGroup.add(Color.rgb(0,178,235));
        colorsForUsersInGroup.add(Color.rgb(236,135,14));
        colorsForUsersInGroup.add(Color.rgb(132,176,185));
        colorsForUsersInGroup.add(Color.rgb(58,106,116));
        colorsForUsersInGroup.add(Color.rgb(189, 167, 0));
        colorsForUsersInGroup.add(Color.rgb(0,0,0));
    }
}

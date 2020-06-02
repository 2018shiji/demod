package com.example.demo.mongo;

/**
 * Created by DGM on 2019/10/25.
 */
public enum Channel {
    Food("美食"),
    Emotion("情感"),
    Pet("宠物"),
    House("家居"),
    Marriage("征婚"),
    Education("教育"),
    Travel("旅游");

    String oldName;

    Channel(String oldName){
        this.oldName = oldName;
    }

    String getOldName(){
        return oldName;
    }

    static String toNewName(String oldName){
        for(Channel channel : values()){
            if(channel.oldName.equalsIgnoreCase(oldName)){
                return channel.name();
            }
        }
        return "";
    }

    static Channel random(){
        Channel[] channels = values();
        int idx = (int)(Math.random() * channels.length);
        return channels[idx];
    }

}

package com.example.demo.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Created by DGM on 2019/10/25.
 */
public class ChangeTask implements Runnable {
    private String taskType;
    private long change_during = 3000;

    @Autowired
    MongoLink mongoLink;
    MongoDatabase database = mongoLink.getLinker().getDatabase("test");
    MongoCollection<Document> topicCollection = database.getCollection("testChannel");


    public ChangeTask(String taskType){
        this.taskType = taskType;
    }

    @Override
    public void run() {
        long startAt = System.currentTimeMillis();

        try {
            while (true) {
                if (taskType.equals("insert")) {

                } else if (taskType.equals("update")) {
                    doUpdate();
                } else if (taskType.equals("replace")) {

                } else if (taskType.equals("delete")) {

                }
                Thread.sleep(200);
                long currentAt = System.currentTimeMillis();
                if(currentAt - startAt > change_during){
                    break;
                }

            }
        }catch(InterruptedException e){

        }

    }

    private void doUpdate(){
        Document random = mongoLink.getRandom();
        if(random == null)return;

        Channel channel = Channel.random();

        random.put("field_channel", channel.getOldName());
        random.put("createTime", new Date());
        topicCollection.updateOne(new Document("_id", random.get("_id")), new Document("$set", random));

    }

    private void doInsert(){

    }

    private void doReplace(){

    }

    private void doDelete(){

    }
}

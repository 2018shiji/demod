package com.example.demo.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Created by DGM on 2019/10/25.
 */
public class MigrateTask implements Runnable {

    @Autowired
    MongoLink mongoLink;
    MongoDatabase database = mongoLink.getLinker().getDatabase("test");
    final MongoCollection<Document> topicColletion = database.getCollection("testChannel");
    final MongoCollection<Document> topicColletion_incr = database.getCollection("testChannel_incr");

    @Override
    public void run() {
        doMigrateTask();
    }

    private void doMigrateTask(){
        Document maxDoc = topicColletion.find().sort(new Document("_id", -1)).first();
        if(maxDoc == null){
            System.out.println("FullTransferTask detect no data, quit.");
            return;
        }

        ObjectId maxID = maxDoc.getObjectId("_id");
        System.out.println("FullTransferTask maxId is  " + maxID.toHexString());;

        AtomicInteger count = new AtomicInteger(0);
        topicColletion.find(new Document("_id", new Document("$lte", maxID)))
                .forEach(new Consumer<Document>(){
                    @Override
                    public void accept(Document topic) {
                        Document topicNew = new Document(topic);
                        //channel转换
                        String oldChannel = topic.getString("field_channel");
                        topicNew.put("field_channel", Channel.toNewName(oldChannel));

                        topicColletion_incr.insertOne(topicNew);
                        if(count.incrementAndGet() % 100 == 0){
                            System.out.println("FullTransferTask progress:{ }" + count.get());
                        }

                    }
                });
    }

}

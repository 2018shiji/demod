package com.example.demo.mongo;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.FullDocument;
import com.mongodb.client.model.changestream.OperationType;
import org.bson.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * mongoDB实时同步
 * http://www.sohu.com/a/245860127_411876
 * https://www.cnblogs.com/littleatp/p/11386487.html
 * Created by DGM on 2019/10/25.
 */
@Component
public class MongoLink {

    MongoClient mongoClient;

    public synchronized MongoClient getLinker() {
        if(null == mongoClient) {
            //服务器实例表
            List servers = new ArrayList<>();
            servers.add(new ServerAddress("localhost", 27017));

            //配置构建器
            MongoClientSettings.Builder settingBuilder = MongoClientSettings.builder();

            //传入服务器实例
            settingBuilder.applyToClusterSettings(builder -> builder.hosts(servers));

            //构建Client实例
            mongoClient = MongoClients.create(settingBuilder.build());
        }

        return mongoClient;
    }

    public void operateMongo(){
        //获得数据库对象
        MongoDatabase database = mongoClient.getDatabase("test");

        //获得集合
        MongoCollection collection = database.getCollection("testUser");

//        collection.watch().cursor();
        ChangeStreamIterable iterable = collection.watch().fullDocument(FullDocument.UPDATE_LOOKUP);
        System.out.println(iterable);



    }

    /**
     * 为testChannel预写入1w条记录
     */
    private void preInsertData(){
        MongoDatabase database = mongoClient.getDatabase("test");
        MongoCollection<Document> topicColletion = database.getCollection("testChannel");

        //分批写入，共写入1w条数据
        int current = 0;
        int batchSize = 100;

        while(current < 10000){
            List<Document> topicDocs = new ArrayList<>();

            for(int j = 0; j < batchSize; j++){
                Channel channel = Channel.random();

                Document topicDoc = new Document();
                topicDoc.append("field_channel", channel.getOldName());
                topicDoc.append("title", "This is the title -- " + UUID.randomUUID().toString());
                topicDoc.append("author", "Lilei");
                topicDoc.append("createTime", new Date());

                topicDocs.add(topicDoc);
            }

            topicColletion.insertMany(topicDocs);
            current += batchSize;
            System.out.println("now has insert " + current + " records");
        }
    }

    /**
     * 开启监听任务，将Channel上的所有变更写入到增量表：
     */
    public void onWatchTask(){
        MongoDatabase database = mongoClient.getDatabase("test");
        MongoCollection<Document> topicColletion = database.getCollection("testChannel");
        MongoCollection<Document> topicColletion_incr = database.getCollection("testChannel_incr");

        //启用FullDocument.update_lookup选项
        MongoCursor<ChangeStreamDocument<Document>> iterator = topicColletion.watch().fullDocument(FullDocument.UPDATE_LOOKUP).iterator();
        while(iterator.hasNext()){
            ChangeStreamDocument<Document> changeEvent = iterator.next();
            OperationType type = changeEvent.getOperationType();

            if(type == OperationType.INSERT || type == OperationType.UPDATE
                    || type == OperationType.REPLACE || type == OperationType.DELETE){
                Document incrDoc = new Document("field_OP", type.getValue());
                incrDoc.append("field_key", changeEvent.getDocumentKey().get("_id"));
                incrDoc.append("field_data", changeEvent.getFullDocument());
                topicColletion_incr.insertOne(incrDoc);
            }
        }

    }

    public Document getRandom(){
        MongoDatabase database = mongoClient.getDatabase("test");
        MongoCollection<Document> topicColletion = database.getCollection("testChannel");
        return topicColletion.find().first();
//        return topicColletion.find().cursor().next();
    }

    public static void main(String[] args){
        MongoLink mongoLink = new MongoLink();
        mongoLink.getLinker();
        mongoLink.operateMongo();
    }
}

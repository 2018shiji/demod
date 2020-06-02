package com.example.demo.bootstrap_table;

import com.mongodb.client.MongoCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Created by DGM on 2019/10/31.
 */
@Component
public class MyMongoClient {

    @Autowired
    private MongoOperations mongoOperations;

    public <Row> Row getSingleResult(int objectId, Class<Row> objClass, String collectionName)  {

        Row result = mongoOperations.findById(objectId, objClass, collectionName);
        return result;
    }

    public <Rows> List<Rows> getAllResults(Class<Rows> objClass, String collectionName){
        List<Rows> results = mongoOperations.findAll(objClass, collectionName);
        return results;
    }

    public void insertCollection(Class objClass){
        CollectionOptions options = CollectionOptions.empty();
        MongoCollection collection = mongoOperations.createCollection(objClass, options);
        System.out.println("-------------------------->插入collection" + collection);
    }

    public <Doc> Doc insertDocument(Doc docToSave, String collectionName){
        Doc document = mongoOperations.insert(docToSave, collectionName);
        return document;
    }

}

package com.example.demo.bootstrap_table;

import com.example.demo.test.MyHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * https://www.cnblogs.com/samve/p/9757847.html
 * http://www.itxst.com/bootstrap-table-events/
 * Created by DGM on 2019/11/11.
 */
@Controller
public class TableController implements WebMvcConfigurer {

    @Autowired
    MyMongoClient myMongoClient;

    @RequestMapping("/111")
    public String index() {
//        myMongoClient.insertCollection(UserInfo.class);
//        for(int i = 0; i < 100; i++){
//            myMongoClient.insertDocument(new UserInfo("李壮杰" + i, "lzj2577577", "555", "0202", "200", "100"), "userInfo");
//        }

        return "table";
    }

    @RequestMapping("/getTableHeader")
    @ResponseBody
    public List<TableHeader> tableHeaderData(){
        List<TableHeader> tableHeader = new ArrayList<>();
        tableHeader.add(new TableHeader("username", "用户名"));
        tableHeader.add(new TableHeader("fullname", "姓名"));
        tableHeader.add(new TableHeader("status", "密码认证"));
        tableHeader.add(new TableHeader("availableSpace", "智能卡认证"));
        tableHeader.add(new TableHeader("totalSpace", "个人空间配额"));
        return tableHeader;
    }

    @RequestMapping("/getTableContent")
    @ResponseBody
    public List<UserInfo> tableData(){
        List<UserInfo> userInfos = myMongoClient.getAllResults(UserInfo.class, "userInfo");
        System.out.println(userInfos);
        return userInfos;
    }

    @RequestMapping("/changeTableContent")
    @ResponseBody
    public void changeMongoContent(String index, String field, String value){

        System.out.println("index" + index + "    field" + field + "    value" + value);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MyHandlerInterceptor());
    }

}

package com.example.demo.bootstrap_table;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfo {
    private String userName;
    private String fullName;
    private String status;
    private String availableSpace;
    private String totalSpace;
    private String storageServer;

}

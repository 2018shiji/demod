package com.example.demo.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by DGM on 2019/10/24.
 */
public class CommandStreamGobbler extends Thread {
    private InputStream is;
    private String command;
    private String prefix = "";
    private boolean readFinish = false;
    private boolean ready = false;
    private List<String> infoList = new LinkedList();

    CommandStreamGobbler(InputStream is, String command, String prefix){
        this.is = is;
        this.command = command;
        this.prefix = prefix;
    }

    @Override
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is, Charset.forName("GBK"));
            BufferedReader br = new BufferedReader(isr);
            String line;
            ready = true;
            while ((line = br.readLine()) != null) {
                infoList.add(line);
                System.out.println(prefix + " line: " + line);
            }
        } catch (IOException e){

        }finally {

        }
    }

    public InputStream getIs(){
        return is;
    }

    public String getCommand(){
        return command;
    }

    public boolean isReadFinish(){
        return readFinish;
    }

    public boolean isReady(){
        return ready;
    }

    public List<String> getInfoList(){
        return infoList;
    }

}

package com.example.demo.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

/**
 * https://blog.csdn.net/lance_wyvern/article/details/50456903
 * https://blog.csdn.net/vcfriend/article/details/81226632
 * Created by DGM on 2019/10/24.
 */
public class CommandStreamGobbler2 extends Thread {
    private InputStream is;
    private String command;
    private String prefix = "";
    private boolean readFinish = false;
    private boolean ready = false;

    //命令执行结果，0：执行中 1：超时 2：执行完成
    private int commandResult = 0;
    private List<String> infoList = new LinkedList<>();

    public CommandStreamGobbler2(InputStream is, String command, String prefix){
        this.is = is;
        this.command = command;
        this.prefix = prefix;
    }

    @Override
    public void run() {
        InputStreamReader isr = null;
        BufferedReader br = null;
        try{
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            String line = null;
            ready = true;
            while(commandResult != 1){
                if(br.ready() || commandResult == 2){
                    if((line = br.readLine()) != null)
                        infoList.add(line);
                    else
                        break;
                } else {
                    Thread.sleep(100);
                }
            }
        } catch (IOException | InterruptedException e){
            System.out.println("IO | Interrupt异常");
        } finally {
            try{
                if(br != null)br.close();
                if(isr != null)isr.close();
            } catch (IOException e){
                System.out.println("IO异常");
            }
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

    public void setTimeout(int timeout){
        this.commandResult = timeout;
    }

}

package com.example.demo.shell;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * https://my.oschina.net/u/1473861/blog/1930129
 * Created by DGM on 2019/10/24.
 */
public class ProcessUtil {

    private static final ExecutorService EXECUTORS = Executors.newFixedThreadPool(4);

    public static ProcessRunner buildProcessRunner(){
        return new ProcessRunner();
    }

    public static class ProcessRunner {
        private int maxErrorLineNumb = 100;

        private int maxInputLineNumb = 500;

        /**
         * @param cmd           命令
         * @param cmdInputParas 执行命令需要输入的参数，比如命令行登录数据库需要输入密码
         * @return 返回值，包括错误回显，正确回显，整个脚本执行结果
         */
        public Result runCMD(List<String> cmd, List<String> cmdInputParas) {
            Process process = null;
            BufferedWriter bw = null;

            try {
                ProcessBuilder processBuilder = new ProcessBuilder(cmd);
                process = processBuilder.start();
                bw = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

                for (String p : cmdInputParas) {
                    bw.write(p);
                    bw.newLine();
                }

                bw.flush();
                bw.close();

                Callable<List<String>> inputRunner = new Runner(maxInputLineNumb, process.getInputStream());
                FutureTask<List<String>> inputTask = new FutureTask<>(inputRunner);
                EXECUTORS.execute(inputTask);

                Callable<List<String>> errorRunnable = new Runner(maxErrorLineNumb, process.getErrorStream());
                FutureTask<List<String>> errorTask = new FutureTask<>(errorRunnable);
                EXECUTORS.execute(errorTask);

                List<String> inputResult = inputTask.get();
                List<String> errorResult = errorTask.get();

                int returnCode = process.waitFor();
                return new Result(inputResult, errorResult, returnCode);

            } catch (IOException | InterruptedException | ExecutionException e) {
                e.printStackTrace();

            } finally {
                try{if(bw != null)bw.close();}catch (IOException e){}
                if(process != null)process.destroy();
            }

            return new Result();
        }

        public Result runCMD(List<String> cmd){
            return runCMD(cmd, Arrays.asList());
        }

    }

    /**
     * 执行命令类
     */
    private static class Runner implements Callable<List<String>>{

        private int maxLineNumb;

        private InputStream inputStream;

        public Runner(int maxLineNumb, InputStream inputStream){
            super();
            this.maxLineNumb = maxLineNumb;
            this.inputStream = inputStream;
        }

        @Override
        public List<String> call() throws Exception {
            List<String> result = new ArrayList<>();
            BufferedReader br = null;
            try{
                br = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("GBK")));
                int i = 0;
                String line = null;
                while(null != (line = br.readLine()) && (i++ < maxLineNumb)){
                    result.add(line);
                }
                return result;
            } finally {
                if(br != null) br.close();
            }
        }

    }


    public static class Result{
        //回显
        private List<String> inputInfos;
        //错误回显
        private List<String> errorInfos;
        //返回码
        private int returnCode = -1;

        public Result(){}

        public Result(List<String> inputInfos, List<String> errorInfos, int returnCode){
            this.inputInfos = inputInfos;
            this.errorInfos = errorInfos;
            this.returnCode = returnCode;
        }

        @Override
        public String toString()
        {
            return "Result [inputInfos=" + inputInfos + ", errorInfos=" + errorInfos + ", returnCode=" + returnCode + "]";
        }

        public boolean isSuccess(Judge judge){
            if(errorInfos == null || inputInfos == null || returnCode == -1)
                return false;
            return judge.isSuccess(inputInfos, errorInfos, returnCode);
        }

        /**
         * 脚本调用结果接口化
         */
        interface Judge{
            boolean isSuccess(List<String> inputInfos, List<String> errorInfos, int returnCode);
        }
    }

    public static void main(String[] args){
        ProcessRunner runner = ProcessUtil.buildProcessRunner();
        Result result = runner.runCMD(Arrays.asList("cmd", "/C", "ping 127.0.0.1 -n 1"));
        System.out.println(result);
        boolean isRight = result.isSuccess((inputInfos, errorInfos, returnCode) -> returnCode == 0 && errorInfos.size() == 0);
        System.out.println(isRight);
    }

}

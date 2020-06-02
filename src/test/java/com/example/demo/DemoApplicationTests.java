package com.example.demo;


import com.example.demo.shell.CommandStreamGobbler2;
import com.example.demo.shell.CommandWaitForThread;
import com.example.demo.test.IMyClass;
import com.example.demo.test.MyClass;
import com.example.demo.test.MyClassProxy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Proxy;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Scanner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

	@Test
	public void contextLoads() {

	}

	@Test
	public void testbat(){
		InputStreamReader stdISR = null;
		InputStreamReader errISR = null;
		Process process = null;
		String command = "cmd /c C:\\Users\\DGM.DESKTOP-HJTAO\\Desktop\\testbat.bat";
		try{
			process = Runtime.getRuntime().exec(command);
			int exitValue = process.waitFor();

			String line;

			stdISR = new InputStreamReader(process.getInputStream(), Charset.forName("GBK"));
			BufferedReader stdBR = new BufferedReader(stdISR);

			while((line = stdBR.readLine()) != null){
				System.out.println("STD line:" + line);
			}

			errISR = new InputStreamReader(process.getErrorStream());
			BufferedReader errBR = new BufferedReader(errISR);

			while((line = errBR.readLine()) != null){
				System.out.println("ERR line: " + line);
			}
		} catch(IOException | InterruptedException e){
			e.printStackTrace();
		} finally {
			try{
				if(stdISR != null)stdISR.close();
				if(errISR != null)errISR.close();
				if(process != null)process.destroy();
			} catch (IOException e){
				System.out.println("IO异常");
			}
		}

	}

	@Test
	public void testBat2(){
		InputStreamReader stdISR = null;
		InputStreamReader errISR = null;
		Process process = null;
		String command = "cmd /c C:\\Users\\DGM.DESKTOP-HJTAO\\Desktop\\testbat.bat";
		long timeout = 10 * 1000;
		try{
			process = Runtime.getRuntime().exec(command);

			CommandStreamGobbler2 errorGobbler = new CommandStreamGobbler2(process.getErrorStream(), command, "ERR");
			CommandStreamGobbler2 outputGobbler = new CommandStreamGobbler2(process.getInputStream(), command, "STD");

			//必须先等待错误输出准备完毕后再建立标准输出
			errorGobbler.start();
			while(!errorGobbler.isReady()){
				Thread.sleep(10);
			}

			outputGobbler.start();
			while(!outputGobbler.isReady()){
				Thread.sleep(10);
			}

			CommandWaitForThread commandThread = new CommandWaitForThread(process);
			commandThread.start();

			long commandTime = new Date().getTime();
			long nowTime = new Date().getTime();
			boolean timeoutFlag = false;

			while(!commanIsFinish(commandThread, errorGobbler, outputGobbler)){
				if(nowTime - commandTime > timeout){
					timeoutFlag = true;
					break;
				} else {
					Thread.sleep(100);
					nowTime = new Date().getTime();
				}
			}

			if(timeoutFlag){
				//命令超时
				errorGobbler.setTimeout(1);
				outputGobbler.setTimeout(1);
				System.out.println("命令: " + command + " 超时");
			} else {
				//命令执行完成
				errorGobbler.setTimeout(2);
				outputGobbler.setTimeout(2);
			}

			while(true){
				if(errorGobbler.isReadFinish() && outputGobbler.isReadFinish())
					break;
				Thread.sleep(10);
			}

		} catch(IOException | InterruptedException e){
			e.printStackTrace();

		} finally {
			if(process != null)
				process.destroy();
		}
	}

	private boolean commanIsFinish(CommandWaitForThread commandThread, CommandStreamGobbler2 errorGobbler,
								   CommandStreamGobbler2 outputGobbler){
		if(commandThread != null){
			return commandThread.isFinish();
		} else {
			return (errorGobbler.isReadFinish() && outputGobbler.isReadFinish());
		}
	}

	@Test
	public void test(){
		IMyClass proxy = (IMyClass)MyClassProxy.getProxyInstance(MyClass.class);
		System.out.println("------------------------------");
		System.out.println(proxy);

		proxy.print();
	}

}

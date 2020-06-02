package com.example.demo.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MyClassProxy {
    public static Object getProxyInstance(Class target){
        return Proxy.newProxyInstance(MyClassProxy.class.getClassLoader(), target.getInterfaces(), new ProxyHandler());
    }

    static class ProxyHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("proxy handler invoke");
            System.out.println("invoke method:" + method.getName());
            return null;
        }
    }
}

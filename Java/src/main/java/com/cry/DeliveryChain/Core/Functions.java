package com.cry.DeliveryChain.Core;

public class Functions {
    public Functions() {}

    public void Logger(String message) {
        try {
            System.out.println("--**--" + Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
            System.out.println(message);
            System.out.println("--**--" + Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
        }
        catch (Exception e) {
            System.out.println("--**--" + Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
            System.err.println(e.getMessage());
            System.out.println("--**--" + Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
        }
    }
}

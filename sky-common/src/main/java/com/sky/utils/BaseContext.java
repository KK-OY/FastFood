package com.sky.utils;


    public class BaseContext {

        // 创建一个存放 Integer (用户ID) 的 ThreadLocal
        private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

        // 存入当前线程
        public static void setCurrentId(Long id) {
            threadLocal.set(id);
        }

        // 取出当前线程的 ID
        public static Long getCurrentId() {
            return threadLocal.get();
        }
        public static String getname(){
            return threadLocal.toString();
        }
        // 移除当前线程的数据（防止内存泄漏和串号，必须在拦截器结束后调用）
        public static void removeCurrentId() {
            threadLocal.remove();
        }
    }


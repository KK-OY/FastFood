package com.sky.aspect;

import com.sky.annotion.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.enumeration.OperationType;
import com.sky.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Update;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoAspect {
    //先写切入点
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotion.AutoFill)")
    public void AutoCut(){};

    //aop拦截匹配到的连接点
    @Before("AutoCut()")
    public void addPublic(JoinPoint joinPoint){
        log.info("开始自动填充公共字段");

        //获取注解的值类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = annotation.value();

        Object[] args = joinPoint.getArgs();

        if(args == null  || args.length == 0){
            return;
        }

        Object entity = args[0];

        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        switch (operationType){
            case INSERT:
                try {
                    Method setCreatTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                    Method setCreatUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                    Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                    Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                    setCreatTime.invoke(entity,now);
                    setCreatUser.invoke(entity,currentId);
                    setUpdateTime.invoke(entity,now);
                    setUpdateUser.invoke(entity,currentId);
                } catch (Exception e) {
                    throw new RuntimeException(e);
             }

            case UPDATE:
                try {
                    Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                    Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                    setUpdateTime.invoke(entity,now);
                    setUpdateUser.invoke(entity,currentId);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
        }
    }
}

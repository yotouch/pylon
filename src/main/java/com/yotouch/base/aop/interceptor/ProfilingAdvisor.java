package com.yotouch.base.aop.interceptor;

import java.lang.reflect.Method;

import org.aopalliance.aop.Advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProfilingAdvisor extends AbstractPointcutAdvisor {

    private static final Logger logger = LoggerFactory.getLogger(ProfilingAdvisor.class);

    private final StaticMethodMatcherPointcut pointcut = new
            StaticMethodMatcherPointcut() {

                @Override
                public boolean matches(Method method, Class<?> targetClass) {

                    return method.isAnnotationPresent(ProfileExecution.class);

                    /*
                    int modifiers = method.getModifiers();

                    logger.info("Method " + method.toGenericString());
                    logger.info("Method is abstract " + (modifiers & Modifier.ABSTRACT));

                    logger.info("targetClass " + targetClass.getName());

                    logger.info("targetClass is Component " + targetClass.isAnnotationPresent(Component.class));

                    if (targetClass.isAnnotationPresent(Configuration.class)) {
                        return false;
                    }

                    if (targetClass.isAnnotationPresent(Component.class)) {
                        return false;
                    }


                    String className = targetClass.getName();

                    if (className.startsWith("com.yotouch.app.wuli.wechat.")) {
                        return false;
                    }

                    if (className.startsWith("com.yotouch")) {
                        return true;
                    }

                    //logger.info("Profiling ");

                    //return method.isAnnotationPresent(ProfileExecution.class);
                    return false;
                    */
                }
            };

    @Autowired
    private ProfilingMethodInterceptor interceptor;

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.interceptor;
    }
}

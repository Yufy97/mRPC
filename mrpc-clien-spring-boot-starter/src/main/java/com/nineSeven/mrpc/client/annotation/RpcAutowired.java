package com.nineSeven.mrpc.client.annotation;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Autowired
public @interface RpcAutowired {

    String version() default "1.0";

}

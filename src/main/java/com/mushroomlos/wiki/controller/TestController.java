package com.mushroomlos.wiki.controller;


import com.mushroomlos.wiki.domain.Test;
import com.mushroomlos.wiki.service.TestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/*
RestController返回字符串
Controller返回页面
RestController = Controller注解 + ResponseBody注解
 */

//@Controller
@RestController
public class TestController {
    // 优先读取配置文件，然后再读取默认值
//    @Value("${test.hello:HELLO}")
    @Value("${test.hello}")
    private String testHello;

    @Resource
    private TestService testService;

    /*
     * GET, POST, PUT, DELETE
     *
     * Normal /user?id=1
     * REST /user/1
     * RequestMapping任意请求都可以
     * @return
     */
//    @GetMapping
//    @PostMapping
//    @PutMapping
//    @DeleteMapping
//    @RequestMapping(value="/hello", method=RequestMethod.GET)
    // 405请求不支持，直接读取页面为GET请求
    @GetMapping("/hello")
    public String hello(){
        return "Hello World" + testHello;
    }

    @PostMapping("/hello/post")
    public String helloPost(String name){
        return "Hello world post " + name;
    }

    @GetMapping("/test/list")
    public List<Test> list(){
        return testService.list();
    }

}

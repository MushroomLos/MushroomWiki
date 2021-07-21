package com.mushroomlos.wiki.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/*
RestController返回字符串
Controller返回页面
RestController = Controller注解 + ResponseBody注解
 */

//@Controller
@RestController
public class TestController {

    /**
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
        return "Hello World";
    }

    @PostMapping("/hello/post")
    public String helloPost(String name){
        return "Hello world post " + name;
    }
}

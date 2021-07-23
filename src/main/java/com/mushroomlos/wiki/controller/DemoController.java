package com.mushroomlos.wiki.controller;


import com.mushroomlos.wiki.domain.Demo;
import com.mushroomlos.wiki.service.DemoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/demo")
public class DemoController {

    @Resource
    private DemoService demoService;

    @GetMapping("/list1")
    public List<Demo> list(){
        return demoService.list();
    }
}

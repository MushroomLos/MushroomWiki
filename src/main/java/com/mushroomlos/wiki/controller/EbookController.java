package com.mushroomlos.wiki.controller;


import com.mushroomlos.wiki.req.EbookQueryReq;
import com.mushroomlos.wiki.req.EbookSaveReq;
import com.mushroomlos.wiki.resp.CommonResp;
import com.mushroomlos.wiki.resp.EbookQueryResp;
import com.mushroomlos.wiki.resp.PageResp;
import com.mushroomlos.wiki.service.EbookService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;


@RestController
@RequestMapping("/ebook")
public class EbookController {

    @Resource
    private EbookService ebookService;

    @GetMapping("/list")
    public CommonResp list(@Valid EbookQueryReq req){
        CommonResp<PageResp<EbookQueryResp>> resp = new CommonResp<>();
        PageResp<EbookQueryResp> list =  ebookService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@RequestBody EbookSaveReq req){
        CommonResp resp = new CommonResp<>();
        ebookService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id){
        CommonResp resp = new CommonResp<>();
        ebookService.delete(id);
        return resp;
    }
}

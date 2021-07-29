package com.mushroomlos.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mushroomlos.wiki.domain.Doc;
import com.mushroomlos.wiki.domain.DocExample;
import com.mushroomlos.wiki.mapper.DocMapper;
import com.mushroomlos.wiki.req.DocQueryReq;
import com.mushroomlos.wiki.req.DocSaveReq;
import com.mushroomlos.wiki.resp.DocQueryResp;
import com.mushroomlos.wiki.resp.PageResp;
import com.mushroomlos.wiki.util.CopyUtil;
import com.mushroomlos.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DocService {

    private static final Logger LOG = LoggerFactory.getLogger(DocService.class);

    @Resource
    private DocMapper docMapper;

    @Resource
    private SnowFlake snowFlake;

    public List<DocQueryResp> all() {

        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        List<Doc> docList = docMapper.selectByExample(docExample);

        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        return list;
    }

    public PageResp<DocQueryResp> list(DocQueryReq req) {

        DocExample docExample = new DocExample();
        // Criteria类似于where条件
        docExample.setOrderByClause("sort asc");
        DocExample.Criteria criteria = docExample.createCriteria();
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Doc> docList = docMapper.selectByExample(docExample);

        PageInfo<Doc> pageInfo = new PageInfo<>(docList);
        LOG.info("Total line: {}", pageInfo.getTotal());
        LOG.info("Total pages: {}", pageInfo.getPages());

//        List<DocResp> respList = new ArrayList<>();
//        for(Doc doc : docList){
////            DocResp docResp = new DocResp();
////            BeanUtils.copyProperties(doc, docResp);
//
//            DocResp docResp = CopyUtil.copy(doc, DocResp.class);
//
//            respList.add(docResp);
//        }

        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        PageResp<DocQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }

    /**
     * 保存
     */
    public void save(DocSaveReq req){
        Doc doc = CopyUtil.copy(req, Doc.class);
        if(ObjectUtils.isEmpty(req.getId())){
            // 新增
            doc.setId(snowFlake.nextId());
            docMapper.insert(doc);
        }else{
            // 更新
            docMapper.updateByPrimaryKey(doc);
        }
    }

    /**
     * 删除
     */
    public void delete(Long id){
        docMapper.deleteByPrimaryKey(id);
    }
}

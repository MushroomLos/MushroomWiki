package com.mushroomlos.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mushroomlos.wiki.domain.Content;
import com.mushroomlos.wiki.domain.Doc;
import com.mushroomlos.wiki.domain.DocExample;
import com.mushroomlos.wiki.exception.BusinessException;
import com.mushroomlos.wiki.exception.BusinessExceptionCode;
import com.mushroomlos.wiki.mapper.ContentMapper;
import com.mushroomlos.wiki.mapper.DocMapper;
import com.mushroomlos.wiki.mapper.DocMapperCust;
import com.mushroomlos.wiki.req.DocQueryReq;
import com.mushroomlos.wiki.req.DocSaveReq;
import com.mushroomlos.wiki.resp.DocQueryResp;
import com.mushroomlos.wiki.resp.PageResp;
import com.mushroomlos.wiki.util.CopyUtil;
import com.mushroomlos.wiki.util.RedisUtil;
import com.mushroomlos.wiki.util.RequestContext;
import com.mushroomlos.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
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
    private DocMapperCust docMapperCust;

    @Resource
    private ContentMapper contentMapper;

    @Resource
    private SnowFlake snowFlake;

    @Resource
    public RedisUtil redisUtil;

    @Resource
    public WsService wsService;

    public List<DocQueryResp> all(Long ebookId) {

        DocExample docExample = new DocExample();
        docExample.createCriteria().andEbookIdEqualTo(ebookId);
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
        Content content = CopyUtil.copy(req, Content.class);
        if(ObjectUtils.isEmpty(req.getId())){
            // 新增
            doc.setId(snowFlake.nextId());
            doc.setViewCount(0);
            doc.setVoteCount(0);
            docMapper.insert(doc);

            content.setId(doc.getId());
            contentMapper.insert(content);
        }else{
            // 更新
            docMapper.updateByPrimaryKey(doc);
            int count = contentMapper.updateByPrimaryKeyWithBLOBs(content);
            if(count == 0){
                contentMapper.insert(content);
            }
        }
    }

    /**
     * 删除
     */
    public void delete(Long id){
        docMapper.deleteByPrimaryKey(id);
    }

    public void delete(List<String> ids) {
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);
    }

    public String findContent(Long id) {
        Content content = contentMapper.selectByPrimaryKey(id);
        // 文档阅读数加一
        docMapperCust.increaseViewCount(id);
        if(ObjectUtils.isEmpty(content)){
            return "";
        }else {
            return content.getContent();
        }
    }

    public void vote(Long id){
        String ip = RequestContext.getRemoteAddr();
        if(redisUtil.validateRepeat("DOC_VOTE_" + id + "_" + ip, 3600 * 24)){
            docMapperCust.increaseVoteCount(id);
        }else{
            throw new BusinessException(BusinessExceptionCode.VOTE_REPEAT);
        }

        // 推送消息
        Doc docDb = docMapper.selectByPrimaryKey(id);
        String logId = MDC.get("LOG_ID");
        wsService.sendInfo("【" + docDb.getName() + "】被点赞", logId);
    }

    public void updateEbookInfo(){
        docMapperCust.updateEbookInfo();
    }
}

package com.mushroomlos.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mushroomlos.wiki.domain.User;
import com.mushroomlos.wiki.domain.UserExample;
import com.mushroomlos.wiki.exception.BusinessException;
import com.mushroomlos.wiki.exception.BusinessExceptionCode;
import com.mushroomlos.wiki.mapper.UserMapper;
import com.mushroomlos.wiki.req.UserLoginReq;
import com.mushroomlos.wiki.req.UserQueryReq;
import com.mushroomlos.wiki.req.UserResetPasswordReq;
import com.mushroomlos.wiki.req.UserSaveReq;
import com.mushroomlos.wiki.resp.PageResp;
import com.mushroomlos.wiki.resp.UserLoginResp;
import com.mushroomlos.wiki.resp.UserQueryResp;
import com.mushroomlos.wiki.util.CopyUtil;
import com.mushroomlos.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Resource
    private UserMapper userMapper;

    @Resource
    private SnowFlake snowFlake;

    public List<UserQueryResp> all() {
        UserExample userExample = new UserExample();
        userExample.setOrderByClause("sort asc");
        List<User> userList = userMapper.selectByExample(userExample);

        List<UserQueryResp> list = CopyUtil.copyList(userList, UserQueryResp.class);

        return list;
    }

    public PageResp<UserQueryResp> list(UserQueryReq req) {

        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        if (!ObjectUtils.isEmpty(req.getLoginName())) {
            criteria.andLoginNameEqualTo(req.getLoginName());
        }
        PageHelper.startPage(req.getPage(), req.getSize());
        List<User> userList = userMapper.selectByExample(userExample);

        PageInfo<User> pageInfo = new PageInfo<>(userList);
        LOG.info("Total line: {}", pageInfo.getTotal());
        LOG.info("Total pages: {}", pageInfo.getPages());

//        List<UserResp> respList = new ArrayList<>();
//        for(User user : userList){
////            UserResp userResp = new UserResp();
////            BeanUtils.copyProperties(user, userResp);
//
//            UserResp userResp = CopyUtil.copy(user, UserResp.class);
//
//            respList.add(userResp);
//        }

        List<UserQueryResp> list = CopyUtil.copyList(userList, UserQueryResp.class);

        PageResp<UserQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }

    /**
     * ??????
     */
    public void save(UserSaveReq req) {
        User user = CopyUtil.copy(req, User.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            User userDB = selectByLoginName(req.getLoginName());
            if(ObjectUtils.isEmpty(userDB)){
                // ??????
                user.setId(snowFlake.nextId());
                userMapper.insert(user);
            }else {
                // ??????????????????
                throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
            }
        } else {
            // ??????
            user.setLoginName(null);
            user.setPassword(null);
            userMapper.updateByPrimaryKeySelective(user);
        }
    }

    /**
     * ??????
     */
    public void delete(Long id) {
        userMapper.deleteByPrimaryKey(id);
    }

    public User selectByLoginName(String LoginName) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andLoginNameEqualTo(LoginName);
        List<User> userList = userMapper.selectByExample(userExample);
        if(CollectionUtils.isEmpty(userList)){
            return null;
        }else{
            return userList.get(0);
        }
    }

    /**
     * ????????????
     */
    public void resetPassword(UserResetPasswordReq req) {
        User user = CopyUtil.copy(req, User.class);
        userMapper.updateByPrimaryKeySelective(user);
    }

    /**
     * ??????
     */
    public UserLoginResp login(UserLoginReq req) {
        User userDb = selectByLoginName(req.getLoginName());
        if(ObjectUtils.isEmpty(userDb)){
            // ??????????????????
            LOG.info("????????????????????? {}", req.getLoginName());
            throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
        }else{
            if(userDb.getPassword().equals(req.getPassword())){
                // ????????????
                UserLoginResp userLoginResp = CopyUtil.copy(userDb,UserLoginResp.class);
                return userLoginResp;
            }else{
                // ????????????
                LOG.info("??????????????? ???????????????{}??? ??????????????????{}", req.getPassword(), userDb.getPassword());
                throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
            }
        }
    }
}

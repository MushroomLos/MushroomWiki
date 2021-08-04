package com.mushroomlos.wiki.service;

import com.mushroomlos.wiki.websocket.WebSocketServer;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class WsService {

    @Resource
    public WebSocketServer webSocketServer;

    @Async
    public void sendInfo(String message, String logId){
        // 推送消息
        MDC.put("LOG_ID", logId);
        webSocketServer.sendInfo(message);
    }
}

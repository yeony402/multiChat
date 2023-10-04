package com.miniProject.multiChat.controller;

import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@Controller
@ServerEndpoint("/websocket")
public class MessageController extends Socket {
    private static final List<Session> session = new ArrayList<Session>();

    @GetMapping("/")
    public String index() {
        return "index.html";
    }

    // 사용자가 페이지에 접속할 때 실행된다.
    @OnOpen
    public void open(Session newUser) {
        System.out.println("connected");
        session.add(newUser);
        System.out.println(newUser.getId());
    }

    // 사용자로부터 메시지를 받았을 때 실행된다.
    @OnMessage
    public void getMsg(Session recieveSession, String msg) {
        for (int i = 0; i < session.size(); i++) {
            if (!recieveSession.getId().equals(session.get(i).getId())) {
                try {
//                  "상대" 대신 사용자 이름을 변수로 넣는다.
                    session.get(i).getBasicRemote().sendText("상대 : "+msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    session.get(i).getBasicRemote().sendText("나 : "+msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Scheduled(cron = "* * * * * *")
    private void isSessionClosed() {
        if (session.size() != 0) {
            try {
                System.out.println("! = " + session.size());
                for (int i = 0; i < session.size(); i++) {
                    if (! session.get(i).isOpen()) {
                        session.remove(i);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
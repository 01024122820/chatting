package com.kjbank.chat.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kjbank.chat.DTO.ChatMessage;

public class ChatWebSocketHandler extends TextWebSocketHandler {
	
    private static CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    
    private static Map<WebSocketSession, String> userNames = new ConcurrentHashMap<>();
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    	
        sessions.add(session);
        
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    	  ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);

    	  if (chatMessage.getType() == ChatMessage.MessageType.JOIN) {
              userNames.put(session, chatMessage.getSender());
              broadcastMessage(chatMessage);
              
              // 현재 접속 중인 사용자 목록을 새로 접속한 사용자에게 전송
              sendUserListToNewUser(session);
              
              // 사용자가 접속했음을 알리는 메시지 생성
              ChatMessage welcomeMessage = new ChatMessage();
              welcomeMessage.setType(ChatMessage.MessageType.CHAT);
              welcomeMessage.setSender("System");
              welcomeMessage.setContent(chatMessage.getSender() + "님이 접속하셨습니다.");
              broadcastMessage(welcomeMessage);
              
    	  } else if (chatMessage.getType() == ChatMessage.MessageType.CHAT) {
              broadcastMessage(chatMessage);
          } else if (chatMessage.getType() == ChatMessage.MessageType.LEAVE) {
              broadcastMessage(chatMessage);
              // 사용자가 나갔음을 알리는 메시지 생성
              ChatMessage exitMessage = new ChatMessage();
              exitMessage.setType(ChatMessage.MessageType.CHAT);
              exitMessage.setSender("System");
              exitMessage.setContent(chatMessage.getSender() + "님이 나가셨습니다.");
              broadcastMessage(exitMessage);
          }
    }
    
    private void sendUserListToNewUser(WebSocketSession session) throws Exception {
        List<String> userList = new ArrayList<>(userNames.values());
        ChatMessage userListMessage = new ChatMessage();
        userListMessage.setType(ChatMessage.MessageType.USER_LIST);
        userListMessage.setContent(objectMapper.writeValueAsString(userList));
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(userListMessage)));
    }
    
    private void broadcastMessage(ChatMessage chatMessage) throws Exception {
    	 String jsonMessage = objectMapper.writeValueAsString(chatMessage);
         List<WebSocketSession> closedSessions = new ArrayList<>();
         for (WebSocketSession webSocketSession : sessions) {
             if (webSocketSession.isOpen()) {
                 webSocketSession.sendMessage(new TextMessage(jsonMessage));
             } else {
                 closedSessions.add(webSocketSession);
             }
         }
         sessions.removeAll(closedSessions);
         closedSessions.forEach(session -> userNames.remove(session));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    	
    	 String userName = userNames.get(session);
         if (userName != null) {
        	 ChatMessage leaveMessage = new ChatMessage();
             leaveMessage.setType(ChatMessage.MessageType.LEAVE);
             leaveMessage.setSender(userName);
             broadcastMessage(leaveMessage);

             // 사용자가 접속 종료했음을 알리는 메시지 생성
             ChatMessage exitMessage = new ChatMessage();
             exitMessage.setType(ChatMessage.MessageType.CHAT);
             exitMessage.setSender("System");
             exitMessage.setContent(userName + "님이 접속 종료하셨습니다.");
             broadcastMessage(exitMessage);
             
         }
         sessions.remove(session);
         userNames.remove(session);
    }
}
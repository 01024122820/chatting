<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="kr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>어서오세요~!</title>
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans&display=swap" rel="stylesheet">
    <!-- Bootstrap CSS -->
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
    
    <link href="<c:url value='/css/common.css' />" rel="stylesheet">
    
    <!-- Bootstrap JS and dependencies -->
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.3/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    
    
</head>

 <script>
        var ws;
        var username;
        var selectedEmoji = '😀'; // Default emoji

        function enterKey(event, callback) {
            if (event.keyCode === 13) { // Enter key
                callback();
                return false; // Prevent default action
            }
            return true;
        }

        function selectEmoji(emoji) {
            selectedEmoji = emoji;
            document.querySelectorAll('.emoji').forEach(function(el) {
                el.classList.remove('selected-emoji');
            });
            event.target.classList.add('selected-emoji');
        }

        function connect() {
            username = document.getElementById('username').value;
            if (username) {
                username = selectedEmoji + ' ' + username;
                var host = location.hostname;
                var port = location.port;
                var wsProtocol = location.protocol === 'https:' ? 'wss:' : 'ws:';
                var wsUrl = wsProtocol + '//' + host + ':' + port + location.pathname+'chatConnect';

                ws = new WebSocket(wsUrl);

                ws.onopen = function() {
                    var joinMessage = {
                        type: 'JOIN',
                        sender: username,
                        content: ''
                    };
                    ws.send(JSON.stringify(joinMessage));
                    document.getElementById('login').style.display = 'none';
                    document.getElementById('chat').style.display = 'block';
                };

                ws.onmessage = function(event) {
                    var chatbox = document.getElementById('chatbox');
                    var data = JSON.parse(event.data);

                    if (data.type === 'USER_LIST') {
                        var userList = JSON.parse(data.content);
                        userList.forEach(function(user) {
                            updateUserList(user, true);
                        });
                        return; // 사용자 목록 갱신 후 메시지 추가 작업을 건너뜀
                    }

                    var message = document.createElement('div');
                    message.className = 'chat-message';
                    if (data.sender === "System") {
                        message.className += ' system-message';
                        message.textContent = data.content;
                    } else if (data.type !== 'LEAVE') { // LEAVE 메시지를 무시
                        if (data.sender === username) {
                            message.className += ' message-sent';
                            message.innerHTML = '<span class="message-sender">' + data.sender + ':</span> <span class="message-content">' + data.content + '</span>';
                        } else {
                            message.className += ' message-received';
                            message.innerHTML = '<span class="message-sender">' + data.sender + ':</span> <span class="message-content">' + data.content + '</span>';
                        }
                    }
                    chatbox.appendChild(message);
                    chatbox.scrollTop = chatbox.scrollHeight;

                    if (data.type === 'JOIN') {
                        updateUserList(data.sender, true);
                    } else if (data.type === 'LEAVE') {
                        updateUserList(data.sender, false);
                    }
                };
            } else {
                alert("별명을 입력해주세요.");
            }
        }

        function sendMessage() {
            var messageInput = document.getElementById('message');
            var chatMessage = {
                type: 'CHAT',
                sender: username,
                content: messageInput.value
            };
            ws.send(JSON.stringify(chatMessage));
            messageInput.value = '';
        }

        function disconnect() {
            if (ws) {
                var leaveMessage = {
                    type: 'LEAVE',
                    sender: username,
                    content: ''
                };
                ws.send(JSON.stringify(leaveMessage));
                ws.close();
                document.getElementById('chat').style.display = 'none';
                document.getElementById('login').style.display = 'block';
            }
        }

        function updateUserList(username, isJoining) {
            var userList = document.getElementById('user-list');
            var userItem = document.getElementById('user-' + username);
            if (isJoining) {
                if (!userItem) {
                    userItem = document.createElement('div');
                    userItem.id = 'user-' + username;
                    userItem.textContent = username;
                    userList.appendChild(userItem);
                }
            } else {
                if (userItem) {
                    userList.removeChild(userItem);
                }
            }
        }

        function toggleUserList() {
            var userListContainer = document.getElementById('user-list-container');
            var hamburger = document.querySelector('.hamburger');
            userListContainer.classList.toggle('active');
            hamburger.classList.toggle('active');
        }
    </script>


<body>
    <div class="container">
        <div id="login" class="mt-5">
            <h1 class="text-center">별명을 정해주세요</h1>
            <div class="form-group">
                <input type="text" id="username" class="form-control" placeholder="별명을 입력해주세요.." autofocus onkeypress="return enterKey(event, connect)">
            </div>
            <div class="text-center mb-3">
                <span class="emoji" onclick="selectEmoji('😀')">😀</span>
                <span class="emoji" onclick="selectEmoji('😎')">😎</span>
                <span class="emoji" onclick="selectEmoji('😂')">😂</span>
                <span class="emoji" onclick="selectEmoji('😍')">😍</span>
                <span class="emoji" onclick="selectEmoji('🤔')">🤔</span>
            </div>
            <button class="btn btn-primary btn-block" onclick="connect()">입 장</button>
        </div>

        <div id="chat" class="mt-5 position-relative">
            <div class="row">
            	
                <div class="col-md-9">
                    <h1 class="text-center">채팅방</h1>
                    <div id="chatbox" class="mb-3"></div>
                    <div class="input-group">
                        <input type="text" id="message" class="form-control" placeholder="메세지를 입력해주세요..." autofocus onkeypress="return enterKey(event, sendMessage)">
                        <div class="input-group-append">
                            <button class="btn btn-primary" onclick="sendMessage()">보내기</button>
                        </div>
                    </div>
                </div>
                <div class="col-md-2">
                	<div><button class="btn btn-danger" onclick="disconnect()">방 나가기</button></div>
                    <div id="user-list"></div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
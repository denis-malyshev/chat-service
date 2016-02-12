var View = function (eventBus) {
    this.eventBus = eventBus;

    eventBus.registerConsumer("USER_LOGGED", function () {
        MainView(eventBus);
        $(function(){
            $('a[data-toggle = "tab"]').on('shown.bs.tab', function (e) {
                var activeTab = $(e.target).text();
                alert(activeTab);
            });
        });
    });
    eventBus.registerConsumer("LOGOUT_SUCCESSFUL", function () {
        StartView(eventBus);
    });
};
View.prototype.launch = function () {
    StartView(this.eventBus);
};

var StartView = function (eventbus) {
    var eventBus = eventbus;
    var divId = "start-view";
    var innerHTML = '<div class = "container" id="' + divId + '"></div>';
    document.body.innerHTML = innerHTML;

    $("#" + divId).html(
        '<div class = "container">' +
        '<h2>Registration</h2>' +
        '<div class="col-md-3" id="registrationForm">' +
        '<label>First Name</label>' +
        '<input type="text" class = "form-control" placeholder = "Enter First Name" id="firstName">' +
        '<label>Email</label>' +
        '<input type="email" class = "form-control" placeholder = "Enter Email" id="register-email">' +
        '<label>Password</label>' +
        '<input type="password" class = "form-control" placeholder = "Enter Password" id="register-pwd">' +
        '</br><button id="registerBtn" class = "btn btn-primary">Register</button></div></div>' +
        '<div class = "container">' +
        '<h2>Login</h2>' +
        '<div class="col-md-3">' +
        '<label>Email</label>' +
        '<input type="email" class = "form-control" placeholder = "Enter Email" id="login-email">' +
        '<label>Password</label>' +
        '<input type="password" class = "form-control" placeholder = "Enter Password" id="login-pwd">' +
        '</br><button id="loginBtn" class = "btn btn-primary">Login</button></div></div>'
    );

    $(document).ready(function () {
        $("#registerBtn").click(function () {
            eventBus.postMessage("REGISTRATION_ATTEMPT",
                new UserDTO($("#firstName").val(), $("#register-email").val(), $("#register-pwd").val()));
            $("#firstName").val("");
            $("#register-email").val("");
            $("#register-pwd").val("");
        });
        $("#loginBtn").click(function () {
            eventBus.postMessage("LOGIN_ATTEMPT",
                new LoginInfo($("#login-email").val(), $("#login-pwd").val()));
        });

        eventBus.registerConsumer("REGISTRATION_SUCCESSFUL", function () {
            $("#registrationForm").append(
                '<div id = "myAlert" class = "alert alert-success">' +
                '<a href = "#" class = "close" data-dismiss = "alert">&times;</a>' +
                '<strong>Success!</strong> You have successfully signed up.' +
                '</div>'
            );
        }, "LOGOUT_SUCCESSFUL");
    });
};

var MainView = function (eventBus) {
    var eventBus = eventBus;
    var innerHTML = '<div id="main-view" class = "container"></div>';
    document.body.innerHTML = innerHTML;

    $("#main-view").html(
        '<div align="right"><button id="logoutBtn" class = "btn btn-primary btn-xs">Logout</button></div>' +
        '</br><div class="container"></div>' +
        '<div align="left" class="form-group">' +
        '<div class="container">' +
        '<div class="col-md-2">' +
        '<label>Chat-name</label>' +
        '<input type="text" id="chat-name" class = "form-control">' +
        '</br><button id="create-chat" class = "btn btn-primary btn-xs">Create</button></div></div></div>');

    $(document).ready(function () {
        document.getElementById("logoutBtn").onclick = function () {
            eventBus.postMessage("LOGOUT_ATTEMPT", null);
        };
        document.getElementById("create-chat").onclick = function () {
            eventBus.postMessage("CREATE_CHAT_ATTEMPT", $("#chat-name").val());
            $("#chat-name").val("");
        };

        eventBus.registerConsumer("CHAT_LIST_LOADED", function (chatList) {
            showChatList(eventBus, chatList);
        }, "LOGOUT_SUCCESSFUL");

        eventBus.registerConsumer("SUCCESSFUL_JOINED", function (chatRoomDTO) {
            showChatComp(eventBus, chatRoomDTO);
        }, "LOGOUT_SUCCESSFUL");

        eventBus.postMessage("MAIN_VIEW_LOAD", null);
    });
};

var registerChat = function (eventbus, chatRoomId) {
    var eventBus = eventbus;

    eventBus.registerConsumer(chatRoomId + "_MESSAGES_UPDATED", function (messages) {
        for (var i = 0; i < Object.keys(messages).length; i++) {
            var text = messages[i].sender + ": " + messages[i].text;
            $("#correspondence_" + chatRoomId).append(text + "&#13;&#10;");
        }
    }, "SUCCESSFUL_LEAVE_FROM_" + chatRoomId);

    eventBus.registerConsumer(chatRoomId + "_SENT_PRIVATE_MESSAGES_UPDATED", function (messages) {
        for (var i = 0; i < Object.keys(messages).length; i++) {
            var text = "You to " + messages[i].receiver + ": " + messages[i].text;
            $("#correspondence_" + chatRoomId).append(text + "&#13;&#10;");
        }
    }, "SUCCESSFUL_LEAVE_FROM_" + chatRoomId);

    eventBus.registerConsumer(chatRoomId + "_RECEIVED_PRIVATE_MESSAGES_UPDATED", function (messages) {
        for (var i = 0; i < Object.keys(messages).length; i++) {
            var text = messages[i].sender + " to you: " + messages[i].text;
            $("#correspondence_" + chatRoomId).append(text + "&#13;&#10;");
        }
    }, "SUCCESSFUL_LEAVE_FROM_" + chatRoomId);

    eventBus.registerConsumer(chatRoomId + "_USERS_UPDATED", function (userList) {
        showUserList("user-list_" + chatRoomId, userList, chatRoomId);
    }, "SUCCESSFUL_LEAVE_FROM_" + chatRoomId);

    var update = function () {
        eventBus.postMessage("CHECK_MESSAGES", chatRoomId);
        eventBus.postMessage("CHECK_SENT_PRIVATE_MESSAGES", chatRoomId);
        eventBus.postMessage("CHECK_RECEIVED_PRIVATE_MESSAGES", chatRoomId);
        eventBus.postMessage("CHECK_USERS", chatRoomId);
    };

    var delay = 1000 * 2;

    var intervalId = setInterval(update, delay);

    eventBus.registerConsumer("CHAT_MESSAGES_UPDATE" + chatRoomId, function () {
        intervalId
    }, "SUCCESSFUL_LEAVE_FROM_" + chatRoomId);

    eventBus.registerConsumer("SUCCESSFUL_LEAVE_FROM_" + chatRoomId, function () {
        $("#currentChat_" + chatRoomId).remove();
        $("#link_on_chat_" + chatRoomId).remove();
        clearInterval(intervalId);
    }, "SUCCESSFUL_LEAVE_FROM_" + chatRoomId);
};

var showChatComp = function (eventbus, chatRoomDTO) {
    var eventBus = eventbus;

    $("#myTab").append('<li id="link_on_chat_' + chatRoomDTO.id + '">' +
        '<a href="#currentChat_' + chatRoomDTO.id + '" data-toggle = "tab">' + chatRoomDTO.name + '</a>' +
        '</li>');
    $("#myTabContent").append('<div class = "tab-pane fade" id="currentChat_' + chatRoomDTO.id + '"></div>');

    $("#currentChat_" + chatRoomDTO.id).html(
        '<div class = "container">' +
        '<div class = "col-lg-5">' +
        '<label>' + chatRoomDTO.name + '</label>' +
        '</br>' +
        '<textarea class = "form-control" style="background-color:white;" readonly id="correspondence_' + chatRoomDTO.id + '" rows="10"></textarea>' +
        '</br>' +
        '<div class = "input-group">' +
        '<input type="text" class = "form-control" id="messageArea_' + chatRoomDTO.id + '">' +
        '<span class = "input-group-btn">' +
        '<button class = "btn btn-primary" type = "button" id="sendMessage_' + chatRoomDTO.id + '">Send</button>' +
        '</span></div></br>' +
        '<input type="checkbox" id="private_' + chatRoomDTO.id + '" name="isPrivate" value=true>' +
        '<label>Send privately to: </label>' +
        '<div id="user-list_' + chatRoomDTO.id + '">' +
        '</div></div></div>');

    registerChat(eventBus, chatRoomDTO.id);

    eventBus.postMessage("CHAT_MESSAGES_UPDATE" + chatRoomDTO.id);

    $("#sendMessage_" + chatRoomDTO.id).click(function () {
        var receiverId;
        var type;
        var PRIVATE_MESSAGE = "SEND_PRIVATE_MESSAGE_ATTEMPT";
        var MESSAGE = "SEND_MESSAGE_ATTEMPT";

        if (document.getElementById("private_" + chatRoomDTO.id).checked) {
            receiverId = $("#selectUser_" + chatRoomDTO.id).val();
            console.log("RECEIVER:" + receiverId);
            type = PRIVATE_MESSAGE;
        } else {
            receiverId = chatRoomDTO.id;
            type = MESSAGE;
        }

        var messageData = new MessageData(receiverId, $("#messageArea_" + chatRoomDTO.id).val());
        eventBus.postMessage(type, messageData);
        $("#messageArea_" + chatRoomDTO.id).val("");
    });
};

var showUserList = function (divId, userList, chatRoomId) {
    var listBox = '<select class="form-control" id="selectUser_' + chatRoomId + '">';

    for (var i = 0; i < Object.keys(userList).length; i++) {
        listBox += '<option value="' + userList[i].id + '">' + userList[i].firstName + '</option>';
    }
    listBox += '</select>';

    $("#" + divId).html(listBox);
};

var showChatList = function (eventBus, chatList) {
    var eventBus = eventBus;
    var innerHTML = '<div class="container"><div id="chat-list" class ="col-lg-3"></div></div>';
    if (!document.getElementById("chat-list")) {
        $("#main-view").append(innerHTML);
    }
    if (!document.getElementById("myTab")) {
        $("#main-view").append(
            '<ul id = "myTab" class = "nav nav-tabs"></ul>' +
            '<div id = "myTabContent" class = "tab-content"></div>');
    }

    var listBox = '<div class = "input-group">' +
        '<select class="form-control" id="selectChat">';

    for (var i = 0; i < Object.keys(chatList).length; i++) {
        listBox += '<option value="' + chatList[i].id + '">' + chatList[i].name + '</option>';
    }
    listBox += '</select>';

    $("#chat-list").html(
        '<label>Chat-rooms:</label>' + listBox +
        '<span class = "input-group-btn">' +
        '<button id="join" class = "btn btn-success">Join</button>' +
        '<button id="leave" class = "btn btn-danger">Leave</button>' +
        '</span></div>' +
        '</br>');

    document.getElementById("join").onclick = function () {
        eventBus.postMessage("JOIN_TO_CHAT_ATTEMPT",
            new ChatRoomDTO($("#selectChat").val(), $("#selectChat").find('option:selected').text()));
    };
    document.getElementById("leave").onclick = function () {
        eventBus.postMessage("LEAVE_FROM_CHAT_ATTEMPT", $("#selectChat").val());
    };
};
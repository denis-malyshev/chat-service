function View(eventBus) {
    this.eventBus = eventBus;

    eventBus.registerConsumer("USER_LOGGED", function () {
        MainView(eventBus);
    });
    eventBus.registerConsumer("LOGOUT_SUCCESSFUL", function () {
        StartView(eventBus);
    });
};
View.prototype.launch = function () {
    StartView(this.eventBus);
};

function StartView(eventbus) {
    var eventBus = eventbus;
    var divId = "start-view";
    var innerHTML = '<div class = "container" id="' + divId + '"></div>';
    document.body.innerHTML = innerHTML;

    $("#" + divId).html(
        '<div class = "container">' +
        '<h2>Registration</h2>' +
        '<div class="col-md-2">' +
        '<label>First Name</label>' +
        '<input type="text" class = "form-control" placeholder = "Enter First Name" id="firstName">' +
        '<label>Email</label>' +
        '<input type="email" class = "form-control" placeholder = "Enter Email" id="register-email">' +
        '<label>Password</label>' +
        '<input type="password" class = "form-control" placeholder = "Enter Password" id="register-pwd">' +
        '</br><button id="registerBtn" class = "btn btn-primary">Register</button></div></div>' +
        '<div class = "container">' +
        '<h2>Login</h2>' +
        '<div class="col-md-2">' +
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
    });
};

function MainView(eventBus) {
    var eventBus = eventBus;
    var innerHTML = '<div id="main-view" class = "container"></div>';
    document.body.innerHTML = innerHTML;

    $("#main-view").html(
        '<div align="right"><button id="logoutBtn" class = "btn btn-primary btn-xs">Logout</button></div>' +
        '</br><div class="container"></div>' +
        '<div align="left" class="form-group">' +
        '<div align="right" class="container">' +
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

        eventBus.registerConsumer("SUCCESSFUL_JOINED", function (chatRoomId) {
            showChatComp(eventBus, chatRoomId);
        }, "LOGOUT_SUCCESSFUL");

        $(document).ready(function () {
            eventBus.postMessage("MAIN_VIEW_LOAD", null);
        });
    });
};

function registerChat(eventbus, chatRoomId) {
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
        $("#currentChat").remove();
        clearInterval(intervalId);
    }, "SUCCESSFUL_LEAVE_FROM_" + chatRoomId);
};

function showChatComp(eventbus, chatRoomId) {
    var eventBus = eventbus;
    var innerHTML = '<li>' +
        '<a href="#currentChat_' + chatRoomId +'" data-toggle = "tab">Chat</a>'+
        '</li>' +
        '<div class = "tab-pane fade" id="currentChat_' + chatRoomId + '"></div>';
    $("#myTabContent").append(innerHTML);

    $("#currentChat_" + chatRoomId).html(
        '<div align="center">' +
        '<label>Current chat</label>' +
        '</br><textarea readonly id="correspondence_' + chatRoomId + '" rows="10" cols="50"></textarea> ' +
        '</br><input type="text" id="messageArea_' + chatRoomId + '" align="left">' +
        '<button id="sendMessage_' + chatRoomId + '" class = "btn btn-primary btn-xs">Send</button>' +
        '</br><input type="checkbox" id="private_' + chatRoomId + '" name="isPrivate" value=true>' +
        '<label>send privately to: </label><div id="user-list_' + chatRoomId + '"></div></div>');

    registerChat(eventBus, chatRoomId);

    eventBus.postMessage("CHAT_MESSAGES_UPDATE" + chatRoomId);

    $("#sendMessage_" + chatRoomId).click(function () {
        var receiverId;
        var type;
        var PRIVATE_MESSAGE = "SEND_PRIVATE_MESSAGE_ATTEMPT";
        var MESSAGE = "SEND_MESSAGE_ATTEMPT";

        if (document.getElementById("private_" + chatRoomId).checked) {
            receiverId = $("#selectUser_" + chatRoomId).val();
            console.log("RECEIVER:" + receiverId);
            type = PRIVATE_MESSAGE;
        } else {
            receiverId = chatRoomId;
            type = MESSAGE;
        }

        var messageData = new MessageData(receiverId, $("#messageArea_" + chatRoomId).val());
        eventBus.postMessage(type, messageData);
        $("#messageArea_" + chatRoomId).val("");
    });
};

function showUserList(divId, userList, chatRoomId) {
    var listBox = '<select id="selectUser_' + chatRoomId + '">';

    for (var i = 0; i < Object.keys(userList).length; i++) {
        listBox += '<option value="' + userList[i].id + '">' + userList[i].firstName + '</option>';
    }
    listBox += '</select>';

    $("#" + divId).html(listBox);
};

function showChatList(eventBus, chatList) {
    var eventBus = eventBus;
    var innerHTML = '<div id="chat-list" class="container"></div>';
    $("#main-view").append(innerHTML);
    $("#main-view").append(
        '<ul id = "myTab" class = "nav nav-tabs"></ul>' +
        '<div id = "myTabContent" class = "tab-content"></div>');
    var listBox = '<select id="selectChat">';

    for (var i = 0; i < Object.keys(chatList).length; i++) {
        listBox += '<option value="' + chatList[i].id + '">' + chatList[i].name + '</option>';
    }
    listBox += '</select>';

    $("#chat-list").html(
        '<label>Chat-rooms:</label>' + listBox +
        '<button id="join" class = "btn btn-success btn-xs">Join</button>' +
        '<button id="leave" class = "btn btn-danger btn-xs">Leave</button>');

    document.getElementById("join").onclick = function () {
        eventBus.postMessage("JOIN_TO_CHAT_ATTEMPT", $("#selectChat").val());
    };
    document.getElementById("leave").onclick = function () {
        eventBus.postMessage("LEAVE_FROM_CHAT_ATTEMPT", $("#selectChat").val());
    };
};
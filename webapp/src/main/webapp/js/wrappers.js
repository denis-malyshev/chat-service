var UserDTO = function (name, email, password) {
    this.firstName = name;
    this.email = email;
    this.password = password;
};

var LoginInfo = function(email, password) {
    this.email = email;
    this.password = password;
};

var UserId = function (userId) {
    this.id = userId;
};

var MessageDTO = function (id, sender, text, time) {
    this.id = id;
    this.sender = sender;
    this.text = text;
    this.time = time;
};

var ChatRoomDTO = function (id, name) {
    this.id = id;
    this.name = name;
};

var ChatRoomId = function (chatRoomId) {
    this.id = chatRoomId;
};

var Token = function (token) {
    this.key = token;
};

var ChatRoomRequest = function (token, userId, name) {
    this.token = token;
    this.userId = userId;
    this.name = name;
};

var MessageRequest = function (token, userId, receiverId, text) {
    this.token = token;
    this.userId = userId;
    this.receiverId = receiverId;
    this.text = text;
};

var MessageData = function (receiverId, text) {
    this.receiverId = receiverId;
    this.text = text;
};

var ReadMessageRequest = function (token, userId, date, chatRoomId) {
    this.token = token;
    this.userId = userId;
    this.date = date;
    this.chatRoomId = chatRoomId;
};

var UpdateChatRequest = function (token, userId, chatRoomId) {
    this.token = token;
    this.userId = userId;
    this.chatRoomId = chatRoomId;
};

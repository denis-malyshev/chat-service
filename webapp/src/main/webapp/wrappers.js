function UserDTO(name, email, password) {
    this.firstName = name;
    this.email = email;
    this.password = password;
};

function LoginInfo(email, password) {
    this.email = email;
    this.password = password;
};

function UserId(userId) {
    this.id = userId;
};

function MessageDTO(id, sender, text, time) {
    this.id = id;
    this.sender = sender;
    this.text = text;
    this.time = time;
};

function ChatRoomDTO(id, name) {
    this.id = id;
    this.name = name;
};

function ChatRoomId(chatRoomId) {
    this.id = chatRoomId;
};

function Token(token) {
    this.key = token;
};

function ChatRoomRequest(token, userId, name) {
    this.token = token;
    this.userId = userId;
    this.name = name;
};

function MessageRequest(token, userId, receiverId, text) {
    this.token = token;
    this.userId = userId;
    this.receiverId = receiverId;
    this.text = text;
};

function MessageData(receiverId, text) {
    this.receiverId = receiverId;
    this.text = text;
};

function ReadMessageRequest(token, userId, date, chatRoomId) {
    this.token = token;
    this.userId = userId;
    this.date = date;
    this.chatRoomId = chatRoomId;
};

function UpdateChatRequest(token, userId, chatRoomId) {
    this.token = token;
    this.userId = userId;
    this.chatRoomId = chatRoomId;
};

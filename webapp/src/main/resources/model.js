function User(eventBus) {
    this.id = 0;
    this.token = null;
    this.eventBus = eventBus;

    eventBus.registerConsumer("LOGIN_SUCCESSFUL", function (data) {
        this.token = data.key;
        this.id = data.userId;
        eventBus.postMessage("USER_LOGGED");
    });
};
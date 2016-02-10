function app(eventBus) {

    return {
        "launch": function () {
            new Controller(eventBus, new User(eventBus));
            new View(eventBus).launch();
        }
    };
}

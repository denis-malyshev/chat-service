$(function () {
    (new launch(new EventBus()));
});
var launch = function (eventBus) {
    new Controller(eventBus, new User(eventBus));
    new View(eventBus).launch();
};


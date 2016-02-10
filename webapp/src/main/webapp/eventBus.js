function EventBus() {
    this.consumers = [];

};
EventBus.prototype.registerConsumer = function (type, consumer, closure) {
    var length = this.consumers.length;
    this.consumers[length] = {
        type: type,
        callback: consumer,
        closure: closure
    };
    console.log(new Date().toLocaleTimeString() + ": added consumer " + type);
};
EventBus.prototype.postMessage = function (type, message) {
    var unsubscribe = function (consumers, length, type) {
        for (var j = 0; j < length; j++) {
            if (consumers[j].closure == type) {
                console.log(new Date().toLocaleTimeString() + ": consumer " + consumers[j].type + " removed");
                consumers.splice(j, 1);
                length--;
            }
        }

        return length;
    };

    var consumers = this.consumers;
    var length = this.consumers.length;

    for (var i = 0; i < length; i++) {
        console.log("LENGTH" + this.consumers.length);
        if (consumers[i].type == type) {
            var callback = consumers[i].callback;
            var myCallback = function () {
                callback(message);
                length = unsubscribe(consumers, length, type);
            };
            setTimeout(myCallback, 100);
            console.log(new Date().toLocaleTimeString() + ": message posted to " + type);
        }
    }
};

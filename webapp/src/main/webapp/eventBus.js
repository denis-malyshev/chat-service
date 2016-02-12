function EventBus() {
    this.consumers = [];

    this.unsubscribeOldConsumers = function (type) {
        for (var j = 0; j < this.consumers.length; j++) {
            if (this.consumers[j].closure == type) {
                console.log(new Date().toLocaleTimeString() + ": consumer " + this.consumers[j].type + " removed");
                this.consumers.splice(j, 1);
                j--;
            }
        }
    }
};
EventBus.prototype.registerConsumer = function (type, consumer, closure) {
    this.consumers.push({
        type: type,
        callback: consumer,
        closure: closure
    });
    console.log(new Date().toLocaleTimeString() + ": added consumer " + type);
};
EventBus.prototype.postMessage = function (type, message) {
    for (var i = 0; i < this.consumers.length; i++) {
        if (this.consumers[i].type == type) {
            var callback = this.consumers[i].callback;
            var myCallback = function () {
                callback(message);
            };
            setTimeout(myCallback, 100);

            console.log(new Date().toLocaleTimeString() + ": message posted to " + type);
        }
    }
    this.unsubscribeOldConsumers(type);
};

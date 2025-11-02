const websocketManager = (() => {
    let stompClient = null;
    let subscriptions = {};

    function connect(callback) {
        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, frame => {
            console.log('Connected: ' + frame);
            if (callback) {
                callback();
            }
        });
    }

    function disconnect() {
        if (stompClient !== null) {
            stompClient.disconnect();
        }
        console.log("Disconnected");
    }

    function subscribe(topic, callback) {
        if (stompClient && stompClient.connected) {
            const subscription = stompClient.subscribe(topic, message => {
                callback(JSON.parse(message.body));
            });
            subscriptions[topic] = subscription;
        } else {
            console.error("STOMP client is not connected.");
        }
    }

    function unsubscribe(topic) {
        if (subscriptions[topic]) {
            subscriptions[topic].unsubscribe();
            delete subscriptions[topic];
        }
    }

    return {
        connect,
        disconnect,
        subscribe,
        unsubscribe
    };
})();
console.log("Welcome to your Play application's JavaScript!");

var webSocket = new WebSocket("ws://localhost:9000/ws");

webSocket.onopen = function (event) {
    console.log(event)
};

webSocket.onmessage = function (evt) {
    console.log("onmessage: " + evt.data)

};

webSocket.onopen = function (event) {
    console.log(event);

    webSocket.send("{\n" +
        "  \"action\": \"openCell\",\n" +
        "  \"position\": {\n" +
        "    \"row\": 1,\n" +
        "    \"col\": 1\n" +
        "  }\n" +
        "}");

};

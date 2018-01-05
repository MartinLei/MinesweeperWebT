"use strict";

class WebSocketController {
    constructor(url, onMessage) {
        this.url = url;
        this.onMessage = onMessage;

        this.createNewWebSocket();
    }

    isConnecting() {
        return this.webSocket.readyState === 0
    }

    isOpen() {
        return this.webSocket.readyState === 1
    }

    isClosed() {
        return this.webSocket.readyState >= 2
    }

    createNewWebSocket() {
        this.messageQueue = [];

        this.webSocket = new WebSocket(this.url);

        this.webSocket.onopen = event => {
            console.log("webSocket.onopen");

            console.log(event);

            console.log(this.messageQueue);

            this.messageQueue.forEach(message => this.webSocket.send(message));

            this.messageQueue = [];
        };

        this.webSocket.onclose = event => {
            console.log("webSocket.onclose");

            console.log(event);
        };

        this.webSocket.onmessage = evt => {
            console.log("webSocket.onmessage");

            let minesweeperEvent = JSON.parse(evt.data);

            this.onMessage(minesweeperEvent);
        };
    }

    send(object) {
        console.log("WebSocketController.send");
        console.log(object);

        let message = JSON.stringify(object);

        if (this.isConnecting()) {
            this.messageQueue.push(message);
        }
        if (this.isOpen()) {
            this.webSocket.send(message);
        }
        if (this.isClosed()) {
            this.createNewWebSocket();
            this.messageQueue.push(message);
        }
    }

    close() {
        console.log("WebSocketController.close");

        this.webSocket.close();
    }
}

class GridController {
    constructor(url) {
        this.wsController = new WebSocketController(url, (minesweeperEvent) => this.onMessage(minesweeperEvent));

        this.wsController.send({action: "join"});

        let vueInstance;
    }


    onMessage(minesweeperEvent) {
        this.updateStatus(minesweeperEvent.gameState);
        this.updateVueGrid(minesweeperEvent.grid);
    }

    updateVueGrid(grid) {
        // set grid on vue
        if (this.vueInstance !== null) {
            console.log("set grid on vue");
            Vue.set(this.vueInstance, "wsField", grid)
        } else {
            console.log("ERROR: vue not set");
        }
    }

    updateStatus(gameState) {
        // set state on vue
        if (this.vueInstance !== null) {
            console.log("set state on vue");
            Vue.set(this.vueInstance, "wsState", gameState)
        } else {
            console.log("ERROR: vue not set");
        }
    }

    sendOpenCell(position){
        this.wsController.send({
            action: "openCell",
            position: position
        });
    }

    sendFlagCell(position){
        this.wsController.send({
            action: "toggleFlag",
            position: position
        });
    }
}

class GameControls {

    constructor(wsController) {
        this.wsController = wsController;
    }

    newGame() {
        this.wsController.send({
            "action": "newGame"
        });
    }

    changeDifficulty(settings) {
        this.wsController.send({
            action: "changeSettings",
            settings: settings
        });
    }
}

$(document).ready(() => {
    let gridController = new GridController("ws://" + location.host + "/ws");
    let gameControls = new GameControls(gridController.wsController);

    window.gridControllerVue = gridController;
    window.gameControlsVue = gameControls;
});

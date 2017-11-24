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
            this.messageQueue.append(message);
        }
    }
}

class GridController {
    constructor(url) {
        this.wsController = new WebSocketController(url, (minesweeperEvent) => this.onMessage(minesweeperEvent));

        this.wsController.send({action: "join"});

        this.gridRoot = $(".grid").first();
        this.gridRoot.contextmenu(() => false);
    }

    onMessage(minesweeperEvent) {
        this.rebuildGrid(minesweeperEvent.grid);
    }

    rebuildGrid(grid) {
        this.gridRoot.empty();

        grid.forEach(row => {
            row.forEach(cell => {
                this.gridRoot.append(this.getCellElement(cell))
            })
        })
    };

    getCellElement(cell) {
        let cellClassString =
            `grid__cell 
             ${cell.isFlagged ? "grid__cell--flagged" : ""}
             ${cell.isRevealed ? "grid__cell--revealed" : ""}
             ${!cell.isRevealed ? "grid__cell--closed" : ""}
             ${cell.isRevealed && cell.hasMine ? "grid__cell--mine" : ""}`;

        let cellElement = $("<a/>", {
            "class": cellClassString
        });

        cellElement.append(this.getCellContentElement(cell));

        cellElement.mousedown(e => this.onCellClick(e, cell));

        return cellElement;
    }

    onCellClick(e, cell) {
        console.log("onCellClick");
        console.log(e);

        e.preventDefault();

        if (e.which === 1) {
            // Left Click

            this.wsController.send({
                action: "openCell",
                position: cell.position
            });
        } else if (e.which === 2) {
            // Middle Click

            this.wsController.send({
                action: "openAround",
                position: cell.position
            });
        } else if (e.which === 3) {
            // Right Click

            this.wsController.send({
                action: "toggleFlag",
                position: cell.position
            });
        }
    }

    getCellContentElement(cell) {
        if (cell.isRevealed) {
            if (cell.hasMine) {
                return $("<div/>", {
                    "class": "grid__content grid__mine"
                }).append("M");
            } else {
                let classString = `grid__content grid__number grid__number--${cell.surroundingMines}`;
                let numberString = cell.surroundingMines > 0 && cell.surroundingMines;

                return $("<div/>", {
                    "class": classString
                }).append(numberString);
            }
        } else {
            if (cell.isFlagged) {
                return $("<img/>", {
                    "class": "grid__content grid__flag",
                    "src": "/assets/images/flag.svg"
                });
            } else {
                return $("<div/>", {
                    "class": "grid__content grid__empty"
                });
            }
        }
    }
}

$(document).ready(() => {
    document.gridBuilder = new GridController("ws://localhost:9000/ws");
});

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

        this.gridRoot = $(".grid").first();
        this.gridRoot.contextmenu(() => false);

        this.statusRoot = $(".game-status").first();

        let vueInstance;
    }

    static getCellContentElement(cell) {
        if (cell.isRevealed) {
            if (cell.hasMine) {
                return $("<img/>", {
                    "class": "grid__content grid__mine",
                    "src": "/assets/images/mine.png"
                });
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

    onMessage(minesweeperEvent) {
        this.setGridCss(minesweeperEvent.settings);
        this.updateGrid(minesweeperEvent.grid);
        this.updateStatus(minesweeperEvent.gameState);

        this.updateVueGrid(minesweeperEvent.grid);
    }

    updateVueGrid(grid) {
        // set grid in vue
        if (this.vueInstance !== null) {
            console.log("set grid on vue");
            Vue.set(this.vueInstance, "wsField", grid)
        } else {
            console.log("ERROR: vue not set");
        }
    }

    setGridCss(settings) {
        this.gridRoot.css({
            "--gridWidth": settings.width.toString(),
            "--gridHeight": settings.height.toString(),
        });
    }

    updateStatus(gameState) {
        if (gameState === "Win") {
            this.statusRoot.empty();

            this.statusRoot.append(
                $("<div/>", {
                    "class": "alert alert-success",
                    "role": "alert",
                }).append("You win!")
            )
        } else if (gameState === "Lose") {
            this.statusRoot.empty();

            this.statusRoot.append(
                $("<div/>", {
                    "class": "alert alert-danger",
                    "role": "alert",
                }).append("Game over!")
            )
        } else {
            this.statusRoot.empty()
        }
    }

    updateGrid(grid) {
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

        cellElement.append(GridController.getCellContentElement(cell));

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

    window.gridController = gridController;
    window.gameControls = gameControls;
});

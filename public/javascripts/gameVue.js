var minesweeperGame;

$(document).ready(function () {
    minesweeperGame = new Vue({
        el: '#minesweeper-game',
        data: function () {
            return {
                wsField: 0
            }
        }
    });

    //bind to game.js
    window.gridController.vueInstance = minesweeperGame;

});

Vue.component('minesweeper-controls', {
    template: `
        <div class="gamecontainer">
           <button class="btn btn-primary" v-on:click="newGame">New Game</button>
           
           <button class="btn btn-secondary dropdown-toggle" type="button" id="dropdownMenuButton" 
              data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Change Difficulty</button>
           <div class="dropdown-menu" aria-labelledby="dropdown-change-difficulty">
              <a class="dropdown-item" href="#" v-on:click="changeDifficultyEasy">Easy</a>
              <a class="dropdown-item" href="#" v-on:click="changeDifficultyIntermediate">Intermediate</a>
              <a class="dropdown-item" href="#" v-on:click="changeDifficultyHard">Hard</a>
           </div>
        </div>
    `,
    methods: {
        newGame: function () {
            window.gameControls.newGame();
        },
        changeDifficultyEasy: function () {
            window.gameControls.changeDifficulty({
                "height": 8,
                "width": 8,
                "mines": 10
            })
        },
        changeDifficultyIntermediate: function () {
            window.gameControls.changeDifficulty({
                "height": 16,
                "width": 16,
                "mines": 40
            })
        },
        changeDifficultyHard: function () {
            window.gameControls.changeDifficulty({
                "height": 16,
                "width": 30,
                "mines": 99
            })
        }
    }
});

Vue.component('minesweeper-field', {
    template: `
    <div class="field-container text-center">
        <a class="field card-2" @click.right="disableRightClickMenu( $event)">

            <a class="field-row" v-for="aRow in field">
              <a v-for="aCell in aRow">
               <minesweeper-cell :cell="aCell" ></minesweeper-cell>
              </a>
            </a>
         
        </a>
    </div>
    `,
    props: ['field'],
    methods:{
        disableRightClickMenu: function (event) {
            event.preventDefault();
        }
    }
});

Vue.component('minesweeper-cell', {
    template: `
       <a v-if="cell.isRevealed">
           <div v-if="cell.hasMine" class="cell--mine"><img src="/assets/images/mine.png"></div>
           <div v-else class="cell--number" v-bind:class="getNumberColor(cell.surroundingMines)">{{cell.surroundingMines}}</div>
       </a>
       <a v-else >
           <div v-if="cell.isFlagged" class="cell--flagged"><img src="/assets/images/flag.svg"></div>
           <div v-else class="cell--normal" v-on:mousedown.left="openCell(cell.position)" @click.right="flagCell( $event, cell.position)"></div>
       </a>     
    `,
    props: ['cell'],
    methods: {
        openCell: function (position) {
            window.gridController.sendOpenCell(position);
        },
        flagCell: function (event,position) {
            event.preventDefault();
            window.gridController.sendFlagCell(position);
        },
        getNumberColor: function(numberOfSurroundMines){
            return 'cell--number--' + numberOfSurroundMines;
        },
    }
});


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
        <div class="gamecontainer">
         <a>FIELD://</a>
         <a>{{field}}</a>
         <a>END</a>
        </div>
    `,
    props: ['field'],
});

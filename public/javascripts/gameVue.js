$(document).ready(function () {
    var minesweeperGame = new Vue({
        el: '#minesweeper-game'
        ,
        data: {
            name: 'Vue.js'
        }
    })

});

Vue.component('minesweeper-controls', {
    template: `
        <div class="gamecontainer">
           <p>TEST COMPONENT</p>
           <button v-on:click="newGame">New Game</button>
        </div>
    `,
    methods: {
        newGame: function () {
            window.gameControls.newGame()
        }
    }
});
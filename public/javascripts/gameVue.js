$(document).ready(function () {
    var minesweeperGame = new Vue({
        el: '#minesweeper-game'
    })

});

Vue.component('minesweeper-field', {
    template: `
        <div class="gamecontainer">
           <p>TEST COMPONENT</p>
        </div>
    `
});
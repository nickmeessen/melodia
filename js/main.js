/*
    Melodia
    Copyright (C) 2009
    Nick Meessen

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General  License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General  License for more details.

    You should have received a copy of the GNU General  License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    ----------------------------------------------------------------------------
*/

var bg = new Image();
var hero = new Hero();

bg.src = "canvas-bg.png";

setInterval(draw, 100);

function draw() {

	var ctx = document.getElementById('viewport').getContext('2d');

	ctx.drawImage(bg, 0, 0);
	// ctx.fillRect(0, 0, w, 50);

	switch(hero.getOrientation()) {

		case 0 : ctx.drawImage(hero.getImage(), hero.getFrame(), 30, 30, 30, hero.getLocationX(), hero.getLocationY(), 30, 30); break;
		case 1 : ctx.drawImage(hero.getImage(), hero.getFrame(), 0, 30, 30, hero.getLocationX(), hero.getLocationY(), 30, 30); break;
		case 2 : ctx.drawImage(hero.getImage(), hero.getFrame(), 60, 30, 30, hero.getLocationX(), hero.getLocationY(), 30, 30); break;
		case 3 : ctx.drawImage(hero.getImage(), hero.getFrame(), 90, 30, 30, hero.getLocationX(), hero.getLocationY(), 30, 30); break;

	}

	hero.update();

}

document.onkeyup = function() {

	switch(event.keyCode) {

		case 37 : hero.stopWalking(); break;
		case 38 : hero.stopWalking(); break;
		case 39 : hero.stopWalking(); break;
		case 40 : hero.stopWalking(); break;
   }

};

document.onkeydown = function() {

	switch(event.keyCode) {

		case 37 : hero.setOrientation(2); hero.startWalking(); break;
		case 38 : hero.setOrientation(1); hero.startWalking(); break;
		case 39 : hero.setOrientation(3); hero.startWalking(); break;
		case 40 : hero.setOrientation(0); hero.startWalking(); break;
   }

};

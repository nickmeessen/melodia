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

function Hero() {
	
	this.getLocationX = getLocationX;
	this.getLocationY = getLocationY;
	this.getOrientation = getOrientation;
	this.setOrientation = setOrientation;
	this.startWalking = startWalking;
	this.stopWalking = stopWalking;
	this.update = update;
	this.getImage = getImage;
	this.getFrame = getFrame;
	
	var walking = false;
	var loc_x = 305;
	var loc_y = 228;
	var orientation = 0;
	var frame = 0;
	var img = new Image();
	
	img.src = "hero.png";
	
	function getImage() {
		return img;
	}
	
	function getLocationX() {
		return loc_x;
	}
	
	function getLocationY() {
		return loc_y;
	}
	
	function getOrientation() {
		return orientation;
	}
	
	function setOrientation(o) {
		orientation = o;
	}
	
	function update() {
		
		if(walking) {
			
			switch(orientation) {
			
				case 0 : loc_y += 5; break;
				case 1 : loc_y -= 5; break;
				case 2 : loc_x -= 5; break;
				case 3 : loc_x += 5; break;
			
			}
			
			frame++;
			
			if(frame == 4) { frame = 0; }

		}
		
	}
	
	function startWalking() {
		walking = true;
	}
	
	function stopWalking() {
		walking = false;
		frame = 0;
	}
	
	function getFrame() {
		return frame * 30;
	}
}
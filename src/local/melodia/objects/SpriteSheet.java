/*
    Melodia
    Copyright (C) 2007-2010
    Nick Meessen (http://nickmeessen.nl)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    ----------------------------------------------------------------------------
*/
package local.melodia.objects;

import local.melodia.util.ImageLoader;

import java.awt.image.BufferedImage;

/**
 * 'SpriteSheet' Class
 *
 * This class represents a spritesheet.
 */
public class SpriteSheet
{

    private BufferedImage sheetImage;
    private String imageUrl;
    private int frameWidth;
    private int frameHeight;
    private int offsetX;
    private int offsetY;

    public SpriteSheet(String imgUrl, int width, int height, int offX, int offY)
    {

        sheetImage = ImageLoader.load(imgUrl);

        imageUrl = imgUrl;

        frameWidth = width;
        frameHeight = height;
        offsetX = offX;
        offsetY = offY;

    }

    public void revert()
    {
        sheetImage = ImageLoader.load(imageUrl);
    }

    public int getFrameCount()
    {
        return sheetImage.getWidth() / frameWidth + 1;
    }

    public BufferedImage getImage()
    {
        return sheetImage;
    }

    public BufferedImage getSprite(int row, int frame)
    {

        int x = (frame * frameWidth) - frameWidth;
        int y = (row * frameHeight) - frameHeight;

        return sheetImage.getSubimage(x, y, frameWidth, frameHeight);
    }

    public int getOffsetX()
    {
        return offsetX;
    }

    public int getOffsetY()
    {
        return offsetY;
    }

}

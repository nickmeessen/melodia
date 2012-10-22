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
package local.melodia.views;

import local.melodia.controllers.Main;
import local.melodia.objects.Area;
import local.melodia.objects.Character;
import local.melodia.util.Developer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * 'AreaView' Class.
 *
 * This Class takes care of the playing field where everything happens. It paints the maps, characters and the console.
 */
public class AreaView extends JPanel
{

    /**
     * Painting the playing field.
     */
    @Override
    public void paint(Graphics g)
    {

        Area activeArea = Main.getWorldMap().getCurrentArea();

        Graphics2D mainGraphics = (Graphics2D) g;

        /* Calculate where the Hero should be painted according to the player's location on screen. */
        int mapX, mapY;
        int heroX, heroY;

        if (Main.getHero().getLocationX() > (746 + Main.getHero().getSpriteOffsetX()))
        {
            heroX = Main.getHero().getLocationX() - 500 - Main.getHero().getSpriteOffsetX();
            mapX = 500;
        } else if (Main.getHero().getLocationX() > (246 + Main.getHero().getSpriteOffsetX()))
        {
            heroX = 250;
            mapX = Main.getHero().getLocationX() - 250 - Main.getHero().getSpriteOffsetX();
        } else
        {
            heroX = Main.getHero().getLocationX() - Main.getHero().getSpriteOffsetX();
            mapX = 0;
        }

        if (Main.getHero().getLocationY() > (838 + Main.getHero().getSpriteOffsetY()))
        {
            heroY = Main.getHero().getLocationY() - 702 - Main.getHero().getSpriteOffsetY();
            mapY = 700;
        } else if (Main.getHero().getLocationY() > (138 + Main.getHero().getSpriteOffsetY()))
        {
            heroY = 140;
            mapY = Main.getHero().getLocationY() - 140 - Main.getHero().getSpriteOffsetY();
        } else
        {
            heroY = Main.getHero().getLocationY() - Main.getHero().getSpriteOffsetY();
            mapY = 0;
        }


        try
        {

            mainGraphics.drawImage(activeArea.getMap().getSubimage(mapX, mapY, 500, 300), 0, 0, null);

            Graphics walkMapGraphics = activeArea.getWalkMap().createGraphics();

            /* Dev-Mode; Painting on WalkMap */
            if (Developer.getEnabled())
            {

                switch (Developer.getPaintMode())
                {

                    case 1:

                        paintSpot(Main.getHero().getLocationX(), Main.getHero().getLocationY(), Developer.getPaintSize(), walkMapGraphics, Color.WHITE);
                        break;

                    case 2:

                        paintSpot(Main.getHero().getLocationX(), Main.getHero().getLocationY(), Developer.getPaintSize(), walkMapGraphics, Color.BLACK);
                        break;
                }
            }

            /* Dev-Mode; Overlay WalkMap */
            if (Developer.getEnabled())
            {

                Composite originalComposite = mainGraphics.getComposite();

                mainGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2F));

                mainGraphics.drawImage(activeArea.getWalkMap().getSubimage(mapX, mapY, 500, 300), 0, 0, null);

                mainGraphics.setComposite(originalComposite);

            }

            /* Put all Characters into a list to sort on LocationY, to paint everything in the proper order by depth. */
            ArrayList<Character> elementsToPaint = new ArrayList<Character>();

            elementsToPaint.addAll(activeArea.getPlayerList());
            elementsToPaint.addAll(activeArea.getMonsterList());

            Collections.sort(elementsToPaint, new Comparator<Character>()
            {

                public int compare(Character char1, Character char2)
                {

                    return char1.getLocationY() - char2.getLocationY();

                }

            });

            for (Character character : elementsToPaint)
            {

                if (Main.getHero().equals(character))
                {

                    mainGraphics.drawImage(character.getImage(), heroX, heroY, null);
                    character.update();

                    if (Main.getClient().getConnected())
                    {

                        Main.getClient().send("LOC::" + Main.getWorldMap().getCurrentArea().getName() + "," + character.getLocationX() + "," + character.getLocationY() + "," + character.getOrientation() + "," + character.getCurrentRow() + "," + character.getCurrentFrame());
                    }

                } else
                {

                    if (!Main.getClient().getConnected())
                    {
                        character.update();
                    }

                    mainGraphics.drawImage(character.getImage(), character.getLocationX() - mapX - character.getSpriteOffsetX(), character.getLocationY() - mapY - character.getSpriteOffsetY(), null);
                }


            }

            /* Paint additional Developer details if Dev Mode is enabled. */
            if (Developer.getEnabled())
            {

                mainGraphics.setColor(Color.CYAN);
                mainGraphics.setFont(mainGraphics.getFont().deriveFont(Font.BOLD, 9));
                mainGraphics.drawString("Current Location : (" + Main.getWorldMap().getCurrentArea().getName() + ") " + Main.getHero().getLocationX() + ", " + Main.getHero().getLocationY(), 5, 15);
                mainGraphics.drawString("WalkCheck Disabled : " + Developer.getWalkCheckDisabled(), 5, 25);
                mainGraphics.drawString("Paintmode : " + Developer.getPaintMode(), 5, 35);
                mainGraphics.drawString("Paintsize : " + Developer.getPaintSize(), 5, 45);

            }

            /* Painting the Console */
            Composite originalComposite = mainGraphics.getComposite();

            mainGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4F));
            mainGraphics.setColor(Color.BLACK);
            mainGraphics.fillRect(0, 245, 500, 60);

            mainGraphics.setComposite(originalComposite);
            mainGraphics.setFont(new Font("Console", Font.PLAIN, 9));
            mainGraphics.setColor(Color.WHITE);

            String[] consoleLines = Main.getConsole().getLines();

            mainGraphics.drawString(consoleLines[3], 6, 258);
            mainGraphics.drawString(consoleLines[2], 6, 270);
            mainGraphics.drawString(consoleLines[1], 6, 282);
            mainGraphics.drawString(consoleLines[0], 6, 294);

            /* Paint HP & Score */
            mainGraphics.setColor(Color.WHITE);
            mainGraphics.setFont(mainGraphics.getFont().deriveFont(Font.BOLD, 9));
            mainGraphics.drawString("HP: " + Main.getHero().getCurrentHP() + "/" + Main.getHero().getMaxHP(), 450, 15);
            mainGraphics.drawString("Score: " + Main.getHero().getScore(), 450, 30);


        } catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    /**
     * Paints a spot on the given graphics, in our case the WalkMaps.
     *
     * @param x    (where to paint)
     * @param y    (where to paint)
     * @param g    (the destination image's graphics)
     * @param size (the size of the raster to paint)
     * @param c    (the colour to paint).
     */
    public void paintSpot(int x, int y, int size, Graphics g, Color c)
    {

        int initX, initY, tempX, tempY, endX, endY;

        initX = x - ((size / 2) * 5);
        initY = y - ((size / 2) * 5);

        tempX = initX;
        tempY = initY;

        endX = initX + (size * 5);
        endY = initY + (size * 5);

        while (tempY != endY)
        {

            while (tempX != endX)
            {

                g.setColor(c);
                g.fillRect(tempX - 1, tempY - 1, 3, 3);

                tempX += 5;

            }

            tempX = initX;
            tempY += 5;
        }

    }

}

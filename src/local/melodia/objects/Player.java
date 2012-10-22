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

import local.melodia.controllers.Main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

/**
 * 'Player' Class
 *
 * This class represents a player, both the Hero and any friends that might be playing along.
 */
public class Player extends Character
{

    private boolean isHero;

    private Color playerColour;
    private int Score;

    public Player(String name, Color colour, int x, int y)
    {

        characterName = name;
        playerColour = colour;
        locationX = x;
        locationY = y;

        currentRow = 1;

        currentFrame = 1;
        currentOrientation = 2;

        maxHP = 15;
        currentHP = maxHP;
        Damage = 1;
        Range = 30;

        spriteSheet = new SpriteSheet("images/sheets/player.png", 50, 50, 24, 32);

        updateColour();
    }

    public void setHero(boolean b)
    {
        isHero = b;
    }

    public boolean isHero()
    {
        return isHero;
    }

    public void updateSpawnLocation()
    {
        spawnlocationX = locationX;
        spawnlocationY = locationY;
    }

    public String getColour()
    {
        return Integer.toString(playerColour.getRGB());
    }

    /**
     * Assigns a new colour to this player.
     *
     * @param colour (the new colour to be assigned to this player).
     */
    public void setColour(Color colour)
    {

        playerColour = colour;

        updateColour();

        if (isHero && Main.getClient().getConnected())
        {

            Main.getClient().send("COLOUR::" + Main.getWorldMap().getCurrentArea().getName() + "," + Integer.toString(playerColour.getRGB()));
        }

    }

    /**
     * Replaces the old colours on the spritesheet with the colour assigned to this player.
     */
    public void updateColour()
    {

        spriteSheet.revert();

        BufferedImage sheetImage = spriteSheet.getImage();

        int width = sheetImage.getWidth(null);
        int height = sheetImage.getHeight(null);
        int[] oldData = new int[width * height];

        int[] newData = sheetImage.getRGB(0, 0, width, height, oldData, 0, width);

        int x = 0;

        for (int i : newData)
        {

            if (i == -3407668)
            {
                newData[x] = playerColour.getRGB();
            }

            if (i == -65281)
            {
                newData[x] = playerColour.brighter().getRGB();
            }

            x++;

        }

        sheetImage.setRGB(0, 0, width, height, newData, 0, width);

    }

    public void attack()
    {

        if (isAttacking)
        {
            return;
        }

        currentFrame = 1;

        isWalking = false;
        isAttacking = true;

        try
        {

            Random randomGen = new Random();

            int i = randomGen.nextInt(3);

            Clip clip = AudioSystem.getClip();

            AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File("audio/attack" + i + ".wav"));

            clip.open(inputStream);
            clip.start();

        } catch (Exception ex)
        {

            ex.printStackTrace();

        }
    }

    public void increaseScore()
    {
        setScore(getScore() + 1);
        if (getScore() % 3 == 0)
        {
            maxHP++;
            Damage++;
            currentHP = maxHP;
        }
    }

    /**
     * @return the Score
     */
    public int getScore()
    {
        return Score;
    }

    /**
     * @param Score the Score to set
     */
    public void setScore(int Score)
    {
        this.Score = Score;
    }


}

/*
    Melodia
    Copyright (C) 2007-2011
    Nick Meessen (http://nickmeessen.nl)
    David Strijbos

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

import java.util.Random;

/**
 * 'Monster' Class
 *
 * This class represents a monster.
 */
public class Monster extends Character
{

    protected boolean isScanning;
    protected boolean hasAggro;
    protected boolean isStatic;

    protected Player currentTarget;

    protected int stepsTaken;

    protected String monsterType;

    protected String activeArea;

    public Monster(String name, String type, int x, int y, String area)
    {
        characterName = name;

        monsterType = type;
        locationX = x;
        locationY = y;
        currentRow = 1;

        currentFrame = 1;
        currentOrientation = 1;

        activeArea = area;

        if (type.equalsIgnoreCase("flower"))
        {
            maxHP = 2;
            currentHP = maxHP;
            Damage = 2;
            Range = 40;
            isStatic = true;
            isRandomWalking = false;
            isScanning = true;
            spriteSheet = new SpriteSheet("images/sheets/" + type + ".png", 50, 50, 24, 32);

        } else if (type.equalsIgnoreCase("mushroom"))
        {
            maxHP = 3;
            currentHP = maxHP;
            Damage = 1;
            Range = 30;
            isStatic = false;
            isRandomWalking = true;
            isScanning = true;
            spriteSheet = new SpriteSheet("images/sheets/" + type + ".png", 50, 50, 24, 32);
        }

    }

    @Override
    public void update()
    {

        if (isDying)
        {

            currentRow = 13;

            currentFrame += 1;

            if (currentFrame == spriteSheet.getFrameCount())
            {

                currentFrame = 1;

                isDying = false;

                if (Main.getServer().getListening())
                {

                    Main.getServer().getPopulation().removeMonster(characterName);

                } else
                {

                    Main.getWorldMap().getCurrentArea().removeMonster(this);

                }
            }

        } else if (isAttacking)
        {

            currentRow = currentOrientation + 8;

            currentFrame += 1;

            if (currentFrame == spriteSheet.getFrameCount())
            {

                currentFrame = 1;
                isAttacking = false;
                processAttack();
            }

        } else if (isWalking)
        {

            currentRow = currentOrientation + 4;

            setLocationX(getNextLocationX());
            setLocationY(getNextLocationY());

            currentFrame += 1;

            if (currentFrame == spriteSheet.getFrameCount())
            {

                currentFrame = 1;
            }

        } else if (isRandomWalking)
        {//The monster roams around aimlessly, turning when encountering obstacles and every few steps taken

            if (stepsTaken % 25 == 0)
            {
                randomDirection();
            }


            if (Main.getWorldMap().getCurrentArea().isWalkable(this))
            {
                currentRow = currentOrientation + 4;

                setLocationX(getNextLocationX());
                setLocationY(getNextLocationY());
                stepsTaken++;

                currentFrame += 1;

                if (currentFrame == spriteSheet.getFrameCount())
                {

                    currentFrame = 1;
                }
            } else randomDirection();

        } else
        {

            currentRow = currentOrientation;

        }

        if (!Main.getWorldMap().getCurrentArea().isWalkable(this))
        {//if a monster encounters an obstacle when trying to move to a player, it will randomly attempt to get past it

            randomDirection();

        } else if (hasAggro & currentTarget != null & Main.getWorldMap().getCurrentArea().isWalkable(this))
        {//Here a monster with a valid target will attempt to reach its target and attack it
            if (this.isNearTarget(currentTarget))
            {//If the monster is close to its target, it will start attacking
                faceTarget(currentTarget);
                isWalking = false;
                isAttacking = true;
            } else if (!isStatic)
            {//If the monster is not close enough, it tries to walk closer
                faceTarget(currentTarget);
                isWalking = true;
            }
        } else if (isScanning)
        {

            Player tempplayer = scanForPlayers();

            if (tempplayer != null)
            {
                setTarget(tempplayer);

            } else clearTarget();

        }


    }

    public Player scanForPlayers() //This method checks to see if a player is nearby
    {
        for (int x = locationX - 100; x < locationX + 100; x++)
        {
            for (int y = locationY - 100; y < locationY + 100; y++)
            {

                Character tempchar = Main.getWorldMap().getArea(activeArea).checkLocationForCharacter(x, y);

                if (tempchar != null)
                {
                    if (tempchar instanceof Player)
                    {
                        return (Player) tempchar;
                    }
                }

            }
        }

        return null;
    }

    public void setTarget(Player p)
    {
        isScanning = false;
        isRandomWalking = false;
        currentTarget = p;
        hasAggro = true;
        faceTarget(p);
    }

    public void clearTarget()
    {
        currentTarget = null;
        hasAggro = false;
        isWalking = false;
        isScanning = true;
        if (!isStatic)
        {
            isRandomWalking = true;
        }
    }

    public String getType()
    {
        return monsterType;
    }

    public String getActiveArea()
    {
        return activeArea;
    }

    public void randomDirection()
    {
        Random randomgen = new Random();
        int nr = randomgen.nextInt(3);

        switch (nr)
        {
            case 0:
                turnAround();
                break;
            case 1:
                turnLeft();
                break;
            case 2:
                turnRight();
                break;
        }
    }


}

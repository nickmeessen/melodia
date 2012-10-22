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

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * 'Character' Class
 *
 * This class represents a character; a Hero, another Player, a Monster or an NPC (Non Playable Character).
 */
public class Character
{

    protected String characterName;

    protected SpriteSheet spriteSheet;

    protected int spawnlocationX;
    protected int spawnlocationY;
    protected int locationX;
    protected int locationY;

    protected int currentOrientation;
    protected int currentFrame;
    protected int currentRow;

    protected boolean isWalking;
    protected boolean isRandomWalking;
    protected boolean isAttacking;
    protected boolean isDying;

    protected int maxHP;
    protected int currentHP;
    protected int Damage;
    protected int Range;

    public void setAttacking(boolean attacking)
    {
        isAttacking = attacking;
    }

    public void setWalking(boolean walking)
    {

        if (!walking)
        {
            currentRow = currentOrientation;
        }

        isWalking = walking;
    }

    public void setRandomWalking(boolean walking)
    {
        isRandomWalking = walking;
    }

    public void setDying(boolean dying)
    {
        isDying = dying;
    }

    public BufferedImage getImage()
    {
        return spriteSheet.getSprite(currentRow, currentFrame);
    }

    public SpriteSheet getSpriteSheet()
    {
        return spriteSheet;
    }

    public String getName()
    {
        return characterName;
    }

    public int getLocationX()
    {
        return locationX;
    }

    public int getLocationY()
    {
        return locationY;
    }

    public void setName(String name)
    {
        characterName = name;
    }

    public void setLocationX(int x)
    {
        locationX = x;
    }

    public void setLocationY(int y)
    {
        locationY = y;
    }

    public int getNextLocationX()
    {

        switch (currentOrientation)
        {

            case 3:
                return locationX - 5;
            case 4:
                return locationX + 5;
            default:
                return locationX;
        }
    }

    public int getNextLocationY()
    {

        switch (currentOrientation)
        {

            case 1:
                return locationY - 5;
            case 2:
                return locationY + 5;
            default:
                return locationY;
        }
    }

    public int getCurrentFrame()
    {
        return currentFrame;
    }

    public void setCurrentFrame(int m)
    {
        currentFrame = m;
    }

    public int getCurrentRow()
    {
        return currentRow;
    }

    public void setCurrentRow(int r)
    {
        currentRow = r;
    }

    public void setOrientation(int z)
    {
        currentOrientation = z;
    }

    public int getOrientation()
    {
        return currentOrientation;
    }

    public int getSpriteOffsetX()
    {
        return spriteSheet.getOffsetX();
    }

    public int getSpriteOffsetY()
    {
        return spriteSheet.getOffsetY();
    }

    /**
     * Update the character's location and active sprite image.
     */
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

                currentHP = maxHP;
                locationX = spawnlocationX;
                locationY = spawnlocationY;
                currentRow = currentOrientation;


            }
        } else if (isWalking)
        {
            isAttacking = false;

            if (!Main.getWorldMap().getCurrentArea().isWalkable(this))
            {

                isWalking = false;

                currentRow = currentOrientation;

                return;
            }

            currentRow = currentOrientation + 4;

            setLocationX(getNextLocationX());
            setLocationY(getNextLocationY());

            currentFrame += 1;

            if (currentFrame == spriteSheet.getFrameCount())
            {

                currentFrame = 1;
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
        }

    }

    /**
     * Processes the attack by this character.
     */
    public void processAttack()
    {

        ArrayList<Character> templist = new ArrayList<Character>();

        switch (currentOrientation)
        {

            case 1:

                for (int x = locationX - (Range / 2); x < locationX + (Range / 2); x++)
                {

                    for (int y = locationY; y > locationY - Range; y--)
                    {
                        Character tempchar = Main.getWorldMap().getCurrentArea().checkLocationForCharacter(x, y);

                        if (tempchar != null & !templist.contains(tempchar) & tempchar != this)
                        {
                            templist.add(tempchar);
                        }
                    }
                }

                break;

            case 2:

                for (int x = locationX - (Range / 2); x < locationX + (Range / 2); x++)
                {

                    for (int y = locationY; y < locationY + Range; y++)
                    {
                        Character tempchar = Main.getWorldMap().getCurrentArea().checkLocationForCharacter(x, y);

                        if (tempchar != null & !templist.contains(tempchar) & tempchar != this)
                        {
                            templist.add(tempchar);
                        }
                    }
                }

                break;

            case 3:

                for (int x = locationX; x > locationX - Range; x--)
                {

                    for (int y = locationY - (Range / 2); y < locationY + (Range / 2); y++)
                    {
                        Character tempchar = Main.getWorldMap().getCurrentArea().checkLocationForCharacter(x, y);

                        if (tempchar != null & !templist.contains(tempchar) & tempchar != this)
                        {
                            templist.add(tempchar);
                        }
                    }
                }

                break;

            case 4:

                for (int x = locationX; x < locationX + Range; x++)
                {

                    for (int y = locationY - (Range / 2); y < locationY + (Range / 2); y++)
                    {
                        Character tempchar = Main.getWorldMap().getCurrentArea().checkLocationForCharacter(x, y);

                        if (tempchar != null & !templist.contains(tempchar) & tempchar != this)
                        {
                            templist.add(tempchar);
                        }
                    }
                }

                break;
        }

        if (!templist.isEmpty())
        {

            for (Character c : templist)
            {
                c.getHitBy(this);
            }
        }


    }

    /**
     * Processes the attack from the given Character on this Character.
     *
     * @param c (the character that attacked this character).
     */
    public void getHitBy(Character c)
    {

        if (Main.getClient().getConnected())
        {
            Main.getClient().send("HIT::" + c.getName() + "," + characterName + "," + Main.getWorldMap().getCurrentArea().getName());
        }


        //Character gets hit by monster
        if (c instanceof Monster)
        {

            Monster m = (Monster) c;
            setCurrentHP(getCurrentHP() - m.Damage);
            knockedBackBy(c);

            //Character dies
            if (getCurrentHP() <= 0)
            {
                startDying();
                m.clearTarget();
            }

            //A player hits another character
        } else if (c instanceof Player)
        {
            Player p = (Player) c;

            //if the character getting hit is a monster, it applies damge
            if (this instanceof Monster)
            {
                Monster m = (Monster) this;
                if (!m.isStatic) knockedBackBy(p);

                setCurrentHP(getCurrentHP() - p.Damage);
                if (getCurrentHP() <= 0)
                {
                    startDying();
                    p.increaseScore();
                }
            } else knockedBackBy(p); //if the character getting hit is a player, is simply knocks them back

        }
    }

    //This method is only called when a client receives a message from it's server,
    // telling it that other characters are fighting
    public void processHit(Character c)
    {

        //Character gets hit by monster
        if (c instanceof Monster)
        {

            Monster m = (Monster) c;
            setCurrentHP(getCurrentHP() - m.Damage);
            knockedBackBy(c);

            if (getCurrentHP() <= 0)
            {

                //Character dies
                startDying();
                m.clearTarget();
            }

            // A player hits another character
        } else if (c instanceof Player)
        {
            Player p = (Player) c;

            if (this instanceof Monster)
            {

                //if the character getting hit is a monster, it applies damge

                Monster m = (Monster) this;
                if (!m.isStatic) knockedBackBy(p);

                setCurrentHP(getCurrentHP() - p.Damage);
                if (getCurrentHP() <= 0)
                {
                    startDying();
                    p.increaseScore();
                }
            } else knockedBackBy(p); //if the character getting hit is a player, is simply knocks them back

        }

    }


    // A character can get knocked back when hit
    public void knockedBackBy(Character c)
    {

        int tempOrientation = this.getOrientation();
        setOrientation(c.getOrientation());

        if (Main.getWorldMap().getCurrentArea().isWalkable(this))
        {
            setLocationX(getNextLocationX());
            setLocationY(getNextLocationY());

            setOrientation(tempOrientation);
        }
    }

    public void startDying()
    {
        isAttacking = false;
        isRandomWalking = false;
        isWalking = false;
        isDying = true;

        if (this instanceof Monster)
        {
            Monster m = (Monster) this;

            if (Main.getClient().getConnected())
            {
                Main.getClient().send("MKILL::" + m.getName() + "," + Main.getWorldMap().getCurrentArea().getName());
            } else
            {
                Main.getWorldMap().getCurrentArea().removeMonster(m);
            }
        }
    }

    public void turnAround()
    {
        if ((getOrientation() == 1) || (getOrientation() == 3))
        {

            currentOrientation++;
        } else if ((getOrientation() == 2) || (getOrientation() == 4))
        {
            currentOrientation--;
        }
    }

    public void turnLeft()
    {
        switch (currentOrientation)
        {
            case 1:
                currentOrientation = 4;
                break;
            case 2:
                currentOrientation--;
                break;
            case 3:
                currentOrientation--;
                break;
            case 4:
                currentOrientation--;
                break;
        }
    }

    public void turnRight()
    {
        switch (currentOrientation)
        {
            case 1:
                currentOrientation++;
                break;
            case 2:
                currentOrientation++;
                break;
            case 3:
                currentOrientation++;
                break;
            case 4:
                currentOrientation = 1;
                break;
        }
    }

    // this method tries to determine the direction this character should face to reach c
    public void faceTarget(Character c)
    {

        int x = c.getLocationX();
        int y = c.getLocationY();
        int xDif, yDif;

        xDif = x % this.getLocationX();
        yDif = y % this.getLocationY();

        if (x >= this.locationX & y >= this.locationY & xDif > yDif)
        {
            this.setOrientation(4);
        } else if (x <= this.locationX & y >= this.locationY & xDif > yDif)
        {
            this.setOrientation(3);
        } else if (x <= this.locationX & y <= this.locationY & xDif > yDif)
        {
            this.setOrientation(3);
        } else if (x >= this.locationX & y <= this.locationY & xDif > yDif)
        {
            this.setOrientation(4);
        } else if (x >= this.locationX & y >= this.locationY & xDif < yDif)
        {
            this.setOrientation(2);
        } else if (x <= this.locationX & y <= this.locationY & xDif < yDif)
        {
            this.setOrientation(1);
        } else if (x >= this.locationX & y <= this.locationY & xDif < yDif)
        {
            this.setOrientation(1);
        } else if (x <= this.locationX & y >= this.locationY & xDif < yDif)
        {
            this.setOrientation(2);
        }
    }

    // This method checks to see if target is within hitting range of the character calling the method
    public boolean isNearTarget(Character target)
    {
        for (int x = locationX - (Range / 2); x < locationX + (Range / 2); x++)
        {

            for (int y = locationY - (Range / 2); y < locationY + (Range / 2); y++)
            {

                Character tempchar = Main.getWorldMap().getCurrentArea().checkLocationForCharacter(x, y);

                if (tempchar != null)
                {
                    if (tempchar == target) return true;
                }
            }
        }

        return false;

    }

    /**
     * @return the maxHP
     */
    public int getMaxHP()
    {
        return maxHP;
    }

    /**
     * @param MaxHP the maxHP to set
     */
    public void setMaxHP(int MaxHP)
    {
        this.maxHP = MaxHP;
    }

    /**
     * @return the currentHP
     */
    public int getCurrentHP()
    {
        return currentHP;
    }

    /**
     * @param currentHP the currentHP to set
     */
    public void setCurrentHP(int currentHP)
    {
        this.currentHP = currentHP;
    }
}


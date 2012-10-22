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
import local.melodia.util.Developer;
import local.melodia.util.ImageLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

/**
 * 'Area' Class
 *
 * This class represents an Area.
 */
public class Area
{

    private String areaName;

    private BufferedImage areaImage;
    private BufferedImage walkmapImage;

    private String northArea;
    private String westArea;
    private String eastArea;
    private String southArea;

    private ArrayList<Player> playerList;
    private ArrayList<Monster> monsterList;

    public Area(String name, String fileName)
    {

        playerList = new ArrayList<Player>();
        monsterList = new ArrayList<Monster>();

        areaName = name;

        try
        {

            File areaFile = new File("db/areas/" + fileName);

            Document areaDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(areaFile);

            NamedNodeMap attr = areaDocument.getElementsByTagName("area").item(0).getAttributes();

            northArea = attr.getNamedItem("north").getTextContent();
            southArea = attr.getNamedItem("south").getTextContent();
            westArea = attr.getNamedItem("west").getTextContent();
            eastArea = attr.getNamedItem("east").getTextContent();

            NodeList monsterList = areaDocument.getElementsByTagName("monster");

            for (int i = 0; i < monsterList.getLength(); i++)
            {

                Element element = (Element) monsterList.item(i);

                addMonster(new Monster("Monster" + UUID.randomUUID().toString(), element.getAttribute("type"), Integer.parseInt(element.getAttribute("x")), Integer.parseInt(element.getAttribute("y")), areaName));

            }

            walkmapImage = ImageLoader.load("images/areas/WalkMap_" + areaName + ".png");


        } catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    public String getName()
    {
        return areaName;
    }

    public BufferedImage getMap()
    {
        return areaImage;
    }

    public BufferedImage getWalkMap()
    {
        return walkmapImage;
    }

    public void setWalkMap(BufferedImage newWalkMapImage)
    {
        walkmapImage = newWalkMapImage;
    }

    /**
     * Checks if the given location is walkable, if the location to be checked is out of the map bounds move the
     * player to the next area.
     *
     * @param c (The character for which it's next location to check.)
     * @return true/false depending on the colour value of the given coordinates.
     */
    public boolean isWalkable(Character c)
    {

        int x = c.getNextLocationX();
        int y = c.getNextLocationY();

        if (Main.getHero().equals(c))
        {

            if ((y <= 20) && !northArea.isEmpty())
            {

                c.setLocationY(970);

                Main.getWorldMap().setCurrentArea(Main.getWorldMap().getArea(northArea));

            }

            if ((y >= 970) && !southArea.isEmpty())
            {

                c.setLocationY(20);

                Main.getWorldMap().setCurrentArea(Main.getWorldMap().getArea(southArea));

            }

            if ((x <= 30) && !westArea.isEmpty())
            {

                c.setLocationX(970);

                Main.getWorldMap().setCurrentArea(Main.getWorldMap().getArea(westArea));

            }

            if ((x >= 970) && !eastArea.isEmpty())
            {

                c.setLocationX(30);

                Main.getWorldMap().setCurrentArea(Main.getWorldMap().getArea(eastArea));

            }

        }

        if (walkmapImage != null)
        {

            if (Main.getHero().equals(c) && Developer.getWalkCheckDisabled())
            {
                return true;
            } else
            {
                return (walkmapImage.getRGB(x, y) > -1000000);
            }

        } else
        {
            return false;
        }

    }

    public void loadImage()
    {

        try
        {
            areaImage = ImageLoader.load("images/areas/Area_" + areaName + ".jpg");

        } catch (Exception ex)
        {

            JOptionPane.showMessageDialog(new JFrame(), "Error loading files.", "There was an error trying to load the map files, please check your areas folder.", JOptionPane.ERROR_MESSAGE);

            System.exit(0);
        }

    }

    public void clearImage()
    {
        areaImage.flush();

        areaImage = null;

    }


    public ArrayList<Player> getPlayerList()
    {
        return playerList;
    }

    public ArrayList<Monster> getMonsterList()
    {
        return monsterList;
    }

    public void addPlayer(Player player)
    {
        player.updateSpawnLocation();

        playerList.add(player);
    }

    public void removePlayer(Player player)
    {
        playerList.remove(player);
    }

    public Player searchPlayer(String name)
    {

        Player match = null;

        for (Player player : playerList)
        {

            if (player.getName().equals(name))
            {

                match = player;
                break;

            }

        }

        return match;

    }

    public void renamePlayer(String oldName, String newName)
    {

        Player player = searchPlayer(oldName);

        if (player != null)
        {

            player.setName(newName);

        }
    }

    public void updatePlayer(String name, String locationX, String locationY, String orientation, String currentRow, String currentFrame)
    {

        Player player = searchPlayer(name);

        if (player != null)
        {

            player.setLocationX(Integer.parseInt(locationX));
            player.setLocationY(Integer.parseInt(locationY));

            player.setOrientation(Integer.parseInt(orientation));
            player.setCurrentRow(Integer.parseInt(currentRow));
            player.setCurrentFrame(Integer.parseInt(currentFrame));

        }
    }

    public void updateMonster(String name, String locationX, String locationY, String orientation, String currentRow, String currentFrame)
    {

        Monster monster = searchMonster(name);

        if (monster != null)
        {

            monster.setLocationX(Integer.parseInt(locationX));
            monster.setLocationY(Integer.parseInt(locationY));

            monster.setOrientation(Integer.parseInt(orientation));
            monster.setCurrentRow(Integer.parseInt(currentRow));
            monster.setCurrentFrame(Integer.parseInt(currentFrame));

        }
    }

    public void updatePlayer(String name, String colour)
    {

        Player player = searchPlayer(name);

        if (player != null)
        {

            player.setColour(new Color(Integer.parseInt(colour)));

        }

    }

    public void addMonster(Monster monster)
    {
        monsterList.add(monster);
    }

    public void removeMonster(Monster monster)
    {
        monsterList.remove(monster);
    }

    public Monster searchMonster(String name)
    {
        Monster match = null;

        for (Monster monster : monsterList)
        {
            if (monster.getName().equals(name))
            {
                match = monster;
                break;

            }

        }

        return match;

    }


    /**
     * Checks given location for a player or character.
     *
     * @param x (The x-location to be checked).
     * @param y (The y-location to be checked).
     * @return the character found at the given location.
     */
    public Character checkLocationForCharacter(int x, int y)
    {

        for (Player p : playerList)
        {

            if (p.getLocationX() == x & p.getLocationY() == y)
            {
                return p;
            }
        }

        for (Monster m : monsterList)
        {

            if (m.getLocationX() == x & m.getLocationY() == y)
            {
                return m;
            }
        }

        return null;
    }

}

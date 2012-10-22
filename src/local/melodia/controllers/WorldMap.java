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
package local.melodia.controllers;

import local.melodia.objects.Area;

import java.io.File;
import java.util.HashMap;

/**
 * 'WorldMap' Class
 *
 * This class represents the 'worldmap', it holds a collection of Area's.
 */
public class WorldMap
{

    private Area activeArea;
    private File areaFiles;
    private HashMap<String, Area> worldMap;

    public WorldMap()
    {

        worldMap = new HashMap<String, Area>();

        areaFiles = new File("db/areas/");

        for (File file : areaFiles.listFiles())
        {

            if (file.getName().substring(file.getName().lastIndexOf(".")).equals(".xml"))
            {

                String mapname = file.getName().replace(".xml", "").replace("Area_", "");

                worldMap.put(mapname, new Area(mapname, file.getName()));
            }

        }

        activeArea = worldMap.get("A2");

        activeArea.loadImage();

    }

    public Area getArea(String label)
    {
        return worldMap.get(label);
    }

    public Area getCurrentArea()
    {
        return activeArea;
    }

    public void setCurrentArea(Area area)
    {

        if (Main.getClient().getConnected())
        {
            Main.getClient().send("MAP::" + activeArea.getName() + "," + area.getName());

            area.getMonsterList().clear();

            Main.getClient().send("REQML::" + area.getName());
        }

        activeArea.removePlayer(Main.getHero());

        activeArea.clearImage();

        activeArea = area;

        activeArea.loadImage();

        activeArea.addPlayer(Main.getHero());

    }

    public void clearMonsterLists()
    {

        for (Area area : worldMap.values())
        {

            area.getMonsterList().clear();

        }

    }
}

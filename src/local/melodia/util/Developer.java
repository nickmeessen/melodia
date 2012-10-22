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
package local.melodia.util;

import local.melodia.controllers.Main;
import local.melodia.objects.Area;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 'Developer' Class
 *
 * This class holds any extras we use to develop real time in game.
 */
public class Developer
{

    private static boolean developerEnabled;
    private static boolean walkcheckDisabled;

    private static int paintSize;
    private static int paintMode;

    private static BufferedImage activeImage;

    /**
     * Creates a new empty walkmap.
     *
     * @param area (the area for which this walkmap is ment).
     */
    public static void createMap(Area area)
    {

        Main.getConsole().appendLine("Creating an empty walkmap for this area..");

        BufferedImage img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

        img.createGraphics().setColor(Color.BLACK);
        img.createGraphics().fillRect(0, 0, 1000, 1000);

        activeImage = img;

        area.setWalkMap(activeImage);
    }

    /**
     * Saves the walkmap for the given area.
     *
     * @param area (the area where the walkmap of should be saved).
     */
    public static void saveMap(Area area)
    {

        Main.getConsole().appendLine("Saving current walkmap for this area..");

        if (activeImage == null)
        {
            activeImage = area.getWalkMap();
        }

        try
        {
            ImageIO.write(activeImage, "PNG", new File("images/areas/WalkMap_" + area.getName() + ".png"));

            Main.getConsole().appendLine("Walkmap saved succesfully.");

        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public static void switchPaintMode()
    {

        if (paintMode == 1)
        {
            paintMode = 2;
        } else
        {
            paintMode = 1;
        }

    }


    public static int getPaintMode()
    {
        return paintMode;
    }

    public static int getPaintSize()
    {
        return paintSize;
    }

    public static void incPaintSize()
    {
        paintSize += 1;
    }

    public static void decPaintSize()
    {
        paintSize -= 1;
    }

    public static void switchWalkCheck()
    {

        walkcheckDisabled = !walkcheckDisabled;

    }

    public static boolean getWalkCheckDisabled()
    {
        return walkcheckDisabled;
    }

    public static boolean getEnabled()
    {
        return developerEnabled;
    }

    public static void setEnabled(boolean b)
    {
        developerEnabled = b;
    }

}

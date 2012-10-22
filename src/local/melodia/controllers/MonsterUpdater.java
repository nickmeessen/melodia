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


/**
 * 'MonsterUpdater' Class
 *
 * This class updates all the monsters currently residing on the server, every 80 ms.
 */
public class MonsterUpdater extends Thread
{

    public void run()
    {
        while (true)
        {

            try
            {

                Main.getServer().getPopulation().updateMonsters();

                sleep(80);

            } catch (Exception ex)
            {
            }

        }
    }


}

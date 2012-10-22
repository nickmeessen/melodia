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

import java.util.ArrayList;

/**
 * 'ConsoleView' Class
 *
 * This class represents the Console, it keeps track of any console messages.
 */
public class ConsoleView
{

    private ArrayList<String> consoleLog;

    public ConsoleView()
    {

        consoleLog = new ArrayList<String>();

        appendLine("SYSTEM :: Initializing..");
        appendLine("SYSTEM :: Welcome to the world of Melodia, enjoy your stay!");
        appendLine("SYSTEM :: To invite your friends, type /server <port>");
        appendLine("SYSTEM :: To play on a friends server type /connect <ip>:<port>");


    }

    public void appendLine(String text)
    {
        consoleLog.add(text);
    }

    /**
     * Returns the last 4 lines added to the log.
     *
     * @return String[] lines   (last 4 lines added to the log)
     */
    public String[] getLines()
    {

        String[] lines =

                {
                        consoleLog.get(consoleLog.size() - 1),
                        consoleLog.get(consoleLog.size() - 2),
                        consoleLog.get(consoleLog.size() - 3),
                        consoleLog.get(consoleLog.size() - 4)
                };


        return lines;
    }

}

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

import local.melodia.objects.Monster;
import local.melodia.objects.Player;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 'Connector' Class
 *
 * This class is used to connect to another game's server.
 */
public class Connector extends Thread
{

    private Socket clientSocket;
    private PrintWriter clientOutput;
    private BufferedReader clientInput;

    private boolean clientConnected;

    public Connector()
    {

        clientConnected = false;
    }

    public boolean getConnected()
    {
        return clientConnected;
    }

    /**
     * Connect to a given server.
     *
     * @param serverAddress (the address of the server to connect to)
     * @param serverPort    (the port on which to connect)
     */
    public void connect(String serverAddress, int serverPort)
    {

        try
        {

            clientSocket = new Socket(serverAddress, serverPort);
            clientOutput = new PrintWriter(clientSocket.getOutputStream(), true);
            clientInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            Main.getConsole().appendLine("CLIENT :: Connection succesful.");

            clientConnected = true;

            start();

            Main.getWorldMap().clearMonsterLists();

        } catch (UnknownHostException ex)
        {

            Main.getConsole().appendLine("CLIENT :: Can't seem to find '" + serverAddress + "'. Please check your network settings.");

        } catch (Exception ex)
        {

            Main.getConsole().appendLine("CLIENT :: Unable to connect to '" + serverAddress + "'. Please check your network settings.");
        }

    }

    /**
     * Disconnect from the server.
     */
    public void disconnect()
    {

        Main.getConsole().appendLine("CLIENT :: Disconnected");

        Main.getWorldMap().getCurrentArea().getPlayerList().clear();
        Main.getWorldMap().getCurrentArea().getPlayerList().add(Main.getHero());

        clientConnected = false;
        interrupt();

    }


    /**
     * Send a message to the server.
     *
     * @param payload (the message to be sent to the server)
     */
    public void send(String payload)
    {

        try
        {
            clientOutput.println(payload);
        } catch (Exception ex)
        {
            ex.printStackTrace();
            Main.getConsole().appendLine("ERROR :: Couldn't send packet!");
        }

    }


    /**
     * Runs this thread and listens for any messages received from the server to process.
     */
    public void run()
    {

        String receivedMsg;

        try
        {

            while ((receivedMsg = clientInput.readLine()) != null)
            {

                if (receivedMsg.contains("REQDATA"))
                {

                    send("MAP::A2," + Main.getWorldMap().getCurrentArea().getName());
                    send("COLOUR::" + Main.getWorldMap().getCurrentArea().getName() + "," + Main.getHero().getColour());
                    send("REQML::" + Main.getWorldMap().getCurrentArea().getName());

                }

                if (receivedMsg.contains("ADD::"))
                {

                    if (receivedMsg.substring(5).startsWith("Monster"))
                    {

                        String name = receivedMsg.substring(5, receivedMsg.indexOf(","));

                        String[] monsterData = receivedMsg.substring(receivedMsg.indexOf(",") + 1).split(",");

                        Main.getWorldMap().getArea(monsterData[0]).addMonster(new Monster(name, monsterData[1], Integer.parseInt(monsterData[2]), Integer.parseInt(monsterData[3]), monsterData[0]));

                    } else
                    {
                        Main.getWorldMap().getArea("A2").addPlayer(new Player(receivedMsg.substring(5), Color.decode("#43810C"), 800, 705));
                    }

                }

                if (receivedMsg.contains("DEL::"))
                {

                    String name = receivedMsg.substring(5, receivedMsg.indexOf(","));
                    String areaName = receivedMsg.substring(receivedMsg.indexOf(",") + 1);

                    if (name.startsWith("Monster"))
                    {
                        Main.getWorldMap().getArea(areaName).removeMonster(Main.getWorldMap().getArea(areaName).searchMonster(name));

                    } else
                    {
                        Main.getWorldMap().getArea(areaName).removePlayer(Main.getWorldMap().getArea(areaName).searchPlayer(name));
                    }

                }


                if (receivedMsg.contains("::MAP::"))
                {

                    String name = receivedMsg.substring(0, receivedMsg.indexOf("::MAP::"));
                    String oldMap = receivedMsg.substring(receivedMsg.indexOf("::MAP::") + 7, receivedMsg.indexOf(","));
                    String newMap = receivedMsg.substring(receivedMsg.indexOf(",") + 1);

                    Player tempPlayer = Main.getWorldMap().getArea(oldMap).searchPlayer(name);

                    Main.getWorldMap().getArea(oldMap).removePlayer(tempPlayer);
                    if (Main.getWorldMap().getCurrentArea().getName().equals(oldMap))
                    {
                        Main.getConsole().appendLine(name + " has just left the area!");
                    }

                    Main.getWorldMap().getArea(newMap).addPlayer(tempPlayer);
                    if (Main.getWorldMap().getCurrentArea().getName().equals(newMap))
                    {
                        Main.getConsole().appendLine(name + " has just entered the area!");
                    }


                }


                if (receivedMsg.startsWith("NAME::"))
                {

                    Main.getHero().setName(receivedMsg.substring(6));

                    Main.getConsole().appendLine("Your new name is : '" + receivedMsg.substring(6) + "'.");

                }


                if (receivedMsg.startsWith("RENAME::"))
                {

                    String msg[] = receivedMsg.substring(8).split(",");

                    Main.getWorldMap().getCurrentArea().renamePlayer(msg[0], msg[1]);

                    Main.getConsole().appendLine("'" + msg[0] + "' is now known as '" + msg[1] + "'.");


                }

                if (receivedMsg.contains("::LOC::"))
                {
                    String msg[] = receivedMsg.substring(receivedMsg.indexOf("::LOC::") + 7).split(",");

                    String name = receivedMsg.substring(0, receivedMsg.indexOf("::LOC::"));

                    if (name.startsWith("Monster"))
                    {
                        Main.getWorldMap().getArea(msg[0]).updateMonster(name, msg[1], msg[2], msg[3], msg[4], msg[5]);

                    } else
                    {
                        Main.getWorldMap().getArea(msg[0]).updatePlayer(name, msg[1], msg[2], msg[3], msg[4], msg[5]);
                    }

                }

                if (receivedMsg.contains("::COLOUR::"))
                {

                    String name = receivedMsg.substring(0, receivedMsg.indexOf("::COLOUR::"));
                    String map = receivedMsg.substring(receivedMsg.indexOf("::COLOUR::") + 10, receivedMsg.indexOf(","));
                    String colour = receivedMsg.substring(receivedMsg.indexOf(",") + 1);

                    Main.getWorldMap().getArea(map).updatePlayer(name, colour);

                }

                if (receivedMsg.contains("::MSG::"))
                {

                    Main.getConsole().appendLine(receivedMsg.replaceAll("::MSG::", " : "));

                }

                if (receivedMsg.contains("HIT::"))
                {

                    String msg[] = receivedMsg.substring(receivedMsg.indexOf("HIT::") + 7).split(",");

                    String character1 = msg[0];
                    String character2 = msg[1];
                    String area = msg[2];

                    if (Main.getWorldMap().getArea(area).searchPlayer(character1) != null & Main.getWorldMap().getArea(area).searchPlayer(character2) != null)
                    {
                        Player p1 = Main.getWorldMap().getArea(area).searchPlayer(character1);
                        Player p2 = Main.getWorldMap().getArea(area).searchPlayer(character2);
                        p2.processHit(p1);
                    } else if (Main.getWorldMap().getArea(area).searchPlayer(character1) != null & Main.getWorldMap().getArea(area).searchMonster(character2) != null)
                    {
                        Player p1 = Main.getWorldMap().getArea(area).searchPlayer(character1);
                        Monster p2 = Main.getWorldMap().getArea(area).searchMonster(character2);
                        p2.processHit(p1);
                    } else if (Main.getWorldMap().getArea(area).searchMonster(character1) != null & Main.getWorldMap().getArea(area).searchPlayer(character2) != null)
                    {
                        Monster p1 = Main.getWorldMap().getArea(area).searchMonster(character1);
                        Player p2 = Main.getWorldMap().getArea(area).searchPlayer(character2);
                        p2.processHit(p1);
                    } else if (Main.getWorldMap().getArea(area).searchMonster(character1) != null & Main.getWorldMap().getArea(area).searchMonster(character2) != null)
                    {
                        Monster p1 = Main.getWorldMap().getArea(area).searchMonster(character1);
                        Monster p2 = Main.getWorldMap().getArea(area).searchMonster(character2);
                        p2.processHit(p1);
                    }
                }

                if (receivedMsg.startsWith("SD::"))
                {

                    Main.getConsole().appendLine("CLIENT :: Disconnecting from server, received shutdown cmd. (" + receivedMsg.substring(4) + ")");

                    disconnect();

                    break;
                }

            }

            clientConnected = false;

            clientOutput.close();
            clientInput.close();
            clientSocket.close();

        } catch (Exception ex)
        {
            ex.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread()
        {

            public void run()
            {
                send("DC::Quitting game.");
            }

        });


    }

}

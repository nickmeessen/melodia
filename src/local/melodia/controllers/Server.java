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

import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.UUID;

public class Server extends Thread
{

    private int serverPort;
    private boolean serverListening;
    private Population serverPopulation;

    public Server(int port)
    {

        serverPort = port;
        serverListening = false;

        serverPopulation = new Population();

        serverPopulation.loadMonsters();

        MonsterUpdater mu = new MonsterUpdater();

        mu.start();
    }

    public Population getPopulation()
    {
        return serverPopulation;
    }

    public boolean getListening()
    {
        return serverListening;
    }

    /**
     * Runs the server thread, listening for any connection requests and accepts them.
     */
    public void run()
    {

        try
        {

            ServerSocket socket = new ServerSocket(serverPort);

            serverListening = true;

            Main.getConsole().appendLine("SERVER :: Server listening on " + InetAddress.getLocalHost().getHostAddress() + ":" + serverPort);

            Main.getClient().connect(InetAddress.getLocalHost().getHostAddress(), serverPort);

            while (serverListening)
            {

                Client client = new Client(socket.accept());

                Main.getConsole().appendLine("SERVER :: New Client connected!");

                client.setName("Guest" + UUID.randomUUID());

                while (!serverPopulation.checkName(client.getName()))
                {
                    client.setName("Guest" + UUID.randomUUID());
                }

                client.start();

                serverPopulation.addClient(client);

            }

            Runtime.getRuntime().addShutdownHook(new Thread()
            {

                public void run()
                {
                    serverPopulation.broadcastMessage("SD::Server is shutting down..");
                }

            });

            Main.getConsole().appendLine("SERVER :: Closing socket..");

            socket.close();

            serverListening = false;

        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

}
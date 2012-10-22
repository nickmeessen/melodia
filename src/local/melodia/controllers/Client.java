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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 'Client' Class.
 *
 * This class represents a 'Client' that is connected to our server.
 */
public class Client extends Thread
{

    private boolean clientReady;
    private String receivedText;
    private Socket clientSocket;
    private PrintWriter clientOutput;
    private BufferedReader clientInput;

    public Client(Socket socket)
    {

        clientSocket = socket;

        clientReady = false;
        receivedText = "";

    }

    /**
     * Returns true/false based on if the client is ready or not.
     *
     * @return boolean
     */
    public boolean getReady()
    {
        return clientReady;
    }

    /**
     * Keeps listening for any data received from the server and process it.
     */
    @Override
    public void run()
    {

        try
        {

            clientOutput = new PrintWriter(clientSocket.getOutputStream(), true);
            clientInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            Main.getConsole().appendLine("SERVER :: Connection established with '" + clientSocket.getInetAddress() + "'.");

            send("Connection successful, welcome aboard! :)");
            send("NAME::" + getName());

            clientReady = true;

            while ((receivedText = clientInput.readLine()) != null)
            {

                if (receivedText.startsWith("REQNC::"))
                {

                    Main.getServer().getPopulation().renameClient(this, receivedText.substring(7));

                } else if (receivedText.contains("REQML::"))
                {

                    Main.getServer().getPopulation().sendMonsterList(this, receivedText.substring(7));

                } else if (receivedText.contains("HIT::"))
                {

                    Main.getServer().getPopulation().broadcastMessage(receivedText);

                } else if (receivedText.contains("MKILL::"))
                {
                    Main.getServer().getPopulation().killMonster(receivedText.substring(7, receivedText.indexOf(",")));

                } else
                {

                    Main.getServer().getPopulation().broadcastMessage(this, this.getName() + "::" + receivedText);

                }
            }


            Main.getServer().getPopulation().deleteClient(this);

            Main.getConsole().appendLine("SERVER :: " + this.getName() + " has disconnected.");


        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Properly disconnect the client when finalized.
     *
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable
    {

        super.finalize();

        try
        {

            Main.getServer().getPopulation().deleteClient(this);

            clientOutput.close();
            clientInput.close();
            clientSocket.close();

            finalize();

        } catch (IOException e)
        {

            e.printStackTrace();

        }

    }

    /**
     * Sends a packet to the connected client.
     *
     * @param msg (the packet to send)
     */
    public void send(String msg)
    {

        try
        {

            clientOutput.println(msg);

        } catch (Exception ex)
        {

            System.err.println("Failed to send a message to " + getName() + " ; '" + msg + "' via " + clientOutput + ".");

            ex.printStackTrace();
            Main.getConsole().appendLine("SERVER :: Failed to send message '" + msg + "' to " + getName() + ".");
        }
    }

}

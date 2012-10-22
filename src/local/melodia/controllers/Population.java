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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static java.lang.Thread.sleep;

/**
 * 'Population' Class
 *
 * This class represents a collection of Clients.
 */
public class Population
{

    private ArrayList<Client> clientList;
    private HashMap<String, ArrayList<Monster>> monsterList;

    public Population()
    {

        clientList = new ArrayList<Client>();
        monsterList = new HashMap<String, ArrayList<Monster>>();

    }

    /**
     * Check if given name exists or not.
     *
     * @param name (The name to check)
     * @return true if name is avaliable, false if not.
     */
    public boolean checkName(String name)
    {

        boolean result = true;

        for (Client client : clientList)
        {

            if (client.getName().equals(name))
            {
                result = false;
                break;
            }

        }

        return result;
    }

    /**
     * Adds a new client to the list, renames the client if necessary.
     * And broadcast to the other clients that a new client has joined the server.
     *
     * @param client (the new client).
     */
    public void addClient(Client client)
    {

        Main.getConsole().appendLine("SERVER :: Adding " + client.getName());

        while (!client.getReady())
        {

            try
            {
                sleep(100);
            } catch (InterruptedException ex)
            {
                ex.printStackTrace();
            }

        }

        // Broadcast to all the other clients that this new client has connected.
        broadcastMessage(client, "ADD::" + client.getName());
        broadcastMessage(client, "MSG::" + client.getName() + " has just connected!");

        // Send all the existing clients to this new client.
        for (Client player : clientList)
        {
            client.send("ADD::" + player.getName());
            client.send("MSG::" + player.getName() + " was already online.");
        }

        clientList.add(client);

        client.send("REQDATA");

    }

    /**
     * Removes a client and notifies all the other clients to remove the given client.
     *
     * @param client (the client to remove)
     */
    public void deleteClient(Client client)
    {
        broadcastMessage(client, "DEL::" + client.getName());
        broadcastMessage(client, client.getName() + " went offline.");

        clientList.remove(client);

    }

    /**
     * Renames a client.
     *
     * @param client  (the client to rename)
     * @param newName (the new name for the client)
     */
    public void renameClient(Client client, String newName)
    {

        if (checkName(newName))
        {

            broadcastMessage(client, "RENAME::" + client.getName() + "," + newName);
            client.setName(newName);
            client.send("NAME::" + newName);
            client.send("MSG::Nickname changed.");

        } else
        {

            client.send("MSG::Name already in use, please choose another.");

        }

    }

    /**
     * Returns a list of Clients.
     *
     * @return clientList
     */
    public ArrayList<Client> getClients()
    {
        return clientList;
    }

    /**
     * Broadcast a message to all the connected clients.
     *
     * @param msg (the message to be sent).
     */
    public void broadcastMessage(String msg)
    {

        for (Client c : clientList)
        {
            c.send(msg);
        }

    }

    /**
     * Broadcast a message to all the connected clients, except the given client.
     *
     * @param client (the client to be excluded)
     * @param msg    (the message to be sent)
     */
    public void broadcastMessage(Client client, String msg)
    {

        for (Client c : clientList)
        {

            if (c != client)
            {
                c.send(msg);
            }

        }

    }

    public void removeMonster(String monsterName)
    {

        Monster foundMonster = null;
        String foundArea = null;

        for (String area : monsterList.keySet())
        {

            for (Monster m : monsterList.get(area))
            {
                if (m.getName().equals(monsterName))
                {

                    foundMonster = m;
                    foundArea = area;

                    break;

                }
            }

        }

        if (foundMonster != null)
        {

            monsterList.get(foundArea).remove(foundMonster);

            broadcastMessage("DEL::" + foundMonster.getName() + "," + foundArea);

        }


    }

    public void killMonster(String monsterName)
    {

        for (String area : monsterList.keySet())
        {

            for (Monster m : monsterList.get(area))
            {
                if (m.getName().equals(monsterName))
                {
                    m.setDying(true);

                    break;

                }
            }

        }
    }

    public void sendMonsterList(Client c, String areaName)
    {

        for (Monster monster : monsterList.get(areaName))
        {

            c.send("ADD::" + monster.getName() + "," + areaName + "," + monster.getType() + "," + monster.getLocationX() + "," + monster.getLocationY());

        }

    }

    public void updateMonsters()
    {

        for (String areaName : monsterList.keySet())
        {

            for (Monster m : monsterList.get(areaName))
            {

                m.update();

                broadcastMessage(m.getName() + "::LOC::" + areaName + "," + m.getLocationX() + "," + m.getLocationY() + "," + m.getOrientation() + "," + m.getCurrentRow() + "," + m.getCurrentFrame());

            }

        }
    }

    public void loadMonsters()
    {

        try
        {

            File areaFiles = new File("db/areas/");

            for (File file : areaFiles.listFiles())
            {

                if (file.getName().substring(file.getName().lastIndexOf(".")).equals(".xml"))
                {

                    String mapName = file.getName().replace(".xml", "").replace("Area_", "");

                    Document areaDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);

                    NodeList nodeList = areaDocument.getElementsByTagName("monster");

                    ArrayList<Monster> monstersLijst = new ArrayList<Monster>();

                    for (int i = 0; i < nodeList.getLength(); i++)
                    {

                        Element element = (Element) nodeList.item(i);

                        monstersLijst.add(new Monster("Monster" + UUID.randomUUID().toString(), element.getAttribute("type"), Integer.parseInt(element.getAttribute("x")), Integer.parseInt(element.getAttribute("y")), mapName));

                    }

                    monsterList.put(mapName, monstersLijst);
                }

            }

        } catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    public String searchMonster(String monsterName)
    {

        String foundArea = null;

        for (String area : monsterList.keySet())
        {

            for (Monster m : monsterList.get(area))
            {
                if (m.getName().equals(monsterName))
                {
                    foundArea = area;

                    break;

                }
            }

        }

        return foundArea;

    }
}

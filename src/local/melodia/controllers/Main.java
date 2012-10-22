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

import local.melodia.objects.Player;
import local.melodia.util.Developer;
import local.melodia.views.ConsoleView;
import local.melodia.views.MainView;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * 'Main' Class
 *
 * This class holds all the other controllers for easy access from anywhere.
 * This class also handles any input given by the user.
 */
public class Main extends KeyAdapter implements ActionListener
{

    private MainView mainView;

    private static Player hero;
    private static WorldMap worldMap;
    private static Connector connector;
    private static Server server;
    private static ConsoleView mainConsole;
    private static Sequencer bgmPlayer;

    public Main()
    {

        hero = new Player("Blokka", Color.decode("#43810C"), 800, 705);

        hero.setHero(true);


        worldMap = new WorldMap();

        worldMap.getCurrentArea().addPlayer(hero);

        connector = new Connector();

        mainConsole = new ConsoleView();
        mainView = new MainView();

        mainView.getInputField().addActionListener(this);
        mainView.addKeyListener(this);

        try
        {

            bgmPlayer = MidiSystem.getSequencer();

            bgmPlayer.open();
            bgmPlayer.setSequence(MidiSystem.getSequence(new File("audio/bgm.mid")));
            bgmPlayer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            bgmPlayer.start();

        } catch (Exception ex)
        {

            ex.printStackTrace();

        }

    }

    public static Player getHero()
    {
        return hero;
    }

    public static WorldMap getWorldMap()
    {
        return worldMap;
    }

    public static Connector getClient()
    {
        return connector;
    }

    public static Server getServer()
    {
        return server;
    }

    public static ConsoleView getConsole()
    {
        return mainConsole;
    }

    public void keyPressed(KeyEvent ev)
    {

        switch (ev.getKeyCode())
        {

            // Key "Enter"
            case 10:
                mainView.getInputField().setFocusable(true);
                mainView.getInputField().grabFocus();
                mainView.getInputField().setText("");
                break;

            // Key "Up"
            case 38:
                hero.setOrientation(1);
                hero.setWalking(true);
                break;

            // Key "Down"
            case 40:
                hero.setOrientation(2);
                hero.setWalking(true);
                break;

            // Key "Left"
            case 37:
                hero.setOrientation(3);
                hero.setWalking(true);
                break;

            // Key "Right"
            case 39:
                hero.setOrientation(4);
                hero.setWalking(true);
                break;

            // Key "W"
            case 87:
                hero.setOrientation(1);
                hero.setWalking(true);
                break;

            // Key "S
            case 83:
                hero.setOrientation(2);
                hero.setWalking(true);
                break;

            // Key "A"
            case 65:
                hero.setOrientation(3);
                hero.setWalking(true);
                break;

            // Key "D"
            case 68:
                hero.setOrientation(4);
                hero.setWalking(true);
                break;

        }
    }

    public void keyReleased(KeyEvent ev)
    {

        switch (ev.getKeyCode())
        {

            // Key "F"
            case 70:
                hero.attack();
                break;

            // Key "Up"
            case 38:
                hero.setWalking(false);
                break;

            // Key "Down"
            case 40:
                hero.setWalking(false);
                break;

            // Key "Left"
            case 37:
                hero.setWalking(false);
                break;

            // Key "Right"
            case 39:
                hero.setWalking(false);
                break;


            // Key "W"
            case 87:
                hero.setWalking(false);
                break;

            // Key "S"
            case 83:
                hero.setWalking(false);
                break;

            // Key "A"
            case 65:
                hero.setWalking(false);
                break;

            // Key "D"
            case 68:
                hero.setWalking(false);
                break;


            // Key "Z"
            case 90:
                Developer.incPaintSize();
                break;

            // Key "X"
            case 88:
                Developer.decPaintSize();
                break;

            // Key "C"
            case 67:
                Developer.switchPaintMode();
                break;


        }

    }

    public void actionPerformed(ActionEvent ev)
    {

        try
        {

            if (!mainView.getInputField().getText().equals(""))
            {

                if (mainView.getInputField().getText().startsWith("/server"))
                {

                    String input = mainView.getInputField().getText().substring(8);

                    int port = Integer.parseInt(input);

                    server = new Server(port);

                    server.start();

                } else if (mainView.getInputField().getText().startsWith("/connect"))
                {

                    String input = mainView.getInputField().getText().substring(9);

                    String address = input.split(":")[0];
                    int port = Integer.parseInt(input.split(":")[1]);

                    connector.connect(address, port);

                } else if (mainView.getInputField().getText().equals("/disconnect"))
                {

                    connector.disconnect();

                } else if (mainView.getInputField().getText().equals("/loc"))
                {

                    Main.getConsole().appendLine("Current Location : (" + Main.getWorldMap().getCurrentArea().getName() + ") " + hero.getLocationX() + "," + hero.getLocationY());

                } else if (mainView.getInputField().getText().equals("/dev on"))
                {

                    Developer.setEnabled(true);

                } else if (mainView.getInputField().getText().equals("/dev off"))
                {

                    Developer.setEnabled(false);

                } else if (mainView.getInputField().getText().equals("/walkcheck"))
                {

                    Developer.switchWalkCheck();

                } else if (mainView.getInputField().getText().equals("/list"))
                {

                    Main.getConsole().appendLine("Connected Clients : ");

                    for (Client c : server.getPopulation().getClients())
                    {
                        Main.getConsole().appendLine(c.getName());
                    }

                    Main.getConsole().appendLine("Players on the map : ");

                    for (Player p : Main.getWorldMap().getCurrentArea().getPlayerList())
                    {
                        Main.getConsole().appendLine(p.getName() + " (" + p.getLocationX() + "," + p.getLocationY() + ")");
                    }

                } else if (mainView.getInputField().getText().equals("/createmap"))
                {
                    Developer.createMap(Main.getWorldMap().getCurrentArea());

                } else if (mainView.getInputField().getText().equals("/savemap"))
                {
                    Developer.saveMap(Main.getWorldMap().getCurrentArea());

                } else if (mainView.getInputField().getText().startsWith("/nick"))
                {

                    String newName = mainView.getInputField().getText().substring(6);

                    if (connector.getConnected())
                    {
                        connector.send("REQNC::" + newName.replaceAll("[^A-Za-z]", ""));
                    } else
                    {

                        hero.setName(newName.replaceAll("[^A-Za-z]", ""));

                        mainConsole.appendLine("You are now called '" + hero.getName() + "'.");

                    }

                } else if (mainView.getInputField().getText().equals("/color"))
                {

                    Color newColor = JColorChooser.showDialog(mainView, "Pick a colour for your hero.", Color.decode("#43810C"));

                    if (newColor != null)
                    {

                        Main.getHero().setColour(newColor);

                    } else
                    {

                        Main.getHero().setColour(Color.decode("#43810C"));

                    }


                } else if (mainView.getInputField().getText().startsWith("/color"))
                {

                    String input = mainView.getInputField().getText();

                    Main.getHero().setColour(Color.decode(input.substring(7)));

                } else if (mainView.getInputField().getText().startsWith("/bg off"))
                {

                    bgmPlayer.stop();

                } else if (mainView.getInputField().getText().equals("/bg on"))
                {

                    bgmPlayer.start();

                } else
                {

                    if (connector.getConnected())
                    {
                        connector.send("MSG::" + mainView.getInputField().getText());
                    }

                    Main.getConsole().appendLine(hero.getName() + " : " + mainView.getInputField().getText());

                }

            }

        } catch (Exception ex)
        {
            Main.getConsole().appendLine("Something went wrong with processing your command, please check your input.");
        }

        mainView.getInputField().setText("");
        mainView.getInputField().setFocusable(false);

        mainView.requestFocus();

    }

}

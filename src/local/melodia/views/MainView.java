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

import local.melodia.util.Repainter;

import javax.swing.*;
import java.awt.*;

/**
 * 'MainView' Class
 *
 * This class holds all the UI elements into the main JFrame.
 */
public class MainView extends JFrame
{

    private AreaView areaView;

    private JPanel inputPane;
    private JTextField inputField;

    private Repainter repainter;

    public MainView()
    {

        super("Melodia");

        areaView = new AreaView();

        areaView.setPreferredSize(new Dimension(500, 300));

        inputPane = new JPanel(new GridLayout(1, 1));
        inputField = new JTextField();

        inputField.setFocusable(false);
        inputField.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        inputPane.add(inputField);

        add(areaView, BorderLayout.NORTH);
        add(inputPane, BorderLayout.SOUTH);

        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pack();

        setLocationRelativeTo(null);

        repainter = new Repainter(areaView);

        repainter.start();

        setVisible(true);


    }

    /**
     * Getter for the input field.
     *
     * @return inputfield   (The inputput field).
     */
    public JTextField getInputField()
    {
        return inputField;
    }

}

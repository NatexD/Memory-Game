//
// Name:        Dao, Nathaniel
// Project:     3
// Due:         October 15, 2018
// Course:      CS-2450
// Description: 
//      Match Pairs is a memory game where you need to match pairs of tiles. 
//  Playing is very simple - you turn over one tile and then try to find a 
//  matching tile. Initially, all the tiles show the same game image. When you 
//  click on a tile of a turn, the game image will turn into the actual image of 
//  the tile. If you have exposed a matching tile in a previous turn, the pair 
//  remains revealed with their actual image. If the tiles are not matched, 
//  they both will be turned back to the game image after a few seconds.

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.Random;
import javax.swing.*;

class MemoryGame {
    
    JToggleButton[] bArray = new JToggleButton[12];
    JToggleButton firstChoice, secondChoice;
    Timer swTimer, delayTimer;
    DecimalFormat df = new DecimalFormat("00");
    int winTime = 0, numClick = 0, second = 0, minute = 0, hour = 0;
    JLabel timerDisplay = new JLabel("", SwingConstants.CENTER);
    JPanel p1 = new JPanel();
    String[] strImage = {"1.png", "2.png", "3.png", "4.png", "5.png", "6.png"};
    String[] matchImage = new String[6];
    ImageIcon defaultIcon = new ImageIcon (getClass().getResource("MemoryGame.png"));
    static boolean isDebug = false;
    JFrame mainFrame = new JFrame("Memory Game");
    
    MemoryGame() {
        
        mainFrame.getContentPane().setLayout(new BorderLayout());
        
        // Give the frame an initial size. 
        mainFrame.setSize(800, 650);

        // Set application to center when open
        mainFrame.setLocationRelativeTo(null);

        // Terminate the program when the user closes the application. 
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set icon
        mainFrame.setIconImage(new ImageIcon("MemoryGame.png").getImage());
        
        // set GridLayout 
        p1.setLayout(new GridLayout(3, 4));
        
        // Create main timer
        swTimer = new Timer(1000, new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e){
                if(winTime == 6)
                    swTimer.stop();
                else
                {
                   second++;
                   updateTime();
                }
            }
        });
        
        // Call create tile
        createTile();
        
        timerDisplay.setText(df.format(hour) + ":" + df.format(minute) + ":" + df.format(second));
        
        // Add Timer Display
        mainFrame.add(timerDisplay, BorderLayout.NORTH);
        
        // Add grid layout to frame
        mainFrame.add(p1, BorderLayout.CENTER);
        
        mainFrame.setVisible(true);
    }
    
    private void updateTime(){
        if(second == 60)
        {
            minute++;
            second = 0;
        }
        if(minute == 60)
        {
            hour++;
            minute = 0;
        }
        timerDisplay.setText(df.format(hour) + ":" + df.format(minute) + ":" + df.format(second));
    }
    
    private void createTile(){
        // Create JToggleButtons and set default, selected, and disable icon.
        // Add those JToggleButtons to an array
        int j = 0;
        for(int i = 0; i < 12; i++)
        {
            if(i == 6)
            {
                j = 0;
            }
            JToggleButton btn = new JToggleButton();
            btn.setSize(123,123);
            if(isDebug)
            {
                btn.setIcon(new ImageIcon (getClass().getResource(strImage[j])));
                btn.setDisabledIcon(new ImageIcon (getClass().getResource(strImage[j])));
                btn.setSelectedIcon(new ImageIcon (getClass().getResource(strImage[j])));
                btn.setDisabledSelectedIcon(new ImageIcon (getClass().getResource(strImage[j])));
            }
            else
            {
                btn.setIcon(new ImageIcon (getClass().getResource("MemoryGame.png")));
                btn.setDisabledIcon(new ImageIcon (getClass().getResource("MemoryGame.png")));
                btn.setSelectedIcon(new ImageIcon (getClass().getResource(strImage[j])));
                btn.setDisabledSelectedIcon(new ImageIcon (getClass().getResource(strImage[j])));
            }
            j++;
            bArray[i] = btn;
        }
        
        // Shuffle the JToggleButtons array
        Random gen = new Random();
        for (int i = 0; i < 12; i++) {
            int rand = gen.nextInt(12);
            JToggleButton temp = bArray[i];
            bArray[i] = bArray[rand];
            bArray[rand] = temp;
        }
        
        // Add JToggleButton to the grid
        for (int i = 0; i < bArray.length; i++)
        {
            JToggleButton btn = bArray[i];
            btn.addActionListener(new KeyPress());
            p1.add(bArray[i]);
        }
    }
    
    class KeyPress implements ActionListener 
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            // start main timer
            swTimer.setInitialDelay(0);
            swTimer.start();
            
            numClick++;
            if(numClick == 1)
            {
                // Save first JToggleButton
                firstChoice = (JToggleButton) e.getSource();
            }

            if(numClick == 2)
            {
                // Save second JToggleButton
                secondChoice = (JToggleButton) e.getSource();
                
                // Check if user clicks the same button twice
                if(firstChoice != secondChoice)
                {
                    // Compare Images from 2 buttons
                    Image temp =((ImageIcon) firstChoice.getSelectedIcon()).getImage();
                    Image temp2 =((ImageIcon) secondChoice.getSelectedIcon()).getImage();
                    if(temp == null ? temp2 != null : !temp.equals(temp2))
                    {
                        // If they are different, start delay timer
                        delayTimer = new Timer(1500, (ActionEvent ae) -> {
                            // After delay, find buttons in the array button and deselect them
                            // Enable all buttons and stop delay timer
                            for(int i = 0; i < bArray.length; i++)
                            {
                                if(firstChoice == bArray[i])
                                {
                                    bArray[i].setSelected(false);
                                    firstChoice = null;
                                }
                                if(secondChoice == bArray[i])
                                {
                                    bArray[i].setSelected(false);
                                    secondChoice = null;
                                }
                            }
                            numClick = 0;
                            buttonControl(false);
                            delayTimer.stop();
                        });
                        
                        // Disable all buttons and start delay timer
                        buttonControl(true);
                        delayTimer.start();
                    }
                    else
                    {
                        // If two buttons have the same image
                        // Set new disable icon button in the array button
                        // Disable two buttons and increase match pair count
                        for(int i = 0; i < bArray.length; i++)
                        {
                            if(firstChoice == bArray[i])
                            {
                                matchImage[winTime] = ((ImageIcon) firstChoice.getSelectedIcon()).getDescription();
                                bArray[i].setDisabledIcon(firstChoice.getSelectedIcon());
                                bArray[i].setEnabled(false);
                                firstChoice = null;
                            }
                            if(secondChoice == bArray[i])
                            {
                                bArray[i].setDisabledIcon(secondChoice.getSelectedIcon());
                                bArray[i].setEnabled(false);
                                secondChoice = null;
                            }
                        }
                        numClick = 0;
                        winTime++;
                    }
                }
                else
                {
                    // If the user clicks same button twice
                    firstChoice = null;
                    secondChoice = null;
                    numClick = 0;
                }
            }
        }
        
        public void buttonControl(boolean isDisable)
        {
            // Disable and enable all buttons
            for(int i = 0; i < bArray.length; i++)
            {
                if(isDisable)
                {
                    bArray[i].setEnabled(false);
                }
                else
                {
                    // Check if they have different disabled icon
                    if(isDebug)
                    {
                        ImageIcon temp = (ImageIcon) bArray[i].getDisabledIcon();
                        boolean found = false;
                        for(int j = 0; j < matchImage.length; j++)
                        {
                            if(!found)
                            {
                                if(temp.getDescription().equals(matchImage[j]))
                                {
                                    found = true;
                                    bArray[i].setEnabled(false);
                                }
                                else
                                {
                                    bArray[i].setEnabled(true);
                                }
                            }
                        }
                    }
                    else
                    {
                        ImageIcon temp = (ImageIcon) bArray[i].getDisabledIcon();
                        boolean isSame = false;
                        if(temp.getImage() == defaultIcon.getImage())
                            isSame = true;
                        if(isSame)
                        {
                            bArray[i].setEnabled(true);
                        }
                        else
                        {
                            isSame = false;
                        }
                    }
                }
            }
        }
    }

    public static void main(String args[]) {
        try{
            if(args.length != 0 && args[0].equals("debug"))
            {
                isDebug = true;
            }
        }
        catch(NumberFormatException ne)
        {
            
        }
        EventQueue.invokeLater(MemoryGame::new);
    }
}

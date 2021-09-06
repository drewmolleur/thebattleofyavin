package View;

import Controller.KeyEventListener;
import Controller.Main;
import Controller.MouseEventListener;

import javax.swing.*;
import java.awt.*;

import static Controller.Main.*;

public class MyWindow extends JFrame {

    public MyCanvas canvas;
    public static JButton startButton, helpButton, quitButton, repairButton, backUpButton, useTheForceButton;

    public void init() {

        // INITIALIZE GAME WINDOW
        canvas = new MyCanvas();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setTitle("STAR WARS: The Battle Of Yavin");
        setSize(1920, 880);
        setLocation(dim.width/2-this.getSize().width/2, 0);
        var cp = getContentPane();
        MouseEventListener listener = new MouseEventListener();
        KeyEventListener keyEventListener = new KeyEventListener();
        canvas.addMouseListener(listener);
        canvas.addMouseMotionListener(listener);
        canvas.addKeyListener(keyEventListener);
        canvas.setFocusable(true);
        cp.add(BorderLayout.CENTER, canvas);

        // INITIALIZE BUTTONS
        helpButton = new JButton("PREPARE FOR MISSION");
        helpButton.setPreferredSize(new Dimension(260, 50));
        helpButton.setEnabled(false);
        startButton = new JButton("START GAME");
        startButton.setPreferredSize(new Dimension(260, 50));
        startButton.setEnabled(false);
        quitButton = new JButton("QUIT GAME");
        quitButton.setPreferredSize(new Dimension(260, 50));
        quitButton.setEnabled(false);
        repairButton = new JButton("REPAIR X-WING");
        repairButton.setPreferredSize(new Dimension(260, 50));
        repairButton.setVisible(false);
        backUpButton = new JButton("REQUEST BACK-UP");
        backUpButton.setPreferredSize(new Dimension(260, 50));
        backUpButton.setVisible(false);
        useTheForceButton = new JButton("USE THE [BRUTE] FORCE");
        useTheForceButton.setPreferredSize(new Dimension(260, 50));
        useTheForceButton.setVisible(false);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setFocusable(false);
        buttonPanel.setBackground(Color.darkGray);
        buttonPanel.add(helpButton);
        buttonPanel.add(startButton);
        buttonPanel.add(quitButton);
        buttonPanel.add(repairButton);
        buttonPanel.add(backUpButton);
        buttonPanel.add(useTheForceButton);
        cp.add(BorderLayout.SOUTH, buttonPanel);

        // BUTTON FUNCTIONALITY
        startButton.addActionListener(e -> {
            System.out.println( System.lineSeparator() +
                    "Stage One: OFFENSE" +  System.lineSeparator() +
                    "Destroy all tie fighters in under 20 seconds to advance to the next stage." );
            if (!Main.running) {
                Main.running = true;
                onStart.stop();
                xWingNoise.start();
                startButtonNoise.start();
                themeSong.start();
                canvas.requestFocus();
                startButton.setVisible(true);
                startButton.setEnabled(false);
                helpButton.setVisible(true);
                helpButton.setEnabled(false);
                repairButton.setVisible(true);
                repairButton.setEnabled(false);
                backUpButton.setVisible(true);
                backUpButton.setEnabled(false);
                useTheForceButton.setVisible(true);
                useTheForceButton.setEnabled(false);
                canvas.backgroundState.goNext(canvas);
                new CountDown(19); // CHANGE BACKGROUND STATE
            }
        });
        helpButton.addActionListener(e -> {
            if (!Main.running) {
                System.out.println( System.lineSeparator() +
                                "Stage One: OFFENSE" +  System.lineSeparator() +
                                "Destroy the Tie Fighters" + System.lineSeparator() + System.lineSeparator() +
                                "Stage Two: DEFENSE" + System.lineSeparator() +
                                "Evade Darth Vader" + System.lineSeparator() + System.lineSeparator() +
                                "Stage Three: THE MISSION" + System.lineSeparator() +
                                "Destroy the Death Star");
                Main.r2.start();
                startButton.requestFocusInWindow();
                helpButton.setEnabled(false);
                startButton.setEnabled(true);
                canvas.backgroundState = new BackgroundState_StartMenu_Help();
            }
        });
        quitButton.addActionListener(e -> {
            if (!Main.running) {
                System.exit(0);
            } else System.exit(0);
        });
        repairButton.addActionListener(e -> {
            if (Main.running) {
                repairButton.setEnabled(false);
                Main.repair.start();
                canvas.backgroundState = new BackgroundState_2dGameplay_Repair();
                new CountDown(5);
                lightSaber=6;
            }
        });
        backUpButton.addActionListener(e -> {
            if (Main.running) {
                Main.backUp.start();
                canvas.backgroundState.goNext(canvas);
                new CountDown(16);
                new CountDown_DisableButton(18);
                backUpButton.setEnabled(false);
                repairButton.setEnabled(false);
            }
        });
        useTheForceButton.addActionListener(e -> {
            if (Main.running) {
                Main.theMission.start();
                System.out.println("YOU WIN! Remember: The Force Will Be With You... Always!");
                new CountDown(18);
                new CountDown_ShowButtons(18);
                CountDown_DisableButton.timer.cancel();
                CountDown_UseTheForce.timer.cancel();
                canvas.backgroundState.goNext(canvas);
                useTheForceButton.setEnabled(false);
                quitButton.setVisible(true);
                quitButton.setEnabled(true);
                startButton.setVisible(false);
                startButton.setEnabled(false);
                helpButton.setVisible(false);
                helpButton.setEnabled(false);
                repairButton.setVisible(false);
                repairButton.setEnabled(false);
                backUpButton.setVisible(false);
                backUpButton.setEnabled(false);
                useTheForceButton.setVisible(false);
                useTheForceButton.setEnabled(false);
            }
        });
    }
}
package Controller;

import Model.*;
import Model.Vader.Vader;
import Model.tieFighter.tieFighter;
import View.CountDown;
import View.MyWindow;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;

public class Main {

    public static MyWindow win;
    public static GameData gameData;
    public static PlayerInputEventQueue playerInputEventQueue;
    public static Clip themeSong, backUp, onStart, r2, theMission, offenseDone, tieFighterDone, xWingMissile, xWingNoise, vaderNoise, startButtonNoise, tieFighterBlaster, repair;
    public static boolean running = false;
    public static int INDEX_MOUSE_POINTER = 0;
    public static int INDEX_SHOOTER = 1;
    public static int FPS = 60;
    public static int lightSaber = 6;

    public static void main(String[] args) {

        // INITIALIZE AUDIO ELEMENTS
        try {
            onStart = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                    Main.class.getResourceAsStream("onStart.wav" ));
            onStart.open(inputStream);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        try {
            backUp = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                    Main.class.getResourceAsStream("backUp.wav" ));
            backUp.open(inputStream);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        try {
            themeSong = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                    Main.class.getResourceAsStream("themeSong.wav" ));
            themeSong.open(inputStream);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        try {
            r2 = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                    Main.class.getResourceAsStream("r2.wav" ));
            r2.open(inputStream);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        try {
            theMission = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                    Main.class.getResourceAsStream("theMission.wav" ));
            theMission.open(inputStream);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        try {
            offenseDone = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                    Main.class.getResourceAsStream("offenseDone.wav" ));
            offenseDone.open(inputStream);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        try {
            tieFighterDone = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                    Main.class.getResourceAsStream("tieFighterDone.wav" ));
            tieFighterDone.open(inputStream);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        try {
            xWingMissile = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                    Main.class.getResourceAsStream("xWingMissile.wav" ));
            xWingMissile.open(inputStream);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        try {
            xWingNoise = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                    Main.class.getResourceAsStream("xWingNoise.wav" ));
            xWingNoise.open(inputStream);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        try {
            vaderNoise = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                    Main.class.getResourceAsStream("vaderNoise.wav" ));
            vaderNoise.open(inputStream);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        try {
            startButtonNoise = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                    Main.class.getResourceAsStream("startButtonNoise.wav" ));
            startButtonNoise.open(inputStream);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        try {
            tieFighterBlaster = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                    Main.class.getResourceAsStream("tieFighterBlaster.wav" ));
            tieFighterBlaster.open(inputStream);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        try {
            repair = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                    Main.class.getResourceAsStream("repair.wav" ));
            repair.open(inputStream);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        // INITIALIZE GAME WINDOW
        win = new MyWindow();
        win.init();
        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        win.setVisible(true);
        win.setResizable(false);
        new CountDown(3);
        gameData = new GameData();
        playerInputEventQueue = new PlayerInputEventQueue();
        onStart.start();
        startScreen();
        initGame();
        gameLoop();
    }

    static void startScreen(){
        System.out.println("\n STAR WARS: The Battle Of Yavin \n Drew Molleur    |    Fall 2019");
        Font font = new Font("Courier New", Font.PLAIN, 40);
        gameData.friendObjects.add(new Text("OBJECT ORIENTED SOFTWARE DESIGN & CONSTRUCTION - DR.SUNG - FALL 2019", 150, 775, Color.WHITE, font));
        while (!running) {
            Main.win.canvas.render();
        }
    }

    public static void initGame() {
        gameData.clear();
        gameData.fixedObjects.add(new MousePointer(0, 0));
        int x = Main.win.getWidth() / 2;
        int y = Main.win.getHeight() - 100;
        gameData.fixedObjects.add(new Shooter(x, y));
        addTieFighterWithListener(100,100);
        addTieFighterWithListener(1200,200);
        addTieFighterWithListener(500,300);
        addTieFighterWithListener(900,400);
        addTieFighterWithListener(200,500);
        addTieFighterWithListener(300,600);
    }

    public static void addTieFighterWithListener(final float x, final float y) {
        var tieFighter = new tieFighter(x,y);
        gameData.enemyObjects.add(tieFighter);
    }

    public static void addVaderWithListener(final float x, final float y) {
        var vader = new Vader(x,y);
        gameData.enemyObjects.add(vader);
    }

    static void gameLoop() {
        running = true;
        while (running) {
            long startTime = System.currentTimeMillis();
            playerInputEventQueue.processInputEvents();
            processCollisions();
            gameData.update();
            win.canvas.render();
            long endTime = System.currentTimeMillis();
            long timeSpent = endTime - startTime;
            long sleepTime = (long) (1000.0 / FPS - timeSpent);
            try {
                if (sleepTime > 0) Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static void processCollisions() {
        var shooter = (Shooter) Main.gameData.fixedObjects.get(Main.INDEX_SHOOTER);
        for (var enemy : Main.gameData.enemyObjects) {
            if (shooter.collideWith(enemy)) {
                ++shooter.hitCount;
                --lightSaber;
                ++enemy.hitCount;
            }
        }
        for (var friend : Main.gameData.friendObjects) {
            for (var enemy : Main.gameData.enemyObjects) {
                if (friend.collideWith(enemy)) {
                    ++friend.hitCount;
                    ++enemy.hitCount;
                }
            }
        }
    }
}


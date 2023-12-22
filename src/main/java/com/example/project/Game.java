package com.example.project;

import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Game extends JPanel {
    public static final int WIDTH = 600;
    public static final int HEIGHT = 1200;
    private int state;
    private static final int START = 0;
    private static final int RUNNING = 1;
    private static final int PAUSE = 2;
    private static final int GAME_OVER = 3;

    private int score = 0;
    private Timer timer;
    private int intervel = 1000 / 100;
    public static BufferedImage background;
    public static BufferedImage start;
    public static BufferedImage airplane;
    public static BufferedImage bee;
    public static BufferedImage bullet;
    public static BufferedImage hero0;
    public static BufferedImage pause;
    public static BufferedImage gameover;

    private FlyingObject[] flyings = {};
    private Bullet[] bullets = {};
    Hero hero = new Hero();

    static {
        try {
            background = ImageIO.read(new File("/Users/thegreaterke/IdeaProjects/project/src/main/java/com/example/project/background.jpeg"));
            start = ImageIO.read(new File("/Users/thegreaterke/IdeaProjects/project/src/main/java/com/example/project/start (1).png"));
            airplane = ImageIO.read(new File("/Users/thegreaterke/IdeaProjects/project/src/main/java/com/example/project/airplane (1).png"));
            bee = ImageIO.read(new File("/Users/thegreaterke/IdeaProjects/project/src/main/java/com/example/project/bee (1).png"));
            bullet = ImageIO.read(new File("/Users/thegreaterke/IdeaProjects/project/src/main/java/com/example/project/bullet (1).png"));
            hero0 = ImageIO.read(new File("/Users/thegreaterke/IdeaProjects/project/src/main/java/com/example/project/fighter-plane (1).png"));
            pause = ImageIO.read(new File("/Users/thegreaterke/IdeaProjects/project/src/main/java/com/example/project/pause (1).png"));
            gameover = ImageIO.read(new File("/Users/thegreaterke/IdeaProjects/project/src/main/java/com/example/project/gameover (1).png"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(background, 0, 0, null);
        paintHero(g);
        paintBullets(g);
        paintFlyingObjects(g);
        paintScore(g);
        paintState(g);
    }

    public void paintHero(Graphics g) {
        g.drawImage(hero.getImage(), hero.getX(), hero.getY(), null);
    }

    public void paintBullets(Graphics g) {
        for (int i = 0; i < bullets.length; i++) {
            Bullet b = bullets[i];
            g.drawImage(b.getImage(), b.getX() - b.getWidth() / 2, b.getY(), null);
        }
    }
    public void paintFlyingObjects(Graphics g) {
        for (int i = 0; i < flyings.length; i++) {
            FlyingObject f = flyings[i];
            g.drawImage(f.getImage(), f.getX(), f.getY(), null);
        }
    }

    public void paintScore(Graphics g) {
        int x = 10;
        int y = 25;
        Font font = new Font(Font.SANS_SERIF, Font.BOLD, 22);
        g.setColor(new Color(0xFF0000));
        g.setFont(font);
        g.drawString("SCORE:" + score, x, y);
        y = y + 20;
        g.drawString("LIFE:" + hero.getLife(), x, y);
    }
    public void paintState(Graphics g) {
        int x = (getWidth() - start.getWidth()) / 2;
        int y = (getHeight() - start.getHeight()) / 2;

        switch (state) {
            case START:
                g.drawImage(start, x, y, null);
                break;
            case PAUSE:
                g.drawImage(pause, x, y, null);
                break;
            case GAME_OVER:
                g.drawImage(gameover, x, y, null);
                break;
        }
    }
    public static void main(String[] args) {
        JFrame frame = new JFrame("Pew Pew Pew");
        Game game = new Game();
        frame.add(game);
        frame.setSize(WIDTH, HEIGHT);
        frame.setAlwaysOnTop(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon("/Users/thegreaterke/IdeaProjects/project/src/main/java/com/example/project/icon.jpg").getImage());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        game.action();
    }
    public void action() {
        MouseAdapter listener = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (state == RUNNING) {
                    int x = e.getX();
                    int y = e.getY();
                    hero.moveTo(x, y);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (state == PAUSE) {
                    state = RUNNING;
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (state == RUNNING) {
                    state = PAUSE;
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                switch (state) {
                    case START:
                        state = RUNNING;
                        break;
                    case GAME_OVER:
                        flyings = new FlyingObject[0];
                        bullets = new Bullet[0];
                        hero = new Hero();
                        score = 0;
                        state = START;
                        break;
                }
            }
        };
        this.addMouseListener(listener);
        this.addMouseMotionListener(listener);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (state == RUNNING) {
                    enterAction();
                    stepAction();
                    shootAction();
                    bangAction();
                    outOfBoundsAction();
                    checkGameOverAction();
                }
                repaint();
            }
        }, intervel, intervel);
    }
    int flyEnteredIndex = 0;
    public void enterAction() {
        flyEnteredIndex++;
        if (flyEnteredIndex % 40 == 0) {
            FlyingObject obj = nextOne();
            flyings = Arrays.copyOf(flyings, flyings.length + 1);
            flyings[flyings.length - 1] = obj;
        }
    }
    public void stepAction() {
        for (int i = 0; i < flyings.length; i++) {
            FlyingObject f = flyings[i];
            f.step();
        }
        for (int i = 0; i < bullets.length; i++) {
            Bullet b = bullets[i];
            b.step();
        }
        hero.step();
    }

    int shootIndex = 0;
    public void shootAction() {
        shootIndex++;
        if (shootIndex % 30 == 0) {
            Bullet[] bs = hero.shoot();
            bullets = Arrays.copyOf(bullets, bullets.length + bs.length);
            System.arraycopy(bs, 0, bullets, bullets.length - bs.length, bs.length);
        }
    }
    public void bangAction() {
        for (int i = 0; i < bullets.length; i++) {
            Bullet b = bullets[i];
            bang(b);
        }
    }
    public void outOfBoundsAction() {
        int index = 0;
        FlyingObject[] flyingLives = new FlyingObject[flyings.length];
        for (int i = 0; i < flyings.length; i++) {
            FlyingObject f = flyings[i];
            if (!f.outOfBounds()) {
                flyingLives[index++] = f;
            }
        }
        flyings = Arrays.copyOf(flyingLives, index);
        index = 0;
        Bullet[] bulletLives = new Bullet[bullets.length];
        for (int i = 0; i < bullets.length; i++) {
            Bullet b = bullets[i];
            if (!b.outOfBounds()) {
                bulletLives[index++] = b;
            }
        }
        bullets = Arrays.copyOf(bulletLives, index);
    }
    public void checkGameOverAction() {
        if (isGameOver()) {
            state = GAME_OVER;
        }
    }
    public boolean isGameOver() {
        for (int i = 0; i < flyings.length; i++) {
            int index = -1;
            FlyingObject obj = flyings[i];
            if (hero.hit(obj)) {
                hero.subtractLife();
                hero.setDoubleFire(0);
                index = i;
            }
            if (index != -1) {
                FlyingObject t = flyings[index];
                flyings[index] = flyings[flyings.length - 1];
                flyings[flyings.length - 1] = t;
                flyings = Arrays.copyOf(flyings, flyings.length - 1);
            }
        }
        return hero.getLife() <= 0;
    }
    public void bang(Bullet bullet) {
        int index = -1;
        for (int i = 0; i < flyings.length; i++) {
            FlyingObject obj = flyings[i];
            if (obj.shootBy(bullet)) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            FlyingObject one = flyings[index];
            FlyingObject temp = flyings[index];
            flyings[index] = flyings[flyings.length - 1];
            flyings[flyings.length - 1] = temp;
            flyings = Arrays.copyOf(flyings, flyings.length - 1);
            if (one instanceof Enemy) {
                Enemy e = (Enemy) one;
                score += e.getScore();
            } else {
                Award a = (Award) one;
                int type = a.getType();
                switch (type) {
                    case Award.DOUBLE_FIRE:
                        hero.addDoubleFire();
                        break;
                    case Award.LIFE:
                        hero.addLife();
                        break;
                }
            }
        }
    }
    public static FlyingObject nextOne() {
        Random random = new Random();
        int type = random.nextInt(20);
        if (type < 4) {
            return new Bee();
        } else {
            return new Airplane();
        }
    }
}

package com.example.project;
import java.util.Random;

public class Bee extends FlyingObject implements Award {
    private int xSpeed = 1;
    private int ySpeed = 2;
    private int awardType;

    public Bee() {
        this.image = Game.bee;
        width = image.getWidth();
        height = image.getHeight();
        y =- height;
        Random rand = new Random();
        x = rand.nextInt(Game.WIDTH - width);
        awardType = rand.nextInt(2);
    }

    public int getType() {
        return awardType;
    }

    @Override
    public boolean outOfBounds() {
        return y > Game.HEIGHT;
    }
    @Override
    public void step() {
        x += xSpeed;
        y += ySpeed;

        if (x > Game.WIDTH - width) {
            xSpeed = -1;
        }

        if (x < 0) {
            xSpeed = 1;
        }
    }
}
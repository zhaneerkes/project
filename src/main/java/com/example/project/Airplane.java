package com.example.project;
import java.util.Random;
public class Airplane extends FlyingObject implements Enemy {
    private int speed = 3;
    public Airplane(){
        this.image = Game.airplane;
        width = image.getWidth();
        height = image.getHeight();
        y =- height;
        Random rand = new Random();
        x = rand.nextInt(Game.WIDTH - width);
    }
    @Override
    public int getScore() {
        return 5;
    }
    @Override
    public boolean outOfBounds() {
        return y>Game.HEIGHT;
    }
    public void step() {
        y += speed;
    }
}
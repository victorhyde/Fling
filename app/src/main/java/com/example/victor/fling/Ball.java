package com.example.victor.fling;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;

import java.util.List;


public class Ball{
    // ***DIRECTIONS***//
    // 0 = not moving
    // 1 = Right
    // 2 = down
    // 3 = left
    // 4 = up
    //THESE ARE STATIC SO THEY CAN BE USED IN THE CONSTRUCTOR
    public int ballWidth;// = mySurfaceView.ballWidth;
    public int spaceWidth;//= mySurfaceView.spaceWidth;
    public int margin;//= (spaceWidth - ballWidth)/2;

    private int maxSpeed= 4;
    private int c;
    private double speed;
//    private final Color RED = new Color(244,67,54);
//    private final Color BLUE = new Color(63,81,181);
//    private final Color GREEN = new Color(76,175,80);
//    private final Color PINK = new Color(233,30,99);
//    private final Color PURPLE = new Color(156,39,176);
//    private final Color LIGHT_BLUE = new Color(33,150,243);
//    private final Color TEAL = new Color(0,150,136);
//    private final Color ORANGE = new Color(255,87,34);

    public static final int OUT_LEFT =1;
    public static final int OUT_TOP =2;
    public static final int OUT_RIGHT =4;
    public static final int OUT_BOTTOM =8;

    public Rect rect;

//    private final Color [] colours = {RED, BLUE, GREEN, PINK, PURPLE,LIGHT_BLUE,TEAL,ORANGE};

    /**Constructs a ball on the grid with a random colour
     *
     * @param col The ball's column
     * @param row The ball's row
     */
    public Ball(int col, int row, int width, int xOffset, int yOffset, int color)
    {
        spaceWidth=width;
        ballWidth=spaceWidth*3/4;
        margin=(spaceWidth-ballWidth)/2;
        maxSpeed=spaceWidth/20;
        int x=(col * spaceWidth) + margin+ xOffset;
        int y =(row * spaceWidth) + margin+yOffset;
        rect = new Rect(x,y,x+ballWidth,y+ballWidth);
        //super(new Point((col * spaceWidth) + MySurfaceView.BOARD_X + margin,(row * spaceWidth) + MySurfaceView.BOARD_Y + margin), ballWidth);
//        int num = (int) (Math.random() * colours.length);
        c = color;
        speed =0;
    }

    /**Constructs a ball on the grid with a specific
     *
     * @param col The ball's column
     * @param row The ball's row
     * @param colour The ball's colour
     */
    public Ball(int col, int row, Color colour)
    {
        int x=(col * spaceWidth) + margin;
        int y =(row * spaceWidth) + margin;
        rect = new Rect(x,y,x+ballWidth,y+ballWidth);
//        super(new Point((col * spaceWidth) + MySurfaceView.BOARD_X + margin,(row * spaceWidth) + MySurfaceView.BOARD_Y + margin), ballWidth);
        speed =0;
    }

    /**Constructs a new ball indentical to another ball
     *
     * @param other The ball to copy
     */
    public Ball (Ball other)
    {
        this.spaceWidth=other.spaceWidth;
        this.ballWidth=other.ballWidth;
        this.margin=other.margin;
        rect = new Rect(other.rect);
//        super(new Point(other.x, other.y), other.width);
        this.c = other.c;
    }

    /**Randomizes the ball's colour
     *
     */
//    public void randomColour(){
//        int num = (int) (Math.random() * colours.length);
//        c = colours[num];
//    }

    /**Checks if the ball collided with another ball
     *
     */
    public boolean collides(Ball other)
    {
        return Rect.intersects(this.rect,other.rect);
    }

    public boolean intersects(Rect other)
    {
        return Rect.intersects(this.rect,other);
    }

    public boolean contains(Point point){
        Rect check = new Rect(rect.left-margin,rect.top-margin,rect.right+margin,rect.bottom+margin);
        return check.contains(point.x,point.y);
    }

    public int getX(){ return rect.left; }

    public int getY(){
        return rect.top;
    }

    public int outcode(double x, double y) {
        int out = 0;
        if (rect.width() <= 0) {
            out |= OUT_LEFT | OUT_RIGHT;
        } else if (x < rect.left) {
            out |= OUT_LEFT;
        } else if (x > rect.left + (double) rect.width()) {
            out |= OUT_RIGHT;
        }
        if (rect.height() <= 0) {
            out |= OUT_TOP | OUT_BOTTOM;
        } else if (y < rect.top) {
            out |= OUT_TOP;
        } else if (y > rect.top + (double) rect.height()) {
            out |= OUT_BOTTOM;
        }return out;
    }
    /**Checks if you can fling the ball
     *
     * @param allPieces The arraylist of all balls
     * @return True if you can fling the ball, false if you can't
     */
    public boolean couldFling(List<Ball> allPieces)
    {
        boolean ballLeft = false;
        boolean ballRight = false;
        boolean ballUp = false;
        boolean ballDown = false;

        for (Ball otherPiece : allPieces)
        {
            // Figures out where the other piece is relative to this piece
            int outCode = this.outcode(otherPiece.rect.left,otherPiece.rect.top);

            // Directly in line with this piece
            if (outCode == 8 || outCode == 4 || outCode == 2|| outCode ==1)
                return true;

            // boolean stuff
            if (outCode >= 8)
            {
                outCode -= 8;
                ballDown = true;
            }
            if (outCode >= 4)
            {
                outCode -= 4;
                ballRight = true;
            }
            if (outCode >= 2)
            {
                outCode -= 2;
                ballUp = true;
            }


            if (outCode >= 1)
            {
                outCode -= 1;
                ballLeft = true;
            }

            // Shows that there are other balls which help make this ball still possibly movable
            if ((ballLeft && ballRight) || (ballUp && ballDown))
                return true;
        }

        return false;

    }

    /**Changes the ball's position
     *
     * @param x The new x coordinate
     * @param y The new y coordinate
     */
    public void setPosition(int x, int y){
        rect.offsetTo(x,y);
    }


    /**Inreases (or decreases) the ball's speed
     *
     * @param acceleration The amount to change the speed by
     */
    public void accelerate(double acceleration){
//        if (speed<maxSpeed)
        speed += acceleration;
    }

    /**Sets the ball's speed to 0
     *
     */
    public void resetSpeed(){
        speed =0;
    }

    /**Sets a new speed for the ball
     *
     * @param speed The ball's new speed
     */
    public void setSpeed(double speed){
        this.speed =speed;
    }


//    public boolean isOnBoard()
//    {
//        Rectangle boardArea = new Rectangle(0, 0, FlingPanel.WINDOW_WIDTH,
//                FlingPanel.WINDOW_HEIGHT);
//        return rect.intersects(boardArea);
//    }

    /**Gets the ball's speed
     *
     * @return The ball's speed
     */
    public double getSpeed(){
        return speed;
    }

    /**Moves the ball an entire space in a certain direction
     *
     * @param direction The direction to fling the ball
     */
    public void move(int direction)
    {
        if (direction == OUT_RIGHT)
            rect.offset(spaceWidth,0);
        else if (direction == OUT_BOTTOM)
            rect.offset(0,spaceWidth);
        else if (direction == OUT_LEFT)
            rect.offset(-spaceWidth,0);
        else if (direction == OUT_TOP)
            rect.offset(0,-spaceWidth);
    }

    /**Moves the ball until it's completely off the grid
     *
     * @param direction The direction to fling the ball
     */
//    public void moveUntilOff(int direction)
//    {
//        while (isOnBoard())
//            move(direction);
//    }

    /**Gets the ball's colour
     *
     * @return The ball's colour
     */
    public int ballColor(){
        return c;
    }

    /**Changes the ball's opacity
     *
     * @param opacity An alpha value between 0 and 250
     */
    public void setOpacity(int opacity){
        c = Color.argb(opacity,Color.red(c),Color.green(c),Color.blue(c));
    }

    public void lighten(int amount){
        c=Color.rgb(Math.max(0,Color.red(c)+amount),Math.max(0,Color.green(c)+amount),Math.max(0,Color.blue(c)+amount));
    }

    /**Draws the  ball
     *
     */
//    public void draw(Graphics g)
//    {
//        if (isOnBoard()){
//            g.setColor(c);
//            g.fillOval(x, y, MySurfaceView.ballWidth, MySurfaceView.ballWidth);
//        }
//
//
//    }
}

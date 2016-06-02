package com.example.victor.fling;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by victor on 2016-05-11.
 */
public class Move {
    ArrayList<Ball> allBalls;
    public Ball movedBall;
    public int direction;

    /**
     * The Constructor for the move object. A deep copy was necessary in this
     * Constructor because in the solveBoard method, it iterates through all
     * Balls and an error would have occur (not logically though... but the
     * computer returns and error) if we did not do a deep copy here.
     * @param allBalls All of the Balls on the board before the move was made
     *            (basically a copy of the board layout)
     * @param ball The Ball that was flung
     * @param direction The direction the ball was flung in
     */
    Move(List<Ball> allBalls, Ball ball, int direction)
    {
        this.allBalls = new ArrayList<Ball>();
        for (Ball thisBall : allBalls)
        {
            // This is where the deep copy comes in
            // It is an arrayList with equivalent Balls (data), but not the same
            // instances of Balls
            this.allBalls.add(new Ball(thisBall));
        }
        movedBall = new Ball(ball);
        this.direction = direction;
    }
}

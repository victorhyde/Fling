package com.example.victor.fling;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MySurfaceView extends SurfaceView implements
        SurfaceHolder.Callback {
    // Timer variables

    static int MIN_DISTANCE;
    public final int GRID_SQUARES = 7;
    // Timer for button info pop up

    public int WINDOW_WIDTH;
    public int WINDOW_HEIGHT;
    public int WIDTH;
    public int BOARD_X;
    public int BOARD_Y;

    public Rect BOARD;
    public int SPACE_WIDTH;
    public int BALL_WIDTH;
    public int MARGIN;
    public double ACCELERATION;
    private Paint paint = new Paint();
    private float x1, x2, y1, y2;
    private LinkedList<Move> pastMoves;

    private String timeLabel = "time";
    private float timeLabelWidth;
    private String timeString = "0:00";
    private int time = 0;

    private List<Ball> allBalls = Collections.synchronizedList(new ArrayList<Ball>());
    private List<Integer> penalties = Collections.synchronizedList(new ArrayList<Integer>());
    private String penaltyText = "+10";
    private boolean addPenalty;

    private Ball movingBall;

    private boolean paused;
    private int highScore;
    private String highScoreLabel = "best";
    private float timeWidth;
    private String highScoreString = "";

    private int smallFont;
    private int bigFont;

    private int numBalls;
    private int DELAY_TIME = 2;


    private double SPEED_BEFORE_STOPPING;
    private int BALL_STOP_FRAMES;
    private int STOP_DISTANCE;

    private boolean canInteract;
    private boolean isDone;
    private boolean canDraw;

    private Thread thread;
    private Thread drawThread;

    public MySurfaceView(Context context) {
        super(context);
        initialize();
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private void initialize() {
        getHolder().addCallback(this);
        setFocusable(true);
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(1);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Cap.SQUARE);
        paint.setStyle(Style.FILL);
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Moon Light.otf");
        paint.setTypeface(font);
    }

    public void loadGame(int balls) {
        WINDOW_WIDTH = this.getWidth();
        WINDOW_HEIGHT = this.getHeight();
        WIDTH = Math.min(WINDOW_WIDTH, WINDOW_HEIGHT) * 5 / 6;
        BOARD_X = (WINDOW_WIDTH - WIDTH) / 2;
        BOARD_Y = (WINDOW_HEIGHT - WIDTH) / 2;
        SPACE_WIDTH = WIDTH / GRID_SQUARES;
        ACCELERATION = SPACE_WIDTH/1700.0;
        BALL_WIDTH = SPACE_WIDTH * 3 / 4;
        MARGIN = (SPACE_WIDTH - BALL_WIDTH) / 2;
        BOARD = new Rect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        STOP_DISTANCE = MARGIN * 5 / 2;
        SPEED_BEFORE_STOPPING = Math.sqrt(2 * ACCELERATION * STOP_DISTANCE);
        BALL_STOP_FRAMES = (int) (Math.floor(SPEED_BEFORE_STOPPING / ACCELERATION));
        MIN_DISTANCE = SPACE_WIDTH;
        isDone = false;
        paused = false;
        numBalls = balls;
        allBalls = new ArrayList<Ball>();
        allBalls.clear();
        addPenalty = false;
        canInteract = true;
        canDraw = true;

        smallFont = BOARD_Y/8;
        bigFont = BOARD_Y/6;
        Toast.makeText(getContext(), "SPACE WIDTH " + SPACE_WIDTH,
                Toast.LENGTH_LONG).show();

        movingBall = null;
        pastMoves = new LinkedList<Move>();
        createLevel(numBalls);
        FlingActivity activity = (FlingActivity) getContext();
        SharedPreferences sharedPref = activity.getSharedPreferences("high scores", Context.MODE_PRIVATE);
        if (numBalls == 6) {
            highScore = sharedPref.getInt("easy high score", -1);
        } else if (numBalls == 8) {
            highScore = sharedPref.getInt("medium high score", -1);
        } else if (numBalls == 10) {
            highScore = sharedPref.getInt("hard high score", -1);

        } else highScore = -1;
        if (highScore == -1) highScoreString = "";
        else
            highScoreString = String.format("%d:%02d", highScore / 60, highScore % 60);

        ((ImageButton) ((FlingActivity) getContext()).findViewById(R.id.pause)).setImageResource(R.drawable.pause);
        ((ImageButton) ((FlingActivity) getContext()).findViewById(R.id.undo)).setImageAlpha(66);
        timeString = "0:00";
        time = 0;
        paint.setTextSize(smallFont);
        timeLabelWidth = paint.measureText(timeLabel);
        paint.setTextSize(bigFont);
        timeWidth = paint.measureText(timeString);
    }

    public void createLevel(int numBalls) {
        boolean solvable = false;
        int x, y;
        int[] colours = {R.color.red, R.color.pink, R.color.purple, R.color.deep_purple, R.color.indigo, R.color.blue, R.color.teal, R.color.green, R.color.amber, R.color.orange};
        while (!solvable) {
            allBalls.clear();
            for (int ball = 0; ball < numBalls; ball++) {
                System.out.println("Ball " + ball);
                boolean overlaps;
                do {
                    overlaps = false;
                    x = (int) (Math.random() * GRID_SQUARES);
                    y = (int) (Math.random() * GRID_SQUARES);
                    for (Ball otherBall : allBalls) {
                        System.out.println("other ball at: " + otherBall.getX() + " " + otherBall.getY());
                        if (otherBall.contains(new Point(x * SPACE_WIDTH + MARGIN + BOARD_X, y * SPACE_WIDTH + MARGIN + BOARD_Y))) {
                            System.out.println("overlaps");
                            overlaps = true;
                        }
                    }
                } while (overlaps);
                allBalls.add(new Ball(x, y, SPACE_WIDTH, BOARD_X, BOARD_Y, ContextCompat.getColor(getContext(), colours[(int) (Math.random() * colours.length)])));
            }
            LinkedList<Move> solution = solveBoard();
            for (int move = 1; move <= solution.size(); move++) {
                undo();
            }
            if (!solution.isEmpty()) {
                solvable = true;
            }
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!canInteract)
            return true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();
                if (movingBall != null)
                    return true;
                for (Ball checkBall : allBalls) {
                    if (checkBall.contains(new Point((int) x1, (int) y1))) {
                        movingBall = checkBall;
                        movingBall.lighten(-40);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (movingBall == null) return true;
                movingBall.lighten(40);
                x2 = event.getX();
                y2 = event.getY();
                float deltaX = x2 - x1;
                float deltaY = y2 - y1;
                int direction;
                if (Math.abs(deltaX) > Math.abs(deltaY) * 2) {
                    if (deltaX > SPACE_WIDTH)
                        direction = Ball.OUT_RIGHT;
                    else if (deltaX < -SPACE_WIDTH)
                        direction = Ball.OUT_LEFT;
                    else {
                        movingBall = null;
                        return true;
                    }
                } else if (Math.abs(deltaY) > Math.abs(deltaX) * 2) {
                    if (deltaY > SPACE_WIDTH)
                        direction = Ball.OUT_BOTTOM;
                    else if (deltaY < -SPACE_WIDTH)
                        direction = Ball.OUT_TOP;
                    else {
                        movingBall = null;
                        return true;
                    }
                } else {
                    movingBall = null;
                    return true;
                }
                Point startingPosition = new Point(movingBall.getX(), movingBall.getY());
                // Show an animation if theres a ball directly adjacent to this one
                movingBall.move(direction);
                for (Ball otherBall : allBalls) {
                    if (movingBall.collides(otherBall) && movingBall != otherBall) {
                        movingBall.setPosition(startingPosition.x, startingPosition.y);
                        (movingBall).resetSpeed();
                        int otherDirection;
                        // Switches direction for the "backwards bump"
                        if (direction == Ball.OUT_BOTTOM)
                            otherDirection = Ball.OUT_TOP;
                        else if (direction == Ball.OUT_TOP)
                            otherDirection = Ball.OUT_BOTTOM;
                        else if (direction == Ball.OUT_LEFT)
                            otherDirection = Ball.OUT_RIGHT;
                        else
                            otherDirection = Ball.OUT_LEFT;

                        //Move this ball forward
                        for (int i = 0; i <= BALL_STOP_FRAMES + 1; i++) {
                            animate(movingBall, direction, ACCELERATION);
                            delay(DELAY_TIME * 2);
                        }
                        (movingBall).setSpeed(SPEED_BEFORE_STOPPING);
                        delay(DELAY_TIME * 5);
                        //Move it back
                        for (int i = 0; i <= BALL_STOP_FRAMES + 1; i++) {
                            animate(movingBall, otherDirection, -1
                                    * ACCELERATION);
                            delay(DELAY_TIME * 2);
                        }
                        movingBall.setPosition((int) Math
                                .round((movingBall.getX() - BOARD_X) / (SPACE_WIDTH * 1.0))
                                * SPACE_WIDTH
                                + BOARD_X
                                + MARGIN, (int) Math
                                .round((movingBall.getY() - BOARD_Y) / (SPACE_WIDTH * 1.0))
                                * SPACE_WIDTH
                                + BOARD_Y
                                + MARGIN);
                        movingBall = null;
                        return true;
                    }
                }
                movingBall.setPosition(startingPosition.x, startingPosition.y);
                // Fling the ball if possible
                if (canFling(movingBall, direction)) {
                    pastMoves.push(new Move(allBalls, movingBall, direction));
                    ((ImageButton) ((FlingActivity) getContext()).findViewById(R.id.undo)).setImageAlpha(255);
                    (movingBall).resetSpeed();
                    // Start a new thread so the timer doesn't stop, and so the player can still interact with the window
                    Fling nextFling = new Fling(movingBall, direction, false);
                    thread = new Thread(nextFling);
                    thread.start();
                } else {
                    // Fake transparent ball illustrating a bad move
                    Ball fakePath = new Ball(movingBall);
                    fakePath.setOpacity(100);
                    synchronized (allBalls) {
                        allBalls.add(fakePath);
                    }
                    Fling nextFling = new Fling(fakePath, direction, true);
                    thread = new Thread(nextFling);
                    thread.start();
                }
                movingBall = null;
                break;
        }
        return true;
    }

    public void onDraw(Canvas canvas) {
        canvas.drawColor(Color.rgb(230, 230, 235));
        paint.setColor(Color.rgb(0, 0, 0));

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(smallFont);
        canvas.drawText(timeLabel, BOARD_X+SPACE_WIDTH/2, BOARD_Y / 3, paint);
        paint.setTextSize(bigFont);
        canvas.drawText(timeString, BOARD_X+SPACE_WIDTH*3/4+ timeLabelWidth, BOARD_Y / 3, paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTextSize(smallFont);
        canvas.drawText(highScoreLabel, WINDOW_WIDTH-BOARD_X-SPACE_WIDTH*3/4- timeWidth, BOARD_Y / 3, paint);
        paint.setTextSize(bigFont);
        canvas.drawText(highScoreString, WINDOW_WIDTH-BOARD_X-SPACE_WIDTH/2, BOARD_Y / 3, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        synchronized (penalties) {
            for (Integer next: penalties) {
                paint.setColor(Color.argb(254-2*next,200,0,0));
                canvas.drawText(penaltyText, BOARD_X+SPACE_WIDTH+ timeLabelWidth+timeWidth,BOARD_Y/3-(int)(next/127.0*SPACE_WIDTH), paint);
            }
        }

        paint.setColor(Color.rgb(210, 210, 210));
        for (int row = 0; row < WIDTH - 5; row += SPACE_WIDTH) {
            for (int col = 0; col < WIDTH - 5; col += SPACE_WIDTH) {

                int x = BOARD_X + col + SPACE_WIDTH / 2;
                int y = BOARD_Y + row + SPACE_WIDTH / 2;
                canvas.drawRect(x - SPACE_WIDTH / 25, y - SPACE_WIDTH / 25, x + SPACE_WIDTH / 25, y + SPACE_WIDTH / 25, paint);
            }
        }
        if (!paused) {
            synchronized (allBalls) {
                for (Ball thisBall : allBalls) {
                    if (thisBall.intersects(BOARD)) {
                        paint.setColor(thisBall.ballColor());
                        canvas.drawCircle(thisBall.getX() + thisBall.ballWidth / 2, thisBall.getY() + thisBall.ballWidth / 2, thisBall.ballWidth / 2, paint);
                    }
                }
            }
        }
    }


    public boolean canFling(Ball thisBall, int direction) {
        // Directly in line with this piece

        // Should check if a Ball is right beside it. The oneSquareMoved
        // boolean aids in this check
        boolean oneSquareMoved = true;
        Point startingPosition = new Point(thisBall.getX(), thisBall.getY());
        Rect boardArea = new Rect(BOARD_X, BOARD_Y, BOARD_X + WIDTH, BOARD_Y + WIDTH);
        while (thisBall.intersects(boardArea)) {
            // Moves the Ball behind the scenes
            thisBall.move(direction);
            // Checks all of the other Balls on the board and sees if
            // thisBall now collides (a bump) with another Ball
            for (Ball otherBall : allBalls) {
                // If it does collide, and the ball has moved more than one
                // square, flinging in this direction is a valid move
                if (thisBall.collides(otherBall)
                        && thisBall != otherBall) {
                    thisBall.setPosition(startingPosition.x, startingPosition.y);
                    // Return false if it bumped into a Ball right beside it
                    return !oneSquareMoved;
                }
            }
            oneSquareMoved = false;
        }
        //Move it back to the original spot
        thisBall.setPosition(startingPosition.x, startingPosition.y);
        return false;
    }

    /**
     * This method is relevant for the solveBoard method. It flings the ball in
     * a given direction without showing an animation
     *
     * @param thisBall  The Ball that needs to be flung
     * @param direction The direction the Ball should be flung in
     */
    public void flingNoAnimate(Ball thisBall, int direction) {
        // Should check if a Ball is right beside it
            while (thisBall.intersects(BOARD)) {
                Point previousSpot = new Point(thisBall.getX(), thisBall.getY());
                thisBall.move(direction);
                for (Ball otherBall : allBalls) {
                    if (thisBall.collides(otherBall)
                            && thisBall != otherBall) {
                        // Move it back in true
                        // flingNoAnimate bumped Ball
                        thisBall.setPosition(previousSpot.x, previousSpot.y);
                        flingNoAnimate(otherBall, direction);

                        return;
                    }
                }
            }
            synchronized (allBalls) {
                allBalls.remove(thisBall);
            }

    }

    /**
     * Assume you've already checking if you can fling. Flings a ball in the
     * direction specified, calling the animation of the ball and also checking
     * for collision logic as it progresses on its path
     *
     * @param thisBall  The ball to be flung
     * @param direction The direction the ball is to be flung
     */
    public void fling(Ball thisBall, int direction) {
        while (thisBall.intersects(BOARD)) {
            // Animate the ball's movement
            animate(thisBall, direction, ACCELERATION);
            for (Ball otherBall : allBalls) {
                if (thisBall.collides(otherBall) && thisBall != otherBall) {
                    // If it bumps into something else
                    // Round the ball's position to keep it aligned with the grid
                    if (direction == Ball.OUT_BOTTOM) {
                        thisBall.setPosition(thisBall.getX(), (int) Math
                                .floor((thisBall.getY() - BOARD_Y) / (SPACE_WIDTH * 1.0))
                                * SPACE_WIDTH
                                + BOARD_Y + MARGIN + STOP_DISTANCE);
                    } else if (direction == Ball.OUT_TOP) {
                        thisBall.setPosition(thisBall.getX(), (int) Math
                                .ceil((thisBall.getY() - BOARD_Y) / (SPACE_WIDTH * 1.0))
                                * SPACE_WIDTH
                                + BOARD_Y + MARGIN - STOP_DISTANCE);
                    } else if (direction == Ball.OUT_LEFT) {
                        thisBall.setPosition((int) Math
                                .ceil((thisBall.getX() - BOARD_X) / (SPACE_WIDTH * 1.0))
                                * SPACE_WIDTH
                                + BOARD_X + MARGIN - STOP_DISTANCE, thisBall.getY());
                    } else if (direction == Ball.OUT_RIGHT) {
                        thisBall.setPosition((int) Math
                                .floor((thisBall.getX() - BOARD_X) / (SPACE_WIDTH * 1.0))
                                * SPACE_WIDTH
                                + BOARD_X + MARGIN + STOP_DISTANCE, thisBall.getY());
                    }
                    // Bump another ball
                    delay(DELAY_TIME * 10);
                    bump(thisBall, otherBall, direction);
                    return;
                }
            }

            // DRAW CALL/ANIMATION

            delay(DELAY_TIME);
        }
        // Removes the ball from allBalls if it is no longer on the board
        if (!thisBall.intersects(BOARD)) {
            synchronized (allBalls) {
                allBalls.remove(thisBall);
            }
        }

    }

    /**
     * This method pops the LinkedList of pastMoves, and resets the Balls on
     * the board to the Balls that were present in the time before this move was
     * made
     *
     * @author Jonathan
     */
    public void undo()
    // Undo may possibly be not adjusting the positions of the
    {
        if (!pastMoves.isEmpty() && canInteract && !paused) {
            synchronized (allBalls) {
                allBalls.clear();
            }
            allBalls = pastMoves.pop().allBalls;
        }
        if (pastMoves.isEmpty())
            ((ImageButton) ((FlingActivity) getContext()).findViewById(R.id.undo)).setImageAlpha(66);
    }


    /**
     * Animates the ball being flung by updating the Ball's position by a
     * different rate appearing that it is Accelerating
     *
     * @param thisBall     The ball to be flung/animated
     * @param direction    The direction it was flung in
     * @param acceleration the rate of acceleration of the ball
     * @author Victor
     */
    public void animate(Ball thisBall, int direction, double acceleration) {
        int speedInt = (int) Math.round(thisBall.getSpeed());
        if (direction == Ball.OUT_RIGHT) {
            thisBall.accelerate(acceleration);
            thisBall.setPosition(thisBall.getX() + speedInt, thisBall.getY());
        } else if (direction == Ball.OUT_BOTTOM) {
            thisBall.accelerate(acceleration);
            thisBall.setPosition(thisBall.getX(), thisBall.getY() + speedInt);
        } else if (direction == Ball.OUT_LEFT) {
            thisBall.accelerate(acceleration);
            thisBall.setPosition(thisBall.getX() - speedInt, thisBall.getY());
        } else if (direction == Ball.OUT_TOP) {
            thisBall.accelerate(acceleration);
            thisBall.setPosition(thisBall.getX(), thisBall.getY() - speedInt);
        }
    }

    /**
     * Pauses a thread
     *
     * @param ms The length of time to pause the Thread
     */
    public void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ie) {
            // Handle exception
        }
    }

    /**
     * This assumes you've already checked if you can fling, so this method
     * flings the given ball in the given direction.
     *
     * @param thisBall  The Ball object that will be flung
     * @param direction The Ball standard integer value of the direction
     *                  the ball should be flung
     * @param fake      A boolean value stating whether this is a fake ball (one
     *                  indicating an invalid move) or a real Ball
     */
    public void fling(Ball thisBall, int direction, boolean fake) {
        // If you fling diagonally, don't do anything
        if (direction != Ball.OUT_RIGHT
                && direction != Ball.OUT_LEFT
                && direction != Ball.OUT_TOP
                && direction != Ball.OUT_BOTTOM) {
            synchronized (allBalls) {
                allBalls.remove(thisBall);
            }
            return;
        }
        if (!fake) {
            fling(thisBall, direction);
            return;
        }

        while (thisBall.intersects(BOARD)) {
            animate(thisBall, direction, ACCELERATION);
            // Update the position based on the direction

            // DRAW CALL/ANIMATION

            delay(DELAY_TIME);
        }
        if (!thisBall.intersects(BOARD)) {
            synchronized (allBalls) {
                allBalls.remove(thisBall);
            }
        }
    }

    /**
     * Deals with collisions between a moving Ball and another Ball
     *
     * @param thisBall  The ball that was in motion before the collision
     * @param otherBall The Ball that thisBall collided with
     * @param direction The direction thisBall was traveling in before the
     *                  collision (direction otherBall will be flung)
     */
    public void bump(Ball thisBall, Ball otherBall, int direction) {
        thisBall.setSpeed(SPEED_BEFORE_STOPPING);
        otherBall.resetSpeed();
        int otherDirection;
        if (direction == Ball.OUT_BOTTOM)
            otherDirection = Ball.OUT_TOP;
        else if (direction == Ball.OUT_TOP)
            otherDirection = Ball.OUT_BOTTOM;
        else if (direction == Ball.OUT_LEFT)
            otherDirection = Ball.OUT_RIGHT;
        else
            otherDirection = Ball.OUT_LEFT;

        //Animate the ball bouncing back to its position
        for (int i = 0; i < BALL_STOP_FRAMES; i++) {
            animate(thisBall, otherDirection, -1 * ACCELERATION);
            animate(otherBall, direction, ACCELERATION);
            delay(DELAY_TIME * 2);
        }
        // Round the ball's position to the exact grid position
        thisBall.setPosition((int) Math.round((thisBall.getX() - BOARD_X) / (SPACE_WIDTH * 1.0))
                * SPACE_WIDTH
                + BOARD_X + MARGIN, (int) Math.round((thisBall.getY() - BOARD_Y) / (SPACE_WIDTH * 1.0))
                * SPACE_WIDTH
                + BOARD_Y + MARGIN);
        fling(otherBall, direction);
    }

    public void checkForWin() {
        if (allBalls.size() <= 1) {
            isDone = true;
            paused = true;
            final FlingActivity activity = (FlingActivity) getContext();
            SharedPreferences sharedPref = activity.getSharedPreferences("high scores",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            if (time < highScore || highScore == -1) {
                highScore=time;
                if (numBalls == 6)
                    editor.putInt("easy high score", time);
                else if (numBalls == 8)
                    editor.putInt("medium high score", time);
                else if (numBalls == 10)
                    editor.putInt("hard high score", time);
            }
            editor.apply();
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    activity.banana(time, highScore);
                }
            });

        }
    }

    public void showHint() {
        if (!canInteract || paused)
            return;
        canDraw = false;
        delay(20);
        LinkedList<Move> solution = solveBoard();
        if (solution.isEmpty()) {
            Toast.makeText(getContext(), "You're stuck! Press the undo button and try again!",
                    Toast.LENGTH_LONG).show();
            canDraw = true;
        } else {
            for (int move = 1; move <= solution.size(); move++) {
                undo();
            }
            Move firstMove = solution.removeFirst();
            pastMoves.push(new Move(allBalls, firstMove.movedBall,
                    firstMove.direction));
            ((ImageButton) ((FlingActivity) getContext()).findViewById(R.id.undo)).setImageAlpha(255);

            // Try to find the ball that was moved (need to do this
            // because references are different even though data is same
            // as that saved in move
            Ball movedBall = allBalls.get(0);
            Ball movesBall = firstMove.movedBall;
            for (Ball otherBall : allBalls) {
                if (otherBall.collides(movesBall))
                    movedBall = otherBall;
            }
            canDraw = true;
            Fling nextFling = new Fling(movedBall, firstMove.direction, false);
            ShowPenalty showPenalty = new ShowPenalty(penalties);
            Thread penaltyThread = new Thread(showPenalty);
            penaltyThread.start();
            thread = new Thread(nextFling);
            thread.start();
        }
    }

    public void stop() {
        paused = true;
        timeString = "Paused";
        ((ImageButton) ((FlingActivity) getContext()).findViewById(R.id.pause)).setImageResource(R.drawable.play);
        isDone = true;

    }

    public void resume() {
        isDone = false;
        drawThread = new DrawThread(getHolder(), this);
        drawThread.start();
    }

    public void pause() {
        paused = !paused;
        if (paused) {
            timeString = "Paused";
            ((ImageButton) ((FlingActivity) getContext()).findViewById(R.id.pause)).setImageResource(R.drawable.play);
        } else {
            ((ImageButton) ((FlingActivity) getContext()).findViewById(R.id.pause)).setImageResource(R.drawable.pause);
            timeString = String.format("%d:%02d", time / 60, time % 60);
        }
    }


    /**
     * Looks through the whole board looking for a solution to the board
     *
     * @return A LinkedList of the moves necessary to complete the board, or an
     * empty LinkedList if there is no solution possible
     * @author Jonathan
     */
    public LinkedList<Move> solveBoard() {

        // Re-wrote
        LinkedList<Move> possibleSolution = new LinkedList<Move>();
        if (allBalls.size() == 1) {
            // Adds a dummy move so that the solveBoard() methods higher in the
            // stack know that this was a valid solution
            possibleSolution
                    .add(new Move(allBalls, allBalls.get(0), 1));
            return possibleSolution;
        }
        for (int index = 0; index < allBalls.size(); index++) {
            for (int direction = 1; direction <= 8; direction *= 2) {
                Ball thisBall = allBalls.get(index);
                if (canFling(thisBall, direction)) {
                    // Get a copy of the board before move
                    Move startBoard = new Move(allBalls, thisBall,
                            direction);

                    // Flings ball in given direction
                    pastMoves.push(startBoard);
                    // Flings the ball without animating so that it can continue
                    // to solve the boarrd "behind the scenes"
                    flingNoAnimate(thisBall, direction);

                    if (isSolvable()) {
                        possibleSolution.addAll(solveBoard());

                        if (possibleSolution.size() != 0) {
                            possibleSolution.push(startBoard);
                            return possibleSolution;
                        }
                    }

                    // This is to catch the veryyyyyy bottom (one ball left on
                    // board)
                    if (allBalls.size() == 1) {
                        possibleSolution.push(startBoard);
                        return possibleSolution;
                    }

                    undo();
                }
            }
        }
        return possibleSolution;

    }

    /**
     * Uses the algorithm described in the AI notes to search for any balls that
     * might be permanently stuck (not sandwiched)
     *
     * @return true if the board is solvable (no balls stuck), false if it is
     * not (if even one ball is stuck)
     * @author Jonathan
     */
    public boolean isSolvable() {
        for (Ball thisBall : allBalls) {
                if (!thisBall.couldFling(allBalls))
                    return false;
        }
        return true;
    }

    /**
     * Checks if the board is finished or not
     *
     * @return true if the board is complete, false if it is not
     * @author Jonathan
     */
    public boolean isComplete() {
        return isDone;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
    }

    private class Fling implements Runnable {

        boolean fake;
        private Ball thisBall;
        private int direction;

        /**
         * Specifies the information that needs to be passed to the fling thread
         *
         * @param thisBall  The ball to fling
         * @param direction The direction to fling it
         * @param fake      True if it's a fake fling, false if it's not
         */
        public Fling(Ball thisBall, int direction, boolean fake) {
            this.thisBall = thisBall;
            this.direction = direction;
            this.fake = fake;
        }

        /**
         * Runs the new thread
         */
        public void run() {
            canInteract = false;
            fling(thisBall, direction, fake);
            canInteract = true;
            checkForWin();
        }

    }

    private class ShowPenalty implements Runnable {
        int position;
        public ShowPenalty(List<Integer> penalties) {
                penalties.add(0);
                position = penalties.size() - 1;
        }

        /**
         * Runs the new thread
         */
        public void run() {
            addPenalty = true;
            delay(500);
                for (int i = 0; i < 127; i++) {
                    penalties.set(position, i);
                    delay(2);
                }
            if (penalties.size()==position+1)
                penalties.clear();
        }

    }

    class DrawThread extends Thread {
        long startTime;
        long currentTime;
        MySurfaceView mySurfaceView;
        private SurfaceHolder surfaceHolder;

        public DrawThread(SurfaceHolder surfaceHolder,
                          MySurfaceView mySurfaceView) {
            startTime = System.currentTimeMillis();
            this.surfaceHolder = surfaceHolder;
            this.mySurfaceView = mySurfaceView;
        }


        @Override
        public void run() {
            Canvas canvas = null;
            while (!isDone) {
                if (!paused) {
                    if (addPenalty){
                        time+=10;
                        addPenalty = false;
                        timeString = String.format("%d:%02d", time / 60, time % 60);
                    }
                    currentTime = System.currentTimeMillis();
                    if (currentTime - startTime >= 1000) {
                        startTime = currentTime;
                        time++;
                        timeString = String.format("%d:%02d", time / 60, time % 60);
                    }
                }
                if (canDraw) {
                    try {
                        canvas = surfaceHolder.lockCanvas(null);
                        synchronized (surfaceHolder) {
                            mySurfaceView.onDraw(canvas);
                        }
                    } finally {
                        if (canvas != null) {
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    }
                }
            }
        }
    }

}
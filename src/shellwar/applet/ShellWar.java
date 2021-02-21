package shellwar.applet;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;

public class ShellWar extends Applet implements MouseListener, Runnable {
    // game objects:
    private Player player1;
    private Player player2;
    private Shell shell;
    private Thread timer;
    private boolean gameOver;

    // game constants:
    private static final int INITIAL_GUN_RADIUS = 50, INITIAL_BARREL_LENGTH = 75, SHELL_WIDTH = 5;
    private static final double MINIMUM_GUN_ELEVATION = degreesToRadians(10);

    private static final int GROUND_LEVEL = 50, CANVAS_WIDTH = 800, CANVAS_HEIGHT = 600;
    private static final int GRAVITY = 2, INITIAL_SHELL_VELOCITY = 25;
    private static final int LEFT_MESSAGE_X_POSITION = (int) (CANVAS_WIDTH * 0.2), RIGHT_MESSAGE_X_POSITION = (int) (CANVAS_WIDTH * 0.7);
    private static final int TOP_MESSAGE_Y_POSITION = (int) (CANVAS_HEIGHT * 0.3), BOTTOM_MESSAGE_Y_POSITION = (int) (CANVAS_HEIGHT * 0.5);

    private static final int MIN_HEALTH = 20;
    private static final Color PLAYER_1_COLOUR = Color.BLUE;
    private static final Color PLAYER_2_COLOUR = Color.YELLOW;

    public void paint(Graphics g) {
        // reset applet canvas
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, CANVAS_WIDTH - 1, CANVAS_HEIGHT - 1);

        if (shell.isActive()) {
            g.setColor(Color.WHITE);
            g.fillOval(shell.getPositionX(), shell.getPositionY(), SHELL_WIDTH, SHELL_WIDTH);
        }

        drawGuns(g);
        drawStatusBars(g);

        if (!gameOver && !shell.isActive()) {
            printGameStatusMessages(g);
        } else if (gameOver) {
            printFinalStatusMessages(g);
        }

        // printTestMessages(g);
    }

    public void drawGuns(Graphics g) {
        if (isPlayerHit(player1.getMyGun())) {
            g.setColor(Color.RED);
            drawGun(player1.getMyGun(), g);
            shell.setActive(false);
            player1.shrinkGun(INITIAL_BARREL_LENGTH, INITIAL_GUN_RADIUS);
        } else {
            g.setColor(PLAYER_1_COLOUR);
            drawGun(player1.getMyGun(), g);
            if (isPlayerHit(player2.getMyGun())) {
                g.setColor(Color.RED);
                drawGun(player2.getMyGun(), g);
                shell.setActive(false);
                player2.shrinkGun(INITIAL_BARREL_LENGTH, INITIAL_GUN_RADIUS);
            } else {
                g.setColor(PLAYER_2_COLOUR);
                drawGun(player2.getMyGun(), g);
            }
        }
        if ((player1.getHealth() <= MIN_HEALTH || player2.getHealth() <= MIN_HEALTH) && timer != null) {
            gameOver = true;
            stop();
            repaint();
        }
    }

    public void drawStatusBars(Graphics g) {
        // draw player1's health
        g.setColor(Color.GREEN);
        g.fillRect((int) (CANVAS_WIDTH * 0.05),
                (int) (CANVAS_HEIGHT - (GROUND_LEVEL * 0.75)),
                (int) ((CANVAS_WIDTH * 0.4 * player1.getHealth()) / player1.MAX_HEALTH),
                (int) (GROUND_LEVEL * 0.5));

        // draw player2's health
        g.fillRect((int) ((CANVAS_WIDTH * 0.55) + ((CANVAS_WIDTH * 0.4 * (player2.MAX_HEALTH - player2.getHealth())) / player2.MAX_HEALTH)),
                (int) (CANVAS_HEIGHT - (GROUND_LEVEL * 0.75)),
                (int) ((CANVAS_WIDTH * 0.4 * player2.getHealth()) / player2.MAX_HEALTH),
                (int) (GROUND_LEVEL * 0.5));

        // draw number of turns for active player
        g.setColor(Color.RED);

        if (player1.isActive()) {
            g.fillRect(0, (int) ((CANVAS_HEIGHT * 0.2) + ((3 - player1.getShotsThisTurn()) * CANVAS_HEIGHT * 0.2)),
                    (int) (CANVAS_WIDTH * 0.01), (int) (((player1.getShotsThisTurn()) * CANVAS_HEIGHT) * 0.2));

        } else {
            // offset player2 turn bar by 1 pixel as it's at edge of canvas
            g.fillRect(((int) (CANVAS_WIDTH * 0.99)) - 1, (int) ((CANVAS_HEIGHT * 0.2) + ((3 - player2.getShotsThisTurn()) * CANVAS_HEIGHT * 0.2)),
                    (int) (CANVAS_WIDTH * 0.01), (int) (((player2.getShotsThisTurn()) * CANVAS_HEIGHT) * 0.2));
        }
    }

    public void printGameStatusMessages(Graphics g) {
        g.setColor(Color.GRAY);

        if (player1.isActive()) {
            if (!player1.hasMoved()) {
                g.drawString("PLAYER 1 TO MOVE", LEFT_MESSAGE_X_POSITION, TOP_MESSAGE_Y_POSITION);
            } else {
                g.drawString("PLAYER 1 TO SHOOT", LEFT_MESSAGE_X_POSITION, TOP_MESSAGE_Y_POSITION);
            }
        } else {
            if (!player2.hasMoved()) {
                g.drawString("PLAYER 2 TO MOVE", RIGHT_MESSAGE_X_POSITION, TOP_MESSAGE_Y_POSITION);
            } else {
                g.drawString("PLAYER 2 TO SHOOT", RIGHT_MESSAGE_X_POSITION, TOP_MESSAGE_Y_POSITION);
            }
        }
    }

    public void printFinalStatusMessages(Graphics g) {
        g.setColor(Color.GRAY);
        g.drawString("GAME OVER", LEFT_MESSAGE_X_POSITION, TOP_MESSAGE_Y_POSITION);

        if (player2.getHealth() < player1.getHealth())
            g.drawString("PLAYER 1 WINS", LEFT_MESSAGE_X_POSITION, BOTTOM_MESSAGE_Y_POSITION);
        else
            g.drawString("PLAYER 2 WINS", LEFT_MESSAGE_X_POSITION, BOTTOM_MESSAGE_Y_POSITION);
    }

    public void printTestMessages(Graphics g) {

        // test: print number of turns per player
        g.setColor(Color.GRAY);
        g.drawString("Player 1 turn total: " + player1.getShotsPerTurn() + " Player 1 hasMoved: " + player1.hasMoved() +
                        " Player 1 isActive: " + player1.isActive() + " Player 1 health: " + player1.getHealth(),
                (int) (CANVAS_WIDTH * 0.4), (int) (CANVAS_HEIGHT * 0.4));
        g.drawString("Player 2 turn total: " + player2.getShotsPerTurn() + " Player 2 hasMoved: " + player2.hasMoved() +
                        " Player 2 isActive: " + player2.isActive() + " Player 2 health: " + player2.getHealth(),
                (int) (CANVAS_WIDTH * 0.4), (int) (CANVAS_HEIGHT * 0.5));
        g.drawString("Shell isActive: " + shell.isActive() + " Shell x: " + shell.getPositionX() +
                " Shell y: " + shell.getPositionY() + " Shell xVel: " + shell.getVelocityX() +
                " Shell yVel: " + shell.getVelocityY(), (int) (CANVAS_WIDTH * 0.4), (int) (CANVAS_HEIGHT * 0.6));
        g.drawString("Thread status: " + timer.toString(),
                (int) (CANVAS_WIDTH * 0.4), (int) (CANVAS_HEIGHT * 0.65));
    }


    public void start() {
        timer = new Thread(this);
        timer.start();
    }

    public void run() {
        while (timer != null && !gameOver) {
            try {
                // 10 frames per second
                Thread.sleep(100);

            } catch (InterruptedException e) {

            }

            if (shell.isActive()) {
                int shellX = shell.getPositionX(), shellY = shell.getPositionY();
                int velocityX = shell.getVelocityX(), velocityY = shell.getVelocityY();

                // check that shell hasn't gone off screen or below ground:
                if ((shellY > (CANVAS_HEIGHT - GROUND_LEVEL)) || // below ground?
                        ((shellX > CANVAS_WIDTH) && (velocityX > 0)) || // to the right?
                        ((shellX < 0) && (velocityX < 0))) // to the left?
                    shell.setActive(false);
                else {
                    shell.setPositionX(shellX + velocityX);
                    // need to subtract velocity as shell travels up screen but to lower y values on canvas:
                    shell.setPositionY(shellY - velocityY);
                    shell.setVelocityY(velocityY - GRAVITY);
                }
            }

            repaint();
        }
    }

    public void stop() {
        timer = null;
    }

    public void init() {
        addMouseListener(this);

        // initialise game objects
        player1 = new WeakPlayer(3,true, false,CANVAS_WIDTH / 4,
                CANVAS_HEIGHT - GROUND_LEVEL, INITIAL_GUN_RADIUS, INITIAL_BARREL_LENGTH,
                degreesToRadians(45));
        player2 = new StrongPlayer(4,false, false, (CANVAS_WIDTH * 3) / 4,
                CANVAS_HEIGHT - GROUND_LEVEL, INITIAL_GUN_RADIUS, INITIAL_BARREL_LENGTH,
                degreesToRadians(135));
        shell = new Shell(-50, -50, 0, 0, false);

        gameOver = false;

        repaint();
    }

    public void mouseClicked(MouseEvent e) {
        if (!shell.isActive()) {
            if (player1.isActive())
                // left-hand player
                takeTurn(player1, true, shell, e);
            else
                // right-hand player
                takeTurn(player2, false, shell, e);
        }

        repaint();
    }

    public void takeTurn(Player currentPlayer, boolean isLeftPlayer, Shell currentShell, MouseEvent e) {
        if (!currentPlayer.hasMoved()) {
            movePlayer(currentPlayer, isLeftPlayer, e);
            currentPlayer.setHasMoved(true);
        } else {
            playerShoots(currentPlayer, isLeftPlayer, currentShell, e);

            currentPlayer.setShotsThisTurn(currentPlayer.getShotsThisTurn() - 1);

            if (currentPlayer.getShotsThisTurn() == 0) {
                if (isLeftPlayer) {
                    player1.setActive(false);
                    player2.setActive(true);
                    player2.setShotsThisTurn(player2.getShotsPerTurn());
                    player2.setHasMoved(false);
                } else {
                    player2.setActive(false);
                    player1.setActive(true);
                    player1.setShotsThisTurn(player1.getShotsPerTurn());
                    player1.setHasMoved(false);
                }
            }
        }
    }

    public void movePlayer(Player currentPlayer, boolean isLeftPlayer, MouseEvent e) {
        // if player1 on left:
        if (isLeftPlayer) {
            if (e.getX() <= ((CANVAS_WIDTH * 0.05) + currentPlayer.getMyGun().getRadius()))
                currentPlayer.getMyGun().setPositionX((int) (CANVAS_WIDTH * 0.05) + currentPlayer.getMyGun().getRadius());
            else if (e.getX() >= ((CANVAS_WIDTH * 0.45) - currentPlayer.getMyGun().getRadius()))
                currentPlayer.getMyGun().setPositionX((int) (CANVAS_WIDTH * 0.45) - currentPlayer.getMyGun().getRadius());
            else
                currentPlayer.getMyGun().setPositionX(e.getX());
        }
        // else if player1 on right:
        else {
            if (e.getX() <= ((CANVAS_WIDTH * 0.55) + currentPlayer.getMyGun().getRadius()))
                currentPlayer.getMyGun().setPositionX((int) (CANVAS_WIDTH * 0.55) + currentPlayer.getMyGun().getRadius());
            else if (e.getX() >= ((CANVAS_WIDTH * 0.95) - currentPlayer.getMyGun().getRadius()))
                currentPlayer.getMyGun().setPositionX((int) (CANVAS_WIDTH * 0.95) - currentPlayer.getMyGun().getRadius());
            else
                currentPlayer.getMyGun().setPositionX(e.getX());
        }
    }

    public void playerShoots(Player currentPlayer, boolean isLeftPlayer, Shell currentShell, MouseEvent e) {
        // all three variables are doubles to preserve precision in atan calculation
        double xDistance, yDistance, newAngle;

        yDistance = currentPlayer.getMyGun().getPositionY() - e.getY();

        if (yDistance < 0)
            // do nothing if player is shooting below the ground
            return;

        // if left player is shooting:
        if (isLeftPlayer) {
            xDistance = e.getX() - currentPlayer.getMyGun().getPositionX();
            if (xDistance <= 0)
                // guard against division by zero and gun shooting left
                newAngle = degreesToRadians(90);
            else
                newAngle = Math.atan(yDistance / xDistance);

            // is gun pointing too far to the right?
            newAngle = (newAngle < MINIMUM_GUN_ELEVATION) ? MINIMUM_GUN_ELEVATION : newAngle;
        }

        // otherwise, if right player is shooting
        else {
            xDistance = currentPlayer.getMyGun().getPositionX() - e.getX();

            // needed for tan calculation:
            double trueXDistance = e.getX() - currentPlayer.getMyGun().getPositionX();
            if (xDistance <= 0)
                // guard against division by zero and gun shooting right
                newAngle = degreesToRadians(90);
            else
                newAngle = Math.atan(yDistance / trueXDistance) + degreesToRadians(180);

            // is gun pointing too far to the left?
            newAngle = (newAngle > (degreesToRadians(180) - MINIMUM_GUN_ELEVATION)) ?
                    degreesToRadians(180) - MINIMUM_GUN_ELEVATION : newAngle;
        }

        currentPlayer.getMyGun().setBarrelAngle(newAngle);

        // activate shell and give it initial velocity
        currentShell.setActive(true);
        currentShell.setVelocityX((int) (INITIAL_SHELL_VELOCITY * Math.cos(newAngle)));
        currentShell.setVelocityY((int) (INITIAL_SHELL_VELOCITY * Math.sin(newAngle)));

        // set x position of shell
        currentShell.setPositionX((int) (currentPlayer.getMyGun().getPositionX()
                + (Math.cos(newAngle) * currentPlayer.getMyGun().getBarrelLength())));
        // distance of barrel from gun is to be subtracted from gun's centre, because of canvas coordinate system
        currentShell.setPositionY((int) (currentPlayer.getMyGun().getPositionY()
                - (Math.sin(newAngle) * currentPlayer.getMyGun().getBarrelLength())));
    }

    public void mouseExited(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mousePressed(MouseEvent me) {

    }

    public void mouseReleased(MouseEvent me) {

    }

    private static double degreesToRadians(int degrees) {
        return (Math.PI * (degrees / 180.0));
    }

    private void drawGun(Gun currentGun, Graphics G) {
        // draw body of gun
        G.fillArc(currentGun.getPositionX() - currentGun.getRadius(),
                currentGun.getPositionY() - currentGun.getRadius(),
                currentGun.getRadius() * 2,
                currentGun.getRadius() * 2,
                0,
                180);

        // draw barrel of gun - addition for cos as it's to the right when angle = 0,
        // subtraction for sin as it's above
        G.drawLine(currentGun.getPositionX(),
                currentGun.getPositionY(),
                currentGun.getPositionX() + ((int) (Math.cos(currentGun.getBarrelAngle()) * currentGun.getBarrelLength())),
                currentGun.getPositionY() - ((int) (Math.sin(currentGun.getBarrelAngle()) * currentGun.getBarrelLength())));

    }

    private boolean isPlayerHit(Gun currentGun) {
        return ((shell.getPositionX() >= (currentGun.getPositionX() - currentGun.getRadius()))
                && (shell.getPositionX() <= (currentGun.getPositionX() + currentGun.getRadius()))
                && (shell.getPositionY() >= (currentGun.getPositionY() - currentGun.getRadius()))
                && (shell.getPositionY() <= currentGun.getPositionY())
                && shell.isActive());
    }
}
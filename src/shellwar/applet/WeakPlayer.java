package shellwar.applet;

public class WeakPlayer implements Player {
    private int health;
    private static final int HEALTH_DECREMENT = 15;
    private int shotsThisTurn;
    private static final int SHOTS_PER_TURN = 3;
    private Gun myGun;
    private boolean isActive;
    private boolean hasMoved;

    public WeakPlayer(int initialShotsThisTurn, boolean isActive, boolean hasMoved, int initialX, int initialY, int initialRadius,
                      int initialLength, double initialAngle) {

        this.health = MAX_HEALTH;
        this.shotsThisTurn = initialShotsThisTurn;
        this.isActive = isActive;
        this.hasMoved = hasMoved;

        myGun = new Gun(initialX, initialY, initialRadius, initialLength, initialAngle);
    }

    public int getShotsPerTurn() { return SHOTS_PER_TURN; }

    public Gun getMyGun() {
        return myGun;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public int getShotsThisTurn() {
        return shotsThisTurn;
    }

    public void setShotsThisTurn(int turns) {
        this.shotsThisTurn = turns;
    }

    public void shrinkGun(int initialBarrelLength, int initialRadius) {
        // reduce health by 20% - five lives in the game per player
        health -= HEALTH_DECREMENT;
        // shrink gun's radius and barrel because of health reduction
        myGun.setRadius((initialRadius*health)/MAX_HEALTH);
        myGun.setBarrelLength((initialBarrelLength*health)/MAX_HEALTH);
    }
}
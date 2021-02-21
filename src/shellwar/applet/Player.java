package shellwar.applet;

public interface Player {
    int MAX_HEALTH = 100;

    int getShotsPerTurn();

    Gun getMyGun();

    int getHealth();

    void setHealth(int health);

    boolean isActive();

    void setActive(boolean active);

    boolean hasMoved();

    void setHasMoved(boolean hasMoved);

    int getShotsThisTurn();

    void setShotsThisTurn(int turns);

    void shrinkGun(int initialBarrelLength, int initialRadius);
}
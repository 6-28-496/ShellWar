package shellwar.applet;

public class Gun {
    private int positionX;
    private int positionY;
    private int radius;
    private int barrelLength;
    private double barrelAngle;

    public Gun(int positionX, int positionY, int radius, int barrelLength, double barrelAngle) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.radius = radius;
        this.barrelLength = barrelLength;
        this.barrelAngle = barrelAngle;
    }

    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public double getBarrelAngle() {
        return barrelAngle;
    }

    public void setBarrelAngle(double barrelAngle) {
        this.barrelAngle = barrelAngle;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getBarrelLength() {
        return barrelLength;
    }

    public void setBarrelLength(int barrelLength) {
        this.barrelLength = barrelLength;
    }
}
package es.hugoalvarezajenjo.jellyfish;

public class Coordinate {
    private final int x, y;
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public int getX() { return this.x; }
    public int getY() { return this.y; }

    public Coordinate add (Coordinate displacement) {
        return this.add(displacement.getX(), displacement.getY());
    }

    public Coordinate add(int x, int y) {
        return new Coordinate(this.x + x, this.y + y);
    }

    public Coordinate add(Coordinate coordinate, int angle) {
        return this.add(coordinate.getX(), coordinate.getY(), angle);
    }

    public Coordinate add(int x, int y, int angle) {
        double radians = Math.toRadians(angle);
        int rotatedX = (int) Math.round(x * Math.cos(radians) - y * Math.sin(radians));
        int rotatedY = (int) Math.round(x * Math.sin(radians) + y * Math.cos(radians));
        return this.add(rotatedX, rotatedY);
    }
}

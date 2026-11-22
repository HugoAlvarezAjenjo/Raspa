package es.hugoalvarezajenjo.jellyfish;

public record Coordinate(int x, int y) {

    public Coordinate add(final Coordinate displacement) {
        return new Coordinate(x + displacement.x, y + displacement.y);
    }
}

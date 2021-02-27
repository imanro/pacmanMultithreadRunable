package packman.model;

public class Kolobok extends Actor {
    public Kolobok(Board board) {
        super(board);
    }

    @Override
    public Character getSymbol() {
        return '0';
    }
}

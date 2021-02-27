package packman.model;

public class Monster extends Actor {

    public Monster(Board board) {
        super(board);
    }

    @Override
    public Character getSymbol() {
        return 'M';
    }
}

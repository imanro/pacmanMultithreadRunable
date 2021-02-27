package packman.gfx;

import packman.model.Board;
import packman.model.VisibleActor;

public class Renderer {

    public void render(Board board) {

//        refreshScreen();
        // break rows

        // FIXME: sqrt of board size
        Integer rowLength = board.getSideSize();

        printLine(rowLength * 2);

        // rows
        for(int i = 0; i < rowLength; i++) {

            printRowStart();
            // columns
            for(int j = 0; j < rowLength; j++) {
                printCell(board.getCellHabitant(((rowLength * i) + j)));
            }

            printRowEnd();
        }
    }

    private void refreshScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void printLine(Integer length) {
        for(int i = 0; i < length; i++) {
            System.out.print("_");
        }

        System.out.print("\n");
    }

    private void printRowStart() {
        System.out.print("|");
    }

    private void printRowEnd() {
        System.out.print("\n");
    }

    private void printCell(VisibleActor actor) {
        Character character = actor == null ? '_' : actor.getSymbol();
        System.out.print(character + "|");
    }
}

package packman;

import packman.gfx.Renderer;
import packman.model.Board;
import packman.model.Kolobok;

public class Packman {
    public static void main(String[] args) throws InterruptedException {

        Board board = new Board(16);


        Renderer renderer = new Renderer();

        while(true) {
            renderer.render(board);
            Thread.sleep(board.getRefreshRate());
        }
    }
}

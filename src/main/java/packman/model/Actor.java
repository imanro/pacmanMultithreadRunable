package packman.model;

import packman.model.exception.OutOfBoundsException;
import packman.model.exception.UnableToMoveException;
import packman.util.Logger;

import java.time.Instant;
import java.util.Random;

public abstract class Actor implements VisibleActor, PositionedActor, Runnable {

    public Integer position;

    public Direction curDirection;

    public Random generator;

    private Board board;

    private Logger logger = new Logger();

    public Actor(Board board) {
        this.board = board;

        generator = new Random();
        generator.setSeed(Instant.now().getEpochSecond());
    }

    @Override
    public void run() {

        while(true) {
            try {
                if (position == null) {
                    // place

                    // decide whether it's time to be placed (0-9, 8,9 -positive)
                    Integer chance = generator.nextInt(10);

                    if (chance >= 8) {
                        placeOnBoard();
                    }
                    // if so

                } else {
                    // move
                    logger.debug("move");
                    move();
                }

                Thread.sleep(board.getMoveRate());

            } catch (InterruptedException e) {
                logger.debug(String.format("I was interrupted: %s\n", e.getMessage()));
            }
        }
    }

    public void placeOnBoard() {
        logger.debug("Place!");

        // init counter
        Integer triesCounter = 0;

        Integer place = null;

        while(place == null && triesCounter++ < 10) {
            // while placement not found && numOfTries < 10

            // generate random position in the size bounds of board
            Integer askPlace = generator.nextInt(board.getSize());

            // check whether this place isn't occupied
            if(board.getCellHabitant(askPlace) == null) {
                // means free

                // if so, store the position
                place = askPlace;
            }

        }

        if(place != null){
            logger.debug("We have found the place!");
            board.place(this, place);
        }
    }

    public void move() {

        Integer triesCounter = 0;

        Integer place = null;

        Direction chosenDirection = null;

        while(place == null && triesCounter++ < 10) {
             chosenDirection = board.getNextDirection(curDirection);
            logger.debug(String.format("Moving %s\n", chosenDirection));

            try {
                place = board.getNextPosition(chosenDirection, position);
            } catch(UnableToMoveException e) {
                // attempt to flip
                logger.debug(e.getMessage());
                logger.debug("Flipping");
                chosenDirection = board.flipDirection(chosenDirection);

                try {
                    place = board.getNextPosition(chosenDirection, position);
                } catch(OutOfBoundsException ee) {
                    logger.debug(String.format("We are unable to move because of exception: %s", ee.getMessage()));
                }

            } catch(OutOfBoundsException e) {
                logger.debug(String.format("We are unable to move because of exception: %s", e.getMessage()));
            }

            if(place != null) {
                if (board.getCellHabitant(place) != null) {
                    logger.debug("We are unable to move there: occupied");
                    place = null;
                }
            }
        }

        if(place != null) {
            board.place(this, place);
        }

        if(chosenDirection != null) {
            curDirection = chosenDirection;
        }
    }

    @Override
    public Integer getPosition() {
        return position;
    }

    @Override
    public void setPosition(Integer position) {
        this.position = position;
    }
}

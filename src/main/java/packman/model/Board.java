package packman.model;

import packman.model.exception.OutOfBoundsException;
import packman.model.exception.UnableToMoveException;
import packman.util.Logger;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Board {
    private Integer size;

    private Integer sideSize;

    private VisibleActor[] cellsOccupied;

    private List<Actor> actors;

    public Random generator;

    Direction[] directions;

    private Logger logger = new Logger();

    /**
     * @throws IllegalArgumentException
     * @param size
     */
    public Board(Integer size) {

        if(size % 2 == 1 || size <= 0) {
            throw new IllegalArgumentException("The size must be positive even integer");
        }


        // FIXME: check sqrt when the wifi will be available hehe
        Double sqrt = Math.sqrt((double)size);
        if(sqrt - Math.floor(sqrt) != 0) {
            throw new IllegalArgumentException("The squart root of the given argument should be perfect");
        }

        this.size = size;
        this.sideSize = (int)Math.floor(sqrt);

        cellsOccupied = new VisibleActor[size];

        initCommon();
        initActors();
        runActors();
    }

    private void initCommon() {
        // directions
        directions = new Direction[4];
        directions[0] = Direction.UP;
        directions[1] = Direction.DOWN;
        directions[2] = Direction.LEFT;
        directions[3] = Direction.RIGHT;

        // random
        generator = new Random();
        generator.setSeed(Instant.now().getEpochSecond());
    }

    private void initActors() {
        // initialize a main actor
        actors = new ArrayList<>();
        actors.add(new Kolobok(this));
        actors.add(new Monster(this));
        actors.add(new Monster(this));
        actors.add(new Monster(this));
    }

    private void runActors() {
        // create runner and run
        ExecutorService executor = Executors.newCachedThreadPool();

        // execute all actors
        actors.forEach(actor -> {
            executor.execute(actor);
        });
    }

    public Integer getSize() {
        return sideSize;
    }

    /**
     * @throws IllegalArgumentException
     *
     * @param position
     * @return
     */
    public VisibleActor getCellHabitant(Integer position) {

        if(position < 0 || position > (cellsOccupied.length - 1)) {
            throw new IllegalArgumentException("The position should be in the bounds of array");
        }

        return cellsOccupied[position];
    }

    /**
     * @throws IllegalArgumentException
     *
     * @param actor
     * @param position
     */
    public void place(Actor actor, Integer position) {

        if(position < 0 || position > (size - 1)) {
            throw new IllegalArgumentException("Unable to place actor");
        }

        // find previous actor's position
        Integer prevPosition = actor.getPosition();
        if(prevPosition != null) {
            if(prevPosition < 0 || prevPosition > (size - 1)){
                ; // do nothing
            } else {
                this.cellsOccupied[prevPosition] = null;
            }
        }
        cellsOccupied[position] = actor;
        actor.setPosition(position);
        checkForConcurrencyError();
    }

    public Boolean isInBounds(Integer index){
        return index >= 0 && index <= size - 1;
    }

    public Integer getSideSize() {
        return sideSize;
    }

    public Direction getRandomDirection() {
        Integer choosenDirectionIndex = generator.nextInt(directions.length);
        return directions[choosenDirectionIndex];
    }

    public Direction getNextDirection(Direction prevDirection) {

        if(prevDirection == null) {
            return getRandomDirection();
        } else {
            // one chanse of 10 to change
            Integer chance = generator.nextInt(10);
            if (chance == 9) {
                return getRandomDirection();
            } else {
                return prevDirection;
            }
        }
    }

    /**
     * @param direction
     */
    Integer getNextPosition(Direction direction, Integer curPosition) throws UnableToMoveException, OutOfBoundsException {
        Integer newPosition = curPosition;

        switch (direction) {
            case RIGHT:
            default:
                if(((curPosition + 1) % getSideSize()) == 0) {
                    logger.debug(String.format("(%d + 1) %% %d == 0: %d", curPosition, getSideSize(), ((curPosition + 1) % getSideSize())));
                    throw new UnableToMoveException("Should change to left");
                    // treat as left
                } else {
                    newPosition += 1;
                }
                break;
            case LEFT:
                if((curPosition % getSideSize()) == 0) {
                    logger.debug(String.format("%d %% %d == 0 : %d", curPosition, getSideSize(), (curPosition % getSideSize())));
                    throw new UnableToMoveException("Should change to right");
                } else {
                    newPosition -= 1;
                }
                break;
            case UP:
                newPosition -= this.getSideSize();
                break;
            case DOWN:
                newPosition += this.getSideSize();
                break;
        }

        if (this.isInBounds(newPosition)){
            return newPosition;
        } else {
            throw new OutOfBoundsException(String.format("We are unable to move %s: out of bounds of board", direction));
        }
    }

    public Long getMoveRate() {
        return 200L;
    }

    public Long getRefreshRate() {
        return 100L;
    }

    public Direction flipDirection(Direction direction) {
        switch (direction) {
            case RIGHT: default:
                return Direction.LEFT;
            case LEFT:
                return Direction.RIGHT;
            case UP:
                return Direction.DOWN;
            case DOWN:
                return Direction.UP;
        }
    }

    private void checkForConcurrencyError() {
        Long countOccupied = Arrays.stream(cellsOccupied)
                .filter(Objects::nonNull)
                .count();

        if(countOccupied < actors.size()) {
            logger.error( "CONCURRENCY ERROR DETECTED");
        }
    }
}

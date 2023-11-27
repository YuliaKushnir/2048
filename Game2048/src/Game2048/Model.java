package Game2048;

import java.util.*;

public class Model {
    private Tile[][] gameTiles;
    private static final int FIELD_WIDTH = 5;

    int maxTile = 2;
    int score = 0;
    private boolean isSaveNeeded = true;

    private Stack<Tile[][]> previousStates = new Stack<>();
    private Stack<Integer> previousScores = new Stack<>();

    public Model() {resetGameTiles(); }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    void resetGameTiles() {
        gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                gameTiles[i][j] = new Tile();
            }
        }
        addTile();
        addTile();
    }

    private void addTile() {
        List<Tile> emptyTiles = getEmptyTiles();
        if(!emptyTiles.isEmpty()) {
            int index = (int) (Math.random() * emptyTiles.size()) % emptyTiles.size();
            Tile emptyTile = emptyTiles.get(index);
            emptyTile.value = Math.random() < 0.9 ? 2 : 4;
        }
    }

    private List<Tile> getEmptyTiles() {
        final List<Tile> list = new ArrayList<>();
        for(Tile[] tileArray: gameTiles) {
            for(Tile t: tileArray) {
                if(t.isEmpty()) {
                    list.add(t);
                }
            }
        }
        return list;
    }

    private boolean compressTiles(Tile[] tiles) {
        int insertPosition = 0;
        boolean result = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if(!tiles[i].isEmpty()) {
                if(i!= insertPosition) {
                    tiles[insertPosition] = tiles[i];
                    tiles[i] = new Tile();
                    result = true;
                }
                insertPosition++;
            }
        }
        return result;
    }

    private boolean mergeTiles(Tile[] tiles) {
        boolean result = false;
        LinkedList<Tile> tilesList = new LinkedList<>();
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if(tiles[i].isEmpty()) continue;

            if(i<FIELD_WIDTH -1 && tiles[i].value == tiles[i+1].value) {
                int updatedValue = tiles[i].value * 2;
                if(updatedValue > maxTile) maxTile = updatedValue;
                score += updatedValue;
                tilesList.addLast(new Tile(updatedValue));
                tiles[i+1].value = 0;
                result = true;
            } else {
                tilesList.addLast(new Tile(tiles[i].value));
            }
            tiles[i].value = 0;
        }

        for (int i = 0; i < tilesList.size(); i++) {
            tiles[i] = tilesList.get(i);
        }
        return result;
    }

    private Tile[][] rotateClockwise(Tile[][] tiles) {
        final int N = tiles.length;
        Tile[][] result = new Tile[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                result[j][N -1 -i] = tiles[i][j];
            }
        }
        return result;
    }

    public void left() {
        if (isSaveNeeded) saveState(gameTiles);
        boolean moveFlag = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if(compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])) {
                moveFlag = true;
            }
        }
        if(moveFlag) addTile();
        isSaveNeeded = true;
    }

    public void right(){
        saveState(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        left();
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
    }

    public void up() {
        saveState(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        left();
        gameTiles = rotateClockwise(gameTiles);
    }

    public void down() {
        saveState(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        left();
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
    }

    private int getEmptyTilesCount() {
        return getEmptyTiles().size();
    }

    private boolean isFull() {
        return getEmptyTilesCount() == 0;
    }

    boolean canMove() {
        if (!isFull()) return true;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                Tile t = gameTiles[i][j];
                if ((i< FIELD_WIDTH - 1 && t.value == gameTiles[i+1][j].value)
                 || ((j< FIELD_WIDTH - 1) && t.value == gameTiles[i][j+1].value)){
                    return true;
                }
            }
        }
        return false;
    }

    private void saveState(Tile[][] tiles) {
        Tile[][] tempTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                tempTiles[i][j] = new Tile(tiles[i][j].value);
            }
        }
        previousStates.push(tempTiles);
        previousScores.push(score);
        isSaveNeeded = false;
    }

    public void rollBack() {
        if(!previousStates.isEmpty() && !previousScores.isEmpty()) {
            gameTiles = previousStates.pop();
            score = previousScores.pop();
        }
    }

    void randomMove() {
        int n = ((int) (Math.random() * 100)) % 4;
        switch (n){
            case 0: left(); break;
            case 1: up(); break;
            case 2: down(); break;
            case 3: right(); break;
        }
    }

    private MoveEfficiency getMoveEfficiency(Move move) {
        MoveEfficiency moveEfficiency = new MoveEfficiency(-1, 0, move);
        move.move();
        if(hasBoardChanged()) moveEfficiency = new MoveEfficiency(getEmptyTilesCount(), score, move);
        rollBack();
        return moveEfficiency;
    }

    private boolean hasBoardChanged() {
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if(gameTiles[i][j].value != previousStates.peek()[i][j].value) return true;
            }

        }
        return false;
    }

    void autoMove() {
        PriorityQueue<MoveEfficiency> moveEfficiencies = new PriorityQueue<>(4, Collections.reverseOrder());
        moveEfficiencies.offer(getMoveEfficiency(this::left));
        moveEfficiencies.offer(getMoveEfficiency(this::up));
        moveEfficiencies.offer(getMoveEfficiency(this::right));
        moveEfficiencies.offer(getMoveEfficiency(this::down));

        moveEfficiencies.peek().getMove().move();
    }

}

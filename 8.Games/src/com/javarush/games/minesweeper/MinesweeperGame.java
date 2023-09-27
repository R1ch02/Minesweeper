package com.javarush.games.minesweeper;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;

    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private int countFlags;
    private boolean isGameStopped;
    private int countClosedTiles = SIDE*SIDE;
    private int score;



    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    @Override
    public void onMouseLeftClick(int x,int y){
        if(isGameStopped){
            restart();
            return;
        }
        openTile(x,y);

    }

    @Override
    public void  onMouseRightClick(int x,int y){
        if(isGameStopped){
            restart();
            return;
        }
        markTile(x,y);
    }


    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellValue(x,y,"");
                setCellColor(x, y, Color.AQUAMARINE);
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }



    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }

    private void countMineNeighbors(){

        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                if(!gameField[y][x].isMine){
                    GameObject gameObject = gameField[y][x];
                    for(GameObject neighbor : getNeighbors(gameObject)){
                        if(neighbor.isMine){
                            gameObject.countMineNeighbors++;
                        }
                    }

                }
            }
        }

    }

    private void openTile(int x, int y){

        GameObject gameObject = gameField[y][x];
        if(isGameStopped || gameObject.isFlag || gameObject.isOpen){
            return;
        }
        gameObject.isOpen=true;
        countClosedTiles--;
        setCellColor(x,y,Color.GREEN);
        
        if(gameObject.isMine){
            setCellValueEx(x,y,Color.RED,MINE);
            gameOver();
        } else if(gameObject.countMineNeighbors > 0){
            setCellNumber(x,y,gameObject.countMineNeighbors);
            score+=5;
            setScore(score);
        } else {
            List<GameObject> neighbors = getNeighbors(gameObject);
            setCellValue(gameObject.x,gameObject.y,"");
            for(GameObject neighbor : neighbors) {
                if (!neighbor.isOpen) {
                    openTile(neighbor.x, neighbor.y);
                }
            }
            score+=5;
            setScore(score);
        }

        if(countMinesOnField == countClosedTiles && !gameObject.isMine){
            win();

        }
    }

    private void markTile(int x, int y){
      GameObject gameObject = gameField[y][x];
      if(gameObject.isOpen || (countFlags == 0 && !gameObject.isFlag)){
        return;
      }
      if(gameObject.isFlag){
          countFlags++;
          gameObject.isFlag = false;
          setCellValue(x,y,"");
          setCellColor(x,y,Color.AQUAMARINE);
      } else {
          countFlags--;
          gameObject.isFlag = true;
          setCellValue(x,y,FLAG);
          setCellColor(x,y,Color.RED);
      }

    }

    private void gameOver(){
        isGameStopped = true;
        showMessageDialog(Color.RED,"GameOver",Color.BLACK,50);
    }

    private void win(){
        isGameStopped = true;
        showMessageDialog(Color.YELLOW,"You win",Color.BLACK,50);


    }

    private void restart(){
        countMinesOnField = 0;
        setScore(score);
        countClosedTiles = SIDE*SIDE;
        isGameStopped = false;
        createGame();


    }

}
package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.Observer;

public interface IModel {
    //public void generateMaze(ActionEvent actionEvent);
    public void generateMaze(int row,int col);
    public void update_car(int dirc);
    public int getCol_car();
    public Maze getMaze();
    public int getRow_car();
    public void assign_obs(Observer o);
    public void solve_maze(Maze maze);
    public Solution getSolution();
    public void loadGame();


    void saveGame();
}
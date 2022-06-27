package ViewModel;

import Model.IModel;
import Model.MyModel;
import algorithms.mazeGenerators.Maze;
import javafx.beans.InvalidationListener;

import java.awt.event.ActionEvent;
import static java.awt.event.KeyEvent.*;

import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javafx.scene.input.KeyCode;

public class MyViewModel extends Observable implements Observer {

    public IModel getModel() {
        return model;
    }

    private IModel model;

    public Maze getMaze() {
        return maze;
    }

    private Maze maze;

    public int getRowCar() {
        return rowCar;
    }

    public int getColCar() {
        return colCar;
    }

    private int rowCar;
    private int colCar;
    public MyViewModel(IModel im) {
        this.model = im;
        this.model.assign_obs(this);
        this.maze = null;
    }

    @Override
    public void update(java.util.Observable o, Object arg) {
       if(o==model){
           if(arg.equals("player moved")){
               this.rowCar = model.getRow_car();
               this.colCar = model.getCol_car();
               setChanged();
               notifyObservers("player moved");


           }
           if(arg.equals("generated maze")){
               this.maze = model.getMaze();
               this.rowCar = model.getRow_car();
               this.colCar = model.getCol_car();
               setChanged();
               notifyObservers("generated maze");
           }
           if(arg.equals("got help")){

               setChanged();
               notifyObservers("got help");
           }

       }


    }
    public void generateMaze(int row, int col){
        if(row<0 || col<0) {
            return;
        }
        this.model.generateMaze(row,col);
        /*this.rowCar = this.model.getRow_car();
        this.colCar = this.model.getCol_car();*/

    }
    public void movechar(int keyevent){


        //this.model.update_car(keyevent.getCode());
        this.model.update_car(keyevent);

    }
    public void solvemaze(Maze maze){
        this.model.solve_maze(maze);
    }
    public void getSolution(){
        model.getSolution();
    }
    public void saveGame() {
        model.saveGame();
    }
    public void loadGame(){
        model.loadGame();
    }



}

package Model;

import View.HelloApplication;
import View.MazeDisplayer;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.search.*;
import com.sun.tools.javac.Main;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import static javafx.scene.input.KeyCode.*;

public class MyModel extends Observable implements IModel{
    Maze maze;
    public MyMazeGenerator generator;
    public TextField textField_mazeRows;
    public TextField textField_mazeColumns;
    public MazeDisplayer mazeDisplayer;
    private int row_car;
    private int  col_car;
    Solution solution;
    private HashMap<String ,Integer> sizes = new HashMap<>();
    public int getRow_car() {
        return row_car;
    }

    public int getCol_car() {
        return col_car;
    }

    public MyModel(){
        this.maze = null;
        this.row_car = 0;
        this.col_car = 0;
    }
    public void update_car(int dirc){
        switch (dirc){
            case 104: //up
                if(row_car!=0 ){
                    if(this.maze.get_position(row_car-1,col_car) !=1) {
                        mazeDisplayer.set_player_pos(row_car - 1, col_car);
                        row_car--;
                    }
                }
                break;

            case 98: //down
                if(row_car!=maze.get_length_row()-1){
                    if(this.maze.get_position(row_car+1,col_car) !=1) {
                        mazeDisplayer.set_player_pos(row_car + 1, col_car);
                        row_car++;
                    }
                }
                break;

            case 102://right
                if(col_car!=maze.get_length_col()-1){
                    if(this.maze.get_position(row_car,col_car+1) !=1) {
                        mazeDisplayer.set_player_pos(row_car, col_car + 1);
                        col_car++;
                    }
                }
                break;

            case 100: //left
                if(col_car!=0){
                    if(this.maze.get_position(row_car,col_car-1) !=1) {
                        mazeDisplayer.set_player_pos(row_car, col_car - 1);
                        col_car--;
                    }
                }
                break;

            case 105://up-right
                if(row_car!=0 && col_car!=maze.get_length_col()-1){
                    if(this.maze.get_position(row_car-1,col_car+1) !=1) {
                        mazeDisplayer.set_player_pos(row_car - 1, col_car + 1);
                        col_car++;
                        row_car--;
                    }
                }
                break;

            case 103: //up-left
                if(row_car!=0 && col_car!=0){
                    if(this.maze.get_position(row_car-1,col_car-1) !=1) {
                        mazeDisplayer.set_player_pos(row_car - 1, col_car - 1);
                        col_car--;
                        row_car--;
                    }
                }
                break;

            case 99: //down-right
                if(row_car!=maze.get_length_row()-1 && col_car!=maze.get_length_col()-1){
                    if(this.maze.get_position(row_car+1,col_car+1) !=1) {
                        mazeDisplayer.set_player_pos(row_car + 1, col_car + 1);
                        row_car++;
                        col_car++;
                    }
                }
                break;

            case 97: //down-left
                if(row_car!=maze.get_length_row()-1 && col_car!=0){
                    if(this.maze.get_position(row_car+1,col_car-1) !=1) {
                        mazeDisplayer.set_player_pos(row_car + 1, col_car - 1);
                        row_car++;
                        col_car--;
                    }
                }
                break;
        }
        setChanged();
        notifyObservers("player moved");
    }

    public void generateMaze(int row,int col) {
        if(generator == null)
            generator = new MyMazeGenerator();
        if(this.mazeDisplayer==null)
            this.mazeDisplayer = new MazeDisplayer();

        /*int rows = Integer.valueOf(textField_mazeRows.getText());
        int cols = Integer.valueOf(textField_mazeColumns.getText());*/
        this.maze = generator.generate(row, col);
        this.row_car = this.maze.getStartPosition().getRowIndex();
        this.col_car = this.maze.getStartPosition().getColumnIndex();
        //this.mazeDisplayer.drawMaze(maze);

        setChanged();
        notifyObservers("generated maze");
    }
    public Maze getMaze(){
        return this.maze;
    }
    public void assign_obs(Observer o){
        this.addObserver(o);
    }
    public void solve_maze(Maze maze){

        mazeDisplayer.help_to_user(maze);
        setChanged();
        notifyObservers("got help");
    }
    public Solution getSolution(){
        return this.solution;
    }
    public void saveGame() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Game");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Game Files", "*.maze"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null){
            try{
                ObjectOutputStream mazeSaver = new ObjectOutputStream(new FileOutputStream(file));
                mazeSaver.flush();
                mazeSaver.writeObject(this.maze);
                mazeSaver.flush();
                mazeSaver.close();


            } catch (Exception e){
                System.out.println("problem");
            }

        }

    }
    public void loadGame(){
        if(this.mazeDisplayer==null){
            this.mazeDisplayer = new MazeDisplayer();
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Game");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Game Files", "*.maze"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null){
            try{
                ObjectInputStream maze_toLoad = new ObjectInputStream(new FileInputStream(file));
                this.maze = (Maze)maze_toLoad.readObject();
                this.row_car = this.maze.getStartPosition().getRowIndex();
                this.col_car = this.maze.getStartPosition().getColumnIndex();
                this.solution = null;
                setChanged();
                notifyObservers("generated maze");
                notifyObservers("got help");
                notifyObservers("player moved");


            } catch (Exception e){
                System.out.println("problem");
            }

        }
    }


}

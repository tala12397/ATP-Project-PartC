package Model;

import IO.MyDecompressorInputStream;
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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import Client.Client;
import Client.IClientStrategy;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;


/**
 * class which implement the model of the game
 */
public class MyModel extends Observable implements IModel{
    Maze maze;
    public MyMazeGenerator generator;
    public TextField textField_mazeRows;
    public TextField textField_mazeColumns;
    public MazeDisplayer mazeDisplayer;
    private int row_car;
    private int  col_car;
    Solution solution;
    Server generate_maze_Server;
    Server solve_maze_Server;

    public int getRow_car() {
        return row_car;
    }

    public int getCol_car() {
        return col_car;
    }

    /**
     * constructor of the model
     */
    public MyModel(){
        this.maze = null;
        this.row_car = 0;
        this.col_car = 0;
        this.generate_maze_Server = new Server(5400,1000,new ServerStrategyGenerateMaze());
        this.generate_maze_Server.start();
        this.solve_maze_Server = new Server(5401,1000, new ServerStrategySolveSearchProblem());
        this.solve_maze_Server.start();
    }

    /**
     * close the servers in exit
     */
    public void Servers_Stop(){
        this.generate_maze_Server.stop();
        this.solve_maze_Server.stop();
    }

    /**
     * move the character on the screen
     * @param dirc the direction to move
     */
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

    /**
     * generate the maze
     * @param row
     * @param col
     * @throws UnknownHostException
     */

    public void generateMaze(int row,int col) throws UnknownHostException {

        if(this.mazeDisplayer==null)
            this.mazeDisplayer = new MazeDisplayer();
        try{
            Client generate_Client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inputStream, OutputStream outputStream) {
                    try {
                        ObjectOutputStream to_Server = new ObjectOutputStream(outputStream);
                        to_Server.flush();
                        ObjectInputStream from_Server = new ObjectInputStream(inputStream);
                        int [] maze_dim = new int[2];
                        maze_dim[0] = row;
                        maze_dim[1] = col;
                        to_Server.writeObject(maze_dim);
                        to_Server.flush();
                        byte[] compressedMaze = (byte[]) from_Server.readObject();
                        InputStream inputStream1 = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte [] decompressedMaze = new byte[row*col + 50];
                        inputStream1.read(decompressedMaze);
                        maze = new Maze(decompressedMaze);
                        row_car = maze.getStartPosition().getRowIndex();
                        col_car = maze.getStartPosition().getColumnIndex();
                        setChanged();
                        notifyObservers("generated maze");

                    }
                    catch (Exception e){
                        System.out.println("problem with servers");
                    }
                }
            });
            generate_Client.communicateWithServer();

        }
        catch (Exception e){
            System.out.println("problem with servers");
        }
    }
    public Maze getMaze(){
        return this.maze;
    }
    public void assign_obs(Observer o){
        this.addObserver(o);
    }

    /**
     * generete the solution for helping the user
     * @param maze the maze to solve
     */
    public void solve_maze(Maze maze){
        try {
            if (maze == null)
                return;
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inputStream, OutputStream outputStream) {
                    try{
                        ObjectOutputStream to_Server = new ObjectOutputStream(outputStream);
                        to_Server.flush();
                        ObjectInputStream from_Server = new ObjectInputStream(inputStream);
                        to_Server.writeObject(maze);
                        to_Server.flush();
                        solution = (Solution) from_Server.readObject();
                        setChanged();
                        notifyObservers("got help");

                    }
                    catch (Exception e){
                        System.out.println("problem with servers");
                    }
                }
            });
            client.communicateWithServer();
        }
        catch (Exception e){
            System.out.println("problem with servers");
        }

    }
    public Solution getSolution(){
        return this.solution;
    }

    /**
     * save the game to the disk
     */
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

    /**
     * load a maze from the disk
     */
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
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("problem");
                a.setContentText("problem to load the file");
                a.showAndWait();
            }

        }
    }
    public void exit_game(){
        Servers_Stop();
    }


}

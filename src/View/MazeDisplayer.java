package View;
import algorithms.search.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import algorithms.mazeGenerators.Maze;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * class which it goal is to display the game
 */
public class MazeDisplayer extends Canvas {
    private Maze maze;
    //int count_help = 0;
    public void set_player_pos(int row, int col) {
        this.my_row_pos =row;
        this.my_col_pos = col;
        draw();
    }
    public MazeDisplayer(){
        widthProperty().addListener(evt -> draw());
        heightProperty().addListener(evt -> draw());
    }

    int my_row_pos;
    int my_col_pos;
    int goal_row_pos;
    MediaPlayer mediaPlayer;
    MediaPlayer mediasolver;

    StringProperty wall_property = new SimpleStringProperty();

    public void setGoal_image_property(String goal_image_property) {
        this.goal_image_property.set(goal_image_property);
    }

    StringProperty player_property = new SimpleStringProperty();

    public String getGoal_image_property() {
        return goal_image_property.get();
    }


    StringProperty goal_image_property = new SimpleStringProperty();


    public void setWall_property(String wall_property) {
        this.wall_property.set(wall_property);
    }

    public void setPlayer_property(String player_property) {
        this.player_property.set(player_property);
    }



    public String getWall_property() {
        return wall_property.get();
    }


    public String getPlayer_property() {
        return player_property.get();
    }


    public int getMy_row_pos() {
        return my_row_pos;
    }

    public int getMy_col_pos() {
        return my_col_pos;
    }
    int goal_col_pos;

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }
    public void drawMaze(Maze maze) {
        if(maze==null)
            return;
        stop_music_solving();
        stop_music_solved();
        this.maze = maze;
        my_row_pos = maze.getStartPosition().getRowIndex();
        my_col_pos = maze.getStartPosition().getColumnIndex();
        goal_row_pos = maze.getGoalPosition().getRowIndex();
        goal_col_pos = maze.getGoalPosition().getColumnIndex();;
        draw();
        play_music_solving();
    }
    public void help_to_user(Maze maze){
        if(maze==null)
            return;

        ISearchable is = new SearchableMaze(maze);
        ASearchingAlgorithm a = new BestFirstSearch();
        Solution s = a.solve(is);
        ArrayList<AState> sol_list = s.getSolutionPath();
        double canvasHeight = getHeight();
        double canvasWidth = getWidth();
        double cellHeight = canvasHeight / maze.get_length_row();
        double cellWidth = canvasWidth / maze.get_length_col();
        GraphicsContext graphicsContext = getGraphicsContext2D();
        graphicsContext.setFill(Color.RED);
        for(int i=0;i<sol_list.size();i++){
            if( sol_list.get(i).getRowIndex()!=this.my_row_pos ||  sol_list.get(i).getColumnIndex()!=this.my_col_pos) {
                if(sol_list.get(i).getRowIndex()!=maze.getGoalPosition().getRowIndex() ||  sol_list.get(i).getColumnIndex()!=maze.getGoalPosition().getColumnIndex()) {
                    graphicsContext.setFill(Color.RED);
                    double x = sol_list.get(i).getColumnIndex() * cellWidth;
                    double y = sol_list.get(i).getRowIndex() * cellHeight;
                    graphicsContext.setFill(Color.GREEN);
                    //graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                    graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                }
            }

        }



    }
    private void play_music_solving(){
        try {
            Media media = new Media(new File("Resources/music/solving_song.mp3").toURI().toString());
            this.mediaPlayer = new MediaPlayer(media);
            this.mediaPlayer.play();

        }
        catch (Exception e){
            System.out.println("problem");
        }


    }
    public void stop_music_solving(){
        if(this.mediaPlayer==null)
            return;
        this.mediaPlayer.stop();
        this.mediaPlayer = null;
    }
    private void play_music_solved(){
        stop_music_solving();
        try {
            Media media = new Media(new File("Resources/music/solved_song.mp3").toURI().toString());
            this.mediasolver = new MediaPlayer(media);
            this.mediasolver.play();

        }
        catch (Exception e){
            System.out.println("problem");
        }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("the end");
        a.setContentText("congrats! you did it!");
        stop_music_solving();
        Optional<ButtonType> result = a.showAndWait();
        if(result.get() == ButtonType.OK || result.get() == ButtonType.CANCEL){
            stop_music_solved();
        }

    }
    private void stop_music_solved(){
        //this.mediaPlayer.pause();
        if(this.mediasolver==null)
            return;
        this.mediasolver.stop();
        this.mediasolver = null;
    }
    public void back_to_maze(){
        draw();
    }
    public void clear_all(){
        double canvasHeight = getHeight();
        double canvasWidth = getWidth();
        GraphicsContext graphicsContext = getGraphicsContext2D();
        //clear the canvas:
        graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);
        this.maze = null;
    }

    public void draw() {
        if(maze != null){
            //count_help = 0;
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            int rows = maze.get_length_row();
            int cols = maze.get_length_col();
            double cellHeight = canvasHeight / rows;
            double cellWidth = canvasWidth / cols;
            GraphicsContext graphicsContext = getGraphicsContext2D();
            //clear the canvas:
            graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);
            graphicsContext.setFill(Color.RED);
            Image wallimage = null;
            try{
                wallimage = new Image(new FileInputStream(getWall_property()));
            }
            catch (FileNotFoundException e){
                System.out.println("File not found...");

            }
            Image goal = null;
            try{
               goal =  new Image(new FileInputStream(getGoal_image_property()));

            }
            catch (FileNotFoundException e){
                System.out.println("File not found...");

            }

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {

                    if(this.my_col_pos==this.goal_col_pos && this.my_row_pos==this.goal_row_pos){

                        play_music_solved();
                        this.maze = null;
                        return;

                    }
                    if(i == this.goal_row_pos && j == this.goal_col_pos){
                        double x = j * cellWidth;
                        double y = i * cellHeight;
                        if(goal==null) {
                            graphicsContext.setFill(Color.BLACK);
                            graphicsContext.fillRect(x, y, cellWidth, cellHeight);

                        }
                        else{
                            graphicsContext.drawImage(goal,x, y, cellWidth, cellHeight);
                        }
                    }
                    graphicsContext.setFill(Color.RED);
                     if(maze.get_position(i,j) == 1){
                        //if it is a wall:
                        double x = j * cellWidth;
                        double y = i * cellHeight;
                        if(wallimage==null)
                            graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                        else
                            graphicsContext.drawImage(wallimage,x, y, cellWidth, cellHeight);
                    }
                }
            }
            double h_player = this.my_row_pos * cellHeight;
            double w_player = this.my_col_pos * cellWidth;
            Image player_image = null;
            try{
                player_image = new Image(new FileInputStream(getPlayer_property()));
            }
            catch (FileNotFoundException e){
                System.out.println("file not found");
            }
            graphicsContext.drawImage(player_image,w_player,h_player,cellWidth,cellHeight);
        }
    }

}

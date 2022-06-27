package View;

import Model.MyModel;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.mazeGenerators.Maze;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.KeyEvent;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.Media;
import javafx.scene.control.MenuItem;
import java.io.*;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.input.MouseDragEvent;
import javafx.stage.Stage;
import java.lang.String;

public class MyViewController implements Initializable, Observer {

    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
    }

    private MyViewModel viewModel;
    public MyMazeGenerator generator;
    public TextField textField_mazeRows;
    public TextField textField_mazeColumns;
    public Pane mypane_a;
    public MenuItem save_but;
    public Maze maze;
    MediaPlayer mediaProblem;

    public MazeDisplayer mazeDisplayer;
    private MyModel model;


    public void ask_for_help(ActionEvent actionEvent) {
        if (this.maze == null)
            return;
        if (this.mazeDisplayer.my_row_pos == this.maze.getGoalPosition().getRowIndex() && this.mazeDisplayer.my_col_pos == this.maze.getGoalPosition().getColumnIndex())
            return;
        if (this.mazeDisplayer == null)
            return;
        if (viewModel.getMaze() == null)
            return;
        viewModel.solvemaze(maze);
    }

    public void back_to_maze(ActionEvent actionEvent) {
        mazeDisplayer.back_to_maze();
    }

    public void play_music_problem() {
        try {
            Media media = new Media(new File("Resources/music/error_music.mp3").toURI().toString());
            this.mediaProblem = new MediaPlayer(media);
            this.mediaProblem.play();

        } catch (Exception e) {
            System.out.println("problem");
        }
    }

    public void stop_music_problem() {
        this.mediaProblem.stop();
    }

    public void generateMaze(ActionEvent actionEvent) {


        if (this.generator == null) {
            this.generator = new MyMazeGenerator();
        }
        int row, col;
        try {
            row = Integer.valueOf(textField_mazeRows.getText());
            col = Integer.valueOf(textField_mazeColumns.getText());
        } catch (Exception e) {
            if (maze != null) {
                this.mazeDisplayer.stop_music_solving();
                this.mazeDisplayer.clear_all();
                this.maze = null;
            }
            Alert input = new Alert(Alert.AlertType.ERROR);
            input.setTitle("problem");
            input.setContentText("wrong size!");
            play_music_problem();
            Optional<ButtonType> result = input.showAndWait();
            if (result.get() == ButtonType.OK || result.get() == ButtonType.CANCEL) {
                stop_music_problem();
                return;
            }
            return;
        }
        if (row > 1000) {
            row = 1000;
        }
        if (col > 1000) {
            col = 1000;
        }
        if (row <= 0 || col <= 0) {
            Alert input = new Alert(Alert.AlertType.ERROR);
            input.setTitle("problem");
            input.setContentText("wrong size!");
            play_music_problem();
            Optional<ButtonType> result = input.showAndWait();
            if (result.get() == ButtonType.OK || result.get() == ButtonType.CANCEL) {
                stop_music_problem();
                return;
            }
            return;
        }
        viewModel.generateMaze(row, col);
        this.maze = viewModel.getMaze();
        this.mazeDisplayer.drawMaze(maze);


    }

    public void save_to_disk(ActionEvent actionEvent) {
        if (this.maze == null)
            return;
        viewModel.saveGame();

        actionEvent.consume();
    }

    public void load_from_disk(ActionEvent actionEvent) {

        viewModel.loadGame();

        actionEvent.consume();


    }

    public void set_Resize(Scene scene) {
        this.mazeDisplayer.widthProperty().bind(mypane_a.widthProperty());
        this.mazeDisplayer.heightProperty().bind(mypane_a.heightProperty());
        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            this.mazeDisplayer.widthProperty().bind(mypane_a.widthProperty());

        });
        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            this.mazeDisplayer.heightProperty().bind(mypane_a.heightProperty());

        });




    }

    public void mouse_clicked(MouseEvent mouseEvent) {
        mazeDisplayer.requestFocus();
    }


    @Override
    public void update(Observable o, Object arg) {
        if (o == this.viewModel) {
            if (arg.equals("player moved")) {
                this.mazeDisplayer.set_player_pos(this.viewModel.getRowCar(), this.viewModel.getColCar());
            }
            if (arg.equals("generated maze")) {
                this.maze = this.viewModel.getMaze();
                this.mazeDisplayer.drawMaze(this.maze);
            }
            if (arg.equals("got help")) {
                this.mazeDisplayer.help_to_user(this.maze);
            }
        }

    }


    public void pressed_key(KeyEvent keyEvent) {

        this.viewModel.movechar(keyEvent.getCode().getCode());


    }

    public void zoom_in_and_out(ScrollEvent scrollEvent) {
        if (scrollEvent.isControlDown()) {
            double zoom_num = 1.05;
            double deltaY = scrollEvent.getDeltaY();
            if (deltaY < 0) {
                zoom_num = 2.0 - zoom_num;
            }
            this.mazeDisplayer.setScaleX(this.mazeDisplayer.getScaleX() * zoom_num);
            this.mazeDisplayer.setScaleY(this.mazeDisplayer.getScaleY() * zoom_num);
        }

    }

    public void move_with_mouse(MouseEvent DragEvent) {
        if (viewModel.getMaze() == null)
            return;
        String s = DragEvent.getButton().name().toString();
        double mouseX = DragEvent.getX();//this.mazeDisplayer.getScaleX() ;
        double mouseY = DragEvent.getY();//this.mazeDisplayer.getScaleY();
        double scaley = mypane_a.getWidth()-this.mazeDisplayer.my_col_pos*5.0;
        double scalex = mypane_a.getHeight()-this.mazeDisplayer.my_row_pos*5.0;
            if (mouseX< scalex) { //left
                this.viewModel.movechar(100);
                return;
            }

            if (mouseX > scalex) { //right
                this.viewModel.movechar(102);
                return;

            }

            if (mouseY < scaley) { //up
                this.viewModel.movechar(104);
                return;
            }

            if (mouseY >scaley) {//down
                this.viewModel.movechar(98);
                return;
            }



    }



    public void HELP_user(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("help.fxml"));
        try {
            Parent root = (Parent) fxmlLoader.load();
            //MyViewController view = fxmlLoader.getController();
            Scene scene = new Scene(root, 640, 480);
            Stage stage = new Stage();
            stage.setTitle("help");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("problem");
        }
    }

    public void ABOUT(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("about.fxml"));
        try {
            Parent root = (Parent) fxmlLoader.load();
            Scene scene = new Scene(root, 640, 480);
            Stage stage = new Stage();
            stage.setTitle("about");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("problem");
        }
    }

    public void properties(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("properties.fxml"));
        try {
            Parent root = (Parent) fxmlLoader.load();
            Scene scene = new Scene(root, 640, 480);
            Stage stage = new Stage();
            stage.setTitle("properties");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("problem");
        }
    }



}

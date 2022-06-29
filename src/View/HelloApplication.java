package View;

import Model.IModel;
import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.Optional;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("MyView.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        MyViewController view = fxmlLoader.getController();
        Scene scene = new Scene(root, 640, 480);
        stage.setTitle("Welcome to Solving a Maze game!");
        stage.setScene(scene);


        IModel model = new MyModel();
        MyViewModel viewmodel = new MyViewModel(model);
        view.setViewModel(viewmodel);
        viewmodel.addObserver(view);
        view.resize(scene);
        stage.show();

        stage.setOnCloseRequest(event-> {
            Alert a = new Alert(Alert.AlertType.CONFIRMATION);
            a.setTitle("exit");
            a.setContentText("are you sure you want to exit?");
            Optional<ButtonType> result = a.showAndWait();
            if (result.get() == ButtonType.OK) {
                view.exitApplication(event);
            }
            else
                event.consume();

        });




    }

    public static void main(String[] args) {
        launch();
    }
}
package db.management.advanced.java;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author AlperenDGRYL
 */
public class Controller extends Application {

    Stage stage;
    TableView table;
    ViewManager viewManager;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        DBconnect db = new DBconnect();
        db.CreateConnection();

        VBox textFieldsVbox = new VBox();
        ObservableList<String> queryResult = FXCollections.observableArrayList();

        DBManager dbm = new DBManager(db, textFieldsVbox, table);

        String tableName = "student"; // Replace this with your actual table name
        viewManager = new ViewManager(db, stage, textFieldsVbox, queryResult, tableName, dbm);

        // Set the selectedObject in DBManager after the viewManager is created
        dbm.setSelectedObject(viewManager.getSelectedObject());

        ActionManager actionManager = new ActionManager(db, textFieldsVbox);

        CreateMainPage(viewManager, actionManager, textFieldsVbox);
    }

    void CreateMainPage(ViewManager viewManager, ActionManager actionManager, VBox textFieldsVbox) {
        HBox root = new HBox(10);
        root.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        root.setStyle("-fx-background-color: #404546;");

        VBox leftVbox = new VBox(10); //Folders
        leftVbox.getChildren().add(viewManager.createTreeView());

        VBox rightVbox = new VBox(10);

        HBox queryTablesHbox = new HBox(); //Tables
        table = viewManager.createTableView("student");
        queryTablesHbox.getChildren().add(table);

        ObservableList<TableColumn<?, ?>> columns = table.getColumns();
        columns.forEach((column) -> {
            column.prefWidthProperty().bind(table.widthProperty().divide(columns.size()));
        });
        rightVbox.getChildren().addAll(queryTablesHbox, actionManager.actions(stage, table), textFieldsVbox);

        root.getChildren().addAll(leftVbox, rightVbox);

        Scene scene = new Scene(root);
        stage.setTitle("DB Management System");
        stage.setHeight(775);
        stage.setMinHeight(500);
        stage.setWidth(1350);
        stage.setMinWidth(900);
        stage.setScene(scene);
        stage.show();
    }

    static void main(String[] args) {
        launch(args);
    }
}

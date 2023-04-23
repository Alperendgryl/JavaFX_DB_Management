package db.management.advanced.java;

import java.util.ArrayList;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author AlperenDGRYL
 */
public class ActionManager {

    private final DBconnect db;

    ArrayList<String> tableColumnName = new ArrayList<>();
    int tableColumnCount = ViewManager.tableColumnCount;

    private VBox textFieldsVbox;

    public ActionManager(DBconnect db, VBox textFieldsVbox) {
        this.db = db;
        this.textFieldsVbox = textFieldsVbox;
    }

    public VBox actions(Stage stage, TableView tableViewInstance) { //Add, delete, search actions

        DBManager dbm = new DBManager(db, textFieldsVbox, tableViewInstance);

        VBox root = new VBox(10);
        root.prefWidthProperty().bind(stage.widthProperty());

        HBox searchHbox = new HBox(5);
        TextArea searchTextArea = new TextArea();
        searchTextArea.setPromptText("Write a query to search.");

        VBox searchBtnResult = new VBox(5);

        Button searchButton = new Button("Search");
        TextArea queryResult = new TextArea();

        queryResult.setEditable(false);
        queryResult.setPromptText("Query result will be seen in here.");

        searchButton.setOnAction(e -> {
            dbm.search(searchTextArea, queryResult);
        });
        searchBtnResult.getChildren().addAll(searchButton, queryResult);

        searchHbox.prefWidthProperty().bind(stage.widthProperty());
        searchTextArea.prefWidthProperty().bind(searchHbox.widthProperty().multiply(.55f));
        searchBtnResult.prefWidthProperty().bind(searchHbox.widthProperty().multiply(.45f));
        searchHbox.getChildren().addAll(searchTextArea, searchBtnResult);

        HBox txtFieldsAndButtonsHbox = new HBox(5);
        textFieldsVbox = new VBox(5);
        for (int i = 0; i < tableColumnCount; i++) {
            TextField textField = new TextField();
            textField.setPromptText(tableColumnName.get(i));
            textFieldsVbox.getChildren().addAll(textField);
        }

        VBox buttonsVbox = new VBox(5);
        Button add = new Button("Add");
        add.setStyle("-fx-background-color: green; -fx-text-fill:white;");
        add.prefWidthProperty().bind(stage.widthProperty());
        add.setOnAction(e -> dbm.insert(textFieldsVbox));

        Button update = new Button("Update");
        update.prefWidthProperty().bind(stage.widthProperty());
        update.setStyle("-fx-background-color: orange; -fx-text-fill:white;");
        update.setOnAction(e -> dbm.update(dbm.textFieldsVbox, dbm.table));

        Button delete = new Button("Delete Selected Row");
        delete.prefWidthProperty().bind(stage.widthProperty());
        delete.setStyle("-fx-background-color: red; -fx-text-fill:white;");
        delete.setOnAction(e -> dbm.delete(dbm.table));

        buttonsVbox.getChildren().addAll(add, update, delete);

        txtFieldsAndButtonsHbox.prefWidthProperty().bind(stage.widthProperty());
        textFieldsVbox.prefWidthProperty().bind(stage.widthProperty().multiply(.80f));
        buttonsVbox.prefWidthProperty().bind(stage.widthProperty().multiply(.20f));

        txtFieldsAndButtonsHbox.getChildren().addAll(textFieldsVbox, buttonsVbox);
        root.getChildren().addAll(searchHbox, txtFieldsAndButtonsHbox);
        return root;
    }
}
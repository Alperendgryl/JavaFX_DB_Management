package db.management.advanced.java;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
public class DBManager {

    private final DBconnect db;
    public VBox textFieldsVbox;
    public TableView table;
    private String currentTableName = "";

    ArrayList<String> tableColumnName = new ArrayList<>();
    int tableColumnCount = ViewManager.tableColumnCount;
    private final TableManager tableManager;

    Object selectedObject;

    public DBManager(DBconnect db, VBox textFieldsVbox, TableView table) {
        this.db = db;
        this.textFieldsVbox = textFieldsVbox;
        this.table = table;
        this.tableManager = new TableManager(db);
    }

    public void setSelectedObject(Object selectedObject) {
        this.selectedObject = selectedObject;
    }

    public VBox actions(Stage stage) { //Add, delete, search actions
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
            search(searchTextArea, queryResult);
        });
        searchBtnResult.getChildren().addAll(searchButton, queryResult);

        searchHbox.prefWidthProperty().bind(stage.widthProperty());
        searchTextArea.prefWidthProperty().bind(searchHbox.widthProperty().multiply(.55f));
        searchBtnResult.prefWidthProperty().bind(searchHbox.widthProperty().multiply(.45f));
        searchHbox.getChildren().addAll(searchTextArea, searchBtnResult);

        HBox txtFieldsAndButtonsHbox = new HBox(5);
        textFieldsVbox = new VBox(5);
        tableColumnName = tableManager.getColumnNames(ViewManager.currentTableName);
        for (int i = 0; i < tableColumnName.size(); i++) {

            TextField textField = new TextField();
            textField.setPromptText(tableColumnName.get(i));
            textFieldsVbox.getChildren().addAll(textField);
        }

        VBox buttonsVbox = new VBox(5);
        Button add = new Button("Add");
        add.setStyle("-fx-background-color: green; -fx-text-fill:white;");
        add.prefWidthProperty().bind(stage.widthProperty());
        add.setOnAction(e -> insert(textFieldsVbox));

        Button update = new Button("Update");
        update.prefWidthProperty().bind(stage.widthProperty());
        update.setStyle("-fx-background-color: orange; -fx-text-fill:white;");
        update.setOnAction(e -> update(textFieldsVbox, table));

        Button delete = new Button("Delete Selected Row");
        delete.prefWidthProperty().bind(stage.widthProperty());
        delete.setStyle("-fx-background-color: red; -fx-text-fill:white;");
        delete.setOnAction(e -> delete(table));

        buttonsVbox.getChildren().addAll(add, update, delete);

        txtFieldsAndButtonsHbox.prefWidthProperty().bind(stage.widthProperty());
        textFieldsVbox.prefWidthProperty().bind(stage.widthProperty().multiply(.80f));
        buttonsVbox.prefWidthProperty().bind(stage.widthProperty().multiply(.20f));

        txtFieldsAndButtonsHbox.getChildren().addAll(textFieldsVbox, buttonsVbox);
        root.getChildren().addAll(searchHbox, txtFieldsAndButtonsHbox);
        return root;
    }

    public void search(TextArea searchTextArea, TextArea queryResult) {
        String searchQuery = searchTextArea.getText();
        try {
            Statement stmt = db.createStatement();
            ResultSet rs = stmt.executeQuery(searchQuery);
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    sb.append(rs.getString(i));
                    sb.append(", ");
                }
                sb.append("\n");
            }
            if (!sb.toString().equals("")) {
                queryResult.setText(sb.toString());
            } else {
                queryResult.setText("No Result !");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void insert(VBox textFieldsVbox) {
        currentTableName = ViewManager.currentTableName;
        StringBuilder values = new StringBuilder();
        int numOfValues = 0;
        boolean ssnFieldEmpty = true;
        for (int i = 0; i < textFieldsVbox.getChildren().size(); i++) {
            if (textFieldsVbox.getChildren().get(i) instanceof TextField) {
                TextField textField = (TextField) textFieldsVbox.getChildren().get(i);
                String columnName = textField.getPromptText();
                String value = textField.getText();
                if (columnName.equals("courseID") && value.isEmpty()) {
                    showAlert(3, "Please provide a value for 'courseID'");
                    return;
                }
                if (columnName.equals("ssn") && !value.isEmpty()) {
                    ssnFieldEmpty = false;
                }
                if (!value.isEmpty()) {
                    if (numOfValues > 0) {
                        values.append(", ");
                    }
                    values.append("'").append(value).append("'");
                    numOfValues++;
                }
            }
        }

        if (ssnFieldEmpty) {
            showAlert(3, "Please provide a value for 'ssn'");
            return;
        }

        String insertQuery = "INSERT INTO " + currentTableName + " VALUES (" + values.toString() + ")";
        try {
            Statement stmt = db.createStatement();
            stmt.executeUpdate(insertQuery);
            showAlert(3, "Inserted Successfully!.\nTo See the Changes Refresh the Table.");
        } catch (SQLException ex) {
            showAlert(3, "Insertion Failed!.\nTo See the Error Read Log.");
            ex.printStackTrace();
        }
        for (int i = 0; i < textFieldsVbox.getChildren().size(); i++) {
            if (textFieldsVbox.getChildren().get(i) instanceof TextField) {
                TextField textField = (TextField) textFieldsVbox.getChildren().get(i);
                textField.clear();
            }
        }
    }

    public void delete(TableView tableView) {
        currentTableName = ViewManager.currentTableName;
        if (selectedObject != null) {
            String deleteQuery = "";
            if (selectedObject instanceof Student) {
                deleteQuery = "DELETE FROM " + currentTableName + " WHERE ssn=" + ((Student) selectedObject).ssn;
            } else if (selectedObject instanceof Course) {
                deleteQuery = "DELETE FROM " + currentTableName + " WHERE courseID=" + ((Course) selectedObject).courseID;
            } else if (selectedObject instanceof Enrollment) {
                deleteQuery = "DELETE FROM " + currentTableName + " WHERE ssn=" + ((Enrollment) selectedObject).ssn + " AND courseID=" + ((Enrollment) selectedObject).courseID;
            }
            try {
                Statement stmt = db.createStatement();
                stmt.executeUpdate(deleteQuery);
                showAlert(3, "Deleted Successfully!\nTo See the Changes Refresh the Table.");
            } catch (SQLException ex) {
                showAlert(3, "Deletion Failed!\nTo See the Error Read Log.");
                ex.printStackTrace();
            }
            table.getItems().remove(selectedObject);
        }
    }

    public void update(VBox textFieldsVbox, TableView tableView) {
        delete(table);
        insert(textFieldsVbox);
        showAlert(3, "Updated Successfully!\nTo See the Changes Refresh the Table.");
    }

    public void showAlert(int type, String message) {
        Alert alert;
        switch (type) {
            case 1:
                alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Information");
                break;
            case 2:
                alert = new Alert(AlertType.WARNING);
                alert.setTitle("Warning");
                break;
            case 3:
                alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                break;
            default:
                alert = new Alert(AlertType.NONE);
                alert.setTitle("Alert");
                break;
        }
        alert.setContentText(message);
        alert.showAndWait();
    }

}

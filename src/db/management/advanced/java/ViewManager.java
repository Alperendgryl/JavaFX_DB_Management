package db.management.advanced.java;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author AlperenDGRYL
 */
public class ViewManager {

    DBconnect db;
    Stage stage;

    VBox textFieldsVbox;
    TableView table;
    private ObservableList<String> queryResult;
    Object selectedObject;

    private final ArrayList<String> tableColumnName;
    public static int tableColumnCount;

    public static String currentTableName = "";

    private DBManager dbManager;

    public ViewManager(DBconnect db, Stage stage, VBox textFieldsVbox, ObservableList<String> queryResult, String tableName, DBManager dbManager) {
        this.db = db;
        this.stage = stage;
        this.textFieldsVbox = textFieldsVbox;
        this.queryResult = queryResult;
        this.dbManager = dbManager;

        this.currentTableName = tableName;

        TableManager tableManager = new TableManager(db);
        this.tableColumnName = tableManager.getColumnNames(currentTableName);
    }

    public TableView createTableView(String tableName) {
        table = new TableView();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = db.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM " + db.DB_NAME + "." + tableName);
            ResultSetMetaData metaData = resultSet.getMetaData();
            tableColumnCount = metaData.getColumnCount();

            for (int i = 1; i <= tableColumnCount; i++) {
                String columnName = metaData.getColumnName(i);
                TableColumn column = new TableColumn(columnName);
                column.setCellValueFactory(new PropertyValueFactory<>(columnName));
                table.getColumns().add(column);
                tableColumnName.add(columnName);
            }

            table.setOnMouseClicked(e -> {
                Object selectedItem = table.getSelectionModel().getSelectedItem();
                setSelectedObject(selectedItem);
                System.out.println(selectedItem);
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }

        table.setEditable(false);
        table.setMinWidth(700);
        table.prefWidthProperty().bind(stage.widthProperty());
        table.prefHeightProperty().bind(stage.heightProperty().divide(2));
        return table;
    }

    public void setSelectedObject(Object selectedObject) {
        this.selectedObject = selectedObject;
    }

    public Object getSelectedObject() {
        return selectedObject;
    }

    public TreeView createTreeView() {
        TableManager tableManager = new TableManager(db);
        TreeItem<String> rootItem = new TreeItem<>("Tables");
        TreeItem<String> student = new TreeItem<>("student");
        TreeItem<String> course = new TreeItem<>("course");
        TreeItem<String> enrollment = new TreeItem<>("enrollment");

        rootItem.setExpanded(true);
        rootItem.getChildren().addAll(student, course, enrollment);

        TreeView<String> treeItems = new TreeView<>(rootItem);
        treeItems.setOnMouseClicked(e -> {
            TreeItem<String> selectedItem = treeItems.getSelectionModel().getSelectedItem();
            if (selectedItem != null && selectedItem != rootItem) {
                String tableName = selectedItem.getValue();
                tableColumnName.clear();
                tableManager.updateTable(table, tableName);
                tableManager.addDataToTable(table, tableName);
                currentTableName = tableName; // Update the currentTableName

                // Update tableColumnName
                tableColumnName.clear();
                tableColumnName.addAll(tableManager.getColumnNames(currentTableName));

                textFieldsVbox.getChildren().clear();
                queryResult.clear();
                for (int i = 0; i < tableColumnName.size(); i++) { // Changed from tableColumnCount to tableColumnName.size()
                    TextField textField = new TextField();
                    textField.setPromptText(tableColumnName.get(i));
                    textFieldsVbox.getChildren().addAll(textField);
                }
            }
        });

        treeItems.setPrefWidth(300);
        treeItems.prefHeightProperty().bind(stage.heightProperty());
        return treeItems;
    }
}

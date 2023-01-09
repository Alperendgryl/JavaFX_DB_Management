package db.management.advanced.java;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

//@author AlperenDGRYL
public class Controller extends Application {

    Stage stage;

    Connection conn = null;
    String host;
    String username;
    String password;
    String dbname;

    TableView table;
    ArrayList<String> tableColumnName = new ArrayList<>();
    String currentTableName = "";
    int tableColumnCount;

    VBox textFieldsVbox;
    TextArea queryResult;

    Object selectedObject;

    @Override
    public void start(Stage stage) {
        try {
            // Load the MySQL Connector/J driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Prompt the user for the database conn information
            host = JOptionPane.showInputDialog("Enter the database host [localhost]");
            username = JOptionPane.showInputDialog("Enter the database username [root]");
            password = JOptionPane.showInputDialog("Enter the database password [null]");
            dbname = JOptionPane.showInputDialog("Enter the database name [javadb]");

            // Build the conn string
            String connectionString = "jdbc:mysql://" + host + ":3306/" + dbname;

            // Open a conn to the database
            conn = DriverManager.getConnection(connectionString, username, password);

            // Do something with the conn here, like executing a query
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        this.stage = stage;
        mainPage();
    }

    void mainPage() {
        HBox root = new HBox(10);
        root.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        root.setStyle("-fx-background-color: #404546;");

        VBox leftVbox = new VBox(10); //Folders
        leftVbox.getChildren().add(createTreeView());

        VBox rightVbox = new VBox(10);

        HBox queryTablesHbox = new HBox(); //Tables
        queryTablesHbox.getChildren().add(createTableView("student"));
        ObservableList<TableColumn<?, ?>> columns = table.getColumns();
        columns.forEach((column) -> {
            column.prefWidthProperty().bind(table.widthProperty().divide(columns.size()));
        });
        rightVbox.getChildren().addAll(queryTablesHbox, actions());

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

    VBox actions() { //Add, delete, search actions
        VBox root = new VBox(10);
        root.prefWidthProperty().bind(stage.widthProperty());

        HBox searchHbox = new HBox(5);
        TextArea searchTextArea = new TextArea();
        searchTextArea.setPromptText("Write a query to search.");

        VBox searchBtnResult = new VBox(5);

        Button searchButton = new Button("Search");
        queryResult = new TextArea();

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
        for (int i = 0; i < tableColumnCount; i++) {
            TextField textField = new TextField();
            textField.setPromptText(tableColumnName.get(i));
            textFieldsVbox.getChildren().addAll(textField);
        }

        VBox buttonsVbox = new VBox(5);
        Button add = new Button("Add");
        add.setStyle("-fx-background-color: green; -fx-text-fill:white;");
        add.prefWidthProperty().bind(stage.widthProperty());
        add.setOnAction(e -> {
            insert(textFieldsVbox);
        });

        Button update = new Button("Update");
        update.prefWidthProperty().bind(stage.widthProperty());
        update.setStyle("-fx-background-color: orange; -fx-text-fill:white;");
        update.setOnAction(e -> {
            update();
        });

        Button delete = new Button("Delete Selected Row");
        delete.prefWidthProperty().bind(stage.widthProperty());
        delete.setStyle("-fx-background-color: red; -fx-text-fill:white;");
        delete.setOnAction(e -> {
            delete();
        });
        buttonsVbox.getChildren().addAll(add, update, delete);

        txtFieldsAndButtonsHbox.prefWidthProperty().bind(stage.widthProperty());
        textFieldsVbox.prefWidthProperty().bind(stage.widthProperty().multiply(.80f));
        buttonsVbox.prefWidthProperty().bind(stage.widthProperty().multiply(.20f));

        txtFieldsAndButtonsHbox.getChildren().addAll(textFieldsVbox, buttonsVbox);
        root.getChildren().addAll(searchHbox, txtFieldsAndButtonsHbox);
        return root;
    }

    void search(TextArea searchTextArea, TextArea queryResult) {
        String searchQuery = searchTextArea.getText();
        try {
            Statement stmt = conn.createStatement();
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

    void insert(VBox textFieldsVbox) {
        StringBuilder values = new StringBuilder();
        for (int i = 0; i < textFieldsVbox.getChildren().size(); i++) {
            if (textFieldsVbox.getChildren().get(i) instanceof TextField) {
                TextField textField = (TextField) textFieldsVbox.getChildren().get(i);
                String value = textField.getText();
                values.append(value);
                if (i < textFieldsVbox.getChildren().size() - 1) {
                    values.append("', '");
                }
            }
        }
        String insertQuery = "INSERT INTO " + currentTableName + " VALUES ('" + values.toString() + "')";
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(insertQuery);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        for (int i = 0; i < textFieldsVbox.getChildren().size(); i++) {
            if (textFieldsVbox.getChildren().get(i) instanceof TextField) {
                TextField textField = (TextField) textFieldsVbox.getChildren().get(i);
                textField.clear();
            }
        }
    }

    void delete() {
        if (selectedObject != null) {
            String deleteQuery = "";
            if (selectedObject instanceof Student) {
                deleteQuery = "DELETE FROM " + currentTableName + " WHERE ssn=" + ((Student) selectedObject).ssn;
            } else if (selectedObject instanceof Course) {
                deleteQuery = "DELETE FROM " + currentTableName + " WHERE ssn=" + ((Course) selectedObject).courseID;
            } else if (selectedObject instanceof Enrollment) {
                deleteQuery = "DELETE FROM " + currentTableName + " WHERE ssn=" + ((Enrollment) selectedObject).ssn + " AND courseID=" + ((Enrollment) selectedObject).courseID;
            }

            try {
                Statement stmt = conn.createStatement();
                stmt.executeUpdate(deleteQuery);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            table.getItems().remove(selectedObject);
        }
    }

    void update() {
        delete();
        insert(textFieldsVbox);
    }

    String tempValues(Object obj, String... fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i];
            String value = "";
            try {
                Field f = obj.getClass().getDeclaredField(field);
                f.setAccessible(true);
                value = f.get(obj).toString();
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
            sb.append(field).append("=").append(value);
            if (i < fields.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    TableView createTableView(String tableName) {
        table = new TableView();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = conn.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM " + dbname + "." + tableName);
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
                selectedObject = table.getSelectionModel().getSelectedItem();
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

    TreeView createTreeView() {
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
                updateTableColumns(table, tableName);
                addDataToTable(table, tableName);
                currentTableName = tableName;
                textFieldsVbox.getChildren().clear();
                queryResult.clear();
                for (int i = 0; i < tableColumnCount; i++) {
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

    void updateTableColumns(TableView table, String tableName) {
        List<String> columnNames = getColumnNames(tableName);
        ObservableList<TableColumn<?, ?>> columns = table.getColumns();
        columns.clear();
        columnNames.stream().map((columnName) -> {
            TableColumn<?, ?> column = new TableColumn<>(columnName);
            column.setCellValueFactory(new PropertyValueFactory<>(columnName));
            return column;
        }).forEachOrdered((column) -> {
            columns.add(column);
        });

        columns.forEach((column) -> {
            column.prefWidthProperty().bind(table.widthProperty().divide(columns.size()));
        });
    }

    void addDataToTable(TableView table, String tableName) {
        ObservableList<Student> studentData = FXCollections.observableArrayList();
        ObservableList<Course> courseData = FXCollections.observableArrayList();
        ObservableList<Enrollment> enrollmentData = FXCollections.observableArrayList();

        try (Statement stmt = conn.createStatement()) {
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM " + dbname + "." + tableName);
            switch (tableName) {
                case "student":
                    while (resultSet.next()) {
                        String ssn = resultSet.getString("ssn");
                        String fName = resultSet.getString("fName");
                        String mi = resultSet.getString("mi");
                        String lName = resultSet.getString("lName");
                        Date bDate = resultSet.getDate("bDate");
                        String street = resultSet.getString("street");
                        String phone = resultSet.getString("phone");
                        String zipCode = resultSet.getString("zipCode");
                        String deptId = resultSet.getString("deptId");

                        Student student = new Student(ssn, fName, mi, lName, bDate, street, phone, zipCode, deptId);
                        studentData.add(student);
                        table.setItems(studentData);
                    }
                    break;
                case "course":
                    while (resultSet.next()) {
                        String courseID = resultSet.getString("courseID");
                        String subjectID = resultSet.getString("subjectID");
                        String courseNum = resultSet.getString("courseNum");
                        String title = resultSet.getString("title");
                        String numCredit = resultSet.getString("numCredit");

                        Course course = new Course(courseID, subjectID, courseNum, title, numCredit);
                        courseData.add(course);
                        table.setItems(courseData);
                    }
                    break;
                case "enrollment":
                    while (resultSet.next()) {
                        String ssn = resultSet.getString("ssn");
                        String courseID = resultSet.getString("courseID");
                        Date dateReg = resultSet.getDate("dateReg");
                        String grade = resultSet.getString("grade");

                        Enrollment enrollment = new Enrollment(ssn, courseID, dateReg, grade);
                        enrollmentData.add(enrollment);
                        table.setItems(enrollmentData);
                    }
                    break;
                default:
                    System.out.println("SELECT * FROM " + dbname + "." + tableName);
                    System.out.println("Unrecognized Database Table !");
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    ArrayList getColumnNames(String tableName) {
        ArrayList columnNames = new ArrayList<>();
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + dbname + "." + tableName);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            tableColumnCount = columnCount;
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnName(i));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return columnNames;
    }

    static void main(String[] args) {
        launch(args);
    }

}

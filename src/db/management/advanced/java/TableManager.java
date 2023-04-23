package db.management.advanced.java;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author AlperenDGRYL
 */
public class TableManager {

    private final DBconnect db;

    public TableManager(DBconnect db) {
        this.db = db;
    }

    public void updateTable(TableView table, String tableName) {
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

    public void addDataToTable(TableView table, String tableName) {
        ObservableList<Student> studentData = FXCollections.observableArrayList();
        ObservableList<Course> courseData = FXCollections.observableArrayList();
        ObservableList<Enrollment> enrollmentData = FXCollections.observableArrayList();

        try (Statement stmt = db.createStatement()) {
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM " + tableName);
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
                    System.out.println("SELECT * FROM " + tableName);
                    System.out.println("Unrecognized Database Table !");
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getColumnNames(String tableName) {
        ArrayList<String> columnNames = new ArrayList<>();
        try (Statement stmt = db.createStatement()) {
            String query = "SELECT * FROM " + db.DB_NAME + "." + tableName;
            System.out.println("Generated SQL Query: " + query); // Debugging line
            ResultSet rs = stmt.executeQuery(query);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnName(i));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return columnNames;
    }
}

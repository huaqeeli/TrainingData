package controllers;

import Validation.FormValidation;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modeles.CoursPlaceModel;

public class AddNewCoursPlaceController implements Initializable {

    @FXML
    private VBox content;
    private TextField newplacename;
    private TableView<CoursPlaceModel> coursplaceTable;
    private TableColumn<?, ?> placeid_col;
    private TableColumn<?, ?> coursplace_col;
    ObservableList<CoursPlaceModel> placeList = FXCollections.observableArrayList();
    String placeid = null;
   

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        refreshcoursplaceTableView();
        getTableRow(coursplaceTable);
        getTableRowByInterKey(coursplaceTable);
    }

    public String getNewplacename() {
        return newplacename.getText();
    }

    public void setNewplacename(String newplacename) {
        this.newplacename.setText(newplacename);
    }

    @FXML
    private void close(ActionEvent event) {
        Stage stage = (Stage) content.getScene().getWindow();
        stage.close();
    }

    private void coursplaceSave(ActionEvent event) {
        String tableName = "placenames";
        String fieldName = "`PLACENAME`";
        String[] data = {getNewplacename()};
        String valuenumbers = "?";
        boolean newcoursnameState = FormValidation.textFieldNotEmpty(newplacename, "الرجاء ادخال مكان الانعقاد");
        if (newcoursnameState) {
            try {
                DatabaseAccess.insert(tableName, fieldName, valuenumbers, data);
                refreshcoursplaceTableView();
                setNewplacename(null);
            } catch (IOException ex) {
                FormValidation.showAlert(null, ex.toString(), Alert.AlertType.ERROR);
            }
        }
    }

    private void coursplaceEdit(ActionEvent event) {
        String tableName = "placenames";
        String fieldName = "`PLACENAME`=?";
        String[] data = {getNewplacename()};
        boolean newcoursnameState = FormValidation.textFieldNotEmpty(newplacename, "الرجاء ادخال مكان الانعقاد");
        boolean newcoursidState = FormValidation.notNull(placeid, "الرجاء اختر مكان الانعقاد من الجدول");
        if (newcoursnameState && newcoursidState) {
            try {
                DatabaseAccess.updat(tableName, fieldName, data, "PLACEID = '" + placeid + "'");
                refreshcoursplaceTableView();
            } catch (IOException ex) {
                FormValidation.showAlert(null, ex.toString(), Alert.AlertType.ERROR);
            }
        }
    }

    private void coursplaceDelete(ActionEvent event) {
        boolean newcoursidState = FormValidation.notNull(placeid, "الرجاء اختر مكان الانعقاد من الجدول");
        if (newcoursidState) {
            try {
                DatabaseAccess.delete("placenames", "PLACEID = '" + placeid + "' ");
                refreshcoursplaceTableView();
            } catch (IOException ex) {
                FormValidation.showAlert(null, ex.toString(), Alert.AlertType.ERROR);
            }
        }
    }

    private void coursplaceTableView() {
        try {
            ResultSet rs = DatabaseAccess.select("placenames");
            while (rs.next()) {
                placeList.add(new CoursPlaceModel(
                        rs.getString("PLACEID"),
                        rs.getString("PLACENAME")
                ));
            }
            rs.close();
        } catch (SQLException | IOException ex) {
            FormValidation.showAlert(null, ex.toString(), Alert.AlertType.ERROR);
        }
        placeid_col.setCellValueFactory(new PropertyValueFactory<>("placeid"));
        coursplace_col.setCellValueFactory(new PropertyValueFactory<>("placeName"));

        coursplaceTable.setItems(placeList);
    }

    public void getTableRow(TableView table) {
        table.setOnMouseClicked(new EventHandler() {
            @Override
            public void handle(Event event) {
                ObservableList<CoursPlaceModel> list = FXCollections.observableArrayList();
                list = table.getSelectionModel().getSelectedItems();
                if (list.isEmpty()) {
                    FormValidation.showAlert("", "لاتوجد بيانات");
                } else {
                    newplacename.setText(list.get(0).getPlaceName());
                    placeid = list.get(0).getPlaceid();
                }
            }
        });
    }

    private void getTableRowByInterKey(TableView table) {
        table.setOnKeyPressed(new EventHandler() {
            @Override
            public void handle(Event event) {
                ObservableList<CoursPlaceModel> list = FXCollections.observableArrayList();
                list = table.getSelectionModel().getSelectedItems();
                if (list.isEmpty()) {
                    FormValidation.showAlert("", "لاتوجد بيانات");
                } else {
                    newplacename.setText(list.get(0).getPlaceName());
                    placeid = list.get(0).getPlaceid();
                }
            }
        });
    }

    private void refreshcoursplaceTableView() {
        placeList.clear();
        coursplaceTableView();
    }

}

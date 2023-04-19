package lk.ijse.hibernate.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.hibernate.bo.BOFactory;
import lk.ijse.hibernate.bo.custom.RoomBO;
import lk.ijse.hibernate.dto.RoomDTO;
import lk.ijse.hibernate.util.ValidationUtil;
import lk.ijse.hibernate.view.tm.RoomTM;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class RoomManagementFormController {
    public AnchorPane roomManagementFormContext;
    public JFXComboBox<String> cmbType;
    public JFXTextField txtQty;
    public JFXTextField txtKeyMoney;
    public TableView tblStudent;
    public TableColumn colRoomTypeId;
    public TableColumn colType;
    public TableColumn colKeyMoney;
    public TableColumn colQty;
    public JFXButton btnDelete;
    public JFXButton btnSave;
    public JFXTextField txtRoomTypeId;
    public JFXButton btnNew;
    public TableView<RoomTM> tblRoom;

    private final RoomBO roomBO = (RoomBO) BOFactory.getBOFactory().getBO(BOFactory.BOTypes.ROOM);
    LinkedHashMap<TextField, Pattern> map = new LinkedHashMap<>();

    public void initialize() {

        textClearAndBtnDisable();
        cmbType.getItems().addAll("Non-AC", "Non-AC / Food", "AC", "AC / Food");
        loadAllRooms();


        colRoomTypeId.setCellValueFactory(new PropertyValueFactory<>("room_type_id"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colKeyMoney.setCellValueFactory(new PropertyValueFactory<>("key_money"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));

        tblRoom.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            btnDelete.setDisable(newValue == null);
            btnSave.setText(newValue != null ? "Update" : "Save");
            btnSave.setDisable(newValue == null);

            if (newValue != null) {
                txtRoomTypeId.setText(newValue.getRoom_type_id());
                cmbType.setValue(newValue.getType());
                txtKeyMoney.setText(String.valueOf(newValue.getKey_money()));
                txtQty.setText(String.valueOf(newValue.getQty()));

                txtRoomTypeId.setDisable(false);
                txtKeyMoney.setDisable(false);
                txtQty.setDisable(false);
                cmbType.setDisable(false);

            }
        });
        Pattern idPattern = Pattern.compile("^RM-[0-9]{4}$");
        Pattern keyMoneyPattern = Pattern.compile("^[1-9][0-9]*(.[0-9]{1,2})?$");
        Pattern qtyPattern = Pattern.compile("^[1-9][0-9]*$");

        map.put(txtRoomTypeId, idPattern);
        map.put(txtKeyMoney, keyMoneyPattern);
        map.put(txtQty, qtyPattern);

    }

    private void loadAllRooms() {
        try {
            List<RoomDTO> allRooms = roomBO.getAllRooms();
            for (RoomDTO roomDTO : allRooms) {
                tblRoom.getItems().add(
                        new RoomTM(roomDTO.getRoom_type_id(), roomDTO.getType(), roomDTO.getKey_money(), roomDTO.getQty())
                );

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void textClearAndBtnDisable() {
        txtRoomTypeId.clear();
        txtKeyMoney.clear();
        txtQty.clear();
        cmbType.getSelectionModel().clearSelection();

        txtRoomTypeId.setDisable(true);
        txtKeyMoney.setDisable(true);
        txtQty.setDisable(true);
        cmbType.setDisable(true);

        btnSave.setDisable(true);
        btnDelete.setDisable(true);

    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.WARNING, "Do you Want Delete", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();
        if (buttonType.get().equals(ButtonType.YES)) {
            try {
                String roomId = tblRoom.getSelectionModel().getSelectedItem().getRoom_type_id();
                boolean b = roomBO.deleteRoom(roomId);
                if (b) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Deleted").show();
                }
                tblRoom.getItems().remove(tblRoom.getSelectionModel().getSelectedItem());
                tblRoom.getSelectionModel().clearSelection();
                textClearAndBtnDisable();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void btnNewOnAction(ActionEvent actionEvent) {
        txtRoomTypeId.setDisable(false);
        txtKeyMoney.setDisable(false);
        txtQty.setDisable(false);
        cmbType.setDisable(false);

        txtRoomTypeId.clear();
        txtKeyMoney.clear();
        txtQty.clear();
        cmbType.getSelectionModel().clearSelection();

        txtRoomTypeId.requestFocus();
        btnSave.setDisable(false);
        btnSave.setText("Save");
        tblRoom.getSelectionModel().clearSelection();
    }

    public void btnSaveOnAction(ActionEvent actionEvent) {

        String roomID = txtRoomTypeId.getText();
        String type = cmbType.getValue();
        double keyMoney = Double.parseDouble(txtKeyMoney.getText());
        int qty = Integer.parseInt(txtQty.getText());


        try {
            if (btnSave.getText().equalsIgnoreCase("save")) {
                boolean save = roomBO.saveRoom(new RoomDTO(roomID, type, keyMoney, qty));

                tblRoom.getItems().add(new RoomTM(roomID, type, keyMoney, qty));
                if (!save) {
                    new Alert(Alert.AlertType.ERROR, "Failed to Saved the User").show();
                }
                textClearAndBtnDisable();

            } else {
                boolean updated = roomBO.updateRoom(new RoomDTO(roomID, type, keyMoney, qty));

                if (updated) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Updated").show();
                }

                RoomTM selectedItem = tblRoom.getSelectionModel().getSelectedItem();
                selectedItem.setRoom_type_id(roomID);
                selectedItem.setType(type);
                selectedItem.setKey_money(keyMoney);
                selectedItem.setQty(qty);

                tblRoom.refresh();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void textFields_Key_Released(KeyEvent keyEvent) {
        ValidationUtil.validate(map, btnSave);

        if (keyEvent.getCode() == KeyCode.ENTER) {
            Object response = ValidationUtil.validate(map, btnSave);

            if (response instanceof TextField) {
                TextField textField = (TextField) response;
                textField.requestFocus();// if there is a error just focus it
            }
        }
    }
}




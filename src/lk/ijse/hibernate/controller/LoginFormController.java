package lk.ijse.hibernate.controller;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.hibernate.bo.BOFactory;
import lk.ijse.hibernate.bo.custom.LogBO;
import lk.ijse.hibernate.dto.UserDTO;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class LoginFormController {
    public AnchorPane loginFormContext;
    public JFXTextField txtUserName;
    public JFXPasswordField txtPassword;
    public JFXButton btnLogin;
    public Label lblWrong;
    private final LogBO logBO = (LogBO) BOFactory.getBOFactory().getBO(BOFactory.BOTypes.LOG);

    public void btnLoginOnAction(ActionEvent actionEvent) throws Exception {
        if(txtUserName.getText().equals("") && txtPassword.getText().equals("")){
            setUI("../view/DashBoardForm.fxml");
        }else if (txtUserName.getText()!=null&& txtPassword.getText()!=null){
            List<UserDTO> userDetails = logBO.getUserDetails(txtUserName.getText(), txtPassword.getText());
            UserDTO userDTO = new UserDTO();
            for (UserDTO u:userDetails
            ) {
                userDTO.setUserName(u.getUserName());
                userDTO.setPassword(u.getPassword());
            }
            if (txtUserName.getText().equals(userDTO.getUserName()) &&  txtPassword.getText().equals(userDTO.getPassword())){
                setUI("../view/DashBoardForm.fxml");
            }else {
                new Alert(Alert.AlertType.WARNING,"Wrong UserName OR Password, Try Again!").show();
            }


        }else {
            new Alert(Alert.AlertType.WARNING,"Try Again !").show();
        }

    }


    private void setUI(String URI) throws IOException {

        Stage stage1= (Stage)loginFormContext.getScene().getWindow();
        stage1.close();
        URL resource = getClass().getResource(URI);
        Parent load = FXMLLoader.load(resource);
        Scene scene = new Scene(load);
        Stage stage2= new Stage();
        stage2.setTitle("Hostel Management System");
        Image image = new Image("lk/ijse/hibernate/assessts/images/ogimage1.png");
        stage2.getIcons().add(image);
        stage2.setScene(scene);
        stage2.centerOnScreen();
        stage2.show();
    }
}

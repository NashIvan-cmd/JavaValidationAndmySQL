package com.example.finalsprogtools;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountListController {

    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, String> nameColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> ageColumn;
    Scene scene;
    FXMLLoader fxmlLoader;
    Stage stage;

    public static class User {
        private String givenName;
        private String surname;
        private String age;
        private String email;

        // Add getters for TableView
        public String getFullName() {
            return givenName + " " + surname;
        }

        public String getEmail() {
            return email;
        }

        public String getAge() {
            return age;
        }
    }

    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName")); // Assumes a getter
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));

        List<User> users = fetchUsers();
        userTable.setItems(FXCollections.observableArrayList(users));
    }
    public List<User> fetchUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT given_name, surname, age, email, birthday FROM user order by id desc";
        try (Connection conn = DatabaseConn.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.givenName = rs.getString("given_name");
                user.surname = rs.getString("surname");
                user.age = rs.getString("age");
                user.email = rs.getString("email");
                users.add(user);
            }

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
        return users;
    }

    @FXML
    protected void onChangeSceneButtonClick(ActionEvent event) {
        try {
            switchToRegistrationScene(event);
        } catch (IOException e) {
            System.out.println("Failed to switch scenes" + e.getMessage());
            e.fillInStackTrace();
        }
    }

    public void switchToRegistrationScene(ActionEvent event) throws IOException {
        fxmlLoader = new FXMLLoader(getClass().getResource("Registration.fxml"));
        scene = new Scene(fxmlLoader.load());

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}

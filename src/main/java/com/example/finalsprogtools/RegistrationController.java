package com.example.finalsprogtools;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Function;
import java.util.List;

public class RegistrationController {

    @FXML
    private Label givenNameLabel;
    @FXML
    private TextField givenNameField;
    @FXML
    private Label surnameLabel;
    @FXML
    private TextField surnameField;
    @FXML
    private Label emailLabel;
    @FXML
    private TextField emailField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Label ageLabel;
    @FXML
    private TextField ageField;
    @FXML
    private Label psLabel;
    @FXML
    private TextField psField;
    @FXML
    private Label errorLabel;
    @FXML
    private Label confirmPsLabel;
    @FXML
    private TextField confirmPsField;

    boolean isGivenNameValid = false;
    boolean isEmailValid = false;
    boolean isSurnameValid = false;
    boolean isAgeValid = false;
    boolean isPasswordValid = false;
    //boolean isPasswordMatched = false;
    boolean isDateValid = false;

    private void applyStyle(Node node, boolean isValid) {
        String defaultStyle = "-fx-background-color: transparent; " +
                "-fx-text-fill: white; " +
                "-fx-border-width: 0 0 1 0; " +
                "-fx-border-color: #CCCCCC; " +  // Default gray border
                "-fx-padding: 5 0;";

        String validStyle = "-fx-background-color: transparent; " +
                "-fx-text-fill: white; " +
                "-fx-border-width: 0 0 1 0; " +
                "-fx-border-color: #00FF00; " +  // Green border
                "-fx-padding: 5 0;";

        String invalidStyle = "-fx-background-color: transparent; " +
                "-fx-text-fill: white; " +
                "-fx-border-width: 0 0 1 0; " +
                "-fx-border-color: #FF0000; " +  // Red border
                "-fx-padding: 5 0;";

        if (node instanceof TextField && ((TextField) node).getText().isEmpty()) {
            node.setStyle(defaultStyle);
        } else if (node instanceof DatePicker && ((DatePicker) node).getValue() == null) {
            node.setStyle(defaultStyle);
        } else {
            node.setStyle(isValid ? validStyle : invalidStyle);
        }
    }

    // Custom exception
    public class InvalidFormDataException extends Exception {
        private final List<String> errors;
        public InvalidFormDataException(List<String> errors) {
            super(String.join("\n", errors));
            this.errors = errors;
        }
        public List<String> getErrors() {
            return errors;
        }
    }
    private void setupValidationListener(TextField field, Function<String, Boolean> validationFunction) {
        field.textProperty().addListener((observable, oldValue, newValue) ->
                validationFunction.apply(newValue));
    }

    @FXML
    public void initialize() {
        setupValidationListener(emailField, this::validateEmail);
        setupValidationListener(givenNameField, this::validateGivenName);
        setupValidationListener(surnameField, this::validateSurnameField);
//        ageField.textProperty().addListener((observable, oldValue, newValue) ->  {
//          validateAge(Integer.valueOf(newValue));
//        });
        setupValidationListener(ageField, this::validateAge);
        setupValidationListener(psField, this::validatePassword);
    }

    private  boolean validateGivenName(String givenName) {
        String givenNameRegex = "^[a-zA-Z]+(\\s[a-zA-Z]+)*$";
        isGivenNameValid = givenName != null && !givenName.trim().isEmpty() && givenName.matches(givenNameRegex);
        applyStyle(givenNameField, isGivenNameValid);
        givenNameLabel.setText(isGivenNameValid ? "Given Name ✔" : "Invalid Name ❌");
        if(givenName == null || givenName.trim().isEmpty()) {
            givenNameLabel.setText("Given Name");
        }

        return isGivenNameValid;
    }
    private  boolean validateSurnameField(String surname) {
        String givenNameRegex = "^[a-zA-Z]+(\\s[a-zA-Z]+)*$";
        isSurnameValid = surname != null && surname.matches(givenNameRegex);
        applyStyle(surnameField, isSurnameValid);
        surnameLabel.setText(isSurnameValid ? "Surname Name ✔" : "Invalid Name ❌");
        if(surname == null || surname.trim().isEmpty()) {
            surnameLabel.setText("Surname");
        }

        return isSurnameValid;
    }
    private boolean validateEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";

        String query = "Select email from user";
        try(Connection conn = DatabaseConn.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
        ) {

            while (rs.next()) {
                String dbMail = rs.getString("email");
                if (dbMail.equals(email)) {
                    emailLabel.setText("Email is already used");
                    applyStyle(emailField, false);
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error" + e.getMessage());
        }
        isEmailValid = email != null && !email.trim().isEmpty() && email.matches(emailRegex);
        applyStyle(emailField, isEmailValid);
        emailLabel.setText(isEmailValid ? "Email" : "Invalid email format");
        if(email == null || email.trim().isEmpty()) {
            emailLabel.setText("Surname");
        }

        return isEmailValid;
    }

    private boolean validateDatePicker(LocalDate date) {
        return date != null &&
                Period.between(date, LocalDate.now()).getYears() >= 18;
    }

    private boolean validateAge(String age) {
        String ageRegex = "^[0-9]+$";
        isAgeValid = age != null && Integer.parseInt(age) >= 18 && Integer.parseInt(age) <= 100 && age.matches(ageRegex);
        applyStyle(ageField, isAgeValid);
        ageLabel.setText(isAgeValid ? "Age" : "Age must range 18 - 100");
        if (age == null || age.trim().isEmpty()) {
            ageLabel.setText("Age");
        }

        return isAgeValid;
    }

    private boolean validatePassword(String ps) {
        String passRegex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{7,}$";
        List<String> pwRequirements = new ArrayList<>();

        // First check if password is valid
        isPasswordValid = ps != null && ps.matches(passRegex);
        applyStyle(psField, isPasswordValid);

        // If password is valid, we don't need to check individual requirements
        if (isPasswordValid) {
            psLabel.setText("Valid Password");  // Clear the label
            return true;
        }

        if(ps == null || ps.trim().isEmpty()) {
            psLabel.setText("Password");

        }

        // Only check individual requirements if password is not null and not valid
        if (ps != null) {
            // Check for uppercase letter
            if (!ps.matches(".*[A-Z].*")) {
                pwRequirements.add("Must contain 1 uppercase letter.");
            }

            // Check for digit
            if (!ps.matches(".*\\d.*")) {
                pwRequirements.add("Must contain at least 1 Digit.");
            }

            // Check for special character
            if (!ps.matches(".*[@$!%*?&].*")) {
                pwRequirements.add("Must contain at least 1 Special Character.");
            }

            // Check for length
            if (ps.length() < 7) {
                pwRequirements.add("At least more than 6 characters.");
            }
        }

        // Set the label text with requirements
        psLabel.setText(String.join("\n", pwRequirements));

        return false;
    }

    private boolean confirmedPassword(String confirmPs) {
        return Objects.equals(confirmPs, psField.getText());
    }
    private boolean validateFormData() throws InvalidFormDataException {
        List<String> errors = new ArrayList<>();
        String givenName = givenNameField.getText();
        String email = emailField.getText();
        String surname = surnameField.getText();
        String age = ageField.getText();
        String pw = psField.getText();
        String psConfirmVal = confirmPsField.getText();
        LocalDate date = datePicker.getValue();

        // Add other validations here
        if (!validateGivenName(givenName)) {
            errors.add("Given name is required");
        }

        if(!validateSurnameField(surname)) {
            errors.add("Surname is required");
        }

        if (!validateEmail(email)) {
            errors.add("Invalid email address");
        }

        if(!validateAge(age)) {
            errors.add("Invalid age");
        }

        if (!validatePassword(pw)) {
            errors.add("Invalid password");
        }

        if(!validateDatePicker(date)) {
            errors.add("Invalid Birthdate");
        }

        if(!confirmedPassword(psConfirmVal)) {
            errors.add("Password do not match");
        }
        // If any errors were found, throw the exception with all errors
        if (!errors.isEmpty()) {
            throw new InvalidFormDataException(errors);
        }

        return true;
    }
    @FXML
    protected void onSubmitButtonClick(ActionEvent event) {
        try {
            boolean isPassed = validateFormData();
            if(isPassed) {
                postRequestToDatabase();
                applyStyle(errorLabel, true);
                errorLabel.setText("Successful Insertion.");
                onChangeSceneButtonClick(event);
            }
        } catch (InvalidFormDataException e) {
            errorLabel.setText(e.getMessage());
            applyStyle(errorLabel, false);
        }
    }

    private void postRequestToDatabase() {
        try (Connection conn = DatabaseConn.getConnection()) {
            String sql = "Insert into user (given_name, surname, email, birthday, age, password) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.setString(1, givenNameField.getText());
                preparedStatement.setString(2, surnameField.getText());
                preparedStatement.setString(3, emailField.getText());
                preparedStatement.setObject(4, datePicker.getValue());
                preparedStatement.setInt(5, Integer.parseInt(ageField.getText()));
                preparedStatement.setString(6, psField.getText());
                preparedStatement.executeUpdate();

            }
        } catch (SQLException e) {
            System.out.println("Sql Error" + e.getMessage());
            errorLabel.setText("Fail Connection, Please try again later.");
        }
    }

    @FXML
    protected void onChangeSceneButtonClick(ActionEvent event) {
        try {
            switchToAccountScene(event);
        } catch (IOException e) {
            System.out.println("Error loading scene: " + e.getMessage());
            e.fillInStackTrace();
        }
    }
    Stage stage;
    Scene scene;
    private FXMLLoader fxmlLoader;
    public void switchToAccountScene(ActionEvent event) throws IOException {
        // Create and load the FXML in one step
        fxmlLoader = new FXMLLoader(getClass().getResource("AccountList.fxml"));
        scene = new Scene(fxmlLoader.load());
        // Get the stage and set the new scene
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setTitle("Account List");
        stage.setScene(scene);
        stage.show();
    }

}
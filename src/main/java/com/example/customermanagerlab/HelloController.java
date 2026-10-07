package com.example.customermanagerlab;
// Keep the 'package ...;' line IntelliJ generates above this comment.

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class HelloController {

    private static final List<String> PROVINCES = List.of(
            "Central", "Copperbelt", "Eastern", "Luapula", "Lusaka",
            "Muchinga", "Northern", "North-Western", "Southern", "Western");

    private static final int MAX_NAME_LENGTH = 50;
    // Starts with a letter; then letters, spaces, dots, apostrophes or hyphens
    private static final Pattern NAME_PATTERN = Pattern.compile("\\p{L}[\\p{L} .'-]*");

    // ---------- Controls injected from hello-view.fxml ----------
    @FXML private Label nameLabel, provinceLabel, messageLabel, countLabel;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> provinceBox;
    @FXML private Button deleteBtn;
    @FXML private TableView<Customer> table;
    @FXML private TableColumn<Customer, String> colName, colProvince;

    // 2. The data behind the table
    private final ObservableList<Customer> customers = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        provinceBox.getItems().addAll(PROVINCES);

        // Alt+N and Alt+P jump to the matching field
        nameLabel.setLabelFor(nameField);
        provinceLabel.setLabelFor(provinceBox);

        table.setItems(customers);
        colName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getName()));
        colProvince.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getProvince()));

        // Delete only works when a row is selected; Delete key also works in the table
        deleteBtn.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());
        table.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DELETE) onDeleteCustomer();
        });

        countLabel.textProperty().bind(Bindings.size(customers).asString("Customers: %d"));

        // Clear the old message as soon as the user starts correcting
        nameField.textProperty().addListener((obs, oldV, newV) -> messageLabel.setText(""));
        provinceBox.valueProperty().addListener((obs, oldV, newV) -> messageLabel.setText(""));

        Platform.runLater(nameField::requestFocus);
    }

    // ---------- 4. Validate input, then add the customer ----------
    @FXML
    private void onAddCustomer() {
        String name = nameField.getText().trim().replaceAll("\\s+", " ");
        String province = provinceBox.getValue();

        if (name.isEmpty()) {
            fail("Please enter the customer's name.", nameField);
            return;
        }
        if (name.length() > MAX_NAME_LENGTH) {
            fail("Name must be " + MAX_NAME_LENGTH + " characters or fewer.", nameField);
            return;
        }
        if (!NAME_PATTERN.matcher(name).matches()) {
            fail("Name must start with a letter and use only letters, spaces, . ' or -", nameField);
            return;
        }
        if (province == null) {
            fail("Please choose a province.", provinceBox);
            return;
        }
        boolean duplicate = customers.stream().anyMatch(
                c -> c.getName().equalsIgnoreCase(name) && c.getProvince().equals(province));
        if (duplicate) {
            fail("That customer is already in the list.", nameField);
            return;
        }

        Customer added = new Customer(name, province);
        customers.add(added);
        table.scrollTo(added);

        nameField.clear();                // keep the province: handy when adding several people
        showMessage("Added " + name + ".", false);
        nameField.requestFocus();
    }

    // ---------- 5. Confirm deletion of the selected customer ----------
    @FXML
    private void onDeleteCustomer() {
        Customer selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete " + selected + "?\nThis cannot be undone.");
        confirm.setTitle("Confirm deletion");
        confirm.setHeaderText(null);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            customers.remove(selected);
            showMessage("Deleted " + selected.getName() + ".", false);
        }
    }

    // ---------- Helpers ----------
    private void fail(String text, Node fieldToFocus) {
        showMessage(text, true);
        fieldToFocus.requestFocus();
    }

    private void showMessage(String text, boolean isError) {
        messageLabel.setStyle("-fx-text-fill: " + (isError ? "#c0392b" : "#1e8449") + ";");
        messageLabel.setText(text);
    }
}

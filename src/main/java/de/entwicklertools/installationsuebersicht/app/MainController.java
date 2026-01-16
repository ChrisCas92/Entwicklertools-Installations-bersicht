package de.entwicklertools.installationsuebersicht.app;

import de.entwicklertools.installationsuebersicht.model.FormData;
import de.entwicklertools.installationsuebersicht.model.SoftwareEntry;
import de.entwicklertools.installationsuebersicht.service.ExportService;
import de.entwicklertools.installationsuebersicht.service.SoftwareCatalogService;
import de.entwicklertools.installationsuebersicht.service.UserDataStorage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class MainController {
    private static final Logger LOGGER = LogManager.getLogger(MainController.class);
    private static final List<String> YES_NO = List.of("Ja", "Nein");
    private static final List<String> REQUIRED_CHOICES = List.of("Benötigt", "Nicht benötigt");
    private static final List<String> REFERAT_CHOICES = List.of("Ref.92", "Ref.93");

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField windowsIdField;
    @FXML
    private TextField deviceNameField;
    @FXML
    private ComboBox<String> referatBox;
    @FXML
    private TableView<SoftwareEntry> softwareTable;
    @FXML
    private TableColumn<SoftwareEntry, String> nameColumn;
    @FXML
    private TableColumn<SoftwareEntry, String> vendorColumn;
    @FXML
    private TableColumn<SoftwareEntry, String> installedColumn;
    @FXML
    private TableColumn<SoftwareEntry, String> installedVersionColumn;
    @FXML
    private TableColumn<SoftwareEntry, String> requiredColumn;
    @FXML
    private TableColumn<SoftwareEntry, String> licenseColumn;
    @FXML
    private Button saveLocalButton;
    @FXML
    private Button exportCsvButton;
    @FXML
    private Button exportExcelButton;
    @FXML
    private Button exportPdfButton;

    private final SoftwareCatalogService catalogService = new SoftwareCatalogService();
    private final ExportService exportService = new ExportService();
    private final UserDataStorage storage = new UserDataStorage(Path.of(System.getProperty("user.home"), ".entwicklertools-installationsuebersicht"));
    private final ObservableList<SoftwareEntry> tableData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        referatBox.getItems().setAll(REFERAT_CHOICES);
        windowsIdField.setText(System.getProperty("user.name"));
        windowsIdField.setEditable(false);
        setupTable();
        loadCatalog();
        loadUserData();
    }

    private void setupTable() {
        nameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
        vendorColumn.setCellValueFactory(cell -> cell.getValue().vendorProperty());
        installedColumn.setCellValueFactory(cell -> cell.getValue().installedProperty());
        installedVersionColumn.setCellValueFactory(cell -> cell.getValue().installedVersionProperty());
        requiredColumn.setCellValueFactory(cell -> cell.getValue().requiredProperty());
        licenseColumn.setCellValueFactory(cell -> cell.getValue().licenseRequiredProperty());

        installedColumn.setCellFactory(ChoiceBoxTableCell.forTableColumn(FXCollections.observableArrayList(YES_NO)));
        requiredColumn.setCellFactory(ChoiceBoxTableCell.forTableColumn(FXCollections.observableArrayList(REQUIRED_CHOICES)));
        licenseColumn.setCellFactory(ChoiceBoxTableCell.forTableColumn(FXCollections.observableArrayList(YES_NO)));
        installedVersionColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        installedColumn.setOnEditCommit(event -> event.getRowValue().setInstalled(event.getNewValue()));
        requiredColumn.setOnEditCommit(event -> event.getRowValue().setRequired(event.getNewValue()));
        licenseColumn.setOnEditCommit(event -> event.getRowValue().setLicenseRequired(event.getNewValue()));
        installedVersionColumn.setOnEditCommit(event -> event.getRowValue().setInstalledVersion(event.getNewValue()));

        softwareTable.setItems(tableData);
        softwareTable.setEditable(true);
    }

    private void loadCatalog() {
        List<SoftwareEntry> catalog = catalogService.loadSoftwareCatalog();
        tableData.setAll(catalog);
    }

    private void loadUserData() {
        String windowsId = windowsIdField.getText();
        Optional<FormData> existing = storage.loadIfExists(windowsId, tableData);
        if (existing.isEmpty()) {
            return;
        }
        FormData data = existing.get();
        firstNameField.setText(data.getFirstName());
        lastNameField.setText(data.getLastName());
        deviceNameField.setText(data.getDeviceName());
        referatBox.setValue(data.getReferat());
        tableData.setAll(data.getSoftwareEntries());
        LOGGER.info("Loaded existing data for {}", windowsId);
    }

    @FXML
    public void handleSaveLocal(ActionEvent event) {
        FormData data = gatherFormData();
        storage.save(data);
        LOGGER.info("Local CSV saved for {}", data.getWindowsId());
    }

    @FXML
    public void handleExportCsv(ActionEvent event) {
        Path target = chooseFile("CSV", "*.csv");
        if (target != null) {
            exportService.exportCsv(gatherFormData(), target);
        }
    }

    @FXML
    public void handleExportExcel(ActionEvent event) {
        Path target = chooseFile("Excel", "*.xlsx");
        if (target != null) {
            exportService.exportExcel(gatherFormData(), target);
        }
    }

    @FXML
    public void handleExportPdf(ActionEvent event) {
        Path target = chooseFile("PDF", "*.pdf");
        if (target != null) {
            exportService.exportPdf(gatherFormData(), target);
        }
    }

    private FormData gatherFormData() {
        FormData data = new FormData();
        data.setFirstName(firstNameField.getText());
        data.setLastName(lastNameField.getText());
        data.setWindowsId(windowsIdField.getText());
        data.setDeviceName(deviceNameField.getText());
        data.setReferat(referatBox.getValue());
        data.getSoftwareEntries().addAll(tableData);
        return data;
    }

    private Path chooseFile(String type, String pattern) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(type + " exportieren");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(type, pattern));
        if (softwareTable.getScene() == null) {
            return null;
        }
        var file = chooser.showSaveDialog(softwareTable.getScene().getWindow());
        return file == null ? null : file.toPath();
    }
}

package de.entwicklertools.installationsuebersicht.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SoftwareEntry {
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty vendor = new SimpleStringProperty();
    private final StringProperty installed = new SimpleStringProperty();
    private final StringProperty installedVersion = new SimpleStringProperty();
    private final StringProperty required = new SimpleStringProperty();
    private final StringProperty licenseRequired = new SimpleStringProperty();

    public SoftwareEntry(String name, String vendor) {
        this.name.set(name);
        this.vendor.set(vendor);
        this.installed.set("");
        this.installedVersion.set("");
        this.required.set("");
        this.licenseRequired.set("");
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty vendorProperty() {
        return vendor;
    }

    public StringProperty installedProperty() {
        return installed;
    }

    public StringProperty installedVersionProperty() {
        return installedVersion;
    }

    public StringProperty requiredProperty() {
        return required;
    }

    public StringProperty licenseRequiredProperty() {
        return licenseRequired;
    }

    public String getName() {
        return name.get();
    }

    public String getVendor() {
        return vendor.get();
    }

    public String getInstalled() {
        return installed.get();
    }

    public void setInstalled(String value) {
        installed.set(value);
    }

    public String getInstalledVersion() {
        return installedVersion.get();
    }

    public void setInstalledVersion(String value) {
        installedVersion.set(value);
    }

    public String getRequired() {
        return required.get();
    }

    public void setRequired(String value) {
        required.set(value);
    }

    public String getLicenseRequired() {
        return licenseRequired.get();
    }

    public void setLicenseRequired(String value) {
        licenseRequired.set(value);
    }
}

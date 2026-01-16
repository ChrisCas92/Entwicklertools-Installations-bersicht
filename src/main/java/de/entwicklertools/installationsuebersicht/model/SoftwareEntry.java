package de.entwicklertools.installationsuebersicht.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SoftwareEntry {
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty system = new SimpleStringProperty();
    private final StringProperty installedVersion = new SimpleStringProperty();
    private final StringProperty required = new SimpleStringProperty();
    private final StringProperty licenseRequired = new SimpleStringProperty();
    private final StringProperty comment = new SimpleStringProperty();

    public SoftwareEntry(String name) {
        this.name.set(name);
        this.system.set("");
        this.installedVersion.set("");
        this.required.set("");
        this.licenseRequired.set("");
        this.comment.set("");
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty systemProperty() {
        return system;
    }

    public StringProperty commentProperty() {
        return comment;
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

    public String getsystem() {
        return system.get();
    }

    public String getComment() {
        return comment.get();
    }

    public void setComment(String value) {
        comment.set(value);
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

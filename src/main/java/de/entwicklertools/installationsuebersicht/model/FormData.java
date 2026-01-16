package de.entwicklertools.installationsuebersicht.model;

import java.util.ArrayList;
import java.util.List;

public class FormData {
    private String firstName;
    private String lastName;
    private String windowsId;
    private String deviceName;
    private String referat;
    private final List<SoftwareEntry> softwareEntries = new ArrayList<>();

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getWindowsId() {
        return windowsId;
    }

    public void setWindowsId(String windowsId) {
        this.windowsId = windowsId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getReferat() {
        return referat;
    }

    public void setReferat(String referat) {
        this.referat = referat;
    }

    public List<SoftwareEntry> getSoftwareEntries() {
        return softwareEntries;
    }
}

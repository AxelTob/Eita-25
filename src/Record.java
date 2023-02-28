package src;
import java.util.ArrayList;
import java.util.List;

import src.Enums.Permission;
import src.Enums.Role;

public class Record {
    private final User patient;
    private final List<User> nurses;
    private final List<User> doctors;
    private final String department;
    private String content;

    public Record(User patient, List<User> nurses, List<User> doctors, String department, String content) {
        this.patient = patient;
        this.nurses = nurses == null ? null : new ArrayList<>(nurses);
        this.doctors = new ArrayList<>(doctors);
        this.department = department;
        this.content = content;
    }

    public User getPatient() {
        return patient;
    }

    public List<User> getNurses() {
        return nurses;
    }

    public List<User> getDoctors() {
        return doctors;
    }

    public String getDepartment() {
        return department;
    }

    public String getContent(User user) {
        if (!isAuthorized(user, Permission.READ)) {
            return null;
        }
        return content;
    }

    public void setContent(User user, String content) {
        if (!isAuthorized(user, Permission.WRITE)) {
            return;
        }
        this.content = content;
    }

    public void delete(User user) {
        if (!isAuthorized(user, Permission.DELETE)) {
            return;
        }
        content = null;
    }

    private boolean isAuthorized(User user, Permission permission) {
        boolean authorized = false;
        switch (user.getRole()) {
            case GOVERNMENT:
                authorized = permission == Permission.DELETE || permission == Permission.READ;
                break;
            case PATIENT:
                authorized = permission == Permission.READ && user.equals(patient);
                break;
            case NURSE:
                authorized = (nurses != null && nurses.contains(user)) && (permission == Permission.READ || permission == Permission.WRITE);
                break;
            case DOCTOR:
                authorized = (doctors.contains(user) && (permission == Permission.READ || permission == Permission.WRITE))
                        || (user.getDepartment() != null && user.getDepartment().equals(department) && permission == Permission.READ);
                break;
        }
        return authorized;
    }
    
}

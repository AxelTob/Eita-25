package test;

import java.util.List;

import src.Record;
import src.RecordSystem;
import src.Role;
import src.Setup;
import src.User;


public class MainWriteTest {
    public static void main(String[] args) {
        Setup.run();
        List<User> users = Setup.users;
        RecordSystem recordSystem = Setup.recordSystem;
        
        User patient1 = null; User nurse1 = null; User doctor1 = null;
        for (User user : users) {
            if (user.getRole() == Role.PATIENT) {
                patient1 = user;
            }
            if (user.getRole() == Role.NURSE) {
                nurse1 = user;
            }
            if (user.getRole() == Role.DOCTOR) {
                doctor1 = user;
            }
            if (patient1 != null && nurse1 != null && doctor1 != null) {
                break;
            }
        }

        Record record1 = new Record(patient1, List.of(nurse1), List.of(doctor1), "department1", "Test save to file - 3");
        recordSystem.addRecord(record1);
    
        // Save the records to a file
        recordSystem.saveToFile();
    }
    
}


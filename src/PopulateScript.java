package src;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import src.Enums.Role;

public class PopulateScript {
    private static final String USERS_FILENAME = Paths.get("src","files", "users.txt").toAbsolutePath().toString();
    private static final String RECORDS_FILENAME = Paths.get("src","files", "records.txt").toAbsolutePath().toString();

    public static void main(String[] args) {
        List<User> users = loadUsersFromFile(USERS_FILENAME);
        List<Record> records = loadRecordsFromFile(RECORDS_FILENAME, users);
        RecordSystem recordSystem = new RecordSystem();
        for (Record record : records) {
            recordSystem.addRecord(record);
        }
        User user = findUserByName(users, "patient1");
        List<Record> authorizedRecords = recordSystem.getRecords(user);
        for (Record record : authorizedRecords) {
            System.out.println(record.getContent(user));
        }
    }

    private static List<User> loadUsersFromFile(String filename) {
        List<User> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                String name = fields[0];
                Role role = Role.valueOf(fields[1]);
                String department = fields.length > 2 ? fields[2] : null;
                User user = new User(name, role, department);
                users.add(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    private static User findUserByName(List<User> users, String name) {
        for (User user : users) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
        return null;
    }

    private static List<Record> loadRecordsFromFile(String filename, List<User> users) {
        List<Record> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                String patientName = fields[0];
                List<User> nurses = new ArrayList<>();
                for (String nurseName : fields[1].split("-")) {
                    User nurse = findUserByName(users, nurseName);
                    if (nurse != null) {
                        nurses.add(nurse);
                    }
                }
                List<User> doctors = new ArrayList<>();
                for (String doctorName : fields[2].split("-")) {
                    User doctor = findUserByName(users, doctorName);
                    if (doctor != null) {
                        doctors.add(doctor);
                    }
                }
                String department = fields[3];
                String content = fields[4];
                User patient = findUserByName(users, patientName);
                if (patient != null) {
                    Record record = new Record(patient, nurses, doctors, department, content);
                    records.add(record);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }
}

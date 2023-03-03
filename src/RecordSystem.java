package src;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class RecordSystem {
    private List<Record> records;

    public RecordSystem() {
        this.records = new ArrayList<>();
    }

    public void addRecord(Record record) {
        records.add(record);
    }

    public Record getRecord(User user, String recordID){        //NY
        for (Record record : records) {
            if (record.getContent(user) != null && record.getRecordID().equals(recordID)) {
                return record;
            }
        }
        return null;
    }

    public boolean deleteRecord(User user, String recordID){        //NY
        boolean removed = false;
        Record deleteRec = getRecord(user, recordID);
        if (deleteRec != null) {
            deleteRec.delete(user);
            if(deleteRec.getContent(user) == null){     //Seems unecessary, but it checks privilege to delete. Quite ugly
                removed = records.remove(deleteRec);
            }
        }
        return removed;
    }

    public List<Record> getRecords(User user) {
        List<Record> authorizedRecords = new ArrayList<>();
        for (Record record : records) {
            if (record.getContent(user) != null) {
                authorizedRecords.add(record);
            }
        }
        return authorizedRecords;
    }

    public void saveToFile() {
        String filename = Paths.get("src","files", "records.txt").toAbsolutePath().toString();
        List<Record> alreadyWritten = new ArrayList<>();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Record record : records) {
                for (User user : Setup.users) {
                    String content = record.getContent(user);
                    if (content != null && !alreadyWritten.contains(record)) {
                        writer.write(record.getPatient().getName() + ",");
                        writer.write(record.getRecordID() + ",");
                        writer.write(getUserListString(record.getNurses()) + ",");
                        writer.write(getUserListString(record.getDoctors()) + ",");
                        writer.write(record.getDepartment() + ",");
                        writer.write(content);
                        writer.newLine();
                        alreadyWritten.add(record);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getUserListString(List<User> users) {
        if (users == null || users.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (User user : users) {
            sb.append(user.getName());
            sb.append("-");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}

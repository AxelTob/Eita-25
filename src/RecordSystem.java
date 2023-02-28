package src;
import java.util.ArrayList;
import java.util.List;

public class RecordSystem {
    private final List<Record> records;

    public RecordSystem() {
        this.records = new ArrayList<>();
    }

    public void addRecord(Record record) {
        records.add(record);
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
}

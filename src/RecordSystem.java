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
}

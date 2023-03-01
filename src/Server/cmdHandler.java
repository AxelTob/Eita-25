package src.Server;

import src.RecordSystem;
import src.User;
import src.Enums.Role;
import src.Record;

public class cmdHandler {
    
    RecordSystem records;
    User user;

    
    public cmdHandler(User user, RecordSystem records){

        this.records = records;
        this.user = user;
    }
    
    public String handle(String msg){
        String[] input = msg.split(" ");
            switch (input[0]) {

                case "list":
                    StringBuilder string = new StringBuilder();
                    for(Record r : records.getRecords(user)){
                        string.append(r.getRecordID() + "/n");
                    }        
                    return string.toString();


                case "read":
                    if (input.length != 2) {
                        return "Usage: read [record id]";
                    }
                    Record rec = records.getRecord(user, input[1]);
                    if (rec != null && rec.getContent(user) == null) {
                        return "You do not have permission for this action, or the record does not exist.";
                    }
                    return rec.getContent(user);



                case "write":
                    if (input.length < 3) {
                        return "Usage: write [record id] [info]";
                    }

                    rec = records.getRecord(user, input[1]);

                    if (rec!= null) {
                        StringBuilder sb = new StringBuilder();

                        sb.append(input[2]);                //ADD AS A FOR-LOOP, ALL AFTER RECORD ID
                        rec.setContent(user, sb.toString());
                        return "Record updated.";
                    } else {
                        return "You do not have permission for this action, or the record does not exist.";
                    }

                case "create":      //IMPLEMENT
                if (input.length < 6) return "Usage: create [Record ID] [patient] [division name] [nurse name] [info]";
                    if(user.getRole() != Role.DOCTOR){
                        return "You do not have permission for this action.";
                    }

                    String recordID = input[1];
                    String patient = input[2];
                    String divisionName = input[3];
                    String nurseName = input[4];
                    StringBuilder info = new StringBuilder();
                    

                    for(int i = 5; i < input.length; i++){
                        info.append(input[i] + " ");
                    }
                    //User patient, String recordID, List<User> nurses, List<User> doctors, String department, String content
                    //Record newRecord = new Record(patient, recordID, nurseName, divisionName, info.toString());
                    records.addRecord(newRecord);
                    return "New record was added.";

                case "delete":      //DONE?
                    if (input.length != 2) {
                        return "Usage: delete [record id]";
                    }
                    rec = records.getRecord(user, input[1]);

                    if (rec!= null) {
                        rec.delete(user);
                        return "Record deleted";
                    } else {
                        return "The record does not exist.";
                    }

                default:
                    return "No such command.";

    }
    
}
}
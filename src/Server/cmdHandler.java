package src.Server;

import src.RecordSystem;
import src.Setup;
import src.User;
import src.Enums.Role;

import java.util.ArrayList;
import java.util.List;

import src.Record;

public class cmdHandler {
    
    RecordSystem records;
    User user;
    List<User> userList;

    
    public cmdHandler(User user, RecordSystem records, List<User> userList){

        this.records = records;
        this.user = user;
        this.userList = userList;
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
                        for(int i = 2; i < input.length; i++){
                            sb.append(input[i] + " ");
                        }
                        rec.setContent(user, sb.toString());
                        return "Record updated.";
                    } else {
                        return "You do not have permission for this action, or the record does not exist.";
                    }

                case "create":
                if (input.length < 6) return "Usage: create [new Record ID] [patient] [division name] [nurse name] [info]";
                    if(user.getRole() != Role.DOCTOR){
                        return "You do not have permission for this action.";
                    }

                    String recordID = input[1];
                    String patient = input[2];
                    String divisionName = input[3];
                    String nurseName = input[4];
                    StringBuilder info = new StringBuilder();

                    User patientFormatted = Setup.findUserByName(userList, patient);
                    User nurseFormatted = Setup.findUserByName(userList, nurseName);

                    if(patientFormatted == null || nurseFormatted == null){
                        return "Nurse or patient does not exist. Try again.";
                    }

                    List<User> nurseList = new ArrayList<User>();
                    List<User> doctor = new ArrayList<User>();

                    nurseList.add(nurseFormatted);
                    doctor.add(user);

                    for(int i = 5; i < input.length; i++){
                        info.append(input[i] + " ");
                    }

                    Record newRecord = new Record(Setup.findUserByName(userList, patient), recordID, nurseList, doctor, divisionName, info.toString());
                    records.addRecord(newRecord);
                    return "New record was added.";


                case "delete":
                    if (input.length != 2) {
                        return "Usage: delete [record id]";
                    }
                    if (records.deleteRecord(user, input[1])) {
                        return "Record deleted";
                    } else {
                        return "You do not have permission for this action, or the record does not exist.";
                    }


                default:
                    return "No such command.";

    }
    
}
}
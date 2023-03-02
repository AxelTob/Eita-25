package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//import src.Enums.Role;
import src.RecordSystem;
import src.Role;
import src.User;
import src.Record;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class RecordSystemTest {

    private RecordSystem recordSystem;
    private User patient1, patient2, nurse1, nurse2, doctor1, doctor2, government;
    private Record record1, record2, record3;

    @BeforeEach
    void setUp() {
        // Create users
        patient1 = new User("patient1", Role.PATIENT, null);
        patient2 = new User("patient2", Role.PATIENT, null);
        nurse1 = new User("nurse1", Role.NURSE, "department1");
        nurse2 = new User("nurse2", Role.NURSE, "department2");
        doctor1 = new User("doctor1", Role.DOCTOR, "department1");
        doctor2 = new User("doctor2", Role.DOCTOR, "department2");
        government = new User("government", Role.GOVERNMENT, null);

        // Create records
        record1 = new Record(patient1, "1", List.of(nurse1), List.of(doctor1, doctor2), "department1", "Record 1");
        record2 = new Record(patient1, "2", List.of(nurse1, nurse2), List.of(doctor1), "department1", "Record 2");
        record3 = new Record(patient2, "3", List.of(nurse2), List.of(doctor2), "department2", "Record 3");

        // Create record system
        recordSystem = new RecordSystem();
        recordSystem.addRecord(record1);
        recordSystem.addRecord(record2);
        recordSystem.addRecord(record3);
    }

    @Test
    void testPatientCanReadOwnRecord() {
        String content = record1.getContent(patient1);
        assertEquals("Record 1", content);
    }

    @Test
    void testPatientCannotReadOtherPatientRecord() {
        String content = record1.getContent(patient2);
        assertNull(content);
    }

    @Test
    void testNurseCanReadPatientRecord() {
        String content = record1.getContent(nurse1);
        assertEquals("Record 1", content);
    }

    @Test
    void testNurseCanWriteToPatientRecord() {
        record1.setContent(nurse1, "Updated record 1");
        String content = record1.getContent(nurse1);
        assertEquals("Updated record 1", content);
    }

    @Test
    void testNurseCannotWriteToOtherPatientsRecord() {
        record3.setContent(nurse1, "Updated record 3");
        String content = record3.getContent(nurse1);
        assertNull(content);
    }

    @Test
    void testNurseCannotDeleteRecord() {
        record1.delete(nurse1);
        String content = record1.getContent(nurse1);
        assertNotNull(content);
    }

    @Test
    void testDoctorCanReadPatientRecord() {
        String content = record1.getContent(doctor1);
        assertEquals("Record 1", content);
    }

    @Test
    void testDoctorCanWriteToPatientRecord() {
        record1.setContent(doctor1, "Updated record 1");
        String content = record1.getContent(doctor1);
        assertEquals("Updated record 1", content);
    }

    @Test
    void testDoctorCanCreateNewRecord() {
        Record newRecord = new Record(patient1, "1", List.of(nurse1), List.of(doctor1), "department1", "New record");
        recordSystem.addRecord(newRecord);
        String content = newRecord.getContent(doctor1);
        assertEquals("New record", content);
    }

    @Test
    void testDoctorCanCreateNewRecordWithoutAssociatedNurse() {
        Record newRecord = new Record(patient1, "1", null, List.of(doctor1), "department1", "New record");
        recordSystem.addRecord(newRecord);
        List<Record> authorizedRecords = recordSystem.getRecords(doctor1);
        assertTrue(authorizedRecords.contains(newRecord));
    }

    @Test
    void testDoctorCanCreateNewRecordWithAssociatedNurse() {
        Record newRecord = new Record(patient1, "1", List.of(nurse1), List.of(doctor1), "department1", "New record");
        recordSystem.addRecord(newRecord);
        List<Record> authorizedRecords = recordSystem.getRecords(doctor1);
        assertTrue(authorizedRecords.contains(newRecord));
    }

    @Test
    
    void testDoctorCanReadAllRecordsAssociatedWithDepartment() {
        List<Record> authorizedRecords = recordSystem.getRecords(doctor1);
        assertTrue(authorizedRecords.contains(record1));
        assertTrue(authorizedRecords.contains(record2));
        assertFalse(authorizedRecords.contains(record3));
    }

    @Test
    
    void testGovernmentCanReadAllRecords() {
        List<Record> authorizedRecords = recordSystem.getRecords(government);
        assertTrue(authorizedRecords.contains(record1));
        assertTrue(authorizedRecords.contains(record2));
        assertTrue(authorizedRecords.contains(record3));
    }

    @Test
    
    void testGovernmentCanDeleteAllRecords() {
        record1.delete(government);
        String content = record1.getContent(government);
        assertNull(content);

        record2.delete(government);
        content = record2.getContent(government);
        assertNull(content);

        record3.delete(government);
        content = record3.getContent(government);
        assertNull(content);
    }

}

# Eita25

Project 2 is a demonstrating "real case" of hospital patient record system. 
The project goal is to study and implement security mechanisms that can secure the system and data. 
Different individuals are given access to the records via the server, which is handling the database. In this project database is just a textfile and is not implemented any security mechanisms. 

There are four different types of clients with different access rules: 

- Patient, allowed read only his/her own records

- Nurse, allowed to read&write to all patients records under his/her responsibility
         allowed to read all records associated with the same division

- Doctor, allowed to read&write to all patients records under his/her responsibility
          allowed to read all records associated with the same division
          allowed to create new records to patients under his/her responsibility, nurse must be associated to the patient 

- Government, allowed to read&deletee all types of records





Lets go. Journal system

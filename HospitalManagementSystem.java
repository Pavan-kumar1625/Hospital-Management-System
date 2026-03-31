package HospitalManagementSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class HospitalManagementSystem {

    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "pavan1625";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");  // Corrected syntax for loading MySQL driver
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);

            while (true) {
                System.out.println("HOSPITAL MANAGEMENT SYSTEM ");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.println("Enter your choice: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        // Add patient
                        patient.addPatient();
                        System.out.println();
                        break;  // Corrected break statement
                    case 2:
                        // View patients
                        patient.viewPatients();
                        System.out.println();
                        break;  // Corrected break statement
                    case 3:
                        // View doctors
                        doctor.viewDoctors();
                        System.out.println();
                        break;  // Corrected break statement
                    case 4:
                        // Book appointment
                        bookAppointment(patient, doctor, connection, scanner);
                        System.out.println();
                        break;  // Corrected break statement
                    case 5:
                        return;
                    default:
                        System.out.println("Enter valid choice: ");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner) {
        System.out.print("Enter Patient id: ");
        int patientId = scanner.nextInt();
        System.out.print("Enter Doctor id: ");
        int doctorId = scanner.nextInt();
        System.out.print("Enter Appointment date (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();

        if (patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)) {
            if (checkDoctorAvailability(doctorId, appointmentDate, connection)) {
                String appointmentQuery = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES(?, ?, ?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patientId); // Corrected syntax
                    preparedStatement.setInt(2, doctorId);  // Corrected syntax
                    preparedStatement.setString(3, appointmentDate); // Corrected syntax
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Appointment Booked!");
                    } else {
                        System.out.println("Failed to Book Appointment!");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Doctor not available on this date");
            }
        } else {
            System.out.println("Either doctor or patient doesn't exist!!");
        }
    }

    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection) {
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId); // Corrected syntax
            preparedStatement.setString(2, appointmentDate); // Corrected syntax
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1); // Corrected syntax
                return count == 0;  // Return true if no appointment exists on that date
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

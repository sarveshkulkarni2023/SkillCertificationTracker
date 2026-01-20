package com.skilltracker.main;

import com.skilltracker.dao.CertificationDAO;
import com.skilltracker.dao.SkillDAO;
import com.skilltracker.dao.StudentDAO;
import com.skilltracker.exception.DuplicateSkillException;
import com.skilltracker.exception.ExpiredCertificationException;
import com.skilltracker.model.Certification;
import com.skilltracker.model.Student;
import com.skilltracker.service.CertificationService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;

public class MainApp {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        StudentDAO studentDAO = new StudentDAO();
        SkillDAO skillDAO = new SkillDAO();
        CertificationDAO certDAO = new CertificationDAO();
        CertificationService service = new CertificationService();
        CertificationDAO certDAO = new CertificationDAO();

        while (true) {

<<<<<<< Updated upstream
            System.out.println("\n1.Add Student");
            System.out.println("2.View All Students");
            System.out.println("3.Update Certification");
            System.out.println("4.Find a Student");
            System.out.println("5.View Expired Certificates");
            System.out.println("6.Exit");
=======
            System.out.println("\n===== SKILL TRACKER MENU =====");
            System.out.println("1. Add Student (with Skills & Certification)");
            System.out.println("2. View All Students");
            System.out.println("3. Update Certification Expiry");
            System.out.println("5. Exit");
            System.out.println("6. View Expired Certificates");
           
>>>>>>> Stashed changes
            System.out.print("Choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            try {
                switch (choice) {

                    // -------- ADD STUDENT --------
                    case 1:
                        System.out.print("Name: ");
                        String name = sc.nextLine();

                        System.out.print("Email: ");
                        String email = sc.nextLine();

                        int studentId =
                                studentDAO.addStudentAndReturnId(
                                        new Student(name, email));

                        int count;
                        do {
                            System.out.print("Number of skills (min 1): ");
                            count = sc.nextInt();
                        } while (count <= 0);
                        sc.nextLine();

                        for (int i = 0; i < count; i++) {

                            System.out.print("Skill name: ");
                            String skillName = sc.nextLine();

                            int skillId =
                                    skillDAO.getOrCreateSkill(skillName);

                            System.out.print("Certificate name: ");
                            String certName = sc.nextLine();

<<<<<<< Updated upstream
                            LocalDate expiry = null;
                            System.out.print(
                                "Expiry (yyyy-mm-dd) [Press Enter to skip]: ");
                            String expInput = sc.nextLine();

                            if (!expInput.isEmpty()) {
                                while (true) {
                                    try {
                                        expiry = LocalDate.parse(expInput);
                                        break;
                                    } catch (Exception e) {
                                        System.out.print(
                                            "Invalid date. Re-enter or press Enter to skip: ");
                                        expInput = sc.nextLine();
                                        if (expInput.isEmpty()) {
                                            expiry = null;
                                            break;
                                        }
                                    }
                                }
                            }
=======
                            System.out.print("Expiry (yyyy-mm-dd): ");
                            LocalDate expiry =
                                    LocalDate.parse(sc.nextLine());
>>>>>>> Stashed changes

                            Certification cert =
                                    new Certification(
                                            studentId,
                                            skillId,
                                            certName,
                                            LocalDate.now(),
                                            expiry
                                    );

                            service.assignCertification(cert);
                        }

                        System.out.println("Student added successfully");
                        break;

                    // -------- VIEW ALL --------
                    case 2:
                        certDAO.viewAllStudentDetails();
                        break;

                    // -------- UPDATE EXPIRY --------
                    case 3:
                        System.out.print("Student ID: ");
                        int sid = sc.nextInt();

                        System.out.print("Skill ID: ");
                        int skid = sc.nextInt();
                        sc.nextLine();

<<<<<<< Updated upstream
                        LocalDate newExpiry = null;
                        System.out.print(
                            "New Expiry (yyyy-mm-dd) [Press Enter to remove]: ");
                        String input = sc.nextLine();

                        if (!input.isEmpty()) {
                            newExpiry = LocalDate.parse(input);
                        }

                        certDAO.updateCertificationExpiry(
                            sid, skid, newExpiry);

                        System.out.println("Expiry updated");
                        break;

                    // -------- FIND STUDENT --------
                    case 4:
                        System.out.print("Enter Student ID or Name: ");
                        String search = sc.nextLine();
                        certDAO.findStudent(search);
                        break;
                //------------Find Expired certificates
                    case 5:
                        certDAO.viewExpiredCertificates();
                        break;
                    // -------- EXIT --------
                    case 6:
                        System.out.println("Exiting...");
=======
                        System.out.print("New Expiry (yyyy-mm-dd): ");
                        LocalDate newExp =
                                LocalDate.parse(sc.nextLine());

                        certDAO.updateCertificationExpiry(sid, skid, newExp);
                        System.out.println("Expiry updated successfully");
                        break;

                    case 6:
                        certDAO.viewExpiredCertificates();
                        break;

                    case 5:
                        System.out.println("Exiting application...");
>>>>>>> Stashed changes
                        System.exit(0);

                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }
            catch (DuplicateSkillException |
                   ExpiredCertificationException e) {
                System.out.println("Error: " + e.getMessage());
            }
            catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
            catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
    }
}

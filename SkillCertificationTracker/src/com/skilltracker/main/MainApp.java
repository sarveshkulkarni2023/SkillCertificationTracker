package com.skilltracker.main;

import com.skilltracker.dao.*;
import com.skilltracker.exception.*;
import com.skilltracker.model.*;
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

        while (true) {

            System.out.println("\n1.Add Student (with Skills & Certification)");
            System.out.println("2.View All Students");
            System.out.println("3.Update Certification Expiry");
            System.out.println("4.Find Student (by ID or Name)");
            System.out.println("5.Exit");
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

                    // -------- EXIT --------
                    case 5:
                        System.out.println("Exiting...");
                        System.exit(0);

                    default:
                        System.out.println("Invalid choice");
                }
            }
            catch (DuplicateSkillException |
                   ExpiredCertificationException e) {
                System.out.println(e.getMessage());
            }
            catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }
}

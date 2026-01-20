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

            System.out.println("\n1.Add Student");
            System.out.println("2.View All Students");
            System.out.println("3.Update Certification");
            System.out.println("4.Find a Student");
            System.out.println("5.View Expired Certificates");
            System.out.println("6.Exit");
            
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

                    // ---------- ENTER SKILLS ----------
                    int skillCount;
                    do {
                        System.out.print("Number of skills (min 1): ");
                        skillCount = sc.nextInt();
                    } while (skillCount <= 0);
                    sc.nextLine();

                    // Store skill IDs
                    int[] skillIds = new int[skillCount];

                    for (int i = 0; i < skillCount; i++) {
                        System.out.print("Skill " + (i + 1) + " name: ");
                        String skillName = sc.nextLine();

                        skillIds[i] = skillDAO.getOrCreateSkill(skillName);
                    }

                    // ---------- ENTER CERTIFICATIONS ----------
                    int certCount;
                    do {
                        System.out.print("Number of certifications (min 1): ");
                        certCount = sc.nextInt();
                    } while (certCount <= 0);
                    sc.nextLine();

                    for (int i = 0; i < certCount; i++) {

                        System.out.print("Certification " + (i + 1) + " name: ");
                        String certName = sc.nextLine();

                        // show available skills
                        System.out.println("Select skill for this certification:");
                        for (int j = 0; j < skillIds.length; j++) {
                            System.out.println((j + 1) + ". Skill ID: " + skillIds[j]);
                        }

                        int skillChoice;
                        do {
                            System.out.print("Choice (1-" + skillIds.length + "): ");
                            skillChoice = sc.nextInt();
                        } while (skillChoice < 1 || skillChoice > skillIds.length);
                        sc.nextLine();

                        int selectedSkillId = skillIds[skillChoice - 1];

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
                                selectedSkillId,
                                certName,
                                LocalDate.now(),
                                expiry
                            );

                        service.assignCertification(cert);
                    }

                    System.out.println("Student, skills and certifications added successfully");
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
                        
                        
                    case 5:
                        certDAO.viewExpiredCertificates();
                        break;

                    // -------- EXIT --------
                    case 6:
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

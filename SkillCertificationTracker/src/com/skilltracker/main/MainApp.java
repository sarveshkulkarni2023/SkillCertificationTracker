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
        CertificationService service = new CertificationService();

        while (true) {

            System.out.println("\n1.Add Student (with Skills & Certification)");
            System.out.println("2.View All Students");
            System.out.println("3.Update Certification Expiry");
            System.out.println("4.Exit");
            System.out.print("Choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            try {
                switch (choice) {

                    case 1:
                        System.out.print("Name: ");
                        String name = sc.nextLine();

                        System.out.print("Email: ");
                        String email = sc.nextLine();

                        int studentId =
                            studentDAO.addStudentAndReturnId(
                                new Student(name, email));

                        System.out.print("Number of skills: ");
                        int count = sc.nextInt();
                        sc.nextLine();

                        for (int i = 0; i < count; i++) {

                            System.out.print("Skill name: ");
                            String skillName = sc.nextLine();

                            int skillId =
                                skillDAO.getOrCreateSkill(skillName);

                            System.out.print("Expiry (yyyy-mm-dd): ");
                            LocalDate expiry =
                                LocalDate.parse(sc.nextLine());

                            Certification cert =
                                new Certification(
                                    studentId,
                                    skillId,
                                    LocalDate.now(),
                                    expiry
                                );

                            service.assignCertification(cert);
                        }

                        System.out.println("Student added successfully");
                        break;

                    case 2:
                        new CertificationDAO().viewAllStudentDetails();
                        break;

                    case 3:
                        System.out.print("Student ID: ");
                        int sid = sc.nextInt();
                        System.out.print("Skill ID: ");
                        int skid = sc.nextInt();
                        sc.nextLine();

                        System.out.print("New Expiry (yyyy-mm-dd): ");
                        LocalDate newExp =
                            LocalDate.parse(sc.nextLine());

                        new CertificationDAO()
                            .updateCertificationExpiry(sid, skid, newExp);
                        System.out.println("Expiry updated");
                        break;

                    case 4:
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

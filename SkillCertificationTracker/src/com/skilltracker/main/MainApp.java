package com.skilltracker.main;

import com.skilltracker.dao.CertificationDAO;
import com.skilltracker.dao.SkillDAO;
import com.skilltracker.dao.StudentDAO;
import com.skilltracker.exception.DataNotFoundException;
import com.skilltracker.exception.DuplicateSkillException;
import com.skilltracker.exception.ExpiredCertificationException;
import com.skilltracker.model.Certification;
import com.skilltracker.model.ExpiredCertificateView;
import com.skilltracker.model.Student;
import com.skilltracker.service.CertificationService;
import com.skilltracker.service.CertificationServiceImpl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;


public class MainApp {

    public static void main(String[] args) throws ExpiredCertificationException {
    	final String RED = "\u001B[31m";
    	final String YELLOW = "\u001B[33m";
    	final String RESET = "\u001B[0m";




        Scanner sc = new Scanner(System.in);

        // DAOs (existing, stable)
        StudentDAO studentDAO = new StudentDAO();
        SkillDAO skillDAO = new SkillDAO();
        CertificationDAO certDAO = new CertificationDAO();

        // SERVICE (OOPS + Interface + Polymorphism)
        CertificationService certService = new CertificationServiceImpl();

        while (true) {

            System.out.println("\n1. Add Student");
            System.out.println("2. View All Students");
            System.out.println("3. Update Certification Expiry");
            System.out.println("4. Find a Student");
            System.out.println("5. View Expired Certificates");
            System.out.println("6. Delete Record.");
            System.out.println("7. Exit");

            System.out.print("Choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            try {
                switch (choice) {

                    // ---------- ADD STUDENT ----------
                    case 1:
                        System.out.print("Name: ");
                        String name = sc.nextLine();

                        System.out.print("Email: ");
                        String email = sc.nextLine();

                        int studentId =
                                studentDAO.addStudentAndReturnId(
                                        new Student(name, email));

                        // ---------- SKILLS ----------
                        int skillCount;
                        do {
                            System.out.print("Number of skills (min 1): ");
                            skillCount = sc.nextInt();
                        } while (skillCount <= 0);
                        sc.nextLine();

                        int[] skillIds = new int[skillCount];

                        for (int i = 0; i < skillCount; i++) {
                            System.out.print("Skill " + (i + 1) + " name: ");
                            String skillName = sc.nextLine();
                            skillIds[i] = skillDAO.getOrCreateSkill(skillName);
                        }
                        System.out.println("Are you want to enter the certificates 1/0");
                        Scanner s=new Scanner (System.in);
                        int n=s.nextInt();
                        if(n==1){

                        // ---------- CERTIFICATIONS ----------
                        int certCount;
                        do {
                            System.out.print("Number of certifications (min 1): ");
                            certCount = sc.nextInt();
                        } while (certCount <= 0);
                        sc.nextLine();

                        for (int i = 0; i < certCount; i++) {

                            System.out.print("Certification name: ");
                            String certName = sc.nextLine();

                            System.out.println("Select skill:");
                            for (int j = 0; j < skillIds.length; j++) {
                                System.out.println((j + 1) + ". Skill ID: " + skillIds[j]);
                            }

                            int skillChoice;
                            do {
                                System.out.print("Choice: ");
                                skillChoice = sc.nextInt();
                            } while (skillChoice < 1 || skillChoice > skillIds.length);
                            sc.nextLine();

                            LocalDate expiry = null;
                            System.out.print("Expiry (yyyy-mm-dd) [Enter to skip]: ");
                            String exp = sc.nextLine();
                            if (!exp.isEmpty()) {
                                expiry = LocalDate.parse(exp);
                            }

                            Certification cert =
                                    new Certification(
                                            studentId,
                                            skillIds[skillChoice - 1],
                                            certName,
                                            LocalDate.now(),
                                            expiry
                                    );

                            // SERVICE call (correct)
                            certService.issueCertification(cert);
                        }
                        System.out.println("Student, skills, and certifications added.");
                        }
                        System.out.println("Student, skills added.");
                     
                        break;

                    // ---------- VIEW ALL STUDENTS ----------
                    case 2:
                        certDAO.viewAllStudentDetails();
                        break;

                    // ---------- UPDATE EXPIRY ----------
                    case 3:
                        System.out.print("Student ID: ");
                        int sid = sc.nextInt();

                        System.out.print("Certificate Name: ");
                        String certificate_name= sc.nextLine();
                        sc.nextLine();

                        System.out.print("New Expiry (yyyy-mm-dd) [Enter to remove]: ");
                        String input = sc.nextLine();

                        LocalDate newExpiry =
                                input.isEmpty() ? null : LocalDate.parse(input);

                        certDAO.updateCertificationExpiry(sid, certificate_name, newExpiry);
                        System.out.println("Expiry updated.");
                        break;

                    // ---------- FIND STUDENT ----------
                    case 4:
                        System.out.print("Enter Student ID or Name: ");
                        String search = sc.nextLine();
                        certDAO.findStudent(search);
                        break;

                    // ---------- VIEW EXPIRED CERTIFICATES ----------
                    case 5:
                        List<ExpiredCertificateView> expired =
                                certService.viewExpiredCertificates();

                        System.out.printf(
                        	    "%-5s %-20s %-35s %-12s%n",
                        	    "ID", "NAME", "CERTIFICATE", "EXPIRY"
                        	);

                        LocalDate today = LocalDate.now();
                        LocalDate soonLimit = today.plusDays(30);

                        for (ExpiredCertificateView e : expired) {

                            LocalDate expiry = e.getExpiryDate();

                            // 🔴 EXPIRED
                            if (expiry.isBefore(today)) {
                                System.out.print(RED);

                            // 🟡 EXPIRING SOON (within 30 days)
                            } else if (!expiry.isAfter(soonLimit)) {
                                System.out.print(YELLOW);
                            }

                            System.out.printf(
                                "%-5d %-20s %-35s %-12s%n",
                                e.getStudentId(),
                                e.getStudentName(),
                                e.getCertificateName(),
                                expiry
                            );

                            System.out.print(RESET);
                        }



                        break;
                        
                        
                    case 6:
                        System.out.print("Enter Student ID (or press Enter to skip): ");
                        String idInput = sc.nextLine();

                        System.out.print("Enter Student Name (or press Enter to skip): ");
                        String nameInput = sc.nextLine();

                        Integer id = idInput.isEmpty() ? null : Integer.parseInt(idInput);
                        String name1 = nameInput.isEmpty() ? null : nameInput;

                        certDAO.deleteByIdOrName(id, name1);
                        break;
    

                    case 7:
                        System.out.println("Exiting...");
                        System.exit(0);

                    default:
                        System.out.println("Invalid choice");
                }

            } catch (DataNotFoundException e) {

                System.out.println(e.getMessage());

            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }


            }
        }
    }


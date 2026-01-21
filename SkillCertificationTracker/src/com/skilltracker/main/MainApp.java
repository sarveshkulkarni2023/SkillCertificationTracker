package com.skilltracker.main;

import com.skilltracker.dao.CertificationDAO;
import com.skilltracker.dao.SkillDAO;
import com.skilltracker.dao.StudentDAO;
import com.skilltracker.exception.DataNotFoundException;
import com.skilltracker.exception.ExpiredCertificationException;
import com.skilltracker.model.Certification;
import com.skilltracker.model.ExpiredCertificateView;
import com.skilltracker.model.Student;
import com.skilltracker.service.CertificationService;
import com.skilltracker.service.CertificationServiceImpl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainApp {

    public static void main(String[] args) throws ExpiredCertificationException {

        final String RED = "\u001B[31m";
        final String YELLOW = "\u001B[33m";
        final String RESET = "\u001B[0m";

        Scanner sc = new Scanner(System.in);

        StudentDAO studentDAO = new StudentDAO();
        SkillDAO skillDAO = new SkillDAO();
        CertificationDAO certDAO = new CertificationDAO();
        CertificationService certService = new CertificationServiceImpl();

        while (true) {

            System.out.println("\n===== SKILL CERTIFICATION TRACKER =====");
            System.out.println("1. Add Student");
            System.out.println("2. View All Students");
            System.out.println("3. Update Certification Expiry");
            System.out.println("4. Find a Student");
            System.out.println("5. View Expired Certificates");
            System.out.println("6. Delete Record");
            System.out.println("7. Add Skill to Existing Student");
            System.out.println("8. Add Certificate to Existing Student");
            System.out.println("9. Exit");

            System.out.print("Choice: ");
            int choice = Integer.parseInt(sc.nextLine());

            try {
                switch (choice) {

                    // -------- ADD STUDENT --------
                    case 1 -> {
                        System.out.print("Name: ");
                        String name = sc.nextLine();

                        System.out.print("Email: ");
                        String email = sc.nextLine();

                        int studentId =
                                studentDAO.addStudentAndReturnId(new Student(name, email));

                        System.out.print("Number of skills: ");
                        int skillCount = Integer.parseInt(sc.nextLine());

                        int[] skillIds = new int[skillCount];
                        for (int i = 0; i < skillCount; i++) {
                            System.out.print("Skill " + (i + 1) + ": ");
                            skillIds[i] = skillDAO.getOrCreateSkill(sc.nextLine());
                        }

                        System.out.print("Add certificates? (1 = Yes / 0 = No): ");
                        int addCert = Integer.parseInt(sc.nextLine());

                        if (addCert == 1) {
                            System.out.print("Number of certificates: ");
                            int certCount = Integer.parseInt(sc.nextLine());

                            for (int i = 0; i < certCount; i++) {

                                System.out.print("Certificate Name: ");
                                String certName = sc.nextLine();

                                System.out.println("Select Skill:");
                                for (int j = 0; j < skillIds.length; j++) {
                                    System.out.println((j + 1) + ". Skill ID: " + skillIds[j]);
                                }

                                int skillChoice =
                                        Integer.parseInt(sc.nextLine()) - 1;

                                System.out.print("Expiry (yyyy-mm-dd) [Enter to skip]: ");
                                String exp = sc.nextLine();

                                LocalDate expiry =
                                        exp.isEmpty() ? null : LocalDate.parse(exp);

                                Certification cert = new Certification(
                                        studentId,
                                        skillIds[skillChoice],
                                        certName,
                                        LocalDate.now(),
                                        expiry
                                );

                                certService.issueCertification(cert);
                            }
                        }

                        System.out.println("Student added successfully");
                    }

                    // -------- VIEW ALL --------
                    case 2 -> certDAO.viewAllStudentDetails();

                    // -------- UPDATE EXPIRY --------
                    case 3 -> {
                        System.out.print("Student ID: ");
                        int sid = Integer.parseInt(sc.nextLine());

                        System.out.print("Certificate Name: ");
                        String certName = sc.nextLine();

                        System.out.print("New Expiry (yyyy-mm-dd) [Enter to remove]: ");
                        String exp = sc.nextLine();

                        LocalDate newExpiry =
                                exp.isEmpty() ? null : LocalDate.parse(exp);

                        certDAO.updateCertificationExpiry(sid, certName, newExpiry);
                        System.out.println("Expiry updated");
                    }

                    // -------- FIND STUDENT --------
                    case 4 -> {
                        System.out.print("Enter Student ID or Name: ");
                        certDAO.findStudent(sc.nextLine());
                    }

                    // -------- EXPIRED CERTS --------
                    case 5 -> {
                        List<ExpiredCertificateView> expired =
                                certService.viewExpiredCertificates();

                        System.out.printf("%-5s %-20s %-35s %-12s%n",
                                "ID", "NAME", "CERTIFICATE", "EXPIRY");

                        LocalDate today = LocalDate.now();
                        LocalDate soon = today.plusDays(30);

                        for (ExpiredCertificateView e : expired) {
                            if (e.getExpiryDate().isBefore(today))
                                System.out.print(RED);
                            else if (!e.getExpiryDate().isAfter(soon))
                                System.out.print(YELLOW);

                            System.out.printf("%-5d %-20s %-35s %-12s%n",
                                    e.getStudentId(),
                                    e.getStudentName(),
                                    e.getCertificateName(),
                                    e.getExpiryDate());

                            System.out.print(RESET);
                        }
                    }

                    // -------- call DELETE --------
                    case 6 -> {
                        System.out.print("Student ID (Enter to skip): ");
                        String idStr = sc.nextLine();

                        System.out.print("Student Name (Enter to skip): ");
                        String nameStr = sc.nextLine();

                        Integer id = idStr.isEmpty() ? null : Integer.parseInt(idStr);
                        String nm = nameStr.isEmpty() ? null : nameStr;

                        certDAO.deleteByIdOrName(id, nm);
                    }

                    // -------- ADD SKILL --------
                    case 7 -> {
                        System.out.print("Student ID (Enter to skip): ");
                        String idStr = sc.nextLine();

                        System.out.print("Student Name (Enter to skip): ");
                        String nameStr = sc.nextLine();

                        Integer id = idStr.isEmpty() ? null : Integer.parseInt(idStr);
                        String nm = nameStr.isEmpty() ? null : nameStr;

                        System.out.print("Number of skills: ");
                        int count = Integer.parseInt(sc.nextLine());

                        List<String> skills = new ArrayList<>();
                        for (int i = 0; i < count; i++) {
                            System.out.print("Skill " + (i + 1) + ": ");
                            skills.add(sc.nextLine());
                        }

                        certDAO.addSkillsToStudent(id, nm, skills);
                    }

                    // -------- ADD CERTIFICATE --------
                    case 8 -> {
                        System.out.print("Student ID (Enter to skip): ");
                        String idStr = sc.nextLine();

                        System.out.print("Student Name (Enter to skip): ");
                        String nameStr = sc.nextLine();

                        Integer id = idStr.isEmpty() ? null : Integer.parseInt(idStr);
                        String nm = nameStr.isEmpty() ? null : nameStr;

                        System.out.print("Number of certificates: ");
                        int count = Integer.parseInt(sc.nextLine());

                        List<Certification> certs = new ArrayList<>();

                        for (int i = 0; i < count; i++) {
                            Certification c = new Certification(i, i, nm, null, null);

                            System.out.print("Skill Name: ");
                            c.setSkillName(sc.nextLine());

                            System.out.print("Certificate Name: ");
                            c.setCertificateName(sc.nextLine());

                            System.out.print("Expiry (yyyy-mm-dd) [Enter to skip]: ");
                            String exp = sc.nextLine();
                            c.setExpiryDate(exp.isEmpty() ? null : LocalDate.parse(exp));

                            certs.add(c);
                        }

                        certDAO.addCertificatesToStudent(id, nm, certs);
                    }

                    // -------- EXIT --------
                    case 9 -> {
                        System.out.println("Exiting application...");
                        sc.close();
                        System.exit(0);
                    }

                    default -> System.out.println("Invalid choice");
                }

            } catch (DataNotFoundException | SQLException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}

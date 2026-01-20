package com.skilltracker.main;

import com.skilltracker.dao.CertificationDAO;
import com.skilltracker.dao.SkillDAO;
import com.skilltracker.dao.StudentDAO;
import com.skilltracker.exception.DuplicateSkillException;
import com.skilltracker.exception.ExpiredCertificationException;
import com.skilltracker.model.Certification;
import com.skilltracker.model.Skill;
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
        CertificationService service = new CertificationService();

        while (true) {
        	System.out.println("\n1.Add Student");
        	System.out.println("2.Add Skill");
        	System.out.println("3.Assign Certification");
        	System.out.println("4.View All Students");
        	System.out.println("5.Update Student");
        	System.out.println("6.View Student Skills");
        	System.out.println("7.Update Certification Expiry");
        	System.out.println("8.Exit");


            int choice = sc.nextInt();
            sc.nextLine();

            try {
                switch (choice) {
                    case 1:
                        System.out.print("Name: ");
                        String name = sc.nextLine();
                        System.out.print("Email: ");
                        String email = sc.nextLine();
                        studentDAO.addStudent(new Student(name, email));
                        System.out.println("Student added");
                        break;

                    case 2:
                        System.out.print("Skill Name: ");
                        skillDAO.addSkill(new Skill(sc.nextLine()));
                        System.out.println("Skill added");
                        break;

                    case 3:
                        System.out.print("Student ID: ");
                        int sid = sc.nextInt();
                        System.out.print("Skill ID: ");
                        int skid = sc.nextInt();
                        sc.nextLine();

                        Certification cert =
                            new Certification(
                                sid,
                                skid,
                                LocalDate.now(),
                                LocalDate.now().plusYears(1)
                            );

                        service.assignCertification(cert);
                        System.out.println("Certification assigned");
                        break;

                    case 4:
                        studentDAO.viewAllStudents();
                        break;

                    case 5:
                        System.out.print("Student ID: ");
                        int uid = sc.nextInt();
                        sc.nextLine();
                        System.out.print("New Name: ");
                        String uname = sc.nextLine();
                        System.out.print("New Email: ");
                        String uemail = sc.nextLine();
                        studentDAO.updateStudent(uid, uname, uemail);
                        break;

                    case 6:
                        System.out.print("Student ID: ");
                        int sidView = sc.nextInt();
                        new CertificationDAO().viewSkillsByStudent(sidView);
                        break;

                    case 7:
                        System.out.print("Student ID: ");
                        int sidUpd = sc.nextInt();
                        System.out.print("Skill ID: ");
                        int skidUpd = sc.nextInt();
                        sc.nextLine();

                        System.out.print("New Expiry (yyyy-mm-dd): ");
                        LocalDate newExpiry =
                            LocalDate.parse(sc.nextLine());

                        new CertificationDAO()
                            .updateCertificationExpiry(sidUpd, skidUpd, newExpiry);
                        break;
                        
                    case 8:
                        System.out.println("Exiting application...");
                        System.exit(0);


                    default:
                        System.out.println("Invalid choice");
                }
            }
            catch (DuplicateSkillException | ExpiredCertificationException e) {
                System.out.println(e.getMessage());
            }
            catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }

        }
    }
}

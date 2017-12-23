package org.vap.grading.email;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import java.util.Scanner;

/**
 * @author Vahe Pezeshkian
 *         September 19, 2017
 */

public class CommandLineAuthenticator extends Authenticator {

    private String email;
    private String password;

    public CommandLineAuthenticator() {
        System.out.print("Your email: ");
        email = new Scanner(System.in).nextLine();
        System.out.print("Your password: ");
        password = new Scanner(System.in).nextLine();
    }

    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(email, password);
    }
}

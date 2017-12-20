package org.vap.grading

import org.vap.grading.email.CommandLineAuthenticator
import org.vap.grading.email.GradeMailSender

/**
 * @author Vahe Pezeshkian
 * September 19, 2017
 */

def authenticator = new CommandLineAuthenticator()
def mailSender = new GradeMailSender(authenticator)
mailSender.subject = 'Test email'

def content = 'This is first message \n Another line'
mailSender.send('someone@gmail.com', content)         // TODO email address

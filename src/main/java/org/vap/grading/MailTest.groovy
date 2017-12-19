package org.vap.grading

/**
 * @author Vahe Pezeshkian
 * September 19, 2017
 */

def authenticator = new CommandLineAuthenticator()
def mailSender = new GradeMailSender(authenticator)
mailSender.subject = 'Test email'

def content = 'This is first message \n Another line'
mailSender.send('someone@gmail.com', content)         // TODO email address

package org.vap.grading

/**
 * @author Vahe Pezeshkian
 * September 19, 2017
 */
class Student {
    private String ID, firstName, lastName, email
    private Map<String, Double> grades = [:]
    private letterGrade

    String getID() {
        return ID
    }

    String getFirstName() {
        return firstName
    }

    String getLastName() {
        return lastName
    }

    String getFullName() {
        return firstName + " " + lastName
    }

    String getEmail() {
        return email
    }

    void addGrade(String gradeTitle, Double value) {
        grades[gradeTitle] = value
    }

    Double getGrade(String gradeTitle) {
        return grades[gradeTitle]
    }

    String getLetterGrade() {
        return letterGrade
    }

    void setLetterGrade(letterGrade) {
        this.letterGrade = letterGrade
    }
}

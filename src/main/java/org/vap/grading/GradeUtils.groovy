package org.vap.grading

/**
 * @author Vahe Pezeshkian
 * December 12, 2017
 */
class GradeUtils {

    static Map<String, Student> readStudentInfo(String infoFilePath) {
        Map<String, Student> students = [:]

        new File(infoFilePath).withReader {
            reader ->
                String line
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",")
                    String[] name = parts[0].split(" ")

                    Student student = new Student(ID: parts[1], firstName: name[0], lastName: name[1], email: parts[2].toLowerCase())
                    students[student.ID] = student
                }
                reader.close()
        }

        return students
    }

    static Map<String, Student> readGrades(Map<String, String> gradeInfo, Map<String, Student> students) {
        gradeInfo.keySet().each {key ->
            String path = gradeInfo[key]
            readGradeFile(students, key, path)
        }
        return students;
    }

    static readGradeFile(Map<String, Student> students, String gradeTitle, String gradePath) {
        new File(gradePath).readLines().each {
            String line ->
                String[] parts = line.split(",")
                def ID = parts[0], grade = parts.length == 1 ? '' : parts[1]
                students[ID].addGrade(gradeTitle, grade.length() == 0 ? null : Double.parseDouble(grade))
        }
    }

    static readGradesFile(Map<String, Student> students, Map<String, Integer> mapping, String csvFile, String delimiter) {
        new File(csvFile).readLines().each {
            String line ->
                String[] parts = line.split(delimiter)
                String ID = parts[0]
                Student student = students[ID]
                mapping.keySet().each {
                    String gradeKey ->
                        Integer index = mapping[gradeKey]
                        Double grade = (parts.length <= index || parts[index].isEmpty()) ? null : Double.parseDouble(parts[index])
                        student.addGrade(gradeKey, grade)
                }
        }
    }

    static String getGradesReport(Student student, List<String> gradeKeys) {
        String str = student.fullName
        gradeKeys.each {
            str += "\n\t" + it + ": " + gradeString(student.getGrade(it))
        }
        return str
    }

    static printGrades(Map<String, Student> students, String gradeKey) {
        students.values().each {printGrade(it, gradeKey)}
    }

    static emailGrades(Collection<Student> students, List<String> gradeKeys, String subject, String info, String signature) {
        def mailSender = new GradeMailSender(new CommandLineAuthenticator())
        mailSender.subject = subject

        students.each { s ->
            String content = buildGradesStatement(s, gradeKeys)
                .concat(info.isEmpty() ? "" : "\n$info")
                .concat(signature.isEmpty() ? "" : signature)

            mailSender.send(s.email, content)
        }
    }

    static printGrade(Student student, String gradeTitle) {
        Double grade = student.getGrade(gradeTitle)
        if (grade == null) {
            return;
        }
        println("Mail-to: $student.email")
        println("Dear $student.firstName $student.lastName, your grade for $gradeTitle is: $grade")
        println()
    }

    static String buildGradesStatement(Student student, List<String> gradeKeys) {
        String line = "Dear $student.firstName,\n\nYour "

        if (gradeKeys.size() == 1) {
            line += "grade for " + gradeKeys[0] + " is: " + gradeString(student.getGrade(gradeKeys[0]))
        } else {
            line += "grades are:\n"
            for (String key : gradeKeys) {
                line += key + "\t:\t" + gradeString(student.getGrade(key)) + "\n"
            }

            if (student.letterGrade != null) {
                line += "Letter grade : " + student.letterGrade + "\n"
            }
        }

        return line
    }

    static String gradeString(Double grade) {
        return grade ? grade.toString() : "Missing (0)"
    }

    static String signature(String name) {
        return "\n– regards,\n$name"
    }

    static void save(Collection<Student> students, List<String> fields, List<String> grades, String target) {

        def delimiter = "\t"
        def getGrade = {Double value -> value == null ? "0.0" : value.toString()}

        PrintWriter writer = new File(target).newPrintWriter()

        def columns = []
        columns.addAll(fields)
        columns.addAll(grades)
        columns.each { String key -> writer.write(key + delimiter) }
        writer.write('\n')

        students.each {
            Student s ->
                fields.each { String field -> writer.write(s[field] + delimiter) }
                grades.each { writer.write(getGrade(s.getGrade(it)) + delimiter) }
                writer.write('\n')
        }

        writer.close()
    }
}

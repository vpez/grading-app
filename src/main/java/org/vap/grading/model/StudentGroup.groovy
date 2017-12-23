package org.vap.grading.model

import org.vap.grading.util.GradeUtils
import org.vap.grading.email.CommandLineAuthenticator
import org.vap.grading.email.GradeMailSender

import java.util.function.Consumer
import java.util.function.DoubleConsumer
import java.util.function.IntConsumer

/**
 * @author Vahe Pezeshkian
 * December 12, 2017
 */
class StudentGroup {
    private String gradeKey
    private Map<String, Student> students

    public StudentGroup() {
        students = [:]
    }

    public StudentGroup(Map<String, Student> studentMap) {
        students = studentMap
    }

    public StudentGroup init(String studentInfoFile) {
        students = GradeUtils.readStudentInfo(studentInfoFile)
        return this
    }

    public StudentGroup setGradeKey(String key) {
        gradeKey = key
        return this
    }

    public Collection<Student> getStudents() {
        return students.values()
    }

    public StudentGroup processStudents(Consumer<Student> consumer) {
        students.values().each {consumer.accept(it)}
        return this
    }

    public StudentGroup loadGrades(String gradeKey, String gradePath) {
        GradeUtils.readGradeFile(students, gradeKey, gradePath)
        return this
    }

    public StudentGroup loadGradesBatch(Spreadsheet spreadSheet) {
        GradeUtils.readGradesFile(students, spreadSheet.mapping, spreadSheet.path, spreadSheet.delimiter)
        return this
    }

    public StudentGroup loadGradesBatch(Map<String, Integer> gradeKeyMapping, String gradesPath, String delimiter) {
        GradeUtils.readGradesFile(students, gradeKeyMapping, gradesPath, delimiter)
        return this
    }

    public StudentGroup emailGrades(String gradeKey) {
        def mailSender = new GradeMailSender(new CommandLineAuthenticator())
        mailSender.subject = "Grade for $gradeKey"

        students.values().each { s ->
            Double grade = s.getGrade(gradeKey)
            if (grade != null) {
                String content = GradeUtils.buildGradesStatement(s, [gradeKey])
                mailSender.send('vaheh.pezeshkian@gmail.com', content)
            }
        }

        return this
    }

    public StudentGroup withAverage(DoubleConsumer consumer) {
        withAverage(gradeKey, consumer)
    }

    public StudentGroup withAverage(String gradeKey, DoubleConsumer consumer) {
        students.values().stream()
                .filter({student -> student.getGrade(gradeKey) != null})
                .mapToDouble({ student -> student.getGrade(gradeKey)})
                .average().ifPresent(consumer)

        return this
    }

    public StudentGroup withMin(DoubleConsumer consumer) {
        return withMin(gradeKey, consumer)
    }

    public StudentGroup withMin(String gradeKey, DoubleConsumer consumer) {
        students.values().stream()
                .filter({student -> student.getGrade(gradeKey) != null})
                .mapToDouble({ student -> student.getGrade(gradeKey)})
                .min().ifPresent(consumer)

        return this
    }

    public StudentGroup withMax(DoubleConsumer consumer) {
        return withMax(gradeKey, consumer)
    }

    public StudentGroup withMax(String gradeKey, DoubleConsumer consumer) {
        students.values().stream()
                .filter({student -> student.getGrade(gradeKey) != null})
                .mapToDouble({ student -> student.getGrade(gradeKey)})
                .max().ifPresent(consumer)

        return this
    }

    public StudentGroup withNumParticipants(IntConsumer consumer) {
        return withNumParticipants(gradeKey, consumer)
    }

    public StudentGroup withNumParticipants(String gradeKey, IntConsumer consumer) {
        def count = students.values().stream()
                .filter({ student -> student.getGrade(gradeKey) != null })
                .count()

        consumer.accept(count.intValue())

        return this
    }

    public StudentGroup withAbsents(Consumer<Student> consumer) {
        return withAbsents(gradeKey, consumer)
    }

    public StudentGroup withAbsents(String gradeKey, Consumer<Student> consumer) {
        students.values().stream()
                .filter({student -> student.getGrade(gradeKey) == null})
                .forEach(consumer)
        return this
    }

    public StudentGroup calculateTotal(String totalKey, Scale scale) {

        def calculate = {Student student ->
            Double total = 0.0
            scale.gradeMapper.keySet().each { def key ->
                if (student.getGrade(key) != null) {
                    total += scale.gradeMapper[key](student.getGrade(key) ?: 0.0)
                }
            }

            Double value = Math.ceil(total)
            student.addGrade(totalKey, value)
            student.setLetterGrade(scale.getLetter(value))
        }

        students.values().each calculate
        return this
    }

    /**
     * Exports data to spreadsheet
     * @param fields of students
     * @param grades for each student
     * @param targetFile where to store, shoudl have .tsv or .csv extension
     * @return
     */
    public StudentGroup export(List<String> fields, List<String> grades, String targetFile) {
        GradeUtils.save(students.values(), fields, grades, targetFile)
        return this
    }

    public StudentGroup export(List<String> fields, String sortBy, List<String> grades, String targetFile) {
        Collection students = students.values().toSorted({s1, s2 -> s1[sortBy] <= s2[sortBy] ? -1 : 1})
        GradeUtils.save(students, fields, grades, targetFile)
        return this
    }

    private boolean isGradeKeySet() {
        return gradeKey != null
    }
}

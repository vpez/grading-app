package org.vap.grading

/**
 * @author Vahe Pezeshkian
 * December 18, 2017
 */

def gradeMapper = [
        'Quiz 1' : {Double value -> value * 0.2},
        'Quiz 2' : {Double value -> value * 0.2},
        'Quiz 3' : {Double value -> value * 0.2},
        'Quiz 4' : {Double value -> value * 0.2},
        'Quiz 5' : {Double value -> value * 0.2},
        'HW 1'   : {Double value -> value * 0.025},
        'HW 2'   : {Double value -> value * 0.025},
        'Midterm': {Double value -> value * 5 * 0.2},
        'Final'  : {Double value -> value * 0.35},
        'Project': {Double value -> value * 0.25},
        'Participation' :{Double value -> value}
]

Scale scale = new Scale(
        ['A+', 'A', 'A-', 'B+', 'B', 'B-', 'C+', 'C', 'C-', 'D+', 'D'],
        [
                [95, Double.MAX_VALUE], // A+
                [90, 95],   // A
                [86, 90],   // A-
                [82, 86],   // B+
                [78, 82],   // B
                [74, 78],   // B-
                [70, 74],   // C+
                [65, 70],   // C
                [60, 65],   // C-
                [55, 60],   // D+
                [50, 55]    // D
        ], gradeMapper)

// File info
def studentInfo = '/Users/vahepezeshkian/IdeaProjects/groovy-play/students/student_info.csv'
def spreadsheet = new SpreadSheet(
        '/Users/vahepezeshkian/IdeaProjects/groovy-play/students/Sheet.csv', "\t",
        ['Final', 'Quiz 1', 'Quiz 2', 'Quiz 3', 'Quiz 4', 'Quiz 5', 'HW 1', 'HW 2', 'Midterm', 'Project', 'Participation'])

def components = gradeMapper.keySet().asList()
components.remove('Participation')
components.add('Total')

// Start
def group = new StudentGroup().init(studentInfo)
        .loadGradesBatch(spreadsheet)
        .calculateTotal('Total', scale)
        .setGradeKey('Total')
        .withMax({println(it)})
        .withMin({println(it)})
        .withAverage({println(it)})
        .processStudents({
                println(GradeUtils.buildGradesStatement(it, components))
        })

// Email
def subject     = 'Database Systems grades report'
def info        = 'You can review your scores or approach me if you have any question on Saturday, December 23 starting from 11:00.'
info           += '\nIf you think there are any missing grades, let me know via email.'
def signature   = GradeUtils.signature('V. Pezeshkian')

def deliveredProject = {Student s -> s.getGrade('Project') != null}
GradeUtils.emailGrades(group.students.findAll(deliveredProject), components, subject, info, signature)

def reportFields = gradeMapper.keySet().asList()
reportFields.add(0, 'Total')

GradeUtils.save(group.students,
        ['ID', 'email', 'fullName', 'letterGrade'],
        reportFields,
        '/Users/vahepezeshkian/IdeaProjects/groovy-play/students/final.csv')

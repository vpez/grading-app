package org.vap.grading

import org.vap.grading.model.Scale
import org.vap.grading.model.Spreadsheet
import org.vap.grading.model.StudentGroup
import org.vap.grading.util.GradeUtils

/**
 * @author Vahe Pezeshkian
 * December 18, 2017
 */

def gradeMapper = [
        'Quiz 1' : {it * 0.2},
        'Quiz 2' : {it * 0.2},
        'Quiz 3' : {it * 0.2},
        'Quiz 4' : {it * 0.2},
        'Quiz 5' : {it * 0.2},
        'HW 1'   : {it * 0.025},
        'HW 2'   : {it * 0.025},
        'Midterm': {it * 5 * 0.2},
        'Final'  : {it * 0.35},
        'Project': {it * 0.25},
        'Participation' : {it}
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

def directory = '/Users/vahepezeshkian/Desktop/CS-222/'
def getPath = {String file -> directory + file}

// File info
def spreadsheet = new Spreadsheet(
        getPath('grades.tsv'),
        ['Final', 'Quiz 1', 'Quiz 2', 'Quiz 3', 'Quiz 4', 'Quiz 5', 'HW 1', 'HW 2', 'Midterm', 'Project', 'Participation'])

def components = gradeMapper.keySet().asList() - ['Participation'] + ['Total']

// Start
def group = new StudentGroup().init(getPath('students.tsv'))
        .loadGradesBatch(spreadsheet)
        .calculateTotal('Total', scale)
        .setGradeKey('Total')
        .withMax({println(it)})
        .withMin({println(it)})
        .withAverage({println(it)})
        .processStudents({ println(GradeUtils.buildGradesStatement(it, components)) })
        .export(['fullName', 'letterGrade'], 'lastName', ['Total'] + gradeMapper.keySet().toList(), getPath('report.tsv'))

// Email
def subject     = 'Database Systems grades report'
def info        = 'You can review your scores or approach me if you have any question on Saturday, December 23 starting from 11:00.'
info           += '\nIf you think there are any missing grades, let me know via email.'
def signature   = GradeUtils.signature('V. Pezeshkian')

// GradeUtils.emailGrades(group.students, components, subject, info, signature)

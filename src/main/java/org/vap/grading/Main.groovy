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

def directory = '/path/to/directory/'
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
        .processStudents({
                println(GradeUtils.buildGradesStatement(it, components))
        })
        .export(['fullName', 'letterGrade'], gradeMapper.keySet().toList() + ['Total'], getPath('report.tsv'))

// Email
def subject     = 'Database Systems grades report'
def info        = 'You can review your scores or approach me if you have any question on Saturday, December 23 starting from 11:00.'
info           += '\nIf you think there are any missing grades, let me know via email.'
def signature   = GradeUtils.signature('V. Pezeshkian')

// GradeUtils.emailGrades(group.students, components, subject, info, signature)

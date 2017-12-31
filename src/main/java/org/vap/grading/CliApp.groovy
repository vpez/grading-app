package org.vap.grading

import org.vap.grading.model.Spreadsheet
import org.vap.grading.model.StudentGroup

/**
 * @author Vahe Pezeshkian
 * December 23, 2017
 */

def cli = new CliBuilder()
cli.a(args: 1, argName: 'action', 'Available actions: min,max,avg', required: true)
cli.e(args: 1, argName: 'export', 'Output TSV file of the results', required: false)
cli.g(args: 1, argName: 'grades', 'Input TSV file containing grades data', required: true)
cli.k(args: 1, argName: 'key', 'The key of the component to process', required: false)
cli.s(args: 1, argName: 'students', 'Input TSV file containing student data', required: true)

def options = cli.parse(args)

def line = {40.times({print '-'}); println()}
def header = {s -> line(); println(s); line()}
def underline = {s -> println(s); line()}

header "Welcome to command line grading app"

String studentsFile = options.s
String gradesFile = options.g
String key = options.k ?: 'Final'

def spreadsheet = new Spreadsheet(
        gradesFile,
        ['Final', 'Quiz 1', 'Quiz 2', 'Quiz 3', 'Quiz 4', 'Quiz 5', 'HW 1', 'HW 2', 'Midterm', 'Project', 'Participation'])

def group = new StudentGroup()
        .init(studentsFile).loadGradesBatch(spreadsheet)
        .setGradeKey(key)

underline "Results for $key"

if (options.a) {
    def actions = ['min': ['name': 'Minimum', 'invoke': 'withMin'],
                   'max': ['name': 'Maximum', 'invoke': 'withMax'],
                   'avg': ['name': 'Average', 'invoke': 'withAverage'],
                   'count' : ['name' : 'Count', 'invoke': 'withNumParticipants']]

    def consumer = { name, value -> println "$key $name\t\t $value" }

    actions.keySet().each { // it = min, max, avg etc.
        options.a.contains(it) && group.invokeMethod(actions[it]['invoke'], consumer.curry(actions[it]['name']))
    }
}

if (options.e) {
    String exportFile = options.e
    group.export(['ID', 'fullName'], [key], exportFile)
}

underline ""
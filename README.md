# grading-app
A simple app to help with grading

The intent of this app is to provide a set of classes and utilities that enable instructors to write flexible scripts to perform grade calculation and automate grade announcements.

## Languages
Java (8) and Groovy

## Features
* Load data from CSV or TSV files
* Define grade components and weights
* Calculate total grades per student
* Announce individual grades via email
* Build reports and save in spreadsheets

## Sample usage
```groovy

// Define components and weight of each component
def gradeMapper = [
  'Midterm' : {it * 0.25},
  'Final'   : {it * 0.5},
  'Project' : {it * 0.25}
]

// Define letter grades and ranges
def scale = new Scale(Grades.letters, Grades.ranges, gradeMapper)

// Load student info and grades from tab-separated or comma-separated files and perform calculations
new StudentGroup().init('/path/to/students.tsv')
        .loadGradesBatch(new SpreadSheet('/path/to/grades.tsv', ['Midterm', 'Final', 'Project']))
        .calculateTotal('Total', scale)                     // calculates total scores using the formula
        .setGradeKey('Total')
        .withMax({println("Max total grade is $it")})       // get and print maximum
        .withMin({println("Min total grade is $it")})       // get and print minimum
        .withAverage({println("Average is $it")})           // get and print average
        .processStudents({                                  // perform any task on each student (print, email grade etc.)
                println(GradeUtils.buildGradesStatement(it, ['Total']))
        })
        .export(['fullName', 'letterGrade'], gradeMapper.keySet().toList() + ['Total'], getPath('/path/to/report.tsv'))
```

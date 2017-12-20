package org.vap.grading.model

/**
 * @author Vahe Pezeshkian
 * December 18, 2017
 */
class Scale {
    private Map<String, Closure> gradeMapper
    private List<String> letters
    private List<List<Double>> ranges

    Scale(List<String> letters, List<List<Double>> ranges, Map<String, Closure> gradeMapper) {

        if (letters.size() != ranges.size())
            throw new IllegalArgumentException("Letters and ranges mismatch")

        this.letters = letters
        this.ranges = ranges
        this.gradeMapper = gradeMapper
    }

    public Map<String, Closure> getGradeMapper() {
        return gradeMapper
    }

    public String getLetter(Double value) {
        def round = value.round(0)
        def index = -1

        for (; index < ranges.size(); index++)
            if (round >= ranges[index][0] && round < ranges[index][1]) break

        return letters[index] ?: 'F'
    }
}

package org.vap.grading

/**
 * @author Vahe Pezeshkian
 * December 18, 2017
 */
class SpreadSheet {
    private String path
    private Map<String, Integer> mapping
    private String delimiter

    public SpreadSheet(String path, String delimiter, List<String> fields) {
        this.path = path
        this.delimiter = delimiter
        mapping = [:]
        for (int i = 0; i < fields.size(); i++) {
            mapping[fields[i]] = i + 1
        }
    }

    public Map<String, Integer> getMapping() {
        return mapping
    }

    public String getPath() {
        return path
    }

    String getDelimiter() {
        return delimiter
    }
}

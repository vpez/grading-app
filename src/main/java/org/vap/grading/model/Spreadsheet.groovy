package org.vap.grading.model

/**
 * @author Vahe Pezeshkian
 * December 18, 2017
 */
class Spreadsheet {

    public enum FileType {

        CSV(",", ".csv"),
        TSV("\t", ".tsv")

        private String delimiter
        private String ext

        public FileType(String delimiter, String ext) {
            this.delimiter = delimiter
            this.ext = ext
        }

        public String getDelimiter() {
            return delimiter
        }

        public static FileType getFileType(String ext) {
            return values().find {ext.equals(it.ext)}
        }
    }

    private String path
    private Map<String, Integer> mapping
    private FileType fileType

    public Spreadsheet(String path, List<String> fields) {
        this(path, FileType.getFileType(path.substring(path.indexOf('.'))), fields)
    }

    public Spreadsheet(String path, FileType fileType, List<String> fields) {
        this.path = path
        this.fileType = fileType
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
        return fileType.delimiter
    }
}

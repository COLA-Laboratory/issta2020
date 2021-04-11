package exceptions;

public class AnalysisPropertiesException extends Exception{
    private String fileLocation;

    public void setFileLocation(String fileLocation) {
        this.fileLocation=fileLocation;
    }

    public String getFileLocation() {
        return this.fileLocation;
    }
}

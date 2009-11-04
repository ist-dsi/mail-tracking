/**
 * 
 */
package module.mailtracking.presentationTier;

import java.io.InputStream;

import module.mailtracking.domain.CorrespondenceType;

public class ImportationFileBean implements java.io.Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String filename;
    private String mimetype;
    private Long filesize;
    private InputStream stream;

    private CorrespondenceType type;

    public ImportationFileBean() {

    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public Long getFilesize() {
        return filesize;
    }

    public void setFilesize(Long filesize) {
        this.filesize = filesize;
    }

    public InputStream getStream() {
        return stream;
    }

    public void setStream(InputStream stream) {
        this.stream = stream;
    }

    public CorrespondenceType getType() {
        return type;
    }

    public void setType(CorrespondenceType type) {
        this.type = type;
    }
}
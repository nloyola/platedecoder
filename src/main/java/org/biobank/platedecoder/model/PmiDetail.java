package org.biobank.platedecoder.model;

public class PmiDetail {

    private String siteName;

    private String mrn;

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getMrn() {
        return mrn;
    }

    public void setMrn(String mrn) {
        this.mrn = mrn;
    }


    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
            buf.append("siteName: ").append(siteName);
            buf.append(", mrn: ").append(mrn);
            return buf.toString();
    }
}

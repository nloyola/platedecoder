package org.biobank.platedecoder.model;

/**
 * This is the object created when the OpenSpecimen REST API returns a Site.
 */
public class Site {

    private long   id;
    private String name;
    private String instituteName;
    private String code;
    private String type;
    private String activityStatus;

    public Site() {}

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the instituteName
     */
    public String getInstituteName() {
        return instituteName;
    }

    /**
     * @param instituteName the instituteName to set
     */
    public void setInstituteName(String instituteName) {
        this.instituteName = instituteName;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the activityStatus
     */
    public String getActivityStatus() {
        return activityStatus;
    }

    /**
     * @param activityStatus the activityStatus to set
     */
    public void setActivityStatus(String activityStatus) {
        this.activityStatus = activityStatus;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("id: ").append(id);
        buf.append(", name: ").append(name);
        buf.append(", instituteName: ").append(instituteName);
        buf.append(", code: ").append(code);
        buf.append(", type: ").append(type);
        buf.append(", activityStatus: ").append(activityStatus);
        return buf.toString();
    }

}

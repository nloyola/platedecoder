package org.biobank.platedecoder.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Participant {

   	private Long id;
	private String uid;
	private String activityStatus;
	private String empi;
	private boolean phiAccess;
    private List<PmiDetail> pmis;
    private List<String> registeredCps;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the uid
     */
    public String getUid() {
        return uid;
    }

    /**
     * @param uid the uid to set
     */
    public void setUid(String uid) {
        this.uid = uid;
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

    /**
     * @return the empi
     */
    public String getEmpi() {
        return empi;
    }

    /**
     * @param empi the empi to set
     */
    public void setEmpi(String empi) {
        this.empi = empi;
    }

    /**
     * @return the phiAccess
     */
    public boolean isPhiAccess() {
        return phiAccess;
    }

    /**
     * @param phiAccess the phiAccess to set
     */
    public void setPhiAccess(boolean phiAccess) {
        this.phiAccess = phiAccess;
    }

    public List<PmiDetail> getPmis() {
        return pmis;
    }

    public void setPmis(List<PmiDetail> pmis) {
        this.pmis = pmis;
    }

    /**
     * @return the registeredCps
     */
    public List<String> getRegisteredCps() {
        return registeredCps;
    }

    /**
     * @param registeredCps the registeredCps to set
     */
    public void setRegisteredCps(List<String> registeredCps) {
        this.registeredCps = registeredCps;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("id: ").append(id);
        buf.append(", uid: ").append(uid);
        buf.append(", activityStatus: ").append(activityStatus);
        buf.append(", empi: ").append(empi);
        buf.append(", phiAccess: ").append(phiAccess);

        if (!pmis.isEmpty()) {
            buf.append(", pmis: [ ");
            List<String> pmiStrings = new ArrayList<>();
            StringBuffer buf2 = new StringBuffer();
            for (PmiDetail pmi : pmis) {
                buf2.append(" { ").append(pmi).append(" }");
                pmiStrings.add(buf2.toString());
                buf2.setLength(0);
            }
            buf.append(pmiStrings.stream().collect(Collectors.joining(", ")));
            buf.append(" ]");
        }

        if (!registeredCps.isEmpty()) {
            buf.append(", registeredCps: [ ");
            buf.append(registeredCps.stream().collect(Collectors.joining(", ")));
            buf.append(" ]");
        }

        return buf.toString();
    }

}

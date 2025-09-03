package com.brokersystems.brokerapp.claims.model;

import com.brokersystems.brokerapp.common.Constants;
import com.brokersystems.brokerapp.medical.model.ServiceProviderContracts;
import com.brokersystems.brokerapp.setup.model.ClmCausations;
import com.brokersystems.brokerapp.setup.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by peter on 4/7/2017.
 */
@Getter
@Entity
@Table(name = "sys_brk_clm_activities")
@Slf4j
public class ClaimActivities {

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "clm_act_id")
    private Long activityId;

    @Setter
    @ManyToOne
    @JoinColumn(name="clm_act_clm_id",nullable=false)
    private ClaimBookings claimBookings;

    @Setter
    @ManyToOne
    @JoinColumn(name="clm_act_user_id",nullable=false)
    private User userCreated;

    @Setter
    @ManyToOne
    @JoinColumn(name="clm_act_status_id",nullable=false)
    private ClmCausations activity;

    @Setter
    @Column(name = "clm_act_rem_dt")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date remDate;

    @Setter
    @Column(name = "clm_act_dt")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date activityDate;

    @Column(name = "clm_act_status")
    private String currentActivity;

    @Column(name = "clm_act_notes",length = 2000)
    private String activityNotes;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static class ActivityNote {
        private String note;
        private Date timestamp;
        private String userId;
        private String userName;

        public ActivityNote() {}

        public ActivityNote(String note, Date timestamp, String userName) {
            this.note = note;
            this.timestamp = timestamp;
            this.userId = userId;
            this.userName = userName;
        }

        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }

        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        public Date getTimestamp() { return timestamp; }
        public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
    }

    @Setter
    @ManyToOne
    @JoinColumn(name="clm_act_review_user")
    private User reviewUser;

    @Setter
    @ManyToOne
    @JoinColumn(name="clm_act_spc_id")
    private ServiceProviderContracts serviceProvider;

    @Column(name = "clm_act_insurer_ref")
    private String insurerRef;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input != null) {
            System.out.println("Validating and sanitizing " + fieldName + ": Raw input = '" + input + "'");
            log.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        }
        if (input != null && !input.trim().isEmpty() && !input.matches(pattern)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setInsurerRef(String insurerRef) {
        validateAndSanitize(insurerRef, Constants.REF_PATTERN, "Insurer Reference");
        this.insurerRef = insurerRef == null ? null : StringEscapeUtils.escapeHtml4(insurerRef.trim());
    }

    public void setActivityNotes(String activityNotes) {
        validateAndSanitize(activityNotes, Constants.DESC_PATTERN, "Activity Notes");
        this.activityNotes = activityNotes == null ? null : StringEscapeUtils.escapeHtml4(activityNotes.trim());
    }

    public void setCurrentActivity(String currentActivity) {
        validateAndSanitize(currentActivity, Constants.NAME_PATTERN, "Current Activity");
        this.currentActivity = currentActivity == null ? null : StringEscapeUtils.escapeHtml4(currentActivity.trim());
    }
    public List<ActivityNote> getActivityNotes() {
        if (activityNotes == null || activityNotes.trim().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            List<ActivityNote> notes = OBJECT_MAPPER.readValue(activityNotes,
                    new TypeReference<List<ActivityNote>>() {});
            return notes != null ? notes : new ArrayList<>();
        } catch (Exception e) {
            log.warn("Could not parse activity notes JSON for activity {}: {}. Returning empty list.",
                    activityId, e.getMessage());
            return new ArrayList<>();
        }
    }

    public void addActivityNote(String note, String userName) {
        if (note == null || note.trim().isEmpty()) {
            log.warn("Attempted to add empty note to activity {}", activityId);
            return;
        }

        List<ActivityNote> notes = getActivityNotes();
        notes.add(new ActivityNote(note.trim(), new Date(), userName));
        setActivityNotes(notes);
    }

    public void setActivityNotes(List<ActivityNote> notes) {
        if (notes == null) {
            this.activityNotes = "[]";
            return;
        }

        try {
            this.activityNotes = OBJECT_MAPPER.writeValueAsString(notes);
        } catch (Exception e) {
            log.error("Could not convert activity notes to JSON for activity {}: {}. Setting to empty array.",
                    activityId, e.getMessage());
            this.activityNotes = "[]";
        }
    }

    public String getActivityNotesAsString() {
        return activityNotes;
    }

    public void setActivityNotesAsString(String activityNotes) {
        this.activityNotes = activityNotes;
    }


    public ActivityNote getLatestNote() {
        List<ActivityNote> notes = getActivityNotes();
        return notes.isEmpty() ? null : notes.get(notes.size() - 1);
    }

    public String getFormattedNotes() {
        List<ActivityNote> notes = getActivityNotes();
        if (notes.isEmpty()) {
            return "";
        }

        StringBuilder formatted = new StringBuilder();
        for (ActivityNote note : notes) {
            formatted.append(String.format("[%tF %<tT by %s] %s%n",
                    note.getTimestamp(), note.getUserName(), note.getNote()));
        }
        return formatted.toString();
    }

    public int getNotesCount() {
        return getActivityNotes().size();
    }

    public boolean hasNotes() {
        return getNotesCount() > 0;
    }

}
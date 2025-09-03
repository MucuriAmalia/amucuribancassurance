package com.brokersystems.brokerapp.users.dto;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static com.brokersystems.brokerapp.common.Constants.REF_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResubmitTaskRequest {

    private static final Logger logger = LoggerFactory.getLogger(ResubmitTaskRequest.class);

    private Long taskId;
    private String taskType;
    private Object updatedData;
    private List<String> checkerIds;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input == null) {
            return;
        }
        //logger.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        String trimmedInput = input.trim();
        if (!trimmedInput.isEmpty() && !trimmedInput.matches(pattern)) {
            throw new IllegalArgumentException(
                    String.format("Invalid characters in %s", fieldName)
            );
        }
    }

    public void setTaskType(String taskType) {
        validateAndSanitize(taskType, REF_PATTERN, "Task Type");
        this.taskType = taskType == null ? null : StringEscapeUtils.escapeHtml4(taskType.trim());
    }

    public void setCheckerIds(List<String> checkerIds) {
        if (checkerIds != null) {
            this.checkerIds = checkerIds.stream()
                    .map(checkerId -> {
                        validateAndSanitize(checkerId, REF_PATTERN, "Checker ID");
                        return checkerId == null ? null : StringEscapeUtils.escapeHtml4(checkerId.trim());
                    })
                    .collect(Collectors.toList());
        } else {
            this.checkerIds = null;
        }
    }
}

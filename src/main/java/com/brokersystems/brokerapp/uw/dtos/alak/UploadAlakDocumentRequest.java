package com.brokersystems.brokerapp.uw.dtos.alak;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadAlakDocumentRequest {
    @JsonProperty("PolicyNumber")
    private String PolicyNumber;

    @JsonProperty("DocumentType")
    private String DocumentType;

    @JsonProperty("File")
    private MultipartFile File;
}

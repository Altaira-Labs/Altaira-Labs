package com.altaira.backend.dto.onboarding;

import jakarta.validation.constraints.Size;

import java.util.Map;

/** Input for submit onboarding task in the onboarding API. */
public class SubmitOnboardingTaskRequest {

    /** Signature full name. */
    @Size(max = 120, message = "Signer full name must be at most 120 characters")
    private String signatureFullName;

    /** Identifier of the signature document. */
    @Size(max = 80, message = "Document ID must be at most 80 characters")
    private String signatureDocumentId;

    /** Signature consent. */
    private boolean signatureConsent;

    /** Data. */
    private Map<String, Object> data;

    /** Files. */
    private java.util.List<Map<String, Object>> files;

    /** {@return signature full name} */
    public String getSignatureFullName() { return signatureFullName; }
    /**
     * Sets the signature full name on this submit onboarding task.
     *
     * @param signatureFullName signature full name
     */
    public void setSignatureFullName(String signatureFullName) { this.signatureFullName = signatureFullName; }

    /** {@return identifier of the signature document} */
    public String getSignatureDocumentId() { return signatureDocumentId; }
    /**
     * Sets the identifier of the signature document on this submit onboarding task.
     *
     * @param signatureDocumentId identifier of the signature document
     */
    public void setSignatureDocumentId(String signatureDocumentId) { this.signatureDocumentId = signatureDocumentId; }

    /** {@return signature consent} */
    public boolean isSignatureConsent() { return signatureConsent; }
    /**
     * Sets the signature consent on this submit onboarding task.
     *
     * @param signatureConsent signature consent
     */
    public void setSignatureConsent(boolean signatureConsent) { this.signatureConsent = signatureConsent; }

    /** {@return data} */
    public Map<String, Object> getData() { return data; }
    /**
     * Sets the data on this submit onboarding task.
     *
     * @param data data
     */
    public void setData(Map<String, Object> data) { this.data = data; }

    /** {@return files} */
    public java.util.List<Map<String, Object>> getFiles() { return files; }
    /**
     * Sets the files on this submit onboarding task.
     *
     * @param files files
     */
    public void setFiles(java.util.List<Map<String, Object>> files) { this.files = files; }
}

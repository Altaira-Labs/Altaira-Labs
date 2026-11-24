package com.altaira.backend.service;

import com.altaira.backend.entity.ClientEntity;
import com.altaira.backend.entity.OnboardingTaskEntity;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

/** Builds the PDF evidence of an accepted commercial agreement. */
@Service
public class SignedContractPdfService {

    /** Contract PDF path. */
    private final String contractPdfPath;

    /**
     * Constructs {@code SignedContractPdfService} using the supplied contract PDF path.
     *
     * @param contractPdfPath contract PDF path
     */
    public SignedContractPdfService(@Value("${altaira.onboarding.contract-pdf-path:}") String contractPdfPath) {
        this.contractPdfPath = contractPdfPath == null ? "" : contractPdfPath.trim();
    }

    /**
     * Builds signed contract evidence.
     *
     * @param client customer account in scope
     * @param task task
     * @return the resulting binary content
     * @throws IllegalStateException when validation reports “Could not create signed contract PDF evidence”
     */
    public byte[] buildSignedContractEvidence(ClientEntity client, OnboardingTaskEntity task) {
        try {
            if (!contractPdfPath.isBlank()) {
                File contractFile = new File(contractPdfPath);
                if (contractFile.exists() && contractFile.isFile()) {
                    return stampExistingContract(contractFile, client, task);
                }
            }

            return createSignatureReceipt(client, task);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not create signed contract PDF evidence", ex);
        }
    }

    /**
     * Adds the acceptance evidence to the existing contract PDF.
     *
     * @param contractFile contract file
     * @param client customer account in scope
     * @param task task
     * @return the resulting binary content
     * @throws IOException if the underlying input or output cannot be completed
     */
    private byte[] stampExistingContract(File contractFile, ClientEntity client, OnboardingTaskEntity task) throws IOException {
        try (PDDocument document = Loader.loadPDF(contractFile)) {
            PDPage page = document.getPage(document.getNumberOfPages() - 1);
            PDFontSet fonts = fonts();

            try (PDPageContentStream contentStream = new PDPageContentStream(
                    document,
                    page,
                    PDPageContentStream.AppendMode.APPEND,
                    true,
                    true
            )) {
                contentStream.setFont(fonts.regular(), 8);
                contentStream.beginText();
                contentStream.newLineAtOffset(40, 40);
                contentStream.showText("Altaira Labs written signature audit: " + auditLine(client, task));
                contentStream.endText();
            }

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            document.save(output);
            return output.toByteArray();
        }
    }

    /**
     * Creates signature receipt. Persists changes through {@code document}.
     *
     * @param client customer account in scope
     * @param task task
     * @return the resulting binary content
     * @throws IOException if the underlying input or output cannot be completed
     */
    private byte[] createSignatureReceipt(ClientEntity client, OnboardingTaskEntity task) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDFontSet fonts = fonts();

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(fonts.bold(), 18);
                contentStream.beginText();
                contentStream.newLineAtOffset(56, 760);
                contentStream.showText("Altaira Labs - Signed Service Agreement Evidence");
                contentStream.endText();

                contentStream.setFont(fonts.regular(), 10);
                float y = 715;
                for (String line : receiptLines(client, task)) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(56, y);
                    contentStream.showText(cleanPdfText(line));
                    contentStream.endText();
                    y -= 22;
                }
            }

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            document.save(output);
            return output.toByteArray();
        }
    }

    /**
     * Assembles the lines of the contract acceptance receipt.
     *
     * @param client customer account in scope
     * @param task task
     * @return the string items
     */
    private List<String> receiptLines(ClientEntity client, OnboardingTaskEntity task) {
        return List.of(
                "Client: " + nullSafe(client.getCompany()),
                "Contact: " + nullSafe(client.getName()) + " <" + nullSafe(client.getEmail()) + ">",
                "Task: " + nullSafe(task.getTitle()),
                "Signer full name: " + nullSafe(task.getSignatureFullName()),
                "Document ID: " + nullSafe(task.getSignatureDocumentId()),
                "Consent accepted: " + task.isSignatureConsent(),
                "Signed at: " + nullSafe(task.getSignedAt()),
                "IP address: " + nullSafe(task.getSignedIp()),
                "User-Agent: " + nullSafe(task.getSignedUserAgent()),
                "Server timestamp: " + Instant.now(),
                "",
                "This PDF is an onboarding audit artifact generated by Altaira Labs.",
                "If a formal contract PDF template is configured, this audit line is stamped onto that file."
        );
    }

    /**
     * Formats one entry in the contract acceptance evidence.
     *
     * @param client customer account in scope
     * @param task task
     * @return the resulting text
     */
    private String auditLine(ClientEntity client, OnboardingTaskEntity task) {
        return "client=" + nullSafe(client.getId()) +
                "; signer=" + nullSafe(task.getSignatureFullName()) +
                "; documentId=" + nullSafe(task.getSignatureDocumentId()) +
                "; signedAt=" + nullSafe(task.getSignedAt()) +
                "; ip=" + nullSafe(task.getSignedIp());
    }

    /**
     * Normalizes text before inserting it into a PDF text stream.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private String cleanPdfText(String value) {
        return nullSafe(value).replaceAll("[\\r\\n\\t]+", " ");
    }

    /**
     * Returns text suitable for the document when the source may be null.
     *
     * @param value stored or supplied representation
     * @return the resulting text
     */
    private String nullSafe(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    /**
     * Loads the fonts used to render the contract.
     *
     * @return the pd font set
     */
    private PDFontSet fonts() {
        return new PDFontSet(
                new PDType1Font(Standard14Fonts.FontName.HELVETICA),
                new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD)
        );
    }

    /**
     * Groups the pd font set data used by signed contract PDF service.
     *
     * @param regular regular
     * @param bold bold
     */
    private record PDFontSet(PDType1Font regular, PDType1Font bold) {}
}

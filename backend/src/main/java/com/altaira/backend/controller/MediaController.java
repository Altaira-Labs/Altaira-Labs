package com.altaira.backend.controller;

import com.altaira.backend.dto.media.CreateUploadUrlRequest;
import com.altaira.backend.dto.media.UploadUrlResponse;
import com.altaira.backend.security.ClientAccessService;
import com.altaira.backend.service.MediaUploadUrlService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/** HTTP endpoints for media under {@code /api/v1/media}. */
@RestController
@RequestMapping("/api/v1/media")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://altairalabs.vercel.app"
})
public class MediaController {

    /** Client access operations used by this component. */
    private final ClientAccessService clientAccessService;
    /** Media upload URL operations used by this component. */
    private final MediaUploadUrlService mediaUploadUrlService;

    /**
     * Creates the media controller with its injected collaborators.
     *
     * @param clientAccessService client access operations used by this component
     * @param mediaUploadUrlService media upload URL operations used by this component
     */
    public MediaController(
            ClientAccessService clientAccessService,
            MediaUploadUrlService mediaUploadUrlService
    ) {
        this.clientAccessService = clientAccessService;
        this.mediaUploadUrlService = mediaUploadUrlService;
    }

    /**
     * POST {@code /api/v1/media/upload-url}. Creates upload URL. Membership is checked before the workspace operation.
     *
     * @param request input carrying create upload URL
     * @param clientSessionToken session credential supplied for the client workspace
     * @return the upload URL response
     */
    @PostMapping("/upload-url")
    public UploadUrlResponse createUploadUrl(
            @Valid @RequestBody CreateUploadUrlRequest request,
            @RequestHeader(name = "X-Client-Session-Token", required = false) String clientSessionToken
    ) {
        var context = clientAccessService.requireClientAccess(clientSessionToken);
        clientAccessService.requireClientWriteAccess(context);
        return mediaUploadUrlService.createClientUploadUrl(context, request);
    }
}

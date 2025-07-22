package com.climbx.climbx.video;

import com.climbx.climbx.common.annotation.SuccessStatus;
import com.climbx.climbx.video.dto.VideoUploadRequestDto;
import com.climbx.climbx.video.dto.VideoUploadResponseDto;
import com.climbx.climbx.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController implements VideoApiDocumentation {

    private final VideoService videoService;

    @Override
    @PostMapping("/upload")
    @SuccessStatus(value = HttpStatus.CREATED)
    public VideoUploadResponseDto createVideoUploadUrl(
        @RequestBody
        VideoUploadRequestDto videoUploadRequestDto
    ) {
        return videoService.createVideoUploadUrl(videoUploadRequestDto);
    }
} 
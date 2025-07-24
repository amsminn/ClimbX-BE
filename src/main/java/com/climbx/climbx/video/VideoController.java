package com.climbx.climbx.video;

import com.climbx.climbx.common.annotation.SuccessStatus;
import com.climbx.climbx.video.dto.VideoListResponseDto;
import com.climbx.climbx.video.dto.VideoUploadRequestDto;
import com.climbx.climbx.video.dto.VideoUploadResponseDto;
import com.climbx.climbx.video.service.VideoService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        @AuthenticationPrincipal
        Long userId,

        @RequestBody
        VideoUploadRequestDto videoUploadRequestDto
    ) {
        return videoService.createVideoUploadUrl(userId, videoUploadRequestDto);
    }

    @Override
    @GetMapping("/{nickname}")
    @SuccessStatus(value = HttpStatus.OK)
    public List<VideoListResponseDto> getVideoList(@PathVariable String nickname) {
        return videoService.getVideoList(nickname);
    }
} 
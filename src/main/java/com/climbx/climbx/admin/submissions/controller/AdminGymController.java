package com.climbx.climbx.admin.submissions.controller;

import com.climbx.climbx.admin.submissions.service.AdminGymService;
import com.climbx.climbx.common.annotation.SuccessStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/admin/gyms")
@RequiredArgsConstructor
public class AdminGymController implements AdminGymApiDocumentation {

    private final AdminGymService adminGymService;

    @Override
    @PostMapping("/upload/2d-map")
    @SuccessStatus(HttpStatus.OK)
    public void uploadGym2dMap(
        @RequestPart(name = "gymId") Long gymId,
        @RequestPart(name = "baseImage") MultipartFile baseImage,
        @RequestPart(name = "overlayImages") List<MultipartFile> overlayImages // 벽이름.png 형태로 업로드해야됨
    ) {
        adminGymService.uploadGym2dMap(gymId, baseImage, overlayImages);
    }

}

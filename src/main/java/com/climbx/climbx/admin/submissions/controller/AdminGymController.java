package com.climbx.climbx.admin.submissions.controller;

import com.climbx.climbx.admin.submissions.service.AdminGymService;
import com.climbx.climbx.common.annotation.SuccessStatus;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/api/admin/gyms")
@RequiredArgsConstructor
public class AdminGymController implements AdminGymApiDocumentation {

    private final AdminGymService adminGymService;

    @Override
    @PostMapping("/{gymId}/2d-map")
    @SuccessStatus(HttpStatus.OK)
    public void uploadGym2dMap(
        @PathVariable(name = "gymId") Long gymId,
//        @RequestPart(name = "map2dImage") MultipartFile map2dImage,
        MultipartHttpServletRequest request
    ) {

        MultipartFile map2dImage = null;
        Map<Long, MultipartFile> areaImages = new HashMap<>();
        for (Map.Entry<String, MultipartFile> entry : request.getFileMap().entrySet()) {
            String key = entry.getKey();
            MultipartFile value = entry.getValue();

            String[] keyParts = key.split("-");
            if (keyParts[0].equals("map2dImage")) {
                map2dImage = value;
            } else if (keyParts[0].equals("areaImage")) {
                areaImages.put(Long.parseLong(keyParts[1]), value);
            }
        }

        adminGymService.uploadGym2dMap(gymId, map2dImage, areaImages);
    }

}

package com.SpringBoot.Plan4Land.Controller;

import com.SpringBoot.Plan4Land.DTO.TravelSpotReqDto;
import com.SpringBoot.Plan4Land.DTO.TravelSpotResDto;
import com.SpringBoot.Plan4Land.Service.TravelSpotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/spots")
public class TravelSpotController {

    private final TravelSpotService travelSpotService;


    // 페이지네이션 처리된 TravelSpot 데이터를 반환
    @GetMapping("/api/travelspots")
    public ResponseEntity<Page<TravelSpotResDto>> getTravelSpots(@RequestParam(defaultValue = "0") int currentPage,
                                                                 @RequestParam(defaultValue = "10") int pageSize,
                                                                 @RequestParam(required = false) Integer areaCode,
                                                                 @RequestParam(required = false) Integer subAreaCode,
                                                                 @RequestParam(required = false) String topTheme,
                                                                 @RequestParam(required = false) String middleTheme,
                                                                 @RequestParam(required = false) String bottomTheme,
                                                                 @RequestParam(required = false) String category,
                                                                 @RequestParam(required = false) String searchQuery) {

        List<String> bottomThemeList = (bottomTheme != null && !bottomTheme.isEmpty()) ? List.of(bottomTheme.split(",")) : List.of();


        Page<TravelSpotResDto> travelSpotResDtos = travelSpotService.getFilteredTravelSpots(currentPage, pageSize, areaCode, subAreaCode,
                topTheme, middleTheme, bottomThemeList, category, searchQuery);

        // 필터링 로직을 추가하여 여행지 데이터를 검색합니다.
        return ResponseEntity.ok(travelSpotResDtos);
    }

    // 여행지 상세 정보 조회
    @GetMapping("/api/travelspotInfo/{spotId}")
    public TravelSpotResDto getSpotDetail(@PathVariable Long spotId) {
        return travelSpotService.getSpotDetail(spotId); // 서비스 메서드 호출
    }

    // 여행지 탑5
    @GetMapping("/api/travelspotTop5")
    public ResponseEntity<List<TravelSpotResDto>> getTop5BookmarkedSpots() {
        List<TravelSpotResDto> top5Spots = travelSpotService.getTop5BookmarkedSpots();
        if (top5Spots.size() > 5) {
            top5Spots = top5Spots.subList(0, 5);
        }
        return ResponseEntity.ok(top5Spots);
    }


    @GetMapping("/nearby")
    public ResponseEntity<List<TravelSpotResDto>> getNearbySpots(
            @RequestParam double mapX,
            @RequestParam double mapY,
            @RequestParam(defaultValue = "5") double radius, // 기본 반경 5km
            @RequestParam Long spotId // 제외할 관광지 ID
    ) {
        List<TravelSpotResDto> nearbySpots = travelSpotService.getNearbySpotsExcludingId(mapX, mapY, radius, spotId);
        return ResponseEntity.ok(nearbySpots);
    }


}

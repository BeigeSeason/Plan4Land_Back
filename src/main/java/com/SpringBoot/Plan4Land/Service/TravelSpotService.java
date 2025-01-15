package com.SpringBoot.Plan4Land.Service;

import com.SpringBoot.Plan4Land.DTO.TravelSpotResDto;
import com.SpringBoot.Plan4Land.Entity.TravelSpot;
import com.SpringBoot.Plan4Land.Repository.BookMarkSpotRepository;
import com.SpringBoot.Plan4Land.Repository.TravelSpotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TravelSpotService {
    private final TravelSpotRepository travelSpotRepository;
    private final BookMarkSpotRepository bookMarkSpotRepository;

    public Page<TravelSpotResDto> getFilteredTravelSpots(int page, int size, Integer areaCode, Integer subAreaCode,
                                                    String topTheme, String middleTheme, List<String> bottomThemes, String cat, String searchQuery) {
        Pageable pageable = PageRequest.of(page, size);  // 페이지와 크기를 설정
        Page<TravelSpot> travelSpotPage = travelSpotRepository.getFilterTravelSpot(pageable, areaCode, subAreaCode,
                topTheme, middleTheme, bottomThemes, cat, searchQuery);

        // 페이지된 결과를 DTO로 변환하여 반환
        return travelSpotPage.map(this::convertToDTO);
    }

    // 상세 정보 조회
    public TravelSpotResDto getSpotDetail(Long spotId) {
        TravelSpot travelSpot = travelSpotRepository.findById(spotId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID로 여행지를 찾을 수 없습니다: " + spotId));

        int bookmarked = bookMarkSpotRepository.countBySpot(String.valueOf(spotId));
        TravelSpotResDto rsp = convertToDTO(travelSpot);
        rsp.setBookmark(bookmarked);

        log.warn(rsp.toString());

        return rsp;
    }

    // 북마크가 많은 상위 5개 여행지 반환
    public List<TravelSpotResDto> getTop5BookmarkedSpots() {
        // 상위 5개를 반환하는 쿼리 실행
        List<Object[]> topSpots = bookMarkSpotRepository.findTop5SpotsByBookmarkCount();

        return topSpots.stream().map(data -> {
            String spotId = (String) data[0];
            Long bookmarkCount = (Long) data[1];

            TravelSpot travelSpot = travelSpotRepository.findById(Long.parseLong(spotId))
                    .orElseThrow(() -> new IllegalArgumentException("TravelSpot not found with id: " + spotId));

            TravelSpotResDto dto = convertToDTO(travelSpot);
            dto.setBookmark(bookmarkCount.intValue());

            return dto;
        }).collect(Collectors.toList());
    }


    private TravelSpotResDto convertToDTO(TravelSpot travelSpot) {
        TravelSpotResDto dto = new TravelSpotResDto();
        dto.setId(travelSpot.getId());
        dto.setTitle(travelSpot.getTitle());
        dto.setTel(travelSpot.getTel());
        dto.setThumbnail(travelSpot.getThumbnail());
        dto.setAreaCode(travelSpot.getAreaCode());
        dto.setSigunguCode(travelSpot.getSigunguCode());
        dto.setAddr1(travelSpot.getAddr1());
        dto.setAddr2(travelSpot.getAddr2());
        dto.setCat1(travelSpot.getCat1());
        dto.setCat2(travelSpot.getCat2());
        dto.setCat3(travelSpot.getCat3());
        dto.setTypeId(travelSpot.getTypeId());
        dto.setCreatedTime(travelSpot.getCreatedTime());
        dto.setModifiedTime(travelSpot.getModifiedTime());
        dto.setMapX(travelSpot.getMapX());
        dto.setMapY(travelSpot.getMapY());
        return dto;
    }

}

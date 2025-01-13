package com.SpringBoot.Plan4Land.Service;

import com.SpringBoot.Plan4Land.DTO.TravelSpotReqDto;
import com.SpringBoot.Plan4Land.DTO.TravelSpotResDto;
import com.SpringBoot.Plan4Land.Entity.BookmarkSpot;
import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Entity.TravelSpot;
import com.SpringBoot.Plan4Land.Repository.BookMarkSpotRepository;
import com.SpringBoot.Plan4Land.Repository.MemberRepository;
import com.SpringBoot.Plan4Land.Repository.TravelSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookMarkSpotService {
    private final BookMarkSpotRepository bookMarkSpotRepository;
    private final MemberRepository memberRepository;
    private final TravelSpotRepository travelSpotRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // 북마크 추가
    public String addBookmark(String memberId, String spotId) {
        // id를 기준으로 회원 조회
        Optional<Member> member = memberRepository.findById(memberId);

        if (member.isEmpty()) {
            throw new IllegalArgumentException("해당 회원이 존재하지 않습니다.");
        }

        // 이미 북마크가 있는지 확인
        if (bookMarkSpotRepository.existsByMemberAndSpot(member.get(), spotId)) {
            return "이미 북마크한 장소입니다.";
        }

        // 북마크 추가
        BookmarkSpot newBookmark = new BookmarkSpot();
        newBookmark.setMember(member.get());
        newBookmark.setSpot(spotId);

        bookMarkSpotRepository.save(newBookmark);
        return "북마크가 추가되었습니다.";
    }

    // 북마크 삭제
    public String removeBookmark(String memberId, String spotId) {
        Optional<Member> member = memberRepository.findById(memberId);

        if (member.isEmpty()) {
            throw new IllegalArgumentException("해당 회원이 존재하지 않습니다.");
        }

        // 북마크 존재 여부 확인
        Optional<BookmarkSpot> bookmarkSpot = bookMarkSpotRepository.findByMemberAndSpot(member.get(), spotId);
        if (bookmarkSpot.isEmpty()) {
            return "북마크가 존재하지 않습니다.";
        }

        // 북마크 삭제
        bookMarkSpotRepository.delete(bookmarkSpot.get());
        return "북마크가 삭제되었습니다.";
    }
    // 특정 사용자가 특정 여행지를 북마크했는지 확인
    public boolean isBookmarked(String memberId, String spotId) {
        Optional<Member> member = memberRepository.findById(memberId);

        if (member.isEmpty()) {
            throw new IllegalArgumentException("해당 회원이 존재하지 않습니다.");
        }

        return bookMarkSpotRepository.existsByMemberAndSpot(member.get(), spotId);
    }

    // 내가 북마크한 모든 여행지의 정보 가져오기
    public List<TravelSpotReqDto> getAllBookmarkedSpots(String memberId) {
        // 1. 회원 조회
        Optional<Member> member = memberRepository.findById(memberId);

        if (member.isEmpty()) {
            throw new IllegalArgumentException("해당 회원이 존재하지 않습니다.");
        }

        // 2. 회원이 북마크한 모든 여행지 조회
        List<BookmarkSpot> bookmarkedSpots = bookMarkSpotRepository.findByMember(member.get());

        if (bookmarkedSpots.isEmpty()) {
            throw new IllegalArgumentException("북마크한 여행지가 없습니다.");
        }

        // 3. 여행지 상세 정보를 DTO로 변환
        List<TravelSpotReqDto> travelSpotReqDtos = bookmarkedSpots.stream()
                .map(bookmark -> {
                    Optional<TravelSpot> travelSpot = travelSpotRepository.findById(Long.valueOf(bookmark.getSpot()));
                    return travelSpot.map(spot -> new TravelSpotReqDto(
                            spot.getId(),
                            spot.getTitle(),
                            spot.getTel(),
                            spot.getThumbnail(),
                            spot.getAreaCode(),
                            spot.getSigunguCode(),
                            spot.getAddr1(),
                            spot.getAddr2(),
                            spot.getCat1(),
                            spot.getCat2(),
                            spot.getCat3(),
                            spot.getTypeId(),
                            spot.getCreatedTime(),
                            spot.getModifiedTime(),
                            spot.getMapX(),
                            spot.getMapY()
                    )).orElse(null);
                })
                .collect(Collectors.toList());

        return travelSpotReqDtos;
    }

}

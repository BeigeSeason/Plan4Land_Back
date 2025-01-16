package com.SpringBoot.Plan4Land.Service;

import com.SpringBoot.Plan4Land.Constant.State;
import com.SpringBoot.Plan4Land.DTO.PlannerMembersResDto;
import com.SpringBoot.Plan4Land.DTO.PlannerReqDto;
import com.SpringBoot.Plan4Land.DTO.PlannerResDto;
import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Entity.Planner;
import com.SpringBoot.Plan4Land.Entity.PlannerMembers;
import com.SpringBoot.Plan4Land.Repository.BookMarkPlannerRepository;
import com.SpringBoot.Plan4Land.Repository.MemberRepository;
import com.SpringBoot.Plan4Land.Repository.PlannerMembersRepository;
import com.SpringBoot.Plan4Land.Repository.PlannerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlannerService {
    private final PlannerRepository plannerRepository;
    private final MemberRepository memberRepository;
    private final PlannerMembersRepository plannerMembersRepository;
    private final BookMarkPlannerRepository bookMarkPlannerRepository;

    @Transactional
    public Long makePlanner(PlannerReqDto plannerReqDto) {
        try {
            Member member = memberRepository.findById(plannerReqDto.getId())
                    .orElseThrow(() -> new RuntimeException("Planner 작성 중 회원 조회 실패"));

            Planner planner = plannerReqDto.toEntity(member);
            Planner savedPlanner = plannerRepository.save(planner);

            return savedPlanner.getId();
        } catch (RuntimeException e) {
            log.error("존재하지 않는 회원입니다.");
            return null;
        } catch (Exception e) {
            log.error("Planner 생성 실패 : {}", e.getMessage());
            return null;
        }
    }

    public PlannerResDto findByPlannerId(Long id) {
        Planner planner = plannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 Planner가 존재하지 않습니다."));
        List<PlannerMembers> participants = plannerMembersRepository.findByPlannerId(id);
        List<PlannerMembersResDto> participantDtos = participants.stream()
                .map(member -> {
                    // PlannerMembers의 상태를 함께 조회
                    String state = member.getState() != null ? member.getState().name() : null;
                    return new PlannerMembersResDto(
                            member.getMember().getId(),
                            member.getMember().getNickname(),
                            member.getMember().getProfileImg(),
                            state // 상태를 포함
                    );
                })
                .collect(Collectors.toList());
        Long bookmarkCount = bookMarkPlannerRepository.countByPlannerId(planner.getId());
        return PlannerResDto.fromEntity(planner, participantDtos, bookmarkCount);
    }

    public Page<PlannerResDto> getFilterdPlanner(int currentPage, int pageSize, String areaCode, String subAreaCode,
                                                 String themeList, String searchQuery, String sortBy) {

        String[] arr = themeList == null ? null : themeList.split(",");
        String theme1 = arr != null && arr.length > 0 ? arr[0] : null;
        String theme2 = arr != null && arr.length > 1 ? arr[1] : null;
        String theme3 = arr != null && arr.length > 2 ? arr[2] : null;

        Sort sort = Sort.by(Sort.Direction.DESC, "regDate");
        if (sortBy.equalsIgnoreCase("LatestAsc")) {
            sort = Sort.by(Sort.Direction.ASC, "regDate");
        } else if (sortBy.equalsIgnoreCase("BookmarkDesc")) {
            sort = Sort.by(Sort.Direction.DESC, "bookmarkCount");
        } else if (sortBy.equalsIgnoreCase("BookmarkAsc")) {
            sort = Sort.by(Sort.Direction.ASC, "bookmarkCount");
        }

        Pageable pageable = PageRequest.of(currentPage, pageSize, sort);

        Page<Object[]> queryResults = plannerRepository.findFilteredPlanners(pageable, areaCode, subAreaCode, searchQuery, theme1, theme2, theme3);

        List<PlannerResDto> result = queryResults.getContent().stream()
                .map(resultArray -> {
                    Planner planner = (Planner) resultArray[0];
                    Long bookmarkCount = (Long) resultArray[1];
                    return PlannerResDto.fromEntity(planner, null, bookmarkCount);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(result, pageable, queryResults.getTotalElements());
    }





    public List<PlannerResDto> getTop3BookmarkedPlanners() {
        List<Long> topPlannerIds = bookMarkPlannerRepository.findTop3PlannerIdsByBookmarkCount();
        List<Planner> topPlanners = plannerRepository.findAllById(topPlannerIds);
        return topPlanners.stream()
                .map(planner -> {
                    Long bookmarkCount = bookMarkPlannerRepository.countByPlannerId(planner.getId());
                    return PlannerResDto.fromEntity(planner, null, bookmarkCount);
                })
                .collect(Collectors.toList());
    }

    public Page<Planner> getPlannersByOwner(String memberId, Pageable pageable) {
        return plannerRepository.findByOwnerId(memberId, pageable);
    }

    // 플래닝에 멤버 초대
    public boolean inviteMember(String memberId, Long plannerId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("해당 멤버를 찾을 수 없습니다."));
        Planner planner = plannerRepository.findById(plannerId)
                .orElseThrow(() -> new RuntimeException("해당 플래너를 찾을 수 없습니다."));

        PlannerMembers plannerMembers = new PlannerMembers();
        plannerMembers.setMember(member);
        plannerMembers.setPlanner(planner);
        plannerMembersRepository.save(plannerMembers);

        return true;
    }

    // 초대받은 플래닝 조회
    public List<PlannerResDto> selectInvitedPlanners(String memberId) {
        List<PlannerMembers> invites = plannerMembersRepository.findByMemberIdAndState(memberId, State.WAIT);
        return invites.stream()
                .map(plannerMember -> {
                    Planner planner = plannerMember.getPlanner();

                    List<PlannerMembersResDto> participantDtos = plannerMembersRepository.findByPlannerId(planner.getId())
                            .stream()
                            .map(member -> {
                                String state = member.getState() != null ? member.getState().name() : null;
                                return new PlannerMembersResDto(
                                        member.getMember().getId(),
                                        member.getMember().getNickname(),
                                        member.getMember().getProfileImg(),
                                        state // 상태 포함
                                );
                            })
                            .collect(Collectors.toList());

                    // 4. PlannerResDto로 변환 후 반환
                    return PlannerResDto.fromEntity(planner, participantDtos, 0L);
                })
                .collect(Collectors.toList());
    }

    // 초대 수락
    public boolean acceptInvitation(String memberId, Long plannerId) {
        PlannerMembers plannerMember = plannerMembersRepository
                .findByMemberIdAndPlannerId(memberId, plannerId)
                .orElseThrow(() -> new IllegalArgumentException("초대 정보가 존재하지 않습니다."));
        plannerMember.setState(State.ACCEPT);
        plannerMembersRepository.save(plannerMember);

        return true;
    }

    // 초대 거절
    public boolean rejectInvitation(String memberId, Long plannerId) {
        PlannerMembers plannerMember = plannerMembersRepository
                .findByMemberIdAndPlannerId(memberId, plannerId)
                .orElseThrow(() -> new IllegalArgumentException("초대 정보가 존재하지 않습니다."));
        plannerMembersRepository.delete(plannerMember);

        return true;
    }

    public Page<PlannerResDto> getPlanners(String memberId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Member owner = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("User not found"));

        // Repository에서 플래너 조회
        Page<Planner> planners = plannerRepository.findPlannersByOwnerOrMember(owner, pageable);

        // Dto로 변환
        List<PlannerResDto> plannerDtos = planners.getContent().stream()
                .map(planner -> PlannerResDto.fromEntity(planner, null, 0L)) // bookmarkCount는 0으로 설정
                .collect(Collectors.toList());

        // 페이지네이션 처리된 결과 반환
        return new PageImpl<>(plannerDtos, pageable, planners.getTotalElements());
    }

}

package com.SpringBoot.Plan4Land.Service;

import com.SpringBoot.Plan4Land.Repository.MemberRepository;
import com.SpringBoot.Plan4Land.Repository.PlannerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlannerService {
    private final PlannerRepository plannerRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public boolean createPlanner(){
        try{
            return true;
        } catch (Exception e){
            log.error(e.getMessage());
            return false;
        }
    }
}

package com.SpringBoot.Plan4Land.Repository;

import com.SpringBoot.Plan4Land.Entity.ChatMsg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMsgRepository extends JpaRepository<ChatMsg, Long> {
    void deleteByPlannerId(Long plannerId);
    List<ChatMsg> findTop10ByPlannerIdOrderBySendTimeDesc(Long plannerId);
}

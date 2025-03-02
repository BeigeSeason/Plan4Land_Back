package com.SpringBoot.Plan4Land.Repository;

import com.SpringBoot.Plan4Land.DTO.PlannerResDto;
import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Entity.Planner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface PlannerRepository extends JpaRepository<Planner, Long> {
    Optional<Planner> findById(Long id);

    Page<Planner> findAll(Pageable pageable);

    @Query(value = """
            SELECT p
            FROM Planner p
            WHERE (:areaCode IS NULL OR p.area = :areaCode)
              AND (:subAreaCode IS NULL OR p.subArea = :subAreaCode)
              AND (:searchQuery IS NULL OR p.title LIKE %:searchQuery%)
              AND (
                     (:theme1 IS NULL OR p.theme LIKE %:theme1%)
                     OR (:theme2 IS NOT NULL AND p.theme LIKE %:theme2%)
                     OR (:theme3 IS NOT NULL AND p.theme LIKE %:theme3%)
                     )
              AND p.isPublic = true
            """)
    List<Planner> getFilteredPlanners(
                                      @Param("areaCode") String areaCode,
                                      @Param("subAreaCode") String subAreaCode,
                                      @Param("searchQuery") String searchQuery,
                                      @Param("theme1") String theme1,
                                      @Param("theme2") String theme2,
                                      @Param("theme3") String theme3);
//    @Query(value = """
//            SELECT p, COUNT(b.planner.id)
//            FROM Planner p
//            LEFT JOIN BookmarkPlanner b ON p.id = b.planner.id
//            WHERE (:areaCode IS NULL OR p.area = :areaCode)
//              AND (:subAreaCode IS NULL OR p.subArea = :subAreaCode)
//              AND (:searchQuery IS NULL OR p.title LIKE %:searchQuery%)
//              AND (
//                     (:theme1 IS NULL OR p.theme LIKE %:theme1%)
//                     OR (:theme2 IS NOT NULL AND p.theme LIKE %:theme2%)
//                     OR (:theme3 IS NOT NULL AND p.theme LIKE %:theme3%)
//                     )
//              AND p.isPublic = true
//            GROUP BY p.id
//            """,
//    countQuery = """
//            SELECT COUNT(DISTINCT p.id)
//            FROM Planner p
//                        LEFT JOIN BookmarkPlanner b
//                        ON p.id = b.planner.id
//                        WHERE (:areaCode IS NULL OR p.area = :areaCode)
//                        AND (:subAreaCode IS NULL OR p.subArea = :subAreaCode)
//                        AND (:searchQuery IS NULL OR p.title LIKE %:searchQuery%)
//                        AND (
//                                    (:theme1 IS NULL OR p.theme LIKE %:theme1%)
//                                    OR (:theme2 IS NOT NULL AND p.theme LIKE %:theme2%)
//                                    OR (:theme3 IS NOT NULL AND p.theme LIKE %:theme3%)
//                             )
//                        AND p.isPublic = true
//            """)
//    Page<Object[]> findFilteredPlanners(Pageable pageable,
//                                        @Param("areaCode") String areaCode,
//                                        @Param("subAreaCode") String subAreaCode,
//                                        @Param("searchQuery") String searchQuery,
//                                        @Param("theme1") String theme1,
//                                        @Param("theme2") String theme2,
//                                        @Param("theme3") String theme3);




    Page<Planner> findByOwnerId(String memberId, Pageable pageable);
    Page<Planner> findByOwnerIdAndIsPublicTrue(String memberId, Pageable pageable);


    @Query("SELECT p FROM Planner p " +
            "WHERE p.owner = :owner OR p.id IN (" +
            "SELECT pm.planner FROM PlannerMembers pm WHERE pm.member = :owner AND pm.state = 'ACCEPT')")
    Page<Planner> findPlannersByOwnerOrMember(Member owner, Pageable pageable);

    @Query("SELECT MAX(p.id) FROM Planner p")
    Long findLastId();


    // integer
//    boolean removePlannerById(Long id);
}

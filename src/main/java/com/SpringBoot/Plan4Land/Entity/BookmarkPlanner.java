package com.SpringBoot.Plan4Land.Entity;

import lombok.*;

import javax.persistence.*;

@Table(name="Bookmark_Planner")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkPlanner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bk_pl_id")
    private Long id;

    // 북마크한 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 북마크 된 플래너
    @ManyToOne
    @JoinColumn(name = "planner_id")
    private Planner planner;

}

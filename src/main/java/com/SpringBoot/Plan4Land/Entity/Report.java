package com.SpringBoot.Plan4Land.Entity;

import com.SpringBoot.Plan4Land.Constant.ReportState;
import lombok.*;

import javax.persistence.*;

@Table(name = "Report")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    // 신고자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private Member reporter;

    // 피신고자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_id")
    private Member reported;

    // 신고내용
    @Lob
    private String content;

    // 상태 (대기, 승인, 거절)
    @Enumerated(EnumType.STRING)
    private ReportState state;

    @PrePersist
    protected void onCreate() {
        this.state = ReportState.WAIT;
    }
}

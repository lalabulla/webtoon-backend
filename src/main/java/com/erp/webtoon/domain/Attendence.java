package com.erp.webtoon.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Attendence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "atd_id")
    private Long id;

    private int attendMonth; // 기준월

    private String attendDate;  //  기준일

    private String attendType;  // 근태타입

    private LocalDateTime attendTime;  // 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Attendence(int attendMonth, String attendDate, String attendType, LocalDateTime attendTime, User user) {
        this.attendMonth = attendMonth;
        this.attendDate = attendDate;
        this.attendType = attendType;
        this.attendTime = attendTime;
        this.user = user;
    }
}

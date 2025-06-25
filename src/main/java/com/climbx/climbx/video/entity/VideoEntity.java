package com.climbx.climbx.video.entity;

import com.climbx.climbx.common.entity.BaseTimeEntity;
import com.climbx.climbx.submission.entity.SubmissionEntity;
import com.climbx.climbx.user.entity.UserAccountEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Table(name = "videos")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Getter
@Accessors(fluent = true)
@Builder
public class VideoEntity extends BaseTimeEntity {
    /*
     * 비디오 엔티티 임시 구현.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_id", updatable = false, nullable = false)
    private Long videoId;

    @Column(name = "user_id", insertable = false, updatable = false, nullable = false)
    private Long userId; // 사용자 ID, UserAccountEntity와 동일한 ID 사용

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private UserAccountEntity userAccountEntity;

    @OneToOne(mappedBy = "videoEntity", fetch = FetchType.LAZY)
    private SubmissionEntity submissionEntity; // 비디오 제출 엔티티
}

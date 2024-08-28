package org.hermez.instructor.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 강사 목록을 반환하기 위한 DTO 클래스입니다.
 *
 * @author 김다은
 */
@Getter
@Setter
@NoArgsConstructor
public class InstructorListResponse {
    /**
     * 강사 ID.
     */
    private int instructorId;

    /**
     * 회원 ID (강사와 연관된 회원의 ID).
     */
    private int memberId;

    /**
     * 주제 ID (강사가 가르치는 주제의 ID).
     */
    private int subjectId;

    /**
     * 강사 이름.
     */
    private String name;

    /**
     * 강사 상태 (예: 활동 중, 휴식 중 등).
     */
    private String instructorStatus;

    /**
     * 강사 제목 (강사의 직함 또는 역할).
     */
    private String instructorTitle;

    /**
     * 저장된 이미지의 경로 또는 URL.
     */
    private String saveImage;
}

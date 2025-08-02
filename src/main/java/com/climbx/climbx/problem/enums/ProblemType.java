package com.climbx.climbx.problem.enums;

import com.climbx.climbx.common.exception.InvalidEnumValueException;
import com.climbx.climbx.common.util.OptionalUtil;

public enum ProblemType {

    PINCH_HOLD("핀치 홀드"),
    CRIMP_HOLD("크림프 홀드"),
    SLOPER_HOLD("슬로퍼 홀드"),
    POCKET_HOLD("포켓 홀드"),

    REACH("리치"),
    BALANCE("밸런스"),
    OVERHANG("오버행"),
    BATHANG("배트행"),

    LUNGE("런지"),
    DYNO("다이노"),
    CODINATE("코디네이트");

    private final String tag;

    ProblemType(String tag) {
        this.tag = tag;
    }

    public static ProblemType from(String name) {
        return OptionalUtil.tryOf(() -> valueOf(name))
            .orElseThrow(() -> new InvalidEnumValueException("ProblemType", name));
    }
}

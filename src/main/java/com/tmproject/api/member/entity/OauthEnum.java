package com.tmproject.api.member.entity;

public enum OauthEnum {
    KAKAO(OauthEnum.Authority.KAKAO),
    NAVER(OauthEnum.Authority.NAVER),
    GOOGLE(OauthEnum.Authority.GOOGLE);

    private final String authority;

    OauthEnum(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }

    public static class Authority {
        public static final String KAKAO = "KAKAO";
        public static final String NAVER = "NAVER";
        public static final String GOOGLE = "GOOGLE";
    }
}

package com.jidang.Title;
/*
 칭호(업적)의 메타정보를 관리하는 Enum
 - DB 테이블 없이 이름/설명/카테고리/아이콘을 코드로만 관리
 칭호 추가후 TitleService에서 기능 추가 또한 필요
 */
public enum TitleInfo {

    // 이름, 설명, 카테고리, 아이콘(이모지)
    STRATEGY_MASTER("공략마스터", "공략 태그 글 5개 이상 작성", "공략", "🎮"),
    COMMENT_KING("수다쟁이", "댓글 20개 이상 작성", "커뮤니티", "💬"),
    VETERAN("고인물", "총 플레이타임 10000시간 이상", "플레이 타임", "🕒"),
    FANART_MASTER("팬아트 장인", "팬아트 게시물을 10개 이상 작성", "팬아트", "🎨");

    private final String displayName;   // SiteUser.titles 에 저장되는 한글 이름
    private final String description;
    private final String category;
    private final String icon;

    TitleInfo(String displayName, String description, String category, String icon) {
        this.displayName = displayName;
        this.description = description;
        this.category = category;
        this.icon = icon;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getIcon() { return icon; }

    // SiteUser.titles 에 저장된 문자열 → TitleInfo 로 변환
    public static TitleInfo fromDisplayName(String name) {
        for (TitleInfo t : values()) {
            if (t.displayName.equals(name)) {
                return t;
            }
        }
        return null;
    }
}

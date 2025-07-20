package com.blessing.channel.model
// 📄 com.blessing.channel.model.HelperType.kt

enum class HelperType(
    val title: String,
    val placeholder: String,
    val buttonText: String,
    val requestURL: String,
    val instruction: String
) {
    COUPANG(
        title = "쿠팡 검색",
        placeholder = "예: 맥북 케이스, 아이패드 거치대",
        buttonText = "쿠팡에서 검색",
        requestURL = "/getCoupangLink",
        instruction = "예: 맥북 케이스, 아이패드 거치대"
    ),
    YOUTUBE(
        title = "유튜브 검색",
        placeholder = "예: 이적 노래 검색",
        buttonText = "유튜브에서 검색",
        requestURL = "/getYouTubeLink",
        instruction = "예: 이적 노래"
    ),
    RESERVATION(
        title = "네이버 예약 도우미",
        placeholder = "예: 세종 서울현병원 예약하고 싶어",
        buttonText = "예약 링크 찾기",
        requestURL = "/getReservationLink",
        instruction = "예: 세종 서울현병원 예약하고 싶어"
    ),
    KAKAO(
        title = "카카오 메시지",
        placeholder = "예: 엄마에게 사랑한다고 보내줘",
        buttonText = "카카오톡 메시지 보내기",
        requestURL = "/parseMessageCommand",
        instruction = "예: 엄마에게 사랑한다고 보내줘"
    )
}

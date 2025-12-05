/* postlist.js */

/**
 * 게시물 카드 클릭 → 상세페이지 이동
 */
document.addEventListener("DOMContentLoaded", () => {

    const cards = document.querySelectorAll(".post-card");

    cards.forEach(card => {
        const href = card.getAttribute("data-href");

        // href가 존재한다면 클릭 이벤트 등록
        if (href) {
            card.style.cursor = "pointer";

            card.addEventListener("click", () => {
                window.location.href = href;
            });
        }
    });

});

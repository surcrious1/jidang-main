document.addEventListener("DOMContentLoaded", () => {
    // 좋아요 버튼: /post/like/{id} 로 이동 (기존 토글 로직 재사용)
    const likeButton = document.getElementById("likeButton");
    if (likeButton) {
        likeButton.addEventListener("click", () => {
            const url = likeButton.dataset.likeUrl;
            if (url) {
                window.location.href = url;
            }
        });
    }

    // 댓글 버튼: 댓글 입력창으로 포커스
    const commentButton = document.getElementById("commentButton");
    if (commentButton) {
        commentButton.addEventListener("click", () => {
            const textarea = document.querySelector(".comment-textarea");
            if (textarea) {
                textarea.focus();
            }
        });
    }

    // 공유 버튼: 나중에 실제 공유 기능 붙일 때 교체
    const shareButton = document.getElementById("shareButton");
    if (shareButton) {
        shareButton.addEventListener("click", () => {
            // TODO: 추후 클립보드 복사 / 공유 모달 등 기능 추가
            alert("공유 기능은 추후 추가될 예정입니다.");
        });
    }
});

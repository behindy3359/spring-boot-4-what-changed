const boardId = document.getElementById('boardIdInput').value;

document.addEventListener('DOMContentLoaded', loadComments);

function loadComments() {
    fetch(`/api/boards/${boardId}/comments`)
        .then(response => response.json())
        .then(comments => {
            const totalCount = comments.reduce((sum, comment) =>
                sum + 1 + comment.replies.length, 0);
            document.getElementById('commentCount').textContent = totalCount;

            const commentList = document.getElementById('commentList');
            commentList.innerHTML = '';

            if (comments.length === 0) {
                commentList.innerHTML = '<p class="text-muted text-center py-4">첫 번째 댓글을 작성해보세요!</p>';
                return;
            }

            comments.forEach(comment => {
                const commentElement = createCommentElement(comment, false);
                commentList.appendChild(commentElement);

                if (comment.replies && comment.replies.length > 0) {
                    comment.replies.forEach(reply => {
                        const replyElement = createCommentElement(reply, true);
                        commentList.appendChild(replyElement);
                    });
                }
            });
        });
}

function createCommentElement(comment, isReply) {
    const div = document.createElement('div');
    div.className = isReply
        ? 'border-bottom pb-3 mb-3 ms-5 bg-light p-3 rounded'
        : 'border-bottom pb-3 mb-3';

    const replyIcon = isReply ? '<i class="bi bi-arrow-return-right me-2"></i>' : '';
    const replyButton = !isReply
        ? `<button class="btn btn-sm btn-outline-primary ms-2" onclick="showReplyForm(${comment.id})">
               <i class="bi bi-reply"></i> 답글
           </button>`
        : '';

    div.innerHTML = `
        <div class="d-flex justify-content-between align-items-start">
            <div class="flex-grow-1">
                <div class="mb-2">
                    ${replyIcon}
                    <span class="badge me-2" style="background-color: ${comment.ipColorCode}; color: white;">
                        <i class="bi bi-person-circle"></i> ${comment.nickname}
                    </span>
                    <small class="text-muted">${comment.maskedIp}</small>
                    <small class="text-muted ms-3">${comment.createdDate}</small>
                    ${!isReply && comment.replyCount > 0 ? `<span class="badge bg-info ms-2">답글 ${comment.replyCount}</span>` : ''}
                </div>
                <p class="mb-0">${comment.content}</p>
                ${!isReply ? `
                    <div id="replyForm${comment.id}" class="mt-3 p-3 bg-light rounded" style="display: none;">
                        <form onsubmit="submitReply(event, ${comment.id})">
                            <div class="row g-2 mb-2">
                                <div class="col-md-6">
                                    <input type="text" class="form-control form-control-sm"
                                           id="replyNickname${comment.id}" placeholder="닉네임" required>
                                </div>
                                <div class="col-md-6">
                                    <input type="password" class="form-control form-control-sm"
                                           id="replyPassword${comment.id}" placeholder="비밀번호" minlength="4" required>
                                </div>
                            </div>
                            <textarea class="form-control form-control-sm mb-2" rows="2"
                                      id="replyContent${comment.id}" placeholder="답글 입력..." required></textarea>
                            <button type="submit" class="btn btn-sm btn-primary">답글 작성</button>
                            <button type="button" class="btn btn-sm btn-secondary"
                                    onclick="hideReplyForm(${comment.id})">취소</button>
                        </form>
                    </div>
                ` : ''}
            </div>
            <div>
                ${replyButton}
                <button class="btn btn-sm btn-outline-danger" onclick="deleteComment(${comment.id})">
                    <i class="bi bi-trash"></i>
                </button>
            </div>
        </div>
    `;
    return div;
}

function showReplyForm(commentId) {
    document.getElementById('replyForm' + commentId).style.display = 'block';
}

function hideReplyForm(commentId) {
    document.getElementById('replyForm' + commentId).style.display = 'none';
}

function submitReply(event, parentId) {
    event.preventDefault();

    const nickname = document.getElementById('replyNickname' + parentId).value;
    const password = document.getElementById('replyPassword' + parentId).value;
    const content = document.getElementById('replyContent' + parentId).value;

    fetch(`/api/boards/${boardId}/comments/${parentId}/reply`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nickname, password, content })
    })
    .then(response => {
        if (response.ok) {
            hideReplyForm(parentId);
            loadComments();
        } else {
            alert('대댓글 작성에 실패했습니다.');
        }
    });
}

document.getElementById('commentForm').addEventListener('submit', function(e) {
    e.preventDefault();

    const nickname = document.getElementById('commentNickname').value;
    const password = document.getElementById('commentPassword').value;
    const content = document.getElementById('commentContent').value;

    fetch(`/api/boards/${boardId}/comments`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ nickname, password, content })
    })
    .then(response => response.json())
    .then(() => {
        document.getElementById('commentNickname').value = '';
        document.getElementById('commentPassword').value = '';
        document.getElementById('commentContent').value = '';
        loadComments();
    })
    .catch(error => alert('댓글 작성에 실패했습니다.'));
});

function deleteComment(commentId) {
    const password = prompt('댓글 비밀번호를 입력하세요:');
    if (!password) return;

    fetch(`/api/boards/${boardId}/comments/${commentId}?password=${password}`, {
        method: 'DELETE'
    })
    .then(response => {
        if (response.ok) {
            loadComments();
        } else {
            alert('비밀번호가 일치하지 않습니다.');
        }
    })
    .catch(error => alert('댓글 삭제에 실패했습니다.'));
}

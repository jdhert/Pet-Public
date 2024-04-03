<template>
  <div class="reply2-content">
    <img class="reply2-profile-image" :src="reply.imgPath" alt="Profile" />
    <div class="comment-content2">
      <div class="comment-row-11">
        <div class="user2">{{ reply.name }}</div>
        <div class="time-commented">{{ reply.createdAt.slice(0, 10) }}</div>
        <div v-if="$cookies.get('id') == reply.userId" class="reply-comment-interactions1">
          <button class="btn-edit-comment" @click.prevent="editReplyComment(reply)">
            <i class="fas fa-edit"></i> 
          </button>
          <button class="btn-delete-comment" @click.prevent="goToDelete(reply)">
            <i class="fas fa-trash-alt"></i> 
          </button>
        </div>
      </div>
      <div class="like-commented2">
        <div v-show="!reply.isEditing" class="user2-comment">{{ reply.content }}</div>
        <div v-show="reply.isEditing" class="user2-comment">
          <input type="text" class="editCommnet-input" v-model="reply.newContent" @keydown.enter.prevent="saveEditReplyComment(reply)" />
        </div>
        <div class="comment-like2" @click="toggleReplyLike(reply)">
          <i :class="['fas', 'fa-heart', { 'filled': reply.liked }]"></i>
          <div>{{ reply.likeCount }}</div>  
        </div>
      </div>
    </div>
  </div>
  <div class="re-comment" @click.prevent="toggleReplyInput(reply)">답글 달기</div>
  <div class="re-comment-input" v-if="replyInputStates[reply.id]"> 
    <input class="addNewComment" type="text" v-model="newReplyContent" @keydown.enter="saveNewReply(reply)" />
  </div>
  <div class="toggle-replies" @click.prevent="toggleReplies(reply)">
    {{ reply.showReplies ? '답글 숨기기' : (reply.child && reply.child > 0 ? '── 답글 ' + reply.child + '개 더 보기' : '') }}
  </div>
  <div class="replies" v-if="reply.showReplies">
    <ReplyComponent v-for="reply3 in reply.replies" :liked="reply3.liked" :key="reply3.id" :reply="reply3" :currentUserId="$cookies.get('id')" :selectedCard="selectedCard" :selectedPost="selectedPost" @deleteAction="deleteReplyComment" @checkChangeCount="updateCommentCount" />
  </div>
</template>

<script>
export default {
  props: ['reply', 'currentUserId', 'selectedCard', 'selectedPost', 'liked'],
  data() {
    return {
      isEditing: false, 
      newContent: '',
      replyInputStates: {},
      comments: [],
    };
  },
  mounted() {
    console.log(this.reply);
    this.fetchReplyLikeStatus(this.reply);
    this.fetchProfileImage(this.reply.userId);
    this.fetchCommentCount();
  },
  methods: {
    updateCommentCount(){
      this.$emit('checkChangeCount')
    },
    fetchCommentCount() {
      const boardId = this.selectedCard ? this.selectedCard.id : this.selectedPost.id;
      this.axios.get(`/api/comment/totalCount`, {
        params: {
          boardId: boardId
        }
      })
      .then((res) => {
        this.commentCount = res.data;
      })
      .catch(error => {
        console.error('댓글 개수를 가져오는 중 오류가 발생했습니다.', error);
      });
    },
    fetchProfileImage(userId){
      if (userId && !this.reply.imgPath.startsWith('http')) {
        this.axios.get(`/api/myinfo/img/${userId}`)
          .then((res) => {
            console.log('프로필 이미지를 성공적으로 불러왔습니다.');
            this.reply.imgPath = res.data; // 이미지 경로 업데이트
          })
          .catch((error) => {
            console.log('프로필 이미지를 불러오는데 실패했습니다:', error);
          });
      } 
    },
    goToDelete(replyId){
      const boardId = this.selectedCard ? this.selectedCard.id : this.selectedPost.id;
      this.$emit('deleteAction', replyId, boardId);
    },
    editReplyComment(reply) {
      reply.isEditing = !reply.isEditing;
      reply.oldContent = reply.content;
      reply.newContent = reply.content;
    },
                
    saveEditReplyComment(reply) {
      const parentCommentId = reply.parentCommentId;

      this.axios.put(`/api/comment/${reply.id}/replies`, {
        commentId: reply.id,
        content: reply.newContent
      }).then(() => {
        reply.content = reply.newContent;
        reply.isEditing = false;
      }).catch(error => {
        console.error('댓글 수정 중 오류가 발생했습니다.', error);
      });
    },
    toggleReplyLike(reply) {
      let liked = !reply.liked;
      reply.liked = liked;
      const boardId = this.selectedCard ? this.selectedCard.id : this.selectedPost.id;

      this.axios.post(`/api/comment/replyLiked`, {
        userId: this.$cookies.get('id'),
        boardId: boardId,
        commentId: reply.id,
        liked: liked,   
      })
      .then((res)=>{
        // console.log(res);
        if(res.data === true) {
          reply.likeCount++;
          reply.liked = true;
        } else {
          reply.likeCount--;
          reply.liked = false;
        }
        this.updateReplyLikeStatus(reply.id, liked);
        // console.log(reply.id, liked);
      })
      .catch(error => {
        console.log('대댓글 좋아요 상태를 업데이트하는 중 오류가 발생했습니다.', error);
      });     
    },
    updateReplyLikeStatus(replyId, liked) {
      // console.log(replyId, liked);
      this.axios.put(`/api/comment/${replyId}/replyLike`, null, {
        params: { liked }
      })    
      .then(() => {
        console.log('댓글 좋아요 상태가 업데이트되었습니다.');
      })
      .catch(error => {
        console.error('댓글 좋아요 상태를 업데이트하는 중 오류가 발생했습니다.', error);
      });
    },
    // 댓글 좋아요 상태 유지
    fetchReplyLikeStatus(reply) {
      // console.log('reply', reply);
      this.axios.get(`/api/comment/${reply.id}/likeStatus`)
      .then(res => {
        // console.log(res);
        const replyLiked = res.data;
        // console.log(replyLiked);
        reply.liked = replyLiked === true;
      })
      .catch(error => {
        console.error('댓글 좋아요 상태를 불러오는 중 오류가 발생했습니다.', error);
      });
    },
    toggleReplyInput(reply) {
      if (this.replyInputStates[reply.id]) {
        this.replyInputStates[reply.id] = false; 
      } else {
        for (const id in this.replyInputStates) {
          if (Object.hasOwnProperty.call(this.replyInputStates, id)) {
            this.replyInputStates[id] = false;
          }
        }
        this.replyInputStates[reply.id] = true;
      }
    },  
    saveNewReply(reply) {
      if (!this.newReplyContent) {
        console.error('댓글 내용이 유효하지 않습니다.');
        return;
      }
      
      const boardId = this.selectedCard ? this.selectedCard.id : this.selectedPost.id;
      this.axios.post(`api/comment/${reply.id}/replies`, {
        boardId: boardId,
        userId: this.$cookies.get('id'),   
        content: this.newReplyContent
      }).then(res => {
        console.log('댓글이 성공적으로 등록되었습니다.', res.data);
        
        this.fetchProfileImage();
        this.fetchCommentCount(); 
        
        if(reply.replies) {
          reply.replies = [];
        }    
        reply.child = reply.child + 1;
        this.newReplyContent = '';
        this.replyInputStates[reply.id] = false;
  
        this.$emit('checkChangeCount');
  
      }).catch(error => {
        console.error('댓글 등록 중 오류가 발생했습니다.', error);
      });
    },
    toggleReplies(reply) {
      if (typeof reply === 'object') {
        reply.showReplies = !reply.showReplies;
        const boardId = this.selectedCard ? this.selectedCard.id : this.selectedPost.id;

        this.axios.get(`/api/comment/detailcomment`, {
          params: {
            id: boardId,
            parentCommentId: reply.id
          }
        }).then((res) => {
          if (res.data && res.data.length > 0) {
            reply.replies = res.data;
            for (let s of reply.replies) {
              if (!s.hasOwnProperty('showReplies'))
                s.showReplies = false;

              // 프로필 사진 불러오기
              if (s.imgPath && !s.imgPath.startsWith('http')) {
                this.axios.get(`/api/myinfo/img/${s.userId}`)
                .then((res) => {
                  console.log('이미지를 성공적으로 불러왔습니다.');
                  s.imgPath = res.data;
                })
                .catch((error) => {
                  console.log('이미지를 불러오는데 실패했습니다:', error);
                });
              }
            }
           
          }
        }).catch(error => {
          console.error('답글을 가져오는 중 오류가 발생했습니다.', error);
        });
      } else {
        console.error('올바르지 않은 comment 객체입니다.');
      }
    },
  
    deleteReplyComment(replyId, boardId) {
      console.log(replyId)
      this.reply.replies = this.reply.replies.filter(re1 => re1.id !== replyId.id);
      this.reply.showReplies = false;
      this.reply.child = this.reply.child - 1;
   
      
      this.axios.delete(`/api/comment/${replyId.id}/replies/${boardId}`)
      .then(() => {
        console.log('댓글이 성공적으로 삭제되었습니다.');
        this.$emit('checkChangeCount');
      })
      .catch(error => {
        console.error('대댓글 삭제 중 오류가 발생했습니다.', error);
      });
      
    },
      
    fetchComments() {
    const selectedId = this.selectedCard ? this.selectedCard.id : this.selectedPost.id;
    this.axios.get(`/api/comment/${selectedId}`)
      .then((res) => {
        this.comments = res.data;
        this.comments.forEach(comment => {
          comment.liked = commentLiked === 'true';
          this.fetchReplies(comment);
          this.fetchProfileImage();
          if (comment.replies && comment.replies.length > 0) {
            comment.replies.forEach(reply => {
              reply.liked = replyLiked === 'true';
            });
          }
        });

        this.totalCommentCount = this.calculateTotalCommentCount(this.comments);
      })
      .catch(error => {
        console.error('댓글을 불러오는 중 오류가 발생했습니다.', error);
      });
    },

    fetchReplies(comment) {
      if (!comment.hasOwnProperty('showReplies')) {
        comment.showReplies = false;
      }
      const boardId = this.selectedCard ? this.selectedCard.id : this.selectedPost.id;
      this.axios.get(`api/comment/detailcomment`, {
        params: {
          id: boardId,
          parentCommentId: comment.id
        }
      })
      .then(res => {
        if (res.data && res.data.length > 0){
          comment.replies = res.data;
          for(let s of comment.replies){
            if (!s.hasOwnProperty('showReplies'))
              s.showReplies = false;   

            // 프로필 사진 불러오기
            if (s.imgPath && !s.imgPath.startsWith('http')) {
              this.axios.get(`/api/myinfo/img/${s.userId}`)
              .then((res) => {
                console.log('이미지를 성공적으로 불러왔습니다.');
                s.imgPath = res.data;
              })
              .catch((error) => {
                console.log('이미지를 불러오는데 실패했습니다:', error);
              });
            }
            this.fetchProfileImage();
            this.fetchReplyLikeStatus(s);        
          }
        }
      })
      .catch(error => {
        console.error('답글을 가져오는 중 오류가 발생했습니다.', error);
      });
   
    },
    calculateTotalCommentCount(comments) {
      let total = 0;
      comments.forEach(comment => {
        total += 1;
        if(comment.replies && comment.replies.length > 0) {
          total += comment.replies.length;
        }
      });
      return total;
    },

  },
};
</script>


<style scoped>

.reply2-content {
  display: flex;
  /* flex-wrap: wrap; */
  align-items: center;
  padding: 3px 0;
  width: 97%;
  margin-left: 15px;
}

.reply2-profile-image {
  margin-top: -35px;
  background-color: white;
  border: 1px solid;
  border-radius: 50%;
  width: 35px; 
  height: 35px;
  min-width: 35px;
  overflow: hidden; 
}
.comment-content2{
  width: 100%;
  /* margin-left: 30px; */
}
.comment-row-11 {
  display: flex;
}
.user2 {
  font-size: 1rem;
  margin-left: 10px; 
  font-weight: bold;
}
.time-commented {
  font-size: 0.8rem;
  margin-top: 1px;
  margin-right: 5px;
  margin-left: 10px; 
  color: #999;
}
.reply-comment-interactions {
  font-family: 'omyu_pretty';
  border-top: 2px solid #ddd;
  padding-top: 10px;
  display: flex;
  align-items: center;
  justify-content: space-between; /* 좌우로 요소들을 배치합니다. */
}
.btn-edit:hover, .btn-delete:hover {
  background-color: #007bff;/* 마우스 호버 시 배경색 변경 */
}
.btn-edit-comment,
.btn-delete-comment {
  font-family: 'omyu_pretty';
  font-size: 0.65rem;
  color: #333;
  background-color: transparent; /* 배경색 투명으로 설정 */
  border: none;
  border-radius: 5px;
  padding: 5px 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}
.btn-edit-comment:hover,
.btn-delete-comment:hover {
  background-color: #ccc;
}
.i {
  font-family: "Font Awesome 5 Free";
  font-size: 0.5rem;
  color: rgb(245, 5, 5);   /* 하트 아이콘의 색상 */
  margin-right: 3px; /* 아이콘과 숫자 사이의 간격 조정 */
}            
.fa-heart {
  font-family: "Font Awesome 5 Free";
  font-size: 0.8rem;
  margin-right: 3px; /* 아이콘과 숫자 사이의 간격 조정 */
  color: rgb(245, 5, 5);
}
.fas.fa-heart{
  color: rgb(238, 238, 238);
}
.fas.fa-heart.filled {
  color: rgb(245, 5, 5); /* 채워진 하트의 색상 */
}
.like-commented2 {
  display: flex;
  justify-content: space-between;
}
.user2-comment {
  font-size: 1rem;
  flex-grow: 1; 
  margin-left: 10px;
  margin-right: 10px;
  text-align: left;
  letter-spacing: -1px;
  line-height: 24px;
}
.editCommnet-input {
  font-family: 'Ownglyph_meetme-Rg';
  font-size: 0.8rem;
}
.comment-like2 {
  font-family: 'omyu_pretty';
  font-size: 1rem;
  color: #999;
  margin-right: 3px; /* 아이콘과 숫자 사이의 간격 조정 */
  display: flex;
  align-items: center;
  width: 47px;
}
.re-comment  {
  cursor: pointer;
  display: flex;
  font-size: 0.9rem;
  color: #999;
  margin-left: 60px;
  margin-top: -5px;
}
input {
  font-family: 'Ownglyph_meetme-Rg' !important;
  font-size: 1rem;
  font-weight: 100;
  height: 2rem;
} 
input.addNewComment {
  margin-left: 58px;
  width: 410px;
}
input.addNewReplyComment {
  margin-left: 60px;
  width: 405px;
}

.toggle-replies {
  cursor: pointer;
  display: flex;
  margin-left: 64px;
  margin-top: -3px;
  font-size: 0.9rem;
  color: #5e5e5e;
}

</style>
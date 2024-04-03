<template>
  <div class="modal">
    <div class="preview">
      <carousel :items-to-show="1">
        <slide v-for="slide in slides" :key="slide.id">
          <img :src="slide.src" :alt="slide.alt" class="dog-image" />
        </slide>
        <template #addons>
          <navigation />
        </template>
      </carousel>
      <div class="content">
        <div class="header">
          <div>
            <button type="button" class="btn-close fixed-button" @click="closeModal(this.selectedCard.id)" aria-label="Close">
              <img src="../assets/images/x.png" alt="Close" />
            </button>
          </div>
          <div class="profile-info" style="align-items: center;">
            <img class="profile-image" :src="selectedCard.userImg" alt="Profile" />
            <h1 class="username">{{ this.selectedCard.writer }}</h1>
            <button class="btn-share" style="margin-right: 0.8%;" @click="showShareModal=true"><i class="fas fa-share-alt"></i></button>
            <FreeShareModal v-if="showShareModal" :selectedCard="selectedCard" @closeShareModal="showShareModal = false"/>
            <div v-if="isMine" class="interaction-info">
              <button type="button" class="btn-edit" @click="goToEdit">게시글 수정</button>
              <button type="button" class="btn-delete" @click="goToDelete">게시글 삭제</button>
            </div>
          </div>
          <div class="text-content" style="min-height: 90px;"> 
            <div class="intro">
              <div class="scrollable-content" style="max-height: 130px; overflow-y: auto;">
                <p>{{ selectedCard.content }}</p>
              </div>
            </div>
          </div>
          <div class="hashtags" style="display: flex; flex-wrap: wrap;">
            <a v-for="tag of tags" style="margin: 3px;" href="#" @click="emitTagSearch(tag)">{{ '#' + tag }}</a>
          </div>
          <div class="time-like">
            <div class="time-posted">{{ selectedCard.createdAt.slice(0, 10) }}</div>
            <div class="like" @click="toggleLike(selectedCard)">게시글 좋아요 {{ selectedCard.likeCount }} <i :class="['fas', 'fa-heart', { 'filled': boardLikeStatus }]"></i></div>
          </div>
        </div>
        <div class="cm-interactions" style="max-height: 250px; overflow-y: auto; min-height: 250px;">
          <div v-if="comments.length === 0" class="no-comment">아직 댓글이 없습니다.</div>
          <div class="comments" v-for="comment in comments" :key="comment.id">
            <div class="comment">
              <img class="comment-profile-image" :src="comment.imgPath" alt="Profile" />
              <div class="comment-content">
                <div class="comment-row-1">
                  <div class="user">{{ comment.name }}</div>
                  <div class="time-commented">{{ comment.createdAt.slice(0, 10) }}</div>
                  <div v-if="$cookies.get('id') == comment.userId" class="comment-interactions1">
                    <button class="btn-edit-comment" @click.prevent="editComment(comment)">
                      <i class="fas fa-edit"></i> 
                    </button>
                    <button class="btn-delete-comment" @click.prevent="deleteComment(comment.id)">
                      <i class="fas fa-trash-alt"></i> 
                    </button>
                  </div>
                </div>
                <div class="like-commented">
                  <div v-if="!comment.isEditing" class="user-comment">{{ comment.content }}</div>
                  <div v-if="comment.isEditing" class="user-comment">
                    <input type="text" class="editComment-input" v-model="comment.newContent" @keydown.enter.prevent="saveEditComment(comment)" />
                  </div>
                  <div class="comment-like" @click="toggleCommentLike(comment)">
                    <i :class="['fas', 'fa-heart', { 'filled': comment.liked }]"></i>
                    <div>{{ comment.likeCount }}</div>  
                  </div>
                </div>
              </div> 
            </div>
            <div class="re-comment" @click.prevent="toggleReplyInput(comment)">답글 달기</div>
            <div class="re-comment-input" v-if="replyInputStates[comment.id]">
              <input class="addNewComment" type="text" v-model="newReplyContent" @keydown.enter.prevent="saveNewReply(comment)" />
            </div>
            <div class="toggle-replies" @click.prevent="toggleReplies(comment)">
              {{ comment.showReplies ? '답글 숨기기' : (comment.replies && comment.replies.length > 0 ? '── 답글 ' + comment.replies.length + '개 더 보기' : '') }}
            </div>

            <div class="replies" v-if="comment.showReplies">
              <div v-for="reply in comment.replies" :key="reply.id" class="reply">
                <div class="reply-content">
                  <img class="reply-profile-image" :src="reply.imgPath" alt="Profile" />
                  <div class="comment-content">
                    <div class="comment-row-1">
                      <div class="user">{{ reply.name }}</div>
                      <div class="time-commented">{{ reply.createdAt.slice(0, 10) }}</div>
                      <div v-if="$cookies.get('id') == reply.userId" class="reply-comment-interactions1">
                        <button class="btn-edit-comment" @click.prevent="editReplyComment(reply)">
                          <i class="fas fa-edit"></i> 
                        </button>
                        <button class="btn-delete-comment" @click.prevent="deleteReReplyComment(reply)">
                          <i class="fas fa-trash-alt"></i> 
                        </button>
                      </div>
                    </div>
                    <div class="like-commented">
                      <div v-if="!reply.isEditing" class="user-comment">{{ reply.content }}</div>
                      <div v-if="reply.isEditing" class="user-comment">
                        <input type="text" class="editCommnet-input" v-model="reply.newContent" @keydown.enter.prevent="saveEditReplyComment(reply)" />
                      </div>
                      <div class="comment-like" @click="toggleReplyLike(reply)">
                        <i :class="['fas', 'fa-heart', { 'filled': reply.liked }]"></i>
                      <div>{{ reply.likeCount }}</div>  
                      </div>
                    </div>
                  </div> 
                </div>
                
                <div class="re-reply-comment" @click.prevent="toggleReplyInput(reply)">답글 달기</div>
                <div class="re-comment-input" v-if="replyInputStates[reply.id]">
                <input class="addNewReplyComment" type="text" v-model="newReplyContent" @keydown.enter.prevent="saveNewReply(reply)" />
                </div>
                <div class="toggle-replies2" @click.prevent="toggleReplies1(reply)">
                  {{ reply.showReplies ? '답글 숨기기' : (reply.child > 0 ? '── 답글 ' + reply.child + '개 더 보기' : '') }}
                </div>
                <div class="replies2" v-if="reply.showReplies">
                  <!-- <ReplyComponent v-for="reply2 in reply.replies" :key="reply2.id" :reply="reply2" :liked="reply2.liked" :currentUserId="this.$cookies.get('id')" :selectedCard="selectedCard"  class="reply2" @deleteAction="deleteReplyComment" @totalMinus="countCheck"/>  -->
                   <ReplyComponent v-for="reply2 in reply.replies" :key="reply2.id" :reply="reply2" :liked="reply2.liked" :currentUserId="this.$cookies.get('id')" :selectedCard="selectedCard"  class="reply2" @deleteAction="deleteReplyComment" @checkChangeCount="updateCommentCount"/> 
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="comment-interactions">
          <div class="comment-count">댓글 {{ this.commentCount }} 개 <i class="far fa-comment"></i></div>
          <div class="view-count">조회수 {{ viewCount }} 개</div>
        </div>
        <form class="addcomment" @submit.prevent="addComment">
          <img class="addcomment-profile-image" :src="this.usrImg" alt="Profile" />
          <input type="text" class="comment-input" placeholder="댓글을 입력하세요" v-model="commentLine">
          <button class="comment-button"><i class="far fa-paper-plane"></i></button>
        </form>
      </div>
    </div>
  </div>
</template>

  
<script>
import { Carousel, Slide, Pagination, Navigation } from 'vue3-carousel';
import ReplyComponent from '../components/ReplyComponent.vue';
import FreeShareModal from '../components/FreeShareModal.vue';


export default {
  name: 'preview', 
  components: {
    Carousel,
    Slide,
    Pagination,
    Navigation,
    ReplyComponent,
    FreeShareModal,
  },
  props: {
    reply2: Object,
    showModal: Boolean,
    selectedCard: Object,
    viewCount: Number,
    showShareModal: Boolean
  },
  data() {
    return {
      newContent: '',
      comments: [],
      isEditing: false,
      slides: [],
      tags: [],
      commentLine: "",
      updateButton: false,
      replyInputStates: {},
      usrImg : "",
      commentCount: 0,
      showShareModal: false,
    };
  },
  computed: {
    isMine() {
      return this.$cookies.get('id') == this.selectedCard.userId;
    },
    isLogin() {
      return this.$cookies.isKey('id');
    },
    boardLikeStatus(){
      return this.selectedCard.liked;
    }
  },
  methods: {
  updateCommentCount() {
    this.fetchCommentCount();
  },
  emitTagSearch(tag) {
    this.$emit('tagSearch', tag);
  },
  async increaseViewCount(postId) {
    try {
      const res = await this.axios.put(`/api/free/view/${postId}`);
      // console.log('응답:', res.data);
      await this.fetchPostDetails(postId);
      return res.data;
    } catch (error) {
      console.error('에러:', error);
      throw error;
    }
  },
  async fetchPostDetails(postId) {
    try {
      const response = await this.axios.get(`/api/free/view/${postId}`);
      // console.log('게시물 세부 정보:', response.data);
      return response.data; 
    } catch (error) {
      console.error('게시물 세부 정보를 불러오는 중 오류 발생:', error);
      throw error;
    }
  },

  //게시글 좋아요 토글
  toggleLike(selectedCard) {
    if (this.$cookies.isKey('id')) {
      let liked = !selectedCard.liked;

      this.axios.post(`/api/free/liked`, {
        userId: this.$cookies.get('id'),
        boardId: this.selectedCard.id,
        liked: liked,
      })
      .then((res)=> {
        if (res.status === 200) { // 성공적인 응답일 때만 처리
          selectedCard.likeCount += liked ? 1 : -1;
          selectedCard.liked = liked;
          this.updateLikeStatus(selectedCard.id, liked);
        } else {
          // 실패 시 사용자에게 알림
          console.error('게시글 좋아요 상태 업데이트 실패:', res.statusText);
        }
      })
      .catch(error => {
        console.error('게시글 좋아요 상태를 업데이트하는 중 오류가 발생했습니다.', error);
      });
    } else {
      alert('로그인 한 사용자만 좋아요 표시가 가능합니다!');
    }
  },

   //게시글 좋아요, 좋아요 수 업데이트
  updateLikeStatus(postId, liked) {
    this.axios.put(`/api/free/${postId}/like`, null, {
      params: { liked }
    })    
    .then(() => {
      console.log('게시글 좋아요 상태가 업데이트되었습니다.');
    })
    .catch(error => {
      console.error('게시글 좋아요 상태를 업데이트하는 중 오류가 발생했습니다.', error);
    });
  },
  //게시글 좋아요 상태유지
  fetchPostLikeStatus(){
    this.axios.get(`/api/free/${this.$cookies.get('id')}/${this.selectedCard.id}/likeStatus`)
    .then(res => {
      // console.log(res);
      const postLiked = res.data;
      // console.log(postLiked);
      this.selectedCard.liked = postLiked === true;
    })
    .catch(error => {
      console.error('게시글 좋아요 상태를 불러오는 중 오류가 발생했습니다.', error);
    });
  },
  //최상위 댓글 좋아요 토글
  toggleCommentLike(comment) {
    if(this.$cookies.isKey('id')){
      let liked = !comment.liked;
      comment.liked = liked;

      this.axios.post(`/api/comment/liked`, {
        userId: this.$cookies.get('id'),
        boardId: this.selectedCard.id,
        commentId: comment.id,
        liked: liked,
      })
      .then((res)=> {
        // console.log('res.data = ', res.data);
        if(res.data === true) {
          comment.likeCount++;
          comment.liked = true;
        } else {
          comment.likeCount--;
          comment.liked = false;
        }
        this.updateCommentLikeStatus(comment.id, liked);
      })
      .catch(error => {
        console.log('댓글 좋아요 상태를 업데이트하는 중 오류가 발생했습니다.', error);
      });
    } else alert('로그인 한 사용자만 댓글 좋아요가 가능합니다!');
  },
  //최상위 댓글 좋아요 수, 좋아요 저장
  updateCommentLikeStatus(commentId, liked) {
    this.axios.put(`/api/free/${commentId}/commentLike`, null, {
      params: { liked }
    })
    .then(() => {
      console.log('댓글 좋아요 상태가 업데이트 되었습니다.');
    })
    .catch(error => {
      console.error('댓글 좋아요 상태를 업데이트하는 중 오류가 발생했습니다.', error);
    });
  },
  //최상위 댓글 가져오기
  fetchComments() {
    this.axios.get(`/api/comment/${this.selectedCard.id}`)
    .then((res) => {
      // console.log('댓글 가져오기 성공:', res.data);
      this.comments = res.data;
      this.comments.forEach(comment => {
        const commentId = comment.id;
        this.axios.get(`/api/comment/${commentId}/likeStatus`)
          .then((res)=> {
            // console.log('댓글 좋아요 상태 가져오기 성공:', res.data); 
            const commentLiked = res.data;
            comment.liked = commentLiked === true;
            this.fetchReplies(comment);
            if (comment.replies && comment.replies.length > 0) {
              comment.replies.forEach(reply => {
                this.fetchReplyLikeStatus(reply);
              });
            }
          })
          .catch(error => {
            console.log('댓글 좋아요 상태를 불러오는 중 오류 발생:', error); // 
          });
          
          this.axios.get(`/api/myinfo/img/${comment.userId}`)
          .then((res) => {
            console.log('이미지를 성공적으로 불러왔습니다.');
            comment.imgPath = res.data;
          })
          .catch((error) => {
            console.log('이미지를 불러오는데 실패했습니다:', error);
          });
      });
    })
    .catch(error => {
      console.error('댓글을 불러오는 중 오류 발생:', error); // 
    });
  },
  //댓글의 답글 불러오기
  fetchReplies(comment) {
    if (!comment.hasOwnProperty('showReplies')) {
      comment.showReplies = false;
    }
    this.axios.get(`api/comment/detailcomment`, {
      params: {
        id: this.selectedCard.id,
        parentCommentId: comment.id
      }
    })
    .then(res => {
      if (res.data && res.data.length > 0) {
        comment.replies = res.data;
        // console.log(comment);
        for (let s of comment.replies) {
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

          //좋아요 상태 가져오기
          this.axios.get(`/api/comment/${s.id}/replyLikeStatus`)
          .then((res)=> {
            const replyLiked = res.data;
            s.liked = replyLiked === true;
          })
          .catch(error => {
            console.log('대댓글 좋아요 상태를 불러오는 중 오류 발생:', error);
          });
        }
      }
    })
    .catch(error => {
      console.error('답글을 가져오는 중 오류 발생:', error); 
    });
  },

  //대댓글 좋아요 토글
  toggleReplyLike(reply) {
    if(this.$cookies.isKey('id')){
      let liked = !reply.liked;
      reply.liked = liked;

      this.axios.post(`/api/comment/replyLiked`, {
        userId: this.$cookies.get('id'),
        boardId: this.selectedCard.id,
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
    } else alert('로그인 한 사용자만 댓글 좋아요가 가능합니다!');
  },
  //대댓글 좋아요 수, 좋아요 저장
  updateReplyLikeStatus(replyId, liked) {
    this.axios.put(`/api/comment/${replyId}/replyLike`, null, {
      params: { liked }
    })    
    .then(() => {
      console.log('댓글 좋아요 상태가 업데이트되었습니다.');

      const replies = this.comments.flatMap(comment => comment.replies || []);
      const targetReply = replies.find(reply => reply.id === replyId);
      if (targetReply) {
        targetReply.liked = liked;
      }
      // console.log(replies);
      // console.log(targetReply.liked);
    })
    .catch(error => {
      console.error('댓글 좋아요 상태를 업데이트하는 중 오류가 발생했습니다.', error);
    });
  },

  async fetchReplyLikeStatus(reply) {
    try {
      const res = await this.axios.get(`/api/comment/${reply.id}/replyLikeStatus`);
      const replyLiked = res.data;
      // console.log("응답 확인:", res);
      reply.liked = replyLiked === true;

      if (reply.replies && reply.replies.length > 0) {
        for (const subReply of reply.replies) {
          await this.fetchReplyLikeStatus(subReply);
        }
      }
    } catch (error) {
      console.error('대댓글 좋아요 상태를 불러오는 중 오류가 발생했습니다.', error);
    }
  },

  fetchCommentsAndRepliesLikeStatus(comments) {
    for (const comment of comments) {
      // 댓글의 좋아요 상태 가져오기
      this.fetchReplyLikeStatus(comment);
      if (comment.replies && comment.replies.length > 0) {
        // 대댓글이 있는 경우 대댓글에 대해 재귀적으로 함수 호출
        this.fetchCommentsAndRepliesLikeStatus(comment.replies);
      }
    }
  },
  async fetchReplyAndNestedReplies(comment) {
    const fetchReplies = async (comment) => {
      const replyId = comment.id;
      try {
        const res = await this.axios.get(`/api/comment/${replyId}/replyLikeStatus`);
        const replyLiked = res.data;
        comment.liked = replyLiked === true;

        if (comment.replies && comment.replies.length > 0) {
          for (const subReply of comment.replies) {
            await fetchReplies(subReply);
          }
        }
      } catch (error) {
        console.error('대댓글 좋아요 상태를 불러오는 중 오류가 발생했습니다.', error);
      }
    };
    await fetchReplies(comment);
  },
  async fetchReplyAndNestedRepliesLikeStatus(comment) {
    try {
      await this.fetchReplyLikeStatus(comment); // 현재 댓글의 좋아요 상태 가져오기
      if (comment.replies && comment.replies.length > 0) {
        // 현재 댓글의 대댓글들을 순회하면서 각 대댓글의 좋아요 상태를 가져오기
        for (const reply of comment.replies) {
          await this.fetchReplyAndNestedRepliesLikeStatus(reply);
        }
      }
    } catch (error) {
      console.error('댓글과 대댓글의 좋아요 상태를 불러오는 중 오류가 발생했습니다.', error);
    }
  },
  closeModal() {
    this.$emit('closeModal', this.selectedCard.id);
  },
  addComment() {
    if(this.$cookies.isKey('id')) {
    this.axios.post('/api/comment', {
      content: this.commentLine,
      id: this.selectedCard.id,
      userId: this.$cookies.get('id'),   
    }).then(() => {
      this.commentLine = "";
      this.fetchComments();
      this.fetchCommentCount(this.selectedCard.id);
    }).catch();
  } else alert('로그인 한 사용자만 댓글 작성이 가능합니다!');
  },
  //대댓글 삭제
  deleteReReplyComment(replyId) {
    const boardId = this.selectedCard.id;
    this.axios.delete(`/api/comment/${replyId.id}/replies/${boardId}`)
    .then(() => {
      this.comments.forEach(comment => {
        if (comment.replies && comment.replies.length > 0) {
          comment.replies = comment.replies.filter(reply => reply.id !== replyId.id);
        }
        if(comment.id === replyId.parentCommentId)
          comment.showReplies = false;
      });
      this.fetchCommentCount(boardId);
      
    })
    .catch(error => {
      console.error('대댓글 삭제 중 오류가 발생했습니다.', error);
    });
  },
  goToEdit() {
    this.$cookies.set('boardId', this.selectedCard.id);
    this.$router.push(`/editfree`);
  },
  goToDelete() {
    let id = this.selectedCard.id;
    this.$emit('deleteBoard', id);
  },
  //댓글 수정 버튼 토글
  editComment(comment) {
    comment.isEditing = !comment.isEditing;
    comment.oldContent = comment.content;
    comment.newContent = comment.content;
  },
  //댓글 수정
  saveEditComment(comment) {
    this.axios.put(`/api/comment/${comment.id}`, {
      id: comment.id,
      content: comment.newContent
    }).then(() => {
      comment.content = comment.newContent;
      comment.isEditing = false;
    }).catch(error => {
      console.error('댓글 수정 중 오류가 발생했습니다.', error);
    });
  },
  //대댓글 작성
  saveNewReply(reply) {
    if (!this.newReplyContent) {
      console.error('댓글 내용이 유효하지 않습니다.');
      return;
    }

    this.axios.post(`/api/comment/${reply.id}/replies`, {
      boardId: this.selectedCard.id,
      userId: this.$cookies.get('id'),   
      content: this.newReplyContent
    }).then(res => {
      console.log('댓글이 성공적으로 등록되었습니다.', res.data);
    
      this.fetchReplies(reply);
      this.fetchCommentCount();

      if(reply.replies) {
        reply.replies = [];
      } 

      reply.child = reply.child + 1;
      this.newReplyContent = '';
      this.replyInputStates[reply.id] = false;        
    }).catch(error => {
      console.error('댓글 등록 중 오류가 발생했습니다.', error);
    });
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
  //답글 달기 토글 버튼
  toggleReplyInput(comment) {
    if(this.$cookies.isKey('id')) {
      if (this.replyInputStates[comment.id]) {
        this.replyInputStates[comment.id] = false;
      } else {
        for (const id in this.replyInputStates) {
          if (Object.hasOwnProperty.call(this.replyInputStates, id)) {
            this.replyInputStates[id] = false;
          }
        }
        this.replyInputStates[comment.id] = true;
      }
    } else alert('로그인 한 사용자만 답글 작성이 가능합니다!');
  },  
  //답글 더 보기 버튼 토글
  toggleReplies(comment) {
    if (typeof comment === 'object') {
      comment.showReplies = !comment.showReplies;
      if (comment.showReplies && !comment.replies) {
        this.fetchReplies(comment);
      }
    } else { 
      console.error('올바르지 않은 comment 객체입니다.');
    }
  },
  toggleReplies1(comment) {
    if (typeof comment === 'object') {
      comment.showReplies = !comment.showReplies;
      this.axios.get(`/api/comment/detailcomment`, {
        params: {
          id: this.selectedCard.id,
          parentCommentId: comment.id
        }
      }).then((res) => {
        if (res.data && res.data.length > 0) {
          comment.replies = res.data;
          for (let s of comment.replies) {
            if (!s.hasOwnProperty('showReplies'))
              s.showReplies = false;
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

  //댓글 삭제
  deleteComment(commentId) {
    const boardId = this.selectedCard.id;
    this.axios.delete(`/api/comment/${commentId}/replies/${boardId}`)
    .then(() => {
      this.fetchCommentCount();
      this.comments = this.comments.filter(comment => comment.id !== commentId);
    })
    .catch(error => {
      console.error('댓글 삭제 중 오류가 발생했습니다.', error);
    });
  },
  async deleteReplyComment(replyId) {
    try {
      const boardId = this.selectedCard.id;
      await this.axios.delete(`/api/comment/${replyId.id}/replies/${boardId}`, {
        params: {
          replyId: replyId.id,
          boardId: this.selectedCard.id
        }
      });
      for (const comment of this.comments) {
        if (comment.replies && Array.isArray(comment.replies)) {
          await this.fetchReplies(comment);
          for (const reply of comment.replies) {
            if (reply.replies && Array.isArray(reply.replies)) {
              await this.fetchReplies(reply);
              reply.replies = reply.replies.filter(re1 => re1.id !== replyId.id);
              reply.child = reply.replies.length;
            }
          }
        }
      }
      // 댓글 삭제 후에 총 댓글 수를 다시 가져오고 갱신합니다.
      await this.fetchCommentCount();
    } catch (error) {
      console.error('대댓글 삭제 중 오류가 발생했습니다.', error);
    }
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
  fetchCommentCount() {
    this.axios.get(`/api/comment/totalCount`, {
      params: {
        boardId: this.selectedCard.id
      }
    })
    .then((res) => {
      this.commentCount = res.data;
    })
    .catch(error => {
      console.error('댓글 개수를 가져오는 중 오류가 발생했습니다.', error);
    });
  },
},

  async mounted() {
  if (this.selectedCard) {
    this.selectedCard.viewCount++;
    await this.fetchComments();
    this.fetchPostLikeStatus();
    this.fetchCommentsAndRepliesLikeStatus(this.comments);
    this.fetchCommentCount(this.selectedCard.id);
    this.increaseViewCount(this.selectedCard.id);
  }

  this.axios.get(`/api/free/getTag/${this.selectedCard.id}`).then((res) => {
    this.tags = res.data;
  }).catch();

  this.axios.get(`/api/free/getImage/${this.selectedCard.id}`).then((res) =>{
    let b = 1;
    for(let i of res.data){
      this.slides.push({ id: b++, src: i, alt: 'slide1' });
    }
  }).catch();
  
  
  //모달창 작성자 프로필 불러오기
  // if(!this.selectedCard.userImg!= null){
  //   console.log(this.selectedCard.userImg)
  //   if (!this.selectedCard.userImg.startsWith('http')) {
  //     this.axios.get(`/api/myinfo/img/${this.selectedCard.userId}`)
  //     .then((res) => this.selectedCard.userImg = res.data)
  //     .catch((error)=>{console.log('이미지를 불러오는데 실패하였습니다', error)});
  //   }
  // }

  //모달창 작성자 프로필 불러오기
  if (this.selectedCard.userImg !== null && !this.selectedCard.userImg.startsWith('http')) {
    this.axios.get(`/api/myinfo/img/${this.selectedCard.userId}`)
        .then((res) => this.selectedCard.userImg = res.data)
        .catch((error) => { console.log('이미지를 불러오는데 실패하였습니다', error); });
}


  //로그인한 유저 프로필 불러오기
  this.axios.get(`/api/myinfo/img/${this.$cookies.get('id')}`)
  .then((res) => {
    const imgPath = res.data;
    if(imgPath.startsWith('http')) {
      this.usrImg = imgPath;
      // console.log(this.usrImg)
    }
  })
  .catch((error) => { 
    console.log('이미지를 불러오는데 실패하였습니다', error);
  });
}}
</script>


<style scoped>
  @import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css');
  @import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css" integrity="sha512-fmJ4kaw6U5fSNAnusU4+eJ6qkhsQbS5ya1yW3zL/peXuRDGzH/ln5VTcBYIL3qy9z5H0bs2dnSC6LXw75RlcCw==');

  @font-face {
    font-family: 'Ownglyph_meetme-Rg';
    src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_2402_1@1.0/Ownglyph_meetme-Rg.woff2') format('woff2');
    font-weight: normal;
    font-style: normal;
  }

  @font-face {
    font-family: 'omyu_pretty';
    src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_2304-01@1.0/omyu_pretty.woff2') format('woff2');
    font-weight: normal;
    font-style: normal;
  }
  
  h1, h2, h3, h4, h5, h6 {
      font-family: 'Ownglyph_meetme-Rg';
    margin-top: 0;
    margin-bottom: 0.5rem; 
    line-height: 1.5;
    color: rgba(0, 0, 0, 0.8);
    font-weight: 400;}
  
  .preview {
      background-color: white;
    margin-top: 150px;
    display: flex;
    align-items: center; /* 수직 가운데 정렬 */
    justify-content: center; /* 수평 가운데 정렬 */
    border-radius: 20px;
    max-width: 1600px;
    max-height: 1200px;
    border: 2px solid #ddd; /* 테두리 스타일 및 색상 설정 */
  }
  p {
    margin: 0px;
  }
  .hashtags a:hover {
    box-shadow: 0 8px 12px rgba(0, 0, 0, 0.2);
    transform: translateY(-5px);
  }
  
  .profile-info {
    display: flex;
    align-items: center;
  }
  
  .username {
    max-width: 299px;
    margin: auto;
    font-size: 1.7rem;

    margin-left: 5px;
  }
  h1 {
    margin-bottom: 0px;
  }
  
  .intro {
    font-size: 1.1rem;
    margin-top: 20px;
    margin-bottom: 10px; 
    text-align: left;
  }
    
  /* .image-card {
    margin-top: 200px;
    max-width: 100%;
    height: 80vh;
  } */

  .content {
    display: flex;
    flex-direction: column;
    /* margin-top: 200px; */
    /* max-width: 1200px; */
    width: 70%;
    /* height: 80vh; */
    padding: 0 20px;
  }
    .dog-image {
      max-width: 500vw;
      max-height: 100%;
      display: block;
    }

    .profile-image {
      /* margin-top: -10px; */
      border-radius: 50%;
      border: 4px solid;
      margin-right: 10px;
      width: 50px;
      height: 50px;
    }

    .comment-profile-image {
      margin-top: -18px;
      width: 35px;
      height: 35px;
      min-width: 35px;
      border: 1px solid;
      border-radius: 50%;
    }


    .comment-content{
      width: 100%;
    }

    .text-content p {
      font-family: 'omyu_pretty';
      font-weight: 500;
      font-size: 1.3rem;
      /* margin: 5px 0; */
    }
    
    .hashtags {
      /* margin-top: 20px; */
      font-family: 'omyu_pretty';
      text-align: left;
      color: #0b0c5ce5;
      font-size: 1.2rem;
    }

    .hashtags a:hover {
    box-shadow: 0 8px 12px rgba(0, 0, 0, 0.2);
    transform: translateY(-5px);
  }
    
    .time-posted {
      margin-top: 3px;
      font-family: 'omyu_pretty';
      text-align: left;
      color: #999;
      font-size: 1.1rem;
    
    }
    .far {
      font-family: "Font Awesome 5 Free";
    }
    
    .interactions {
      font-family: 'omyu_pretty';
      border-top: 2px solid #ddd;
      padding-top: 10px;
      margin-top: 15px;
    }
  
    .cm-interactions {
      font-family: 'omyu_pretty';
      border-top: 2px solid #ddd;
      padding-top: 10px;
      margin-top: 15px;
      margin-bottom: 15px;
    }
    .no-comment {
    font-family: 'omyu_pretty';
    text-align: center;
    font-size: 1.2rem;
    color: #999;
    margin-top: 20%;
    margin-bottom: 20%;
  }
  
  .comment-row-1 {
    display: flex;
  }
  
  .comment-interactions {
    font-family: 'omyu_pretty';
    border-top: 2px solid #ddd;
    padding-top: 10px;
    display: flex;
    align-items: center;
    justify-content: space-between; /* 좌우로 요소들을 배치합니다. */
  }
  
  .comment-count,
  .view-count {
    font-family: 'omyu_pretty';
    display: inline-block;
  }
  
    .interaction-info {
    display: flex;
    justify-content: space-between;
  }
  
    .like {
      margin-top: -33px;
      font-family: 'omyu_pretty';
      color: #2222228c;
      font-size: 1.1rem;
      font-weight: bold;
      cursor: pointer;
      text-align: right;
    }
  
    .comment-like {
      font-family: 'omyu_pretty';
      font-size: 1rem;
      color: #999;
      margin-right: 3px; /* 아이콘과 숫자 사이의 간격 조정 */
      display: flex;
      align-items: center;
      width: 47px;
      cursor: pointer;
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
  
  .like-interactions {
    display: flex;
  }
    .comment {
    display: flex;
    flex-direction: row; /* 댓글 요소들을 가로로 배치 */
    align-items: center; /* 요소들을 세로 가운데 정렬 */
    padding: 10px 0; /* 위아래로 간격 추가 */
  }

  .reply {
  flex-wrap: wrap;
  display: flex;
  flex-direction: row; /* 댓글 요소들을 가로로 배치 */
  align-items: center; /* 요소들을 세로 가운데 정렬 */
  margin-left: 15px;
  padding: 3px 0;
  width: 97%;
}

.reply2 {
  flex-wrap: wrap;
  display: flex;
  /* flex-direction: row; */
  align-items: center;
  margin-left: 15px;
  padding: 3px 0;
  width: 97%;
}

.reply-content {
  display: flex;
  align-items: center;
  margin-left: 15px;
  padding: 3px 0;
  width: 97%;
}

  
  .reply-user,
  .user {
    font-size: 1rem;
    margin-left: 10px; 
    font-weight: bold;
  }
  
  .reply-user-commnet,
  .user-comment {
    font-size: 1rem;
    flex-grow: 1; /* 댓글 내용이 가능한 최대 너비를 가지도록 설정 */
    margin-left: 10px;
    margin-right: 10px;
    text-align: left;
    letter-spacing: -1px;
    line-height: 24px;
  }
  
  .reply-time-commentd,
  .time-commented {
    font-size: 0.8rem;
    margin-top: 1px;
    margin-right: 5px;
    margin-left: 10px; 
    color: #999;
  }
  .reply-like-commented,
  .like-commented {
    display: flex;
    justify-content: space-between;
  }
  
  
   .addcomment {
    font-family: 'omyu_pretty';
    font-size: 1rem;
    border-radius: 20px;
    margin-top: 15px;
    display: flex;
    align-items: center;
  }
  

  .addcomment-profile-image {
    background-color: white;
    border: 1px solid;
    border-radius: 50%;
    width: 45px;
    height: 45px; 
    min-width: 45px;
    overflow: hidden;
  }

  .reply-profile-image {
    margin-top: -22px;
    background-color: white;
    border: 1px solid;
    border-radius: 50%;
    width: 35px; /* 이미지 너비 조정 */
    height: 35px; /* 이미지 높이 조정 */
    min-width: 35px;
  }
  
  .add-comment {
    margin-left: 30px; /* 이미지와 텍스트 사이 간격 조정 */
  }
  
  .comment-input {
    padding: 10px; /* 내부 여백 설정 */
    font-size: 1rem; /* 폰트 크기 설정 */
    font-family: 'Ownglyph_meetme-Rg';
    border: 1px solid #ced4da; /* 입력 상자 테두리 설정 */
    border-radius: 50px; /* 입력 상자 테두리 모서리를 둥글게 설정 */
    margin-left: 10px;
  }
  .comment-input:focus {
    outline: none; /* 포커스 상태일 때 외곽선 제거 */
    border-color: #007bff; /* 포커스 상태일 때 테두리 색상 변경 */
  }
 
  .editComment-input {
    font-family: 'Ownglyph_meetme-Rg';
    font-size: 0.8rem;
  }
  .comment-button {
    font-family: "Font Awesome 5 Free";
    font-size: 1.2rem;
    margin-left: 5px;
    padding: 10px;
    border: 1px solid #ced4da;
    border-radius: 50px;
    cursor: pointer;
  }
  
  .comment-button:focus {
    outline: none; /* 포커스 상태일 때 외곽선 제거 */
    border-color: #007bff; /* 포커스 상태일 때 테두리 색상 변경 */
  }
/* 
  .addnewcomment {
    padding: 10px; 
    font-size: 0.8rem; 
    font-family: 'Ownglyph_meetme-Rg';
    border: 1px solid #ced4da; 
    border-radius: 50px; 
    margin-left: 10px;
  } */
  
 input {
    font-family: 'Ownglyph_meetme-Rg' !important;
    font-size: 1rem;
    font-weight: 100;
    height: 2rem;
  } 
  input.comment-input {
    height: 2.75rem;
  }
  input.addNewComment {
    margin-left: 45px;
    width: 435px;
  }
  
  input.addNewReplyComment {
    margin-left: 60px;
    width: 405px;
  }
  
  .modal {
    position: relative;
    /* overflow: hidden; */
  }
  
  .modal .preview {
    background-color: white;
    margin-top: 150px;
    width: 1000px; /* 수정된 부분 */
    height: 750px; /* 수정된 부분 */
    border-radius: 20px;
    border: 2px solid #ddd;
    padding: 20px;
    /* overflow-y: auto; */
  }
  
  .modal .preview::-webkit-scrollbar { /* Webkit 브라우저용 스크롤바 */
    width: 8px;
  }
  
  .modal .preview::-webkit-scrollbar-thumb {
    background-color: #888; /* 스크롤바 색상 */
    border-radius: 4px; /* 스크롤바 모서리 둥글게 */
  }
  
  .modal .preview::-webkit-scrollbar-track {
    background-color: transparent; /* 스크롤바 트랙 배경색 */
  }
  
  .modal .preview {
    scrollbar-width: thin; /* Firefox용 스크롤바 */
  }
  
  .modal .preview::-webkit-scrollbar-thumb:hover {
    background-color: #555; /* 스크롤바 호버 시 색상 */
  }
  
  .btn-close {
    background-color: transparent;
    border: none;
    border-radius: 100%;
    padding: 0;
    cursor: pointer;
    width: 30px;
    height: 30px;
    transition: background-color 0.3s ease;
    position: absolute;
  }

  .fixed-button {
  position: relative;
  left: 250px;
  }
  
  .btn-close:hover {
    background-color: rgba(255, 249, 249, 0.1);
  }
  
  a{
    text-decoration: none;
  }
  .carousel {
    width: 50%;
  }
  /* 캐러셀 내부 요소 간격 조절 */
  .carousel .slide {
    width:60%; /* carousel의 최대 너비를 화면 너비에 맞게 조절합니다. */
    margin: 0 auto; /* 가운데 정렬을 위해 좌우 마진을 자동으로 설정합니다. */
    padding: 0;
  }
  .carousel img {
    max-width: 100%; /* 이미지의 최대 너비를 부모 요소에 맞게 설정합니다. */
    max-height: 100%; /* 이미지의 최대 높이를 부모 요소에 맞게 설정합니다. */
    width: auto; /* 이미지의 너비를 자동으로 조정합니다. */
    height: 600px; /* 이미지의 높이를 자동으로 조정합니다. */
    margin: 10px; /* 이미지 주변의 여백을 제거합니다. */
    padding: 0px; /* 이미지 주변의 여백을 제거합니다. */
  }
  
  /* 화살표와 이미지 간격 조절 */
  .carousel .navigation {
    margin: 0 -800px; /* 화살표와 이미지 사이의 간격 조절 */
  }
  .dog-image {
    width: 80%;
  }
  .btn-share,
  .btn-edit,
  .btn-delete {
    /* margin-top: 10px; */
    font-family: 'omyu_pretty';
    background-color: #999;
    color: #fff;
    border: none;
    border-radius: 5px;
    padding: 10px 10px;
    cursor: pointer;
    transition: background-color 0.3s ease;
  }
  
  .btn-edit {
    margin-right: 5px;
  }
  
  .interaction-info {
    display: flex;
    justify-content: flex-end; /* 버튼을 우측으로 정렬 */
  }
  
  
  .btn-share:hover, .btn-edit:hover, .btn-delete:hover {
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

.re-comment,
.re-reply-comment {
  cursor: pointer;
  display: flex;
  font-size: 0.9rem;
  color: #999;
}

.re-comment{
  margin-left: 46px;
  margin-top: -15px;
}

.re-reply-comment{
  margin-top: -5px;
  margin-left: 62px;
  width: 100%;
 
}

.toggle-replies,
.toggle-replies2 {
  cursor: pointer;
  display: flex;
  margin-top: -3px;
  font-size: 0.9rem;
  color: #5e5e5e;
}

.toggle-replies {
  margin-left: 47px;
}
.toggle-replies2 {
  margin-left: 62px;
  margin-right: auto;
  width: 100%;
}
.replies2 {
  width: 100%;
}

/* @media screen and (min-width: 768px) and (max-width: 1024px) {
  .btn-close {
    margin-right: 10px;
  }
}

@media (max-width: 768px) { 

} */
  
  
</style>
<template>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous"> 
    <div class="modal" :class="{ 'show': showQnaModal }">
      <div class="modal-dialog">
        <div class="modal-content">
          <div>
            <button type="button" class="btn-close fixed-button" @click="closeModal(this.selectedPost.id)" aria-label="Close">
                <img src="../assets/images/x.png" alt="Close" />
            </button>
          </div>
          <div class="modal-header">
            <div class="allTags">
              <div class="tag" :class="getTagClass(selectedPost.category)">{{ selectedPost.category}}</div>
              <div class="hashtags">
                <a v-for="tag of tags" href="#" @click="emitTagSearch(tag)" >{{ '#' + tag }}</a>
              </div>
              <button class="btn-share" style="margin-right: 0.8%;" @click="showShareModal=true"><i class="fas fa-share-alt"></i></button>
              <QnaShareModal v-if="showShareModal" :selectedPost="selectedPost" @closeShareModal="showShareModal = false"/>
              <div v-if="isMine" class="interaction-info">
                <button type="button" class="btn-edit" @click="goToEditPost">게시글 수정</button>
                <button type="button" class="btn-delete" @click="goToDeletePost">게시글 삭제</button>
              </div>        
            </div>
            <h6 class="modal-title"><p class="modal-title-icon">Q.</p> {{selectedPost.title}}</h6>
            <div class="modal-title-writer-date">
              <div class="writer">작성자: {{selectedPost.writer}}</div>
              <div class="createdAt">작성일자: {{ selectedPost.createdAt.slice(0, 10)  }}</div>
            </div>
            <div class="like-view">
              <div class="like" @click="toggleLike(selectedPost)">좋아요 {{ this.selectedPost.likeCount }} <i :class="['fas', 'fa-heart', { 'filled': boardLikeStatus }]"></i></div>
              <div class="view-count">조회수 {{ selectedPost.viewCount }}</div>
            </div>
          </div>  

          <div class="preview">
            <carousel :items-to-show="1">
              <slide v-for="slide in slides" :key="slide.id">
                <img :src="slide.src" :alt="slide.alt" class="dog-image" />
              </slide>
              <template #addons>
                <navigation />
              </template>
            </carousel>
            <div class="text-content"> 
              <div class="intro">
                <div class="scrollable-content">
                  <p>{{ this.selectedPost.content }}</p>
                </div>
              </div>
            </div>
          </div>
        <!-- </div> -->

        <div class="cm-interactions" style="max-height: 175px; overflow-y: auto; min-height: 175px;">
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
                    <input type="text" class="editCommnet-input" v-model="comment.newContent" @keydown.enter.prevent="saveEditComment(comment)" />
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
        <!--대댓글 코드-->
        <div class="replies" v-if="comment.showReplies">
          <div v-for="reply in comment.replies" :key="reply.id">
            <div class="reply">
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
              <ReplyComponent v-for="reply2 in reply.replies" :key="reply2.id" :reply="reply2" :liked="reply2.liked" :currentUserId="$cookies.get('id')" :selectedPost="selectedPost" class="reply2" @deleteAction="deleteReplyComment" @checkChangeCount="updateCommentCount"/> 
            </div>
          </div>
        </div>
      </div>
    </div>
    <!--대댓글 코드 종료-->
    
    <div class="comment-interactions">
      <div class="comment-count">댓글 {{ this.commentCount }} 개 <i class="far fa-comment"></i></div>
    </div>
    <!-- <form class="addcomment" v-if="isLogin" @submit.prevent="addComment"> -->
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
  import QnaShareModal from '../components/QnaShareModal.vue';
  
  
  export default {
    components: {
      Carousel,
      Slide,
      Pagination,
      Navigation,
      ReplyComponent,
      QnaShareModal, 
    },
    props: {
      reply2: Object,
      showQnaModal: Boolean,
      selectedPost: Object,
      // slide: Object,
      showShareModal: Boolean,
      showQnaModal: Boolean,
      selectedPost: Object,
      // images: Array,
    },
    data() {
      return {
        newContent: '',
        comments: [],
        isEditing: false,
        slides: [],
        tags: [],
        commentLine: "",
        replyInputStates: {},
        showQnaImageModal: false,
        selectedImage: {},
        commentCount: 0,
        usrImg : "",
        showShareModal: false,
      }
    },
    computed: {
      isMine() {
        return this.$cookies.get('id') == this.selectedPost.userId ? true : false;
      },
      isLogin() {
        return this.$cookies.isKey('id') ? true : false;
      },
      boardLikeStatus() {
        return this.selectedPost.liked;
      },
      hasNextImage() {
        return this.imageIndex < this.slides.length - 1;
      }
    },
  
    methods: {
      updateCommentCount() {
        this.fetchCommentCount();
      },
      scrollLeft() {
        this.imageIndex = Math.max(0, this.imageIndex - 1);
      },
      scrollRight() {
        this.imageIndex = Math.min(this.images.length - 1, this.imageIndex + 1);
      },
      emitTagSearch(tag) {
        this.$emit('tagSearch', tag);
      },
      // openImageModal(slide) {
      //   this.selectedImage = slide;
      //   this.showQnaImageModal = true;
      // },
      // closeImageModal() {
      //   this.showQnaImageModal = false;
      // },
      //게시글 수정 버튼 클릭 시
      goToEditPost() {
        this.$cookies.set('boardId', this.selectedPost.id);
        this.$router.push(`/editqna`);
      },
      //게시글 삭제 버튼 클릭 시
      goToDeletePost(){
          const id = this.selectedPost.id;
          this.$emit('deleteBoard', id);
      },
   
      toggleLike(selectedPost) {
        if (this.$cookies.isKey('id')) {
          let liked = !selectedPost.liked;

          this.axios.post(`/api/free/liked`, {
            userId: this.$cookies.get('id'),
            boardId: this.selectedPost.id,
            liked: liked,
          })
          .then((res)=> {
            if (res.status === 200) { // 성공적인 응답일 때만 처리
              selectedPost.likeCount += liked ? 1 : -1;
              selectedPost.liked = liked;
              this.updateLikeStatus(selectedPost.id, liked);
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
      fetchPostLikeStatus() {
        this.axios.get(`/api/free/${this.$cookies.get('id')}/${this.selectedPost.id}/likeStatus`)
        .then(res => {
          const postLiked = res.data;
          this.selectedPost.liked = postLiked === true;
        })
        .catch(error => {
          console.error('게시글 좋아요 상태를 불러오는 중 오류가 발생했습니다.', error);
        });
      },
      closeModal(){
        this.$emit('closeModal', this.selectedPost.id);
      },
      //댓글 추가
      addComment() {
        if(this.$cookies.isKey('id')) {
        this.axios.post('/api/comment', {
            content: this.commentLine,
            id: this.selectedPost.id,
            userId: this.$cookies.get('id'),
          }).then(() => {
            this.commentLine = "";
            this.fetchComments();
            this.fetchCommentCount();
          }).catch();
        } else alert('로그인 한 사용자만 댓글 작성이 가능합니다!');
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
        })
          .catch(error => {
          console.error('댓글 수정 중 오류가 발생했습니다.', error);
        });
      },
      //댓글 삭제
      deleteComment(commentId) {
        const boardId = this.selectedPost.id;
        this.axios.delete(`/api/comment/${commentId}/replies/${boardId}`)
        .then(() => {
          this.fetchCommentCount();
          this.comments = this.comments.filter(comment => comment.id !== commentId);
        })
        .catch(error => {
          console.error('댓글 삭제 중 오류가 발생했습니다.', error);
        });
      },
      //답글 달기 토글 버튼
      toggleReplyInput(comment) {
        if (this.$cookies.isKey('id')) {
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
  
      //대댓글 작성
      saveNewReply(reply) {
        if (!this.newReplyContent) {
          console.error('댓글 내용이 유효하지 않습니다.');
          return;
       }
  
        this.axios.post(`/api/comment/${reply.id}/replies`, {
          boardId: this.selectedPost.id,
          userId: this.$cookies.get('id'),
          content: this.newReplyContent
        })
        .then((res) => {
          console.log('댓글이 성공적으로 등록되었습니다.', res.data);
  
          this.fetchReplies(reply);
          this.fetchCommentCount();
          
          if(reply.replies) {
            reply.replies = [];
          }
  
          reply.child = reply.child + 1;
          this.newReplyContent = '';
          this.replyInputStates[reply.id] = false;  
  
        })
        .catch(error => {
          console.error('댓글 등록 중 오류가 발생했습니다.', error);
        });
      },
      //답글 더 보기 버튼 토글
      toggleReplies(comment){
        if (typeof comment === 'object') {
          comment.showReplies = !comment.showReplies;
          if (comment.showReplies && !comment.replies) {
            this.fetchReplies(comment);
          }
        } else {
          console.error('올바르지 않은 comment 객체입니다.');
        }
      },
      //최상위 댓글 좋아요 토글
      toggleCommentLike(comment) {
        if (this.$cookies.isKey('id')) {
          let liked = !comment.liked;
          comment.liked = liked;
          
          this.axios.post(`/api/comment/liked`, {
            userId: this.$cookies.get('id'),
            boardId: this.selectedPost.id,
            commentId: comment.id,
            liked: liked,
          }) 
          .then((res) => {
            if (res.data === true) {
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
        this.axios.get(`/api/comment/${this.selectedPost.id}`)
          .then((res) => {
            this.comments = res.data;
            this.comments.forEach(comment => {
              const commentId = comment.id;
              this.axios.get(`/api/comment/${commentId}/likeStatus`)
                .then((res)=> {
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
                  console.log('댓글 좋아요 상태를 불러오는 중 오류 발생:', error); 
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
            console.error('댓글을 불러오는 중 오류 발생:', error); 
          });
      },
  
      //댓글의 답글 불러오기
      fetchReplies(comment) {
      if (!comment.hasOwnProperty('showReplies')) {
        comment.showReplies = false;
      }
      this.axios.get(`api/comment/detailcomment`, {
        params: {
          id: this.selectedPost.id,
          parentCommentId: comment.id
        }
      })
      .then(res => {
        if (res.data && res.data.length > 0) {
          comment.replies = res.data;
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
  
    //답글 좋아요 상태 유지
    async fetchReplyLikeStatus(reply) {
      try {
        const res = await this.axios.get(`/api/comment/${reply.id}/replyLikeStatus`);
        const replyLiked = res.data;
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
    //대댓글이 있는 경우 대댓글에 대해 재귀적으로 호출
    fetchCommentsAndRepliesLikeStatus(comments) {
      for (const comment of comments) {
        this.fetchReplyLikeStatus(comments);
        if (comment.replies && comment.replies.length > 0) {
          this.fetchCommentsAndRepliesLikeStatus(comment.replies);
        }
      }
    },
    //대댓글 수정 버튼 토글
    editReplyComment(reply) {
      reply.isEditing = !reply.isEditing;
      reply.oldContent = reply.content;
      reply.newContent = reply.content;
    },
    //대댓글 삭제
    deleteReReplyComment(replyId) {
      const boardId = this.selectedPost.id;
      this.axios.delete(`/api/comment/${replyId.id}/replies/${boardId}`)
      .then(() => {
        this.comments.forEach(comment => {
          if (comment.replies && comment.replies.length > 0) {
            comment.replies = comment.replies.filter(reply => reply.id !== replyId.id);
          }
          if (comment.id === replyId.parentCommentId){
            comment.showReplies = false;
          }
        });
        this.fetchCommentCount();
      }).catch(error => {
        console.error('대댓글 삭제 중 오류가 발생했습니다.', error);
      });
    },
    //대댓글 추가
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
    //대댓글 좋아요 토글
    toggleReplyLike(reply) {
      let liked = !reply.liked;
      reply.liked = liked;
  
      this.axios.post(`/api/comment/replyLiked`, {
        userId: this.$cookies.get('id'),
        boardId: this.selectedPost.id,
        commentId: reply.id,
        liked: liked,   
      })
      .then((res)=>{
        if(res.data === true) {
          reply.likeCount++;
          reply.liked = true;
        } else {
          reply.likeCount--;
          reply.liked = false;
        }
        this.updateReplyLikeStatus(reply.id, liked);
      })
      .catch(error => {
        console.log('대댓글 좋아요 상태를 업데이트하는 중 오류가 발생했습니다.', error);
      });     
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
    //대댓글 답글 더 보기 버튼 토글
    toggleReplies1(comment) {
      if (typeof comment === 'object') {
        comment.showReplies = !comment.showReplies;
        this.axios.get(`/api/comment/detailcomment`, {
          params: {
            id: this.selectedPost.id,
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
                    s.imgPath = res.data;
                    console.log('이미지를 성공적으로 불러왔습니다.');
                  }).catch((error) => {
                    console.log('이미지를 불러오는데 실패했습니다:', error);
                  });
                }
            }
          }
        });
      } else {
        console.error('올바르지 않은 comment 객체입니다.');
      }
    },
    //재귀) 대댓글 실제 삭제
  
    async deleteReplyComment(replyId) {
      try {
        const boardId = this.selectedPost.id;
        await this.axios.delete(`/api/comment/${replyId.id}/replies/${boardId}`, {
          params: {
            replyId: replyId.id,
            boardId: this.selectedPost.id
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
        await this.fetchCommentCount();
      } catch (error) {
        console.error('대댓글 삭제 중 오류가 발생했습니다.', error);
      }
    },
    //재귀) 댓글 삭제 시 카운트 
    async countCheck(minusCount) {
      // console.log(minusCount);
      await this.fetchCommentCount();
    },
    //카테고리 태그
    getTagClass(tag) {
      console.log(tag, '태그확인');
      switch (tag) {
        case '고양이':
          return 'cat';
        case '강아지':
          return 'dog';
        case '소동물':
          return 'small-animal';
        default:
          return 'other';
      }
    },
    //댓글 총 갯수 불러오기
    fetchCommentCount() {
      this.axios.get(`/api/comment/totalCount`, {
        params: {
          boardId: this.selectedPost.id
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
      if(this.selectedPost) {
        this.selectedPost.viewCount++;
        await this.fetchComments();
        this.fetchPostLikeStatus();
        this.fetchCommentCount();
        this.fetchCommentsAndRepliesLikeStatus(this.comments);   
      }
  
      this.axios.get(`/api/free/getTag/${this.selectedPost.id}`).then((res) => {
        this.tags = [];
        this.tags = res.data;
      }).catch();
  
      this.axios.get(`/api/free/getImage/${this.selectedPost.id}`).then((res) => {
        let b = 1;
        for(let i of res.data) {
          this.slides.push({ id: b++, src: i, alt: 'slide1' });
        }
      }).catch();
  
      
      //로그인한 유저 프로필 불러오기
      this.axios.get(`/api/myinfo/img/${this.$cookies.get('id')}`)
      .then((res) => {
        const imgPath = res.data;
        if (imgPath.startsWith('http')) {
          this.usrImg = imgPath;
        }
      })
      .catch((error) => {
        console.log('이미지를 불러오는데 실패하였습니다', error);
      });
    }
  }
  </script>
  
  
  <style scoped>
    @font-face {
      font-family: 'omyu_pretty';
      src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_2304-01@1.0/omyu_pretty.woff2') format('woff2');
      font-weight: normal;
      font-style: normal;
    }
  
  
  * {
      font-family: 'omyu_pretty';
  }
  .preview {
    display: flex
  }
  .scrollable-content {
    max-height: 333px; overflow-y: auto;
  }

  /* 모달창 */
  .text-content {
    min-height: 333px;
    width: 50%;
    padding: 50px;
  }
  .modal {
      position: fixed;
      top: 0;
      left: 0;
      width: 100%; 
      height: 100%; 
      margin: 0;
      padding: 0;
      background-color: rgba(0, 0, 0, 0.5); /* 검정색 투명 배경을 나타내는 rgba 값 */
      display: flex;
      justify-content: center; /* 수평 가운데 정렬 */
      align-items: center; /* 수직 가운데 정렬 */
      top: 50%; /* 화면 세로 중앙 */
      left: 50%; /* 화면 가로 중앙 */
      transform: translate(-50%, -50%); /* 가운데 정렬 */
  }
  .modal.show {
    display: flex !important; 
  }
  .modal > .modal-dialog {
      position: fixed; 
      margin: auto; 
      pointer-events: auto; 
      width: 80%;
      max-width: 1000px; 
      height: 90%;
      max-height: 1000px;
  }
  .modal-content {
      position: relative;
      display: flex;
      flex-direction: column;
      pointer-events: auto;
      background-color: #fff;
      background-clip: padding-box;
      border: 1px solid rgba(0, 0, 0, 0.2);
      border-radius: 0.8rem;
      outline: 0;
      width: 100%;
      height: 100%; 
  }
  .interaction-info {
     display: flex;
     justify-content: flex-end;
  }
  
  .allTags {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 15px; 
      text-decoration: none;
      width: 100%;
  }
  .tag {
      display: inline-block;
      padding: 5px 15px; 
      border-radius: 15px; 
      color: #fff;
      background-color: #007bff;
   
  }
  .hashtags {
      margin-left: 10px;
      margin-right: auto;
  }
  
  a {
      margin-left: 3px;
      margin-right: 3px;
      text-decoration: none;
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
      padding: 5px 10px;
      cursor: pointer;
      transition: background-color 0.3s ease;
  }
    
  .btn-edit {
      margin-right: 5px;
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
  
  .btn-close {
    background-image: none;
    position: absolute;
    top: 15px;
    right: 50px;
    z-index: 10;
    background-color: transparent;
    border: none;
    border-radius: 100%;
    padding: 0;
    cursor: pointer;
    width: 30px;
    height: 30px;
    transition: background-color 0.3s ease;
     
    }
    
    .btn-close:hover {
      background-color: rgba(255, 249, 249, 0.1);
    }
    
    .cat {
      background-color: #f87495;
    }
    .dog {
      background-color: #61bffd;
    }
    .small-animal,
    .other {
      background-color: #12af41;
    }
    .modal-footer {
      position: absolute; 
      bottom: 0; 
      right: 0; 
      padding: 0.75rem;
      border-top: 1px solid #dee2e6;
      background-color: #f7f7f7;
    }
    .modal-top {
      display: flex;
      justify-content: space-between; 
      margin-top: 5px;
    }
  
    .modal-header {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding-top: 50px;
      padding-left: 50px;
      padding-right: 50px;
      padding-bottom: 20px;
      border-bottom: 1px solid #dee2e6;
      background-color: #ececec;
      position: absolute;
      left: 0;
      width: 100%;
  
      position: relative;
    }
    .modal-title {
      display: flex;
      align-items: center;
      margin: 0;
      font-size: 28px;
      font-weight: bold;
    }
    .modal-title-icon {
      color: #34aaf8;
      font-weight: bold;
      margin-right: 5px;
    }
    .modal-title-writer-date {
      display: flex;
      align-items: center;
      justify-content: space-between; 
      font-size: 16px;
      width: 100%;
    }
    .modal-title-writer-date .writer {
      text-align: auto;
      font-size: 18px;
    }
    .modal-title-writer-date .createdAt {
      text-align: auto;
      font-size: 18px;
    }
    .like-view {
      display: flex;
      justify-content: flex-end;
      width: 100%;
    }
    .like {
      margin-right: 10px;
    }

    /* 
    .carousel-item {
    transition: 0s;
    } */

  
    
  .carousel {
    width: 50%;
  }
  /* 캐러셀 내부 요소 간격 조절 */
  .carousel .slide {
    display: flex;
    width:60%; 
    margin: 0 auto; 
    padding: 0;
  }
  .carousel img {
    max-width: 97%; 
    max-height: 100%; 
    width: auto; 
    height: 333px;  
    padding: 30px; 
  }
  

  .carousel .navigation {
    margin: 0 -800px; 
  }
  .dog-image {
    width: 80%;
  }
 


    .modal-body1 {
      font-size: 16px;
      position: absolute;
      /* top: 56%; */
      top: 34%;
      left: 50%;
      transform: translate(-50%, -50%);
      overflow-y: auto; 
      height: 135px; 
      width: 90%;
    }
    /* 캐러셀 스타일링 */
    .modal-body2 {
      top: 20%;
      height: 160px; 
      position: relative;
    }
    .carousel-inner {
      display: flex;
      transition: all 3s;
      justify-content: center;
      align-items: center;
      height: 100%;
    }
    .carousel-item img, .carousel-item.active img {
      margin-left: auto;
      margin-right: auto;
      margin-top: 10px;
      margin-bottom: 10px;
      /* height: 125px; */
      height: 150px;
      width: 100%;
      object-fit: contain;
      transform: translateX(-50%); 
    }
    .carousel-control-prev,
    .carousel-control-next {
      display: flex;
      justify-content: center;
      align-items: center;
      width: 20px; /* 이전/다음 버튼의 너비 조정 */
      height: 20px; /* 이전/다음 버튼의 높이 조정 */
      background-color: rgba(0, 0, 0, 0.5); /* 배경색 지정 */
      border-radius: 50%; /* 원형 모양으로 버튼 모양 조정 */
      transition: background-color 0.3s ease; /* hover 효과를 위한 전환 */
      position: absolute; /* 절대 위치 지정 */
      top: 50%; /* 상단으로부터 50% 위치 */
      transform: translateY(-50%); /* 세로 가운데 정렬 */
    }
    .carousel-control-prev {
      left: 5%; /* 좌측에서부터 0 위치 */
    }
    .carousel-control-next {
      right: 5%; /* 우측에서부터 0 위치 */
    }
    .image-modal-open .carousel-control-prev,
    .image-modal-open .carousel-control-next {
      display: flex;
      justify-content: center;
      align-items: center;
      width: 3rem; /* 이전/다음 버튼의 너비 조정 */
      height: 3rem; /* 이전/다음 버튼의 높이 조정 */
      background-color: rgb(0, 0, 0); /* 배경색 지정 */
      border-radius: 50%; /* 원형 모양으로 버튼 모양 조정 */
      transition: background-color 0.3s ease; /* hover 효과를 위한 전환 */
      position: absolute; /* 절대 위치 지정 */
      top: 120%; /* 상단으로부터 50% 위치 */
      transform: translateY(-50%); /* 세로 가운데 정렬 */
    }
    .image-modal-open .carousel-control-prev {
      left: 5%; /* 좌측에서부터 0 위치 */
    }
    .image-modal-open .carousel-control-next {
      right: 5%; /* 우측에서부터 0 위치 */
    }
    .carousel-control-prev:hover,
    .carousel-control-next:hover {
      background-color: rgba(0, 0, 0, 0.8); /* 호버시 색상 변경 */
    }
    .carousel-control-prev-icon,
    .carousel-control-next-icon {
      width: 10px;
      height: 10px;
      font-size: 5px; /* 아이콘 크기 조정 */
      color: #fff; /* 아이콘 색상 지정 */
    }
    .reply {
    display: flex;
    flex-direction: row;
    align-items: center;
    padding: 10px 0;
    }
  
    .reply-profile-image {
      /* margin-top: -22px; */
      background-color: white;
      margin-top: -18px;
      width: 35px;
      height: 35px;
      min-width: 35px;
      border: 1px solid;
      border-radius: 50%;
    }
  
    .cm-interactions {
      padding-bottom: 10px;
      position: absolute;
      top: 65%;
      left: 5%;
      font-family: 'omyu_pretty';
      border-top: 2px solid #ddd;
      border-bottom: 2px solid #ddd;
      /* padding-top: 10px; */
      /* margin-top: 375px; */
      margin-bottom: 15px;
      width: 90%;
      }
      .no-comment {
        margin-top: 70px;
      }
  
    .comment,
    .reply {
    display: flex;
    flex-direction: row; /* 댓글 요소들을 가로로 배치 */
    align-items: center; /* 요소들을 세로 가운데 정렬 */
    padding: 10px 0; /* 위아래로 간격 추가 */
    }
    .reply {
    margin-left: 40px;
    }
  
    .comment-row-1 {
      display: flex;
      align-items: center;
    }
  
    .comment-interactions {
      margin-top: auto;
      font-family: 'omyu_pretty';
      /* border-top: 2px solid #ddd; */
      padding-top: 10px;
      display: flex;
      margin-left: 35px;
    }
    
    .view-count {
      font-family: 'omyu_pretty';
      display: inline-block;
    }
  
    .comment-count {
      font-family: 'omyu_pretty';
      display: inline-block;
      margin-left: auto;
      margin-right: 52px;
    }
  
    .comment-like,
    .reply2.comment-like2 {
        font-family: 'omyu_pretty';
        font-size: 1rem;
        color: #999;
        margin-right: 3px; /* 아이콘과 숫자 사이의 간격 조정 */
        display: flex;
        align-items: center;
        width: 47px;
    }
    .i.fas.fa-heart::before {
      font-size: 1rem;
      color: #999;
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
      color: rgb(221, 221, 221);
    }
    .fas.fa-heart.filled {
      color: rgb(245, 5, 5); /* 채워진 하트의 색상 */
    }
    
    .like-interactions {
      display: flex;
    }
  
    .user {
      font-size: 1rem;
      margin-left: 10px; 
      font-weight: bold;
    }
    .user-comment {
      font-size: 1rem;
      flex-grow: 1; /* 댓글 내용이 가능한 최대 너비를 가지도록 설정 */
      margin-left: 10px;
      margin-right: 10px;
      text-align: left;
    }
    
    .time-commented {
      font-size: 0.8rem;
      margin-top: 1px;
      margin-right: 5px;
      margin-left: 10px; 
      color: #999;
    }
  
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
      width: 90%;
      margin-left: auto;
      margin-right: 50px;
    }
    
    .addcomment-profile-image {
      background-color: white;
      border: 1px solid;
      border-radius: 50%;
      width: 40px; 
      height: 40px;
    }
    
    .add-comment {
      margin-left: 30px; 
    }
    .comment-input {
      padding: 10px;
      font-size: 1rem; 
      font-family: 'Ownglyph_meetme-Rg';
      border: 1px solid #ced4da; 
      border-radius: 50px; 
      margin-left: 10px;
    }
    .comment-input:focus {
      outline: none; /* 포커스 상태일 때 외곽선 제거 */
      border-color: #007bff; /* 포커스 상태일 때 테두리 색상 변경 */
    }
    .toggle-replies,
    .toggle-replies2,
    .re-reply-comment {
      cursor: pointer;
      display: flex;
      margin-left: 47px;
      margin-right: auto;
      width: 89%;
      font-size: 0.9rem;
      color: #5e5e5e;
      margin-top: 1px;
    }
    .re-reply-comment {
      margin-top: -10px;
      margin-left: 85px;
    }
    .toggle-replies2 {
      margin-left: 85px;
  
    }
   
    .editCommnet-input {
      font-family: 'Ownglyph_meetme-Rg';
      font-size: 0.8rem;
    }
    .comment-button {
      font-family: "Font Awesome 5 Free";
      font-size: 1rem;
      margin-left: 5px;
      padding: 10px;
      border: 1px solid #ced4da;
      border-radius: 50px;
    }
    
    .comment-button:focus {
      outline: none; /* 포커스 상태일 때 외곽선 제거 */
      border-color: #007bff; /* 포커스 상태일 때 테두리 색상 변경 */
    }
    .comment-profile-image,
    .reply-profile-image {
        margin-top: -18px;
        width: 35px;
        height: 35px;
        border: 1px solid;
        border-radius: 50%;
     }
     .comment-content,
     .reply-content {
        width: 100%;
     }
     .re-comment,
     .re-reply-comment {
      cursor: pointer;
      display: flex;
      font-size: 0.9rem;
      color: #999;
      width: 88%;
    }
    .re-comment{
      margin-left: 46px;
      margin-top: -10px;
    }
    .re-comment-input {
      width: 88%;
    }
  
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
      margin-left: 45px !important;
      width: 410px !important;
    }
    .replies .replies2 .re-comment-input input.addNewComment {
      margin-left: 80px !important;
      width: 745px !important;
    }
    input.addNewReplyComment{
      margin-left: 80px !important;
      width: 410px !important;
    }
    .reply2-content {
      width: 98% t !important;
      margin-top: 10px !important;
      margin-left: 40px !important;
    }
    .replies2 {
      margin-left: 20px;
    }
    .replies2 .toggle-replies {
      margin-left: 60px !important;
      margin-top: 0px
    }
    .fas.fa-heart {
      color: rgb(221, 221, 221)
    }
  
  </style> 
  
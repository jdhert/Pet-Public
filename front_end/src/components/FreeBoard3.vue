<template>
  <header class="banner">
    <h1 class="banner-title">반려동물의 실시간 인기 사진첩</h1>
  </header>
  <section class="ftco-section bg-light">
    <div class="freeboard">
      <div class="row1 d-flex">
        <div v-for="(post, index) in posts" :key="index" class="col-md-4 d-flex">
          <div class="content-entry align-self-stretch">
            <a @click.prevent="openModal(post)" class="block-20 rounded" :style="{backgroundImage: 'url(' + (post.imgPath ? post.imgPath : '@/assets/images/gallery-6.jpg') + ')'}"></a>
            <div class="text p-4">
              <div class="meta mb-2">
                <div><a href='#'>{{ formatDate(post.createdAt) }}</a></div>
                <div><a href="#">{{ post.writer }}</a></div>
                <div class="meta-chat">
                  <span class="fa fa-comment"></span> {{ post.commentCount}}
                  <span class="fa fa-heart" style="margin-left: 5px;"></span> {{ post.likeCount }}
                  <span class="fa-solid fa-eye" style="margin-left: 5px;"></span> {{ post.viewCount }}
                </div>
              </div>
              <h3 class="heading"><a href="#">{{ truncateTitle(post.title, 16) }}</a></h3>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
  <header>
    <img src="../assets/images/banner.png" alt="Banner" class="banner-image">  
    <h2>반려동물과의 일상을 사진과 함께 사람들과 공유하세요</h2>
  </header>
  <div class="search-bar" style="display: flex; align-items: center;">
    <div class="search-bar1">
      <select class="search-select1" v-model="type1">
        <option value="Latest">최신순</option>
        <option value="Oldest">오래된순</option>
      </select>
    </div>
    <div style="flex-grow: 0.08;">
      <select class="search-select" v-model="type">
        <option value="title">제목</option>
        <option value="content">내용</option>
        <option value="tag">태그</option>
        <option value="writer">작성자</option>
      </select>
    </div>
    <form @submit.prevent="searching">
      <input type="search" class="search-input" placeholder="검색어를 입력할거냥" v-model="search">
      <input type="submit" class="search-button" value="검색">
    </form>
  </div>
  <section class="ftco-section1 bg-light">
    <div class="freeboard2">
      <div class="row2">
        <div class="col-md-4 addpost-item" v-for="(addpost, index) in addposts" :key="index">
          <div class="normal-card content-entry align-self-stretch" @click="openModal(addpost)">
            <a class="block-20 rounded" :style="{backgroundImage: 'url(' + (addpost.imgPath ? addpost.imgPath : '@/assets/images/gallery-6.jpg') + ')'}"></a>
            <div class="text p-4">
              <div class="meta">
                <div class="createdAt"><a href="#">{{ formatDate(addpost.createdAt) }}</a></div>
                <div class="wirter"><a href="#">{{ addpost.writer }}</a></div>
                <div class="meta-chat">
                  <span class="fa fa-comment"></span> {{ addpost.commentCount }}
                  <span class="fa fa-heart" style="margin-left: 5px;"></span> {{ addpost.likeCount }}
                </div>
              </div>
              <h3 class="heading"><a href="#">{{truncateTitle(addpost.title, 16) }}</a></h3>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
  <button v-if="isLogin" class="btn btn-success mt-3 custom-button" @click="goToWrite">글쓰기</button>
  <div class="row mt-5">
    <div class="col text-center">
      <div class="block-27">
        <ul>
          <li><a href="#" @click="currentSwap(this.currentPage-1)">&lt;</a></li>
          <li>
            <a href="#"  v-for="n in this.numbers" :key="n" @click="currentSwap(n)" :class="{ 'active': currentPage === n }" style="margin: 5px;">{{ n }}</a>
          </li>
          <li><a href="#" @click="currentSwap(this.currentPage+1)">&gt;</a></li>
        </ul>
      </div>
    </div>
  </div>
  <detailFreeBoard v-if="showModal" :selectedCard="selectedCard" @closeModal="closeModal" @tagSearch="handleTagSearch" @deleteBoard="realDelete" @updateViewCount="increaseViewCount" :viewCount="selectedCard.viewCount" @openModal="handleModalOpen"/>
</template>


<script>
import detailFreeBoard from './detailFreeBoard.vue';

export default {
  emits: ['forceRerender'],
  computed: {
    isLogin() {
      return this.$cookies.isKey('id') ? true : false;
    }
  },
  components: {
    detailFreeBoard
  },
  props: {
    showModal: Boolean,
    selectedCard: Object
  },
  data() {
    return {
      showModal: false,
      selectedCard: {},
      posts: [],
      addposts: [],
      maxPage: 1,
      paginationLimit: 5,
      currentPage: 1,
      search: "",
      type: "title",
      type1: "Latest",
      numbers: [],
      postId: null,
    }
  },
  methods: {
    truncateTitle(title, maxLength) {
      if (title.length > maxLength) {
        return title.substring(0, maxLength) + '...';
      } else {
        return title;
      }
    },
    formatDate(dateString) {
      const date = new Date(dateString);
      const year = date.getFullYear();
      const month = ('0' + (date.getMonth() + 1)).slice(-2);
      const day = ('0' + date.getDate()).slice(-2);
      return `${year}-${month}-${day}`;
    },
    handleModalOpen(post) {
      this.increaseViewCount(post.id);
    },
    goToWrite() {
      this.$router.push(`/addphoto`);
    },
    currentSwap(n) {
      this.currentPage = Math.max(1, Math.min(n, this.maxPage));
      let startPage = Math.max(1, Math.floor((this.currentPage - 1) / this.paginationLimit) * this.paginationLimit + 1);
      this.getBoard(this.currentPage);
    },
    getPageNumbers() {
      this.numbers = [];
      let startPage = Math.max(1, Math.floor((this.currentPage - 1) / this.paginationLimit) * this.paginationLimit + 1);
      let endPage = Math.min(startPage + this.paginationLimit - 1, this.maxPage);

      for (let i = startPage; i <= endPage; i++) {
        this.numbers.push(i);
      }
    },
    async getBoard(startPage) {
      this.addposts = [];
      try {
        const res = await this.axios.get(`/api/free/search/${startPage}`, {
          params: {
            search: this.search,
            type: this.type,
            type1: this.type1,
            subject: 0
          }
        });
        this.addposts = res.data;
        this.maxPage = Math.ceil(this.addposts[0].totalRowCount / 8) || 1;
        this.getPageNumbers();
      } catch (error) {
        console.error(error);
      }
    },
    async openModal(post) {
      this.selectedCard = post;
      this.$emit('updateViewCount', post.id);
      this.showModal = true;
    },
    async increaseViewCount(postId) {
      try {
        const response = await this.axios.put(`/api/free/view/${postId}`);
        console.log('응답:', response.data);
        await this.fetchViewCount(postId);
        return response.data;
      } catch (error) {
        console.error('에러:', error);
        throw error;
      }
    },
    async fetchViewCount(postId) {
      try {
        const response = await this.axios.get(`/api/free/view/${postId}`);
        console.log('조회수:', response.data);
        return response.data;
      } catch (error) {
        console.error('조회수를 불러오는 중 오류 발생:', error);
        throw error;
      }
    },
   closeModal(postId) {
      this.showModal = false;
      this.fetchCommentCount(postId);
    },
    fetchCommentCount(postId) {
      this.axios.get(`/api/comment/totalCount`, {
        params: {
          boardId: postId
        }
      })
      .then((res) => {
        this.posts.forEach(post => {
          if (post.id === postId) {
            post.commentCount = res.data;
          }
        });
        this.addposts.forEach(addpost => {
          if (addpost.id === postId) {
            addpost.commentCount = res.data;
          }
        });
      })
      .catch(error => {
        console.error('댓글 개수를 가져오는 중 오류가 발생했습니다.', error);
      });
    },
    searching() {
      this.addposts = [];
      this.axios.get(`/api/free/search/1`, {
        params: {
          search: this.search,
          type: this.type,
          type1: this.type1,
          subject: 0
        }
      }).then((res) => {
        console.log(res.data);
        this.addposts = res.data;
        this.maxPage = Math.ceil(this.addposts[0].totalRowCount / 8) || 1;
        this.getPageNumbers();

      }).catch((error) => {
        console.error(error);
      });
    },
    realDelete(id) {
      this.showModal = false;
      console.log(id);
      this.axios.delete(`/api/free/${id}`)
        .then(() => {
          console.log('게시글이 성공적으로 삭제되었습니다.');
          this.getBoard();
          this.$cookies.remove('boardId');
          this.$router.push(`/freeboard3`).then(() => {
            window.location.reload();
          });
        })
        .catch(error => {
          console.error('게시글 삭제 중 오류가 발생했습니다.', error);
        });
    },
    handleTagSearch(tag) {
      this.showModal = false;
      this.axios.get(`/api/free/search/1`, {
        params: {
          search: tag,
          type: 'tag',
          type1: 'Latest',
          subject: 0
        }
      }).then((res) => {
        this.addposts = res.data;
        this.maxPage = Math.ceil(this.addposts[0].totalRowCount / 8) || 1;
        this.getPageNumbers();
      }).catch();
    },
    openModalForPost(postId) {
      this.axios.get(`/api/free/get/${postId}`)
        .then((res) => {
          this.selectedCard = res.data;
          this.showModal = true;
        })
        .catch((error) => {
          console.error('게시물 정보를 가져오는 중 오류 발생:', error);
        });
    },
    fetchPostData(boardId) {
      this.axios.get(`/api/free/get/${boardId}`)
        .then(response => {
          console.log(response.data);
          this.selectedCard = response.data;
          this.showModal = true;
        })
        .catch(error => {
          console.error('Error fetching post data:', error);
        });
    }
  },
  mounted() {
    
    this.axios.get(`/api/free/1`, {
      params: {
        subject: 0
      }
    }).then((res) => {
      this.addposts = res.data;
      this.maxPage = Math.ceil(this.addposts[0].totalRowCount / 8) || 1;
      this.getPageNumbers();
    }).catch((error) => {
      console.error('Error fetching data:', error);
    });

    this.axios.get(`/api/free/popular`, {
      params: {
        subject: 0
      }
    }).then((res) => {
      // console.log(res)
      this.posts = res.data;
    }).catch();

    this.postId = this.$cookies.get('postId');
    if (this.postId) {
      this.openModalForPost(this.postId);
      this.$cookies.remove("postId");
    }

    if (this.$route.path.includes('/get/') && this.$route.params.id) {
      const postId = this.$route.params.id;
      this.fetchPostData(postId);
    }
  }
}
</script>



<style scoped>
  @font-face {
    font-family: 'Ownglyph_meetme-Rg';
    src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_2402_1@1.0/Ownglyph_meetme-Rg.woff2') format('woff2');
    font-weight: normal;
    font-style: normal;
  }

  * {
    font-family: 'Ownglyph_meetme-Rg';
  }

  .banner {
    margin-top: 120px;
    text-align: center;
  }

  .banner-title {
    font-size: 2.3rem;
  }

  .banner-subtitle {
    font-size: 1.3rem;
    color: #777;
  }

  .banner-image {
    /* margin-top: 50px; */
    width: 50%;
  }

  .freeboard {
    display: flex;
    justify-content: center;
    max-width: 1200px;
    margin: 0 auto;
    background-color: white;
    margin-bottom: 90px;
  }

  .freeboard2 {
    /* display: grid; */
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    justify-items: start;
    width: 100%;
    margin: 0 auto;
    background-color: white;
  }

  .col-md-4 {
    margin-top: 20px;
    margin-right: 25px;
    margin-left: 25px;
  }

  .search-bar {
    display: flex;
    align-items: center;
    width: 1000px; 
    margin: 0 auto;
    border: 3px solid #4ea3ff; 
    border-radius: 50px;
    padding: 5px; 
}

.search-bar1 {
  margin-right: 5px;
}

.search-select1 {
  font-family: 'Ownglyph_meetme-Rg';
  color: #222222;
  border-radius: 50px;
  width: 130px;
  border: none;
  border: 2px solid #4ea3ff; 
  background: #fcfdff; 
  padding: 10px;
  font-size: 20px;
  text-align: center;
  outline: none;
  cursor: pointer;
}
.search-select {
  font-family: 'Ownglyph_meetme-Rg';
  color: #222222;
  border-radius: 50px; 
  width: 130px;
  border: 2px solid #4ea3ff;
  background: #fcfdff;
  padding: 10px;
  font-size: 20px;
  text-align: center;
  outline: none;
  cursor: pointer;
}


.search-input {
  font-family: 'Ownglyph_meetme-Rg';
  width: 610px;
  border: none;
  background: none;
  padding: 5px;
  font-size: 20px;
  border-radius: 60px;
  text-align: center;
  outline: none;
}

.search-select option {
color: #222222;
background-color: #fcfdff;
padding: 5px;
border-radius: 60px;
font-size: 20px;
}

.search-select option:hover {
background-color: #4ea3ff;
color: #ffffff;
}
.search-button {
  font-family: 'Ownglyph_meetme-Rg';
  color: #ffffff;
  border: none;
  background-color: #8d8d8d;
  font-size: 20px;
  border-radius: 80px;
  cursor: pointer;
  outline: none;
}
.search-button:hover {
background-color: #4ea3ff;
}
  h1, h2, h3, h4, h5, h6 {
    margin-top: 0;
    margin-bottom: 0.5rem;
    line-height: 1.5;
    color: rgba(0, 0, 0, 0.8);
    font-weight: 400;
  }

  a {
    color: #007bff;
    text-decoration: none;
    background-color: transparent;
  }

  a:hover {
    color: #0056b3;
    text-decoration: underline;
  }

  .rounded {
    border-radius: 0.25rem !important;
  }

  a {
    -webkit-transition: .3s all ease;
    -o-transition: .3s all ease;
    transition: .3s all ease;
    color: #007bff;
  }

  a:hover, a:focus {
    text-decoration: none;
    color: #007bff;
    outline: none !important;
  }

  .bg-secondary {
    background: #207dff !important;
  }

  .bg-primary {
    background: #007bff !important;
  }

  .p-4 {
    padding: 1.5rem !important;
  }

  a.text-dark:hover, a.text-dark:focus {
    color: #121416 !important;
  }

.freeboard {
  font-family: "Montserrat", Arial, sans-serif;
  background: #fff;
  font-size: 15px;
  line-height: 1.8;
  font-weight: 400;
  color: gray;
}

.freeboard.menu-show {
  overflow: hidden;
  position: fixed;
  height: 100%;
  width: 100%;
}

.row1 {
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -ms-flex-wrap: wrap;
  flex-wrap: wrap;
  margin-right: -15px;
  margin-left: -15px;
}

/* 이미지 CSS 부분 */
.block-20 {
  overflow: hidden;
  background-size: cover;
  background-repeat: no-repeat;
  background-position: center center;
  height: 250px;
  position: relative;
  display: block;
}

.block-20 img {
  height: 250px;
}

.row2 {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin: 0 auto;
  max-width: 1450px;
  background-color: white;
}

/* 컨텐츠 부분 */
.content-entry {
  background: #fff;
  box-shadow: 0px 10px 18px -8px rgba(0, 0, 0, 0.1);
  width: 300px;
}

.content-entry .text {
  position: relative;
  border-top: 0;
  border-radius: 2px;
}

.content-entry .text .tag {
  color: #b3b3b3;
}

.content-entry .text .heading {
  font-size: 18px;
  margin-bottom: 16px;
  font-weight: 400;
  cursor: pointer;
}

.content-entry .text .heading a {
  color: #000000;
}

.content-entry .text .heading a:hover,
.blog-entry .text .heading a:focus,
.blog-entry .text .heading a:active {
  color: #007bff;
}

.content-entry .text .meta-chat {
  color: #007bff;
}

.content-entry .text .read {
  color: #000000;
  font-size: 14px;
}

.content-entry .meta > div {
  display: inline-block;
  margin-right: 5px;
  margin-bottom: 5px;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.content-entry .meta > div a {
  color: #999999;
  font-weight: 500;
}

.content-entry .meta > div a:hover {
  color: #333333;
}

.content-entry .btn-custom {
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 1px;
  font-weight: 500;
  color: #007bff;
}

.content-entry .meta .createdAt {
  width: 100%;
}

form {
  margin-bottom: 0px;
}

.addpost-item {
  margin: 20px;
  width: 300px;
}

/* 페이지네이션 부분 */
.mt-5 {
  display: flex;
  justify-content: center;
}

.block-27 {
  margin-top: 50px;
  justify-items: center;
}

.block-27 ul {
  padding: 0;
  margin: 0;
  display: inline-block;
}

.block-27 ul li {
  display: inline-block;
  margin-bottom: 4px;
  font-weight: 400;
  margin-right: 5px;
}

.block-27 ul li a,
.block-27 ul li span {
  color: gray;
  text-align: center;
  display: inline-block;
  width: 40px;
  height: 40px;
  line-height: 40px;
  border-radius: 50%;
  border: 2px solid #e6e6e6;
}

.block-27 ul li.active a,
.block-27 ul li.active span {
  background: #007bff;
  color: #fff;
  border: 1px solid transparent;
}

.custom-button {
  font-family: 'Ownglyph_meetme-Rg';
  margin-left: 1320px;
  padding: 10px 20px;
  font-size: 16px;
  background-color: #8d8d8d;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

/* 모달창 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  margin: 0;
  padding: 0;
  background: rgba(0, 0, 0, 0.5);
}

.modal.show {
  display: block;
}

.preview {
  position: fixed;
  margin: 0.5rem auto;
  margin-top: 100px;
  pointer-events: none;
}

.preview {
  position: relative;
  display: flex;
  flex-direction: column;
  pointer-events: auto;
  background-color: #fff;
  background-clip: padding-box;
  border: 1px solid rgba(0, 0, 0, 0.2);
  border-radius: 0.3rem;
  outline: 0;
}

.preview {
  display: flex;
  align-items: center;
  padding: 0.5rem;
  border-bottom: 1px solid #dee2e6;
  background-color: #f7f7f7;
}

.modal {
  display: flex;
  justify-content: center;
}

.btn-close {
  background-color: transparent;
  border: none;
  border-radius: 100%;
  padding: 0;
  cursor: pointer;
  width: 15px;
  height: 15px;
  transition: background-color 0.3s ease;
  position: absolute;
  top: 10px;
  right: 10px;
}

.btn-close:hover {
  background-color: rgba(0, 0, 0, 0.1);
}

@media screen and (min-width: 1024px) and (max-width: 1440px) {
  .addpost-item {
    width: 33.33%;
  }
  .freeboard2 {
    display: flex;
    justify-content: center;
  }
  .row2 {
    display: grid;
    justify-content: center;
    grid-template-columns: repeat(2, 1fr);
  }
  .content-entry {
    width: 445px;
  }
  .custom-button {
    margin-left: 885px;
  }
}

@media screen and (min-width: 768px) and (max-width: 1024px) {
  .addpost-item {
    width: 50%;
  }
  .freeboard2 {
    display: flex;
    justify-content: center;
  }
    .row2 {
      display: grid;
      justify-content: center;
      grid-template-columns: repeat(2, 1fr);
    }
    .content-entry {
      width: 325px;
    }
  .search-bar {
    width: 90vw;
  }
    .search-select {
      width: 10vw
    }
    .search-select1 {
      width: 10vw;
    }
    .search-input {
      width: 52vw;
    }
    .search-button {
      width: 15vw;
    }
  .custom-button {
    margin-left: 85%;
  }
}

@media (max-width: 768px) {
  .addpost-item {
    width: auto;
    margin-left: 5%;
    margin-right: 0;
  }
  .row2 {
    display: grid;
    grid-template-columns: repeat(2, 1fr); /* 한 줄에 두 개의 컬럼 생성 */
    grid-auto-rows: minmax(100px, auto); /* 모든 행의 최소 높이를 100px로 설정 */
    gap: 0;
  }
    .normal-card {
      height: 100%; /* 부모 요소의 높이에 맞춰 자식 요소의 높이를 설정 */
      width: 88%;
    }
  .search-bar {
    width: 90vw;
  }
    .search-select {
      width: 10vw
    }
    .search-select1 {
      width: 10vw;
    }
    .search-input {
      width: 45vw;
    }
    .search-button {
      width: 20vw;
    }
  .custom-button {
    margin-left: 85%;
  }
}

.btn-close {
  background-color: transparent;
  border: none;
  border-radius: 100%;
  padding: 0;
  cursor: pointer;
  width: 15px;
  height: 15px;
  transition: background-color 0.3s ease;
  align-self: flex-end;
}

.btn-close:hover {
  background-color: rgba(0, 0, 0, 0.1);
}

.active {
  background-color: #61bffd;
  color: #fff !important;
  border: 1px solid transparent;
}

.align-self-stretch{
  cursor: pointer;
}

.align-self-stretch:hover{
  box-shadow: 0 8px 12px rgba(0, 0, 0, 0.2);
  transform: translateY(-3px);
}
</style>



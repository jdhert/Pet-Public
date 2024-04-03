<template>
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.15.3/css/all.css">
    <div class="container">
        <div class="content">
            <h1 class="banner-title">Q&A 인기 게시글</h1>
            <div class="best-card-columns">
                <div class="card best-card" v-for="(bestpost, index) in bestposts" :key="index"
                    style="width: 200px"  @click="openModal(bestpost)">
                    <div class="card-header best-header">
                        <span class="tag" :class="getTagClass(bestpost.category)">{{ bestpost.category }}</span>
                        <h2 class="card-title">{{ truncateTitle(bestpost.title, 16) }}</h2>
                    </div>
                    <div class="card-body best-body">
                        <p>{{ truncateText(bestpost.content, 75) }}</p>
                    </div>
                    <div class="card-footer">
                        <div class="date">{{ formatDate(bestpost.createdAt) }}</div>
                        <div class="stats">
                            <div class="viewCount"> {{ bestpost.viewCount }} <i class="fas fa-eye"></i></div>
                            <div class="likeCount">{{ bestpost.likeCount }} <i class="far fa-heart"></i></div>
                            <div class="comments">{{ bestpost.commentCount }} <i class="far fa-comment"></i></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <br>
        <header class="banner">
            <h1 class="banner-title">반려동물 무엇이든 물어보라냥</h1>
            <h3 class="banner-subtitle">반려동물의 궁금증을 사람들과 공유해보세요</h3>
            <img src="../assets/images/img6.png" alt="Banner" class="banner-image">
            <br>
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
                <input type="search" class="search-input"  placeholder="검색어를 입력할거냥" v-model="search">
                <input type="submit" class="search-button" value="검색">
            </form>
        </div>
        <br>
        <div class="content">
            <div class="card-columns">
                <div class="card normal-card" v-for="post of posts" :key="post.id"
                    :style="{ width: getCardWidth(posts.length) }"  @click="openModal(post)">
                    <div class="card-header">
                        <span class="tag" :class="getTagClass(post.category)">{{ post.category }}</span>
                        <h2 class="card-title">{{ truncateTitle(post.title, 16) }}</h2>
                    </div>
                    <div class="card-body">
                        <p>{{ truncateText(post.content, 75) }}</p>
                    </div>
                    <div class="card-footer">
                        <div class="date">{{ formatDate(post.createdAt) }}</div>
                        <div class="stats normal-stats">
                            <div class="viewCount"> {{ post.viewCount }} <i class="fas fa-eye"></i></div>
                            <div class="likeCount">{{ post.likeCount }} <i class="far fa-heart"></i></div>
                            <div class="comments">{{ post.commentCount }} <i class="far fa-comment"></i></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- <QuestionBoardModal v-if="showQnaModal" :selectedPost="selectedPost" @closeModal="closeModal" :images="images" @tagSearch="handleTagSearch" @deleteBoard="realDelete"/> -->
        <detailQnaBoard v-if="showQnaModal" :selectedPost="selectedPost" @closeModal="closeModal" :images="images" @tagSearch="handleTagSearch" @deleteBoard="realDelete"/>
        <button v-if="isLogin" class="btn btn-success mt-3 custom-button" @click="goToWrite">글쓰기</button>
        <div class="row mt-5">
            <div class="col text-center">
                <div class="block-27">
                    <ul>
                        <li><a href="#" @click="currentSwap(this.currentPage-1)">&lt;</a></li>
                        <li><a href="#"  v-for="n in numbers" :key="n" @click="currentSwap(n)" :class="{ 'active': currentPage === n }" style="margin: 5px;">{{ n }}</a></li>
                        <li><a href="#" @click="currentSwap(this.currentPage+1)">&gt;</a></li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
// import QuestionBoardModal from './QuestionBoardModal.vue';
import detailQnaBoard from './detailQnaBoard.vue';
export default {
    components: {
        // QuestionBoardModal
        detailQnaBoard
    },
    props: {
        showQnaModal: Boolean,
        selectedPost: Object
    },
    computed: {
        isLogin() {
            return this.$cookies.isKey('id') ? true : false;
        }
    },
    data() {
        return {
            selectedPost: {},
            showQnaModal: false,
            posts: [],
            maxPage: 1,
            paginationLimit: 5,
            currentPage: 1,
            images: [],
            search: "",
            type: "writer",
            type1: "Latest",
            bestposts: [],
            numbers: [],
            postId: null,
        };
    },
    async mounted() {
        try {
            const postsResponse = await this.axios.get(`/api/free/1`, {
                params: {
                    subject: 1
                }
            });
            this.posts = postsResponse.data;
            this.maxPage = Math.ceil(this.posts[0].totalRowCount / 8);
            if (this.maxPage === 0) {
                this.maxPage = 1;
            }
            this.getPageNumbers();

            const popularResponse = await this.axios.get(`/api/free/popular`, {
                params: {
                    subject: 1,
                }
            });
            this.bestposts = popularResponse.data;

            this.postId = this.$cookies.get('postId');
            if (this.postId) {
                this.openModalForPost(this.postId);
                this.$cookies.remove("postId");
            }
           
            if (this.$route.path.includes('/get/') && this.$route.params.id) {
                const postId = this.$route.params.id;
                this.fetchPostData(postId);
            }
        } catch (error) {
            console.error('Error fetching data:', error);
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
        fetchPostData(boardId) {
            this.axios.get(`/api/free/get/${boardId}`)
            .then(response => {
                console.log(response.data);
                this.selectedPost = response.data;
                this.showQnaModal = true;
            })
            .catch(error => {
                console.error('Error fetching post data:', error);
            });
        },
        currentSwap(n) {
            this.currentPage = Math.max(1, Math.min(n, this.maxPage));
            console.log(this.curre)
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
        getBoard(startPage) {
            this.posts = [];
            this.axios.get(`/api/free/search/${startPage}`, {
                params: { 
                    search: this.search,
                    type: this.type,
                    type1: this.type1,
                    subject: 1
                }
            }).then((res) => {
                this.posts = res.data;
                this.maxPage = Math.ceil(this.posts[0].totalRowCount / 8);
                if (this.maxPage == 0)
                    this.maxPage = 1;
                this.getPageNumbers();
            }).catch((error) => {
                console.error(error);
            });
        },
        getTagClass(tag) {
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
        getCardWidth(postCount) {
            if (postCount === 1) {
                return '25%'; // 화면 너비의 25%
            } else if (postCount === 2) {
                return '50%'; // 화면 너비의 50%
            } else if (postCount === 3) {
                return '75%'; // 화면 너비의 75%
            } else if (postCount >= 4) {
                return '100%'; // 화면 너비의 100%
            }
        },
        goToWrite() {
            this.$router.push(`/addqna`);
        },
        openModal(post) {
            this.selectedPost = post;
            this.showQnaModal = true;
            this.axios.put(`/api/free/view/${this.selectedPost.id}`, {
                id: this.selectedPost.id
            })
            .then(response => {
                console.log('조회수 업데이트 성공:', response.data);
            })
            .catch(error => {
                console.error('조회수 업데이트 실패:', error);
            });
        },
        closeModal(postId) {
            this.showQnaModal = false;
            this.fetchCommentCount(postId);
        },
        fetchCommentCount(postId){
            this.axios.get(`/api/comment/totalCount`, {
                params: {
                    boardId: postId
                }
            })
            .then((res) => {
                this.bestposts.forEach(bestpost => {
                if (bestpost.id === postId) {
                    bestpost.commentCount = res.data;
                }
                });
                this.posts.forEach(post => {
                if (post.id === postId) {
                    post.commentCount = res.data;
                }
                });
            })
            .catch(error => {
                console.error('댓글 개수를 가져오는 중 오류가 발생했습니다.', error);
            });
        },
        realDelete(id){
            this.showQnaModal = false;
            this.axios.delete(`/api/free/${id}`)
            .then(() => {
                console.log('게시글이 성공적으로 삭제되었습니다.');
                this.getBoard();
                this.$cookies.remove('boardId');
                this.$router.push(`/qnaboard`).then(() => {
                    window.location.reload();
                });
            })
            .catch(error => {
                console.error('게시글 삭제 중 오류가 발생했습니다.', error);
            });
        },
        truncateText(text, maxLength) {
            if (!text || text.length === 0) {
                return ''; 
            }
            if (text.length > maxLength) {
                return text.slice(0, maxLength) + '...';
            } else {
                return text;
            }
        },
        searching() {
            this.posts = [];
            this.axios.get(`/api/free/search/1`, {
                params: {
                    search: this.search,
                    type: this.type,
                    type1: this.type1,
                    subject: 1,
                }
            }).then((res) => {
                this.posts = res.data;
                this.maxPage = Math.ceil(this.posts[0].totalRowCount / 8);
                if (this.maxPage == 0)
                    this.maxPage = 1;
                this.getPageNumbers();
            }).catch((error) => {
                console.error(error);
            });
        },
        handleTagSearch(tag) {
            this.showQnaModal = false;
            this.axios.get(`/api/free/search/1`, {
                params: {
                    search: tag,
                    type: 'tag',
                    type1: 'Latest',
                    subject: 1
                }
            }).then((res) => {
                this.posts = res.data;
                this.maxPage = Math.ceil(this.posts[0].totalRowCount / 8);
                if (this.maxPage == 0)
                    this.maxPage = 1;
                this.getPageNumbers();
            }).catch();
        },
        openModalForPost(postId) {
            this.axios.get(`/api/free/get/${postId}`)
                .then((res) => {
                    this.selectedPost = res.data;
                    this.showQnaModal = true;
                    this.axios.put(`/api/free/view/${this.selectedPost.id}`, {
                        id: this.selectedPost.id
                    }).then(response => {
                        console.log('조회수 업데이트 성공:', response.data);
                    }).catch(error => {
                        console.error('조회수 업데이트 실패:', error);
                    });
                })
                .catch((error) => {
                    console.error('게시물 정보를 가져오는 중 오류 발생:', error);
                });
        },
        fetchPostData(boardId) {
            this.axios.get(`/api/free/get/${boardId}`)
                .then(res => {
                    this.selectedPost = res.data;
                    this.showQnaModal = true;
                })
                .catch(error => {
                    console.error('Error fetching post data:', error);
                });
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
i {
    font-size: 0.9rem;
}

.container {
    width: 100%;
    margin-top: 120px;
    padding: 20px;
}

.meta-chat {
    position: relative;
    display: flex;
    justify-content: right;
    top: -10px;
    right: 5px;
    color: #777;
}

.banner {
    margin-top: 0px;
    text-align: center;
}

.banner-image {
    width: 60%;
    height: auto;
    margin-bottom: 20px;
}

.banner-title {
    font-family: 'Ownglyph_meetme-Rg';
    font-size: 2.3rem;
    margin-bottom: 20px;
}

.banner-subtitle {
    font-family: 'Ownglyph_meetme-Rg';
    font-size: 1.3rem;
    color: #777;
}


.content {
    margin-bottom: 30px;
}

.card-columns {
    /* display: grid;
    grid-template-columns: repeat(auto-fill, minmax(20rem, 1fr));
    gap: 20px; */
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px; 
    justify-items: start; 
    max-width: 1450px;
    margin: 0 auto; 
    background-color: white;
}
.best-card-columns {
    display: flex;
    justify-content: center;
    gap: 20px;
    margin-bottom: 90px;
}

.likeCount .viewCount .comments {
    font-family: 'Ownglyph_meetme-Rg';
    font-size: 0.8rem;
}
.card {
    background-color: #fff;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    border-radius: 8px;
    overflow: hidden;
    transition: box-shadow 0.4s ease, transform 0.4s ease;
    width: 100%;
    min-width: 20rem;
    cursor: pointer;
}
.card:hover {
    box-shadow: 0 8px 12px rgba(0, 0, 0, 0.2);
    transform: translateY(-3px);
}
.card-body {
    font-size: 1.rem;
}

.card-header {
    /* margin-top: 20px; */
    padding: 20px;
    border-radius: 5px;
    background-color: #ececec;
    height: 120px;
}

.tag {
    display: inline-block;
    padding: 5px 10px;
    border-radius: 20px;
    color: #fff;
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


.card-title {
    font-size: 1.2rem;
    margin: 10px 0;
}

.card-body {
    padding: 20px;
    height: 100px;
}

.card-footer {
    padding: 20px;
    background-color: #ffffff;
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: #777;
}
.date {
    margin-right: 45px;
}

.custom-button {
    margin-left: 1380px;
    padding: 10px 20px;
    background-color: #8d8d8d;
    color: #fff;
    border: none;
    border-radius: 5px;
    cursor: pointer;
}

.pagination {
    display: flex;
    justify-content: center;
    gap: 10px;
}

.page-link {
    border: none;
    background-color: transparent;
    color: #333;
    padding: 5px 10px;
    border-radius: 5px;
    cursor: pointer;
}

.page-link:hover {
    background-color: #f0f0f0;
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
    grid-template-columns: repeat(4, 1fr); /* 한 줄에 4개의 열을 생성 */
    gap: 20px; /* 열 사이의 간격 설정 */
    margin: 0 auto;
    max-width: 1450px;
    background-color: white;
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

a:hover,
a:focus {
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

a.text-dark:hover,
a.text-dark:focus {
    color: #121416 !important;
}

.mt-5 {
    display: flex;
    justify-content: center;
}

.block-27 {
    margin-top: 50px; /* 페이지네이션과의 간격 조정 */
    justify-items: center; /* 페이지네이션 가운데 정렬 */
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
    margin-right: 5px; /* 페이지네이션 간격 조정 */
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
    background-color: #007bff;
    color: #fff;
    border: 1px solid transparent;
}

.active {
    background-color: #61bffd;
    color: #fff !important;
    border: 1px solid transparent;
}

form {
    margin: 0px;
}

@media screen and (min-width: 1440px) and (max-width: 2560px) {
    .custom-button {
        margin-left: 95%
    }
}

@media screen and (min-width: 1024px) and (max-width: 1440px) {
    .best-card-columns {
        gap: 15px;
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
    .card-columns {
        grid-template-columns: repeat(2, 1fr);
    }

    .custom-button {
      margin-left: 93%;
    }
}

@media screen and (min-width: 768px) and (max-width: 1024px) {
    .card-columns {
        grid-template-columns: repeat(2, 1fr);
    }
        .card {
            min-width: 0;
        }
        .best-card {
            height: auto;
        }
            .best-header {
                height: 130px;
            }
            .best-body {
                height: 140px;
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
      margin-left: 90%;
    }
}

@media screen and (max-width: 768px) {
    .card-columns {
        grid-template-columns: repeat(2, 1fr);
    }
    .best-card {
        min-width: 21rem;
    }
    .normal-card {
        width: 45vw;
        min-width: 0;
    }
        .card-header {
            height: 35%;
        }
        .card-body {
            height: 120px;
        }
        .normal-stats {
            display: flex;
            flex-direction: column;
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
</style>
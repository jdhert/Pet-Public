<template>
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
	<body id="top">
		<section id="MyBanner">
			<div class="inner"></div>
		</section>
		<!-- Page Content-->
		<section class="py-5">
			<div class="container px-5 my-5" style="box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);">
				<div class="row gx-5">
					<div class="col-lg-3" id="col-lg-3">
						<div class="d-flex align-items-center mt-lg-5 mb-4" id="mt-lg-5">
							<img class="img-fluid rounded-circle" :src="this.user.imgPath || defaultImage" alt="..." id="profil-img"/>
                            <div class="profil-status">
                                <div class="" id="myname">
                                    <div class="fw-bold">{{this.user.name}}</div>
                                    <div class="text-muted">{{this.user.email}}</div>
                                </div>
                                <div id="myButton">
                                    <button type="button" class="btn btn-info" id="mybtn" onclick = "location.href = '/diary'"><div class="fw-bold">내 일지</div></button>
                                    <button type="button" class="btn btn-info" id="mybtn" onclick = "location.href = '/myfeed'"><div class="fw-bold">내 활동</div></button>
                                </div>
                                <div id="contentCount">
                                    <div class="text-muted" id="PageCount">게시물 수 : {{ this.user.boardCount }}</div><hr>
                                    <div class="text-muted" id="PageCount">댓글 수 : {{ this.user.commentCount }}</div><hr>
                                    <div class="text-muted" id="PageCount">기록일지 수 : {{  this.user.diaryCount }}</div><hr>
                                </div>
                            </div>
							<div class="d-grid gap-2">
								<button class="btn btn-lg btn-primary" type="button" id="mybtn" onclick = "location.href = '/userupdate'">회원정보 수정</button>
                                <button class="btn btn-lg btn-primary" v-if="!this.user.social" type="button" id="mybtn" onclick = "location.href = '/updatepw'">비밀번호 변경</button>
								<button class="btn btn-lg btn-primary" type="button" id="mybtn" onclick = "location.href = '/deleteuser'">회원탈퇴</button>
							</div>
						</div>
					</div>
					
					<div class="col-lg-9" id="col-lg-9">
					<!-- Post content-->
						<article>
							<div class="myPetList">
								<!-- Post header-->
								<header class="mb-4" id="PostHeader">
									<!-- Post title-->
									<h1 class="fw-bolder mb-1">마이 펫</h1>
									<!-- Post meta content-->
									<div class="text-muted fst-italic mb-2">나의 귀여운 아이들 한눈에!</div>
									<!-- Post categories-->
									<button class="btn btn-lg btn-primary" type="button" id="mybtn" onclick = "location.href = '/addpet'">펫 등록</button>
									<hr>
								</header>
								<!-- Pet section-->
								<section class="d-flex justify-content-center">
									<div class="card bg-light">
                                        <div>
                                            <div class="PetList">
                                                <div class="card-body d-flex" id="pet-card" v-for="pet of pets" :key="pet" >	
                                                    <a href="#" @click="goToPetDetail(pet.id)">
                                                        <div class="flex-shrink-0"><img class="rounded-circle" :src="pet.img" alt="..." /></div>
                                                        <div class="ms-3">
                                                            <div id="card-src">
                                                                <div class="fw-bold">이름</div>
                                                                <div id="card-content">{{ pet.name }}<hr></div>
                                                            </div>
                                                            <div id="card-src">
                                                                <div class="fw-bold">나이</div>
                                                                <div id="card-content">{{ pet.age }}<hr></div>
                                                            </div>
                                                            <div id="card-src">
                                                                <div class="fw-bold">품종</div>
                                                                <div id="card-content">{{ pet.species }}<hr></div>
                                                            </div>
                                                        </div>
                                                        <div class="pet-datail">자세히 보기</div>
                                                    </a>
                                                    <button class="delete-button" @click.stop="deletePet(pet.id)">X</button>
                                                </div>
                                                <div class="card-body d-flex flex-shrink-0 align-items-center" id="pet-card">
                                                    <a href="/addpet"><img class="rounded-circle" src="../assets/images/plus.png" alt="..." /></a>
                                                    <h5 class="addPet">반려동물을 추가해주세요</h5>
                                                </div>
                                            </div>
                                        </div>
									</div>
								</section>
							</div>
								<!-- 2nd section-->
								<section>
									<header class="mb-4" id="PostHeader">
										<h1 class="fw-bolder mb-1"><a href="/diary">내 기록일지</a></h1>
										<div class="text-muted fst-italic mb-2">하루하루 내 반려동물들의 이야기를 기록해주세요</div>
										<button class="btn btn-lg btn-primary" type="button" id="mybtn" onclick = "location.href = '/create'">일지 등록</button>
										<hr>
									</header>
									<div class="card board-container">
										<div class="board-header">
											<h4 class="header-title">기록일지 제목</h4>
											<h4 class="header-name">이름</h4>
											<h4 class="header-date">날짜</h4>
										</div>
										<div class="board-content">
                                            <div v-if="hasPosts">
                                                <div class="board-item" v-for="post in currentPagePosts" :key="post.id">
                                                    <div class="item-header">
                                                        <img src="../assets/images/CalenderIcon.png" class="calenderIcon">
                                                        <h5><a href="#" @click.prevent="goToDiary(post.id)">{{ post.title }}</a></h5>
                                                        <div class="item-content">
                                                            <p><a href="#" @click.prevent="goToPet(post.petId)">{{ post.petName }}</a></p>
                                                        </div>
                                                        <span>{{ post.createdAt.split('T')  [0] }}</span>
                                                    </div>
                                                    <hr class="item-divider">
                                                </div>
                                            </div>
                                            <div v-else>
                                                <div class="board-item">
                                                    <div class="item-header justify-content-center my-1" >
                                                        <h5 style="color: #b0b0b0;">등록된 게시글이 없습니다.</h5>
                                                    </div>
                                                </div>
                                            </div>
										</div>
                                        <div class="pagination" v-if="hasPosts">
                                            <button class="page-link" @click="goToPreviousPage">«</button>
                                            <button class="page-link" v-for="n in displayedPages" :key="n" :class="{ 'current-page-link': n === currentPage }" @click="goToPage(n)">{{ n }}</button>
                                            <button class="page-link" @click="goToNextPage">»</button>
                                        </div>
                                        <div class="pagination" v-else>
                                            <button class="page-link">«</button>
                                            <button class="page-link current-page-link">1</button>
                                            <button class="page-link">»</button>
                                        </div>
									</div>
								</section>
							<!-- <section class="mb-5">
								<div class="mb-5-1">
								</div>
							</section> -->
						</article>
					   </div>
				</div>
			</div>
		</section>
        <div class="PetCalc">
            <a href="/bmi" class="acalc">
              <h2>비만도<br>계산기</h2>
            </a>
        </div>
	</body>	 
</template>

<script>
	export default {
	  data() {
		return {
		  posts: [],
		  user:{},
		  pets: [],
		  maxpage : 5,
          currentPage: 1, // 현재 페이지를 추적하는 데이터 추가
          itemsPerPage: 10, // 페이지당 아이템 수
          havePassword : false,
          defaultImage: require('../assets/images/default.jpg'),
		};
	  },
      computed: {
            // 현재 페이지의 시작 인덱스
            startIndex() {
                return (this.currentPage - 1) * this.itemsPerPage;
            },
            // 현재 페이지의 끝 인덱스
            endIndex() {
                return Math.min(this.currentPage * this.itemsPerPage, this.posts.length);
            },
            // 현재 페이지에 표시할 데이터
            currentPagePosts() {
                return this.posts.slice(this.startIndex, this.endIndex);
            },
            // 전체 페이지 개수
            pageCount() {
                return Math.ceil(this.posts.length / this.itemsPerPage);
            },
            hasPosts() {
                return this.posts.length > 0;
            },
            displayedPages() {
                const totalPages = Math.ceil(this.posts.length / this.itemsPerPage);
                let startPage;
                let endPage;
                if (this.currentPage <= 3) {
                    startPage = 1;
                    endPage = Math.min(totalPages, 5);
                } else if (this.currentPage >= totalPages - 2) {
                    startPage = Math.max(1, totalPages - 4);
                    endPage = totalPages;
                } else {
                    startPage = this.currentPage - 2;
                    endPage = this.currentPage + 2;
                }
                const displayedPages = [];
                for (let i = startPage; i <= endPage; i++) {
                    displayedPages.push(i);
                }
                return displayedPages;
            },
        },
      methods: {
        goToPetDetail(petId) {
            this.$router.push({ path: '/petdetail', query: { petId: petId } });
        },
        
        // 삭제 버튼 클릭 시 실행되는 함수
        deletePet(petId) {
        // 확인 메시지 표시
            if (confirm("삭제하시겠습니까?")) {
                // 확인을 클릭하면 axios를 사용하여 서버에 DELETE 요청을 보냄
                this.axios.delete(`/api/pet/${petId}`).then((res) => {
                    // 삭제가 성공하면 새로고침 또는 다시 렌더링하여 변경된 상태 반영
                    alert("삭제되었습니다.");
                    // 예를 들어, 페이지를 다시 불러오는 방법은 다음과 같습니다.
                    window.location.reload();
                })
                .catch((error) => {
                    // 삭제에 실패하면 오류 메시지 출력
                    console.error("삭제 실패:", error);
                    alert("삭제에 실패하였습니다. 다시 시도해주세요.");
                });
            }
        },
        
        goToDiary(id){
			this.$cookies.set('diaryId', id);
			this.$router.push('/carousel');
		},
        goToPet(id){
            this.$router.push({ path: '/petdetail', query: { petId: id } });
		},

        goToPage(n) {
            this.currentPage = n;
        },
        // 이전 페이지로 이동하는 함수
        goToPreviousPage() {
            if (this.currentPage > 1) {
                this.currentPage--;
            }
        },
        // 다음 페이지로 이동하는 함수
        goToNextPage() {
            if (this.currentPage < this.pageCount) {
                this.currentPage++;
            }
        },
      },
	  mounted() {  
	    if (!this.$cookies.get("id")) {
	    	alert("로그인이 필요합니다.");
	    	this.$router.push('/login');
	    	return;
	    }
		this.axios.get(`/api/myinfo/${this.$cookies.get("id")}`).then((res) => {
			this.user = res.data;
            this.axios.get(`/api/myinfo/img/${this.$cookies.get("id")}`).then((res) => {
                this.user.imgPath = res.data;
            });
		}).catch();
		this.axios.get(`/api/myinfo/pet/${this.$cookies.get("id")}`).then((res) => {
			this.pets = res.data;
            this.axios.get(`/api/pet/${this.$cookies.get("id")}`).then((res) => {
                res.data.forEach((item, i) => {
                    this.pets[i].img = item;
                });
            });
		}).catch();
		this.axios.get(`/api/myinfo/diary/${this.$cookies.get("id")}`).then((res)=> {
			this.posts = res.data;
            this.maxpage = Math.ceil(this.posts.length / this.itemsPerPage);
		}).catch();
	  },
	}
</script>

	
<style scoped>

/* Font */

@import url('https://fonts.googleapis.com/css2?family=Black+Han+Sans&display=swap');

@font-face {
    font-family: 'KBO-Dia-Gothic_bold';
    src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_2304-2@1.0/KBO-Dia-Gothic_bold.woff') format('woff');
    font-weight: 700;
    font-style: normal;
}

@font-face {
    font-family: 'SUITE-Regular';
    src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_2304-2@1.0/SUITE-Regular.woff2') format('woff2');
    font-weight: 400;
    font-style: normal;
}

@font-face {
    font-family: 'Ownglyph_meetme-Rg';
    src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_2402_1@1.0/Ownglyph_meetme-Rg.woff2') format('woff2');
    font-weight: normal;
    font-style: normal;
}

/* MyBanner */

#MyBanner {
    background-image: url(../assets/images/mypage_background3.jpg);
    background-size: cover;
    background-position: center ;
    background-repeat: no-repeat;
    color: #d8d8d8;
    padding: 14em 0;
    text-align: center;
    position: relative;
    height: 200px;
}

#MyBanner:before {
    content: '';
    position: absolute;
    left: 50%;
    top: 0;
    transform: translateX(-50%); /* 가운데로 이동 */
    width: 100%; /* 너비를 60%로 설정 */
    height: 100%;
    background: rgba(64, 72, 80, 0.25);			
}

#MyBanner .inner {
    position: relative;
    z-index: 1;
}

#MyBanner .inner :last-child {
    margin-bottom: 0;
}

/* content */

.py-5 {
    background-color: #fafafa;
    margin-bottom: -330px;
}

.px-5 {
    position: relative;
    top: -330px;
    background-color: white;
    padding: 30px;
    border-radius: 5px;
    min-width: 950px;
}

@media screen {
    .col-lg-9 {
        width: 75%;
    }
    @media (max-width: 540px){
        .col-lg-9 {
            width: 100%;
        }

    }
}




.myPetList {
    margin-bottom: 5rem;
}

#PostHeader {
    text-align: left;
    margin-top: 10px;
    /* margin-bottom: 0.5rem!important; */
}

.mb-1 {
    display: inline;
    font-family: 'KBO-Dia-Gothic_bold'; 
    letter-spacing: normal;
    margin-left: 15px;
    font-size: 35px;
    color: #1897f1;
}

.mb-2 {
    display: inline;
    margin-left: 10px;
    font-family: 'SUITE-Regular';
    font-weight: bold;
}

#PostHeader > button {
    position: relative;
    float: right;
    right: 20px;
    width: 125px;
    font-family: 'KBO-Dia-Gothic_bold';
    background-color: #a7d3f3;
    border: 2px;
    border-style: solid;
    border-color: #c3e6ff;
    bottom: 5px;
}

#PostHeader > button:hover {
    background-color: #85ccff;
    border: 2px;
    border-style: solid;
    border-color: #9ed2f8;
}

#PostHeader > button:active {
    background-color: #6ac1ff !important;
    border-color: #9ed2f8 !important;
}

#pet-card {
    position: relative;
    display: flex !important;
    justify-content: center;
    flex-direction: column;
    max-width: 260px;
    min-width: 260px;
    width: calc(33.33% - 20px); 
    height: 430px;
    padding: 10px;
    margin-top: 1rem;
    margin-bottom: 1rem;
    margin-right: 0;

    background-color: white;
    border-style: solid;
    border-width: 1px;
    border-radius: 20px;
    border-color: #bbbbbb;
    margin-bottom: 1rem; 
}

@media screen {
    .card {
        width: 100%;
    }

    @media screen and (max-width: 540px) {
        .card {
            width: 95%;
            height: fit-content;
        }
    }
   
}


@media screen {
    .PetList {
        display: flex;
        flex-wrap: wrap;
        justify-content: flex-start;
        gap: 3%;
        margin: 30px;
    }    
    @media screen and (max-width: 540px) {
        .PetList {
            margin-left: 5%;
            margin-right: 5%;
        }
    }
}


.flex-shrink-0 {
    display: flex;
    justify-content: center;
    align-items: center ;
}

.addPet {
    font-family: 'Ownglyph_meetme-Rg';
    color: #929292;
}

.flex-shrink-0 > img {
    max-width: 100%;
    max-height: 100%;
    width: 200px;
    height: 200px;
    margin-top: 1rem;
    margin-bottom: 2rem;

    border: 5px;
    border-style: solid;
    border-color: #d4e5ff;
}

.flex-shrink-0 > a > img {
    max-width: 100%;
    max-height: 100%;
    width: 200px;
    height: 200px;
    margin-top: 1rem;
    margin-bottom: 1rem;
    
    border: 2px;
    border-style: solid;
    border-color:#dbdbdb;
}

.d-flex > a > .ms-3 {
    margin-left: 0rem !important;
    margin-bottom: 1rem;
}

.d-flex > a {
    width: 100%;
    text-decoration-line: none;
    color: black;
}

#card-src {
    position: relative;
    display: flex;
    justify-content: center;
    align-items : center;
    flex-wrap: wrap;
    flex-direction: row;
    width: 95%;
    margin-bottom: 0.5rem;
    margin-left: 5px;
    margin-right: 5px;
}

#card-src .fw-bold {
    font-family: 'Ownglyph_meetme-Rg';
    font-size: 23px;
    background-image: url(../assets/images/skyPaint.jpg);
    background-size: 100%;
    background-position: center center;
    background-repeat: no-repeat;
    width: 30%;
} 

#card-content {
    font-family: 'Ownglyph_meetme-Rg';
    font-size: 23px;
    width: 60%;
    margin-left: 15px;
}

#card-content > hr {
    margin-bottom: 0rem;
}

.pet-datail {
    font-family: 'Ownglyph_meetme-Rg';
    font-size: 18px;
    color: rgba(112, 112, 112, 0.377);
}

/* board */

   .board-container {
    background-color: #fff; /* White background */
    border: 1px solid #DEE2E6; /* Light grey border */
    border-radius: 4px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
    /* padding: 1rem; */
    margin: auto;
  }

  .board-header {
    font-family: 'Ownglyph_meetme-Rg';
    background-color: aliceblue;
    display: flex;
    justify-content: end;
    padding-top: 0.8rem;
    padding-bottom: 0.5rem;
    border-bottom: 2px solid #DEE2E6;
    gap: 50px;
  }

  .header-title {
    width: 50%;
  }

  .header-name {
    width: 12%;
  }

  .header-date {
    width: 22%;

  }
  .board-content {
    margin-bottom: 25px;
  }

  .board-item {
    background-color: white;
    padding: 0.5rem 0;
    margin-left: 1rem;
    margin-right: 1rem;
    border-bottom: 1px solid #DEE2E6; /* Light grey border for each item */
  }

  .item-header {
    font-family: 'Ownglyph_meetme-Rg';
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .item-header > * {
    margin: 0;
    font-size: 18px;
    color: #495057; /* Dark grey color for text */
  }

  .item-header img {
    margin-left: 5px;
    width: 22px;
  }

  .item-header h5 {
    width: 50%;
  }

  .item-header > .item-content {
    width: 20%;
  }

  .item-header > .item-content > p a {
    color: inherit;
    text-decoration: none;
  }

  .item-header span {
    width: 20%;
  }

  .item-details {
    text-align: right;
  }

  .item-author {
    font-size: 0.875rem;
    color: #6C757D; /* Light grey color for author */
  }

  .item-date {
    font-size: 0.875rem;
    color: #ADB5BD; /* Even lighter grey for date */
    margin-left: 0.5rem;
  }

  .item-content p {
    margin: 0;
    color: #495057; /* Match the title color */
  }
  
  .item-divider {
    display: none; /* Remove the hr as we're using border-bottom for each item */
  }

  /* Board_Pagination*/

.pagination {
    display: flex;
    justify-content: center;
    gap: 2px;
	margin-bottom: 20px;
}

.page-link {
    font-family: 'Ownglyph_meetme-Rg';
    width: 36px;
    border: none;
    background-color: transparent;
    color: #333;
    padding: 5px 10px;
    border-radius: 50%;
    cursor: pointer;
}

.page-link:hover {
    background-color: #f0f0f0;
}

.current-page-link {
    border: 1px solid #e0e0e0;
}

/* sideBar */
@media screen {
    .col-lg-3 {
        width: 25%;
        box-shadow: 1px 0px 0 #f0f0f0;
    }    
    @media screen and (max-width: 540px) {
        .col-lg-3 {
            width: 100%;
            box-shadow: 0px 0px 0 #f0f0f0;
            border: 2px solid #ebebeb;
            border-radius: 20px ;
            margin-top: 20px;
            margin-bottom: 50px;
            padding-top: 20px !important;
            padding-bottom: 20px;
        }
    }
}

@media screen {
    #mt-lg-5 {
        justify-content: center;
        flex-direction: column;
    }    
    @media screen and (max-width: 540px) {
        #mt-lg-5 {
            justify-content: space-around;
            flex-direction: row;
            margin-bottom: 0px !important;
        }    
    }
}


#profil-img {
    margin-bottom: 1rem !important;
    /* border 테두리 지정 */
    border: 5px;
    border-style: solid;
    border-color: #BDE3FF;
    /* max-width: 120px;
    width: 100%; */
    max-width: 100%;
    max-height: 100%;
    height: 120px;
    width: 120px;
    object-fit: contain;
}
@media screen {
    .profil-status {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
    }

    @media screen and (max-width: 540px) {
        .profil-status {
            display: flex;
            flex-wrap: wrap;
        }
        .profil-status > .myButton {
            margin-left: auto;
            margin-right: auto;
        }
    }
}


@media screen {
    #myname {
        margin-bottom: 1rem !important;
    }
    @media screen and (max-width: 540px) {
        #myname {
            display: flex;
            flex-direction: row;
            width: 50%;
        }
        #myname > * {
            width: max-content;
            margin-right: 10px;
        }
    }
}


#myname .fw-bold {
    font-family: 'Ownglyph_meetme-Rg';
    font-size: 20px;
}

#myButton {
    margin-bottom: 1rem !important;
}

#myButton > button {
    border: 2px;
    font-family: 'KBO-Dia-Gothic_bold';
    border-style: solid;
    border-color: #cbe6fa;
    background-color: #b1dfff;
    margin-right: 1rem !important;
    margin-bottom: 0.5rem !important;
}

#myButton > button:hover {
    background-color: #85ccff;
    border: 2px;
    border-style: solid;
    border-color: #9ed2f8;
}

#myButton > button:active {
    background-color: #6ac1ff;
}


#myButton > button .fw-bold{
    color: white;
}


@media screen {
    #contentCount {
        text-align: left;
        width: 140px;
    }
    @media screen and (max-width: 540px) {
        #contentCount {
            display: flex;
            width: 100%;
            gap: 10px;
        }   
    }
}


#PageCount {
    color: #929292 !important;
    font-family: 'Ownglyph_meetme-Rg';
    font-size: 20px;
    border-bottom: 0rem !important;
}

.d-grid {
    margin-top: 1rem !important;
    width: 190px;
}

.d-grid > button {
    font-family: 'KBO-Dia-Gothic_bold';
    background-color: #a7d3f3;
    border: 2px;
    border-style: solid;
    border-color: #a7d3f3;
}

.d-grid > button:hover {
    background-color: #85ccff;
    border: 2px;
    border-style: solid;
    border-color: #9ed2f8;
}

.d-grid > button:active {
    background-color: #6ac1ff !important;
    border-color: #9ed2f8 !important;
}

hr {
    margin-top: 0rem !important;
    /* size: 0.1px;
    background-color:#85ccff;
    height: 2px; */
}

#mybtn {
    color: white;
    border-color: #ECEFF1;
    border: 3px solid;
    border-radius: 40px;
    font-weight: bold;
    letter-spacing: 2px;
    text-transform: uppercase;
    font-size: 14px;
}

.delete-button {
    position: absolute;
    top: 5px;
    right: 5px;
    z-index: 1;
    font-family: 'KBO-Dia-Gothic_bold';
    background-color: #ffffff;
    color: #cdcdcd;
    border: 0px #ffffff;
    border-radius: 20px;
}

.delete-button:hover {
    background-color: #efefef;
    color: #cdcdcd;
}

.delete-button:active {
    background-color: #dfdfdf;
    color: #cdcdcd;
}

.PetCalc {
    position: fixed;
    width: 100px;
    height: 110px;
    top: 83%;
    margin-top: -50px;
    right: 1.5%;
    /* background:#fff3fb; */
    background-image: url(../assets/images/PetScale.png);
    background-repeat: no-repeat;
    background-position: center;

    border-radius: 20px;
    border: 3px solid #f0f0f0;    
    /* box-shadow: 0 0 0 2px white; */
    text-shadow: 2px 2px 2px rgb(231, 231, 231);
}

.PetCalc > a > div {
    margin: 10px 2px 10px 2px;
}

.PetCalc > a {
    text-decoration-line: none;
    font-family: 'Ownglyph_meetme-Rg';
    color: rgb(0, 0, 0);
}

.PetCalc > a > * {
  position: relative;
  font-size: 27px;
  top: 105px
}
.PetCalc:hover {
    transform: scale(1.1); /* 마우스를 올렸을 때 크기를 확대하는 효과 */
}
</style>
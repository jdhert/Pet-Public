<template>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <body>
        <div class="background-image">
            <div class="card">
                <div class="card-list-container">
                    <header>
                        <div class="title-image">
                            <img src="../assets/images/banner3.png" alt="Banner" class="banner-image">  
                            <h1>반려동물 기록 일지</h1>
                        </div>
                    </header>
                    <div class="d-flex mx-5 my-3" id="filter-buttons">
                        <div>
                            <button class="btn active" data-filter="all" onclick="location.href='/diary'">
                                <img src="../assets/images/gallery.png" alt=""> 갤러리로 보기
                            </button>
                            <button class="btn" data-filter="nature" onclick="location.href='/calendar'">
                                <img src="../assets/images/calendar1.png" alt=""> 캘린더로 보기
                            </button>
                        </div>
                    </div>
                    <div class="diary-card m-2">
                        <div class="card-list m-3">
                            <div v-for="(diary, index) in this.diary" :key="index" class="card-item" @click.prevent="goTocarousel(diary.diaryId)">
                                <div class="card-img"><img :src="diary.imgPath" alt="Card Image"></div>
                                <div class="info-wrapper my-2">
                                    <div class="info-top">
                                        <span class="name">{{ diary.petName }}</span>
                                        <span class="developer">{{ diary.createdAt ? diary.createdAt.split('T')[0] : 'No date' }}</span>
                                    </div>
                                    <div class="info-bottom">
                                        <h3 class="petcontent">{{ truncateTitle(diary.title, 10) }}</h3>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="pagination">
                        <button class="page-link" @click="goToPreviousPage">«</button>
                        <button class="page-link" v-for="n in displayedPages" :key="n" :class="{ 'current-page-link': n === currentPage }" @click="goToPage(n)">{{ n }}</button>
                        <button class="page-link" :class="{ 'disabled': isLastPage }" @click="goToNextPage">»</button>
                    </div>
                </div>
            </div>
        </div>
    </body>
</template>
    
<script scoped>
export default {
    data() {
        return {
          maxpage : 5,
          currentPage: 1, // 현재 페이지를 추적하는 데이터 추가
          itemsPerPage: 12, // 페이지당 아이템 수// Total number of items in your data
          diary: [],
          Count: 0,
        };
    },
    computed: {
        isLastPage() {
            console.log(this.Count,"www")
            return this.currentPage === Math.ceil(this.Count/this.itemsPerPage);
        },
        // 현재 페이지의 시작 인덱스
        startIndex() {
            return (this.currentPage - 1) * this.itemsPerPage;
        },
        // 현재 페이지의 끝 인덱스
        endIndex() {
            return Math.min(this.currentPage * this.itemsPerPage, this.diary.length);
        },
        // 현재 페이지에 표시할 데이터
        // currentPagePosts() {
        //     return this.diary.slice(this.startIndex, this.endIndex);
        // },
        // 전체 페이지 개수
        pageCount() {
            return Math.ceil(this.diary.length / this.itemsPerPage);
        },
        displayedPages() {
            const totalPages = Math.ceil(this.maxpage / this.itemsPerPage);
            let startPage;
            let endPage;
            // if (this.currentPage <= 3) {
            //     startPage = 1;
            //     endPage = Math.min(totalPages, 5);
            // } else if (this.currentPage >= totalPages - 2) {
            //     startPage = Math.max(1, totalPages - 4);
            //     endPage = totalPages;
            // } else {
            //     startPage = this.currentPage - 2;
            //     endPage = this.currentPage + 2;
            // }
            const displayedPages = [];
            for (let i = 1; i <= totalPages; i++) {
                displayedPages.push(i);
            }
            return displayedPages;
        },
            
    },
    methods: {
        truncateTitle(title, maxLength) {
            if (title.length > maxLength) {
                return title.substring(0, maxLength) + '...';
            } else {
                return title;
            }
        },
        goToPreviousPage() {
            if (this.currentPage > 1) {
                this.currentPage--;
                this.fetchDiaryImages(); // 페이지 변경 시 다시 이미지 가져오도록 호출
            }
        },
        // 다음 페이지로 이동하는 함수
        goToNextPage() {
            if (this.currentPage < this.maxpage) {
                this.currentPage++;
                this.fetchDiaryImages(); // 페이지 변경 시 다시 이미지 가져오도록 호출
            }
        },
        // 페이지 이동 시 이미지를 가져오는 함수
        fetchDiaryImages() {
            this.axios.get(`/api/myinfo/getMainImage/${this.$cookies.get("id")}`, {
                params:{
                    page: this.currentPage
                }
            })
            .then((res) => {
                console.log(this.$cookies.get("id"));
                console.log(res.data);
                this.diary = res.data;
                this.maxpage = res.data[0].maxPage;

                // 이미지 데이터를 JSON 형식으로 변환하여 localStorage에 저장
                localStorage.setItem('diaryImage', JSON.stringify(res.data));
            })
            .catch((error) => {
                console.error('이미지 가져오기 오류:', error);
            });
        },
        // 기존의 goToPage 메서드도 fetchDiaryImages를 호출하도록 수정
        goToPage(n) {
            this.currentPage = n;
            this.fetchDiaryImages();
        },
        // goTocarousel 메서드도 변경된 페이지에 맞게 호출
        goTocarousel(diaryId) {
            this.$cookies.set('diaryId', diaryId);
            this.$router.push('/carousel');
        },    
    },
    mounted() {
        if (!this.$cookies.get("id")) {
        alert("로그인이 필요합니다.");
        this.$router.push('/login');
        return;
    }

    const cachedUrl = localStorage.getItem('diaryImage');
    console.log('diaryImage')
    if (!cachedUrl) {
        // 캐시된 데이터가 없는 경우
        this.fetchDiaryImages();
    } else {
        // 캐시된 데이터가 있는 경우
        const localImages = JSON.parse(cachedUrl);
        this.axios.get(`/api/myinfo/getMainImage/${this.$cookies.get("id")}`, {
            params:{
                page : this.currentPage
            }
        })
            .then((res) => {
                console.log(this.$cookies.get("id"));
                console.log(res.data);
                const dbImages = res.data;
                this.maxpage = res.data[0].maxPage;
                this.Count = res.data[0].diaryCount;

                const newImages = dbImages.filter(dbImage => {
                    return !localImages.some(localImage => localImage.diaryId === dbImage.diaryId);
                });

                // 새로운 이미지가 있을 때만 fetchDiaryImages() 호출
                if (newImages.length > 0) {
                    this.fetchDiaryImages();
                } else {
                    // 새로운 이미지가 없으면 기존 데이터로 업데이트
                    this.diary = localImages;
                }
            })
            .catch((error) => {
                console.error('이미지 가져오기 오류:', error);
            });
        }
    }
};
</script>

<style scoped>
/* Importing Google font - Open Sans */
@import url('https://fonts.googleapis.com/css2?family=Open+Sans:wght@300;400;500;600;700&display=swap');
@font-face {
    font-family: 'Ownglyph_meetme-Rg';
    src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_2402_1@1.0/Ownglyph_meetme-Rg.woff2') format('woff2');
    font-weight: normal;
    font-style: normal;
}

* {
    font-family: 'Ownglyph_meetme-Rg';
}

h1 {
    margin-top: 1rem;
    margin-bottom: 2rem;
    font-family: 'Ownglyph_meetme-Rg';
    font-size: 40px;
}

a {
    color: #a7d3f3;
}

/* main */

.background-image{
    background-image: url(../assets/images/동물들배경.png);
    background-repeat: no-repeat;
    background-size: cover;
    background-position: center top;
    width: 100%;
    height: 100%;
    background-position: 50% 30%;
}

.card {
    width: 920px;
    height: 65%;
    margin: 70px auto 70px auto;
    padding: 0px;
    border: 2px solid #dadada;
    display: inline-block;
    border-radius: 10px;
    background: #fff;
    box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.card-list-container {
    margin: 1em;
    background-color: #fff;    
}

/* Header */

.title-image {
    align-content: center;
}

/* filter-button */

#filter-buttons button {
  border-radius: 3px;
  background: #fff;
  border-color: transparent;
  font-family: 'Ownglyph_meetme-Rg';
  padding:0px;
  width: 120px;
  height: 25px;
  font-size: 15px;
  cursor: pointer; /* 손가락 모양의 커서로 설정 */
  border: 1px solid #74b1e7;
}

#filter-buttons button:hover {
  background: #ddd;
}

#filter-buttons button.active {
  color: #fff;
  background: #74b1e7;
  border: 0px;
}

#filter-buttons button img {
    max-width: 20px;
    max-height: 50px;
}

/* Diary */

.diary-card {
    padding: .5rem; /* 내부 패딩 추가 */
}

.card-list {
    display: grid;
    grid-template-columns: repeat(4, 1fr); /* 각 카드가 동일한 너비로 나란히 위치하도록 설정 */
    gap: 10px; /* 카드 사이의 간격 설정 */
}

.card-list span {
    display: inline-block;
    background: #e6fcff;
    padding: 8px 15px;
    font-size: 0.75rem;
    font-weight: 600;
}

.card-list .card-item {
    display: flex;
    flex-direction: column; 
    background: #fff;
    padding: 5px;
    box-shadow: 0px 5px 10px rgba(0, 0, 0, 0.04);
    list-style: none;
    cursor: pointer;
    border-radius: 10px;
    text-decoration: none;
    border: 2px solid transparent;
    transition: border 0.5s ease;
    width: 200px;
    height: 300px;
}

.card-list .card-item:hover {
    border: 2px solid #ccf0f5
}

.card-list .card-item img {
    width: 100%;
    aspect-ratio: 10/10;
    border-radius: 4px;
    object-fit: cover;
}

.card-list .name{
    background-color: #f1fdff; 
    color: #034c7c;
    background-size: 10px;
    border-radius: 15px;
    margin-right: 5px;
}

.card-list .developer {
    background-color: #f1fdff; 
    color: #034c7c;
    background-size: 10px;
    border-radius: 15px;
}   

.card-list .designer {
    background-color: #d1e8ff;
    color: #2968a8;
}

.card-list .editor {
    background-color: #d6f8d6; 
    color: #205c20;
}

.card-item .arrow {
    display: flex;
    align-items: center;
    justify-content: center;
    transform: rotate(-35deg);
    height: 40px;
    width: 40px;
    color: #000;
    border: 1px solid #000;
    border-radius: 50%;
    margin-top: 40px;
    transition: 0.2s ease;
}

.card-list .card-item:hover .arrow  {
    background: #66d4ff;
    color: #fff; 
}

.card-img {
    height: 100%;
}

.info-top {
    display: flex;
    justify-content:space-evenly;
    margin: 0.5rem;
}

.info-bottom > h3{
    font-size: 1.25em;
}

.petcontent{
    font-family: 'Ownglyph_meetme-Rg';
}

/* Pagination */

.pagination {
    display: flex;
    justify-content: center;
    gap: 2px;
    margin-top: 20px;
	margin-bottom: 20px;
}

.page-link {
    font-family: 'Ownglyph_meetme-Rg';
    width: 36px;
    height: 36px;
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

.disabled {
    pointer-events: none; /* 마우스 이벤트를 무시하도록 함 */
    opacity: 0.5; /* 비활성화된 모습을 나타내기 위해 투명도를 줄임 */
    cursor: not-allowed; /* 커서를 기본으로 변경하여 사용자에게 버튼이 비활성화되었음을 알림 */
}

@media (max-width: 1200px) {
    .card-list .card-item {
        padding: 15px;
    }
}

@media screen and (max-width: 980px) {
    .card-list {
        margin: 0 auto;
    }
}

@media screen and (max-width: 768px) {
    .card-list .card-item {
        margin-right: 5px; /* 카드 사이의 간격을 줄입니다. */
        flex-grow: 0; /* 모든 공간을 차지하지 않도록 설정합니다. */
        max-width: calc(50% - 5px); /* 카드의 최대 너비를 설정합니다. */
    }

    .card-list span {
        margin-top: 16px; /* 화면 크기가 작을 때 margin-top을 줄입니다. */
        padding: 6px 10px; /* 화면 크기가 작을 때 padding을 줄입니다. */
        font-size: 0.65rem; /* 화면 크기가 작을 때 font-size를 줄입니다. */
    }
}
@media screen and (max-width: 480px) {
    .card-list .card-item {
        margin-right: 5px; /* 카드 사이의 간격을 줄입니다. */
        flex-grow: 0; /* 모든 공간을 차지하지 않도록 설정합니다. */
        max-width: calc(50% - 5px); /* 카드의 최대 너비를 설정합니다. */
    }

    .card-list span {
        margin-top: 12px; /* 화면 크기가 매우 작을 때 margin-top을 더 줄입니다. */
        padding: 4px 8px; /* 화면 크기가 매우 작을 때 padding을 더 줄입니다. */
        font-size: 0.6rem; /* 화면 크기가 매우 작을 때 font-size를 더 줄입니다. */
    }
}

</style>
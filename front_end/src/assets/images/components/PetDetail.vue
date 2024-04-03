<template>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
<body id="top">
    <section id="DetailBanner">
    </section>
    <!-- Page Content-->
    <section class="py-5">
        <div class="container px-5 my-5" style="box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);">
            <div class="PetDetail">
                <!-- Calender List-->
                <header class="mb-4" id="PostHeader">
                    <!-- Post title-->
                    <h1 class="fw-bolder mb-1">마이 펫</h1>
                    <!-- Post meta content-->
                    <div class="text-muted fst-italic mb-2">내 반려동물의 상세정보를 기록해주세요</div>
                    <hr>
                </header>
                <div class="row d-flex no-gutters" id="petProfil">
                    <div class="col-md-5 d-flex">
                        <img :src="pet.img" class="img-fluid">
                    </div>
                    <div class="col-md-7 pl-md-5 py-md-3">
                        <div class="heading-section pt-md-5" id="PetName">
                            <div class="text-muted">Name</div>
                            <div class="d-flex">
                              <h2 class="mb-4 fw-bold">{{ pet.name }}</h2>
                              <img :src="require(pet.gender === 'F' ? '../assets/images/female-6925547.png' : '../assets/images/male-2404544.png')" alt="Gender Icon" style="height: 32px;" class="m-1">
                            </div>
                        </div>
                        <div class="row" id="status">
                            <div class="col-md-6 services-2 w-100 d-flex" id="PetAge">
                                <div class="icon d-flex align-items-center justify-content-center"></div>
                                <div class="text pl-3">
                                    <div class="text-muted">Age </div>
                                    <h4 class="fw-bold">{{ pet.age }}</h4>
                                </div>
                            </div>
                            <div class="col-md-6 services-2 w-100 d-flex" id="PetWeight">
                                <div class="icon d-flex align-items-center justify-content-center"></div>
                                <div class="text pl-3">
                                    <div class="text-muted">Weight</div>
                                    <h4 class="fw-bold">{{ pet.weight }}kg</h4>
                                </div>
                            </div>
                            <div class="col-md-6 services-2 w-100 d-flex" id="PetSpecies">
                                <div class="icon d-flex align-items-center justify-content-center"></div>
                                <div class="text pl-3">
                                    <div class="text-muted">Species</div>
                                    <h4 class="fw-bold">{{ pet.species }}<span v-if="pet.specSpecies">({{ pet.specSpecies }})</span></h4>
                                </div>
                            </div>
                            <div class="col-md-4 services-2 w-100 d-flex justify-content-around" id="PetStatus">
                              <h4 class="fw-bold my-3" :style="getDiseaseStyle">질병 여부</h4>
                              <h4 class="fw-bold my-3" :style="getRecogChipStyle">마이크로칩</h4>
                            </div>
                        </div>
                    </div>
                    <div class="edutOrDelete">
                      <div class="">
                          <button class="edit-butto my-1"  @click="goToBack()" title="뒤로가기">
                              <img src="../assets/images/back-page.png">
                          </button>
                      </div>
                      <div class="">
                          <button class="edit-butto my-1"  @click="goToPetUpdate(pet.id)" title="수정">
                              <img src="../assets/images/edit-button-84380.png">
                          </button>
                          <button class="delete-button my-1" @click="deletePet(pet.id)" title="삭제">
                              <img src="../assets/images/delete-7214242.png">
                          </button>
                      </div>
                    </div>
                </div>  
            </div>
            <div class="PetDiary">
                <!-- Calender List-->
                <header class="mb-4" id="PostHeader">
                    <!-- Post title-->
                    <h1 class="fw-bolder mb-1">펫 기록일지</h1>
                    <!-- Post meta content-->
                    <div class="text-muted fst-italic mb-2">내 반려동물의 하루하루 일지를 작성해보세요</div>
                    <div class="text-muted" id="AllDiary"><a href="/diary">전체 보기</a></div>
                    <hr>
                </header>
                <div class="DiaryList2">
				          <div class="row">
                    <div class="col-md-4 ftco-animate" v-for="(addpost) of addposts" :key="addpost">
                      <div class="work mb-4 img d-flex align-items-end" id="DiaryImg" :style="{backgroundImage: addpost.imgPath ? 'url(' + addpost.imgPath + ')' : 'url(https://www.shutterstock.com/image-vector/default-image-icon-vector-missing-260nw-2086941550.jpg)'}">
            	          <div class="desc w-100 px-4">
	                        <div class="text w-100 mb-3">
	              	          <span>{{ addpost.createdAt }}</span>
                            <h2><a href="#" @click.prevent="goToCarousel(addpost.diaryId)">{{ addpost.title }}</a></h2>
	                        </div>
                        </div>
                      </div>
                    </div>
                    <div class="col-md-4 ftco-animate">
                      <a href="/create"><div class="addDiary mb-4 img d-flex align-items-end" id="addDiary" onclick="">
                        <img src="../assets/images/plus.png" alt="Add Diary">
                      </div></a>
                    </div>                  
                  </div>
                </div>
                <hr>
            </div>
        </div>

        <div class="PetCalc">
            <a href="/bmi">
              <h2>비만도<br>계산기</h2>
            </a>
        </div>
    </section>
</body>	 
</template>
        
<script>    
export default {
  name: 'Query',
  data() {
    return {
      petId: this.$route.query.petId,
      pet: [],
      addposts: [],
      statusStyle: {
        color: '#ffffff',
        borderColor: '#bce0ff',
        backgroundColor: '#bfebff',
      },
    }
  },
  methods: {
    goToCarousel(diaryId) {
      this.$cookies.set('diaryId', diaryId);
      this.$router.push('/carousel');
    },
      goToBack() {
        this.$router.push('/mypage');
      },
      goToPetUpdate(petId) {
        this.$router.push({ path: '/petupdate', query: { petId: petId } });
      },
      deletePet(petId) {
        // 확인 메시지 표시
            if (confirm("삭제하시겠습니까?")) {
                // 확인을 클릭하면 axios를 사용하여 서버에 DELETE 요청을 보냄
                this.axios.delete(`/api/pet/${petId}`).then((res) => {
                    // 삭제가 성공하면 새로고침 또는 다시 렌더링하여 변경된 상태 반영
                    alert("삭제되었습니다.");
                    // 예를 들어, 페이지를 다시 불러오는 방법은 다음과 같습니다.
                    this.$router.push('/mypage');
                })
                .catch((error) => {
                    // 삭제에 실패하면 오류 메시지 출력
                    console.error("삭제 실패:", error);
                    alert("삭제에 실패하였습니다. 다시 시도해주세요.");
                });
            }
        },
  },
  mounted() {
	    if (!this.$cookies.get("id")) {
	    	alert("로그인이 필요합니다.");
	    	this.$router.push('/login');
	    	return;
	    }

      this.axios.get(`/api/pet/detail/${this.petId}`).then((res) => {
        this.pet = res.data;
        this.axios.get(`/api/pet/detail/img/${this.petId}`).then((res) => {
            this.pet.img = res.data;

            // console.log(this.pet.img)
        });
      }).catch();

      this.axios.get(`/api/pet/getDiary/${this.petId}`).then((res) => {
        this.addposts = res.data;
        console.log(this.addposts)
      })
      console.log(this.addposts);

	  },

  computed: {
    getDiseaseStyle() {
      return this.pet.disease == 1 ? this.statusStyle : {};
    },
    getRecogChipStyle() {
      return this.pet.recogChip == 1 ? this.statusStyle : {};
    }
  }
}
</script>

<style scoped> 
@import '../assets/css/skel.css';
@import '../assets/css/style.css';
@import '../assets/css/style-xlarge.css';

@font-face {
    font-family: 'Ownglyph_meetme-Rg';
    src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_2402_1@1.0/Ownglyph_meetme-Rg.woff2') format('woff2');
    font-weight: normal;
    font-style: normal;
}

h2, h4 {
    font-family: 'Ownglyph_meetme-Rg';

}

/* MyBanner */

#DetailBanner {
    background-image: url(../assets/images/bg_2-1.jpg);
    background-size: cover;
    background-position: center center;
    background-repeat: no-repeat;
    color: #d8d8d8;
    padding: 15em 0;
    text-align: center;
    position: relative;
    height: 200px;
}

#DetailBanner:before {
    content: '';
      position: absolute;
      left: 50%;
      top: 0;
     transform: translateX(-50%); /* 가운데로 이동 */
      width: 100%; /* 너비를 60%로 설정 */
      height: 100%;
      background: rgba(64, 72, 80, 0.25);			
}

.PetDetail {
    margin: 30px 50px 50px 50px;
}

#petProfil {
    margin: 0 auto; /* 수평 가운데 정렬을 위한 왼쪽과 오른쪽 마진을 자동으로 설정합니다. */
    margin-bottom: 2rem;
    margin-top: 2rem;
    max-width: 100%; 
    width: 100%;
    /* padding: 1.5rem; */
    /* background-color: #f8fbff; */
    justify-content: space-around;
}

.col-md-5 {
    padding-left: 0rem !important;
    width: 400px; 
    height: 400px; 
    overflow: hidden;
    justify-content: center;
    width: 400px;
}

.col-md-5 > img {
    align-self: stretch;
    align-items: center;
    justify-content: center;
    -webkit-box-pack: center;
    width: 400px;
    height: 400px;
    object-fit: cover;
    padding-right: 0px;

    border-radius: 100%;
    border: 5px solid #d4e5ff;
}

#status {
    margin : 0px 0px 0px 0px;
}

#status > .col-md-6 {
    padding-left: 30px !important;
    padding-right: 30px !important;

}

.col-md-7 {
    /* padding-left: 100px !important ; */
    padding-right: 12px !important;  
    width: 33% !important;
    
}

.mb-4 {
    margin-bottom: 1rem !important;
}

.pl-3 {
    text-align: left !important;
}

#PetName {
    padding-top: 1rem !important;
    padding-left: 30px !important;
    padding-right: 30px !important;
    text-align: left !important;
}

#PetAge {
    background-color: #f8fbff;
}

#PetSpecies {
    background-color: #f8fbff;
}

#AllDiary {
    display: inline;
    margin-left: 10px;
    font-family: 'Ownglyph_meetme-Rg';
    font-weight: bold;
    font-size: 24px;
}

#AllDiary > a {
    color: rgb(96, 184, 255);
    text-decoration: none;
}

.col-md-7 .d-grid {
    margin: 0 auto;
}

/* PetDiary */

.PetDiary { 
    margin: 70px 50px 50px 50px;
} 

/* PetCalc */

.PetCalc {
    position: fixed;
    width: 100px;
    height: 110px;
    top: 83%;
    margin-top: -50px;
    right: 1.5%;
    background:#fff3fb;
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

.DiaryList2 {
  width: 100%;
  padding-right: 15px;
  padding-left: 15px;
  margin-right: auto;
  margin-left: auto; 
  margin-top: 50px;
  margin-bottom: 30px;
}
  @media (min-width: 576px) {
    .container {
      max-width: 540px; } }
  @media (min-width: 768px) {
    .container {
      max-width: 720px; } }
  @media (min-width: 992px) {
    .container {
      max-width: 960px; } }
  @media (min-width: 1200px) {
    .container {
      max-width: 1140px; } }

.DiaryList2 .row {
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -ms-flex-wrap: wrap;
  flex-wrap: wrap;
}

.DiaryList2 .row .col-md-4 {
  position: relative;
  /* width: 100%; */
  padding-right: 15px;
  padding-left: 15px; 
  min-width: 310px;
}

#DiaryImg {
  display: -webkit-box !important;
  display: -ms-flexbox !important;
  display: flex !important;
  -webkit-box-align: end !important;
  -ms-flex-align: end !important;
  align-items: flex-end !important; 
}

.work .px-4 {
  padding-left: 1.5rem !important; 
}
  
.work {
  display: block;
  width: 100%;
  height: 270px;
  margin-bottom: 0;
  background-position: top center !important;
  z-index: 0;
  position: relative;
  overflow: hidden;
  border-radius: 4px; 
}

.work:after {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  content: '';
  background: #fff;
  opacity: 0;
  z-index: 0;
  -moz-transition: all 0.3s ease;
  -o-transition: all 0.3s ease;
  -webkit-transition: all 0.3s ease;
  -ms-transition: all 0.3s ease;
  transition: all 0.3s ease; }
          
.work .text {
  opacity: 0; }
            
@media (max-width: 991.98px) {          
.work .text {
  opacity: 1; } }
            
.work .text span {
  color: rgb(255, 255, 255); 
  font-family: 'Ownglyph_meetme-Rg';

}

.work h2 {
  color: #fff;
  line-height: 1.2;
  font-size: 16px;
  text-transform: uppercase;
  font-weight: 700;
  letter-spacing: 1px; }
            
.work h2 a {
  color: #fff; }

.work .desc {
  z-index: 1; }
          
.work .icon {
  position: absolute;
  top: 50%;
  right: 50%;
  width: 70px;
  height: 70px;
  content: '';
  z-index: 1;
  background: #00bd56;
  -webkit-transform: translate(50%, 50%);
  -ms-transform: translate(50%, 50%);
  transform: translate(50%, 50%);
  margin-top: -70px;
  opacity: 0;
  -webkit-border-radius: 50%;
  -moz-border-radius: 50%;
  -ms-border-radius: 50%;
  border-radius: 50%;
  -moz-transition: all 0.3s ease;
  -o-transition: all 0.3s ease;
  -webkit-transition: all 0.3s ease;
  -ms-transition: all 0.3s ease;
  transition: all 0.3s ease; }

@media (max-width: 991.98px) {
.work .icon {
  opacity: 1; } }

.work .icon span {
  color: #fff; }

.work:hover:after {
  opacity: .3; }
  
.work:hover .icon {            
  opacity: 1; }
          
.work:hover .text {            
  opacity: 1; }

.img {
  width: 100%;
  background-size: cover;
  background-repeat: no-repeat;
  background-position: center center; 
}

#mybtn {
  color: #000;
  font-size: 18px;
  background-color: aliceblue;
  font-family: 'Ownglyph_meetme-Rg';
  border-color: #ececec;
}

#mybtn:hover {
  background-color: rgb(198, 228, 255);
  border-color: #ececec;
}

#addDiary > img {
  width: 95%;
  height: auto;
  border: 2px solid #ececec;
  border-radius: 20px;
  display: block;
  margin: 0 auto;
}

.edutOrDelete {
  position: absolute;
  display: flex;
  justify-content: space-between;
}

.edutOrDelete > div {
  margin-left: 90px;
  margin-right: 90px;
  border: 2px solid #e2f0ff ;
  border-radius: 10px;
  /* box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); */

}

.edutOrDelete > div > button {
  background-color: #fff;
  border: none;
  margin: 2px;
}

.edutOrDelete > div > button > img {
  width: 25px;
}

#PetStatus .fw-bold {
  width: 110px;
  padding: 5px;
  border: 3px solid #ededed;
  border-radius: 10px;
  margin: 1rem 0.3rem 1rem 0.3rem;
  color: #e2e2e2;
  background-color: #fcfcfc ;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
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

.col-lg-9 {
    width: 75%;
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
    display: flex !important;
    justify-content: center;
    flex-direction: column;
    max-width: 260px;
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


.PetList {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    margin: 30px;
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
    border-color: #d8d7ff;
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

/* sideBar */

.col-lg-3 {
    width: 25%;
    box-shadow: 1px 0px 0 #f0f0f0;
}

#mt-lg-5 {
    /* div 내에 중앙 정렬 */
    display : flex;
    justify-content: center;
    align-items : center;
    /* 여러 요소 세로 정렬 */
    flex-direction: column;
}

#profil-img {
    margin-bottom: 1rem !important;
    /* border 테두리 지정 */
    border: 5px;
    border-style: solid;
    border-color: #BDE3FF;
}

#myname {
    margin-bottom: 1rem !important;
}

#myname .fw-bold {
    font-family: 'Ownglyph_meetme-Rg';
    font-size: 20px;
}

#myButten {
    margin-bottom: 1rem !important;
}

#myButten > button {
    border: 2px;
    font-family: 'KBO-Dia-Gothic_bold';
    border-style: solid;
    border-color: #cbe6fa;
    background-color: #b1dfff;
    margin-right: 1rem !important;
    margin-bottom: 0.5rem !important;
}

#myButten > button:hover {
    background-color: #85ccff;
    border: 2px;
    border-style: solid;
    border-color: #9ed2f8;
}

#myButten > button:active {
    background-color: #6ac1ff;
}


#myButten > button .fw-bold{
    color: white;
}

#contentCount {
    text-align: left;
    width: 140px;
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

</style>

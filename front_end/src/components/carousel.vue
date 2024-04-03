<template>
  <body>
    <div class="Diary">
      <div class="wrapper">
        <div class="image-container">
          <!-- 이미지의 사이즈를 조절하고 압축하여 사용 -->
          <img src="../assets/images/일기그림.png" alt="고양이" class="catImage">
          <!-- <img src="../assets/images/CalenderIcon.png" class="calenderIcon" @click="navigateToCalendar"> -->
        </div>
        <h2>오늘의 {{ name }} 일기 미리 볼거냥</h2>
        <div style="border: 1px solid #ccc; border-radius: 10px; background-color: white; margin-bottom: 15px; padding: 10px;"> 날짜 : {{ created_at }} </div>
        <div style="border: 1px solid #ccc; border-radius: 10px; background-color: white; margin-bottom: 15px; padding: 10px;"> 제목 : {{ title }}</div>
        <div style="border: 1px solid #ccc; border-radius: 10px; background-color: white; margin-bottom: 15px; padding: 10px;" spellcheck="false" required>{{ content }}</div>
        <div class="file-options">
          <div class="option file-name">
            <label>오늘의 기분</label>
            <div style="border: 1px solid #ccc; border-radius: 10px; background-color: white; margin-bottom: 15px; padding: 10px;">{{ mood }}</div>
          </div>
          <div class="option save-as">
            <label>오늘의 날씨</label>
            <div style="border: 1px solid #ccc; border-radius: 10px; background-color: white; margin-bottom: 15px; padding: 10px;">{{ weatherchange(weather) }}</div>
          </div>
        </div>
        <button type="submit" class="register-btn" onclick="location.href = '/diary'">목록가기</button>
        <button class="list-btn" @click="navigateToCalendar">일기 수정하기</button>
        <button class="list-btn" @click="deleteDiary()">일기 삭제하기</button>
      </div>
      <carousel :items-to-show="1">
        <slide v-for="slide in slides" :key="slide">
          <!-- 이미지의 사이즈를 조절하고 압축하여 사용 -->
          <img :src="slide.imgPath" alt="반려동물" class="slideImage">
        </slide>
        <template #addons>
          <Navigation />
          <!-- <Pagination /> -->
        </template>
      </carousel>
    </div>
  </body>
</template>

<script>
import 'vue3-carousel/dist/carousel.css'
import { Carousel, Slide, Pagination, Navigation } from 'vue3-carousel'

export default {
components: {
  Carousel,
  Slide,
  Pagination,
  Navigation,
},
data() {
  return {
    slides: [],
    title: '',
    content: '',
    mood: '',
    weather: '',
    name: '',
  }
},
methods: {
  onFileChange(e) {
  const file = e.target.files[0];
  this.file = file;
  this.imageUrl = URL.createObjectURL(file);
  },

  weatherchange(weather){
    if(weather == "sunny"){
      return "햇빛 쨍쨍"
    }
    if(weather == "cloudy"){
      return "흐림"
    }
    if(weather == "wind"){
      return "바람 쌩쌩"
    }
    if(weather == "rain"){
      return "비 주룩주룩"
    }
    if(weather == "snow"){
      return "눈 펑펑"
    }
  },
  deleteDiary() {
      const diaryId = this.$cookies.get('diaryId'); // 삭제할 게시물의 ID를 가져옵니다.
      console.log(diaryId);
      this.axios.delete(`/api/myinfo/${diaryId}`)
        .then(response => {
          console.log("게시물이 성공적으로 삭제되었습니다.");
          this.$router.push('/diary'); 
        })
        .catch(error => {
          console.error("오류가 발생하였습니다.");
        });
    },
    navigateToCalendar(){
      console.log("수정하기")
      
      this.$router.push('/editcarousel');
    }

},


mounted() {


  const queryString = this.$route.query.diaryId;
  // 쿼리스트링이 존재하는지 확인
  if (queryString) {
    // 쿼리스트링이 있다면 해당 값으로 Axios 요청 보내기
    this.axios.get(`/api/myinfo/DiaryImages/${queryString}`).then((res) => {
      this.slides = [];
      for (let a of res.data) {
        this.slides.push(a);
      }
      this.axios.get(`/api/myinfo/select/${queryString}`).then((res) => {
        this.diary = res.data;
        this.created_at = res.data.createdAt;
        this.title = res.data.title;
        this.content = res.data.content;
        this.mood = res.data.mood;
        this.weather = res.data.weather;
        this.name = res.data.name;
      }).catch(error => {
        console.error("데이터를 가져오는 도중 오류가 발생했습니다:", error);
      });
    }).catch(error => {
      console.error("데이터를 가져오는 도중 오류가 발생했습니다:", error);
    });
  } else {
    // 쿼리스트링이 없으면 쿠키에 저장된 값으로 Axios 요청 보내기
    const cookieId = this.$cookies.get('diaryId');
    this.axios.get(`/api/myinfo/DiaryImages/${cookieId}`).then((res) => {
      this.slides = [];
      for (let a of res.data) {
        this.slides.push(a);
      }
      this.axios.get(`/api/myinfo/select/${cookieId}`).then((res) => {
        this.diary = res.data;
        this.created_at = res.data.createdAt;
        this.title = res.data.title;
        this.content = res.data.content;
        this.mood = res.data.mood;
        this.weather = res.data.weather;
        this.name = res.data.name;
      }).catch(error => {
        console.error("데이터를 가져오는 도중 오류가 발생했습니다:", error);
      });
    }).catch(error => {
      console.error("데이터를 가져오는 도중 오류가 발생했습니다:", error);
    });
  }
}
}
</script>
<style scoped>
/* Import Google font - Poppins */
/* @import url('https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap'); */
@font-face {
font-family: 'Ownglyph_meetme-Rg';
src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_2402_1@1.0/Ownglyph_meetme-Rg.woff2') format('woff2');
font-weight: normal;
font-style: normal;
}
*{
margin: 0;
padding: 10px;
box-sizing: border-box;
font-family: 'Ownglyph_meetme-Rg';
}
body{
display: flex;
align-items: center;
justify-content: center;
min-height: 100vh;
/* padding: 10px; */
background:#fafafa;
}

.Diary {
  margin-top: 100px;
  margin-bottom: 100px;
  width: 1600px;
  display: flex;
}

.image-container {
  display: flex;
  flex-direction: column; /* 이미지와 아이콘을 세로로 정렬하기 위해 column으로 설정 */
  align-items: center; /* 수직 가운데 정렬 */
  position: relative; /* position 속성 추가 */
}

.catImage {
  width: 100%;
  max-width: 250px;
}

input {
margin-top: 0.4rem;

}
.wrapper input{
  border: 1px solid #ccc;
  border-radius: 5px;
  margin-bottom: 10px;
}
.label-text {
display: flex;
flex-direction: column;
}

div.wrapper{
  width: 50%;
}

.wrapper{
border-radius: 7px;
background: #fff;
padding: 30px 25px 40px;
box-shadow: 0 10px 15px rgba(0,0,0,0.05);
position: relative; /* wrapper를 상대적으로 위치시킵니다. */
width: 50%;
height: 70%;
}
.wrapper :where(textarea, input, select, button){
width: 100%;
outline: none;
border: none;
font-size: 17px;
border-radius: 5px;
}
.wrapper :where(textarea, input)::placeholder{
color: #aaa;
}
.wrapper :where(textarea, input):focus{
box-shadow: 0px 2px 4px rgba(0,0,0,0.08);
}
.wrapper textarea{
height: 270px;
resize: none;
padding: 8px 13px;
font-size: 17.6px;
border: 1px solid #ccc;
}
.wrapper .file-options{
display: flex;
margin-top: 10px;
align-items: center;
justify-content: space-between;
}

.file-options .option{
width: calc(100% / 2 - 8px);
}
.option label{
font-size: 17px;
}
.option :where(input, .select-menu){
height: 50px;
padding: 0 13px;
margin-top: 6px;
border-radius: 5px;
border: 1px solid #bfbfbf;
}
.option .select-menu select{
height: 50px;
background: none;
}
div.select-menu{
  margin: 2.4px 0px 8px;
}
.wrapper .save-btn{
color: #fff;
cursor: pointer;
opacity: 0.6;
padding: 16px 0;
margin-top: 20px;
pointer-events: none;
background: #8fe5f2;
font-size: 25px;
}
.save-btn:hover{
background: #29d8f4;
}
textarea:valid ~ .save-btn{
opacity: 1;
pointer-events: auto;
transition: all 0.3s ease;
}

.custom-file-upload {
  /* 파일 선택 버튼의 스타일 지정 */
  font-family: 'Ownglyph_meetme-Rg';
  display: flex;
  justify-content: center;
  align-items: center;
  /* padding: 10px 20px;
  margin-right: 10px; */
  border: none;
  border-radius: 5px;
  cursor: pointer;
  background-color: #edf6fc;
  color: #7ab7e0;
  font-size: 20px;
  width: 25%;
  height: 100%;
  border: 2px solid #7ab7e0;
  margin-top: 10px;
}
.image {
  display: none;
}

@media screen and (max-width: 475px) {
.wrapper{
  padding: 25px 18px 30px;
}
.wrapper :where(textarea, input, select, button, label){
  font-size: 16px!important;
}
.file-options .option{
  width: calc(100% / 2 - 5px);
}
.option :where(input, .select-menu){
  padding: 0 10px;
}
.wrapper .save-btn{
  padding: 15px 0;
}
}
.carousel {
  max-width: 45%; /* carousel의 최대 너비를 화면 너비에 맞게 조절합니다. */
  margin: 0 auto; /* 가운데 정렬을 위해 좌우 마진을 자동으로 설정합니다. */
  margin-top: 20px; /* 필요한 만큼의 간격으로 조정하세요 */
}

.carousel img {
  max-width: 100%; /* 이미지의 최대 너비를 부모 요소에 맞게 설정합니다. */
  max-height: 100%; /* 이미지의 최대 높이를 부모 요소에 맞게 설정합니다. */
  width: 800px; /* 이미지의 너비를 자동으로 조정합니다. */
  height: 600px; /* 이미지의 높이를 자동으로 조정합니다. */
  margin-top: 100px; /* 이미지 주변의 여백을 제거합니다. */
  padding: 0; /* 이미지 주변의 여백을 제거합니다. */
}
.pagination {
  margin-top: 20px; /* 필요한 만큼의 간격으로 조정하세요 */

}
.custom-file-upload {
    /* 파일 선택 버튼의 스타일 지정 */
    font-family: 'Ownglyph_meetme-Rg';
    display: flex;
    justify-content: center;
    align-items: center;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    background-color: #edf6fc;
    color: #7ab7e0;
    font-size: 20px;
    width: 150px; /* 너비 조정 */
    height: 50px; /* 높이 조정 */
    border: 2px solid #7ab7e0;
    margin-top: 10px;
}

.Diary {
  display: flex;
  flex-wrap: wrap; /* 버튼들이 넘칠 경우 자동으로 줄바꿈되도록 설정 */
  justify-content: space-between; /* 버튼들을 양쪽 끝에 정렬 */
}

.register-btn, .list-btn {
  flex: 1; /* 같은 크기로 버튼을 나란히 배치하기 위해 flex 속성 추가 */
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  background-color: #7ab7e0;
  color: white;
  font-family: 'Ownglyph_meetme-Rg';
  font-size: 20px;
  margin-top: 10px;
  margin-left: 5px; /* 일정한 간격을 위해 좌측 마진 추가 */
}

.list-btn {
  width: calc(50% - 5px); /* 부모 요소의 너비를 기준으로 버튼의 너비를 반으로 설정합니다. 간격도 고려하여 조정합니다. */
  margin-left: 5px; /* 일기삭제하기 버튼과의 간격을 설정합니다. */
}
</style>
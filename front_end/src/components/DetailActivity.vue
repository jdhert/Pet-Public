<template>
<div class="modal">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-top">
                <div class="modal-header">
                  <div class="modal-name">
                    <h1> {{ this.place.시설명 }}</h1>
                  </div>
                  <div class="bookmark">
                    <button @click="checking()" v-if="!checkedBookmark" title="북마크"><img src="../assets/images/bookmarks.png"></button>
                    <button @click="checking()" v-if="checkedBookmark" title="북마크"><img src="../assets/images/bookmarks-checked.png"></button>
                  </div>
                  <div class="close">
                    <button class="close_button" @click="$emit('closeModal')">닫기</button>
                  </div>
                </div>
                <div id="roadview"></div>
            </div>
    
            <div class="detail">
              <img :src="this.place.img != null ? this.place.img : 'https://www.shutterstock.com/image-vector/default-image-icon-vector-missing-260nw-2086941550.jpg'" onerror="this.onerror=null; this.src='https://www.shutterstock.com/image-vector/default-image-icon-vector-missing-260nw-2086941550.jpg'" alt="">
                <div class="info">
                <h5> 기본 장소 설명 : {{ this.place.basicInfoPlaceDescription }}</h5>
                <h5>주소 : {{ this.place.도로명주소 }}</h5>
                <h5> 운영시간 : {{  this.place.운영시간 }}</h5>
                <h5> 휴무일 : {{ this.place.휴무일 }}</h5>
                <hr>
                <h5> 장소(실내) 여부 : {{ this.place.indoorPlaceInfo }}</h5>
                <h5> 장소(실외) 여부 : {{ this.place.outdoorPlaceInfo }}</h5>
                <h5> 반려동물 동반 가능 여부 : {{ this.place.petCompanionInfo }}</h5>
                <h5> 반려동물 제한사항 : {{ this.place.petRestriction }}</h5>
                <h5> 반려동물 전용정보 : {{ this.place.petExclusiveInfo }}</h5>
                <h5> 입장가능 동물크기 : {{ this.place.petSizeAdmission }}</h5>
                <h5>  입장료 정보 : {{ this.place.admissionFeeInfo }}</h5>
                <h5> 애견동반 추가요금 : {{ this.place.additionalPetFee }}</h5>
                <hr>
                <h5> 주차가능여부 : {{ this.place.parkingAvailability }}</h5>
                </div>
            </div>
            <div class="category">
                <h5>카테고리 :  {{ this.place.카테고리3 }}</h5>
                <h5>전화번호 :  {{ this.place.전화번호 }}</h5>
                <hr>
                <h5><a :href="this.place.홈페이지 != '정보없음' ? this.place.홈페이지 : '/not_found'">홈페이지 바로가기</a></h5>
            </div>
        </div>
    </div>
</div>
</template>
<script>
export default {
    props: {
    place: Object,
    showModal: Boolean
  },
  data() {
    return {
      checkedBookmark: false,
    }
  },
  watch: {
    showModal(newValue) {
      if (newValue) {
        this.roadView();
      }
    }
  },
  methods: {
    checking() {
      if (!this.$cookies.isKey("id")) {
        alert("로그인 후 이용해주세요.")
        return 
      }
      this.axios.post(`/api/data/bookmarks`, {
        userId: this.$cookies.get("id"),
        placeId: this.place.id,
        checked: this.checkedBookmark,
      }).then((res) => {
        this.checkedBookmark = res.data
      })
    },

    roadView() {
      console.log(this.place);
      var rvContainer = document.getElementById('roadview');
      var rv = new kakao.maps.Roadview(rvContainer);
      var rc = new kakao.maps.RoadviewClient();
      var position = new kakao.maps.LatLng(this.place.위도, this.place.경도);

      rc.getNearestPanoId(position, 100, (panoId) => {
        if (panoId === null) {
          console.error('No panoId found near the given position.');
          return;
        }
        rv.setPanoId(panoId, position);
        this.initRoadviewMarker(rv, position);
      });
    },
    initRoadviewMarker(rv, position) {
      var rMarker = new kakao.maps.Marker({
        position: position,
        map: rv
      });

      var rLabel = new kakao.maps.InfoWindow({
        position: position,
        content: this.place.시설명
      });
      rLabel.open(rv, rMarker);

      kakao.maps.event.addListener(rv, 'init', () => {
        var projection = rv.getProjection();
        var viewpoint = projection.viewpointFromCoords(rMarker.getPosition(), rMarker.getAltitude());
        rv.setViewpoint(viewpoint);
      });
    },
  },
  mounted() {
    if (this.showModal) {
      this.roadView();
    }

    if (this.$cookies.isKey("id")) {
      this.axios.post(`/api/data/findBookmark`, {
        userId: this.$cookies.get("id"),
        placeId: this.place.id,
      }).then((res) => {
        this.checkedBookmark = res.data
      })
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
.custom-info-window {
  font-family:'Ownglyph_meetme-Rg';
}

.detail {
    margin-top: 15px;
    display: flex;
    flex-wrap: wrap;
    max-height: 350px;
}

.detail img{
    width: 375px;
    height: 340px;
    margin: 10px;
    margin-left: 30px;
}

.detail .info{
    margin-top: 5px;
    align-items: center;
    width: 450px;
    margin-left: 120px;
    text-align: left;
}

.category {
  margin-top: 8px;
  margin-left: 20px;
  align-self: flex-start; /* 자기 자신만 왼쪽에 밀착시킵니다. */
  width: 100%; /* 부모의 전체 너비를 사용합니다. */
}
.category h5 {
  text-align: left; /* 텍스트를 왼쪽 정렬합니다. */
  /* 각 항목이 자신의 줄을 차지하도록 합니다. */
  margin-left: 15px;
  align-self: left;
  justify-content: space-between;
  display: flex; 
}

.category h5 a {
  text-align: center; 
  align-self: left;
  /* text-decoration: underline; */
  border-radius: 5px;
  background-color: rgba(231, 231, 231, 0.486);
  width: 150px;
}

.category h5 a:hover {
  box-shadow: 0 8px 12px rgba(0, 0, 0, 0.2);
    transform: translateY(-5px);
}

.category h5 button {
  text-align: end; 
  align-self: flex-end;
  margin-right: 40px;
  border-radius: 10px;
}

.category hr {
  width: 40%;
  margin: 1%;
}

#roadview {
    width:100%;
    height:300px;
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
    z-index: 1000; 
}
.modal.show {
  display: flex !important; /* Important 추가 */
}
.modal > .modal-dialog {
    position: fixed; /* 변경 */
    margin: auto; /* 변경 */
    pointer-events: auto; /* 변경 */
    width: 80%;
    max-width: 1000px; /* 모달의 최대 너비를 지정합니다. */
    height: 95%;
    max-height: 1000px;
}
.modal-content {
    position: relative;
    display: flex;
    flex-direction: column;
    pointer-events: auto;
    background-color: #fff;
    background-clip: padding-box;
    /* border: 1px solid rgba(0, 0, 0, 0.2); */
    border-radius: 0.8rem;
    outline: 0;
    width: 100%; /* 변경 */
    height: 100%; /* 변경 */
    z-index: 1001;
}

.modal-content > img {
    width: 300px;
    height: 300px;
}

.modal-header {
  display: flex;
  justify-content: center;
}

.bookmark {
    position: absolute;
    left: 0; /* 부모 요소의 오른쪽에 정렬됩니다. */
}

.bookmark > button {
  background-color: #fff;
  border: #fff;
}
.close {
  position: absolute;
  right: 0;
  margin-right: 10px;
}
.close_button {
  border-radius: 10px;
}

@media screen and (min-width: 1440px) and (max-width: 2560px) {
  .detail {
    margin-top: 5px;
    display: flex;
    flex-wrap: wrap;
    max-height: 350px;
  }
  .detail .info{
    margin-top: 5px;
    align-items: center;
    width: 450px;
    margin-left: 120px;
    text-align: left;
  }
  
  .detail img{
    width: 350;
    height: 350px;
    margin: 10px;
    margin-left: 30px;
  } 
  .modal-content {
    width:100%; /* 변경 */
    height: 100%; /* 변경 */
  }
  .category {
    margin-top: 25px;
    margin-left: 20px;
    align-self: flex-start; /* 자기 자신만 왼쪽에 밀착시킵니다. */
    width: 100%; /* 부모의 전체 너비를 사용합니다. */
  }
  
}
@media screen and (min-width: 1024px) and (max-width: 1440px) {
  .detail {
    margin-top: 0px;
    display: flex;
    flex-wrap: wrap;
    max-height: 350px;
  }
  .detail .info{
    margin-top: 5px;
    align-items: center;
    width:255px;
    float: left;
    margin-left: 110px;
    text-align: left;
  }
  .detail img{
    width: 340px;
    height: 340px;
    margin: 10px;
    margin-left: 30px;
  } 
  .modal-content {
    width:100%; /* 변경 */
    height: 100%; /* 변경 */
  }
}
@media screen and (min-width: 768px) and (max-width: 1024px) {
  .detail {
    margin-top: 0px;
    display: flex;
    flex-wrap: wrap;
    max-height: 310px;
  }
  .detail .info{
    margin-top: 5px;
    align-items: center;
    width:255px;
    float: left;
    margin-left: 20px;
    text-align: left;
  }
  
  .detail img{
    width: 300px;
    height: 300px;
    margin: 10px;
    margin-left: 5px;
  } 
  .category {
    margin-top: 10px;
    margin-left: 0px;
    align-self: flex-start; /* 자기 자신만 왼쪽에 밀착시킵니다. */
    width: 100%; /* 부모의 전체 너비를 사용합니다. */
  }
}
@media (max-width: 768px) {
  .detail {
    margin-top: 0px;
    display: flex;
    flex-wrap: wrap;
    max-height: 250px;
}
  .detail .info{
    margin-top: 5px;
    align-items: center;
    width:255px;
    float: left;
    margin-left: 10px;
    text-align: left;
  }
  
  .detail img{
    width: 230px;
    height: 220px;
    margin: 10px;
    margin-left: 10px;
  }
  .category {
    margin-top: 8px;
    margin-left: 5px;
    align-self: flex-start; /* 자기 자신만 왼쪽에 밀착시킵니다. */
    width: 100%; /* 부모의 전체 너비를 사용합니다. */
  }
}
@media screen and (min-width: 540px) and (max-width: 690px){
  .detail {
    margin-top: 0px;
    display: flex;
    flex-wrap: wrap;
    max-height: 300px;
  }
  .detail .info{
    margin-top: 5px;
    align-items: center;
    width:200px;
    float: left;
    margin-left: 10px;
    text-align: left;
  }
  
  .detail img{
    width: 250px;
    height: 300px;
    margin: 10px;
    margin-left: 10px;
  }
  .category {
    margin-top: 20px;
    margin-left: 0px;
    align-self: flex-start; /* 자기 자신만 왼쪽에 밀착시킵니다. */
    width: 50%; /* 부모의 전체 너비를 사용합니다. */
  }
  .category h5 a {
    margin-top: 15px;
  }
  .modal-content {
    width:110%; /* 변경 */
    height: 120%; /* 변경 */
  }
  
}
  
@media screen and (min-width: 540px) and (max-width: 640px) {
  .detail {
    margin-top: 0px;
    display: flex;
    flex-wrap: wrap;
    max-height: 150px;
}
  .detail .info{
    margin-top: 5px;
    align-items: center;
    width:150px;
    float: left;
    margin-left: 20px;
    text-align: left;
  }
  
  .detail img{
    width: 180px;
    height: 120px;
    margin: 10px;
    margin-left: 20px;
  }
  .category {
    margin-top: 10px;
    margin-left: 0px;
    align-self: flex-start; /* 자기 자신만 왼쪽에 밀착시킵니다. */
    width: 50%; /* 부모의 전체 너비를 사용합니다. */
  }
  .category h5 a {
    margin-top: 15px;
  }
  .modal-content {
    width:100%; /* 변경 */
    height: 125%; /* 변경 */
  }
}
@media (max-width: 540px) {
  .detail {
    margin-top: 0px;
    display: flex;
    flex-wrap: wrap;
    max-height: 150px;
}
  .detail .info{
    margin-top: 5px;
    align-items: center;
    width:190px;
    float: left;
    margin-left: 10px;
    text-align: left;
  }
  
  .detail img{
    width: 100px;
    height: 120px;
    margin: 10px;
    margin-left: 10px;
  }
  .category {
    margin-top: 8px;
    margin-left: 5px;
    align-self: flex-start; /* 자기 자신만 왼쪽에 밀착시킵니다. */
    width: 25%; /* 부모의 전체 너비를 사용합니다. */
  }
  .category h5 a {
    margin-top: 15px;
  }
  .category h5 {
    margin-left: 0px;
    width: 80px;
  }
  .modal-content {
    width:100%; /* 변경 */
    height: 115%; /* 변경 */
  }
}

</style> 
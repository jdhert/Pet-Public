<template>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
  <body id="body1">
  <section id="banner">
      <div class="inner">
  			<h2>내 반려동물과 같이 갈 수 있는 곳</h2>
  			<p>반려동물과 함께하는 활동들을 손쉽게 찾아보아요</p>
  		</div>
  </section>

  <h1>{{ this.user }}님을 위한 추천 장소</h1>
  <div id="map"></div>
  <br>
  <div class="act_info">
    <header class="site-header">
      <h1>{{ this.user}}님 주위에 위치한 반려동물 액티비티 장소추천!!</h1>
      
    </header>
    <div class="recommend-carousel-container">
      <button @click="scrollRecoLeft" class="carousel-control left">&#60;</button>
      <div class="carousel-items" ref="recommendCarousel">
        <div v-for="product of this.products2" :key="product" class="product" @click.prevent="openModal(product)">
          <div class="product-image">
            <img :src="product.img" alt="준비중">
          </div>
          <div class="product-info">
            <h3>{{ product.시설명 }}</h3>
            <p class="rating">★★★★★ 5</p>
            <p class="price">{{ product.도로명주소 }}</p>
          </div>
        </div>
        </div>
      <button @click="scrollRecoRight" class="carousel-control right">&#62;</button>
    </div>
  </div>
  <br>

  <div id="app">
    <header class="site-header">
      <h1>{{ this.user}}님을 위한 반려동물 동반 장소 검색 상자</h1>
      <br>
      <div>
        <div class="search-bar" style="display: flex; align-items: center;">       
          <form @submit.prevent="searching">
              <input type="search" class="search-input"  placeholder="검색어를 입력할거냥" v-model="search">
              <input type="submit" class="search-button" value="검색">
          </form>
        </div>
        <br>
        <button class="region-btn" @click="selectRegion"> 지역 선택 </button>
        <div class="detail-region-btn" :class="{ 'show': showDetailRegion }">
          <div class="box">
            <div class="box-title">
              <p style="margin: 0; font-weight: bold; font-size: 1.2rem; color: black;">분류</p>
            </div>
            <div class="box-list">
              <ul class="box-list-ul">
                <li @click="highlightRegion" v-for="(Region, index) in Regions" :key="index" style="margin-top: 0.15rem; margin-bottom: 0.15rem; padding: 0;">
                  {{ Region }}
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </header>
    <br>
    <div class="category-items-carousel">
      <button @click="prevItem" class="category-carousel-button">&lt;</button>
      <div class="category-items" ref="categoryCarousel">
        <button v-for="(item, index) in visibleItems" :key="index" class="category-item" 
                :class="{ 'active': item.name === this.selectedCategory }">
          <h5 style="margin: 0;" @click.prevent="category(item.name)"> {{ item.name }} </h5>
        </button>
      </div>
      <button @click="nextItem" class="category-carousel-button"> &gt;</button>
    </div>

    <div class="carousel-container">
      <button @click="scrollLeft" class="carousel-control left">&#60;</button>
      <div class="carousel-items" ref="itemsCarousel">
        <div v-for="product in this.products" :key="product" class="product" @click.prevent="openModal(product)">
          <div class="product-image">
            <img :src="product.img" alt="준비중">
          </div>
          <div class="product-info">
            <h3>{{ product.시설명 }}</h3>
            <p class="rating">★★★★★ 5</p>
            <p class="price">{{ product.도로명주소 }}</p>
          </div>
        </div>
        <div id="loadingIndicator" v-show="mapLoading">
            <img src="../assets/images/loading_spinner.gif" alt="로딩 중..."/>
          </div>
        </div>
      <button @click="scrollRight" class="carousel-control right">&#62;</button>
    </div>
  </div>
  <div class="row mt-5">
    <div class="col text-center">
        <div class="block-27">
          <ul>
              <li><a href="#" @click="currentSwap(this.currentPage-1)">&lt;</a></li>
              <li><a  :class="{ 'active': n === this.currentPage }" href="#"  v-for="n in this.numbers" :key="n" @click="currentSwap(n)" style="margin: 5px;">{{ n }}</a></li>
              <li><a href="#" @click="currentSwap(this.currentPage+1)">&gt;</a></li>
          </ul>
        </div>
      </div>
  </div>
  <detailActivity v-if="showModal" :place="this.place" @closeModal="showModal = false" :showModal="this.showModal"/>
</body>
</template>

<script>
import detailActivity from './DetailActivity.vue';

export default {
  data(){
	return {
    currentIndex: 0,
    itemsPerPage: 5,
    activity : {},
		map : null,
		markers : [],
		latitude: 0,
		longitude : 0,
    user: "김아무개",
    items: [
        { name : "동물병원" },
        { name : "동물약국"},
        { name : "카페"},
        { name : "식당"},
        { name : "미용"},
        { name : "위탁관리"},
        { name : "반려동물용품"},
        { name : "여행지"},
        { name : "펜션"},
        { name : "호텔"},
        { name : "박물관"},
        { name : "미술관"},
        { name : "문예회관"}
    ],
      products: [  ],
      carouselResponsiveSettings: [
        {
          breakpoint: 1024,
          settings: {
            slidesToShow: 3,
            slidesToScroll: 3,
          }
        },
        {
          breakpoint: 600,
          settings: {
            slidesToShow: 2,
            slidesToScroll: 2,
          }
        },
        {
          breakpoint: 480,
          settings: {
            slidesToShow: 1,
            slidesToScroll: 1,
          }
        }
      ],
      imgList: [],
      mapLoading: true,
      currentPage: 1,
      maxPage: 1,
      paginationLimit: 5,
      numbers : [],
      markerList: [],
      showDetailRegion: false,
      Regions: ['전체', '강원도', '경기도', '경상남도', '경상북도', '광주광역시', '대구광역시', '대전광역시', '부산광역시', '서울특별시', '세종특별자치시', '울산광역시', '인천광역시', '전라남도', '전라북도', '제주특별자치도', '충청남도', '충청북도'],
      selectedRegion: null, 
      selectedCategory: "%", 
      city : "%",
      search : "",
      overlays: [],
      nearBy : [],
      imgSet : "",
      place : {},
      showModal : false,
      thumbNail : [],
      products2 : []
	  }
  },
  components:{
    detailActivity
  },
  props: {
    msg: String
  },
  computed: {
    visibleItems() {
      return this.items.slice(this.currentIndex, this.currentIndex + this.itemsPerPage);
    }
  },
  async mounted() {
    if(this.$cookies.isKey('name'))
      this.user = this.$cookies.get('name');
    try {
      if ("geolocation" in navigator) {
        const position = await new Promise((resolve, reject) => {
          navigator.geolocation.getCurrentPosition(resolve, reject, {timeout: 10000});
        });
        this.latitude = position.coords.latitude;
        this.longitude = position.coords.longitude;
        this.markerList.unshift([this.latitude, this.longitude]);
      } else {
        console.error("Geolocation is not supported by this browser.");
      }
      await this.surroundPlace(); 

      if (window.kakao && window.kakao.maps) {
        kakao.maps.load(() => {
          this.initMap();
        });
      } else {
        const script = document.createElement("script");
        script.onload = () => {
          kakao.maps.load(() => {
            this.initMap();
          });
        };
        script.src = "//dapi.kakao.com/v2/maps/sdk.js?appkey=c2a63c53b4bb9f45634c727367987e63&autoload=false";
        document.head.appendChild(script);
      }
  } catch (error) {
      console.error('An error occurred:', error);
      alert(error.message);
  }
  await this.getList();
},
 methods: {
    openModal(card){
      this.place = card;
      this.showModal = true;
    },
    async surroundPlace(){
      let i =0;
      const res = await this.axios.get(`/api/data/locate`, {
          params: {
            lat: this.latitude,
            lon: this.longitude
          }});
      for(let a of res.data) 
          this.markerList.push([a.위도, a.경도]);
      this.nearBy = res.data;
      for(let item of this.nearBy){
        const res = await this.axios.get(`/getimage?query=${item.시설명}}`,{
          headers: {
            Authorization : 'KakaoAK 1873813cac8513a7b412ff42dd4083de',
          }
        })

        if(res.data.documents[0].image_url){
          this.imgSet = res.data.documents[0].image_url;
          this.thumbNail.push(res.data.documents[0].thumbnail_url);
        }

        const isProductExist = this.products2.find(product => product.시설명 === item.시설명);
        
        if(isProductExist){
          if(res.data.documents[++i].image_url){
            this.imgSet = res.data.documents[i].image_url;
            this.thumbNail.push(res.data.documents[i].thumbnail_url);
          }else if(res.data.documents[i-1].image_url){
            this.imgSet = res.data.documents[i-1].image_url;
            this.thumbNail.push(res.data.documents[i-1].thumbnail_url);
          }
        } else i = 0;
        item.img = this.imgSet;
        this.products2.push(item);
      }
    },
    searching(){
      this.currentPage = 1;
      this.getList();
    },
    async category(name) {
      if(name === this.selectedCategory) 
        this.selectedCategory = "%"; 
      else
        this.selectedCategory = name; 
      this.currentPage = 1;
      await this.getList();
    },
    async getImage(List){
      let i = 0;
      for(let item of List){
        const res = await this.axios.get(`/getimage?query=${item.시설명}}`,{
          headers: {
            Authorization : 'KakaoAK 1873813cac8513a7b412ff42dd4083de',
          }
        })
        if(res.data.documents[0].image_url)
          this.imgSet = res.data.documents[0].image_url;

          const isProductExist = this.products.find(product => product.시설명 === item.시설명);
        
        if(isProductExist){
          if(res.data.documents[++i].image_url){
            this.imgSet = res.data.documents[i].image_url;
          }else if(res.data.documents[i-1].image_url){
            this.imgSet = res.data.documents[i-1].image_url;
          }
        } else i = 0; 
        item.img = this.imgSet;
        this.products.push(item);
      }
    },
    getPageNumbers() {
        this.numbers = [];
        let startPage = Math.max(1, Math.floor((this.currentPage - 1) / this.paginationLimit) * this.paginationLimit + 1);
        let endPage = Math.min(startPage + this.paginationLimit - 1, this.maxPage);
        for (let i = startPage; i <= endPage; i++)
            this.numbers.push(i);   
    },
    showLoadingIndicator(show) {
      this.mapLoading = show;
      document.getElementById("loadingIndicator").style.display = show ? "block" : "none";
    },
    currentSwap(n) {
        this.currentPage = Math.max(1, Math.min(n, this.maxPage));
        this.getList(this.currentPage);
    },
    async getList(){
      this.products = [];
      this.showLoadingIndicator(true);
      try {
        const res = await this.axios.get(`/api/data/${this.currentPage}`,{
          params:{
            category : this.selectedCategory,
            city : this.city,
            search : this.search,
          }
        });
        this.activity = res.data;
        this.maxPage = this.activity[0].maxPage;
        this.getPageNumbers();
        await this.getImage(this.activity);
        this.showLoadingIndicator(false); 
      } catch (error) {
        console.error(error);
      }
    },
  	initMap() {
      const container = document.getElementById("map");
      const options = {
        center: new kakao.maps.LatLng(33.450701, 126.570667),
        level: 1,
      };
      this.map = new kakao.maps.Map(container, options);
      this.displayMarker(this.markerList);
    },
    displayMarker(markerPositions) {
        
      if (this.markers.length > 0) {
        this.markers.forEach((marker) => marker.setMap(null));
      }

      this.markers = [];


      if (this.overlays) {
          this.overlays.forEach(overlay => overlay.setMap(null));
      }
      this.overlays = [];

      const positions = markerPositions.map(
        position => new kakao.maps.LatLng(...position)
      );

      positions.forEach((position, index) => {
        const marker = new kakao.maps.Marker({
            map: this.map,
            position,
        });

        
        if(index != 0){
        kakao.maps.event.addListener(marker, 'click', () => {
            let overlay = this.overlays[index];
            if(document.querySelector(`#overlay${index}`) && document.querySelector(`#overlay${index}`).style.display == 'none') {
                  document.querySelector(`#overlay${index}`).style.display  = '';
            }
            else if (overlay) {
                 overlay.setMap(overlay.getMap() ? null : this.map);
             } 
            else {
              let facilityInfo = this.nearBy[index-1];
              let content = `
                  <div class="wrap" id="overlay${index}">
                      <div class="info">
                          <div class="title">
                              ${facilityInfo.시설명}
                              <div class="close" title="닫기" onclick="this.parentNode.parentNode.parentNode.style.display = 'none';"></div>
                          </div>
                          <div class="body">
                            <div class="img">
                               <img src="${this.thumbNail[index-1]}" width="73" height="70">
                          </div>
                              <div class="desc">
                                <div class="ellipsis">${facilityInfo.도로명주소}</div> 
                                <div class="jibun ellipsis">(우) ${facilityInfo.우편번호} (지번) ${facilityInfo.번지}</div> 
                                <div><a href=${facilityInfo.홈페이지} target="_blank" class="link">홈페이지</a></div>
                                </div>
                          </div>
                      </div>
                  </div>
              `;

              let overlay = new kakao.maps.CustomOverlay({
                  content: content,
                  position: marker.getPosition(),
                  map: this.map,
              });

              this.overlays[index] = overlay;
          }
        });
      }
      else this.markers.push(marker);
    });

        var content = '<div class="customoverlay">' +
        '  <a  @click.prevent target="_blank">' +
        '    <span class="title">현재 위치</span>' +
        '  </a>' +
        '</div>';
      
  	    var position1 = positions[0]; 

  	    var customOverlay = new kakao.maps.CustomOverlay({
        	map: this.map,
        	position: position1,
        	content: content,
        	xAnchor: 0.5, 
          yAnchor: 1.1,
  	    });

        if (positions.length > 0) {
        const bounds = positions.reduce(
            (bounds, latlng) => bounds.extend(latlng),
            new kakao.maps.LatLngBounds()
        );
          this.map.setBounds(bounds);
        }
    },
    closeOverlay() {
      if(this.overlay) {
          this.overlay.setMap(null);     
      }
    },
    scrollRecoRight() {
      this.scrollRecoCarousel(1);
    },
    scrollRecoLeft() {
      this.scrollRecoCarousel(-1);
    },
    scrollRecoCarousel(direction) {
      const carousel = this.$refs.recommendCarousel;
      const scrollAmount1 = carousel.offsetWidth / 5; 
      carousel.scrollBy({ left: direction * scrollAmount1, behavior: 'smooth' });
     },
    nextItem() {
      if (this.currentIndex < this.items.length - this.itemsPerPage) {
        this.currentIndex++;
      }
    },
    prevItem() {
      if (this.currentIndex > 0) {
        this.currentIndex--;
      }
    },
    scrollLeft() {
      this.scrollCarousel(-1);
    },
    scrollRight() {
      this.scrollCarousel(1);
    },
    scrollCarousel(direction) {
      const carousel = this.$refs.itemsCarousel;
      const scrollAmount2 = carousel.offsetWidth / 5; // Width of one item
      carousel.scrollBy({ left: direction * scrollAmount2, behavior: 'smooth' });
    },
    async fetchPhoto(photoReference) {
      const cachedUrl = localStorage.getItem(photoReference);
      if (cachedUrl) {
         return cachedUrl;
       }
       const photoRes = await this.axios.get(`/googleimg?maxwidth=400&photo_reference=${photoReference}&key=AIzaSyBUH1_H3djDNJeVGuUEwNlrc-fVOw_RKCs`, { responseType: 'blob' });
       const imgUrl = URL.createObjectURL(photoRes.data);
       localStorage.setItem(photoReference, imgUrl);
       return imgUrl;
    },
    selectRegion() {
      this.showDetailRegion = !this.showDetailRegion;
    },
    highlightRegion(event) {
      if(event.target == this.selectedItem){
        this.selectedItem.style.backgroundColor = '';
        this.city = "%";
        return;
      }
      if (this.selectedItem) {
        this.selectedItem.style.backgroundColor = '';
      }
      event.target.style.backgroundColor = 'lightblue';
      this.selectedItem = event.target;
      if(this.selectedItem.textContent == "전체")
        this.city = "%";
      else this.city = this.selectedItem.textContent;
    },
    closeModal(){
      this.showModal = false;
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

.site-header {
  background-color: #f8f9fa; /* 배경색 */
  padding: 20px 40px; /* 상하 좌우 패딩 */
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1); /* 상단에 그림자 효과 */
}
.site-header h1 {
  color: #343a40; /* 제목 색상 */
}

.search-bar {
  display: flex;
  align-items: center;
  justify-content: space-between; /* 입력 요소와 버튼을 동시에 감싸는 컨테이너를 좌우로 분배 */
  width: 30vw; /* 원하는 너비로 조정하세요 */
  margin: 0 auto; /* 가운데 정렬 */
  border: 3px solid #4ea3ff; /* 테두리 추가 */
  border-radius: 50px; /* 테두리의 모양을 더 둥글게 만들기 위해 추가 */
  padding: 5px; /* 내부 여백 추가 */
  height: 6vh;
}
.search-input {
  text-align: center;  
  flex: 1; /* 나머지 공간을 모두 차지 */
  width: calc(30vw - 105px); /* 검색어 입력란 너비를 동적으로 조정합니다 */
  background: none;
  font-size: 20px;
  border-radius: 60px;
  border: none; /* 기본 테두리 제거 */
  padding: 5px; /* 내부 여백 추가 */
  outline: none; /* 포커스 효과 제거 */
}
.search-button {
  color: #ffffff;
  border: none;
  background-color: #8d8d8d;
  font-size: 1rem;
  border-radius: 80px;
  cursor: pointer;
  outline: none;
}
.search-button:hover {
background-color: #4ea3ff;
}
form{
    margin: 0px;
}
@media (min-width: 768px) {
    .card-columns {
        column-count: 3;
        column-width: 80%;
    }
}
.detail-region-btn {
  display: none;
}
.detail-region-btn.show {
  border: 2px solid black; /* 검정 선(border) 스타일 적용 */
  width: 40%;
  display: flex;
  justify-content: center;
  margin: 0 auto; 
}
.box {
  display: flex; /* flex로 배치 */
  flex-direction: column;
  width: 100%;
  padding: 0;
  margin: 0;
}
.box-list {
  display: flex; /* flex로 배치 */
  justify-content: space-between;
}
.box-list > .box-list-ul {
  list-style: none;
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  padding: 0;
}
.box-list > .box-list-ul > li {
  width: 50%; /* 각 li 요소의 너비를 조정합니다. */
  margin-bottom: 10px; /* 각 li 요소 사이의 간격을 조정합니다. */
  height: 3vh;
}

.recommend-carousel-container {
  display: flex;
  align-items: center;
  margin: auto;
  position: relative;
  overflow: hidden;
  width: 95%;
}

.carousel {
  display: flex;
  justify-content: center;
  align-items: center;
}

.product-grid {
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
}

.rating {
  color: #ffa500; 
}

.price {
  color: #333;
}

#body1 #banner{
  height: 400px; 
  margin-bottom: 100px;
  background-image: url('../assets/images/activity_banner.jpg');
}

#map{
  width: 70vw; 
  height: 60vh; 
  margin: auto;
  border: solid;
  border-radius: 5%;
}

.category-items-carousel {
  display: flex;
  align-items: center;
  justify-content: center; /* 수평 가운데 정렬을 위한 속성 추가 */
  margin: 0 auto; /* 수평 가운데 정렬을 위한 margin 속성 추가 */
  position: relative;
}
.category-items {
  display: flex;
}
.category-item {
  width: 12vw; /* Width of each item, adjust as needed */
  border: 1px dashed #ccc;
  padding: 0.7%;
  margin: 2%;
  border-radius: 0.8rem;
}
.category-item:hover {
  color: white;
  background-color: #4ea3ff;
}
.category-item.active {
  color: white;
  background-color: #4ea3ff;
}
.category-carousel-button {
  background-color: rgba(0,0,0,0.5);
  color: #fff;
  border: none;
}

.carousel-container {
  display: flex;
  align-items: center;
  margin: auto;
  position: relative;
  overflow: hidden;
  width: 95%;
}
.carousel-items {
  display: flex;
  flex-wrap: nowrap; /* Prevent wrapping */
  overflow-x: hidden; /* Hide horizontal scrollbar */
  scroll-behavior: smooth;
  transition: transform 0.8s ease;
  min-height: 40vh;
  width: 90%;
  margin: 0 auto; /* 가운데 정렬을 위한 margin 속성 추가 */
  cursor: pointer;
}
.product {
  min-width: calc(20% - 20px); /* 캐러셀 아이템의 최소 너비 설정 */
  max-width: calc(20% - 20px); /* 캐러셀 아이템의 최대 너비 설정 */
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); /* 그림자 추가 */
  margin: 10px; /* 마진 설정 */
  overflow: hidden; /* 오버플로우 숨기기 */
  border-radius: 8px; /* 모서리를 둥글게 */
  object-fit: contain; /* 이미지가 비율을 유지하면서 컨테이너에 맞도록 설정 */
}
.product:hover {
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.2); /* 호버 시 그림자 효과 */
  transform: translateY(-5px); /* 호버 시 약간 위로 이동 */
}
.product-image > img {
  width: 100%; /* 이미지의 너비를 부모 요소에 맞게 100%로 설정 */
  height: auto; /* 높이를 자동으로 조정하여 이미지 비율을 유지 */
  max-height: 20vh; /* 최대 높이를 viewport 높이의 10%로 제한 */
  background-color: #ddd; /* Placeholder color */
  margin-bottom: 10px;
  display: block;
}
.product-info {
  padding: 15px;
  background-color: #fff;
}

.carousel-control {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background-color: rgba(0,0,0,0.5);
  color: #fff;
  border: none;
  cursor: pointer;
  padding: 10px 15px;
  font-size: 18px;
  user-select: none;
}
.carousel-control.left {
  left: 10px;
}
.carousel-control.right {
  right: 10px;
}

#loadingIndicator {
  position: absolute; /* 절대 위치 */
  top: 50%; /* 부모 요소의 세로 중앙 */
  height: 400px;
  left: 50%; /* 부모 요소의 가로 중앙 */
  transform: translate(-50%, -50%); /* 요소의 중심을 기준으로 가운데 정렬 */
}
#loadingIndicator > img {
  height: 35vh;
}

/* Add some more specific selectors for the rating and price if needed */
.product .rating,
.product .price {
  margin: 5px 0; /* Vertical spacing */
}

/*페이지네이션*/
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
  text-decoration: none;
}
.block-27 ul li a.active,
.block-27 ul li a.active span {
  background: #007bff;
  color: #fff;
  border: 1px solid transparent;
}

@media (max-width: 768px) {
  div.product-info > h3 {
    font-size: calc(1.3rem - .4vw);
  }
}

:deep(.customoverlay) {position:relative;bottom:85px;border-radius:6px;border: 1px solid #ccc;border-bottom:2px solid #ddd;float:left;}
:deep(.customoverlay:nth-of-type(n)) {border:0; box-shadow:0px 1px 2px #888;}
:deep(.customoverlay a) {display:block;text-decoration:none;color:#000;text-align:center;border-radius:6px;font-size:14px;font-weight:bold;overflow:hidden;background: #d95050;background: #d95050 url(https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/arrow_white.png) no-repeat right 14px center;}
:deep(.customoverlay .title) {display:block;text-align:center;background:#fff;margin-right:35px;padding:10px 15px;font-size:14px;font-weight:bold;}
:deep(.customoverlay:after) {content:'';position:absolute;margin-left:-12px;left:50%;bottom:-12px;width:22px;height:12px;background:url('https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/vertex_white.png')}

/* :deep .customoverlay.close {position: absolute;top: 10px;right: 10px;color: #888;width: 17px;height: 17px;background: url('https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/overlay_close.png');}
:deep .customoverlay.close:hover {cursor: pointer;} */


:deep(.wrap) {position: absolute;left: 0;bottom: 40px;width: 288px;height: 132px;margin-left: -144px;text-align: left;overflow: hidden;font-size: 12px;font-family: 'Malgun Gothic', dotum, '돋움', sans-serif;line-height: 1.5;}
:deep(.wrap *) {padding: 0;margin: 0;}
:deep(.wrap .info) {width: 286px;height: 120px;border-radius: 5px;border-bottom: 2px solid #ccc;border-right: 1px solid #ccc;overflow: hidden;background: #fff;}
:deep(.wrap .info:nth-child(1)) {border: 0;box-shadow: 0px 1px 2px #888;}
:deep(.info .title) {padding: 5px 0 0 10px;height: 40px;background: #eee;border-bottom: 1px solid #ddd;font-size: 18px;font-weight: bold;}
:deep(.info .close) {position: absolute;top: 10px;right: 10px;color: #888;width: 17px;height: 17px;background: url('https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/overlay_close.png');}
:deep(.info .close:hover) {cursor: pointer;}
:deep(.info .body) {position: relative;overflow: hidden;}
:deep(.info .desc) {position: relative;margin: 13px 0 0 90px;height: 75px;}
:deep(.desc .ellipsis) {overflow: hidden;text-overflow: ellipsis;white-space: nowrap;}
:deep(.desc .jibun) {font-size: 11px;color: #888;margin-top: -2px;}
:deep(.info .img) {position: absolute;top: 6px;left: 5px;width: 73px;height: 71px;border: 1px solid #ddd;color: #888;overflow: hidden;}
:deep(.info:after) {content: '';position: absolute;margin-left: -12px;left: 50%;bottom: 0;width: 22px;height: 12px;background: url('https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/vertex_white.png')}
:deep(.info .link) {color: #5085BB;}
</style>

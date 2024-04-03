<template>
  <section>
    <div class="main">
      <div class="content">
        <header class="banner">
          <h1 class="banner-title">
            <span>C A L E N D A R</span>
          </h1>
          <img src="../assets/images/배너5.png" alt="Banner" class="banner-image">
          <br>
        </header>
        <div class="row mt-1" id="filter-buttons">
          <div class="col-12">
            <button class="btn mb-2 me-1 active" data-filter="all" onclick="location.href='/diary'"><img src="../assets/images/gallery.png" alt=""> 갤러리로 보기</button>
            <button class="btn mb-2 mx-1" data-filter="nature" onclick="location.href='/calendar'"><img src="../assets/images/calendar1.png" alt=""> 캘린더로 보기</button>
          </div>
        </div>
        <div class="change-buttons">
            <div class="changeBtn">
              <button class="changeColor" @click="showColorPicker"><img src="../assets/images/paint.png" alt=""> 일지 색상 변경</button>
            </div>
            <div v-if="showPicker" class="color-picker-container">
              <select class="petName" v-model="selectedPet" @change="updateColor">
                <option :value="pet.id" v-for="pet of petList" :key="pet" class="PetOption">{{ pet.name }}</option>
              </select>
              <!-- RGB 색상표를 표시할 input 요소 추가 -->
              <input type="color" v-model="selectedColor">
              <!-- Submit 버튼 추가 -->
              <button class="colorbutton" @click="submitColor">확인</button>
            </div>
        </div>
        <div>
          <FullCalendar :options='calendarOptions' />
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'

export default {
  components: {
    FullCalendar,
  },
  data() {
    return {
      calendarOptions: {
        plugins: [dayGridPlugin],
        initialView: 'dayGridMonth',
        editable: true, 
        eventLimit: true,
        weekends: true,
        scrollTime: '00:00:00',
        events: [],
        locale: "ko",
      },
      showPicker: false,
      selectedPet: null,
      selectedColor: null, // 추가: 선택된 색상을 저장할 변수
    };
  },
  mounted() {

  if (!this.$cookies.get("id")) {
	    alert("로그인이 필요합니다.");
	    this.$router.push('/login');
	    return;
	}
  this.axios.get(`/api/myinfo/calendar/${this.$cookies.get("id")}`)
    .then((res) => {
      const petIdColorMap = {};
      const existingColors = []; // 기존 색상 목록
   
      this.petList = [];
      this.selectedPet = res.data[0].petId;
      res.data.forEach(item => {
        const pet = {
          name: item.name,
          id: item.petId
        };
        // 이미 같은 이름의 펫이 리스트에 있는지 확인
        const existingPet = this.petList.find(p => p.name === pet.name);
        if (!existingPet) {
          this.petList.push(pet);
        }
      });

      res.data.forEach(item => {
        if (item.petColor) {
          petIdColorMap[item.petId] = item.petColor;
          existingColors.push(item.petColor); // 기존 색상 목록에 추가
        } else {
          const petColor = this.getRandomColor(existingColors);
          petIdColorMap[item.petId] = petColor;
          existingColors.push(petColor); // 새로운 색상을 목록에 추가
          this.updateColorToServer(item.petId, petColor);
        }
      });


      // FullCalendar의 이벤트에 petColor를 적용하여 events 배열에 추가
      res.data.forEach(item => {
        this.calendarOptions.events.push({
          title: item.name,
          start: item.createdAt.split('T')[0],
          backgroundColor: petIdColorMap[item.petId],
          textColor: "#000000",
          diaryId: item.diaryId,
          borderColor: "#ffff",
          url: `http://localhost:3000/carousel?diaryId=${item.diaryId}`
        });
      });
    })
    
    .catch(error => {
      console.error("데이터를 가져오는 도중 오류가 발생했습니다:", error);
    });

},
  methods: {
    // 랜덤 색상 생성 함수
    getRandomColor(existingColors) {
      const pastelColors = [
        "#FFA07A", "#FFB6C1", "#FFC0CB", "#FFD700", "#FFDAB9",
        "#FFDEAD", "#FFE4B5", "#FFE4C4", "#FFE4E1", "#FFEBCD",
        "#FFEFD5", "#FFF0F5", "#FFF5EE", "#FFF8DC", "#FFFACD",
        "#FFFAF0", "#FFFAFA", "#FFFF00", "#FFFFE0", "#FFFFF0",
        "#B0E0E6", "#AFEEEE", "#ADD8E6", "#87CEEB", "#87CEFA",
        "#87CEFA", "#AFEEEE", "#B0E0E6", "#98FB98", "#90EE90"
      ];

      // 중복되지 않는 랜덤 색상을 선택
      let randomColor;
      do {
        randomColor = pastelColors[Math.floor(Math.random() * pastelColors.length)];
      } while (existingColors.includes(randomColor)); // 기존 색상 목록에 있는지 확인

      return randomColor;
      },
    // 색상을 서버로 업데이트하는 함수
    updateColorToServer(petId, petColor) {
      this.axios.post(`/api/myinfo/updateColor/${petId}`, { petColor })
        .then(response => {
          console.log(petId, petColor) // 수정: selectedColor 사용
          console.log("색상이 성공적으로 업데이트되었습니다.");
        })
        .catch(error => {
          console.error("색상 업데이트 도중 오류가 발생했습니다:", error);
        });
    },
    showColorPicker() {
      this.showPicker = !this.showPicker;
    },
   submitColor() {
    // 선택한 펫과 색상을 가져옴
    const petId = this.selectedPet;
    const petColor = this.selectedColor; // 수정: selectedColor 변수 사용
    if (!petId || !petColor) {
      // 펫과 색상이 선택되지 않은 경우 에러 처리 혹은 알림을 할 수 있습니다.
      alert("펫과 색상을 모두 선택해주세요.");
      return;
    }
    

    // 서버로 petId와 petColor를 보냄
    this.axios.post(`/api/myinfo/updateColor/${petId}`, { petColor: petColor })
      .then(response => {
        console.log(petId, petColor);
        console.log("색상이 성공적으로 업데이트되었습니다.");

        // FullCalendar 이벤트를 업데이트하기 위해 서버에서 새로운 데이터를 가져옴
        this.axios.get(`/api/myinfo/calendar/${this.$cookies.get("id")}`)
          .then((res) => {
            const petIdColorMap = {};
            const existingColors = []; // 기존 색상 목록

            this.petList = [];
            this.selectedPet = res.data[0].petId;
            res.data.forEach(item => {
              const pet = {
                name: item.name,
                id: item.petId
              };
              // 이미 같은 이름의 펫이 리스트에 있는지 확인
              const existingPet = this.petList.find(p => p.name === pet.name);
              if (!existingPet) {
                this.petList.push(pet);
              }
            });

            this.calendarOptions.events = []; // 기존 이벤트 초기화

            res.data.forEach(item => {
              if (item.petColor) {
                petIdColorMap[item.petId] = item.petColor;
                existingColors.push(item.petColor); // 기존 색상 목록에 추가
              } else {
                const petColor = this.getRandomColor(existingColors);
                petIdColorMap[item.petId] = petColor;
                existingColors.push(petColor); // 새로운 색상을 목록에 추가
                this.updateColorToServer(item.petId, petColor);
              }
            });

            // FullCalendar의 이벤트에 petColor를 적용하여 events 배열에 추가
            res.data.forEach(item => {
              this.calendarOptions.events.push({
                title: item.name,
                start: item.createdAt.split('T')[0],
                backgroundColor: petIdColorMap[item.petId],
                textColor: "#000000",
                diaryId: item.diaryId,
                url: `http://localhost:3000/carousel?diaryId=${item.diaryId}`
              });
            });
          })
          .catch(error => {
            console.error("데이터를 가져오는 도중 오류가 발생했습니다:", error);
          });

        this.showPicker = !this.showPicker; // 색상 선택기를 닫음
      })
      .catch(error => {
        console.error("색상 업데이트 도중 오류가 발생했습니다:", error);
      });
},
    
    refreshCalendar() {

     this.axios.get(`/api/myinfo/calendar/${this.$cookies.get("id")}`)
    .then((res) => {
      const petIdColorMap = {};
      const existingColors = []; // 기존 색상 목록
   
      this.petList = [];
      this.selectedPet = res.data[0].petId;
      res.data.forEach(item => {
        const pet = {
          name: item.name,
          id: item.petId
        };
        // 이미 같은 이름의 펫이 리스트에 있는지 확인
        const existingPet = this.petList.find(p => p.name === pet.name);
        if (!existingPet) {
          this.petList.push(pet);
        }
      });
      this.calendarOptions.events = [];
      
      res.data.forEach(item => {
        if (item.petColor) {
          petIdColorMap[item.petId] = item.petColor;
          existingColors.push(item.petColor); // 기존 색상 목록에 추가
        } else {
          const petColor = this.getRandomColor(existingColors);
          petIdColorMap[item.petId] = petColor;
          existingColors.push(petColor); // 새로운 색상을 목록에 추가
          this.updateColorToServer(item.petId, petColor);
        }
      });
      
      // FullCalendar의 이벤트에 petColor를 적용하여 events 배열에 추가
      res.data.forEach(item => {
        this.calendarOptions.events.push({
          title: item.name,
          start: item.createdAt.split('T')[0],
          backgroundColor: petIdColorMap[item.petId],
          textColor: "#000000",
          diaryId: item.diaryId,
          url: `http://localhost:3000/carousel?diaryId=${item.diaryId}`
        });
      });
    })
    
    .catch(error => {
      console.error("데이터를 가져오는 도중 오류가 발생했습니다:", error);
    });

    this.showPicker = !this.showPicker; 
    },

  
  }
};
</script>

<style scoped>
@font-face {
    font-family: 'Ownglyph_meetme-Rg';
    src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_2402_1@1.0/Ownglyph_meetme-Rg.woff2') format('woff2');
    font-weight: normal;
    font-style: normal;
}

*{
  font-family: 'Ownglyph_meetme-Rg';
}

.content {
  width: 100%;
}
.fc{
  margin: 0 auto;
  width:60%;
  height: auto;
  margin-bottom: 200px;
}
.fc-scroller{
  margin: 0%;
  scrollbar-width: none; /* 스크롤바 숨기기 */
  -ms-overflow-style: none; /* IE 및 Edge용 스크롤바 숨기기 */
}

.banner {
  margin-top: 120px;
  text-align: center;
}

.banner-title {
  font-family: 'Ownglyph_meetme-Rg';
  font-size: 3.3rem;
}

.banner-subtitle {
  font-family: 'Ownglyph_meetme-Rg';
  font-size: 1.3rem;
  color: #777;
}
.banner-image {
  width: 25%;
}
.banner_2 {
  margin-top: 50px;
}
.btn img {
  max-width: 20px;
  max-height: 50px;
}
#filter-buttons{
  margin-top: 40px;
  margin-bottom: 20px;
}
#filter-buttons button {
  border-radius: 5px;
  background: #fff;
  border-color: transparent;
  font-family: 'Ownglyph_meetme-Rg';
  width: 120px;
  height: 25px;
  font-size: 15px;
}
#filter-buttons button.active:hover {
  background: #ddd;
}
#filter-buttons button.active {
  color: #000000;
  background-color: #fff;
}
#filter-buttons button{
  color: #fff;
  background: #74b1e7;
  margin: 0px;
  padding:0px;
}
.col-12{
  margin-left: 19%;
}
.fc-scroller-harness{
  overflow: hidden !important;
}

#filter-buttons button {
  cursor: pointer; /* 손가락 모양의 커서로 설정 */
}

#filter-buttons {
  margin-top: 30px; /* 원하는 값으로 조정 */
  margin-bottom: 40px; /* 원하는 값으로 조정 */
}
.color-picker-container {
  display: flex;
  align-items: center;
  justify-content: end;
  margin-right: 20%;
}


.color-picker {
  display: flex;
  align-items: center;
}

.color-picker input {
  margin-right: 10px;
  margin-top: 5px;
}

.color-picker select {
  width: 150px;
  padding: 5px;
  border-radius: 5px;
}

.colorbutton {
  margin-left: 10px;
}
.color-picker-container select {
  width: 150px;
  padding: 5px;
  border-radius: 5px;
}
.changeColor{
  margin-right: 0%;
  /* margin-bottom: 20px; */
}
.petName{
  width: 20px;
  margin-right: 5px;
  margin-top: 5px;
  margin-bottom: 10px;
}

.PetOption{
  width: 30px;
}
input{
  margin-top: 10px;
}

.change-buttons{
  display: flex;
  flex-direction: column;
}
.changeBtn { 
  display: flex;
  justify-content: end;
  margin-right: 20%;
  margin-bottom: 5px;
}


</style>

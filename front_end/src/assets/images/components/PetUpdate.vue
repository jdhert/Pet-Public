<template>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
  <div class="card">
    <form @submit.prevent="petUpdate">
    <div class="top-container">
      <img src="../assets/images/edit_pet.png" alt="Logo" class="petUpdateLogo">
      <h1>반려동물 정보수정</h1>
    </div>
      <div class="image_container">
        <div class="file-upload-buttons">
          <input type="file" id="image" accept="image/*" @change="setThumbnail($event)" style="display: none;"/>
        </div>
        <img :src="thumbnail || defaultImage" alt="Thumbnail" class="thumbnail" @click="openFileInput" />
      </div>
      <div class="input_container">
        <div class="addPetName mb-3">
          <label class="m-2">이름</label>
          <input type="text" placeholder="이름을 수정하시겠습니까?" v-model="pet.name"/>
        </div>
        <div class="addPetAge mb-3">
          <label class="m-2">나이</label>
          <select v-model="pet.age" class="form-select" required>
            <option value="" disabled>나이를 선택해주세요.</option>
            <option v-for="age in ages" :key="age" :value="age">{{ age }}세</option>
          </select>
        </div>
        <div class="addPetWeight mb-3">
          <label class="m-2">몸무게</label>
          <div class="input-group">    
            <input type="text" class="form-control" aria-label="Amount (to the nearest dollar)" placeholder="몸무게(숫자)을 입력해주세요." v-model="pet.weight" pattern="\d+(\.\d{1,2})?" >
            <span class="input-group-text">kg</span>
          </div>
        </div>
        <div class="category-section mb-3">
          <label class="m-2">종류</label>
          <input type="text" placeholder="자세한 품종을 입력해주세요." v-model="pet.specSpecies"/>
        </div>
        <div class="addPetDisease mb-3">
          <label class="m-2">질병 여부</label>
          <div class="form-check">
            <input class="form-check-input" type="checkbox" value="" id="petDisease" v-model="pet.disease">
            <label class="form-check-label" for="petDisease">현재 질병을 앓고 있거나 최근에 질병으로 병원 진료를 받은 적이 있다.</label>
          </div>
        </div>
        <div class="addPetRecog_chip mb-3">
          <label class="m-2">동물정보등록</label>
          <div class="form-check">
            <input class="form-check-input" type="checkbox" value="" id="recog_chip" v-model="pet.recogChip">
            <label class="form-check-label" for="recog_chip">동물정보등록 시스템에 등록이 되어 있다.</label>
          </div>
        </div>
      </div>        
      <div class="submit-button-container">
        <button type="submit">반려동물 정보수정</button>
      </div>
    </form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      petId: this.$route.query.petId,
      pet : [],
      thumbnail: '',
      fileList : '',
      imgPath: "",
      defaultImage: require('../assets/images/plus.png'),
      ages: Array.from({ length: 99 }, (_, index) => index + 1) // 1부터 99까지의 숫자 배열 생성
    };
  },

  methods: {
    // 'image' 클릭 이벤트 시 fileInput 작동
    openFileInput() {
     const fileInput = document.getElementById('image');
     fileInput.click();
    },

    // 썸네일 출력 
    setThumbnail(event) {
      if (event.target && event.target.files && event.target.files.length > 0) {
        const files = event.target.files;
        this.imageUploaded = [];
        this.fileList = files;
        this.fileList = Array.from(event.target.files);
        console.log(this.fileList);

        const reader = new FileReader();

        reader.onload = (event) => {
            this.thumbnail = event.target.result; // Set the thumbnail URL
        };

        // 이미지를 읽어올 때 사용자가 올린 이미지 파일을 가져옵니다.
        this.image = this.fileList[0];

        reader.readAsDataURL(this.image);

        reader.onloadend = () => {
            this.pet.petimg = reader.result; // this를 사용하여 pet 객체에 접근합니다.
        };
        } else {
            console.error("Event target or files are undefined or empty.");
        }
    },

    petUpdate(){
      if (this.pet.disease) {
        this.pet.disease = 1;
      }
      if (this.pet.recog_chip) {
        this.pet.recog_chip = 1;
      }

      if (this.fileList && this.fileList.length > 0) {
            let formData = new FormData();
            formData.append('image', this.fileList[0]);
            
            this.axios.post(`/api/free/img`, formData, {
              headers: {
                'Content-Type': 'multipart/form-data'
              }
            }).then((res) => {
              for(let s of res.data)
                this.imgPath = s;

                this.axios.put(`/api/pet`, {
                  petId: this.petId,
                  petImg : this.imgPath,
                  petName : this.pet.name, 
                  petAge : this.pet.age,
                  petWeight :  parseFloat(this.pet.weight),
                  spec_species : this.pet.specSpecies,
                  petDisease : !!this.pet.disease,
                  petRecog_chip : !!this.pet.recogChip,
              }).then(this.$router.push({ path: '/petdetail', query: { petId: this.petId } })).catch();
            }).catch();
          } else {
            // 파일이 첨부되지 않았을 경우의 처리
            this.axios.put(`/api/pet`, {
              petId: this.petId,
              petName : this.pet.name, 
              petAge : this.pet.age,
              petWeight :  parseFloat(this.pet.weight),
              spec_species : this.pet.specSpecies,
              petDisease : !!this.pet.disease,
              petRecog_chip : !!this.pet.recogChip,
            }).then(this.$router.push({ path: '/petdetail', query: { petId: this.petId } })).catch();
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
            this.thumbnail = res.data;

            console.log(this.pet.img)

        });
      }).catch();

		  // this.axios.get(`/api/myinfo/diary/${this.$cookies.get("id")}`).then((res)=> {
  		// 	this.posts = res.data;
		  // }).catch();
	  },
};

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

.card {
  border: 1px solid #e0e0e0;
  border-radius: 20px;
  padding: 20px;
  max-width: 1000px;
  max-height: 1500px;
  margin: auto;
  margin-top: 120px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.petUpdateLogo {
  max-width: 200px;
  height: auto;
}

label {
  display: block;
  font-size: 24px;
  color: #666;
  text-align: left;
  margin: 0 0px 10px 10px;

}

/* main */

.image_container {
  padding-top: 10px;
  padding-bottom: 10px;
}

.thumbnail {
  width: 200px;
  height: 200px;
  object-fit: cover;
  background-color: #fffce0;
  border: 5px solid rgb(206, 206, 206);
  border-radius: 50%;
}

.input_container {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
}

.input_container > * {
  width: 50%;
}

.form-select {
  appearance: none;
  background: rgba(144, 144, 144, 0.075);
  border-radius: 6px;
  border: none;
  border: solid 1px rgba(144, 144, 144, 0.25);
  display: block;
  outline: 0;
  padding: 0 1em;
  text-decoration: none;
}

.form-select:invalid {
  color: #bbbbbb; /* 선택되지 않았을 때의 색상 설정 */
} 

.form-check {
  text-align: left;
  padding-left: 0px;
}

input[type="checkbox"]:checked + label:before, input[type="radio"]:checked + label:before {
  background: #a9e0ff;
  border-color: #d7effd;
}

/* category */

.category-section {
  display: flex;
  flex-direction: column; /* 아래로 정렬하도록 설정 */
  align-items: flex-start; /* 좌측 정렬 */
}

.category-section > label {
  margin-left: 5px;
  margin-bottom: 5px;
}

.category-buttons {
  display: flex;
  flex-wrap: wrap; /* 버튼들이 넘칠 경우 다음 줄로 넘어가도록 설정 */
}

.category-buttons button {
  color: white;
  margin-right: 10px;
  margin-bottom: 10px; /* 버튼 아래 간격 추가 */
  padding: 8px 12px;
  border: none;
  border-radius: 20px;
  background-color: #a7a7a7e5;
  cursor: pointer;
  transition: background-color 0.3s;
}

.category-buttons button:hover {
  background-color: #a9d3ff;
}

.category-buttons button.active {
  background-color: #89c2ff;
}
  
/* submit-button */

.submit-button-container {
  display: flex;
  justify-content: center;
  margin-top: 30px; /* 원하는 여백 설정 */
}

.submit-button-container button {
  font-family: 'KBO-Dia-Gothic_bold';
  color: #ffffff;
  background-color: #a7d3f3;
  border: 2px solid #a7d3f3;
  border-radius: 20px;
  width: 50%;
  height: 40px;
}

.submit-button-container button:hover {
  background-color: #85ccff;
  border: 2px;
  border-style: solid;
  border-color: #9ed2f8;
}

.submit-button-container button:active {
    background-color: #6ac1ff !important;
    border-color: #9ed2f8 !important;
}
</style>
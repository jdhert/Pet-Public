<template>
  	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <div class="card">
      <form @submit.prevent="addPet">
      <div class="top-container">
        <img src="../assets/images/addpet_logo.png" alt="고양이" class="addPetLogo">
        <h1>반려동물 등록하기</h1>
      </div>
        <div class="image_container">
          <div class="file-upload-buttons">
            <input type="file" id="image" accept="image/*" ref="image" @change="setThumbnail($event)" style="display: none;"/>
          </div>
          <img :src="thumbnail || defaultImage" alt="Thumbnail" class="thumbnail" @click="openFileInput"/>
        </div>
        <div class="input_container">
          <div class="addPetName mb-3">
            <label class="m-2">이름</label>
            <input type="text" placeholder="이름을 입력해주세요." v-model="pet.name" required/>
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
              <input type="text" class="form-control" aria-label="Amount (to the nearest dollar)" placeholder="몸무게(숫자)을 입력해주세요." v-model="pet.weight" pattern="\d+(\.\d{1,2})?" required>
              <span class="input-group-text">kg</span>
            </div>
          </div>
          <div class="category-section mb-3">
            <label class="m-2">종류</label>
            <div class="category-buttons">
              <button :class="{ 'active': pet.species === '강아지' }" @click.prevent="selectCategory('강아지')">강아지</button>
              <button :class="{ 'active': pet.species === '고양이' }" @click.prevent="selectCategory('고양이')">고양이</button>
              <button :class="{ 'active': pet.species === '소동물' }" @click.prevent="selectCategory('소동물')">소동물</button>
              <button :class="{ 'active': pet.species === '기타' }" @click.prevent="selectCategory('기타')">기타</button>
            </div>
            <input type="text" placeholder="자세한 품종을 입력해주세요." v-model="pet.spec_species"/>
          </div>
          <div class="addPetGender mb-3">
            <label class="m-2">성별</label>
            <div class="d-flex">
              <div class="form-check">
                <input class="form-check-input" type="radio" name="optionsRadios" id="optionsRadios1" value="M" v-model="pet.gender" required>
                <label class="form-check-label" for="optionsRadios1">수컷</label>
              </div>
              <div class="form-check">
                <input class="form-check-input" type="radio" name="optionsRadios" id="optionsRadios2" value="F" v-model="pet.gender" required>
                <label class="form-check-label" for="optionsRadios2">암컷</label>
              </div>
            </div>
          </div>
          <div class="addPetDisease mb-3">
            <label class="m-2">질병 여부</label>
            <div class="form-check">
              <input class="form-check-input" type="checkbox" value="" id="petDisease" v-model="pet.disease">
              <label class="form-check-label" for="petDisease">현재 질병을 앓고 있거나 최근에 질병으로 병원 진료를 받은 적이 있다.</label>
            </div>
          </div>
          <div class="addRecog_chip mb-3">
            <label class="m-2">동물정보등록</label>
            <div class="form-check">
              <input class="form-check-input" type="checkbox" value="" id="recog_chip" v-model="pet.recog_chip">
              <label class="form-check-label" for="recog_chip">동물정보등록 시스템에 등록이 되어 있다.</label>
            </div>
          </div>
        </div>        
        <div class="submit-button-container">
          <button type="submit">반려동물 등록</button>
        </div>
      </form>
    </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        pet : {
          petimg: null,
          name : '',
          age : '',
          weight : '',
          gender : '',
          species : '',
          spec_species : '',
          disease : 0,
          recog_chip : 0
        },
        thumbnail: '',
        fileList : [],
        imgPath: "",
        defaultImage: require('../assets/images/plus.png'),
        ages: Array.from({ length: 30 }, (_, index) => index + 1) // 1부터 30까지의 숫자 배열 생성
      };
    },

    methods: {
      selectCategory(category) {
        this.pet.species = category; 
      },

    
      openFileInput() {
       const fileInput = document.getElementById('image');
       fileInput.click();
      },

   
      setThumbnail(event) {

        const files = event.target.files;
        this.imageUploaded=[];
        this.fileList = files;
        this.fileList = Array.from(event.target.files);
        console.log(this.fileList);

        const reader = new FileReader();

        reader.onload = (event) => {
          this.thumbnail = event.target.result; 
        };

        this.image = this.$refs.image.files[0]; // 사용자가 올린 이미지

        reader.readAsDataURL(event.target.files[0]);

        reader.onloadend = () => {
          this.pet.petimg = reader.result; // 수정된 부분: this를 사용하여 pet 객체에 접근합니다.
        };
        
      },
      getRandomColor(){
        const letters = '0123456789ABCDEF';
        let color = '#';
        for (let i = 0; i < 6; i++) {
            color += letters[Math.floor(Math.random() * 16)];
        }
        return color;
      },

      addPet(){
        if (this.pet.disease) {
          this.pet.disease = 1;
        }
        if (this.pet.recog_chip) {
          this.pet.recog_chip = 1;
        }

        let formData = new FormData();
        formData.append('image', this.fileList[0]);
        console.log(this.pet.petimg);
        console.log(this.fileList[0]);
        console.log(formData);
        this.axios.post(`/api/free/img`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }}).then((res) => {
          for(let s of res.data)
            this.imgPath = s;

          this.axios.post(`/api/pet`, {
            userId :  this.$cookies.get("id"),
            petImg : this.imgPath,
            petName : this.pet.name, 
            petAge : this.pet.age,
            petWeight :  parseFloat(this.pet.weight),
            species : this.pet.species,
            spec_species : this.pet.spec_species,
            petGender : this.pet.gender,
            petDisease : !!this.pet.disease,
            petRecog_chip : !!this.pet.recog_chip,
            petColor : this.getRandomColor(),
            //petcolor 랜덤값 부여
        }
        ).then( this.$router.push('/mypage')).catch();
        }).catch();       
      },
      
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

  .addPetLogo {
    max-width: 330px;
    height: auto;
  }
  
  label {
    display: block;
    font-size: 24px;
    color: #666;
    text-align: left;
    margin: 0 0px 10px 10px;

  }


  .image_container {
    padding-top: 10px;
    padding-bottom: 10px;
  }

  .thumbnail {
    width: 200px;
    height: 200px;
    object-fit: cover;
    background-color: #ffffff;
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
    color: #bbbbbb;
  } 

  .form-check {
    text-align: left;
    padding-left: 0px;
  }

  input[type="checkbox"]:checked + label:before, input[type="radio"]:checked + label:before {
    background: #a9e0ff;
    border-color: #d7effd;
  }
  


  .category-section {
    display: flex;
    flex-direction: column;
    align-items: flex-start; 
  }

  .category-section > label {
    margin-left: 5px;
    margin-bottom: 5px;
  }
  
  .category-buttons {
    display: flex;
    flex-wrap: wrap; 
  }
  
  .category-buttons button {
    color: white;
    margin-right: 10px;
    margin-bottom: 10px; 
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
    

  .submit-button-container {
    display: flex;
    justify-content: center;
    margin-top: 30px; 
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
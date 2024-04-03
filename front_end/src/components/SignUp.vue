<template>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
  <div class="main">
    <div class="card">
      <div class="form-container">
        <!-- <form class="form" @submit.prevent="handleSubmit"> -->
        <form class="form" @submit.prevent="signUp">
          <div>
            <h1>반갑개<img src="../assets/images/paw1.png" alt="Logo"></h1>
            <p>반려동물 관리 솔루션, 지금 바로 시작해보세요!</p>
          </div>
          
          <div>
            <div class="image_container">
              <div class="file-upload-buttons">
                <input type="file" id="image" accept="image/*" ref="image" @change="setThumbnail($event)" style="display: none;"/>
              </div>
              <img :src="imgPath || defaultImage" alt="Thumbnail" class="thumbnail" @click="openFileInput"/>
            </div>

            <div class="input-block">
              <label for="name">닉네임*</label>
              <input type="text" id="name" placeholder="닉네임을 입력해주세요." v-model="name" required>
            </div>

            <div class="input-block">
              <label for="email">이메일*</label>
              <input type="email" id="email" placeholder="이메일을 입력해주세요." v-model="email" required>
            </div>

            <div class="input-block">
              <label for="password">비밀번호*</label>
              <input type="password" id="password" placeholder="비밀번호를 입력해주세요." v-model="password" required>
            </div>

            <div class="input-block">
              <label for="password-confirm">비밀번호 확인*</label>
              <input type="password" id="password-confirm" placeholder="비밀번호를 다시 한 번 입력해주세요." v-model="passwordVerify" required>
            </div>

            <div class="input-block">
              <label for="address">주소</label>
              <div class="searchAddress d-flex">
                <div class="d-flex align-items-center">
                  <input class="postcode" type="text" v-model="postcode" placeholder="우편번호">
                  <input class="postBtn" type="button" @click="execDaumPostcode" value="주소 찾기">
                </div>
                <input class="roadAddress" type="text" v-model="roadAddress" placeholder="도로명주소">
                <input class="jibunAddress" type="text" v-model="jibunAddress" placeholder="지번주소" style="display: none;">
                <span id="guide" style="color:#999;display:none"></span>
                <input class="detailAddress" type="text" v-model="detailAddress" placeholder="상세주소를 입력해주세요.">
              </div>
            </div>
          </div>

          <p v-if="passwordError">{{ passwordErrorMessage }}</p>

          <div class="checkbox">
            <input type="checkbox" id="agree" required>
            <label for="agree" >이용약관과 개인정보처리방침에 동의합니다.</label>
          </div>
          
          <button type="submit" v-if="!codeVerify">회원가입</button>


          <div>
            <div class="input-block" v-if="codeVerify">
              <label for="code">코드 입력</label>
              <input type="text" id="code" placeholder="코드를 입력해주세요." v-model="code" required>
              <p>남은 시간: {{ timeFormatted }}</p>
            </div>
            <button type="submit" v-if="codeVerify">코드 확인</button>
          </div>
        </form>
      </div>  
    </div>
  </div>
</template>

<script>
export default {
  name: 'SignUpForm'
  ,
  data(){
    return {
      name : "",
      email : "",
      password : "",
      passwordVerify : "",
      passwordError: false,
      passwordErrorMessage: '',
      codeVerify : false,
      code : "",
      timer: null,
      timeLeft: 180, 
      fileList: [],

      imgPath: '',
      thumbnail: '',
      postcode: '',
      roadAddress: '',
      jibunAddress: '',
      detailAddress: '',
      defaultImage: require('../assets/images/plus.png'),        
    }
  },
  computed:{
    timeFormatted() {
      const minutes = Math.floor(this.timeLeft / 60);
      const seconds = this.timeLeft % 60;
      return `${minutes}:${seconds.toString().padStart(2, '0')}`;
    }
  },
  watch: {
    codeVerify(newValue) {
      if (newValue) {
        this.startTimer();
      } else {
        this.resetTimer();
      }
    },
  },
  mounted() {
    this.loadDaumPostcodeScript();
  },
  methods : {
    startTimer() {
      this.timeLeft = 180; 
      this.timer = setInterval(() => {
        if (this.timeLeft > 0) {
          this.timeLeft -= 1;
        } else {
          alert('코드 입력시간 초과!!');
          window.location.reload();
        }
      }, 1000);
    },
    openFileInput() {
      const fileInput = document.getElementById('image');
      fileInput.click();
    },
    setThumbnail(event) {
      const files = event.target.files;
      if (files && files.length > 0) {
        const reader = new FileReader();
        reader.onload = (event) => {
          this.imgPath = event.target.result; // 썸네일 URL 설정
        };
        reader.readAsDataURL(files[0]); // 선택한 파일을 Data URL로 읽기
        // 파일 리스트에 선택한 파일을 추가합니다.
        this.fileList.push(files[0]);
      } else {
        console.error("No files selected or unable to read the selected file.");
      }
    },
    resetTimer() {
      clearInterval(this.timer);
      this.timer = null;
    },
    handleSubmit(){
      this.codeVerify ? this.codeCheck() : this.validateForm();
    },
    validateForm() {
      this.passwordError = false;
      const passwordRegex = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$/;
      if (!passwordRegex.test(this.password)) {
        this.passwordError = true;
        this.passwordErrorMessage = '!!! 비밀번호는 최소 8글자 이상이며, 숫자, 대소문자, 특수문자를 포함해야 합니다. !!!';
        return; 
      }

      if (this.password !== this.passwordVerify) {
        this.passwordError = true;
        this.passwordErrorMessage = '비밀번호가 일치하지 않습니다.';
        return; 
      }
      this.axios.get('/api/login/sendCode', {
        params:{
          email : this.email
        }
      }).then(()=> {
          alert('이메일로 코드가 발송되었습니다.');
          this.codeVerify = true;
      });
      // this.signUp();
    },
    codeCheck(){
      this.axios.get('/api/login/codeVerify',{
        params:{
          code : this.code
        }
      }).then((res) => {
        console.log(res.data)
        if(res.data == true)
          this.signUp();
        else alert('코드를 잘못입력하셨습니다!');
      })
    },
    signUp(){
      if (!this.roadAddress && this.detailAddress) {
        alert("주소를 입력 후 상세주소를 입력해주세요.");
        return;
      }
      let formData = new FormData();
      formData.append('image', this.fileList[0]);
    
      this.axios.post(`/api/free/img`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }).then((res) => {
        for(let s of res.data)
        this.imgPath = s;
        this.axios.post(`/api/signup`, {
          name : this.name,
          email : this.email,
          password : this.password,
          passwordVerify : this.passwordVerify,
          imgPath : this.imgPath,
          address : `${this.roadAddress}/${this.detailAddress}`,
        }).then((res) => {
          if(res.data == true){
            alert('회원가입 성공!!');
            this.$router.push('/login');
          }
          else alert('회원가입 실패하였습니다. 다시 입력해주세요.');
        }).catch();
      }).catch();      
    },
    // 주소 찾기
    execDaumPostcode() {
      new daum.Postcode({
        oncomplete: (data) => {
          let roadAddr = data.roadAddress;
          let extraRoadAddr = '';

          if (data.bname !== '' && /[동|로|가]$/g.test(data.bname)) {
              extraRoadAddr += data.bname;
          }
          if (data.buildingName !== '' && data.apartment === 'Y') {
              extraRoadAddr += (extraRoadAddr !== '' ? ', ' + data.buildingName : data.buildingName);
          }
          if (extraRoadAddr !== '') {
              extraRoadAddr = ' (' + extraRoadAddr + ')';
          }

          this.postcode = data.zonecode;
          this.roadAddress = roadAddr;
          this.jibunAddress = data.jibunAddress;

          let guideTextBox = document.getElementById("guide");
          if (data.autoRoadAddress) {
              let expRoadAddr = data.autoRoadAddress + extraRoadAddr;
              guideTextBox.innerHTML = '(예상 도로명 주소 : ' + expRoadAddr + ')';
              guideTextBox.style.display = 'block';
          } else if (data.autoJibunAddress) {
              let expJibunAddr = data.autoJibunAddress;
              guideTextBox.innerHTML = '(예상 지번 주소 : ' + expJibunAddr + ')';
              guideTextBox.style.display = 'block';
          } else {
              guideTextBox.innerHTML = '';
              guideTextBox.style.display = 'none';
          }
        } 
      }).open();
    },
    // Daum 주소 검색 API
    loadDaumPostcodeScript() {
      const script = document.createElement('script');
      script.src = "//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js";
      document.head.appendChild(script);
    },
  },
  beforeDestroy() {
    this.resetTimer();
  },
}
</script>
  
<style scoped>

@font-face {
    font-family: 'Ownglyph_meetme-Rg';
    src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_2402_1@1.0/Ownglyph_meetme-Rg.woff2') format('woff2');
    font-weight: normal;
    font-style: normal;
}

/* main */

* {
  font-family: 'Ownglyph_meetme-Rg';
}

h1 {
  font-size: 65px;
  font-weight: bold;
}

.card {
  border: 1px solid #e0e0e0;
  border-radius: 20px;
  padding: 20px;
  max-width: 900px;
  min-width: 400px;
  max-height: 1500px;
  margin: auto;
  margin-top: -1em;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.form-container {
  width: 100%;
  max-width: 600px;
  margin: 2.5em auto;
  padding: 20px;
  text-align: center;
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

.logo img {
  max-width: 100px;
  margin-bottom: 20px;
}

.form h1 {
  margin-bottom: 10px;
}

.form p {
  margin-bottom: 20px;
}

.input-block {
  margin-bottom: 20px;
}

.input-block label {
  display: block;
  text-align: start; /* Align to the start (left) */
  font-size: 22px;
  color: #666;
  margin: 0.5rem;
}

button{
  cursor: pointer;
}

.searchAddress {
    display: flex;
    flex-direction: column;
}

.searchAddress > * {
    margin-bottom: 5px;
}

.postcode {
    display: inline;
    width: 50%;
}

.postBtn {
  color: #ffffff;
  background-color: #a7d3f3;
  border: 2px solid #b6e0ff;
}

.postBtn:hover {
  background-color: #4eaef3;
  border: 2px solid #a2d8ff;
}

.postBtn:active {
    background-color: #6ac1ff !important;
    border-color: #9ed2f8 !important;
}


.form button[type="submit"] {
  width: 100%;
  padding: 10px;
  border-radius: 40px;
  background-color: #007BFF;
  color: white;
  border: none;
  margin-top: 10px;
}

.form .checkbox {
  text-align: left;
  margin-top: 10px;
}

.form .checkbox input[type="checkbox"] {
  margin-right: 5px;
}


</style>

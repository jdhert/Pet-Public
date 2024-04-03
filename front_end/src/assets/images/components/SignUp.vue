<template>
    <div class="form-container">
      <form class="form" @submit.prevent="handleSubmit">
        <h1>반갑개<img src="../assets/images/paw1.png" alt="Logo"></h1>
        <p>반려동물 관리 솔루션, 지금 바로 시작해보세요!</p>
        
        <div class="input-block">
          <label for="name">닉네임</label>
          <input type="text" id="name" placeholder="닉네임을 입력해주세요." v-model="name" required>
        </div>
  
        <div class="input-block">
          <label for="email">이메일</label>
          <input type="email" id="email" placeholder="이메일을 입력해주세요." v-model="email" required>
        </div>
  
        <div class="input-block">
          <label for="password">비밀번호</label>
          <input type="password" id="password" placeholder="비밀번호를 입력해주세요." v-model="password" required>
        </div>
  
        <div class="input-block">
          <label for="password-confirm">비밀번호 확인</label>
          <input type="password" id="password-confirm" placeholder="비밀번호를 다시 한 번 입력해주세요." v-model="passwordVerify" required>
        </div>
  
        <div class="input-block">
          <label for="address">주소</label>
          <textarea id="address" placeholder="주소를 입력해주세요." v-model="address"></textarea>
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
        address : "",
        passwordError: false,
        passwordErrorMessage: '',
        codeVerify : false,
        code : "",
        timer: null,
        timeLeft: 180, 
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
        this.axios.post(`/api/signup`, {
          name : this.name,
          email : this.email,
          password : this.password,
          passwordVerify : this.passwordVerify,
          address : this.address
        }).then((res) => {
          if(res.data == true){
            alert('회원가입 성공!!');
            this.$router.push('/login');
          }
          else alert('회원가입 실패!!');
        }).catch();
      }
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

* {
    font-family: 'Ownglyph_meetme-Rg';
}

    h1 {
        font-size: 65px;
        font-weight: bold;
    }

  .form-container {

    width: 100%;
    max-width: 600px;
    margin: 100px auto;
    padding: 20px;
    text-align: center;
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
  margin-bottom: 5px;
  font-size: 16px;
  color: #333;
}

button{
  cursor: pointer;
}


.form input[type="text"],
.form input[type="email"],
.form input[type="password"],
.form textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #020202;
  border-radius: 4px;
  font-size: 16px;
}

  .form input[type="text"]:focus + .input-label,
.form input[type="email"]:focus + .input-label,
.form input[type="password"]:focus + .input-label,
.form textarea:focus + .input-label {
  top: -18px; /* Moves label above the input on focus */
  left: 10px;
  font-size: 12px; /* Optional: reduce font size on focus */
  color: #007bff; /* Optional: change label color on focus */
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
  
<template>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
        <section>
            <form @submit.prevent="UpdatePassword">
                <div class="container d-flex justify-content-center">
                    <div class="card">
                        <div class="d-flex">
                            <div class="d-flex m-4 flex-column">
                                <h3 class="m-2 mb-4">비밀번호 변경</h3>
                                <div class="my-3">
                                    <div class="updatePw mb-3">
                                        <label class="m-2">현재 비밀번호</label>
                                        <input type="password" placeholder="현재 비밀번호를 입력해주세요." v-model="currentPassword" required @keydown.enter.prevent="submitOnEnter"/>
                                    </div>
                                    <div class="updatePw mb-3">
                                        <label class="m-2">새 비밀번호</label>
                                        <input type="password" placeholder="새 비밀번호를 입력해주세요. (최소 7자 이상)" v-model="newPassword" minlength="7" required @keydown.enter.prevent="submitOnEnter"/>
                                    </div>
                                    <div class="updatePw mb-3">
                                        <label class="m-2">새 비밀번호 확인</label>
                                        <input type="password" placeholder="새 비밀번호를 다시 입력해주세요. (최소 7자 이상)" v-model="confirmPassword" minlength="7" required @keydown.enter.prevent="submitOnEnter"/>
                                    </div>
                                </div>
                                <div class="d-flex justify-content-between my-3">
                                    <button class="btn btn-secondary" @click="cancelPage">취소</button>
                                    <button type="submit" class="btn btn-primary">변경하기</button>
                                </div>                    
                            </div>
                        </div>
                    </div>
                </div>
            </form>
        </section>
    
</template>
    
<script>
export default {
  data() {
    return {
        currentPassword: '',
        newPassword: '',
        confirmPassword: '',
    };
  },

  methods: {
    submitOnEnter(event) {
        // 엔터 키가 눌렸는지 확인하고 변경하기 메소드 호출
        this.UpdatePassword();
    },

    cancelPage() {
        this.$router.push('/mypage'); // 이동할 페이지 경로 설정
    },
    
    UpdatePassword() {
        // 비밀번호 변경 로직 추가
        if (this.newPassword.length < 7) {
            alert("새 비밀번호는 최소 7자 이상이어야 합니다.");
            return;
        }
        if (this.newPassword !== this.confirmPassword) {
            alert("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
            return;
        }     
        this.axios.post(`/api/myinfo/updatepw/${this.$cookies.get("id")}`, { 
            currentPassword: this.currentPassword, 
            newPassword: this.newPassword 
        }).then((res) => {
               if(res.data == true){ 
                alert('비밀번호 변경 완료!');
                this.$router.push('/mypage');
               } else alert('비밀번호 변경실패!! 현재 비밀번호를 확인하세요.');
        }).catch(error => {
            console.error('Error occurred:', error); 
        });
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

.card {
    display: flex;
    justify-content: center;
    border: 1px solid #e0e0e0;
    border-radius: 20px;
    padding: 20px;
    max-width: 500px;
    max-height: 1500px;
    margin: auto;
    margin-top: 150px;
    margin-bottom: 150px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.m-4 > * {
    font-family: 'Ownglyph_meetme-Rg';
}

.updatePw > input {
    width: 320px;
}

.btn-secondary {
    background-color: #ffffff;
    color: black;
    width: 60px;
}

.btn-primary {
    background-color: #a7d3f3;
    border: 2px solid #a7d3f3;
    width: 80px;
}

.btn-primary:hover {
    background-color: #85ccff;
    border: 2px solid #9ed2f8;
}

.btn-primary:active {
    background-color: #6ac1ff !important;
    border-color: #9ed2f8 !important;
}

</style> 

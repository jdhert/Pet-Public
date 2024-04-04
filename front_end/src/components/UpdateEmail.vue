<template>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
        <section>
            <form @submit.prevent="UpdateEmail">
                <div class="container d-flex justify-content-center">
                    <div class="card">
                        <div class="d-flex">
                            <div class="d-flex m-4 flex-column" v-if="!this.delay">
                                <h3 class="m-2 mb-4">이메일 변경</h3>
                                <div class="my-3">
                                    <div class="updatePw mb-3">
                                        <label class="m-2">변경할 이메일 입력</label>
                                        <input type="text" placeholder="변경할 이메일을 입력해주세요." v-model="email" required  @keydown.enter.prevent="submitOnEnter"/>
                                    </div>
                                </div>
                                <div class="d-flex justify-content-between my-3">
                                    <button class="btn btn-secondary" @click.prevent="cancelPage">취소</button>
                                    <button type="submit" class="btn btn-primary">변경하기</button>
                                </div>                    
                            </div>
                            <div class="d-flex m-4 flex-column" v-if="this.delay">
                                <h3 class="m-2 mb-4">이메일 변경</h3>
                                <div id="loadingIndicator">
                                     <img src="../assets/images/loading-activity.gif" alt="로딩 중..."/>
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
        email: "",
        delay: false
    };
  },

  methods: {
    submitOnEnter() {
        this.UpdateEmail();
    },
    cancelPage() {
        this.$router.push('/mypage'); 
    },
    UpdateEmail() {
        this.axios.get(`/api/myinfo/check_mail`, {
            params:{
                email : this.email
            }
        }).then(res => {
            if(res.data == true){
                this.delay = true;
                this.axios.get('/api/login/sendCode',{
                    params:{
                        email : this.email
                    }
                });
                alert('이메일로 코드가 발송되었습니다!');
                //추가 수정해야하는부분
            } else alert('이미 사용중인 이메일입니다.');
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

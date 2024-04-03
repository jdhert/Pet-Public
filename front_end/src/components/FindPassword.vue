<template>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
        <section>
            <form @submit.prevent="findPassword">
                <div class="container d-flex justify-content-center">
                    <div class="card">
                        <div class="d-flex">
                            <div class="d-flex m-4 flex-column">
                                <h3 class="m-2 mb-4">비밀번호 찾기</h3>
                                <div class="inputEmail mb-3">
                                    <label class="m-2">Email</label>
                                    <input type="email" placeholder="본인 Email을 입력해주세요." v-model="email" required/>
                                </div>
                                <div class="d-flex justify-content-between mx-3 my-3">
                                    <button class="btn btn-secondary" @click="cancelPage">취소</button>
                                    <button type="submit" class="btn btn-primary">보내기</button>
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
            email: '',
        };
      },
    
      methods: {
        cancelPage() {
            this.$router.push('/login');
        },
        
        findPassword() {
            this.axios.get(`/api/login/findPass`, {
                params:{
                    email : this.email
                }
            }).then((res) =>{
                if(res.data == true){
                    alert('임시비밀번호가 메일로 발송되었습니다.');
                    this.$router.push('/login');
                } else {
                    alert('존재하지 않는 사용자입니다.');
                }
            })
             
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
        margin-top: 250px;
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    }
    
    .m-4 > * {
        font-family: 'Ownglyph_meetme-Rg';
    }
    
    .inputEmail > input {
        width: 350px;
    }

    .btn-secondary {
        background-color: #ffffff;
        color: black;
        width: 60px;
    }
    
    .btn-primary {
        background-color: #a7d3f3;
        border: 2px solid #a7d3f3;
        width: 60px;
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
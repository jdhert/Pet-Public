<template>
  <div class="card">
    <form @submit.prevent="upload">
      <div class="qa-section">
        <img src="../assets/images/img7.png" alt="고양이" class="catImage">
        <h2>펫스타그램 게시글 작성</h2>
        <div class="category-section">
          <label>카테고리</label>
          <div class="category-buttons">
            <button :class="{ 'active': selectedCategory === '강아지' }" @click.prevent="selectCategory('강아지')">강아지</button>
            <button :class="{ 'active': selectedCategory === '고양이' }" @click.prevent="selectCategory('고양이')">고양이</button>
            <button :class="{ 'active': selectedCategory === '소동물' }" @click.prevent="selectCategory('소동물')">소동물</button>
            <button :class="{ 'active': selectedCategory === '기타' }" @click.prevent="selectCategory('기타')">기타</button>
          </div>
        </div>
        <input type="text" placeholder="제목을 입력해주세요." v-model="title" required/>
      </div>
      <div class="question-detail">
        <textarea placeholder="자세한 내용을 입력해주세요." v-model="content" required></textarea>
      </div>
      <div style="display: flex; flex-wrap: wrap;">
        <img v-for="(file1,idx) of this.fileList" :key="idx"  :src=imageUploaded[idx] alt="올린 이미지"  style="border-color: black; border: thick double #32a1ce; width: 32%; height: 35vh; margin: 5px"  /> <br />
      </div>
      <br>
      <div class="tag-input">
        <label>태그 입력</label>
        <!-- <input type="text" placeholder="태그를 입력해주세요. 예: #태그 #입력" v-model="tag"/> -->
        <div class="comp_hashtag" @click="setHashtags" ref="group">
          <p class="help" v-if="helpVisible">{{ defaultPlaceholder }}</p>

          <!-- Hashtags -->
          <div class="tags" v-if="!helpVisible">
            <input
              type="text"
              class="fake"
              ref="fake"
              @keydown.backspace.prevent="deleteTag(focusIndex)"
              @keydown.delete.prevent="deleteTag(focusIndex)"
            />
            <span
              class="tag"
              v-for="(row, index) in tags"
              :key="index"
              :class="{ active: row.select }"
              @click="selectTag(index)"
              >{{ row.value }}</span
            >
          </div>
          <!--// Hashtags -->

          <div class="inp" v-show="!helpVisible">
            <input
              type="text"
              ref="input"
              v-model.trim="value"
              @focus="initSelect"
              @keydown.space.prevent="addHashTags"
              @keydown.enter.prevent="addHashTags"
              @keydown.backspace="initErrorMsg"
              @keydown.delete="initErrorMsg"
              placeholder="태그입력"
            />
          </div>

          <transition
            enter-active-class="animate__animated animate__fadeInDown animate__faster"
            leave-active-class="animate__animated animate__fadeOut"
          >
            <p class="noti" v-if="this.errorMsg">{{ errorMsg }}</p>
          </transition>
        </div>
      </div>
      <br>
      <div class="photo-input">
        <div class="file-upload-buttons">
          <input type="file" id="fileInput"  ref="image" accept="image/*" multiple style="display: none;" @change="previewImages">
          <button class="file-button" @click.prevent="openFileInput">사진 첨부</button>       
        </div>
        <div id="imageList"></div>
      </div>
      <div class="submit-button-container">
        <button type="submit">게시글 등록</button>
        &nbsp;
        <button class="cancle" type="submit" @click.prevent="cancle">취소</button>
      </div>
    </form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      selectedCategory: '', // 선택된 카테고리를 저장할 변수
      title: '',
      content: '',
      tag: [],
      tags: [],
      value: '',
      defaultPlaceholder: this.placeholder ? this.placeholder : "#추천태그 #특수문자제외",
      errorMsg: null,
      focusIndex: null,
      helpVisible: true,
      image: null,
      imageUploaded: [],
      fileList: [],
      imageList: []
    };
  },
  methods: {
    selectCategory(category) {
      this.selectedCategory = category; // 선택된 카테고리 업데이트
    },
    cancle() {
      this.$router.push('/freeboard3').then(() => {
        window.location.reload();
      });
    },
    openFileInput() {
      const fileInput = document.getElementById('fileInput');
      fileInput.click();
    },
    previewImages(event) {
      const files = event.target.files;
      this.imageUploaded = [];
      this.fileList = files;
      this.fileList = Array.from(event.target.files);

      for (let file1 of this.fileList) {
        this.imageUploaded.push(URL.createObjectURL(file1));
      }
    },
    uploadImages() {
      this.image = this.$refs.image.files[0]; // 사용자가 올린 이미지
      console.log(this.image);
      // URL.createObjectURL로 사용자가 올린 이미지를 URL로 만들어서 화면에 표시할 수 있게 한다. img 태그의 src값에 바인딩해준다
      this.imageUploaded = URL.createObjectURL(this.image);
      // 파일 업로드 로직
    },
    upload() {
      let formData = new FormData();
      this.fileList.forEach((file) => {
        formData.append('image', file);
      });

      if (this.fileList.length > 0) {
        this.axios
          .post(`/api/free/img`, formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          })
          .then((res) => {
            this.imageList = res.data;
            for (let tag1 of this.tags) {
              this.tag.push(tag1.value);
            }
            this.axios
              .post(`/api/free`, {
                userId: this.$cookies.get('id'),
                title: this.title,
                content: this.content,
                category: this.selectedCategory,
                tags: this.tag,
                subject: 0,
                images: this.imageList
              })
              .then(() => {
                this.$router.push('/freeboard3').then(() => {
                  window.location.reload();
                });
              })
              .catch((error) => {
                console.error('게시글 등록 중 오류가 발생했습니다.', error);
              });
          })
          .catch((error) => {
            console.error('이미지 업로드 중 오류가 발생했습니다.', error);
          });
      } else {
        for (let tag1 of this.tags) {
          this.tag.push(tag1.value);
        }
        this.axios
          .post(`/api/free`, {
            userId: this.$cookies.get('id'),
            title: this.title,
            content: this.content,
            category: this.selectedCategory,
            tags: this.tag,
            subject: 0,
            images: this.imageList
          })
          .then(() => {
            this.$router.push('/freeboard3').then(() => {
              window.location.reload();
            });
          })
          .catch((error) => {
            console.error('게시글 등록 중 오류가 발생했습니다.', error);
          });
      }
    },
    validateTags() {
      const isValid = /^(\#\w+\s*)+/.test(this.tag);
      console.log(isValid);
      if (!isValid) {
        alert('태그는 "#태그" 형식으로 입력해야 합니다. 예: #태그 #입력');
        this.tag = '';
      }
    },
    // 해쉬태그 시작부분
    setVisible() {
      return (this.helpVisible = false);
    },
    async setHashtags() {
      if (this.tags.length > 0) {
        return;
      }

      const result = await this.setVisible();

      if (!result) this.$refs.input.focus();
    },
    addTag() {
      this.tags.push({ value: this.value, select: false });
      return true;
    },
    unselectTag() {
      this.tags.forEach((tag) => (tag.select = false));
    },
    selectTag(idx) {
      if (this.tags.some((tag) => tag.select)) {
        this.unselectTag();
      }

      this.tags[idx].select = !this.tags[idx].select;

      if (!this.tags[idx].select) {
        this.initSelectIndex();
        return;
      }

      this.$refs.fake.focus();
      this.focusIndex = idx;
    },
    deleteTag(idx) {
      if (idx === null) {
        return;
      }

      this.initSelectIndex();
      this.tags.splice(idx, 1);
    },
    initSelect() {
      if (!this.tags.some((tag) => tag.select)) {
        return;
      }

      this.unselectTag();
      this.initSelectIndex();
    },
    initSelectIndex() {
      this.focusIndex = null;
    },
    initErrorMsg() {
      this.errorMsg = null;
    },
    validate() {
      if (this.tags.some((tag) => tag.value === this.value)) {
        return '중복된 단어를 입력하셨습니다.';
      }

      const regex = /[~!@#$%^&*()+|<>?:{},.="':;/-]/;
      if (regex.test(this.value)) {
        return '특수문자는 태그로 등록할 수 없습니다.';
      }

      return false;
    },
    async addHashTags(event) {
      // CASE 공백
      if (event.target.value === '') {
        this.initErrorMsg();
        event.target.focus();
        return;
      }
      // CASE 유효성(중복,특문)
      const resultMsg = await this.validate();
      if (resultMsg) {
        this.errorMsg = resultMsg;
        this.$refs.input.focus();
        return;
      }

      await this.addTag();

      this.errorMsg = null;
      this.value = null;
      this.$refs.input.focus();
    }
  },
  mounted() {
    if (!this.$cookies.get('id')) {
      alert('로그인이 필요합니다.');
      this.$router.push('/login');
      return;
    }
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

* {
  font-family: 'Ownglyph_meetme-Rg';
}

.cancle {
  width: 101.31px;
}

.catImage {
  max-width: 200px;
  height: auto;
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

.category-section {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.category-buttons {
  display: flex;
  flex-wrap: wrap;
}

.category-buttons button {
  margin-right: 10px;
  margin-bottom: 10px;
  padding: 8px 12px;
  border: none;
  border-radius: 20px;
  background-color: #a7a7a7e5;
  cursor: pointer;
  transition: background-color 0.3s;
}

.category-buttons button.active {
  background-color: #007BFF;
}

.qa-section input,
.tag-input input {
  width: calc(100% - 22px);
  margin: 10px 0;
  padding: 12px;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  outline: none;
}

.question-detail textarea {
  width: calc(100% - 22px);
  height: 150px;
  margin: 10px 0;
  padding: 12px;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  outline: none;
}

.qa-section h2 {
  margin-bottom: 16px;
  font-size: 32px;
  color: #1a1a1a;
}

label {
  display: block;
  font-size: 18px;
  color: #666;
  text-align: left;
}

.submit-button-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.file-button {
  background-color: #a8a8a8e5;
  color: white;
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  margin-left: 10px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s;
}

.photo-input {
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.file-upload-buttons {
  margin-right: 20px;
}

button {
  background-color: #287dd8;
  color: white;
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  float: right;
  font-size: 16px;
  transition: background-color 0.3s;
}

button:hover {
  background-color: #0056B3;
}

.comp_hashtag {
  position: relative;
  width: calc(100% - 22px);
  padding: 5px 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  min-height: 40px;
  text-align: left;
  box-sizing: border-box;
}

.comp_hashtag .noti {
  position: absolute;
  left: 0;
  top: 100%;
  font-size: 12px;
  margin-top: 5px;
  padding: 0 5px;
  border-radius: 4px;
  border: 1px solid #ea2136;
  color: #ea2136;
  text-align: left;
  line-height: 2;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
}

.comp_hashtag .help {
  padding: 0;
  margin: 0;
  line-height: 30px;
  font-weight: 300;
  font-size: 14px;
  color: #ccc;
  vertical-align: top;
}

.comp_hashtag .tags {
  position: relative;
  overflow: hidden;
  display: inline-block;
  vertical-align: top;
  margin-bottom: -6px;
}

.comp_hashtag .tags .fake {
  position: absolute;
  width: 1px;
  height: 1px;
  left: -1px;
  right: -1px;
  padding: 0;
  border: 0;
  outline: none;
  -webkit-appearance: none;
  -webkit-text-size-adjust: none;
}

.comp_hashtag .tags .tag {
  display: inline-block;
  position: relative;
  margin: 0 5px 6px 0;
  padding: 0 5px;
  line-height: 30px;
  border-radius: 5px;
  background-color: #eee;
  vertical-align: top;
  word-wrap: break-word;
  word-break: break-all;
  font-size: 13px;
  text-align: left;
}

.comp_hashtag .tags .tag:hover:after {
  display: block;
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  border: 1px solid #aaa;
  content: "";
  border-radius: 5px;
}

.comp_hashtag .tags .tag:before {
  display: inline;
  content: "#";
}

.comp_hashtag .tags .tag.active {
  background-color: #656565;
  color: #fff;
}

.comp_hashtag .inp {
  display: inline-flex;
  align-items: center;
  height: 30px;
  width: 150px;
  vertical-align: top;
}

.comp_hashtag .inp:before {
  display: inline;
  position: relative;
  top: -1px;
  content: "#";
  color: #3e3e3e;
  margin-right: 2px;
  vertical-align: top;
  line-height: 30px;
}

.comp_hashtag .inp input {
  margin-left: 2px;
  width: 135px;
  height: 28px;
  vertical-align: top;
  color: #3e3e3e;
  -webkit-appearance: none;
  -webkit-text-size-adjust: none;
  padding: 0;
  border: 0;
  outline: none;
  vertical-align: top;
}
</style>

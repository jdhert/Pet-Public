import { createApp } from 'vue'
import { router } from './router/router.js'
import App from './App.vue'
import axios from 'axios'
import VueCookies from "vue-cookies";
import store from './store/store';

const app = createApp(App);
app.config.globalProperties.axios = axios
app.use(router)
app.use(VueCookies)
app.use(store);
window.Kakao.init("b4ec84857dd50dc6c250526cb7e3c05b");
app.mount('#app')
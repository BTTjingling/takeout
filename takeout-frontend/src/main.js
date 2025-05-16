import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import axios from 'axios';
axios.defaults.baseURL = 'http://localhost:8080';
const app = createApp(App)
const pinia = createPinia()
app.config.globalProperties.$axios = axios;
app.use(ElementPlus)
app.use(router)
app.use(pinia)

app.mount('#app') 
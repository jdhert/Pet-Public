import { createWebHistory, createRouter } from 'vue-router';
import WelcomePage from '../components/HelloWorld.vue'
import LoginPage from '../components/LoginView.vue'
import MyPage from '../components/MyPage.vue'
import PetDetail from '../components/PetDetail.vue'
import SignUp from '../components/SignUp.vue'
import QuestionBoard from '../components/QuestionBoard.vue'
import AddQuestionBoard from '../components/AddQuestionBoard.vue'
import AddPhotoBoard from '../components/AddPhotoBoard.vue'
import Diary from '../components/PetDiary.vue'
import CreateDiary from '../components/CreateDiary.vue'
import PetCalendar from '../components/PetCalendar.vue'
import PetActivity from '../components/PetActivity.vue'
import bmi from '../components/bmi.vue'
import carousel from '../components/carousel.vue'
import FreeBoard3 from '../components/FreeBoard3.vue'
import detailFreeBoard from '../components/detailFreeBoard.vue'
import EditFreeBoard from '../components/EditFreeBoard.vue'
import MyFeed from '../components/MyFeed.vue'
import UserUpdate from '../components/UserUpdate.vue'
import DeleteUser from '../components/DeleteUser.vue'
import AddPet from '../components/AddPet.vue'
import PetUpdate from '../components/PetUpdate.vue'
import EditQuestionBoard from '../components/EditQuestionBoard.vue'
import Editcarousel from '../components/Editcarousel.vue'
import ReplyComponent from '../components/ReplyComponent.vue'
import FindPassword from '../components/FindPassword.vue'
import UpdatePw from '../components/UpdatePw.vue'
import Footer2 from '../components/Footer2.vue'


const routes = [
  {
    path: '/', component:WelcomePage
  },
  {
    path: '/login', component: LoginPage
  },
  {
    path: '/mypage', component: MyPage
  },
  {
    path: '/petdetail', component: PetDetail,
  },
  {
    path: '/signup', component: SignUp
  },
  {
    path: '/qnaboard', component: QuestionBoard
  },
  { 
    path: '/qnaboard/get/:id', component: QuestionBoard
  },
  {
    path: '/addqna', component: AddQuestionBoard
  },
  {
    path: '/addphoto', component: AddPhotoBoard
  },
  {
    path: '/diary', component: Diary
  },
  {
    path: '/create', component: CreateDiary
  },
  {
    path: '/calendar', component: PetCalendar
  },
  {
    path : '/pet_act', component: PetActivity
  },
  {
    path : '/bmi', component: bmi
  },
  {
    path : '/carousel', component: carousel
  },
  {
    path: '/freeboard3', component: FreeBoard3
  },
  { 
    path: '/freeboard3/get/:id', component: FreeBoard3
  },
  {
    path: '/detailfree', component: detailFreeBoard
  },
  {
    path: '/editfree', component: EditFreeBoard
  },
  {
    path: '/myfeed', component: MyFeed
  },
  {
    path: '/userupdate', component: UserUpdate
  },
  {
    path: '/deleteuser', component: DeleteUser
  },
  {
    path: '/addpet', component: AddPet
  },
  {
    path: '/editqna', component : EditQuestionBoard
  },
  {
    path: '/petupdate', component : PetUpdate
  },
  {
    path: '/editcarousel', component : Editcarousel
  },
  {
    path: '/reply', component: ReplyComponent
  },
  {
    path: '/findpassword', component : FindPassword
  },
  {
    path: '/updatepw', component : UpdatePw
  },
 
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export { router };
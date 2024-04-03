import { createStore } from 'vuex';

const store = createStore({
  state() {
    return {
      isLoggedIn: false, // 로그인 상태
      user: null, // 사용자 정보
    };
  },
  mutations: {
    // 로그인 상태 설정
    setLoginStatus(state, status) {
      state.isLoggedIn = status;
    },
    // 사용자 정보 설정
    setUser(state, user) {
      state.user = user;
    }
  },
  actions: {
    // 사용자 로그인 처리
    login({ commit }, user) {
      commit('setLoginStatus', true);
      commit('setUser', user);
      // 여기에 로그인 처리 로직 추가
    },
    // 사용자 로그아웃 처리
    logout({ commit }) {
      commit('setLoginStatus', false);
      commit('setUser', null);
      // 여기에 로그아웃 처리 로직 추가
    }
  }
});

export default store;
  

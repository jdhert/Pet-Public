<template>
  <NavBar :key="componentKey"/>
  <router-view @forceRerender="componentKey++"/>
  <Footer/>
</template>

<script>
import NavBar from './components/NavBar2.vue';
import Footer from './components/Footer.vue';
import { router } from './router/router';

export default {
  name: 'App',
  components: {
    NavBar,
    Footer,
    router
}, data() {
    return {
      componentKey: 0,
    };
  },
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}
</style>

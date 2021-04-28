<template>
  <v-app>
    <v-main v-on:login="update">
      <Login/>
    </v-main>
  </v-app>
</template>

<script>
import Login from '../components/Login';
export default {
  name: 'App',
  components: {
    Login,
  },

  data: () => ({
    //
  }),
  methods:{
    update(){
          this.is_login = true;
      }
  }
};
</script>

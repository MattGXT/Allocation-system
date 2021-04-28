<template>
  <v-app>
    <v-app-bar color="secondary">
      <v-col class="text-left"></v-col>
      <v-col class="text-center">
        <v-btn @click="redirectProject()" v-if="is_login">Projects</v-btn>
      </v-col>
      <v-col class="text-right">
        <v-btn @click="redirectLogin()" v-if="is_login">Sign Out</v-btn>
      </v-col>
    </v-app-bar>
    <router-view v-on:login='update'></router-view>
  </v-app>
</template>

<script>
export default {
    data () {
      return {
        is_login: false,
        componentKey: 0,
    }},
    name: 'App',
    methods: {
      redirectProject() { // this method is called on button click
        this.$router.push('/project');
      },
      redirectLogin() { // this method is called on button click
        localStorage.clear();
        this.is_login = false;
        this.$router.push('/');
      },
      update(){
        
        this.is_login = true;
      }
    }
    
};
</script>

<style>
</style>

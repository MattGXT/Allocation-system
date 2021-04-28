<template>
   <v-app id="inspire">
         <v-container fluid style="height: 100vh;">
            <v-layout align-center justify-center fill-height>
               <v-flex xs12 sm8 md4>
                  <v-card :aspect-ratio="16/9" class="elevation-12">
                     <v-toolbar dark color="primary">
                        <v-toolbar-title>Welcome</v-toolbar-title>
                     </v-toolbar>
                     <v-card-text>
                        <v-form>
                           <v-text-field
                              prepend-icon="email"
                              v-model="field.email"
                              name="email"
                              label="Email"
                              type="text"
                           ></v-text-field>
                           <v-text-field
                              id="password"
                              prepend-icon="lock"
                              v-model="field.password"
                              name="password"
                              label="Password"
                              type="password"
                           ></v-text-field>
                        </v-form>
                     </v-card-text>
                     <v-card-actions>
                        <v-spacer></v-spacer>
                        <v-btn color="primary" outlined @click="login()">Sign in</v-btn>
                     </v-card-actions>
                  </v-card>
                  <v-snackbar
                     v-model="snackbar"
                     :color="bg_color"
                  >
                     {{snackbar_text}}
                     <template v-slot:action="{ attrs }">
                     <v-btn
                        color="white"
                        text
                        v-bind="attrs"
                        @click="snackbar = false"
                     >
                        Close
                     </v-btn>
                     </template>
                  </v-snackbar>
               </v-flex>
            </v-layout>
            <v-layout align-center justify-center>
               <Register v-on:register="updatealert"></Register>
            </v-layout>
         </v-container>    
   </v-app>
</template>

<script>
import Register from './Register.vue';
import axios from 'axios';

export default {
   name: 'Login',
   components: {
    Register,
  },
   props: {
      source: String,
   },
   data(){
      return{
         snackbar: false,
         snackbar_text: '',
         bg_color:'',
         field:{
            email:'',
            password:''
         }
      }
   },
   methods: {
      login() { 
         axios.post(`http://localhost:8080/login`, {
            account_email:this.field.email,
            password:this.field.password
         })
         .then(response => {
            if(response.data.data){
               localStorage.setItem('token', JSON.stringify(response.data.data.token));
               this.alertsuccess();
               this.$router.push('/project');
            }else{
               this.alertwarning();
            }
         })
         .catch(e => {
            this.alerterror();
            console.log(e)
         })
      },

      updatealert(info){
         if(info == 'success'){
            this.alertsuccess();
         }else if(info == 'warning'){
            this.alertwarning();
         }else{
            this.alerterror();
         }
      },

      alertsuccess(){
         this.snackbar_text = 'Success!';
         this.bg_color = 'success';
         this.snackbar = true;
      },

      alertwarning(){
         this.snackbar_text = 'Please check your account/password';
         this.bg_color = 'warning';
         this.snackbar = true;
      },

      alerterror(){
         this.snackbar_text = 'Oh... There are something wrong';
         this.bg_color = 'error';
         this.snackbar = true;
      }
    }
   
};
</script>

<style>
</style>

<template>
   <v-app id="inspire">
      <v-content>
         <v-container fluid fill-height>
            <v-layout align-center justify-center>
               <v-flex xs12 sm8 md4>
                  <v-card :aspect-ratio="16/9" class="elevation-12">
                     <v-toolbar dark color="primary">
                        <v-toolbar-title>Welcome</v-toolbar-title>
                     </v-toolbar>
                     <v-card-text>
                        <v-form>
                           <v-text-field
                              prepend-icon="person"
                              name="login"
                              label="Login"
                              type="text"
                           ></v-text-field>
                           <v-text-field
                              id="password"
                              prepend-icon="lock"
                              name="password"
                              label="Password"
                              type="password"
                           ></v-text-field>
                        </v-form>
                     </v-card-text>
                     <v-card-actions>
                        <v-spacer></v-spacer>
                        <v-btn color="primary" outlined @click="redirectUser()">Sign in</v-btn>
                     </v-card-actions>
                  </v-card>
               </v-flex>
            </v-layout>
            <Register></Register>
         </v-container>
      </v-content>
   </v-app>
</template>

<script>
import Register from './Register.vue';
export default {
   name: 'Login',
   components: {
    Register,
  },
   props: {
      source: String,
   },
   methods: {
      redirectUser() { // this method is called on button click
           this.$router.push('/project');
      }
    }
};
</script>

<style>
.v-btn {
  min-width: 10;
}
</style>

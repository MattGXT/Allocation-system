<template>

    <v-dialog
      v-model="dialog"
      persistent
      max-width="600px"
    >
      <template v-slot:activator="{ on, attrs }">
        <v-btn
          color="primary"
          text
          v-bind="attrs"
          v-on="on"
        >
          Sign up
        </v-btn>
      </template>
      <v-card>
        <v-card-title>
          <span class="headline">User Profile</span>
        </v-card-title>
        <v-card-text>
          <v-container>
            <v-row>
              <v-col cols="12">
                <v-text-field
                  v-model="register.name"
                  label="Name*"
                  name="name"
                  :counter="10"
                  :rules="nameRules"
                  required
                ></v-text-field>
              </v-col>
              <v-col cols="12">
                <v-text-field
                  v-model="register.age"
                  label="Age*"
                  name="age"
                  :rules="ageRules"
                  required
                ></v-text-field>
              </v-col>
              <v-col cols="12">
                <v-text-field
                  v-model="register.email"
                  label="Email*"
                  name = "email"
                  :rules="emailRules"
                  required
                ></v-text-field>
              </v-col>
            </v-row>
          </v-container>
          <small>*indicates required field</small>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            color="blue darken-1"
            text
            @click="dialog = false"
          >
            Close
          </v-btn>
          <v-btn
            color="blue darken-1"
            text
            @click="Register()"
            
          >
            Send
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    
</template>

<script>
import axios from 'axios';
  export default {
    data: () => ({
      snackbar: false,
      snackbar_text: '',
      bg_color:'',
      dialog: false,
      name:'',
      email:'',
      nameRules: [
      v => !!v || 'Name is required'
      ],
      ageRules: [
      v => !!v || 'Age is required'
      ],
      emailRules: [
      v => !!v || 'E-mail is required',
      v => /.+@.+/.test(v) || 'E-mail must be valid',
      ],
      register:{
        name:'',
        age:'',
        email:'',
      }
    }),
    methods: {
      Register() { 
        this.dialog = false;
         axios.post(`http://localhost:8080/user/student/rigister`, {
           name: this.register.name,
           age: parseInt(this.register.age),
           account_email:this.register.email,
         })
         .then(response => {
            console.log(response.data.msg)
            if(response.data.msg == 'successs'){
              this.$emit('register','success');
            }else{
              this.$emit('register','failed');
            }
         })
         .catch(e => {
            this.$emit('register','error');
            console.log(e)
         })
      }
    }
    
  }
</script>

<style>
</style>

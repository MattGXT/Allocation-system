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
            <v-form ref="form">
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
                  v-model="register.sid"
                  label="SID*"
                  name="sid"
                  :rules="sidRules"
                  required
                ></v-text-field>
              </v-col>
              <v-col cols="12">
                <v-text-field
                  v-model="register.unikey"
                  label="Unikey*"
                  name="unikey"
                  :rules="unikeyRules"
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
            </v-form>
            
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
      sidRules: [
      v => !!v || 'SID is required'
      ],
      unikeyRules: [
      v => !!v || 'Unikey is required'
      ],
      emailRules: [
      v => !!v || 'E-mail is required',
      v => /.+@uni.sydney.edu.au$/.test(v) || 'E-mail must be USYD address',
      ],
      register:{
        name:'',
        sid:'',
        unikey:'',
        email:'',
      }
    }),
    methods: {
      Register() { 
        if(!this.$refs.form.validate()){
          return
        }
        this.dialog = false;
         axios.post(`http://localhost:4399/user/student/register`, {
           name: this.register.name,
           sid: this.register.sid,
           unikey: this.register.unikey,
           account_email:this.register.email,
         })
         .then(response => {
            console.log(response.data.msg)
            if(response.data.msg == 'successs'){
              this.$emit('alert','success',"Success!");
            }else{
              this.$emit('alert','warning',response.data.msg);
            }
         })
         .catch(e => {
            this.$emit('alert','error',"Network error");
            console.log(e)
         })
      }
    }
    
  }
</script>

<style>
</style>

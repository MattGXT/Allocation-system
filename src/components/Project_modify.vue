<template>
  <v-dialog v-model="dialog" persistent max-width="600px">
    <template v-slot:activator="{ on, attrs }">
      <v-btn
        icon
        v-bind="attrs"
        v-on="on"
        @click="getdata"
      >
        <v-icon>mdi-pencil</v-icon>
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
                  v-model="project.name"
                  label="Name*"
                  name="name"
                  :counter="20"
                  :rules="nameRules"
                  required
                ></v-text-field>
              </v-col>
              <v-col cols="12">
                <v-text-field
                  v-model="project.description"
                  label="Description*"
                  name="description"
                  :rules="desRules"
                  required
                ></v-text-field>
              </v-col>
              <v-col cols="12">
                <v-text-field
                  v-model="project.skill"
                  label="Skill required*"
                  name="skill"
                  :rules="skillRules"
                  required
                ></v-text-field>
              </v-col>
              <v-col cols="12">
                <v-text-field
                  v-model="project.company"
                  label="Company*"
                  name="company"
                  :rules="compRules"
                  required
                ></v-text-field>
              </v-col>
              <v-col cols="12">
                <v-text-field
                  v-model="project.email"
                  label="Email*"
                  name="email"
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
        <v-btn color="blue darken-1" text @click="dialog = false">
          Close
        </v-btn>
        <v-btn color="blue darken-1" text @click="project_modify()"> Update </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import axios from "axios";
export default {
    name:'Projectmodify',
    props:{
        input:Object
    },
  data: () => ({
    snackbar: false,
    snackbar_text: "",
    bg_color: "",
    dialog: false,
    name: "",
    email: "",
    nameRules: [(v) => !!v || "Name is required"],
    desRules: [(v) => !!v || "Description is required"],
    skillRules: [
      (v) => !!v || "Skill is required"
    ],
    compRules: [(v) => !!v || "Role is required"],
    emailRules: [
      (v) => !!v || "E-mail is required",
      (v) => /.+@.+/.test(v) || "E-mail must be valid",
    ],
    project: {
      name: "",
      description: "",
      skill: "",
      company: "",
      email:""
    }
  }),
  created(){   
        
    },
  methods: {
    project_modify() {
      if (!this.$refs.form.validate()) {
        return;
      }
      this.dialog = false;
      console.log(JSON.parse(localStorage.getItem("token")))
      axios
        .post(`http://localhost:4399/project/modify`, {
            id:this.input.id,
            email:this.input.email,
            name:this.project.name,
            company:this.project.company,
            skillRequire:this.project.skill,
            describe:this.project.description,
            state:this.input.state},{
          headers: {
            token: JSON.parse(localStorage.getItem("token")),
        }
        })
        .then((response) => {
          console.log(response.data.msg);
          if (response.data.msg == "successs") {
            this.$emit("alert", "success");
            this.$emit("update")
          } else {
            this.$emit("alert", "error");
          }
        })
        .catch((e) => {
          this.$emit("alert", "error");
          console.log(e);
        });
    },
    getdata(){
        this.project.description = this.input.describe;
        this.project.skill = this.input.skillRequire;
        this.project.company = this.input.company;
        this.project.email = this.input.email;
        this.project.name = this.input.name
    }
  },
};
</script>

<style>
</style>

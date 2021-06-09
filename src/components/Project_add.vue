<template>
  <v-dialog v-model="dialog" persistent max-width="600px">
    <template v-slot:activator="{ on, attrs }">
      <v-btn
        style="position: absolute; left: 10px; bottom: 10px"
        icon
        v-bind="attrs"
        v-on="on"
      >
        <v-icon>mdi-plus</v-icon>
      </v-btn>
    </template>
    <v-card>
      <v-card-title>
        <span class="headline">New Project</span>
      </v-card-title>
      <v-card-text>
        <v-container>
          <v-form ref="form">
            <v-row>
              <v-col cols="12">
                <v-text-field
                  v-model="project.uniqueId"
                  label="Id*"
                  name="Id"
                  :counter="10"
                  :rules="IdRules"
                  required
                ></v-text-field>
              </v-col>
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
                  v-model="project.number"
                  label="Group number*"
                  name="number"
                  :rules="numRules"
                  required
                ></v-text-field>
              </v-col>
              <v-col>
                <v-checkbox
                  v-model="project.checkbox"
                  :label="`Do you have special requirement?`"
                ></v-checkbox>
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
        <v-btn color="blue darken-1" text @click="project_add()"> Add </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import axios from "axios";
export default {
  data: () => ({
    dialog: false,
    name: "",
    email: "",
    nameRules: [(v) => !!v || "Name is required"],
    IdRules: [(v) => !!v || "Id is required"],
    desRules: [(v) => !!v || "Description is required"],
    skillRules: [(v) => !!v || "Skill is required"],
    compRules: [(v) => !!v || "Role is required"],
    numRules: [
      (v) => !!v || "Number is required",
      (v) => !isNaN(v) || "Please input a number",
    ],
    project: {
      name: "",
      description: "",
      skill: "",
      uniqueID:"",
      company: "",
      number: "",
      checkbox:false
    },
  }),
  methods: {
    project_add() {
      if (!this.$refs.form.validate()) {
        return;
      }
      this.dialog = false;
            console.log({
            name: this.project.name,
            company: this.project.company,
            skillRequire: this.project.skill,
            describe: this.project.description,
            uniqueId:this.project.uniqueId,
            client: JSON.parse(localStorage.getItem("name")),
            clientId: JSON.parse(localStorage.getItem("id")),
            email: JSON.parse(localStorage.getItem("email")),
            isNeedAnnex: this.project.checkbox.toString(),
            groupNumber: this.project.number,})
      axios
        .post(
          `http://18.116.164.154:4399/project/add`,
          {
            name: this.project.name,
            company: this.project.company,
            skillRequire: this.project.skill,
            describe: this.project.description,
            uniqueId:this.project.uniqueId,
            client: JSON.parse(localStorage.getItem("name")),
            clientId: JSON.parse(localStorage.getItem("id")),
            email: JSON.parse(localStorage.getItem("email")),
            isNeedAnnex: this.project.checkbox.toString(),
            groupNumber: this.project.number,
          },
          {
            headers: {
              token: JSON.parse(localStorage.getItem("token")),
            },
          }
        )
        .then((response) => {
          console.log(response.data.msg);
          if (response.data.msg == "successs") {
            this.$emit("alert", "success","Success!");
            this.$emit("update");
          } else {
            this.$emit("alert", "warning",response.data.msg);
          }
        })
        .catch((e) => {
          this.$emit("alert", "error","Network error");
          console.log(e);
        });
    },
  },
};
</script>

<style>
</style>

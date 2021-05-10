<template>
  <v-dialog v-model="dialog" persistent max-width="600px">
    <template v-slot:activator="{ on, attrs }">
      <v-btn icon v-bind="attrs" v-on="on" @click="getdata">
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
                  v-model="register.name"
                  label="Name*"
                  value=""
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
                  label="Email"
                  name="email"
                  :rules="emailRules"
                  disabled
                  required
                ></v-text-field>
              </v-col>
              <v-col cols="12">
                <v-select
                  v-model="register.role"
                  :items="items"
                  label="Role*"
                  :rules="roleRules"
                  required
                ></v-select>
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
        <v-btn color="blue darken-1" text @click="user_modify()">
          Update
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import axios from "axios";
export default {
  name: "Usermodify",
  props: {
    input: Object,
  },
  data: () => ({
    dialog: false,
    name: "",
    email: "",
    nameRules: [(v) => !!v || "Name is required"],
    sidRules: [(v) => !!v || "SID is required"],
    unikeyRules: [(v) => !!v || "Unikey is required"],
    emailRules: [
      (v) => !!v || "E-mail is required",
      (v) => /.+@.+/.test(v) || "E-mail must be valid",
    ],
    roleRules: [(v) => !!v || "Role is required"],
    register: {
      name: "",
      sid: "",
      unikey:"",
      email: "",
      role: "",
    },
    items: ["Admin", "Client", "Student"],
  }),
  methods: {
    user_modify() {
      if (!this.$refs.form.validate()) {
        return;
      }
      this.dialog = false;
      console.log(this.register.role);
      axios
        .post(
          `http://localhost:4399/user/modify`,
          {
            name: this.register.name,
            sid: this.register.sid,
            unikey: this.register.unikey,
            accountEmail: this.register.email,
            password: "123456",
            role: this.register.role.toLowerCase(),
          },
          {
            headers: {
              token: JSON.parse(localStorage.getItem("token")),
            },
          }
        )
        .then((response) => {
          console.log(response);
          if (response.data.msg == "successs") {
            this.$emit("alert", "success");
            this.$emit("update");
          } else {
            this.$emit("alert", "error");
          }
        })
        .catch((e) => {
          this.$emit("alert", "error");
          console.log(e);
        });
    },

    getdata() {
      this.register.name = this.input.name;
      this.register.unikey = this.input.unikey;
      this.register.sid = this.input.sid;
      this.register.email = this.input.accountEmail;
      this.register.role =
        this.input.role[0].toUpperCase() + this.input.role.substring(1);
    },
  },
};
</script>

<style>
</style>

<template>
  <v-container fluid style="height: 100vh;">
    <v-layout align-center justify-center fill-height>
      <v-flex xs12 sm8 md4>
        <v-card :aspect-ratio="16 / 9" class="elevation-12">
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
      </v-flex>
    </v-layout>
    <v-layout align-center justify-center>
       <Register v-on="$listeners" block></Register>
    </v-layout>
  </v-container>
</template>

<script>
import Register from "./Register.vue";
import axios from "axios";

export default {
  name: "Login",
  components: {
    Register,
  },
  props: {
    source: String,
  },
  data() {
    return {
      field: {
        email: "",
        password: "",
      },
    };
  },
  methods: {
    login() {
      axios
        .post(`http://localhost:4399/login`, {
          account_email: this.field.email,
          password: this.field.password,
        })
        .then((response) => {
          if (response.data.data) {
            console.log(response.data.data)
            localStorage.setItem(
              "token",
              JSON.stringify(response.data.data.token)
            );
            localStorage.setItem(
              "role",
              JSON.stringify(response.data.data.role)
            );
            localStorage.setItem(
              "name",
              JSON.stringify(response.data.data.name)
            );
            localStorage.setItem(
              "id",
              JSON.stringify(response.data.data.userId)
            );
            localStorage.setItem(
              "email",
              JSON.stringify(response.data.data.email)
            );
            this.$emit("alert", "success","Success!");
            this.$emit("reload");
            this.$router.push("/project");
          } else {
            this.$emit("alert", "warning","Please check your password/account");
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

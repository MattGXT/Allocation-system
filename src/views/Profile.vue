<template>
  <v-app>
    <v-main>
      <v-container fluid>
        <v-layout justify-center>
          <v-flex xs12 sm12 md8>
            <v-card>
              <v-card-title>Profile</v-card-title>

              <v-form ref="form" lazy-validation>
                <v-col cols="12" sm="12">
                  <v-text-field
                    v-model="name"
                    :counter="10"
                    :rules="nameRules"
                    label="Name"
                    required
                  ></v-text-field>
                </v-col>
                <v-col cols="12" sm="12">
                  <v-text-field
                    v-model="age"
                    :rules="ageRules"
                    label="Age"
                    required
                  ></v-text-field>
                </v-col>
                <v-col cols="12" sm="12">
                  <v-text-field
                    v-model="password"
                    :rules="pwRules"
                    label="Password"
                    required
                  ></v-text-field>
                </v-col>
              </v-form>
              <v-card-actions class="justify-center">
                <v-btn color="primary" @click="update"> Update </v-btn>
              </v-card-actions>
            </v-card>
          </v-flex>
        </v-layout>
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
import axios from "axios";
export default {
  
  name: "App",
  components: {},

  data: () => ({
    name: "",
    age: "",
    password: "",
    nameRules: [(v) => !!v || "Name is required"],
    ageRules: [(v) => !!v || "Age is required"],
    pwRules: [(v) => !!v || "Password is required"],
    //
  }),
  methods: {
    update(){
        if(!this.$refs.form.validate()){
          return
        }
        axios
        .post(
          `http://localhost:4399/user/modify`,
          {
            name: this.name,
            age: this.age,
            accountEmail: JSON.parse(localStorage.getItem("email")),
            password: this.password,
            role: JSON.parse(localStorage.getItem("role")),
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
            localStorage.clear();
            this.$router.push("/");
          } else {
            this.$emit("alert", "error");
          }
        })
        .catch((e) => {
          this.$emit("alert", "error");
          console.log(e);
        });
    }
  },
  created() {
    this.$emit("login");
    this.name = JSON.parse(localStorage.getItem("name"))
  },
};
</script>

<template>
  <v-app>
    <v-app-bar color="secondary">
      <v-col class="text-left">
          <v-img
            alt="Logo"
            class="shrink mr-2"
            contain
            src="./assets/bar_logo.jpg"
            transition="scale-transition"
            max-width="200"
          />
      </v-col>
      <v-col class="text-center">
        <v-row align="center" justify="center" class="my-n1">
          <v-btn v-if="is_login" class="mx-1">Groups</v-btn>
          <v-btn @click="redirectProject()" v-if="is_login" class="mx-1"
            >Projects</v-btn
          >
          <v-btn @click="redirectProfile()" v-if="is_login" class="mx-1"
            >Profile</v-btn
          >
        </v-row>
      </v-col>
      <v-col class="text-right">
        <v-btn @click="redirectLogin()" v-if="is_login">Sign Out</v-btn>
      </v-col>
    </v-app-bar>
    <v-main>
      <router-view v-on:login="update" v-on:alert="showalert"></router-view>
      <v-snackbar v-model="snackbar" :color="bg_color">
        {{ snackbar_text }}
        <template v-slot:action="{ attrs }">
          <v-btn color="white" text v-bind="attrs" @click="snackbar = false">
            Close
          </v-btn>
        </template>
      </v-snackbar>
    </v-main>
  </v-app>
</template>

<script>
export default {
  data() {
    return {
      snackbar: false,
      snackbar_text: "",
      bg_color: "",
      is_login: false,
      componentKey: 0,
    };
  },
  name: "App",
  methods: {
    redirectProject() {
      // this method is called on button click
      this.$router.push("/project");
    },
    redirectLogin() {
      // this method is called on button click
      localStorage.clear();
      this.is_login = false;
      this.$router.push("/");
    },
    redirectProfile() {
      // this method is called on button click
      this.$router.push("/profile");
    },
    update() {
      this.is_login = true;
    },

    showalert(info) {
      if (info == "success") {
        this.alertsuccess();
      } else if (info == "warning") {
        this.alertwarning();
      } else {
        this.alerterror();
      }
    },

    alertsuccess() {
      this.snackbar_text = "Success!";
      this.bg_color = "success";
      this.snackbar = true;
    },

    alertwarning() {
      this.snackbar_text = "Please check your account/password";
      this.bg_color = "warning";
      this.snackbar = true;
    },

    alerterror() {
      this.snackbar_text = "Oh... There are something wrong";
      this.bg_color = "error";
      this.snackbar = true;
    },
  },
};
</script>

<style>
</style>

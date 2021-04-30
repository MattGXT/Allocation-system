<template>
  <v-app>
    <v-navigation-drawer permanent expand-on-hover app v-if="['Home'].indexOf($route.name) === -1">
      <v-list>
        <v-list-item class="px-2">
          <v-list-item-avatar>
            <v-img
              src="https://randomuser.me/api/portraits/women/85.jpg"
            ></v-img>
          </v-list-item-avatar>
        </v-list-item>

        <v-list-item link>
          <v-list-item-content>
            <v-list-item-title class="title"> Mr.X </v-list-item-title>
            <v-list-item-subtitle>sandra_a88@gmail.com</v-list-item-subtitle>
          </v-list-item-content>
        </v-list-item>
      </v-list>

      <v-divider></v-divider>

      <v-list nav dense>
        <v-list-item link href="/project">
          <v-list-item-icon>
            <v-icon>mdi-pencil</v-icon>
          </v-list-item-icon>
          <v-list-item-title>Projects</v-list-item-title>
        </v-list-item>
        <v-list-item link href="/profile">
          <v-list-item-icon>
            <v-icon>mdi-account</v-icon>
          </v-list-item-icon>
          <v-list-item-title>My Account</v-list-item-title>
        </v-list-item>
        <v-list-item link href="/group">
          <v-list-item-icon>
            <v-icon>mdi-account-multiple</v-icon>
          </v-list-item-icon>
          <v-list-item-title>Group</v-list-item-title>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>
    <v-toolbar color="secondary" class="ml-15">
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
      <v-col class="text-right">
        <v-btn @click="redirectLogin()" v-if="is_login">Sign Out</v-btn>
      </v-col>
    </v-toolbar>
    <v-content>
      <v-container fluid>
        <router-view v-on:login="update" v-on:alert="showalert"></router-view>
      </v-container>
      <v-snackbar v-model="snackbar" :color="bg_color">
        {{ snackbar_text }}
        <template v-slot:action="{ attrs }">
          <v-btn color="white" text v-bind="attrs" @click="snackbar = false">
            Close
          </v-btn>
        </template>
      </v-snackbar>
    </v-content>
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
    redirectGroup() {
      // this method is called on button click
      this.$router.push("/group");
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

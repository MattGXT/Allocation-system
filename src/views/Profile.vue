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
    }
  },
  created() {
    this.$emit("login");
  },
};
</script>

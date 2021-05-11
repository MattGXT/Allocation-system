<template>
  <v-dialog v-model="dialog" persistent max-width="600px">
    <template v-slot:activator="{ on, attrs }">
      <v-btn
        style="position: absolute; left: 45px; bottom: 10px"
        icon
        v-bind="attrs"
        v-on="on"
      >
        <v-icon>mdi-upload</v-icon>
      </v-btn>
    </template>
    <v-card>
      <v-card-title>
        <span class="headline">Upload</span>
      </v-card-title>
      <v-card-text>
        <v-container>
          <v-form ref="form">
            <v-row>
              <v-col cols="12">
                <v-file-input
                v-model="userfile"
                  accept=".csv, application/vnd.ms-excel"
                  placeholder="Upload csv or xls file"
                  prepend-icon="mdi-file-upload"
                  label="Userlist*"
                ></v-file-input>
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
        <v-btn color="blue darken-1" text @click="user_add()"> Add </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import axios from "axios";
export default {
  data: () => ({
    dialog: false,
    userfile:[]
  }),
  methods: {
    user_add() {
      if (!this.$refs.form.validate()) {
        return;
      }
      this.dialog = false;
      console.log(JSON.parse(localStorage.getItem("token")));
      let formdata = new FormData();
      formdata.append('userAnnex',this.userfile);
      axios
        .post(
          `http://localhost:4399/user/annex/upload`,
          formdata,
          {
            headers: {
              token: JSON.parse(localStorage.getItem("token")),
            },
          }
        )
        .then((response) => {
          console.log(response.data.msg);
          if (response.data.msg == "successs") {
              console.log(response.data);
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
  },
};
</script>

<style>
</style>

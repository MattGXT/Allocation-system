<template>
  <v-dialog v-model="dialog" persistent max-width="600px">
    <template v-slot:activator="{ on, attrs }">
      <v-tooltip bottom>
        <template v-slot:activator="{ on: tooltip }">
        <v-btn
          style="position: absolute; left: 45px; bottom: 10px"
          icon
          v-bind="attrs"
          v-on="{ ...tooltip, ...on }"
        >
          <v-icon>mdi-upload</v-icon>
        </v-btn>
        </template>
        <span>Please put all data into one column and split them by ,</span>
        <br>
        <span>For example:</span>
        <br>
        <span>accountEmail,password,name,sid,unikey,role</span>
      </v-tooltip>
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
        <v-btn color="blue darken-1" text @click="downloadfile">
          Download Sample
        </v-btn>
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
    userfile: [],
    publicPath: process.env.BASE_URL
  }),
  methods: {
    user_add() {
      if (!this.$refs.form.validate()) {
        return;
      }
      this.dialog = false;
      console.log(JSON.parse(localStorage.getItem("token")));
      let formdata = new FormData();
      formdata.append("userAnnex", this.userfile);
      axios
        .post(`http://18.116.164.154:4399/user/annex/upload`, formdata, {
          headers: {
            token: JSON.parse(localStorage.getItem("token")),
          },
        })
        .then((response) => {
          console.log(response.data.msg);
          if (response.data.msg == "successs") {
            this.$emit("alert", "success", "Success!");
            this.$emit("update");
          } else {
            this.$emit("alert", "warning", response.data.msg);
          }
        })
        .catch((e) => {
          this.$emit("alert", "error", "Network error");
          console.log(e);
        });
    },

    downloadfile(){
      var downloadElement = document.createElement("a");
      var href = `${this.publicPath}sample.xls`
      downloadElement.href = href;
      downloadElement.style.display = "none";
      downloadElement.download = "sample.xls";
      document.body.appendChild(downloadElement);
      downloadElement.click();
      document.body.removeChild(downloadElement);
      window.URL.revokeObjectURL(href);
      console.log(downloadElement);
    }
  },
};
</script>

<style>
</style>

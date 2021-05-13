<template>
  <v-container fluid>
    <v-layout align-center justify-center>
      <v-flex xs12 sm12 md8>
        <v-card>
          <v-card-title>
            Users
            <v-spacer></v-spacer>
            <v-text-field
              v-model="search"
              append-icon="mdi-magnify"
              label="Search"
              single-line
              hide-details
            ></v-text-field>
          </v-card-title>
          <v-data-table :headers="headers" :items="Users" :search="search" :loading="loading">
            <template v-slot:[`item.action`]="{ item }">
              <v-dialog v-model="dialog_reset" width="500" :retain-focus="false">
                <template v-slot:activator="{ on, attrs }">
                  <v-btn v-bind="attrs" v-on="on" icon v-if="item.role!='superAdmin'">
                    <v-icon>mdi-cached</v-icon>
                  </v-btn>
                </template>

                <v-card>
                  <v-card-title> Warning </v-card-title>

                  <v-card-text>
                    Do want to reset the password?
                  </v-card-text>

                  <v-divider></v-divider>

                  <v-card-actions>
                    <v-btn color="green darken-1" text @click="dialog_reset = false">
                      No
                    </v-btn>
                    <v-btn
                      color="green darken-1"
                      text
                      @click="reset_password(item)"
                    >
                      Yes
                    </v-btn>
                  </v-card-actions>
                </v-card>
              </v-dialog>
              <Usermodify v-on:update='update' :input="item" v-on="$listeners" v-if="item.role!='superAdmin'"></Usermodify>
              <v-dialog
                v-model="dialog_delete"
                width="500"
                :retain-focus="false"
              >
                <template v-slot:activator="{ on, attrs }">
                  <v-btn v-bind="attrs" v-on="on" icon v-if="item.role!='superAdmin'" @click="this.user_delete_id = item.id">
                    <v-icon>mdi-minus</v-icon>
                  </v-btn>
                </template>

                <v-card>
                  <v-card-title> Warning </v-card-title>

                  <v-card-text>
                    Do you want to delete it?
                  </v-card-text>

                  <v-divider></v-divider>

                  <v-card-actions>
                    <v-btn color="green darken-1" text @click="dialog_delete = false">
                      No
                    </v-btn>
                    <v-btn
                      color="green darken-1"
                      text
                      @click="deleteuser(this.user_delete_id)"
                    >
                      Yes
                    </v-btn>
                  </v-card-actions>
                </v-card>
              </v-dialog>
            </template>
            <template slot="footer">
              <Useradd v-on:update='update' v-on="$listeners"></Useradd>
              <Userupload v-on:update='update' v-on="$listeners"></Userupload>
            </template>
          </v-data-table>
        </v-card>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
import Useradd from "./User_add";
import Usermodify from "./User_modify";
import Userupload from "./User_upload";
import axios from "axios";

export default {
    components: {
    Useradd,
    Usermodify,
    Userupload
  },

  data() {
    return {
      search: "", 
      user_delete_id:"",
      loading: true,
      dialog_delete: false,
      dialog_reset: false,
      headers: [
        {
          text: "Id",
          align: "start",
          sortable: false,
          value: "id",
        },
        { text: "Email", value: "accountEmail" },
        { text: "Name", value: "name" },
        { text: "SID", value: "sid" },
        { text: "Unikey", value: "unikey" },
        { text: "Role", value: "role" },
        { text: "", value: "action", align: "end", sortable: false },
      ],
      Users: [{}],
    };
  },
  created() {
    this.getuser();
  },
  methods: {
    deleteuser(item) {
      this.dialog_delete = false;
      axios
      .delete("http://localhost:4399/user/delete/"+item, {
        headers: {
          token: JSON.parse(localStorage.getItem("token")),
        },
      })
      .then((response) => {
        console.log(response);
        if (response.data.msg == "successs") {
          this.$emit('alert','success')
          this.getuser();
        }else{
            this.$emit('alert','error')
        }
      })
      .catch((error) => {
        console.log(error);
        this.$emit("alert", "error");
      })
      .finally(() => (this.loading = false));
    },

    getuser(){
      this.loading= true;
        const currentpage = 1;
    const pagesize = 10;
    const url =
      "http://localhost:4399/user/page?currentPage=" +
      currentpage +
      "&pageSize=" +
      pagesize;
    axios
      .get(url, {
        headers: {
          token: JSON.parse(localStorage.getItem("token")),
        },
      })
      .then((response) => {
        console.log(response);
        if (response.data.msg == "successs") {
          this.loading= false;
          this.Users = response.data.data.userList;
        }
      })
      .catch((error) => {
        console.log(error);
        this.$emit("alert", "error");
      })
      .finally(() => (this.loading = false));
    },

    reset_password(item){
        this.dialog_reset = false;
        axios
        .post(`http://localhost:4399/user/modify`, {
            name:item.name,
            age:item.age,
            accountEmail:item.accountEmail,
            password:123456,
            role:item.role},{
          headers: {
            token: JSON.parse(localStorage.getItem("token")),
        }
          
        })
        .then((response) => {
          console.log(response);
          if (response.data.msg == "successs") {
            this.$emit("alert", "success");
          } else {
            this.$emit("alert", "error");
          }
        })
        .catch((e) => {
          this.$emit("alert", "error");
          console.log(e);
        });
    },

    update(){
        this.getuser();
    }
  },
};
</script>
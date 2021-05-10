<template>
  <v-dialog v-model="dialog" persistent max-width="600px">
    <template v-slot:activator="{ on, attrs }">
      <v-btn
        icon
        v-bind="attrs"
        v-on="on"
      >
        <v-icon>mdi-pencil</v-icon>
      </v-btn>
    </template>
    <v-card>
      <v-card-title>
        <span class="headline">New Group</span>
      </v-card-title>
      <v-card-text>
        <v-container>
          <v-form ref="form">
            <v-row>
              <v-col cols="12">
                <v-text-field
                    disabled
                  v-model="group.name"
                  label="Name*"
                  name="name"
                  :counter="20"
                  :rules="nameRules"
                  required
                ></v-text-field>
              </v-col>
              <v-col cols="12">
                <v-text-field
                  v-model="group.perference"
                  label="Perferences"
                  name="perference"
                  required
                ></v-text-field>
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
        <v-btn color="blue darken-1" text @click="group_add()"> Add </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import axios from "axios";
export default {
    name:'Groupmodify',
    props:{
        input:Object
    },
  data: () => ({
    dialog: false,
    name: "",
    email: "",
    nameRules: [(v) => !!v || "Name is required"],
    group: {
      name: "",
      perference: "",
    }
  }),
  methods: {
    group_add() {
      if (!this.$refs.form.validate()) {
        return;
      }
      this.dialog = false;
      console.log(JSON.parse(localStorage.getItem("token")))
      axios
        .post(`http://localhost:4399/group/modify`, {
            name:this.group.name,
            describe:this.group.perference,
            leaderId:this.input.leaderId,
            state:this.input.state,
            id:this.input.id},{
          headers: {
            token: JSON.parse(localStorage.getItem("token")),
        }
        })
        .then((response) => {
          console.log(response.data.msg);
          if (response.data.msg == "successs") {
            this.$emit("alert", "success");
            this.$emit("update")
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
  created(){
      this.group.name = this.input.name
      this.group.describe = this.input.describe
  }
};
</script>

<style>
</style>

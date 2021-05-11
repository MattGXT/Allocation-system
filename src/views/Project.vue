<template>
  <v-app>
    <v-main>
      <Projecttime></Projecttime>
      <Projectlist v-on:numbers="setItem" v-on="$listeners" />
      <v-container fluid v-if="role == 'student'">
        <v-layout justify-center>
          <v-flex xs12 sm12 md8>
            <v-row>
              <v-col cols="6" sm="6">
                <v-card height="100%">
                  <v-card-title>My Group</v-card-title>
                  <v-card-text>
                    Group: {{ groupname }}<br />
                    Preferences: {{ perference }}<br />
                    {{ grouptext }}
                  </v-card-text>
                </v-card>
              </v-col>
              <v-col cols="6" sm="6">
                <v-card height="100%">
                  <v-card-title> Preferences </v-card-title>
                  <v-col>
                    <v-select
                      ref="selector"
                      v-model="selected"
                      :items="items"
                      attach
                      chips
                      label="Select the project you want to join~"
                      multiple
                      v-on:input="limiter"
                      :rules="selectrule"
                      v-if="id == leaderid"
                    ></v-select>
                    <v-card-text v-if="id != leaderid">
                      {{ remindtext }}
                    </v-card-text>
                    <v-card-actions
                      class="justify-center"
                      v-if="id == leaderid"
                    >
                      <v-dialog v-model="dialog" width="600">
                        <template v-slot:activator="{ on, attrs }">
                          <v-btn
                            dark
                            v-bind="attrs"
                            v-on="on"
                            color="primary"
                            @click="application"
                          >
                            Submit
                          </v-btn>
                        </template>

                        <v-card>
                          <v-card-title class="headline lighten-2">
                            {{ title }}
                          </v-card-title>

                          <v-card-text>
                            {{ content }}
                          </v-card-text>
                          <v-card-actions>
                            <v-file-input
                            :rules="filerules"
                            accept="image/png, image/jpeg, image/bmp"
                            placeholder="Pick a screenshot"
                            prepend-icon="mdi-camera"
                            label="Screenshot"
                            v-model="evidence"
                            v-if="fileshow"
                          ></v-file-input>
                          </v-card-actions>
                          <v-divider></v-divider>

                          <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn color="primary" text @click="dialog = false,fileshow = false">
                              Close
                            </v-btn>
                            <v-btn color="primary" text @click="dialog = false" v-if="fileshow">
                              Submit
                            </v-btn>
                          </v-card-actions>
                        </v-card>
                      </v-dialog>
                    </v-card-actions>
                  </v-col>
                </v-card>
              </v-col>
            </v-row>
          </v-flex>
        </v-layout>
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
import Projectlist from "../components/ProjectList";
import Projecttime from "../components/Project_time";
import axios from "axios";
export default {
  name: "App",
  components: {
    Projectlist,
    Projecttime,
  },

  data() {
    return {
      remindtext: "Only the group leader can submit the preference",
      id: "",
      leaderid: "",
      grouptext:
        "You are not in a group now. Please go to group page and join a group first.",
      groupname: "",
      perference: "",
      items: [],
      role: "",
      selected: null,
      dialog: false,
      content: "You have to select at less one project",
      title: "Sorry",
      selectrule: [(v) => !!v || "You need select at least three projects"],
      filerules: [(v) => !!v || "You need select at least one image"],
      fileshow: false,
      groupnum: 0,
      project: [],
      evidence: [],
      path:''
    };
  },

  created() {
    this.$emit("login");
    this.role = JSON.parse(localStorage.getItem("role"));
    this.getmygroup([]);
    this.id = JSON.parse(localStorage.getItem("id"));
  },

  methods: {
    limiter(e) {
      if (e.length > 3) {
        console.log("you can only select two", e);
        e.pop();
      }
    },
    setItem(projects) {
      this.project = projects;
      projects.forEach((element) => {
        this.items.push(element.id);
      });
    },
    application() {
      if (!this.$refs.selector.validate()) {
        this.content = "You have to select at least one project";
        return;
      }
      var special = false;
      for (const val of this.selected) {
        this.project.forEach((element) => {
          if (element.id == val && element.isNeedAnnex == "true") {
            special = true;
          }
        });
        this.path += val;
        this.path += ",";
      }
      this.path = this.path.slice(0, -1);
      if (special) {
        this.fileshow = true;
        this.content = "You need to provide a screenshot to prove you have already got the permission";
      } else {
        this.sendapplication(this.path)
      }
    },

    getmygroup() {
      const url = "http://localhost:4399/group/myGroup/page";
      axios
        .get(url, {
          headers: {
            token: JSON.parse(localStorage.getItem("token")),
          },
        })
        .then((response) => {
          if (response.data.msg == "successs") {
            if (response.data.data.groupList[0]) {
              this.groupname = response.data.data.groupList[0].name;
              this.perference = response.data.data.groupList[0].describe;
              this.leaderid = response.data.data.groupList[0].leaderId;
              this.groupnum =
                response.data.data.groupList[0].applicationEntities.length;
              this.grouptext = "";
            } else {
              this.groupname = "none";
              this.perference = "none";
              this.grouptext =
                "You are not in a group now. Please go to group page and join a group first.";
            }
          }
        })
        .catch((error) => {
          console.log(error);
          this.$emit("alert", "error");
        })
        .finally(() => (this.loading = false));
    },

    sendapplication(path) {
      axios
        .post(
          "http://localhost:4399/application/allocation/" + path,
          {},
          {
            headers: {
              token: JSON.parse(localStorage.getItem("token")),
            },
          }
        )
        .then((response) => {
          console.log(response);
          if (response.data.msg == "successs") {
            if (response.data.data.GroupInfo) {
              this.dialog = true;
              this.title = "Congratulation";
              this.content =
                "You have submitted your preferences"
            } else {
              this.dialog = true;
              this.title = "Sorry";
              this.content =
                "The projects you selected are full. But we still have another project you may instersted: Project: ";
            }
          } else {
            this.dialog = true;
            this.title = "Sorry";
            this.content = "You already in the group of this project";
          }
        })
        .catch((e) => {
          console.log(e);
        });
    },

    uploadfile(){
      let formdata = new FormData();
      formdata.append('appAnnex',this.evidence);
      this.fileshow = false;
      this.dialog = false;
      axios
        .post(
          `http://localhost:4399/application/annex/upload`,
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
            this.sendapplication(this.path);
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
};
</script>

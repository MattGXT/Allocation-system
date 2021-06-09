<template>
  <v-app>
    <v-main>
      <Projecttime v-on="$listeners" v-on:timelimit="settime"></Projecttime>
      <Projectlist v-on:numbers="setItem" v-on="$listeners" :endtime="endtime"/>
      <v-container fluid v-if="role == 'student'">
        <v-layout justify-center>
          <v-flex xs12 sm12 md8>
            <v-row>
              <v-col cols="6" sm="6">
                <v-card height="100%">
                  <v-card-title>My Group</v-card-title>
                  <v-card-text class="text-subtitle-1 gray--text">
                    Group: {{ groupname }}<br />
                    Preferences: {{ perference==""?"None":perference }}<br />
                    <v-divider></v-divider>
                    Members:
                    <v-card-text class="pa-0" v-for="(aa, i) in groupmember" :key="'A' + i">
                    {{ aa.studentName }}<v-icon right v-if="aa.studentId == leaderid">mdi-crown-outline</v-icon> 
                    </v-card-text>
                    
                    </v-card-text>
                </v-card>
              </v-col>
              <v-col cols="6" sm="6">
                <v-card height="100%">
                  <v-card-title>Prefered Project</v-card-title>
                  <v-card-text v-if="id != leaderid" class="text-subtitle-1 gray--text">
                      Only the group leader can submit Prefered Project
                    </v-card-text>
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
                      v-if="id == leaderid && this.groupstate == 'prepare'"
                    ></v-select>
                    <v-card-actions
                      class="justify-center"
                      v-if="id == leaderid && this.groupstate == 'prepare'"
                    >
                      <v-dialog v-model="dialog" width="600">
                        <template v-slot:activator="{ on, attrs }">
                          <v-btn
                            dark
                            v-bind="attrs"
                            v-on="on"
                            color="primary"
                            @click="membercheck"
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
                            accept="application/zip,application/x-zip,application/x-zip-compressed"
                            placeholder="Pick a file"
                            prepend-icon="mdi-zip-box"
                            label=".zip file"
                            v-model="evidence"
                            v-if="fileshow"
                          ></v-file-input>
                          </v-card-actions>
                          <v-divider></v-divider>

                          <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn color="primary" text @click="closedialog">
                              Close
                            </v-btn>
                            <v-btn color="primary" text @click="application()" v-if="specialgroup">
                              Yes
                            </v-btn>
                            <v-btn color="primary" text @click="uploadfile()" v-if="fileshow">
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
      specialgroup:false,
      groupmember:"",
      groupname: "",
      groupId:"",
      groupstate:"",
      perference: "",
      items: [],
      role: "",
      selected: null,
      dialog: false,
      content: "Processing...Just a moment",
      title: "Hold on",
      selectrule: [(v) => !!v || "You need select at least three projects"],
      filerules: [(v) => !!v || "You need select at least one image"],
      fileshow: false,
      groupnum: 0,
      project: [],
      evidence: [],
      path:'',
      idMap:new Map(),
      starttime:Date,
      endtime:Date
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
        this.items.push(element.uniqueId);
        this.idMap.set(element.uniqueId,element.id)
      });
      
    },
    membercheck(){
      if (!this.$refs.selector.validate() || this.selected.length<3) {
        this.content = "You have to select at least three projects";
        return;
      }
      var now = new Date();
      if(now<this.starttime||now>this.endtime){
        this.content = "You are not in the right moment";
        return;
      }
      if (this.groupnum < 4){
        this.specialgroup = true
        this.content = "Please note if your group is less than 4 people, you can still submit your prefered project. But your application will have a low priority in the system. \n Do you want to continue?";
      }else{
        this.application()
      }
    },
    
    application() {
      this.specialgroup = false;
      this.path = ""
      var special = false;
      for (const val of this.selected) {
        this.project.forEach((element) => {
          if (element.id == this.idMap.get(val) && element.isNeedAnnex == "true") {
            special = true;
          }
        });
        this.path += this.idMap.get(val);
        this.path += ",";
      }
      this.path = this.path.slice(0, -1);
      console.log(this.path)
      if (special) {  
        this.fileshow = true;
        this.content = "You need to provide a .zip file with screenshots to prove you have already got the permission.";
      } else {
        this.sendapplication(this.path)
      }
    },

    getmygroup() {
      const url = "http://18.116.164.154:4399/group/myGroup/page";
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
              this.groupId = response.data.data.groupList[0].id;
              this.groupstate = response.data.data.groupList[0].state;
              this.groupmember = response.data.data.groupList[0].applicationEntities;
              this.groupnum =
                response.data.data.groupList[0].applicationEntities.length;
            } else {
              this.groupname = "none";
              this.perference = "none";
              if(this.role == 'student'){
                this.$emit('alert','Notice','You are not in a group now. Please go to group page and join a group first.')
              }
            }
          }
        })
        .catch((error) => {
          console.log(error);
          this.$emit("alert", "error","Network error");
        })
        .finally(() => (this.loading = false));
    },

    sendapplication(projectId) {
      axios
        .post(
          "http://18.116.164.154:4399/group/allocation/",
          {
            projectIds:projectId,
            groupId: this.groupId
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
            if (response.data.data.groupInfo) {
              this.title = "Congratulation";
              this.content =
                "You have submitted your preferences";
              this.dialog = true;
            } else if(response.data.data.recommendProject){
              this.path = "";
              this.title = "Sorry";
              this.content =
                "The projects you selected are full. But we still have another project you may instersted: Project: " + response.data.data.recommendProject[0].uniqueId;
              this.dialog = true;
            }else if(response.data.data.optResult){
              this.title = "Congratulation";
              this.content =
                "You have submitted your preferences";
              this.dialog = true;
            }
            else{
              this.path = "";
              this.title = "Sorry";
              this.content =
                "The projects you selected are full";
              this.dialog = true;
            }
          } else {
            this.title = "Sorry";
            this.content = "You already in the group of this project";
            this.dialog = true;
          }
        })
        .catch((e) => {
          console.log(e);
        });
    },

    uploadfile(){
      if(this.evidence==""){
        this.$emit("alert", "warning", "Please select a file");
        return
      }
      this.dialog = false;
      let formdata = new FormData();
      formdata.append('appAnnex',this.evidence);
      formdata.append('groupId',this.groupId);
      this.fileshow = false;
      this.dialog = false;
      axios
        .post(
          `http://18.116.164.154:4399/application/annex/upload`,
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
            this.$emit("alert", "success", "Success!");
            this.$emit("update");
            this.sendapplication(this.path);
          } else {
            this.$emit("alert", "error","The file you upload may have some issues");
          }
        })
        .catch((e) => {
          this.$emit("alert", "error", "Network error");
          console.log(e);
        });
    },

    settime(start,end){
      this.starttime = start;
      this.endtime = end;
    },

    closedialog(){
      this.dialog = false;
      this.fileshow =false;
      this.content = "Processing...Just a moment";
      this.title = "Hold on";
    }
  },
};
</script>

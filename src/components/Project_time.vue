<template>
  <v-container fluid>
    <v-layout align-center justify-center>
      <v-flex xs12 sm12 md8>
        <v-card height="100%">
          <v-card-title
            >Apply Time
            <v-dialog v-model="dialog" persistent max-width="600px">
              <template v-slot:activator="{ on, attrs }">
                <v-btn icon v-bind="attrs" v-on="on" v-if="role=='superAdmin' || role == 'admin'">
                  <v-icon>mdi-cog</v-icon>
                </v-btn>
              </template>
              <v-card>
                <v-card-title>
                  <span class="headline">Set Time</span>
                </v-card-title>
                <v-card-text>
                  <v-container>
                    <v-form ref="form">
                        <v-col>
                            <v-datetime-picker v-model="start_set" label="Apply start at:"></v-datetime-picker>
                        </v-col>
                        <v-col>
                            <v-datetime-picker v-model="end_set" :rule="endtimerule" label="Apply end at:" required></v-datetime-picker>
                        </v-col>
                      
                    </v-form>
                  </v-container>
                </v-card-text>
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn color="blue darken-1" text @click="dialog = false">
                    Close
                  </v-btn>
                  <v-btn color="blue darken-1" text @click="settime()"> Set </v-btn>
                </v-card-actions>
              </v-card>
            </v-dialog>
          </v-card-title>
          <v-card-text>
            Start at: {{ starttime }}<br />
            End at: {{ endtime }}
          </v-card-text>
        </v-card>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
import axios from "axios";
export default {

  components: {
    
  },
  data() {
    return {
      starttime: "Pending",
      endtime: "Pending",
      role: "",
      dialog: false,
      start_set:'',
      end_set:'',
      endtimerule:[
      (v) => !!v || "End date is required",
    ],
    };
  },
  created() {
    this.role = JSON.parse(localStorage.getItem("role"));
    this.gettime();
  },
  methods: {
    set() {
      this.dialog = false;
      var newdate = new Date(this.start_set)
      console.log(newdate)
    },
    gettime(){
      axios
        .get("http://18.116.164.154:4399/setting/applyTime/get?id=1", {
          headers: {
            token: JSON.parse(localStorage.getItem("token")),
          },
        })
        .then((response) => {
          console.log(response);
          if(response.data.data.applyTime != null){
            var date = new Date(response.data.data.applyTime.applyStartTime)
            this.starttime = date.toLocaleDateString('zh-Hans-CN') + ' ' + date.toLocaleTimeString('it-IT')
            var date2 = new Date(response.data.data.applyTime.applyEndTime)
            this.endtime = date2.toLocaleDateString('zh-Hans-CN') + ' ' + date2.toLocaleTimeString('it-IT')
            this.$emit("timelimit",date,date2)
          }
        })
        .catch((error) => {
          console.log(error);
          this.$emit("alert", "error","Network error");
        })
        .finally(() => (this.loading = false));
    },

    settime(){
      if(this.start_set === '' || this.end_set ===""){
        this.$emit("alert", "failed","Please select all the time");
        return
      }else if(this.end_set<this.start_set){
        this.$emit("alert", "failed","End time must large than start time");
        return
      }
      console.log(this.start_set.toLocaleDateString('zh-Hans-CN') + ' ' + this.start_set.toLocaleTimeString('it-IT'),this.end_set.toLocaleDateString('zh-Hans-CN') + ' ' + this.end_set.toLocaleTimeString('it-IT'))
      axios
        .post("http://18.116.164.154:4399/setting/applyTime/modify", {
          id:1,
          applyStartTime: this.start_set.toLocaleDateString('zh-Hans-CN') + ' ' + this.start_set.toLocaleTimeString('it-IT'),
          applyEndTime: this.end_set.toLocaleDateString('zh-Hans-CN') + ' ' + this.end_set.toLocaleTimeString('it-IT')
        },{
          headers: {
            token: JSON.parse(localStorage.getItem("token")),
          },
        })
        .then((response) => {
          if(response.data.msg == 'successs'){
            this.gettime()
            this.$emit("alert", "success","Success!");
          }else{
            this.$emit("alert", "warning",response.data.msg);
          }
          this.dialog = false;
        })
        .catch((error) => {
          console.log(error);
          this.$emit("alert", "error","Network error");
        })
        .finally(() => (this.loading = false));
    }
  },

};
</script>

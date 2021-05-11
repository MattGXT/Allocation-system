<template>
  <v-container fluid>
    <v-layout align-center justify-center>
      <v-flex xs12 sm12 md8>
        <v-card height="100%">
          <v-card-title
            >Apply Time
            <v-dialog v-model="dialog" persistent max-width="600px">
              <template v-slot:activator="{ on, attrs }">
                <v-btn icon v-bind="attrs" v-on="on" v-if="role=='superAdmin' || role == 'Admin'">
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
                            <v-datetime-picker v-model="end_set" :rule="endtimerule" label="Apply end at:"></v-datetime-picker>
                        </v-col>
                      
                    </v-form>
                  </v-container>
                </v-card-text>
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn color="blue darken-1" text @click="dialog = false">
                    Close
                  </v-btn>
                  <v-btn color="blue darken-1" text @click="set()"> Set </v-btn>
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
      (v) => !!v || "Number is required",
      (v) => !isNaN(v) || "Please input a number",
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

    }
  },
};
</script>

<template>
  <v-container fluid>
    <v-layout align-center justify-center>
      <v-flex xs12 sm12 md8>
        <v-card>
          <v-card-title>
            Allocation
            <v-spacer></v-spacer>
            <v-text-field
              v-model="search"
              append-icon="mdi-magnify"
              label="Search"
              single-line
              hide-details
            ></v-text-field>
          </v-card-title>
          <v-data-table :headers="headers" :items="applications" :search="search" :loading="loading">
            <template v-slot:[`item.action`]="{ item }">
              <Members :input = item.member :groupName = item.id></Members>
              <v-btn
                icon
                v-if="item.state == 'audit'"
                @click = "approve(item)"
              >
                <v-icon>mdi-check</v-icon>
              </v-btn>
              <v-btn
                icon
                v-if="item.state == 'audit'"
                @click = "reject(item)"
              >
                <v-icon>mdi-close</v-icon>
              </v-btn>
            </template>
            <template slot="footer">
            </template>
          </v-data-table>
        </v-card>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
import axios from "axios";
import Members from "./Members";
export default {
    components: {
      Members
  },

  data() {
    return {
      search: "", 
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
        { text: "Members", value: "members" },
        { text: "Perferences", value: "describe" },
        { text: "Project Id", value: "project_id" },
        { text: "Space", value: "space" },
        { text: "", value: "action", align: "end", sortable: false },
      ],
      applications: [{}],
      projects:new Map()
    };
  },
  created() {
    this.getproject();
  },
  methods: {
    update(){
        this.getproject();
    },
    getgroup(){
      const currentpage = 1;
      const pagesize = 10;
      const url =
        "http://localhost:4399/group/page?currentPage=" +
        currentpage +
        "&pageSize=" +
        pagesize + "&state=audit";
      axios
        .get(url, {
          headers: {
            token: JSON.parse(localStorage.getItem("token")),
          },
        })
        .then((response) => {
          this.info = response.data.bpi;
          console.log(response);
          if (response.data.msg == "successs") {
            this.applications = response.data.data.groupList;
            this.applications = this.applications.map(s => ({'id':s.id,'name':s.name,'members':s.applicationEntities.length, 'describe':s.describe,'state':s.state,'member':s.applicationEntities,'space':this.projects.get(1).name,'leaderId':s.leaderId}))
          }
        })
        .catch((error) => {
          console.log(error);
          this.$emit("alert", "error");
        })
        .finally(() => (this.loading = false));
    },

    getproject() {
      this.loading = true;
      const currentpage = 1;
      const pagesize = 10;
      const url =
        "http://localhost:4399/project/page?currentPage=" +
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
          this.info = response.data.bpi;
          console.log(response);
          if (response.data.msg == "successs") {
            this.loading = false;
            var Projects = response.data.data.projectList;
            Projects.forEach(element => {
              this.projects.set(element.id,{name:element.name,space: element.groupNumber - element.permitCount})
            });
            this.getgroup();
          }
        })
        .catch((error) => {
          console.log(error);
          this.$emit("alert", "error");
        })
        .finally(() => (this.loading = false));
    },

    approve(item){
      axios
        .post(`http://localhost:4399/group/modify`, {
            name:item.name,
            describe:item.describe,
            leaderId:item.leaderId,
            state:"permit",
            id:item.id},{
          headers: {
            token: JSON.parse(localStorage.getItem("token")),
        }
        })
        .then((response) => {
          console.log(response.data.msg);
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

    reject(item){
      axios
        .post(`http://localhost:4399/group/modify`, {
            name:item.name,
            describe:item.describe,
            leaderId:item.leaderId,
            state:"prepare",
            id:item.id},{
          headers: {
            token: JSON.parse(localStorage.getItem("token")),
        }
        })
        .then((response) => {
          console.log(response.data.msg);
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
    }
  },
};
</script>
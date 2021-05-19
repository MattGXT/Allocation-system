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
          <v-data-table
            :headers="headers"
            :items="applications"
            :search="search"
            :loading="loading"
          >
            <template v-slot:[`item.space`]="{ item }">
              <v-chip
                :color="getColor(projects.get(item.project_id).space)"
                dark
              >
                {{ projects.get(item.project_id).space }}
              </v-chip>
            </template>
            <template v-slot:[`item.choose`]="{ item }">
              <v-col cols="12" sm="6" md="6">
                <v-select
                  label="Project Id"
                  solo
                  :items="items"
                  :value="1"
                  v-model="item.project_id"
                  hide-details=""
                ></v-select>
              </v-col>
            </template>
            <template v-slot:[`item.action`]="{ item }">
              <v-btn icon v-if="Array.isArray(item.annex) && item.annex.length" @click="downloadfile(item.annex[0].id,item.annex[0].name)">
                <v-icon>mdi-cloud-download-outline</v-icon>
              </v-btn>
              <Members :input="item.member" :groupName="item.id"></Members>
              <v-btn icon v-if="item.state == 'audit'" @click="approve(item)">
                <v-icon>mdi-check</v-icon>
              </v-btn>
            </template>
            <template slot="footer"> </template>
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
    Members,
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
        { text: "Project Id", value: "choose", sortable: false },
        { text: "Space", value: "space", sortable: false },
        { text: "", value: "action", align: "end", sortable: false },
      ],
      applications: [{}],
      projects: new Map(),
      items: [],
    };
  },
  created() {
    this.getproject();
  },
  methods: {
    update() {
      this.getproject();
    },
    getgroup() {
      const currentpage = 1;
      const pagesize = 10;
      const url =
        "http://localhost:4399/group/page?currentPage=" +
        currentpage +
        "&pageSize=" +
        pagesize +
        "&state=audit";
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
            this.applications = this.applications.map((s) => ({
              id: s.id,
              name: s.name,
              members: s.applicationEntities.length,
              describe: s.selectProjectId,
              state: s.state,
              member: s.applicationEntities,
              leaderId: s.leaderId,
              project_id: s.proId,
              annex: s.annexEntities,
            }));
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
            Projects.forEach((element) => {
              this.projects.set(element.id, {
                name: element.name,
                space: element.groupNumber - element.permitCount,
              });
              this.items.push(element.id);
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

    approve(item) {
      axios
        .post(
          `http://localhost:4399/group/modify`,
          {
            name: item.name,
            describe: item.describe,
            leaderId: item.leaderId,
            state: "permit",
            id: item.id,
          },
          {
            headers: {
              token: JSON.parse(localStorage.getItem("token")),
            },
          }
        )
        .then((response) => {
          console.log(response.data.msg);
          if (response.data.msg == "successs") {
            this.$emit("alert", "success","Success!");
            this.getproject();
          } else {
            this.$emit("alert", "warning",response.data.msg);
          }
        })
        .catch((e) => {
          this.$emit("alert", "error","Network error");
          console.log(e);
        });
    },

    getColor(space) {
      if (space < 2) {
        return "red";
      } else if (space == 2) {
        return "orange";
      } else return "green";
    },

    downloadfile(Id,filename) {
      axios
        .get(
          "http://localhost:4399/application/annex/download",
          {
            responseType: "blob",
            annexId:Id
          ,
            headers: {
              token: JSON.parse(localStorage.getItem("token")),
            },
          }
        )
        .then((response) => {
          console.log(response);
          const blob = new Blob([response.data], { type: response.data.type });
      const link = document.createElement('a');
      link.href = URL.createObjectURL(blob);
      link.download = filename;
      link.click();
      URL.revokeObjectURL(link.href);
        });
    },
  },
};
</script>
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
                :color="getColor(idlist.get(item.project_id).space)"
                dark
              >
                {{ idlist.get(item.project_id).space }}
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
                  @change="changeprojectid(item)"
                ></v-select>
              </v-col>
            </template>
            <template v-slot:[`item.action`]="{ item }">
              <v-btn
                icon
                v-if="Array.isArray(item.annex) && item.annex.length"
                @click="downloadfile(item.annex[0].id, item.annex[0].name)"
              >
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
      idlist:new Map(),
      prefered:(describe)=>this.calculate(describe),
      headers: [
        {
          text: "Id",
          align: "start",
          sortable: false,
          value: "id",
        },
        { text: "Members", value: "members" },
        { text: "Prefered Project", value: "trans" },
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
  computed: {
    
  },

  methods: {
    update() {
      this.getproject();
    },
    getgroup() {
      this.loading = true;
      const currentpage = 1;
      const pagesize = 500;
      const url =
        "http://18.116.164.154:4399/group/page?currentPage=" +
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
              project_id: this.projects.get(s.proId).uniqueid,
              annex: s.annexEntities,
              trans: this.calculate(s.selectProjectId)
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
      const pagesize = 500;
      const url =
        "http://18.116.164.154:4399/project/page?currentPage=" +
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
            var Projects = response.data.data.projectList;
            Projects.forEach((element) => {
              var result = element.groupNumber - element.permitCount;
              if (result < 0) {
                result = 0;
              }
              this.projects.set(element.id, {
                name: element.name,
                space: result,
                uniqueid: element.uniqueId
              });
              this.idlist.set(element.uniqueId,{
                name: element.name,
                space: result,
                id: element.id
              })
              this.items.push(element.uniqueId);
            });
            this.getgroup();

          }
        })
        .catch((error) => {
          console.log(error);
          this.$emit("alert", "error");
        })
        .finally();
    },

    approve(item) {
      if (this.idlist.get(item.project_id).space == 0) {
        this.$emit("alert", "warning", "This project is full");
        return;
      }
      axios
        .post(
          `http://18.116.164.154:4399/group/modify`,
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
            this.$emit("alert", "success", "Success!");
            this.getproject();
          } else {
            this.$emit("alert", "warning", response.data.msg);
          }
        })
        .catch((e) => {
          this.$emit("alert", "error", "Network error");
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

    downloadfile(Id, filename) {
      axios
        .get("http://18.116.164.154:4399/application/annex/download", {
          responseType: "blob",
          params: { annexId: Id },
          headers: {
            token: JSON.parse(localStorage.getItem("token")),
          },
        })
        .then((response) => {
          const blob = new Blob([response.data]);
          var downloadElement = document.createElement("a");
          var href = window.URL.createObjectURL(blob);
          downloadElement.href = href;
          downloadElement.style.display = "none";
          downloadElement.download = filename;
          document.body.appendChild(downloadElement);
          downloadElement.click();
          document.body.removeChild(downloadElement);
          window.URL.revokeObjectURL(href);
          console.log(downloadElement);
        });
    },

    changeprojectid(item) {
      
      axios
        .post(
          `http://18.116.164.154:4399/group/modify`,
          {
            name: item.name,
            describe: item.describe,
            leaderId: item.leaderId,
            projectId: this.idlist.get(item.project_id).id,
            state: "audit",
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
        })
        .catch((e) => {
          console.log(e);
        });
    },

    calculate(prefer){
      var list = prefer.split(",")
      var res = ''
      list.forEach(element=>
        {res += this.projects.get(parseInt(element)).uniqueid
        res += ","}
      )
      res = res.slice(0,-1)
      return res
    }
  },
};
</script>
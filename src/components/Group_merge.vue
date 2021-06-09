<template>
  <v-container fluid>
    <v-layout align-center justify-center>
      <v-flex xs12 sm12 md8>
        <v-card>
          <v-card-title>
            Merge
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
           <template v-slot:[`item.action`]="{ item }">
              <Members :input="item.member" :groupName="item.id"></Members>
              <Merge :input="item" :list="applications" v-on="$listeners" v-on:update="update"></Merge>
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
import Merge from "./Merge";
export default {
  components: {
      Members,
      Merge
  },

  data() {
    return {
      search: "",
      loading: false,
      dialog_delete: false,
      dialog_reset: false,
      idlist:new Map(),
      projectlist:new Map(),
      headers: [
        {
          text: "Id",
          align: "start",
          sortable: false,
          value: "id",
        },
        { text: "Members", value: "members" },
        { text: "Prefered Project", value: "prefer" },
        { text: "", value: "action", align: "end", sortable: false },
      ],
      applications: [{}],
    };
  },
  created() {
    
    
  },
  mounted(){
    this.getproject();
  },
  methods: {
    update() {
      this.getproject();
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
              this.projectlist.set(element.id,element.uniqueId);
              this.idlist.set(element.uniqueId,element.id)
            });
          }
          this.getgroup()
        })
        .catch((error) => {
          console.log(error);
        })
        .finally();
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
        "&state=invalid";
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
              describe: s.selectProjectId.slice(0,-1),
              state: s.state,
              member: s.applicationEntities,
              leaderId: s.leaderId,
              project_id: s.proId,
              annex: s.annexEntities,
              prefer: this.convert(s.selectProjectId)
            }));
          }
        })
        .catch((error) => {
          console.log(error);
          this.$emit("alert", "error");
        })
        .finally(() => (this.loading = false));
    },

    convert(input){
      var a = input.slice(0,-1)
      var list = a.split(",")
      var res = ''
      list.forEach(element=>
        {res += this.projectlist.get(parseInt(element))
        res += ","}
      )
      res = res.slice(0,-1)
      return res
    }
  },
};
</script>
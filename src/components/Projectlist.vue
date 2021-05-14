<template>
  <v-container fluid>
    <v-layout align-center justify-center>
      <v-flex xs12 sm12 md8>
        <v-card>
          <v-card-title>
            Projects
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
            :items="Projects"
            :search="search"
            :loading="loading"
            show-expand
          >
          <template v-slot:item.space="{ item }">
          <v-chip :color="getColor(item.space)" dark>
                {{ item.space }}
              </v-chip>
              </template>
            <template v-slot:expanded-item="{ headers, item }">
              
              <td :colspan="headers.length">
                {{ item.describe }}
                <br />
                By {{ item.client }}
              </td>
            </template>
            <template
              v-slot:[`item.action`]="{ item }"
              v-if="role != 'student'"
            >
              <Members :input= "item.member" v-if="Array.isArray(item.member)&&item.member.length && role != 'student'"></Members>
              <Projectmodify
                v-on:update="update"
                :input="item"
                v-on="$listeners"
                v-if="item.state == 'publish'"
              ></Projectmodify>
              <v-dialog
                v-model="dialog_delete"
                width="500"
                :retain-focus="false"
                v-if="item.state == 'publish'"
              >
                <template v-slot:activator="{ on, attrs }">
                  <v-btn v-bind="attrs" v-on="on" icon>
                    <v-icon>mdi-minus</v-icon>
                  </v-btn>
                </template>

                <v-card>
                  <v-card-title> Warning </v-card-title>

                  <v-card-text> Do you want to delete it? </v-card-text>

                  <v-divider></v-divider>

                  <v-card-actions>
                    <v-btn
                      color="green darken-1"
                      text
                      @click="dialog_delete = false"
                    >
                      No
                    </v-btn>
                    <v-btn
                      color="green darken-1"
                      text
                      @click="deleteproject(item)"
                    >
                      Yes
                    </v-btn>
                  </v-card-actions>
                </v-card>
              </v-dialog>
              <v-btn
                icon
                v-if="item.state == 'audit'"
                @click="approveproject(item)"
              >
                <v-icon>mdi-check</v-icon>
              </v-btn>
            </template>
            <template slot="footer" v-if="role != 'student'">
              <Projectadd v-on:update="update" v-on="$listeners"></Projectadd>
            </template>
          </v-data-table>
        </v-card>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
import axios from "axios";
import Projectadd from "./Project_add";
import Projectmodify from "./Project_modify";
import Members from "./Members_project";

export default {
  components: {
    Projectadd,
    Projectmodify,
    Members
  },

  data() {
    return {
      dialog_delete: false,
      search: "",
      role: "",
      loading: true,
      headers: [
        {
          text: "Id",
          align: "start",
          sortable: false,
          value: "id",
        },
        { text: "Name", value: "name" },
        { text: "Skills", value: "skillRequire" },
        { text: "Special Requirements", value: "isNeedAnnex" },
        { text: "Space", value: "space" },
        { text: "", value: "action", align: "end", sortable: false },
      ],
      Projects: [
        {
          id: 1,
          name: "The test project1",
          description: "Come on",
          skill_required: "Java",
          client: "Dr.Max",
        },
        {
          id: 2,
          name: "The test project2",
          description: "Come on",
          skill_required: "Python",
          client: "Dr.David",
        },
        {
          id: 3,
          name: "The test project3",
          description: "Come on",
          skill_required: "ML",
          client: "Dr.Marin",
        },
        {
          id: 4,
          name: "The test project4",
          description: "Come on",
          skill_required: "NLP",
          client: "Dr.Max",
        },
        {
          id: 5,
          name: "The test project5",
          description: "Come on",
          skill_required: "Java",
          client: "Dr.Max",
        },
      ],
    };
  },
  created() {
    this.role = JSON.parse(localStorage.getItem("role"));
  },

  mounted() {
    this.getproject();
  },
  methods: {
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
            this.Projects = response.data.data.projectList;
            this.Projects = this.Projects.map((s) => ({
              id: s.id,
              name: s.name,
              client: s.client,
              clientId: s.clientId,
              Preference: s.describe,
              auditCount: s.auditCount,
              groupNumber: s.groupNumber,
              isNeedAnnex: s.isNeedAnnex,
              skillRequire: s.skillRequire,
              state: s.state,
              email: s.email,
              space: s.groupNumber - s.permitCount,
              company: s.company,
              describe: s.describe,
              member:s.groupEntities
            }));
            console.log(this.Projects.length);
            this.$emit("numbers", this.Projects);
          }
        })
        .catch((error) => {
          console.log(error);
          this.$emit("alert", "error");
        })
        .finally(() => (this.loading = false));
    },

    update() {
      this.getproject();
    },

    deleteproject(item) {
      this.dialog_delete = false;
      console.log(item);
      axios
        .delete("http://localhost:4399/project/delete/" + item.id, {
          headers: {
            token: JSON.parse(localStorage.getItem("token")),
          },
        })
        .then((response) => {
          console.log(response);
          if (response.data.msg == "successs") {
            this.$emit("alert", "success");
            this.getproject();
          } else {
            this.$emit("alert", "error");
          }
        })
        .catch((error) => {
          console.log(error);
          this.$emit("alert", "error");
        })
        .finally(() => (this.loading = false));
    },

    approveproject(item) {
      console.log(item);
      axios
        .post(
          `http://localhost:4399/project/modify`,
          {
            id: item.id,
            email: item.email,
            name: item.name,
            company: item.company,
            skillRequire: item.skillRequire,
            describe: item.describe,
            state: "publish",
            isNeedAnnex: item.isNeedAnnex,
            groupNumber: item.groupNumber,
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
            this.$emit("alert", "success");
            this.getproject();
          } else {
            this.$emit("alert", "error");
          }
        })
        .catch((e) => {
          this.$emit("alert", "error");
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
  },
};
</script>
<template>
  <div>
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
              show-expand
            >
              <template v-slot:expanded-item="{ headers, item }">
                <td :colspan="headers.length">
                  {{ item.describe }}
                  <br />
                  By {{ item.client }}
                </td>
              </template>
            </v-data-table>
          </v-card>
        </v-flex>
      </v-layout>
    </v-container>
  </div>
</template>

<script>
import axios from "axios";
export default {
  data() {
    return {
      search: "",
      headers: [
        {
          text: "Id",
          align: "start",
          sortable: false,
          value: "id",
        },
        { text: "Name", value: "name" },
        { text: "Skills", value: "skillRequire" },
      ],
      Projects: [
        {
          id: 1,
          name: "The best project1",
          description: "Come on",
          skill_required: "Java",
          client: "Dr.Max",
        },
        {
          id: 2,
          name: "The best project2",
          description: "Come on",
          skill_required: "Python",
          client: "Dr.David",
        },
        {
          id: 3,
          name: "The best project3",
          description: "Come on",
          skill_required: "ML",
          client: "Dr.Marin",
        },
        {
          id: 4,
          name: "The best project4",
          description: "Come on",
          skill_required: "NLP",
          client: "Dr.Max",
        },
        {
          id: 5,
          name: "The best project5",
          description: "Come on",
          skill_required: "Java",
          client: "Dr.Max",
        },
        {
          id: 6,
          name: "The best project6",
          description: "Come on",
          skill_required: "Python",
          client: "Dr.David",
        },
        {
          id: 7,
          name: "The best project7",
          description: "Come on",
          skill_required: "ML",
          client: "Dr.Marin",
        },
        {
          id: 8,
          name: "The best project8",
          description: "Come on",
          skill_required: "NLP",
          client: "Dr.Max",
        },
        {
          id: 9,
          name: "The best project9",
          description: "Come on",
          skill_required: "Java",
          client: "Dr.Max",
        },
      ],
    };
  },
  created() {
    console.log({ token: JSON.parse(localStorage.getItem("token")) });
    const currentpage = 1;
    const pagesize = 10;
    const url =
      "http://localhost:8080/project/page?currentPage=" +
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
          this.Projects = response.data.data.projectList;
          console.log(this.Projects.length);
          this.$emit("numbers", this.Projects.length);
        }
      })
      .catch((error) => {
        console.log(error);
        this.errored = true;
      })
      .finally(() => (this.loading = false));
  },
  methods: {
    customFilter(items, search) {
      return items.filter(
        (Projects) =>
          JSON.stringify(Projects)
            .toLowerCase()
            .indexOf(search.toLowerCase()) !== -1
      );
    },
  },
};
</script>
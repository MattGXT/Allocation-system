<template>
  <v-container fluid>
    <v-layout align-center justify-center>
      <v-flex xs12 sm12 md8>
        <v-data-iterator
          :items="items"
          :items-per-page.sync="itemsPerPage"
          :page.sync="page"
          :search="search"
          :sort-by="sortBy.toLowerCase()"
          :sort-desc="sortDesc"
          hide-default-footer
        >
          <template v-slot:header>
            <v-toolbar flat class="mb-1">
              <v-text-field
                v-model="search"
                clearable
                flat
                solo-inverted
                hide-details
                prepend-inner-icon="mdi-magnify"
                label="Search"
              ></v-text-field>
              <template v-if="$vuetify.breakpoint.mdAndUp">
                <v-spacer></v-spacer>

                <v-btn-toggle v-model="sortDesc" mandatory>
                  <v-btn large depressed :value="false">
                    <v-icon>mdi-arrow-up</v-icon>
                  </v-btn>
                  <v-btn large depressed :value="true">
                    <v-icon>mdi-arrow-down</v-icon>
                  </v-btn>
                </v-btn-toggle>
              </template>
            </v-toolbar>
          </template>

          <template v-slot:default="props">
            <v-row>
              <v-col
                v-for="item in props.items"
                :key="item.name"
                cols="12"
                sm="6"
                md="4"
                lg="3"
              >
                <v-card v-if="item.state == 'prepare'">
                  <v-card-title class="subheading font-weight-bold">
                    {{ item.name }}
                    <v-spacer></v-spacer>
                    <Groupmodify
                      v-on:update="update"
                      v-if="
                        role == 'admin' ||
                        role == 'superAdmin' ||
                        item.leaderId == userid
                      "
                      :input="item"
                    ></Groupmodify>
                    <Deletefunc
                      v-on:delete="[deletegroup(item)]"
                      v-if="
                        role == 'admin' ||
                        role == 'superAdmin' ||
                        item.leaderId == userid
                      "
                    ></Deletefunc>
                  </v-card-title>

                  <v-divider></v-divider>

                  <v-list dense>
                    <v-list-item
                      v-for="(key, index) in filteredKeys"
                      :key="index"
                    >
                      <v-list-item-content
                        :class="{ 'blue--text': sortBy === key }"
                      >
                        {{ key }}:
                      </v-list-item-content>
                      <v-list-item-content
                        class="align-end"
                        :class="{ 'blue--text': sortBy === key }"
                      >
                        <span class="text-end">{{ item[key] }}</span>
                      </v-list-item-content>
                    </v-list-item>
                    <v-list-item>
                      <v-list-item-content>Members:</v-list-item-content>
                    </v-list-item>
                    <v-list-item v-for="(aa,i) in item.member" :key="'A'+i">                      
                      <v-list-item-content >{{aa.studentName}}<br></v-list-item-content>
                    </v-list-item>
                    <div class="text-center">
                      <v-btn
                        color="primary"
                        v-if=" check(item.member) && role !='superAdmin'&& role !='admin'&& role !='client' && ismember"
                        @click="joingroup(item.id)"
                        >Join</v-btn
                      >
                    </div>
                  </v-list>
                </v-card>
              </v-col>
            </v-row>
          </template>

          <template v-slot:footer>
            <v-row>
              <Groupadd v-on="$listeners" v-on:update="update" v-if='ismember'></Groupadd>
            </v-row>
            <v-row class="mt-2" align="center" justify="center">
              <span class="grey--text">Items per page</span>
              <v-menu offset-y>
                <template v-slot:activator="{ on, attrs }">
                  <v-btn
                    dark
                    text
                    color="primary"
                    class="ml-2"
                    v-bind="attrs"
                    v-on="on"
                  >
                    {{ itemsPerPage }}
                    <v-icon>mdi-chevron-down</v-icon>
                  </v-btn>
                </template>
                <v-list>
                  <v-list-item
                    v-for="(number, index) in itemsPerPageArray"
                    :key="index"
                    @click="updateItemsPerPage(number)"
                  >
                    <v-list-item-title>{{ number }}</v-list-item-title>
                  </v-list-item>
                </v-list>
              </v-menu>

              <v-spacer></v-spacer>

              <span class="mr-4 grey--text">
                Page {{ page }} of {{ numberOfPages }}
              </span>
              <v-btn
                fab
                dark
                color="blue darken-3"
                class="mr-1"
                @click="formerPage"
              >
                <v-icon>mdi-chevron-left</v-icon>
              </v-btn>
              <v-btn
                fab
                dark
                color="blue darken-3"
                class="ml-1"
                @click="nextPage"
              >
                <v-icon>mdi-chevron-right</v-icon>
              </v-btn>
            </v-row>
          </template>
        </v-data-iterator>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
import axios from "axios";
import Deletefunc from "./Delete";
import Groupadd from "./Group_add";
import Groupmodify from "./Group_modify";
export default {
  name: "GroupList",
  components: {
    Deletefunc,
    Groupadd,
    Groupmodify,
  },
  data() {
    return {
      ismember:true,
      userid: "",
      isleader: false,
      itemsPerPageArray: [4, 8, 12],
      search: "",
      role: "",
      filter: {},
      sortDesc: false,
      page: 1,
      itemsPerPage: 4,
      sortBy: "name",
      keys: ["Name", "Current Person", "Preference"],
      items: [{}],
    };
  },
  computed: {
    numberOfPages() {
      return Math.ceil(this.items.length / this.itemsPerPage);
    },
    filteredKeys() {
      return this.keys.filter((key) => key !== "Name");
    },
  },
  methods: {
    nextPage() {
      if (this.page + 1 <= this.numberOfPages) this.page += 1;
    },
    formerPage() {
      if (this.page - 1 >= 1) this.page -= 1;
    },
    updateItemsPerPage(number) {
      this.itemsPerPage = number;
    },
    getgroup() {
      const currentpage = 1;
      const pagesize = 10;
      const url =
        "http://localhost:4399/group/page?currentPage=" +
        currentpage +
        "&pageSize=" +
        pagesize + "&state=prepare";
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
            this.items = response.data.data.groupList;
            this.items = this.items.map((s) => ({
              id: s.id,
              name: s.name,
              "Current Person": s.applicationEntities.length,
              "Max Person": s.maxPerson,
              Preference: s.describe,
              leaderId: s.leaderId,
              state: s.state,
              member: s.applicationEntities,
            }));
            console.log(this.items)
            this.items.forEach((element) => {
              if (element.leaderId == this.userid) {
                this.isleader = true;
              }
            });
            this.getmygroup();
          }
        })
        .catch((error) => {
          console.log(error);
          this.$emit("alert", "error","Network error");
        })
        .finally(() => (this.loading = false));
    },
    deletegroup(item) {
      axios
        .delete("http://localhost:4399/group/delete/" + item.id, {
          headers: {
            token: JSON.parse(localStorage.getItem("token")),
          },
        })
        .then((response) => {
          console.log(response);
          if (response.data.msg == "successs") {
            this.$emit("alert", "success","Success!");
            this.getgroup();
          } else {
            this.$emit("alert", "error",response.data.msg);
          }
        })
        .catch((error) => {
          console.log(error);
          this.$emit("alert", "error","Network error");
        })
        .finally(() => (this.loading = false));
    },
    update() {
      this.getgroup();
    },

    joingroup(id) {
      axios
        .post(
          "http://localhost:4399/application/apply",
          {
            groupId: id.toString(),
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
            this.getgroup();
          }
        })
        .catch((error) => {
          console.log(error);
          this.$emit("alert", "error","Network error");
        })
        .finally(() => (this.loading = false));
    },

    check(item) {
      var result = true;
      for (let i of item) {
        if (i.studentId == this.userid) {
          result = false;
        }
      }
      return result;
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
              if(response.data.data.groupList[0].state != 'prepare'){
                this.ismember = false
              }
              console.log(response.data.data.groupList[0])
            } else {
              this.ismember = true
            }
          }
        })
        .catch((error) => {
          console.log(error);
          this.$emit("alert", "error","Network error");
        })
        .finally(() => (this.loading = false));
    },
  },
  mounted() {
    this.getgroup();
  },

  created() {
    this.role = JSON.parse(localStorage.getItem("role"));
    this.userid = JSON.parse(localStorage.getItem("id"));
  },

  
};
</script>

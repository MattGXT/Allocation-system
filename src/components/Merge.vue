<template>
  <v-dialog v-model="dialog" persistent max-width="1200px">
    <template v-slot:activator="{ on, attrs }">
      <v-btn icon v-bind="attrs" v-on="on">
        <v-icon>mdi-merge</v-icon>
      </v-btn>
    </template>
    <v-card>
      <v-toolbar color="secondary" dark class="px-4">Merge Group</v-toolbar>
      <v-row no-gutters>
        <v-col cols="6" class="pa-4">
          <v-card>
            <v-card-title>Group {{ input.id }}</v-card-title>
            <v-card-subtitle class="px-4 py-0"
              >Prefered Project: {{ input.prefer }}</v-card-subtitle
            >
            <v-card-subtitle class="px-4 pt-0"
              >Members: {{ input.members }}</v-card-subtitle
            >
          </v-card>
        </v-col>
        <v-col cols="6" class="pa-4">
          <v-card>
            <v-card-title
              >Group {{ this.group.id
              }}<v-chip
                small
                class="mx-2 text-right"
                color="green"
                text-color="white"
              >
                {{ this.group.same ? this.group.same : 0 }}
              </v-chip></v-card-title
            >
            <v-card-subtitle class="px-4 py-0"
              >Prefered Project: {{ this.group.prefer }}</v-card-subtitle
            >
            <v-card-subtitle class="px-4 pt-0"
              >Members: {{ this.group.members }}</v-card-subtitle
            >
          </v-card>
        </v-col>
      </v-row>
      <v-select
        label="Merged Group"
        solo
        :items="selectlist"
        :value="1"
        hide-details=""
        class="px-4"
        v-model="selectgroup"
        @change="select"
      ></v-select>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="blue darken-1" text @click="dialog = false">
          Close
        </v-btn>
        <v-btn color="blue darken-1" text @click="merge"> Merge </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import axios from "axios";
export default {
  name: "Groupadd",
  props: {
    input: Object,
    list: Array,
  },
  data: () => ({
    dialog: false,
    group: {
      id: "",
      prefer: "",
      members: "",
      same: "",
    },
    selectlist: [],
    grouplist: [],
    maplist: new Map(),
    preferlist: [],
    selectgroup: -1,
  }),
  methods: {
    select(id) {
      this.group.id = id;
      this.group.prefer = this.maplist[id].prefer;
      this.group.members = this.maplist[id].members;
      this.group.same = this.maplist[id].same;
    },

    merge() {
      if (this.selectgroup == -1 || !this.selectgroup) {
        this.$emit("alert", "warning", "Please select another group");
        return;
      }
      axios
        .post(
          `http://18.116.164.154:4399/group/mergeAllocation`,
          {
            mainGroupId: this.input.id,
            subGroupId: this.selectgroup,
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
            this.dialog = false;
            this.$emit("update");
          } else {
            this.$emit("alert", "warning", response.data.msg);
          }
        })
        .catch((e) => {
          this.$emit("alert", "error", "Network error");
          console.log(e);
        });
    },

    compare(a, b) {
      if (a.same < b.same) {
        return -1;
      }
      if (a.same > b.same) {
        return 1;
      }
      return 0;
    },
  },
  mounted() {
    this.grouplist = this.list.filter((element) => element.id != this.input.id);
    if (this.input.describe) {
      this.preferlist = this.input.describe.split(",");
    }
    this.grouplist.forEach((element) => {
      var elist = element.describe.split(",");
      element.same = elist.filter((element) =>
        this.preferlist.includes(element)
      ).length;
      this.maplist[element.id] = {
        describe: element.describe,
        members: element.members,
        same: element.same,
        prefer: element.prefer
      };
    });
    this.grouplist.sort(this.compare);
    this.grouplist.forEach((element)=>{
      this.selectlist.push(element.id);
    })
    console.log(this.maplist);
  },
};
</script>

<style>
</style>

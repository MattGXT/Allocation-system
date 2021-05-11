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
          <v-data-table :headers="headers" :items="Applications" :search="search" :loading="loading">
            <template v-slot:[`item.action`]="{  }">
              
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
//import axios from "axios";
export default {
    components: {


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
        { text: "Group Id", value: "accountEmail" },
        { text: "Members", value: "current_person" },
        { text: "Perferences", value: "describe" },
        { text: "Project Id", value: "project_id" },
        { text: "", value: "action", align: "end", sortable: false },
      ],
      Users: [{}],
    };
  },
  created() {
    this.getuser();
  },
  methods: {
    update(){
        this.getuser();
    }
  },
};
</script>
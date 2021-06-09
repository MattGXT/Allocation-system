<template>
  <v-dialog v-model="dialog" persistent max-width="600px">
    <template v-slot:activator="{ on, attrs }">
      <v-btn icon v-bind="attrs" v-on="on">
        <v-icon>mdi-account-group</v-icon>
      </v-btn>
    </template>
    <v-card>
      <v-card-title>
        <span class="headline">Group {{groupName}}</span>
      </v-card-title>
      <v-divider></v-divider>
      <v-card-text>
        <v-container>
          <v-list subheader>
            <v-subheader>Members</v-subheader>

            <v-list-item v-for="student in this.input" :key="student.studentName">
              <v-list-item-content>
                {{student.studentName}}
              </v-list-item-content>
                
            </v-list-item>
          </v-list>
        </v-container>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="blue darken-1" text @click="dialog = false">
          Close
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
export default {
  name: "Members",
  props: {
    input: Array,
    groupName:Number
  },
  data: () => ({
    dialog: false,
    group: {
      name: "",
    },
  }),
  methods: {},

  created() {
  },
};
</script>

<style>
</style>

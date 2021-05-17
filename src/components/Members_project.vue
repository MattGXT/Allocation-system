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
      <v-card-text>
        <v-container>
          <v-expansion-panels>
    <v-expansion-panel
      v-for="(item,i) in group"
      :key="i"
    >
      <v-expansion-panel-header>
        Group {{item.id}}
      </v-expansion-panel-header>
      <v-expansion-panel-content v-for="(member,i) in item.applicationEntities" :key="'A'+ i">
        {{member.studentName}}
      </v-expansion-panel-content>
    </v-expansion-panel>
  </v-expansion-panels>
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
    group: Array
  }),
  methods: {},

  created() {
    this.group = this.input.filter(function(value){ 
        return value.state == 'permit';
    });
  },
};
</script>

<style>
</style>

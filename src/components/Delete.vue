<template>
  <v-dialog
    v-model="dialog_delete"
    width="500"
    :retain-focus="false"
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
        <v-btn color="green darken-1" text @click="dialog_delete = false">
          No
        </v-btn>
        <v-btn color="green darken-1" text @click="deleteit">
          Yes
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
export default {
    data() {
        return {
        dialog_delete:false,
        role:''
        }
    },

    methods:{
        deleteit(){
            this.dialog_delete = false
            this.$emit('delete')
        }
    },

    
    
}
</script>